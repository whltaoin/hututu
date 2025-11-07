package cn.varin.hututu.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.*;
import cn.varin.hututu.config.COSClientConfig;
import cn.varin.hututu.exception.CustomizeException;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.exception.ThrowUtil;
import cn.varin.hututu.model.dto.file.UploadPictureResult;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.First;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 文件上传业务
 */
@Deprecated //  过期方法

@Slf4j

public class FileManager {

    @Resource
    private COSClientConfig cosClientConfig;
    @Resource
    private CosManager cosManager;

    /**
     * 验证图片 （文件）
     */

    public void validPicture(MultipartFile file) {
        // 1. 图片为空
        ThrowUtil.throwIf(ObjectUtil.isEmpty(file), ResponseCode.OPERATION_ERROR.getCode(),"上传文件不能为空");
        // 2. 图片限制在2M内
        long size = file.getSize();
       final long one_m = 1024 * 1024;
       ThrowUtil.throwIf(size> 2*one_m,ResponseCode.OPERATION_ERROR.getCode(),"文件大小不能超过2M");
        // 3. 限制图片的后缀名
        String suffix = FileUtil.getSuffix(file.getOriginalFilename());

        List<String> pictureTypeList = Arrays.asList("jpg", "jpeg", "png", "webp");
        ThrowUtil.throwIf(!pictureTypeList.contains(suffix),ResponseCode.OPERATION_ERROR.getCode(),"文件格式错误");

    }
    /**
     * 验证图片 （文件）
     */

    public void validPicture(String url) {
        // 1. 图片为空
        ThrowUtil.throwIf(StringUtils.isEmpty(url), ResponseCode.OPERATION_ERROR.getCode(),"图片URL不能为空");
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new CustomizeException(ResponseCode.PARAMS_ERROR, "文件地址格式不正确");
        }
        // 2. 判断图片的网络协议名称
        ThrowUtil.throwIf(!url.startsWith("http://") || !url.startsWith("https://"),
                ResponseCode.OPERATION_ERROR.getCode(),"URL仅仅支持HTTP或HTTPS协议"
                );
        //尝试构建http中的head请求，来验证url中的内容

