package cn.varin.hututu.controller;

import cn.varin.hututu.common.BaseResponse;
import cn.varin.hututu.common.ResponseUtil;
import cn.varin.hututu.exception.ResponseCode;
import cn.varin.hututu.manager.CosManager;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Api(tags = "文件服务测试接口")
@RestController
@RequestMapping("/file")
public class FileController {

    @Resource
    private CosManager cosManager;
    @ApiOperation(value ="文件上传")
    @PostMapping("/test/upload")
    public BaseResponse<String> upload(@RequestPart("file") MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        String filename = String.format("/test/%s", originalFilename);
        File tempFile =null;
        try {
            tempFile=  File.createTempFile(filename, null);

            multipartFile.transferTo(tempFile); // 文件保存到内存中
            cosManager.putObject(filename, tempFile);
            return ResponseUtil.success(filename);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (tempFile != null) {
//                tempFile.delete();
            }
        }

    }

    @GetMapping("/test/downlaod")
    public void downlaod(  String filePath, HttpServletResponse response) {
        COSObject cosObject = cosManager.getObject(filePath);

        try(   COSObjectInputStream objectContent   = cosObject.getObjectContent()){

            byte[] byteArray = IOUtils.toByteArray(objectContent);
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filePath);
            response.getOutputStream().write(byteArray);
            response.getOutputStream().flush();

        }catch (Exception e){
            log.error("文件下载失败："+e.getMessage());
            throw new RuntimeException(e);

        }



    }



}
