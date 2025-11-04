package cn.varin.hututu.manager;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.varin.hututu.config.COSClientConfig;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.exception.ThrowUtil;
import cn.varin.hututu.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 文件上传业务
 */
@Service
@Slf4j
public class FileManager {

    @Resource
    private COSClientConfig cosClientConfig;
    @Resource
    private CosManager cosManager;

    /**
     * 验证图片
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
}
