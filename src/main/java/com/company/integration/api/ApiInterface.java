package com.company.integration.api;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@TableName("api_interface")
public class ApiInterface {
    @TableId(type = IdType.AUTO)
    public Long id;
    @NotBlank
    public String interfaceCode;
    @NotBlank
    public String interfaceName;
    @NotBlank
    public String sourceSystem;
    @NotBlank
    public String targetSystem;
    @NotBlank
    public String targetUrl;
    public String method;
    public String contentType;
    public Integer timeout;
    public String status;
    public String remark;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}
