package com.optum.acc.fileupload;

import com.microsoft.azure.storage.CloudStorageAccount;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Properties;

public class ContainerSettings {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContainerSettings.class);
    private final String accountName;
    private final String accountKey;
    private final String blobContainer;
    private final String fileContainer;
    private final boolean overwrite;
    private final boolean useBlob;
    private final boolean debug;

    public ContainerSettings(String propertiesFilePath) {
        Properties properties = new Properties();

        try (InputStream inputStream = new FileInputStream(propertiesFilePath)) {
            properties.load(inputStream);
        } catch (IOException e) {
            throw new AzureContainerException("Unable to read settings from " + propertiesFilePath);
        }

        accountName = getProperty(properties, "accountName");
        accountKey = getProperty(properties, "accountKey");
        blobContainer = getProperty(properties, "blobContainer");
        fileContainer = getProperty(properties, "fileContainer");
        overwrite = getProperty(properties, "overwrite").equals("true");
        useBlob = getProperty(properties, "useBlob").equals("true");
        debug = getProperty(properties, "debug").equals("true");
    }

    private String getProperty(Properties properties, String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isEmpty()) {
            throw new AzureContainerException("Settings NOT found: " + key);
        }
        return value;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public String getBlobContainer() {
        return blobContainer;
    }

    public String getFileContainer() {
        return fileContainer;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public boolean useBlob() {
        return useBlob;
    }

    public boolean debug() {
        return debug;
    }

    public CloudStorageAccount getCloudStorageAccount() {
        String storageConnectionString = "DefaultEndpointsProtocol=https;" +
                "AccountName=" + getAccountName() + ";" +
                "AccountKey=" + getAccountKey();
        try {
            return CloudStorageAccount.parse(storageConnectionString);
        } catch (URISyntaxException | InvalidKeyException e) {
            LOGGER.debug(e.getMessage(), e);
            throw new AzureContainerException("Invalid Endpoint: " + storageConnectionString);
        }
    }

    public ProgressBarBuilder getProgressBarBuilder() {
        return new ProgressBarBuilder()
                .setTaskName("Upload")
                .setUnit("MB", 1048576)
                .setStyle(ProgressBarStyle.ASCII);
    }
}
