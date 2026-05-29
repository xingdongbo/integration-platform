package com.company.integration.header;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@TableName("api_header")
public class ApiHeader {
    @TableId(type = IdType.AUTO)
    public Long id;
    @NotBlank
    public String interfaceCode;
    @NotBlank
    public String headerName;
    @NotBlank
    public String headerValue;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}
