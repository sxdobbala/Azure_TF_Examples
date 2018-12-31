package com.optum.acc.fileupload;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.BlobRequestOptions;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import me.tongfei.progressbar.ProgressBar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;

public class AzureBlob {

    private static final Logger LOGGER = LoggerFactory.getLogger(AzureFile.class);
    private final ContainerSettings settings;
    private final CloudBlobContainer container;

    public AzureBlob(ContainerSettings settings) {
        this.settings = settings;

        CloudStorageAccount storageAccount = settings.getCloudStorageAccount();
        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();

        try {
            container = blobClient.getContainerReference(settings.getBlobContainer());
            if (!container.exists()) {
                throw new AzureContainerException("Blob Container NOT found");
            }
        } catch (Exception e) {
            throw new AzureContainerException(e);
        }
    }

    public void upload(File file) {
        try {
            if (file.isDirectory()) {
                uploadDirectory(file, container.getDirectoryReference(file.getName()));
            } else if (file.isFile()) {
                uploadFile(file, container.getBlockBlobReference(file.getName()));
            } else {
                LOGGER.warn("Invalid file {}", file.getAbsolutePath());
            }
        } catch (URISyntaxException | StorageException e) {
            throw new AzureContainerException(e);
        }
    }

    private void uploadDirectory(File directory, CloudBlobDirectory blobDirectory) {
        try {
            for (File file : directory.listFiles(File::isFile)) {
                uploadFile(file, blobDirectory.getBlockBlobReference(file.getName()));
            }

            for (File subDir : directory.listFiles(File::isDirectory)) {
                uploadDirectory(subDir, blobDirectory.getDirectoryReference(subDir.getName()));
            }
        } catch (Exception e) {
            throw new AzureContainerException(e);
        }
    }

    private void uploadFile(File file, CloudBlockBlob blob) {
        try {
            if (blob.exists() && !settings.isOverwrite()) {
                LOGGER.info("Skipping {}: already present", file.getName());
            } else {
                LOGGER.info("Uploading {}", file.getName());
                try (InputStream stream = ProgressBar.wrap(new FileInputStream(file), settings.getProgressBarBuilder())) {
                    BlobRequestOptions blobRequestOptions = new BlobRequestOptions();
                    blobRequestOptions.setConcurrentRequestCount(4);
                    blobRequestOptions.setSingleBlobPutThresholdInBytes(65000000);
                    blob.upload(stream, file.length(), null, blobRequestOptions, null);
                    blob.createSnapshot();
                }
            }
        } catch (Exception e) {
            throw new AzureContainerException(e);
        }
    }
}
