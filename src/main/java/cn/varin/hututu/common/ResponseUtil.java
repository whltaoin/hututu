package cn.varin.hututu.common;


import cn.varin.hututu.exception.ResponseCode;

public class ResponseUtil {
    /**
     *
     * @param data 数据
     * @return 响应
     * @param <T> 数据类型
     */
    public static<T> BaseResponse<T> success(T data) {
        return  new BaseResponse<>(200, "请求成功", data);
    }

    /**
     *
     * @param responseCode 响应吗枚举
     * @return 响应
     */
    public static BaseResponse<?> error(ResponseCode responseCode) {
        return new BaseResponse<>(responseCode);
    }

    /**
     *
     * @param code 响应码
     * @param message 响应消息
     * @return 响应体
     */
    public static BaseResponse<?> error(Integer code, String message) {
    return new BaseResponse<>(code, message, null);}

    /**
     *
     * @param responseCode 响应枚举
     * @param message 响应消息
     * @return 响应体
     */
    public static BaseResponse<?> error(ResponseCode responseCode, String message) {
        return new BaseResponse<>(responseCode.getCode(), message, null);
    }
}