        try(HttpResponse httpResponse = HttpUtil.createRequest(Method.HEAD, url).execute()) {
            int httpResponseStatus = httpResponse.getStatus();
            if (httpResponseStatus != HttpStatus.HTTP_OK) {
                // 不支持head请求，直接返回
                return;
            }
            // 判断文件类型
            String header = httpResponse.header("Content-Type");
            if (StrUtil.isNotBlank(header)) {

                final List<String> ALLOW_CONTENT_TYPES = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/webp");
                ThrowUtil.throwIf(!ALLOW_CONTENT_TYPES.contains(header.toLowerCase()),
                        ResponseCode.PARAMS_ERROR.getCode(), "文件类型错误");
            }
            // 判断文件大小
            String length = httpResponse.header("Content-Length");
            try {
                long fileSize = Long.parseLong(length);

                final long one_m = 1024 * 1024;
                ThrowUtil.throwIf(fileSize> 2*one_m,ResponseCode.OPERATION_ERROR.getCode(),"文件大小不能超过2M");
                // 3. 限制图片的后缀名
            }catch (Exception e) {
               throw  new CustomizeException(ResponseCode.SYSTEM_ERROR,e.getMessage());

            }






        }catch (Exception e){
            new CustomizeException(ResponseCode.SYSTEM_ERROR,e.getMessage());

        }

    }

    /**
     * 清除文件
     *
     */
    public void deleteFile(File file) {
        if (file==null) {

            return;
        }
        boolean delete = file.delete();
        if (!delete) {
            log.error("file delete error :{}",file.getAbsolutePath());
        }
    }


    /**
     * 上传图片并且返回图片属性信息
     * @param multipartFile  文件
     * @param uploadPicturePathPrefix 文件上传路径前缀
     * @return 成功返回的图片信息
     */
    public UploadPictureResult uploadPicture(MultipartFile multipartFile,String uploadPicturePathPrefix) {
        // 1. 验证图片
        validPicture(multipartFile);
        // 2. 拼接图片路径：16位UUID_当前日期.图片后缀
        String uuid = RandomUtil.randomNumbers(16);
        String originalFilename = multipartFile.getOriginalFilename();
        String suffix = FileUtil.getSuffix(originalFilename);
        String nowDate = DateUtil.formatDate(new Date());
        String uploadFileName = String.format("%s_%s.%s", uuid, nowDate, suffix);
        String uploadPath = String.format("/%s/%s", uploadPicturePathPrefix, uploadFileName);



        File tempFile = null;
        try {
            // 3. 上传图片
            tempFile = File.createTempFile(uploadPath, null);

            multipartFile.transferTo(tempFile); // 文件保存到内存中
            PutObjectResult putObjectResult = cosManager.putObject(uploadPath, tempFile);
            // 文章参考：https://cloud.tencent.com/document/product/436/55377#.E5.AF.B9.E4.BA.91.E4.B8.8A.E6.95.B0.E6.8D.AE.E8.BF.9B.E8.A1.8C.E5.9B.BE.E7.89.87.E5.A4.84.E7.90.86
            // 4. 存储图片信息
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();

            int height = imageInfo.getHeight();
            int width = imageInfo.getWidth();
            Double picScale = NumberUtil.round(height*1.0/width,2).doubleValue();


            UploadPictureResult result = new UploadPictureResult();
            result.setPicName(FileUtil.mainName(originalFilename));
            result.setPicHeight(height);
            result.setPicWidth(width);
            result.setPicSize(FileUtil.size(tempFile));
            result.setPicScale(picScale);
            result.setPicFormat(imageInfo.getFormat());
            result.setUrl(cosClientConfig.getHost()+"/"+uploadPath);

            // 5. 返回
            return result;


        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            this.deleteFile(tempFile);
        }

    }

    /**
     *  通过url上传图片
     */
    public UploadPictureResult uploadPictureByURl(String url,String uploadPicturePathPrefix) {
        // 校验
        validPicture(url);
        String uuid = RandomUtil.randomNumbers(16);
        String originalFilename =FileUtil.mainName(url);
        String suffix = FileUtil.getSuffix(originalFilename);
        String nowDate = DateUtil.formatDate(new Date());
        String uploadFileName = String.format("%s_%s.%s", uuid, nowDate, suffix);
        String uploadPath = String.format("/%s/%s", uploadPicturePathPrefix, uploadFileName);

        File file = null;
        try {
           file =  File.createTempFile(uploadPath ,null);

           HttpUtil.downloadFile(url, file);

            PutObjectResult putObjectResult = cosManager.putObject(uploadPath, file);
            // 文章参考：https://cloud.tencent.com/document/product/436/55377#.E5.AF.B9.E4.BA.91.E4.B8.8A.E6.95.B0.E6.8D.AE.E8.BF.9B.E8.A1.8C.E5.9B.BE.E7.89.87.E5.A4.84.E7.90.86
            // 4. 存储图片信息
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();

            int height = imageInfo.getHeight();
            int width = imageInfo.getWidth();
            Double picScale = NumberUtil.round(height*1.0/width,2).doubleValue();


            UploadPictureResult result = new UploadPictureResult();
            result.setPicName(FileUtil.mainName(originalFilename));
            result.setPicHeight(height);
            result.setPicWidth(width);
            result.setPicSize(FileUtil.size(file));
            result.setPicScale(picScale);
            result.setPicFormat(imageInfo.getFormat());
            result.setUrl(cosClientConfig.getHost()+"/"+uploadPath);

            // 5. 返回
            return result;

        }catch (Exception e){
            log.error("图片上传到对象存储失败", e);
            throw new CustomizeException(ResponseCode.SYSTEM_ERROR, "上传失败");
        }finally {
            this.deleteFile(file);
        }


    }


    }
