package com.company.integration.system;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@TableName("sys_system")
public class SysSystem {
    @TableId(type = IdType.AUTO)
    public Long id;
    @NotBlank
    public String systemCode;
    @NotBlank
    public String systemName;
    @NotBlank
    public String systemType;
    public String baseUrl;
    public String status;
    public String remark;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}
