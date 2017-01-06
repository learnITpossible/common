package com.domain.common;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

/**
 * com.weyao.calc.util
 * @author Mark Li
 * @version 1.0.0
 * @since 2017/1/5
 */
public class CSVUtil {

    private static final Logger logger = LoggerFactory.getLogger(CSVUtil.class);

    /**
     * 生成CSV文件的简单方法
     * @param dataList javaBean list
     * @param headers  文件头数组，英文，对应javaBean的属性名，例：["name","age"]
     * @param fileName 不带后缀的文件名
     * @param dir      文件目录，如为空，用java.io.tmpdir
     * @return CSV file
     * @throws Exception
     */
    public static File simpleWriteCSVFile(List<?> dataList, String[] headers, String fileName, String dir) throws Exception {

        if (StringUtils.isEmpty(dir)) {
            dir = System.getProperty("java.io.tmpdir");
        }
        if (StringUtils.isEmpty(fileName)) {
            fileName = String.valueOf(System.currentTimeMillis() + new Random().nextInt());
        }
        File file = new File(dir, fileName + ".csv");
        OutputStreamWriter osWriter = new OutputStreamWriter(new FileOutputStream(file), "GB2312");
        ICsvBeanWriter writer = new CsvBeanWriter(osWriter, CsvPreference.EXCEL_PREFERENCE);
        writer.writeHeader(headers);
        for (Object obj : dataList) {
            writer.write(obj, headers);
        }
        writer.close();
        return file;
    }

    /**
     * 生成新的CSV文件
     * @param dataList  数据列表，支持javaBean和map
     * @param rowMapper 属性名对应文件头名称的map，例：{"name":"姓名"}
     * @param fileName  不带后缀的文件名
     * @param dir       文件目录，如为空，用java.io.tmpdir
     * @return CSV file
     */
    public static File writeCSVFile(List dataList, LinkedHashMap rowMapper, String fileName, String dir) {

        if (StringUtils.isEmpty(fileName)) {
            fileName = String.valueOf(System.currentTimeMillis() + new Random().nextInt());
        }
        if (StringUtils.isEmpty(dir)) {
            dir = System.getProperty("java.io.tmpdir");
        }
        File csvFile = new File(dir, fileName + ".csv");
        BufferedWriter csvFileOutputStream = null;
        try {
            if (csvFile.exists()) {
                if (!csvFile.delete()) {
                    throw new Exception("CSV file " + csvFile.getAbsolutePath() + " exists and failed to delete, please try again...");
                }
            }
            if (!csvFile.createNewFile()) {
                throw new Exception("Failed to create new csv file");
            }

            // GB2312使正确读取分隔符","
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GB2312"), 1024);
            // 写入文件头部
            for (Iterator propertyIterator = rowMapper.entrySet().iterator(); propertyIterator.hasNext(); ) {
                Entry propertyEntry = (Entry) propertyIterator.next();
                csvFileOutputStream.write("\"" + propertyEntry.getValue().toString() + "\"");
                if (propertyIterator.hasNext()) {
                    csvFileOutputStream.write(",");
                }
            }
            csvFileOutputStream.newLine();
            writeCSVData(dataList, rowMapper, csvFileOutputStream);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                if (csvFileOutputStream != null) {
                    csvFileOutputStream.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
        return csvFile;
    }

    /**
     * 续写CSV文件
     * @param dataList  数据列表，支持javaBean和map
     * @param rowMapper 属性名对应文件头名称的map，例：{"name":"姓名"}
     * @param fileName  不带后缀的文件名
     * @param dir       文件目录，如为空，用java.io.tmpdir
     * @return CSV file
     * @throws Exception
     */
    public static File continueWriteCSVFile(List dataList, LinkedHashMap rowMapper, String fileName, String dir) throws Exception {

        if (StringUtils.isEmpty(fileName)) throw new Exception("Please fill out target csv's name!");

        if (StringUtils.isEmpty(dir)) {
            dir = System.getProperty("java.io.tmpdir");
        }
        File csvFile = new File(dir, fileName + ".csv");
        if (!csvFile.exists()) {
            writeCSVFile(dataList, rowMapper, fileName, dir);
        } else {
            BufferedWriter csvFileOutputStream = null;
            try {
                csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile, true), "GB2312"), 1024);
                // 写入文件内容
                writeCSVData(dataList, rowMapper, csvFileOutputStream);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            } finally {
                try {
                    if (csvFileOutputStream != null) {
                        csvFileOutputStream.close();
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
        return csvFile;
    }

    private static void writeCSVData(List dataList, LinkedHashMap rowMapper, BufferedWriter csvFileOutputStream) throws IOException {

        // 写入文件内容
        for (Object data : dataList) {
            for (Iterator keyIterator = rowMapper.keySet().iterator(); keyIterator.hasNext(); ) {
                String text = (String) keyIterator.next();
                try {
                    text = CommonUtil.getFieldValueByName(text, data);
                } catch (Exception e) {
                    text = "";
                    logger.error(e.getMessage(), e);
                }
                csvFileOutputStream.write("\"" + text + "\"");
                if (keyIterator.hasNext()) {
                    csvFileOutputStream.write(",");
                }
            }
            csvFileOutputStream.newLine();
        }
        csvFileOutputStream.flush();
    }
}
