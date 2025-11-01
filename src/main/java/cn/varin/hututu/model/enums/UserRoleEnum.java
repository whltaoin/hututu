package cn.varin.hututu.model.enums;

import cn.varin.hututu.exception.ThrowUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
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

    /**
     * 通过value查询到当前的枚举
     * @param value
     * @return
     */
    public static UserRoleEnum getUserRoleEnum(String value){
        if (StringUtils.isEmpty(value)) {

            return null;
        }
        for( UserRoleEnum userRoleEnum : UserRoleEnum.values()){
            if(userRoleEnum.getValue().equals(value)){
                return userRoleEnum;
            }
        }
        return null;
    }
}
