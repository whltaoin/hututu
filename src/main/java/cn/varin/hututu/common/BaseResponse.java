package cn.varin.hututu.common;

import cn.varin.hututu.exception.ResponseCode;
import io.swagger.models.auth.In;
import lombok.Data;
import org.apache.catalina.valves.rewrite.RewriteCond;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serializable;

/**
 * 请求响应体
 */
@Data
public class BaseResponse<T>  implements Serializable {
    private Integer code;
    private String message;
    private T data;
    public BaseResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    public BaseResponse(Integer code, String message) {
        this(code, message, null);
    }
    public BaseResponse(ResponseCode responseCode) {
        this(responseCode.getCode(), responseCode.getMessage(), null);

    }

}
