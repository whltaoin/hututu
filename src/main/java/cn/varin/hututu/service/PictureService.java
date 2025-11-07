package cn.varin.hututu.service;

import cn.varin.hututu.model.dto.picture.PictureQueryRequest;
import cn.varin.hututu.model.dto.picture.PictureReviewRequest;
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
     * @param object 可能是文件也有可能是字符串
     * @param pictureUploadRequest 图片请求类
     * @param loginUser 上传文件的用户
     * @return 图片VO
     */
     PictureVo uploadPictureSaveOrUpdate(Object object, PictureUploadRequest pictureUploadRequest, User loginUser) ;



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

    /**
     * 图片审核
     * @param pictureReviewRequest 审核状态请求包装类
     * @param LoginUser 审核用户
     * @return true为完成审核流程，
     */
    Boolean doPictureReview(PictureReviewRequest pictureReviewRequest,User LoginUser);
    /**
     * 判断是否是管理员，如果是管理员就自动过审
     * @param picture 需要审核的图片
     * @param loginUser 审核人
     */

    void fillterReviewPictureParams(Picture picture,User loginUser);



}

