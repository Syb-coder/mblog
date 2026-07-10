package com.sunblog.modules.entity;

import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资源实体 —— 对应数据库表 mto_resource
 *
 * <h3>业务含义</h3>
 * 记录每一个上传到系统的文件。每次上传文件时，系统会计算文件的 MD5 值，
 * 然后先查询 Resource 表是否已有同 MD5 的记录：
 * - 如果有（文件已存在），则 amount+1（增加引用计数）
 * - 如果没有，则创建新记录
 * 这样实现了文件去重：即使同一张图片被十篇文章引用，磁盘上只存一份。
 *
 * <h3>资源引用生命周期</h3>
 * <pre>
 * 用户上传图片 → Resource 表新增/计数+1 → PostResource 新增关联
 * 文章删除      → PostResource 删除关联 → Resource.amount-1
 * amount=0     → 资源不再被引用，可被清理
 * </pre>
 *
 * <h3>关键字段</h3>
 * <ul>
 *   <li>md5 —— 文件内容的 MD5 摘要，加唯一约束实现去重</li>
 *   <li>path —— 文件的存储路径（相对路径，基于 site.location 配置）</li>
 *   <li>amount —— 该资源被多少篇文章引用，0 表示可回收</li>
 *   <li>create_time / update_time —— 由数据库自动生成和更新（@Generated 注解回读）</li>
 * </ul>
 */
@Data
@Entity
// 唯一约束 UK_MD5 基于 md5 列，确保相同内容的文件只入库一次，实现去重
@Table(name = "mto_resource",
        uniqueConstraints = {@UniqueConstraint(name = "UK_MD5", columnNames = {"md5"})}
)
public class Resource implements Serializable {
    private static final long serialVersionUID = -2263990565349962964L;

    /**
     * 主键ID
     * <p>自增主键，对应表 {@code mto_resource.id}，不可空。</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    /**
     * 签名
     * <p>对应列 {@code md5}，长度 100，默认空串；文件内容 MD5 摘要，配合唯一约束实现去重。</p>
     */
    @Column(name = "md5", columnDefinition = "varchar(100) NOT NULL DEFAULT ''")
    private String md5;

    @Column(name = "path", columnDefinition = "varchar(255) NOT NULL DEFAULT ''")
    private String path;

    /**
     * 引用次数
     * <p>对应列 {@code amount}，默认 0；记录资源被文章引用的次数，便于去重判断与清理。</p>
     */
    @Column(name = "amount", columnDefinition = "bigint(20) NOT NULL DEFAULT '0'")
    private long amount;

    @Column(name = "create_time")
    @Generated(GenerationTime.INSERT)
    private LocalDateTime createTime;

    /**
     * <p>对应列 {@code update_time}，数据库默认 {@code CURRENT_TIMESTAMP} 且 {@code ON UPDATE CURRENT_TIMESTAMP}；
     * {@link Generated}({@link GenerationTime#ALWAYS}) 表示在 insert 与 update 后均由数据库生成，ORM 每次写操作后回读。</p>
     */
    @Column(name = "update_time", columnDefinition = "datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    @Generated(GenerationTime.ALWAYS)
    private LocalDateTime updateTime;

}
