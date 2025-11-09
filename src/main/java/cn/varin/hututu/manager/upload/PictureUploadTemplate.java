package cn.varin.hututu.manager.upload;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import cn.varin.hututu.config.COSClientConfig;
import cn.varin.hututu.manager.CosManager;
import cn.varin.hututu.model.dto.file.UploadPictureResult;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * 文件上传的模版方式：分为了：使用文件上传和使用url上传
 *
 * 公共流程：
 * 校验文件
 *
 * 获取上传地址
 *
 * 获取本地临时文件
 *
 * 上传到对象存储
 *
 * 封装解析得到的图片信息
 *
 * 清理临时文件
 *
 * 需要单独处理的：
 *
 * 校验图片
 *
 * 获取文件名称
 *
 * 保存临时文件
 */


@Slf4j
public abstract class PictureUploadTemplate {


    @Resource
    private COSClientConfig cosClientConfig;
    @Resource
    private CosManager cosManager;
    // 验证图片
    abstract  void validPicture(Object fileResource);

    // 获取文件名称
    abstract  String getFileName(Object fileResource);
    // 保存临时文件
    abstract  void getTempFile(Object fileResource ,File file) throws Exception ;




    /**
     * 上传图片并且返回图片属性信息
     * @param fileResource  文件
     * @param uploadPicturePathPrefix 文件上传路径前缀
     * @return 成功返回的图片信息
     */
    public UploadPictureResult uploadPicture(Object fileResource, String uploadPicturePathPrefix) {
        // 1. 验证图片
        validPicture(fileResource);
        // 2. 拼接图片路径：16位UUID_当前日期.图片后缀



        String uuid = RandomUtil.randomString(16);
        String originFilename = getFileName(fileResource);

        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid,
                FileUtil.getSuffix(originFilename));
        String uploadPath = String.format("/%s/%s", uploadPicturePathPrefix, uploadFilename);
        //todo 为什么这样就会产生一样的图片？？？？？
        // String uploadPath = String.format("/%s/%s", uploadPicturePathPrefix, originFilename);



        File tempFile = null;
        try {
            // 3. 上传图片
            tempFile = File.createTempFile(uploadPath, null);
            getTempFile(fileResource,tempFile);
            // multipartFile.transferTo(tempFile); // 文件保存到内存中
            PutObjectResult putObjectResult = cosManager.putObject(uploadPath, tempFile);
            // 文章参考：https://cloud.tencent.com/document/product/436/55377#.E5.AF.B9.E4.BA.91.E4.B8.8A.E6.95.B0.E6.8D.AE.E8.BF.9B.E8.A1.8C.E5.9B.BE.E7.89.87.E5.A4.84.E7.90.86
            // 4. 存储图片信息
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();


            UploadPictureResult result = buildResult(uploadPath,tempFile,uploadPath,imageInfo);

            // 5. 返回
            return result;


        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            this.deleteFile(tempFile);
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

    private UploadPictureResult buildResult(String originFilename, File file, String uploadPath, ImageInfo imageInfo) {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        uploadPictureResult.setPicSize(FileUtil.size(file));
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        return uploadPictureResult;
    }

}
