package cc.mrbird.febs.common.utils;


import java.util.List;

public class HistogramUtils {

    /**
     * 构造直方图
     *
     * @param threshold
     * @param scoreList
     * @return
     */
    public static Object[][] buildHistogram(Double threshold, List<Double> scoreList) {
        int INSTANCE = threshold.intValue() / 10;
        int split = threshold.intValue() / INSTANCE;
        int[] array = new int[INSTANCE + 1];
        scoreList.forEach(score -> {
            int i = score.intValue() / split;
            if (i <= INSTANCE) {
                array[i] = array[i] + 1;
            }
        });
        array[INSTANCE - 1] = array[INSTANCE - 1] + array[INSTANCE];
        Object[][] result = new Object[INSTANCE][4];
        for (int i = 0; i < INSTANCE; i++) {
            result[i][0] = split * i;
            result[i][1] = split * (i + 1);
            result[i][2] = array[i];
            result[i][3] = result[i][0] + " ~ " + result[i][1];
        }

        System.out.println("直方图数据打印结果");
        printTwoArray(result);
        return result;
    }

    /**
     * 构造直方图折线图标题
     *
     * @param threshold
     * @return
     */
    public static String[] buildLineTitle(Double threshold) {
        int INSTANCE = threshold.intValue() / 10;
        int split = threshold.intValue() / INSTANCE;
        String[] array = new String[INSTANCE];
        for (int i = 0; i < INSTANCE; i++) {
            array[i] = String.valueOf(i * split + Double.valueOf(split) / 2.0);
        }
        return array;
    }

    /**
     * 打印二维数组
     *
     * @param a
     */
    public static void printTwoArray(Object[][] a) {
        for (int i = 0, j = 0; i < a.length; ) {
            System.out.println(a[i][j]);
            j++;
            if (j >= a[i].length) {
                i++;
                j = 0;
            }
        }
    }
}
