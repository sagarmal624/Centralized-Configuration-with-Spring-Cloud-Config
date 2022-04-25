package com.sagarandcompany.cloudconfigserver;


import com.azure.storage.file.share.ShareClient;
import com.azure.storage.file.share.ShareClientBuilder;
import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareFileClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureStorageClientConfig {

    @Value("${file.share.connection-string}")
    private String connectionString;

    @Value("${file.share.name}")
    private String shareName;

    @Bean
    public ShareDirectoryClient getFileShareClient() {
        return new ShareFileClientBuilder()
                .connectionString(connectionString).shareName(shareName)
                .resourcePath("")
                .buildDirectoryClient();
    }
}
