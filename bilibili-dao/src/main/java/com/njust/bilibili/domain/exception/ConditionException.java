package com.njust.bilibili.domain.exception;



public class ConditionException extends RuntimeException{   //条件异常，根据条件来抛异常
    private static final long serialVersionUID=1L;
    private String code;

    public ConditionException(String code,String name){
        super(name);
        this.code=code;
    }
    public ConditionException(String name){
        super(name);
        code="500"; //通用错误状态返回码
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
