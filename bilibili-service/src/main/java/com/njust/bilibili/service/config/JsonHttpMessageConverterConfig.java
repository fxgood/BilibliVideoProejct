package com.njust.bilibili.service.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration  //配置相关
public class JsonHttpMessageConverterConfig {

    @Bean
    @Primary    //高优先级
    public HttpMessageConverters fastjsonHttpMessageConverter(){
        FastJsonHttpMessageConverter fastConverter=new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig=new FastJsonConfig();
        fastJsonConfig.setDateFormat("yyyy-MM-dd hh:mm:ss");    //HH?
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,//标准json格式化，缩进，换行
                SerializerFeature.WriteNullStringAsEmpty,   //没有数据，则赋值为空字符串，否则值为null的字段不会返回给前端
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.MapSortField,
                SerializerFeature.DisableCircularReferenceDetect    //禁用循环引用
        );
        fastConverter.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fastConverter);
    }
}
