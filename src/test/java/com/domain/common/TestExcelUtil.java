package com.domain.common;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * com.domain.common
 * @author Mark Li
 * @version 1.0.0
 * @since 2017/1/6
 */
public class TestExcelUtil {

    @Test
    public void testWriteExcel() {

        String fileName = "D:\\Temp\\temp.xls";
        String[] heads = new String[]{"姓名", "年龄", "性别"};
        String[] properties = new String[]{"name", "age", "sex"};
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", "测试Excel" + i);
            map.put("age", 20 + i);
            map.put("sex", i % 2 == 1);
            dataList.add(map);
        }
        ExcelUtil.writeExcel(fileName, heads, properties, dataList);
    }
}
