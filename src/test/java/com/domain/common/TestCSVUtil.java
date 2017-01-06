package com.domain.common;

import org.junit.Test;

import java.util.*;

/**
 * com.domain.common
 * @author Mark Li
 * @version 1.0.0
 * @since 2017/1/6
 */
public class TestCSVUtil {

    @Test
    public void testWriteCSVFile() {

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "测试CSV" + i);
            map.put("age", 20 + i);
            map.put("sex", i % 2 == 1);
            dataList.add(map);
        }
        LinkedHashMap rowMapper = new LinkedHashMap();
        rowMapper.put("name", "姓名");
        rowMapper.put("age", "年龄");
        rowMapper.put("sex", "性别");
        String fileName = "tempCSV";
        String dir = "D:/Temp";
        CSVUtil.writeCSVFile(dataList, rowMapper, fileName, dir);
    }
}
