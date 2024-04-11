package com.heima.file.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "minio")  // 文件上传 配置前缀file.oss
//适配任何需求的延迟任务
//redis
//并发 数据库锁机制
//分布式锁。
//乐观锁。
//悲观锁。
//redis实现分布式锁。
//提升执行效率。
//管道技术。多个redis合并为一个。
public class MinIOConfigProperties implements Serializable {

    private String accessKey;
    private String secretKey;
    private String bucket;
    private String endpoint;
    private String readPath;
}
