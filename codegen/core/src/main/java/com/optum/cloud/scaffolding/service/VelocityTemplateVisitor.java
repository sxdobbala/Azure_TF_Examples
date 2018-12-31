package com.optum.cloud.scaffolding.service;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

public class VelocityTemplateVisitor extends SimpleFileVisitor<Path> {
    private static final Logger LOGGER = LoggerFactory.getLogger(VelocityTemplateVisitor.class);

    private final Path targetPath;
    private final VelocityContext context;
    private Path sourcePath = null;

    public VelocityTemplateVisitor(final Path targetPath, final VelocityContext context) {
        this.targetPath = targetPath;
        this.context = context;
    }

    @Override
    public FileVisitResult preVisitDirectory(final Path dir, final BasicFileAttributes attrs) throws IOException {
        LOGGER.debug("preVisitDirectory - processing {}", dir);

        String newDir = dir.toString();
        if (newDir.contains("__")) {
            newDir = StringUtils.replace(newDir, "__artifactId__", context.get("artifactId").toString());
            newDir = StringUtils.replace(newDir, "__team__", context.get("team").toString());
            newDir = StringUtils.replace(newDir, "__appName__", context.get("appName").toString());
        }
        final Path newPath = Paths.get(newDir);
        if (sourcePath == null) {
            sourcePath = newPath;
        } else {
            Files.createDirectories(targetPath.resolve(sourcePath.relativize(newPath)));
        }

        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
        LOGGER.debug("visitFile - processing {}", file);

        String targetFile = file.toString();
        if (targetFile.indexOf("__") > -1) {
            targetFile = StringUtils.replace(targetFile, "__artifactId__", context.get("artifactId").toString());
            targetFile = StringUtils.replace(targetFile, "__team__", context.get("team").toString());
            targetFile = StringUtils.replace(targetFile, "__appName__", context.get("appName").toString());
        }
        final Path newTargetPath = Paths.get(targetFile);
        if (file.toString().endsWith(".vm")) {
            final VelocityEngine ve = new VelocityEngine();
            ve.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, file.getParent().toString());
            ve.setProperty(VelocityEngine.RESOURCE_LOADER, "file");
            ve.setProperty("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.FileResourceLoader");
            ve.init();

            //String templateName = file.getFileName().toString();
            String templateName = file.toFile().getName();
            if (templateName.indexOf("classes") > -1) {
                templateName = StringUtils.substringAfter(templateName, "classes");
            }
            //String outputFileName = templateName.substring(0, templateName.length() - 3);
            final String fileName = file.getFileName().toString();
            final String outputFileName = fileName.substring(0, fileName.length() - 3);

            final Path outputDir = targetPath.resolve(sourcePath.relativize(newTargetPath)).getParent();
            final Path dest = outputDir.resolve(sourcePath.getFileSystem().getPath(outputFileName));

            // Without OpenOption it allows the file to be "replaced" if existing
            try (Writer writer = Files.newBufferedWriter(dest, StandardCharsets.UTF_8)) {
                ve.mergeTemplate(templateName, StandardCharsets.UTF_8.name(), this.context, writer);
            }
        } else {
            Files.copy(file, targetPath.resolve(sourcePath.relativize(newTargetPath)), StandardCopyOption.REPLACE_EXISTING);
        }

        return FileVisitResult.CONTINUE;
    }
}
