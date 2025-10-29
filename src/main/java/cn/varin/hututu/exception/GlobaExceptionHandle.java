package cn.varin.hututu.exception;

import cn.varin.hututu.common.BaseResponse;
import cn.varin.hututu.common.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 */
@RestControllerAdvice
@Slf4j
public class GlobaExceptionHandle {
    /**
     * 自定义异常
     * @param customizeException 自定义异常
     * @return 响应体
     */
    @ExceptionHandler(value = CustomizeException.class)
    public BaseResponse<?> customizeExceptionHandle (CustomizeException customizeException) {
        log.error("CustomizeException>>>>>",customizeException);
        return ResponseUtil.error(customizeException.getCode(), customizeException.getMessage());
    }


    @ExceptionHandler(value = RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandle (RuntimeException runtimeException) {
        log.error("RuntimeException>>>>>",runtimeException);
        return ResponseUtil.error(ResponseCode.SYSTEM_ERROR.getCode(), ResponseCode.SYSTEM_ERROR.getMessage());
    }


}
