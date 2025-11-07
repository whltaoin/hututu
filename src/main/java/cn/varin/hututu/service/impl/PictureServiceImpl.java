package cn.varin.hututu.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.varin.hututu.constant.UserConstant;
import cn.varin.hututu.exception.CustomizeException;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.exception.ThrowUtil;
import cn.varin.hututu.manager.FileManager;
import cn.varin.hututu.manager.upload.FilePictureUpload;
import cn.varin.hututu.manager.upload.PictureUploadTemplate;
import cn.varin.hututu.manager.upload.UrlPictureUpload;
import cn.varin.hututu.model.dto.picture.PictureEditRequest;
import cn.varin.hututu.model.dto.picture.PictureQueryRequest;
import cn.varin.hututu.model.dto.picture.PictureReviewRequest;
import cn.varin.hututu.model.dto.picture.PictureUploadRequest;
import cn.varin.hututu.model.dto.file.UploadPictureResult;
import cn.varin.hututu.model.entity.User;
import cn.varin.hututu.model.enums.ReviewStatusEnum;
import cn.varin.hututu.model.vo.picture.PictureVo;
import cn.varin.hututu.model.vo.user.UserVo;
import cn.varin.hututu.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.varin.hututu.model.entity.Picture;
import cn.varin.hututu.service.PictureService;
import cn.varin.hututu.mapper.PictureMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.swing.undo.CannotUndoException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author varya
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-11-03 19:07:37
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{
//
//    @Resource
//    private FileManager fileManager;
    @Autowired
    private UserService userService;
    @Resource
    private FilePictureUpload filePictureUpload;

    @Resource
    private UrlPictureUpload urlPictureUpload;


    @Override
    public PictureVo uploadPictureSaveOrUpdate(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {

        //1. 校验信息
        ThrowUtil.throwIf(ObjectUtil.isEmpty(inputSource), ResponseCode.NOT_FOUND_ERROR);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(pictureUploadRequest), ResponseCode.NOT_FOUND_ERROR);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(loginUser), ResponseCode.NOT_FOUND_ERROR);
        Long picId= pictureUploadRequest.getId();

        if (picId!=null) {

            // 图片id存储，查询下是否真实存储
            boolean exists = this.lambdaQuery().eq(Picture::getId, picId).exists();
            ThrowUtil.throwIf(!exists,ResponseCode.NOT_FOUND_ERROR.getCode(),"请求图片不存在");
            // 判断是否用权限修改
            Picture oldPicture = this.getById(picId);
            ThrowUtil.throwIf(
                    oldPicture.getUserId().equals(loginUser.getId()) &&
                            userService.isAdmin(loginUser),
                    new CustomizeException(ResponseCode.NO_AUTH_ERROR)
            );

        }





        // 2. 图片上传

        String uploadPathPrefix = String.format("public/%s", loginUser.getId());

        PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
        if (inputSource instanceof String) {
            pictureUploadTemplate = urlPictureUpload;
        }
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);


        // 3. 数据复制
        Picture picture = new Picture();
        BeanUtils.copyProperties(uploadPictureResult, picture);
        picture.setName(uploadPictureResult.getPicName());
        picture.setUserId(loginUser.getId());
       // 管理员自动审核
        this.fillterReviewPictureParams(picture, loginUser);

        if (picId !=null) {

            picture.setId(picId);
            picture.setEditTime(new Date());
        }
        // 4. 创建或更新
        boolean saveOrUpdateStatus = this.saveOrUpdate(picture);
        ThrowUtil.throwIf(!saveOrUpdateStatus,ResponseCode.OPERATION_ERROR.getCode(),"图片上传失败");




        return PictureVo.entityToVo(picture);
    }

    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest) {
        //1. 校验
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        if (ObjectUtil.isNull(pictureQueryRequest)) {
            return queryWrapper;

        }
        // 2. 获取pictureQueryRequest参数
        Long id = pictureQueryRequest.getId();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        String[] tags = pictureQueryRequest.getTags();
        Long picSize = pictureQueryRequest.getPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        String searchText = pictureQueryRequest.getSearchText();
        Long userId = pictureQueryRequest.getUserId();
        String sortField = pictureQueryRequest.getSortField();
        String sortOrder = pictureQueryRequest.getSortOrder();
        Integer reviewStatus = pictureQueryRequest.getReviewStatus();
        Long reviewerId = pictureQueryRequest.getReviewerId();
        String reviewMessage = pictureQueryRequest.getReviewMessage();


        // 3. 名称和简介一起一起模糊查询：searchText 支持同时从 name 和 introduction 中检索

        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(query->query.like("name",searchText))
                    .or()
                    .like("introduction",searchText);

        }
        // 4. 查询条件
        queryWrapper.eq(ObjectUtil.isNotEmpty(id),"id", id)
                .eq(ObjectUtil.isNotEmpty(userId),"userId", userId)
        .like(StrUtil.isNotBlank(name), "name", name)
        .like(StrUtil.isNotBlank(introduction), "introduction", introduction)
        .like(StrUtil.isNotBlank(picFormat), "picFormat", picFormat)
        .like(StrUtil.isNotBlank(reviewMessage), "reviewMessage", reviewMessage)
        .eq(StrUtil.isNotBlank(category), "category", category)
        .eq(ObjUtil.isNotEmpty(picWidth), "picWidth", picWidth)
        .eq(ObjUtil.isNotEmpty(picHeight), "picHeight", picHeight)
        .eq(ObjUtil.isNotEmpty(picSize), "picSize", picSize)
        .eq(ObjUtil.isNotEmpty(picScale), "picScale", picScale)
        .eq(ObjUtil.isNotEmpty(reviewerId), "reviewerId", reviewerId)
        .eq(ObjUtil.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus)
                ;


        if (tags != null && tags.length > 0) {
            for (String tag : tags) {
                queryWrapper.like("tags",tag);
            }


        }
        // 排序
        queryWrapper.orderBy(StrUtil.isNotEmpty(sortField), sortOrder.equals("ascend"), sortField);


        return queryWrapper;
    }

    @Override
    public PictureVo getPictureVo(Picture picture, HttpServletRequest request) {

        ThrowUtil.throwIf(ObjectUtil.isEmpty(picture),ResponseCode.PARAMS_ERROR);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(request),ResponseCode.PARAMS_ERROR);
        PictureVo pictureVo = PictureVo.entityToVo(picture);
        if (pictureVo.getUserId()!=null ||pictureVo.getUserId()>0) {
            User loginUser = userService.getLoginUser(request);
            UserVo userVo = userService.getUserVo(loginUser);
            pictureVo.setUser(userVo);
        }

        return pictureVo;
    }

    @Override
    public Page<PictureVo> getPictureVOPage(Page<Picture> page, HttpServletRequest request) {

        // 1获取到picture列表
        List<Picture> pictureList = page.getRecords();
        // 2构造 Page<PictureVo>
        Page<PictureVo> pictureVoPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        // 3 如果picture列表为空直接返回
        if (ObjectUtil.isNull(pictureList) || ObjectUtil.isEmpty(pictureVoPage) || pictureList.size()==0) {
            return pictureVoPage;
        }
        // 以下步骤是为了一次获取到所有的用户，并且将用户放置到对应的pictureVo中
        // 4. 将picture列表中的picture转pictureVo
        List<PictureVo> pictureVoList = pictureList.stream().map(PictureVo::entityToVo).collect(Collectors.toList());
        // 获取到pictureVoList所有的userId
        Set<Long> userIdSet = pictureVoList.stream().map(PictureVo::getUserId).collect(Collectors.toSet());
        List<UserVo> userVoList = userService.listByIds(userIdSet).stream().map(userService::getUserVo).collect(Collectors.toList());
        Map<Long, List<UserVo>> userVoMap = userVoList.stream().collect(Collectors.groupingBy(UserVo::getId));
        // for循环，根据id查询用户并插入
        pictureVoList.forEach(pictureVo -> {
            Long userId = pictureVo.getUserId();
            UserVo userVo = null;
            if (userVoMap.containsKey(userId)) {
                 userVo = userVoMap.get(userId).get(0);

            }
            pictureVo.setUser(userVo);

        });
        pictureVoPage.setRecords(pictureVoList);


        return pictureVoPage;
    }

    @Override
    public void validPicture(Picture picture) {
        ThrowUtil.throwIf(picture == null, ResponseCode.PARAMS_ERROR);

        Long id = picture.getId();
        String url = picture.getUrl();
        String introduction = picture.getIntroduction();

        ThrowUtil.throwIf(ObjUtil.isNull(id), ResponseCode.PARAMS_ERROR.getCode(), "id 不能为空");
        if (StrUtil.isNotBlank(url)) {
            ThrowUtil.throwIf(url.length() > 1024, ResponseCode.PARAMS_ERROR.getCode(), "url 过长");
        }
        if (StrUtil.isNotBlank(introduction)) {
            ThrowUtil.throwIf(introduction.length() > 800, ResponseCode.PARAMS_ERROR.getCode(), "简介过长");
        }
    }

    @Override
    public Boolean doPictureReview(PictureReviewRequest pictureReviewRequest, User LoginUser) {
        // 1.审核状态请求类参数验证
        Integer reviewStatus = pictureReviewRequest.getReviewStatus();
        String reviewMessage = pictureReviewRequest.getReviewMessage();
        Long id = pictureReviewRequest.getId();
        ReviewStatusEnum reviewEnum = ReviewStatusEnum.getReviewEnum(reviewStatus);
        ThrowUtil.throwIf(

                id ==null || StringUtils.isBlank(reviewMessage)||
                        reviewStatus==null
                || ReviewStatusEnum.REVIEWING.equals(reviewEnum),new CustomizeException(ResponseCode.PARAMS_ERROR)
        );

        // 2. 图片是否存在
        Picture oldPicture = this.getById(id);
        ThrowUtil.throwIf(ObjectUtil.isNull(oldPicture), ResponseCode.PARAMS_ERROR)
                ;
        ThrowUtil.throwIf(oldPicture.getReviewStatus()==reviewStatus, new  CustomizeException(ResponseCode.PARAMS_ERROR.getCode(),"请勿重复审核"));


        // 3.写入数据库
        Picture picture = new Picture();
        BeanUtils.copyProperties(oldPicture,picture);
        picture.setReviewStatus(reviewStatus);
        picture.setReviewTime(new Date());
        picture.setReviewMessage(reviewMessage);
        picture.setReviewerId(LoginUser.getId());
        boolean reviewResult = this.saveOrUpdate(picture);
        ThrowUtil.throwIf(!reviewResult,ResponseCode.PARAMS_ERROR);
        return true;



    }

    @Override
    public void fillterReviewPictureParams(Picture picture, User loginUser) {
        //1. 判断用户角色
        if (userService.isAdmin(loginUser)) {
            // 2. 填写自动过审需要的参数

            picture.setReviewStatus(ReviewStatusEnum.PASS.getValue());
            picture.setReviewTime(new Date());
            picture.setReviewerId(loginUser.getId());
            picture.setReviewMessage("管理员"+loginUser.getUserName()+"审核通过！");
        }else{
            picture.setReviewStatus(ReviewStatusEnum.REVIEWING.getValue());
        }

    }
}




