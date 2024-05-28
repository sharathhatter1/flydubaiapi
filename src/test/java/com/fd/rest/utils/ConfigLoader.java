package com.fd.rest.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigLoader {
    private static Properties properties;

    static {
        properties = new Properties();
        Path currentRelativePath = Paths.get("");
        String strPathToRoot = currentRelativePath.toAbsolutePath().toString();
        String strDataFolderPath=strPathToRoot + "/src/test/resources/data/";
        //File fileTestDataFile = new File(strDataFolderPath + "StaticData.properties");
        try (InputStream inputStream = new FileInputStream(strDataFolderPath +"StaticData.properties")) {
            properties.load(inputStream);
            System.out.println("Data File Path:- " +inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}

