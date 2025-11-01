package cn.varin.hututu.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 *用户更新请求
 */
@Data

public class UserUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    /**
     * 账号
     */
    private String userAccount;


    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色: user/admin
     */
    private String userRole;


}
