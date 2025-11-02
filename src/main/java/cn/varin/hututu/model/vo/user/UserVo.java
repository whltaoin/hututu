package cn.varin.hututu.model.vo.user;

import lombok.Data;

import java.util.Date;

/**
 * 用户拖敏数据
 */
@Data

public class UserVo {
    /**
     * id
     */
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

    private Date createTime;



}
