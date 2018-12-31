package com.optum.acc.fileupload;

import com.microsoft.azure.storage.OperationContext;

import java.io.File;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;


public class AzureFileUpload {

    public static void main(String[] args) throws Exception {
        Class clazz = AzureFileUpload.class;
        String className = clazz.getSimpleName() + ".class";
        String classPath = clazz.getResource(className).toString();
        if (!classPath.startsWith("jar")) {
            // Class not from JAR
            return;
        }
        String manifestPath = classPath.substring(0, classPath.lastIndexOf("!") + 1) +
                "/META-INF/MANIFEST.MF";
        Manifest manifest = new Manifest(new URL(manifestPath).openStream());
        Attributes attr = manifest.getMainAttributes();
        String version = attr.getValue("Implementation-Version");
        System.out.println("AzureFileUpload Version: " + version);
        if (args.length < 2) {
            System.out.println("Usage: [settings file] [file or dir to upload]");
            System.exit(1);
        }

        String propertiesFilePath = args[0];
        String filePath = args[1];

        ContainerSettings settings = new ContainerSettings(propertiesFilePath);

        File sourceFile = new File(filePath);
        if (!sourceFile.exists()) {
            throw new AzureContainerException("File NOT found: " + filePath);
        }

        SSLSetup.setup();

        OperationContext.setLoggingEnabledByDefault(settings.debug());
        OperationContext ctx = new OperationContext();
        ctx.setLoggingEnabled(settings.debug());

        if (settings.useBlob()) {
            AzureBlob azureBlob = new AzureBlob(settings);
            azureBlob.upload(sourceFile);
        } else {
            AzureFile azureFile = new AzureFile(settings);
            azureFile.upload(sourceFile);
            azureFile.snapshot();
        }
    }
}
