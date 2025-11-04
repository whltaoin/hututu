package cn.varin.hututu.model.dto.picture;

import lombok.Data;

import java.util.List;

/**
 * 显示分类和标签内容
 */
@Data
public class PictureTagCategory {
    private List<String> tagList;
    private List<String> categoryList;

}
