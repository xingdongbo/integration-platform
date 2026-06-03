package com.company.integration.log;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@TableName("api_exchange_log")
public class ApiExchangeLog {
    @TableId(type = IdType.AUTO)
    public Long id;
    public String traceId;
    public String interfaceCode;
    public String sourceSystem;
    public String targetSystem;
    public String businessNo;
    public String sourceRequestRaw;
    public String targetRequestData;
    public String targetResponseRaw;
    public String sourceResponseData;
    public String status;
    public String errorMsg;
    public Long executeTime;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}
