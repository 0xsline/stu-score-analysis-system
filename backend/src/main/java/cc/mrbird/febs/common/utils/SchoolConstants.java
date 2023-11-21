package cc.mrbird.febs.common.utils;

public class SchoolConstants {

    public static String COLLEGE = "C_";

    public static String GRADE = "G_";

    public static String EXAM_SCORE_ = "examScore_";

    public static String STUDENT_SCORE_ = "studentScore_";

    /**
     * 品牌
     */
    public static final String BRAND_NAME = "高校学生信息管理";

    public static final String BAR_SERIES = "{\n" +
            "          name: '%s',\n" +
            "          type: 'bar',\n" +
            "          itemStyle: {\n" +
            "            normal: {\n" +
            "              label: {\n" +
            "                show: true,\n" +
            "                position: 'top',\n" +
            "                textStyle: {\n" +
            "                  color: '#800080'\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }";


    public static final String LINE_SERIES = "{\n" +
            "          name: '%s',\n" +
            "          type: 'line',\n" +
            "          smooth: true,\n" +
            "          itemStyle: {\n" +
            "            normal: {\n" +
            "              label: {\n" +
            "                show: true,\n" +
            "                position: 'top',\n" +
            "                textStyle: {\n" +
            "                  color: '#800080'\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          },\n" +
            "          markPoint: {\n" +
            "            data: [\n" +
            "              {type: 'max', name: '最大值'},\n" +
            "              {type: 'min', name: '最小值'}\n" +
            "            ]\n" +
            "          },\n" +
            "          markLine: {\n" +
            "            data: [\n" +
            "              {type: 'average', name: '平均值'}\n" +
            "            ]\n" +
            "          }\n" +
            "        }";
}
