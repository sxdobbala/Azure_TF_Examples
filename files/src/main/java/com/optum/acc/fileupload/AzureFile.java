package com.optum.acc.fileupload;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import me.tongfei.progressbar.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;

public class AzureFile {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureFile.class);
    private final ContainerSettings settings;
    private final CloudFileShare share;
    private final CloudFileDirectory rootDir;

    public AzureFile(ContainerSettings settings) {
        this.settings = settings;

        CloudStorageAccount storageAccount = settings.getCloudStorageAccount();
        CloudFileClient fileClient = storageAccount.createCloudFileClient();

        try {
            share = fileClient.getShareReference(settings.getFileContainer());
            if (!share.exists()) {
                throw new AzureContainerException("File Container NOT found");
            }

            rootDir = share.getRootDirectoryReference();
        } catch (Exception e) {
            throw new AzureContainerException(e);
        }
    }

    public void snapshot() {
        try {
            share.createSnapshot();
        } catch (StorageException e) {
            throw new AzureContainerException(e);
        }
    }

    public void upload(File file) {
        try {
            if (file.isDirectory()) {
                uploadDirectory(file, rootDir.getDirectoryReference(file.getName()));
            } else if (file.isFile()) {
                uploadFile(file, rootDir.getFileReference(file.getName()));
            } else {
                LOGGER.warn("Invalid file {}", file.getAbsolutePath());
            }
        } catch (URISyntaxException | StorageException e) {
            throw new AzureContainerException(e);
        }
    }

    private void uploadDirectory(File directory, CloudFileDirectory cloudFileDirectory) {
        try {
            cloudFileDirectory.createIfNotExists();

            File[] files = directory.listFiles(File::isFile);
            if (files != null) {
                for (File file : files) {
                    uploadFile(file, cloudFileDirectory.getFileReference(file.getName()));
                }
            }

            files = directory.listFiles(File::isDirectory);
            if (files != null) {
                for (File subDir : files) {
                    uploadDirectory(subDir, cloudFileDirectory.getDirectoryReference(subDir.getName()));
                }
            }
        } catch (URISyntaxException | StorageException e) {
            throw new AzureContainerException(e);
        }
    }

    private void uploadFile(File file, CloudFile cloudFile) {
        try {
            if (cloudFile.exists() && !settings.isOverwrite()) {
                LOGGER.info("Skipping {}: already present", file.getName());
            } else {
                LOGGER.info("Uploading {}", file.getName());
                try (InputStream stream = ProgressBar.wrap(new FileInputStream(file), settings.getProgressBarBuilder())) {
                    cloudFile.upload(stream, file.length());
                }
            }
        } catch (Exception e) {
            throw new AzureContainerException(e);
        }
    }
}
