package cn.varin.hututu.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import cn.varin.hututu.exception.CustomizeException;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.exception.ThrowUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.qcloud.cos.transfer.Upload;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Service

public class UrlPictureUpload extends PictureUploadTemplate {
    @Override
    void validPicture(Object fileResource) {
        String url = (String) fileResource;
        // 1. 图片为空
        ThrowUtil.throwIf(StringUtils.isEmpty(url), ResponseCode.OPERATION_ERROR.getCode(),"图片URL不能为空");
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            throw new CustomizeException(ResponseCode.PARAMS_ERROR, "文件地址格式不正确");
        }
        // 2. 判断图片的网络协议名称
        ThrowUtil.throwIf(!url.startsWith("http") || !url.startsWith("https"),
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

    @Override
    String getFileName(Object fileResource) {
        String url = (String) fileResource;
        return FileUtil.mainName(url)+".png";
    }

    @Override
    void getTempFile(Object fileResource, File file) throws Exception {
        String url = (String) fileResource;
        HttpUtil.downloadFile(url, file);

    }
}
