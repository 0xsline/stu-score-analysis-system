package cc.mrbird.febs.common.utils;

import com.wuwenze.poi.pojo.ExcelMapping;
import com.wuwenze.poi.pojo.ExcelProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SuperExcelMappingFactory {

    private static final String PREFIX = "SB";


    /**
     * @param title
     * @param columnList
     * @return
     */
    public static ExcelMapping buildExcelMapping(String title, String[][] columnList) {
        ExcelMapping excelMapping = new ExcelMapping();
        excelMapping.setName(title);
        List<ExcelProperty> excelPropertyList = new ArrayList<>();
        for (int i = 0; i < columnList[0].length; i++) {
            ExcelProperty excelProperty = new ExcelProperty();
            excelProperty.setName(PREFIX + i);
            excelProperty.setColumn(columnList[0][i]);
            excelPropertyList.add(excelProperty);
        }
        excelMapping.setPropertyList(excelPropertyList);
        return excelMapping;
    }


    public static List<Map<String, String>> buildExcelData(String[][] columnList) {
        List<Map<String, String>> mapList = new ArrayList<>();
        for (int i = 1; i < columnList.length; i++) {
            String[] dataList = columnList[i];
            Map<String, String> map = new HashMap<>();
            for (int j = 0; j < dataList.length; j++) {
                map.put(PREFIX + j, dataList[j]);
            }
            mapList.add(map);
        }

        return mapList;
    }
}
