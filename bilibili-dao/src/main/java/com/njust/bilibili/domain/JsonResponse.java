package com.njust.bilibili.domain;

public class JsonResponse<T> {
    private String code, msg;
    private T data;

    public String getCode() {
        return code;
    }

    public JsonResponse(T data) {
        this.data = data;
        msg = "成功";
        code = "0";
    }

    public JsonResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    //用于前端一些请求成功但不需要返回数据给前端的场景
    public static JsonResponse<String> success() {
        return new JsonResponse<>(null);
    }

    //请求成功，并返回给前端一些数据
    public static JsonResponse<String> success(String data) {
        return new JsonResponse<>(data);
    }

    //请求失败
    public static JsonResponse<String>fail(){
        return new JsonResponse<>("1","失败");
    }

    //请求失败，返回定制化信息
    public static JsonResponse<String>fail(String code,String msg){
        return new JsonResponse<>(code,msg);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
