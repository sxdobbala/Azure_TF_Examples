package com.optum.cloud.scaffolding.utilities;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

public class ResourceFileUtilities {

    public static ArrayList<String> listResourceFilesIn(String directory) throws IOException {
        ArrayList<String> files = new ArrayList<>();
        InputStream in = ResourceFileUtilities.class.getResourceAsStream(directory);
        BufferedReader filePathReader = new BufferedReader(new InputStreamReader(in));

        String line;
        while ((line = filePathReader.readLine()) != null) {
            files.add(line);
        }
        filePathReader.close();
        in.close();
        return files;
    }

    public static List<String> listResourceFilesInAndUnder(String directory) throws IOException, URISyntaxException {
        ArrayList<String> files = new ArrayList<>();
        URL dirURL = ResourceFileUtilities.class.getResource(directory);
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            Stream<Path> fileListing = Files.walk(Paths.get(dirURL.toURI()));
            fileListing.filter(Files::isRegularFile).forEach(i -> files.add(i.toString()));
            return files;
        }
        if (dirURL == null) {
            String classLocation = ResourceFileUtilities.class.getName().replace(".", "/") + ".class";
            dirURL = ResourceFileUtilities.class.getResource(classLocation);
        }
        if (dirURL.getProtocol().equals("jar")) {
            String jarPath = dirURL.getPath().substring(5, dirURL.getPath().indexOf("!"));
            JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"));
            Enumeration<JarEntry> folder = jar.entries();
            while (folder.hasMoreElements()) {
                String item = folder.nextElement().getName();
                if (item.startsWith(directory) && !(item.endsWith("/") || item.endsWith("\\"))) {
                    files.add(item);
                }
            }
            return files;
        }

        throw new UnsupportedOperationException("Unable to get files for path: " + dirURL);
    }

    public static boolean resourceFileExists(String path) throws IOException {
        InputStream in = ResourceFileUtilities.class.getResourceAsStream(path);
        if (in != null) {
            in.close();
            return true;
        } else {
            return false;
        }
    }

    public static String getResourceFileAsText(String path) throws IOException {
        String fullFile = "";
        InputStream in = ResourceFileUtilities.class.getResourceAsStream(path);
        if (in != null) {
            BufferedReader filePathReader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = filePathReader.readLine()) != null) {
                fullFile += line;
            }

            filePathReader.close();
            in.close();
            return fullFile;
        } else {
            throw new FileNotFoundException();
        }
    }

    public static ArrayList<String> getResourceFileAsList(String path) throws IOException {
        ArrayList<String> fullFile = new ArrayList<>();
        InputStream in = ResourceFileUtilities.class.getResourceAsStream(path);
        if (in != null) {
            BufferedReader filePathReader = new BufferedReader(new InputStreamReader(in));

            String line;
            while ((line = filePathReader.readLine()) != null) {
                fullFile.add(line);
            }

            filePathReader.close();
            in.close();
            return fullFile;
        } else {
            throw new FileNotFoundException();
        }
    }

    public static void doRecursiveCopyOfResourceFolder(String path, Path destination) throws URISyntaxException, IOException {
        List<String> filesToCopy = listResourceFilesInAndUnder(path);
        for (String file : filesToCopy) {
            ArrayList<String> fullFile = new ArrayList<>();
            if (!file.endsWith(path) && !file.endsWith("/")) {
                if (!file.startsWith(path)) {
                    file = file.substring(file.indexOf(path));
                }
                new BufferedReader(new InputStreamReader(ResourceFileUtilities.class.getResourceAsStream(file))).lines().forEach(fullFile::add);
                String fileWithoutPath = file.replace(path, "");
                Path writeFilePath = Paths.get(destination.toString(), fileWithoutPath);
                Files.createDirectories(writeFilePath.getParent());
                Files.write(writeFilePath, fullFile, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        }
    }
}
