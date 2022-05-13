package com.njust.bilibili.api;


import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController //说明是RESTful风格的控制器
public class RESTfulAPI {
    private final Map<Integer, Map<String,Object>>dataMap;
    private Integer maxID=1;
    public RESTfulAPI(){
        dataMap=new HashMap<>();
        for(int i=1;i<=10;i++){
            Map<String,Object>data=new HashMap<>();
            data.put("id",i);
            data.put("name","小"+i);
            dataMap.put(i,data);
            maxID=Math.max(i,maxID);
        }
    }

    @GetMapping("/objects/{id}") //响应get请求，获取某一数据
    public Map<String,Object>getData(@PathVariable Integer id){ //这个注解让参数与上边的{id}关联
        return dataMap.get(id);
    }
    @GetMapping("/objects")
    public Map<Integer, Map<String,Object>>getAllData(){
        return dataMap;
    }
    @PostMapping("/objects/{id}")   //响应post请求，删除数据
    public String deleteData(@PathVariable Integer id){
        dataMap.remove(id);
        return "delete success";
    }
    @PostMapping("/objects")    //响应post请求，添加数据
    public String postData(@RequestBody Map<String,Object>data){ //会将请求的json封装成参数传入
        maxID++;
        dataMap.put(maxID,data);
        return "post success";
    }
    @PutMapping("/objects") //响应put请求，添加数据（注意，有幂等性）
    public String putData(@RequestBody Map<String,Object>data){
        Integer id=Integer.valueOf(String.valueOf(data.get("id")));
        Object res=dataMap.put(id,data);   //如果有就更新，如果没有就添加
        maxID=Math.max(maxID,id);
        if(res==null){
            return "put success new value";
        }else{
            return "put success update old value";
        }
    }
}
