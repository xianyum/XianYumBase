package cn.xianyum.common.handler;

/**
 * @Description
 * @Author ZhangWei
 * @Date 2023/11/1 14:18
 */
public class PermissionThreadLocal {

    private static final InheritableThreadLocal<Boolean> inheritableThreadLocal = new InheritableThreadLocal() {
        protected Boolean initialValue() {
            return false;
        }
    };

    public PermissionThreadLocal() {
    }

    public static boolean get() {
        return inheritableThreadLocal.get();
    }

    public static void set() {
        inheritableThreadLocal.set(true);
    }

    public static void remove() {
        inheritableThreadLocal.remove();
    }
}
