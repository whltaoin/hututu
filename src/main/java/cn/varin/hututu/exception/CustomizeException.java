package cn.varin.hututu.exception;


import lombok.Getter;

/**
 * 自定义异常类
 */
@Getter

public class CustomizeException extends RuntimeException {
    private final Integer code;
    public CustomizeException(Integer code,String message ) {
        super(message);
        this.code = code;
    }

    public CustomizeException(ResponseCode responseCode ) {
        super(responseCode.getMessage());
        this.code = responseCode.getCode();
    }
    public CustomizeException(ResponseCode responseCode ,String message) {
        super(message);
        this.code = responseCode.getCode();
    }


}
