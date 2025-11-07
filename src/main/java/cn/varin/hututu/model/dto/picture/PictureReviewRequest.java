package cn.varin.hututu.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 审核状态请求包装类
 */
@Data

public class PictureReviewRequest implements Serializable {
    private Long id;


    private Integer reviewStatus;


    private String reviewMessage;


    private static final long serialVersionUID = 1L;
}
