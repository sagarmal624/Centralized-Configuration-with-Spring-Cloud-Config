package com.sagarandcompany.cloudconfigserver;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.environment.PropertySource;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.config.server.environment.EnvironmentRepository;
import org.springframework.core.Ordered;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.beans.factory.config.YamlProcessor.MatchStatus.*;

@Component
public class AzureBlobEnvironmentRepository implements EnvironmentRepository, Ordered {

    private AzureFileShareAdapter azureFileShareAdapter;

    private final ConfigServerProperties serverProperties;
    protected int order = Ordered.LOWEST_PRECEDENCE;

    public AzureBlobEnvironmentRepository(AzureFileShareAdapter azureFileShareAdapter, ConfigServerProperties server) {
        this.azureFileShareAdapter = azureFileShareAdapter;
        this.serverProperties = server;
    }

    public Environment findOne(String specifiedApplication, String specifiedProfile, String specifiedLabel) {
        String application = StringUtils.isEmpty(specifiedApplication) ? this.serverProperties.getDefaultApplicationName() : specifiedApplication;
        String profile = StringUtils.isEmpty(specifiedProfile) ? this.serverProperties.getDefaultProfile() : specifiedProfile;
        String label = StringUtils.isEmpty(specifiedLabel) ? this.serverProperties.getDefaultLabel() : specifiedLabel;
        profile = profile + ",default";
        Environment environment = new Environment(application, new String[]{profile});
        environment.setLabel(label);
        String[] profiles = StringUtils.commaDelimitedListToStringArray(profile);

        String[] specifiedApplications = StringUtils.commaDelimitedListToStringArray(specifiedApplication);
        for (String app : specifiedApplications) {
            for (String profileName : profiles) {
                StringBuilder objectKeyPrefix = new StringBuilder();
                if (!StringUtils.isEmpty(label)) {
                    objectKeyPrefix.append(label).append("/");
                }
                if (profileName.contains("default")) {
                    objectKeyPrefix.append("common").append("/").append(app);
                } else {
                    objectKeyPrefix.append(profileName).append("/").append(app).append("-").append(profileName);
                }
                List<PropertySource> properties = this.getAzureBlobConfigFile(objectKeyPrefix.toString(), profiles);
                if (properties == null) {
                    System.out.println("No such File: " + objectKeyPrefix.toString() + "(.properties | .yml | .json)");
                } else {
                    properties.forEach(environment::add);
                }
            }
        }
        return environment;
    }

    private List<PropertySource> getAzureBlobConfigFile(String keyPrefix,String[] profiles) {

        return Arrays.asList(keyPrefix + ".properties", keyPrefix + ".yml").stream().map(key -> {
            File file = null;
            try {
                if (FilenameUtils.getExtension(key).equals("properties")) {
                    file = this.azureFileShareAdapter.getFile(key);
                    if (Objects.nonNull(file)) {
                        Map config = read(new FileInputStream(file));
                        config.putAll(this.serverProperties.getOverrides());
                        return new PropertySource(key, config);
                    }
                } else if (FilenameUtils.getExtension(key).equals("yml") || FilenameUtils.getExtension(key).equals("yaml")) {

                    file = this.azureFileShareAdapter.getFile(key);
                    if (Objects.nonNull(file)) {
                        final YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
                        yamlFactory.setDocumentMatchers(properties -> {
                            String profileProperty = properties.getProperty("spring.profiles");
                            if (profileProperty != null && profileProperty.length() > 0) {
                                return Arrays.asList(profiles).contains(profileProperty) ? FOUND : NOT_FOUND;
                            }
                            else {
                                return ABSTAIN;
                            }
                        });
                        yamlFactory.setResources(new ByteArrayResource(Files.readAllBytes(Paths.get(file.getPath()))));
                        Map config = new HashMap(yamlFactory.getObject());
                        config.putAll(this.serverProperties.getOverrides());
                        return new PropertySource(key, config);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (Objects.nonNull(file)) {
                    file.delete();
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());

    }

    private Map<?, ?> read(InputStream in) {
        Properties props = new Properties();

        try {

            Throwable var3 = null;

            try {
                props.load(in);
            } catch (Throwable var13) {
                var3 = var13;
                throw var13;
            } finally {
                if (in != null) {
                    if (var3 != null) {
                        try {
                            in.close();
                        } catch (Throwable var12) {
                            var3.addSuppressed(var12);
                        }
                    } else {
                        in.close();
                    }
                }

            }

            return props;
        } catch (IOException var15) {
            throw new IllegalStateException("Cannot load environment", var15);
        }
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

}
