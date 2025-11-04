package cn.varin.hututu.model.dto.file;

import lombok.Data;

/**
 * 数据万象，上传图片解析结果包装类
 */
@Data
public class UploadPictureResult {

    /**
     * 图片 url
     */
    private String url;

    /**
     * 图片名称
     */
    private String picName;




    /**
     * 图片体积
     */
    private Long picSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

}
