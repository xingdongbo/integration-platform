package com.company.integration.retry;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class ApiRetryTaskServiceImpl extends ServiceImpl<ApiRetryTaskMapper, ApiRetryTask> implements ApiRetryTaskService {
}
