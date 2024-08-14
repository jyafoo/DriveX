package com.atguigu.daijia.model.entity.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 实体基类
 */
@Data
public class BaseEntity implements Serializable {

    /**
     * 自动增长的主键ID
     * 通过@TableId注解指定为主键，并设置增长策略为自动增长
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间戳
     * 用于记录实体创建的时间，使用@TableField注解将字段映射到表的create_time列
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 更新时间戳
     * 用于记录实体最近一次更新的时间，使用@JsonIgnore避免被JSON序列化
     */
    @JsonIgnore
    @TableField("update_time")
    private Date updateTime;

    /**
     * 逻辑删除标记
     * 用于标记实体是否被逻辑删除，使用@JsonIgnore避免被JSON序列化，@TableLogic支持逻辑删除操作
     */
    @JsonIgnore
    @TableLogic
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 扩展参数映射
     * 用于存储实体的额外属性，这些属性可能未在表中预定义，使用@TableField注解设置exist参数为false表示该字段不在数据库表中
     */
    @JsonIgnore
    @TableField(exist = false)
    private Map<String, Object> param = new HashMap<>();
}