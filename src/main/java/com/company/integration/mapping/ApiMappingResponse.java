package com.company.integration.mapping;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@TableName("api_mapping_response")
public class ApiMappingResponse {
    @TableId(type = IdType.AUTO)
    public Long id;
    @NotBlank
    public String interfaceCode;
    public String sourceField;
    @NotBlank
    public String targetField;
    @NotBlank
    public String mappingType;
    public String defaultValue;
    public String expression;
    public Integer sort;
    public LocalDateTime createTime;
    public LocalDateTime updateTime;
}
