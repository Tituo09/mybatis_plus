package com.atguigu.mybatis_plus.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import sun.rmi.runtime.Log;

import java.util.Date;

/**
 * @author Tituo
 * @create 2020-02-11 19:49
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    /**
     *插入时自动填充
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("insert自动填充");
        //根据字段名（属性名不是数据库表中的字段名）设置字段值
        //第一个参数：实体属性名
        //第二个参数：数据类型和实体属性
        //第三个参数：元数据对象
        this.setFieldValByName("creatTime",new Date(),metaObject);
        this.setFieldValByName("updateTime",new Date(),metaObject);
    }

    /**
     * 修改时自动填充
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("update自动填充");
        this.setFieldValByName("updateTime",new Date(),metaObject);
    }
}
