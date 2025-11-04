package cn.varin.hututu.service;

import cn.varin.hututu.model.dto.picture.PictureQueryRequest;
import cn.varin.hututu.model.dto.picture.PictureUploadRequest;
import cn.varin.hututu.model.entity.Picture;
import cn.varin.hututu.model.entity.User;
import cn.varin.hututu.model.vo.picture.PictureVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author varya
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-11-03 19:07:37
*/
public interface PictureService extends IService<Picture> {
    /**
     * 图片信息创建上传或更新
     * @param multipartFile 文件
     * @param pictureUploadRequest 图片请求类
     * @param loginUser 上传文件的用户
     * @return 图片VO
     */
     PictureVo uploadPictureSaveOrUpdate(MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, User loginUser) ;



    /**
     * 构造一个公共的picture查询对象
     * @param pictureQueryRequest
     * @return
     */
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 转换类：实体转VO
     * @param picture
     * @param request
     * @return
     */
    PictureVo getPictureVo(Picture picture, HttpServletRequest request);

    /**
     * 转换类：分页Picture转PictureVO
     * @param page
     * @param request
     * @return
     */
    Page<PictureVo> getPictureVOPage(Page<Picture>  page, HttpServletRequest request);

    /**
     * 图片校验
     * @param picture
     */
    public void validPicture(Picture picture) ;

    }
