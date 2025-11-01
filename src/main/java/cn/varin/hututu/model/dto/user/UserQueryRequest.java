package cn.varin.hututu.model.dto.user;

import cn.varin.hututu.common.PageRequest;
import lombok.Data;

import java.io.Serializable;

/**
 *用户查询请求
 */
@Data
public class UserQueryRequest  extends PageRequest implements Serializable {

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
