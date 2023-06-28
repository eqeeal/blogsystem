package com.example.blogsystem.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * 自定义源对象处理器
 */

@Component
@Slf4j
public class MyMateObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充[insert]");
        if(metaObject.hasSetter("createTime"))
            metaObject.setValue("createTime", LocalDateTime.now());
        if(metaObject.hasSetter("updateTime"))
            metaObject.setValue("updateTime", LocalDateTime.now());
        if(metaObject.hasSetter("updataTime"))
            metaObject.setValue("updataTime", LocalDateTime.now());
        Long empId=1l;
        if(metaObject.hasSetter("createUser"))
            metaObject.setValue("createUser", empId);
        if(metaObject.hasSetter("updateUser"))
            metaObject.setValue("updateUser",empId);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充[update]");
        log.info(metaObject.toString());
        Long empId=1l;
        if(metaObject.hasSetter("updateUser"))
            metaObject.setValue("updateUser", empId);
        if(metaObject.hasSetter("updateTime"))
            metaObject.setValue("updateTime", LocalDateTime.now());
    }
}
