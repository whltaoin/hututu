package cn.varin.hututu.model.enums;

import lombok.Getter;

/**
 * 用户角色枚举类
 */
@Getter
public enum UserRoleEnum {

    USER("用户","user"),
    ADMIN("管理员","admin"),
    VIP("会员","vip");

    private final String key;
    private final String value;
     UserRoleEnum(String key,String value){
        this.key = key;
        this.value = value;
    }
}
