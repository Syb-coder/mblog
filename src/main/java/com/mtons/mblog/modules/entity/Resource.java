package com.mtons.mblog.modules.entity;

import lombok.Data;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 资源 Entity
 * <p>
 * 业务含义：每次上传文件计算 MD5 签名并入库，通过 {@code md5} 唯一约束去重，
 * {@link PostResource} 通过 {@code resource_id} 关联到本表主键。
 * </p>
 *
 * <p>关键约束：
 * <ul>
 *   <li>{@code md5} 通过 {@link UniqueConstraint}（约束名 {@code UK_MD5}）建立唯一约束，
 *       用于文件去重。</li>
 *   <li>{@code amount} 记录资源被引用次数，便于统计与清理。</li>
 *   <li>{@code create_time} / {@code update_time} 由数据库生成，
 *       通过 {@link Generated} 让 ORM 回读。</li>
 * </ul>
 * </p>
 *
 * <p>注：使用 Lombok {@link Data} 自动生成 getter/setter。</p>
 *
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
