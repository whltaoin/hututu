package cn.varin.hututu.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录接口
 */
@Data
public class UserLoginRequest  implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userAccount;
    private String userPassword;
}
