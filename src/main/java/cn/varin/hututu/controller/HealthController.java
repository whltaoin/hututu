package cn.varin.hututu.controller;

import cn.varin.hututu.common.BaseResponse;
import cn.varin.hututu.common.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HealthController {
    /**
     * 项目健康检查
     * @return
     */
    @GetMapping("/health")
    public BaseResponse health() {
        return ResponseUtil.success("success");
    }
}
