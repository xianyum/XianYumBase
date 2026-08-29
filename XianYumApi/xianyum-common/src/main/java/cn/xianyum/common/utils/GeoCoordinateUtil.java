package cn.xianyum.common.utils;

/**
 * 坐标转换工具类
 * 提供 WGS84、GCJ02、BD09 等坐标系之间的转换
 */
public final class GeoCoordinateUtil {

    private GeoCoordinateUtil() {
    }

    private static final double PI = 3.14159265358979324;
    private static final double A = 6378245.0;
    private static final double EE = 0.006693421622965823;

    /**
     * 将WGS84坐标转为GCJ02（火星坐标系）
     * 适用于中国境内坐标，境外坐标不转换
     *
     * @param lng WGS84经度
     * @param lat WGS84纬度
     * @return GCJ02坐标 [经度, 纬度]
     */
    public static double[] wgs84ToGcj02(double lng, double lat) {
        if (outOfChina(lng, lat)) {
            return new double[]{lng, lat};
        }
        double dLat = transformLat(lng - 105.0, lat - 35.0);
        double dLng = transformLng(lng - 105.0, lat - 35.0);
        double radLat = lat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLng = (dLng * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
        return new double[]{lng + dLng, lat + dLat};
    }

    /**
     * 将GCJ02坐标转为WGS84坐标
     *
     * @param lng GCJ02经度
     * @param lat GCJ02纬度
     * @return WGS84坐标 [经度, 纬度]
     */
    public static double[] gcj02ToWgs84(double lng, double lat) {
        double[] gcj02 = wgs84ToGcj02(lng, lat);
        return new double[]{lng * 2 - gcj02[0], lat * 2 - gcj02[1]};
    }

    /**
     * 将GCJ02坐标转为BD09（百度坐标系）
     *
     * @param lng GCJ02经度
     * @param lat GCJ02纬度
     * @return BD09坐标 [经度, 纬度]
     */
    public static double[] gcj02ToBd09(double lng, double lat) {
        double z = Math.sqrt(lng * lng + lat * lat) + 0.00002 * Math.sin(lat * PI);
        double theta = Math.atan2(lat, lng) + 0.000003 * Math.cos(lng * PI);
        return new double[]{z * Math.cos(theta) + 0.0065, z * Math.sin(theta) + 0.006};
    }

    /**
     * 将BD09坐标转为GCJ02坐标
     *
     * @param lng BD09经度
     * @param lat BD09纬度
     * @return GCJ02坐标 [经度, 纬度]
     */
    public static double[] bd09ToGcj02(double lng, double lat) {
        double x = lng - 0.0065;
        double y = lat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * PI);
        return new double[]{z * Math.cos(theta), z * Math.sin(theta)};
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLng(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }

    private static boolean outOfChina(double lng, double lat) {
        return !(72.004 <= lng && lng <= 137.8347 && 0.8293 <= lat && lat <= 55.8271);
    }
}
