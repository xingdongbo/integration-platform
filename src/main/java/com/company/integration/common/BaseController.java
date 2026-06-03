package com.company.integration.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.lang.reflect.Field;

public abstract class BaseController<T> {
    private final IService<T> service;

    protected BaseController(IService<T> service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<IPage<T>> page(@RequestParam(defaultValue = "1") long current,
                                      @RequestParam(defaultValue = "20") long size) {
        return ApiResponse.success(service.page(Page.of(current, size)));
    }

    @GetMapping("/{id}")
    public ApiResponse<T> get(@PathVariable Serializable id) {
        return ApiResponse.success(service.getById(id));
    }

    @PostMapping
    public ApiResponse<T> create(@Valid @RequestBody T body) {
        service.save(body);
        return ApiResponse.success(body);
    }

    @PutMapping("/{id}")
    public ApiResponse<T> update(@PathVariable Long id, @Valid @RequestBody T body) {
        setId(body, id);
        service.updateById(body);
        return ApiResponse.success(body);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Serializable id) {
        return ApiResponse.success(service.removeById(id));
    }

    private void setId(T body, Long id) {
        try {
            Field field = body.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(body, id);
        } catch (ReflectiveOperationException ex) {
            throw new BusinessException("entity does not expose an id field", ex);
        }
    }
}
