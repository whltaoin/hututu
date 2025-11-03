package cn.varin.hututu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.varin.hututu.model.entity.Picture;
import cn.varin.hututu.service.PictureService;
import cn.varin.hututu.mapper.PictureMapper;
import org.springframework.stereotype.Service;

/**
* @author varya
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-11-03 19:07:37
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

}




