package com.sagarandcompany.cloudconfigserver;

import com.azure.storage.file.share.ShareDirectoryClient;
import com.azure.storage.file.share.ShareFileClient;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service

public class AzureFileShareAdapter {

    @Autowired
    private ShareDirectoryClient shareDirectoryClient;

    public File getFile(String name) {
        try {
            ShareFileClient fileClient = shareDirectoryClient.getFileClient(name);
            String fileName = System.getProperty("java.io.tmpdir") +"/"+ FilenameUtils.getName(name);
            fileClient.downloadToFile(fileName);
            return new File(fileName);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return null;
    }

}