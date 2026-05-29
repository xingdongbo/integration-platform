package com.company.integration.retry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@TableName("api_retry_task")
public class ApiRetryTask {
    @TableId(type = IdType.AUTO)
    public Long id;
    public String traceId;
    public String interfaceCode;
    public Integer retryCount;
    public Integer maxRetryCount;
    public String lastErrorMsg;
    public LocalDateTime nextRetryTime;
    public String status;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}
