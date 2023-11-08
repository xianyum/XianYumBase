package cn.xianyum.common.handler;

/**
 * @Description
 * @Author ZhangWei
 * @Date 2023/11/1 14:18
 */
public class PermissionThreadLocal {

    private static final ThreadLocal<Boolean> permissionThreadLocal = new ThreadLocal(){
        @Override
        protected Boolean initialValue() {
            return false;
        }
    };

    public PermissionThreadLocal() {
    }

    public static boolean get() {
        return permissionThreadLocal.get();
    }

    public static void set() {
        permissionThreadLocal.set(true);
    }

    public static void remove() {
        permissionThreadLocal.remove();
    }
}
