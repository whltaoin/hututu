package cn.varin.hututu.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import cn.varin.hututu.annotation.AutoCheckRole;
import cn.varin.hututu.common.BaseResponse;
import cn.varin.hututu.common.DeleteRequest;
import cn.varin.hututu.common.PageRequest;
import cn.varin.hututu.common.ResponseUtil;
import cn.varin.hututu.constant.UserConstant;
import cn.varin.hututu.exception.CustomizeException;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.exception.ThrowUtil;
import cn.varin.hututu.model.dto.picture.*;
import cn.varin.hututu.model.entity.Picture;
import cn.varin.hututu.model.entity.User;
import cn.varin.hututu.model.enums.ReviewStatusEnum;
import cn.varin.hututu.model.enums.UserRoleEnum;
import cn.varin.hututu.model.vo.picture.PictureVo;
import cn.varin.hututu.service.PictureService;
import cn.varin.hututu.service.UserService;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.squareup.okhttp.internal.framed.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

// @Api(tags = "图片模块接口")
@RestController
@RequestMapping("/picture")
public class PictureController {
    @Resource
    private PictureService pictureService;
    @Resource
    private UserService userService;

    /**
     * 只有登录用户才可以上传图片，并且管理员可以修改任何图片，但是普通用户只能修改自己上传的图片
     * @param multipartFile
     * @param pictureUploadRequest
     * @param httpServletRequest
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation(value ="上传图片")

    public BaseResponse<PictureVo> upload(@RequestPart("file") MultipartFile multipartFile, PictureUploadRequest pictureUploadRequest, HttpServletRequest httpServletRequest) {
        ThrowUtil.throwIf(ObjectUtil.isEmpty(httpServletRequest), ResponseCode.OPERATION_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(loginUser), ResponseCode.OPERATION_ERROR);

        PictureVo pictureVo = pictureService.uploadPictureSaveOrUpdate(multipartFile, pictureUploadRequest, loginUser);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(pictureVo), ResponseCode.OPERATION_ERROR);
        return ResponseUtil.success(pictureVo);


    }

    @PostMapping("/upload/url")
    @ApiOperation(value ="通过url链接上传图片")

    public BaseResponse<PictureVo> uploadByUrl( PictureUploadRequest pictureUploadRequest, HttpServletRequest httpServletRequest) {
        ThrowUtil.throwIf(ObjectUtil.isEmpty(httpServletRequest), ResponseCode.OPERATION_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(loginUser), ResponseCode.OPERATION_ERROR);
        String fileUrl = pictureUploadRequest.getFileUrl();
        ThrowUtil.throwIf(StringUtils.isBlank(fileUrl), ResponseCode.OPERATION_ERROR.getCode(),"图片路径不存在");

        PictureVo pictureVo = pictureService.uploadPictureSaveOrUpdate(fileUrl, pictureUploadRequest, loginUser);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(pictureVo), ResponseCode.OPERATION_ERROR);
        return ResponseUtil.success(pictureVo);


    }
    @ApiOperation(value ="删除图片")
    @DeleteMapping("/delete")
    public BaseResponse<Boolean> delete(@RequestBody DeleteRequest deleteRequest, HttpServletRequest httpServletRequest) {
        ThrowUtil.throwIf(ObjectUtil.isEmpty(deleteRequest)|| deleteRequest.getId()<=0, ResponseCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(loginUser), ResponseCode.NOT_LOGIN_ERROR);

        Picture oldPicture = pictureService.getById(deleteRequest.getId());
        ThrowUtil.throwIf(ObjectUtil.isEmpty(oldPicture), ResponseCode.NOT_FOUND_ERROR);

        ThrowUtil.throwIf(!oldPicture.getUserId().equals(loginUser.getId()) || !  userService.isAdmin(loginUser), ResponseCode.NO_AUTH_ERROR);

        boolean deleteStatus = pictureService.removeById(oldPicture.getId());

        ThrowUtil.throwIf(
              !  deleteStatus

                , ResponseCode.OPERATION_ERROR);
        return ResponseUtil.success(deleteStatus);


    }

    @ApiOperation(value ="更新图片")


    @PutMapping("/update")
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest,HttpServletRequest httpServletRequest) {
        if (pictureUpdateRequest == null || pictureUpdateRequest.getId() <= 0) {
            throw new CustomizeException(ResponseCode.PARAMS_ERROR);
        }

        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureUpdateRequest, picture);

        picture.setTags(JSONUtil.toJsonStr(pictureUpdateRequest.getTags()));

        pictureService.validPicture(picture);

        long id = pictureUpdateRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtil.throwIf(oldPicture == null, ResponseCode.NOT_FOUND_ERROR);
        // 管理员自动审核
        User loginUser = userService.getLoginUser(httpServletRequest);
        pictureService.fillterReviewPictureParams(oldPicture, loginUser);


        boolean result = pictureService.updateById(picture);
        ThrowUtil.throwIf(!result, ResponseCode.OPERATION_ERROR);
        return ResponseUtil.success(true);
    }
    @ApiOperation(value ="根据ID查询图片")
    @GetMapping("/get")
    @AutoCheckRole(roleValue = UserConstant.ADMIN_ROLE)
    public BaseResponse<Picture> getPictureById(long id, HttpServletRequest request) {
        ThrowUtil.throwIf(id <= 0, ResponseCode.PARAMS_ERROR);

        Picture picture = pictureService.getById(id);
        ThrowUtil.throwIf(picture == null, ResponseCode.NOT_FOUND_ERROR);

        return ResponseUtil.success(picture);
    }

    @ApiOperation(value ="根据ID查询图片VO")
    @GetMapping("/get/vo")
    @AutoCheckRole(roleValue = UserConstant.ADMIN_ROLE)
    public BaseResponse<PictureVo> getPictureVOById(long id, HttpServletRequest request) {
        ThrowUtil.throwIf(id <= 0, ResponseCode.PARAMS_ERROR);


        Picture picture = pictureService.getById(id);
        ThrowUtil.throwIf(picture == null, ResponseCode.NOT_FOUND_ERROR);
        PictureVo pictureVo = pictureService.getPictureVo(picture, request);

        return ResponseUtil.success(pictureVo);
    }
    @ApiOperation(value = "分页图片列表")
    @PostMapping("/list/page")
    public BaseResponse<Page<Picture>> listPictureByPage(@RequestBody PictureQueryRequest pictureQueryRequest) {
        int current = pictureQueryRequest.getCurrent();
        int pageSize = pictureQueryRequest.getPageSize();
        Page<Picture> picturePage = pictureService.page(new Page<>(current, pageSize)
                ,
                pictureService.getQueryWrapper(pictureQueryRequest));
    return ResponseUtil.success(picturePage);

    }

    @ApiOperation(value = "分页图片列表VO")
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVo>> listPictureVOByPage(@RequestBody PictureQueryRequest pictureQueryRequest,HttpServletRequest request) {
        int current = pictureQueryRequest.getCurrent();
        int pageSize = pictureQueryRequest.getPageSize();
        ThrowUtil.throwIf(pageSize > 20, ResponseCode.PARAMS_ERROR);

        // 只有审核过的图片可以在主页显示
        pictureQueryRequest.setReviewStatus(ReviewStatusEnum.PASS.getValue());
        Page<Picture> picturePage = pictureService.page(new Page<>(current, pageSize)
                ,
                pictureService.getQueryWrapper(pictureQueryRequest));
        Page<PictureVo> pictureVOPage = pictureService.getPictureVOPage(picturePage, request);
        return ResponseUtil.success(pictureVOPage);

    }

    /**
     * 编辑图片
     * @param pictureEditRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑图片")

    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest, HttpServletRequest request) {
        if (pictureEditRequest == null || pictureEditRequest.getId() <= 0) {
            throw new CustomizeException(ResponseCode.PARAMS_ERROR);
        }

        Picture picture = new Picture();
        BeanUtils.copyProperties(pictureEditRequest, picture);
        // picture 中的tags为字符串，
        picture.setTags(JSONUtil.toJsonStr(pictureEditRequest.getTags()));

        picture.setEditTime(new Date());

        pictureService.validPicture(picture);
        User loginUser = userService.getLoginUser(request);

        long id = pictureEditRequest.getId();
        Picture oldPicture = pictureService.getById(id);
        ThrowUtil.throwIf(oldPicture == null, ResponseCode.NOT_FOUND_ERROR);

        ThrowUtil.throwIf(!oldPicture.getUserId().equals(loginUser.getId()) || !  userService.isAdmin(loginUser), ResponseCode.NO_AUTH_ERROR);

        // 管理员自动审核
        pictureService.fillterReviewPictureParams(oldPicture, loginUser);

        boolean result = pictureService.updateById(picture);
        ThrowUtil.throwIf(!result, ResponseCode.OPERATION_ERROR);
        return ResponseUtil.success(true);
    }

    /**
     * 分类和图片标签列表内容
     * @return
     */
    @ApiOperation(value = "分类和图片标签列表")

    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory() {
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        List<String> tagList = Arrays.asList("热门", "搞笑", "生活", "高清", "艺术", "校园", "背景", "简历", "创意");
        List<String> categoryList = Arrays.asList("模板", "电商", "表情包", "素材", "海报");
        pictureTagCategory.setTagList(tagList);
        pictureTagCategory.setCategoryList(categoryList);
        return ResponseUtil.success(pictureTagCategory);
    }

    /**
     * 审核接口（管理员可用）
     * @param pictureReviewRequest
     * @param request
     * @return
     */
    @PostMapping("/review")
    @AutoCheckRole(roleValue = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> doPictureReview(@RequestBody PictureReviewRequest pictureReviewRequest, HttpServletRequest request) {
        ThrowUtil.throwIf(pictureReviewRequest == null, ResponseCode.PARAMS_ERROR);
        ThrowUtil.throwIf(request == null, ResponseCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        ThrowUtil.throwIf(request == null, ResponseCode.PARAMS_ERROR);
        Boolean pictureReviewStatus  = pictureService.doPictureReview(pictureReviewRequest, loginUser);
        return   ResponseUtil.success(pictureReviewStatus);


    }


}
