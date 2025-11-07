package cn.varin.hututu.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 图片上传请求
 * 由于图片需要支持重复上传（基础信息不变，只改变图片文件），所以要添加图片 id 参数
 */

@Data
public class PictureUploadRequest implements Serializable {
  
      
    private Long id;  
    private String fileUrl;

    private static final long serialVersionUID = 1L;  
}