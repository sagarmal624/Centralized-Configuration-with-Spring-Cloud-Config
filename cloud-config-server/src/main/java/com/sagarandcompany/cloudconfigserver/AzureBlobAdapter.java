package com.sagarandcompany.cloudconfigserver;

import com.azure.storage.blob.BlobClientBuilder;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class AzureBlobAdapter {

    @Autowired
    private BlobClientBuilder client;

    public File getFile(String name) {
        try {
            File temp = new File("/tmp/" + FilenameUtils.getName(name));
            client.blobName(name).buildClient().downloadToFile(temp.getPath(), true);
            return temp;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}