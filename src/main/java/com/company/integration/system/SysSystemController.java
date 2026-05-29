package com.company.integration.system;

import com.company.integration.common.ApiResponse;
import com.company.integration.common.BaseController;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/systems")
public class SysSystemController extends BaseController<SysSystem> {
    private final SysSystemService service;

    public SysSystemController(SysSystemService service) {
        super(service);
        this.service = service;
    }
}
