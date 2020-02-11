package com.atguigu.mybatis_plus.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.util.Date;

/**
 * @author Tituo
 * @create 2020-02-11 18:39
 */
@Data
public class User {
    /*
     *AUTO自增策略
     *  @TableId(type = IdType.AUTO)
     *ID_WORKER（默认值）
     *ID_WORKER_STR（默认值）
     *  @TableId(type = IdType.ID_WORKER_STR)
     *  private String id;
     */




    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    /**
     * @TableField(fill = FieldFill.UPDATE)
     * INSERT_UPDATE即在新增时创建也在修改时创建
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    private Long id;
    private String name;
    private Integer age;
    private String email;

    /**
     * 用户信息被查看的次数
     */
    private Integer viewCount;

    /**
     * 乐观锁版本号
     */
    @Version
    private Integer version;

    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}
