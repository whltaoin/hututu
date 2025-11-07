package cn.varin.hututu.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.exception.ThrowUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Service
public class FilePictureUpload extends PictureUploadTemplate {

    @Override
    void validPicture(Object fileResource) {

        MultipartFile file =  (MultipartFile) fileResource;
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

    @Override
    String getFileName(Object fileResource) {
        MultipartFile file =  (MultipartFile) fileResource;

        return file.getOriginalFilename();
    }

    @Override
    void getTempFile(Object fileResource, File file) throws Exception {
        MultipartFile multipartFile = (MultipartFile) fileResource;
        multipartFile.transferTo(file);
    }
}