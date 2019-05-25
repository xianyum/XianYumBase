package com.base.common.exception;

/**
 * 简称 索马里异常
 * 自定义异常
 * @author zhangwei
 * @date 2019/1/31 14:16
 */
public class SoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public SoException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public SoException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public SoException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public SoException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }


}
