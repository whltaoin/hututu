package cn.varin.hututu.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/*8
图片修改
 */
@Data
public class PictureEditRequest implements Serializable {
  
      
    private Long id;  
  
      
    private String name;  
  
      
    private String introduction;  
  
      
    private String category;  
  
      
    private List<String> tags;
  
    private static final long serialVersionUID = 1L;  
}