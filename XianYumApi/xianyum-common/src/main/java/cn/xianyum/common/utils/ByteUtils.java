package cn.xianyum.common.utils;

import java.util.Locale;

/**
 * @author zhangwei
 * @date 2021/6/3 10:53
 */

public class ByteUtils {

    /**
     *
     * @param bytes 转换得字节
     * @param si 是否需要单位
     * @return
     */
    public static String byteFormat(long bytes, boolean si) {

        String[] units = new String[]{" B", " KB", " MB", " GB", " TB", " PB", " EB", " ZB", " YB"};
        int unit = 1024;
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        double pre = 0;
        if (bytes > 1024) {
            pre = bytes / Math.pow(unit, exp);
        } else if(bytes <= 1024 && bytes > 0){
            pre = (double) bytes / (double) unit;
        }else{
            pre = bytes;
        }
        if (si) {
            if(pre == 0){
                return "0B";
            }
            return String.format(Locale.ENGLISH, "%.1f%s", pre, units[(int) exp]);
        }
        return String.format(Locale.ENGLISH, "%.1f", pre);
    }

}
