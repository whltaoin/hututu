package cn.varin.hututu.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片信息更新
 */
@Data
public class PictureUpdateRequest implements Serializable {
  
      
    private Long id;  
  
      
    private String name;  
  
      
    private String introduction;  
  
      
    private String category;  
  
      
    private List<String> tags;
  
    private static final long serialVersionUID = 1L;  
}
