package cn.varin.hututu.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.varin.hututu.constant.UserConstant;
import cn.varin.hututu.exception.CustomizeException;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.exception.ThrowUtil;
import cn.varin.hututu.manager.CosManager;
import cn.varin.hututu.manager.FileManager;
import cn.varin.hututu.manager.upload.FilePictureUpload;
import cn.varin.hututu.manager.upload.PictureUploadTemplate;
import cn.varin.hututu.manager.upload.UrlPictureUpload;
import cn.varin.hututu.model.dto.picture.*;
import cn.varin.hututu.model.dto.file.UploadPictureResult;
import cn.varin.hututu.model.entity.Space;
import cn.varin.hututu.model.entity.User;
import cn.varin.hututu.model.enums.ReviewStatusEnum;
import cn.varin.hututu.model.vo.picture.PictureVo;
import cn.varin.hututu.model.vo.user.UserVo;
import cn.varin.hututu.service.SpaceService;
import cn.varin.hututu.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.varin.hututu.model.entity.Picture;
import cn.varin.hututu.service.PictureService;
import cn.varin.hututu.mapper.PictureMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.swing.undo.CannotUndoException;
import java.io.IOException;
import java.security.cert.CertStoreException;
import java.util.*;
import java.util.stream.Collectors;

/**
* @author varya
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-11-03 19:07:37
*/
@Slf4j
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
    @Resource
    private SpaceService spaceService;;

    @Override
    public PictureVo uploadPictureSaveOrUpdate(Object inputSource, PictureUploadRequest pictureUploadRequest, User loginUser) {

        //1. 校验信息
        ThrowUtil.throwIf(ObjectUtil.isEmpty(inputSource), ResponseCode.NOT_FOUND_ERROR);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(pictureUploadRequest), ResponseCode.NOT_FOUND_ERROR);
        ThrowUtil.throwIf(ObjectUtil.isEmpty(loginUser), ResponseCode.NOT_FOUND_ERROR);
        Long spaceId = pictureUploadRequest.getSpaceId();
        // 有空间的时候
        if(spaceId != null){

            Space space = spaceService.getById(spaceId);
            ThrowUtil.throwIf(space == null, ResponseCode.NOT_FOUND_ERROR.getCode(), "空间不存在");

            if (!loginUser.getId().equals(space.getUserId())) {
                throw new CustomizeException(ResponseCode.NO_AUTH_ERROR, "没有空间权限");
            }

        }
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
            // 判断修改的spaceid和之前的spaceid是否一致
            if (spaceId == null) {
                if (oldPicture.getSpaceId() != null) {
                    spaceId = oldPicture.getSpaceId();
                }
            } else {

                if (ObjUtil.notEqual(spaceId, oldPicture.getSpaceId())) {
                    throw new CustomizeException(ResponseCode.PARAMS_ERROR, "空间 id 不一致");
                }
            }

        }

        Space space = spaceService.getById(spaceId);
        ThrowUtil.throwIf(space == null, ResponseCode.NOT_FOUND_ERROR.getCode(), "空间不存在");

        if (!loginUser.getId().equals(space.getUserId())) {
            throw new CustomizeException(ResponseCode.NO_AUTH_ERROR, "没有空间权限");
        }

        if (space.getTotalCount() >= space.getMaxCount()) {
            throw new CustomizeException(ResponseCode.OPERATION_ERROR, "空间条数不足");
        }
        if (space.getTotalSize() >= space.getMaxSize()) {
            throw new CustomizeException(ResponseCode.OPERATION_ERROR, "空间大小不足");
        }





        // 2. 图片上传

        String uploadPathPrefix;
        if (spaceId == null) {
            uploadPathPrefix =  String.format("public/%s", loginUser.getId());
        } else {
            uploadPathPrefix = String.format("space/%s", spaceId);
        }


        PictureUploadTemplate pictureUploadTemplate = filePictureUpload;
        if (inputSource instanceof String) {
            pictureUploadTemplate = urlPictureUpload;
        }
        UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, uploadPathPrefix);


        // 3. 数据复制
        Picture picture = new Picture();
        BeanUtils.copyProperties(uploadPictureResult, picture);
        picture.setUserId(loginUser.getId());
        picture.setSpaceId(spaceId);

        // 批量生成前缀名称补充
        String picName =  uploadPictureResult.getPicName();
        if(pictureUploadRequest!=null && StringUtils.isNotBlank(pictureUploadRequest.getPicName())) {
            picName=pictureUploadRequest.getPicName();

        }
        picture.setName( picName);

       // 管理员自动审核
        this.fillterReviewPictureParams(picture, loginUser);

        if (picId !=null) {

            picture.setId(picId);
            picture.setEditTime(new Date());
        }
        // 4. 创建或更新
        Long finalSpaceId = spaceId;
        transactionTemplate.execute(status -> {
            boolean result = this.saveOrUpdate(picture);
            ThrowUtil.throwIf(!result, ResponseCode.OPERATION_ERROR.getCode(), "图片上传失败");
            if (finalSpaceId != null) {
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, finalSpaceId)
                        .setSql("totalSize = totalSize + " + picture.getPicSize())
                        .setSql("totalCount = totalCount + 1")
                        .update();
                ThrowUtil.throwIf(!update, ResponseCode.OPERATION_ERROR.getCode(), "额度更新失败");
            }
            return PictureVo.entityToVo(picture);
        });


        return PictureVo.entityToVo(picture);
    }

    @Resource
    private TransactionTemplate transactionTemplate;
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
        Long spaceId = pictureQueryRequest.getSpaceId();
        boolean nullSpaceId = pictureQueryRequest.isNullSpaceId();




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
                .eq(ObjUtil.isNotEmpty(spaceId), "spaceId", spaceId)
        .isNull(nullSpaceId, "spaceId");

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

    /**
     * https://cn.bing.com/images/search?q=%E7%8C%AB&first=1
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return
     */

    @Override
    public Integer pictureUploadByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser) {

        String searchText = pictureUploadByBatchRequest.getSearchText();
        Integer count = pictureUploadByBatchRequest.getCount();
        ThrowUtil.throwIf(count>=30, ResponseCode.PARAMS_ERROR.getCode(),"一次最多生成30张图片");
        String nameFrefix = pictureUploadByBatchRequest.getNameFrefix();
        if (StrUtil.isBlank(nameFrefix)) {
            nameFrefix = searchText;
        }
        String pageNumber = String.valueOf(new Random().nextInt(10000)+1);

       //  String basicUrl  = String.format("https://cn.bing.com/images/search?q=%s&first=%s",searchText, pageNumber);
        String basicUrl = String.format("https://cn.bing.com/images/async?q=%s&mmasync=1", searchText);
        Document document= null;
        try {
        document  = Jsoup.connect(basicUrl).get();

        } catch (IOException e) {
            log.error("获取网页失败：{}",e.getMessage());
            throw new RuntimeException(e);
        }
        Integer imageCount = 0;

        Elements imageList = document.getElementsByClass("dgControl").first().select("img.mimg");

        for(Element image : imageList) {
            String src = image.attr("src");
            if (StrUtil.isBlank(src)) {

                src = image.attr("data-src");
                if (StrUtil.isBlank(src)) {
                    log.info("当前链接为空，已跳过: {}", src);
                    continue;
                }


            }
       //      有链接，截取？号后的所有数据
            int i = src.indexOf("?");
            if (i > -1) {
                src = src.substring(0, i);
            }

            log.info("图片url：{}",src);

            // 上传
            PictureUploadRequest pictureUploadRequest = new PictureUploadRequest();
            if(StringUtils.isNotBlank(nameFrefix)){
                pictureUploadRequest.setPicName(nameFrefix + (imageCount + 1));
            }

            try {
                PictureVo pictureVo = this.uploadPictureSaveOrUpdate(src, pictureUploadRequest, loginUser);
                log.info("图片上传成功, id = {}", pictureVo.getId());
                imageCount++;
            }catch (Exception e){
                log.error("图片上传失败", e);
                continue;
            }
                if (imageCount >= count) {
                    break;

                }

        }

        return imageCount;
    }


    @Override
    public void checkPictureAuth(User loginUser, Picture picture) {
        Long spaceId = picture.getSpaceId();
        if (spaceId == null) {
            // 共同图库，只能管理员管理
            if (!picture.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
                throw new CustomizeException(ResponseCode.NO_AUTH_ERROR);
            }
        } else {
            // 判断图片是否是登录用户创建的
            if (!picture.getUserId().equals(loginUser.getId())) {
                throw new CustomizeException(ResponseCode.NO_AUTH_ERROR);
            }
        }
    }



    @Override
    public void deletePicture(long pictureId, User loginUser) {
        ThrowUtil.throwIf(pictureId <= 0, ResponseCode.PARAMS_ERROR);
        ThrowUtil.throwIf(loginUser == null, ResponseCode.NO_AUTH_ERROR);

        Picture oldPicture = this.getById(pictureId);
        ThrowUtil.throwIf(oldPicture == null, ResponseCode.NOT_FOUND_ERROR);

        checkPictureAuth(loginUser, oldPicture);

        transactionTemplate.execute(status -> {

            boolean result = this.removeById(pictureId);
            ThrowUtil.throwIf(!result, ResponseCode.OPERATION_ERROR);

            Long spaceId = oldPicture.getSpaceId();
            if (spaceId != null) {
                boolean update = spaceService.lambdaUpdate()
                        .eq(Space::getId, spaceId)
                        .setSql("totalSize = totalSize - " + oldPicture.getPicSize())
                        .setSql("totalCount = totalCount - 1")
                        .update();
                ThrowUtil.throwIf(!update, ResponseCode.OPERATION_ERROR.getCode(), "额度更新失败");
            }
            return true;
        });


        this.clearPictureFile(oldPicture);
    }

    @Resource
    private CosManager cosManager;

    // 异步删除图片
    @Async
    public void clearPictureFile(Picture oldPicture) {
        // 判断改图片是否被多条记录使用
        String pictureUrl = oldPicture.getUrl();
        long count = this.lambdaQuery()
                .eq(Picture::getUrl, pictureUrl)
                .count();
        // 有不止一条记录用到了该图片，不清理
        if (count > 1) {
            return;
        }
        // 删除图片
        cosManager.deleteObject(pictureUrl);
        // 删除缩略图
        String thumbnailUrl = oldPicture.getThumbnailUrl();
        if (StrUtil.isNotBlank(thumbnailUrl)) {
            cosManager.deleteObject(thumbnailUrl);
        }
    }
}




