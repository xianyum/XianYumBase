package cn.xianyum.common.utils;

/**
 * 版本号比较工具类（支持x.y.z、x.y、x等格式，如1.0.1、2.1、3）
 * @author xianyum
 * @date 2026/1/23 21:13
 */
public class AppVersionCompareUtil {

    /**
     * 比较两个版本号，判断客户端版本是否需要更新
     * @param clientVersion 客户端传入的版本号（如1.0.0）
     * @param latestVersion 服务端最新版本号（如1.0.1）
     * @return true=需要更新，false=无需更新
     */
    public static boolean needUpdate(String clientVersion, String latestVersion) {
        // 空值处理：客户端版本为空/最新版本为空，默认无需更新（可根据业务调整）
        if (StringUtil.isBlank(clientVersion) || StringUtil.isBlank(latestVersion)) {
            return false;
        }

        // 分割版本号为数组（按.拆分）
        String[] clientParts = clientVersion.split("\\.");
        String[] latestParts = latestVersion.split("\\.");

        // 取最长长度，补零对齐（如1.0 和 1.0.1 → 1.0.0 和 1.0.1）
        int maxLength = Math.max(clientParts.length, latestParts.length);
        for (int i = 0; i < maxLength; i++) {
            // 超出长度的部分补0
            int clientNum = i < clientParts.length ? parseInt(clientParts[i]) : 0;
            int latestNum = i < latestParts.length ? parseInt(latestParts[i]) : 0;

            // 比较每一位数字
            if (latestNum > clientNum) {
                return true; // 最新版本更大，需要更新
            } else if (latestNum < clientNum) {
                return false; // 客户端版本更大，无需更新
            }
            // 相等则继续比较下一位
        }

        // 版本号完全相同，无需更新
        return false;
    }

    /**
     * 字符串转数字（容错处理，非数字返回0）
     */
    private static int parseInt(String str) {
        try {
            return Integer.parseInt(str.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
