package com.github.akovac35;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.io.FileUtils;

public class TestFileHelper {
    private static final Logger logger = LoggerFactory.getLogger(TestFileHelper.class);

    public static String readFile(String fileName) throws IOException
    {
        ClassLoader classLoader = TestFileHelper.class.getClassLoader();
        File file = new File(classLoader.getResource(fileName).getFile());

        logger.info("Reading test file: {}", file.getAbsolutePath());

        String data = FileUtils.readFileToString(file, "UTF-8");
        return data;
    }
    
}