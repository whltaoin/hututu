package cn.varin.hututu.model.dto.space;

import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;

/**
 * 空间Edit:只能修改空间名称
 * @TableName space
 */
@Data
public class SpaceEditRequest implements Serializable {
    private static final long serialVersionUID = 1L;
     private Long id;

    /**
     * 空间名称
     */
    private String spaceName;



}