package com.domain.common;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * com.domain.java.util
 * @author Mark Li
 * @version 1.0.0
 * @since 2016/12/21
 */
public class ExcelUtil {

    private static final Logger logger = LoggerFactory.getLogger(ExcelUtil.class);

    public static List<String[]> readExcel(String filePath) {

        List<String[]> result = new ArrayList<>();
        try {
            // 方案一，只能读xlsx
            File excel = new File(filePath);
            FileInputStream fis = new FileInputStream(excel);
            XSSFWorkbook book = new XSSFWorkbook(fis);
            XSSFSheet sheet = book.getSheetAt(0);
            // 方案二，可读xlsx和xls
            /*Workbook book = WorkbookFactory.create(new File(filePath));
            Sheet sheet = book.getSheetAt(0);*/

            Iterator<Row> itr = sheet.iterator();

            System.out.println(itr.hasNext());
            // Iterating over Excel file in Java
            while (itr.hasNext()) {
                Row row = itr.next();
                if (row.getRowNum() == 0) continue; // 跳过首行

                String[] values = new String[row.getLastCellNum() + 1];

                // Iterating over each column of Excel file
                Iterator<Cell> cellIterator = row.cellIterator();
                int index = 0;
                while (cellIterator.hasNext()) {
                    String value;
                    Cell cell = cellIterator.next();
                    switch (cell.getCellType()) {
                        case Cell.CELL_TYPE_STRING:
                            value = cell.getStringCellValue();
                            break;
                        case Cell.CELL_TYPE_NUMERIC:
                            if (DateUtil.isCellDateFormatted(cell)) {
                                Date date = cell.getDateCellValue();
                                if (date != null) {
                                    value = com.domain.common.DateUtil.时分秒.format(date);
                                } else {
                                    value = "";
                                }
                            } else {
                                value = new DecimalFormat("0").format(cell.getNumericCellValue());
                            }
                            break;
                        default:
                            value = "";
                    }
                    values[index++] = rightTrim(value);
                }
                result.add(values);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return result;
    }

    /**
     * 去掉字符串右边的空格
     * @param str 要处理的字符串
     * @return 处理后的字符串
     */
    private static String rightTrim(String str) {

        if (str == null) {
            return "";
        }
        int length = str.length();
        for (int i = length - 1; i >= 0; i--) {
            if (str.charAt(i) != 0x20) {
                break;
            }
            length--;
        }

        return str.substring(0, length);
    }

    public static <T> void writeExcel(String fileName, String[] heads, String[] properties, Collection<T> dataList) {

        HSSFWorkbook book = new HSSFWorkbook();
        HSSFSheet sheet = book.createSheet();
        HSSFRow row = sheet.createRow(0);
        HSSFCellStyle style = book.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER);

        for (int column = 0; column < heads.length; column++) {
            HSSFCell cell = row.createCell(column);
            cell.setCellStyle(style);
            HSSFRichTextString text = new HSSFRichTextString(heads[column]);
            cell.setCellValue(text);
        }

        Iterator<T> iterator = dataList.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            index++;
            row = sheet.createRow(index);
            T t = iterator.next();
            for (int column = 0; column < heads.length; column++) {
                HSSFCell cell = row.createCell(column);
                String property = properties[column];
                Object obj;
                if (t instanceof Map) {
                    Map map = (Map) t;
                    obj = map.get(property);
                } else {
                    String methodName = "get" + property.substring(0, 1).toUpperCase() + property.substring(1);
                    Class clazz = t.getClass();
                    Method method;
                    try {
                        method = clazz.getMethod(methodName);
                    } catch (NoSuchMethodException e) {
                        logger.warn(e.getMessage(), e);
                        methodName = "is" + property.substring(0, 1).toUpperCase() + property.substring(1);
                        try {
                            method = clazz.getMethod(methodName);
                        } catch (NoSuchMethodException e1) {
                            logger.error(e.getMessage(), e);
                            continue;
                        }
                    }
                    try {
                        obj = method.invoke(t);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        logger.error(e.getMessage(), e);
                        continue;
                    }
                }
                String text;
                if (obj instanceof Boolean) {
                    boolean value = (boolean) obj;
                    text = value ? "true" : "false";
                } else if (obj instanceof Date) {
                    Date date = (Date) obj;
                    text = com.domain.common.DateUtil.时分秒.format(date);
                } else {
                    text = obj.toString();
                }
                if (text != null) {
                    Pattern p = Pattern.compile("^\\d+(\\.\\d+)?$");
                    Matcher matcher = p.matcher(text);
                    if (matcher.matches()) {
                        // 是数字当作double处理
                        cell.setCellValue(Double.parseDouble(text));
                    } else {
                        HSSFRichTextString richString = new HSSFRichTextString(text);
                        /*HSSFFont font03 = book.createFont();
                        font03.setColor(HSSFColor.BLUE.index);
                        richString.applyFont(font03);*/
                        cell.setCellValue(richString);
                    }
                }
            }
        }
        OutputStream os = null;
        try {
            os = new FileOutputStream(fileName);
            book.write(os);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                    throw new RuntimeException("Close failed!");
                }
            }
        }
    }
}
