package cn.varin.hututu.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * 图片审核状态枚举类
 */
@Getter
public enum ReviewStatusEnum {
    REVIEWING("待审核", 0),
    PASS("通过", 1),
    REJECT("拒绝", 2);

    private final String key;
    private final Integer value;
     ReviewStatusEnum(String key, Integer value){
        this.key = key;
        this.value = value;
    }

    /**
     * 通过value查询到当前的枚举
     * @param value
     * @return
     */
    public static ReviewStatusEnum getReviewEnum(Integer value){
        if (ObjectUtil.isEmpty(value)) {

            return null;
        }
        for( ReviewStatusEnum reviewStatusEnum : ReviewStatusEnum.values()){
            if(reviewStatusEnum.value==value){
                return reviewStatusEnum;
            }
        }
        return null;
    }
}
