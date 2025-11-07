package cn.varin.hututu.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片上传批量请求
 */
@Data
public class PictureUploadByBatchRequest implements Serializable {
    /**
     * 搜索词
     */
    private String searchText;
    // 名称前缀
    private String nameFrefix;

    // 请求数量
    private Integer count = 10;
    private static final long serialVersionUID = 1L;

}
