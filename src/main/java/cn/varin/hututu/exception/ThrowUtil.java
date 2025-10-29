package cn.varin.hututu.exception;

/**
 * 异常工具类
 */
public class ThrowUtil {
    /**
     * 条件成立，抛运行时异常
     * @param flag 条件
     * @param runtimeException 异常
     */
    public static void throwIf(Boolean flag, RuntimeException runtimeException) {
        if (flag) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立，抛异常
     * @param flag 条件
     * @param responseCode 响应码
     */
    public static void throwIf(Boolean flag,ResponseCode responseCode) {
        if (flag) {
            throwIf(flag,new CustomizeException(responseCode));
        }
    }

    /**
     * 条件成立，抛异常
     * @param flag 条件
     * @param code 响应码
     * @param message 响应信息
     */
    public static void throwIf(Boolean flag,Integer code,String message) {
        if (flag) {
            throwIf(flag,new CustomizeException(code,message));
        }
    }


}
