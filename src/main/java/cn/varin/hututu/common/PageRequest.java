package cn.varin.hututu.common;

import lombok.Data;

@Data
public class PageRequest {
    // 页号
    private int current = 1;
    // 页数
    private int pageSize = 10;
    // 排序字段
    private String sortField;
    // 降序/升序 默认：降序
    private String sortOrder ="desc";
}
