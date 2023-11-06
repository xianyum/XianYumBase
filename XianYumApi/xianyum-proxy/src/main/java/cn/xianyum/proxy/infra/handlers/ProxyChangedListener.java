package cn.xianyum.proxy.infra.handlers;

/**
 * 监控配置是否发生变化
 * @author zhangwei
 * @date 2021/6/1 17:12
 */
public interface ProxyChangedListener {

    void onChanged();
}
