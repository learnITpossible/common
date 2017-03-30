package com.domain.common.utils;

import java.io.File;

public class FileUtil {

    public static File createTempFile(String fileName) {

        File file = new File(getTmpDir(), fileName);
        return file;
    }

    public static File getTmpDir() {

        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        return tmpDir;
    }

}
