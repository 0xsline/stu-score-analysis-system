package cc.mrbird.febs.common.utils;

import com.wuwenze.poi.exception.ExcelKitRuntimeException;
import com.wuwenze.poi.pojo.ExcelMapping;
import com.wuwenze.poi.util.Const;
import com.wuwenze.poi.util.POIUtil;
import com.wuwenze.poi.xlsx.ExcelXlsxWriter;
import java.net.URLEncoder;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

/**
 * @author IU
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SuperExcelKit {

    private ExcelMapping mExcelMapping = null;
    private HttpServletResponse mResponse = null;
    private Integer mMaxSheetRecords = 50000;
    private String mCurrentOptionMode = SuperExcelKit.MODE_EXPORT;
    private final static String MODE_EXPORT = "$MODE_EXPORT$";

    /**
     * 使用此构造器来执行浏览器导出
     *
     * @param excelMapping 导出实体对象
     * @param response 原生 response 对象, 用于响应浏览器下载
     * @return ExcelKit obj.
     * @see com.wuwenze.poi.ExcelKit#downXlsx(List, boolean)
     */
    public static SuperExcelKit $Export(ExcelMapping excelMapping, HttpServletResponse response) {
        return new SuperExcelKit(excelMapping, response);
    }

    public void downXlsx(List<?> data, boolean isTemplate) {
        if (!mCurrentOptionMode.equals(SuperExcelKit.MODE_EXPORT)) {
            throw new ExcelKitRuntimeException(
                    "请使用cc.mrbird.febs.common.utils.SuperExcelKit.$Export(Class<?> clazz, HttpServletResponse response)构造器初始化参数.");
        }
        try {
            ExcelMapping excelMapping = mExcelMapping;
            ExcelXlsxWriter excelXlsxWriter = new ExcelXlsxWriter(excelMapping,
                    mMaxSheetRecords);
            SXSSFWorkbook workbook = excelXlsxWriter.generateXlsxWorkbook(data, isTemplate);
            String fileName = isTemplate ? (excelMapping.getName() + "-导入模板.xlsx")
                    : (excelMapping.getName() + "-导出结果.xlsx");
            POIUtil.download(workbook, mResponse, URLEncoder.encode(fileName, Const.ENCODING));
        } catch (Throwable e) {
            throw new ExcelKitRuntimeException("downXlsx error", e);
        }
    }

    protected SuperExcelKit(ExcelMapping excelMapping, HttpServletResponse response) {
        mExcelMapping = excelMapping;
        mResponse = response;
        mCurrentOptionMode = SuperExcelKit.MODE_EXPORT;
    }
}