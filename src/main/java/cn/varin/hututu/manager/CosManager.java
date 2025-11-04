package cn.varin.hututu.manager;


import cn.varin.hututu.config.COSClientConfig;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.exception.CosServiceException;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.GetObjectRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.PicOperations;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;

@Component
public class CosManager {

    @Resource
    private COSClient cosClient;

    @Resource
    private COSClientConfig cosClientConfig;

    /**
     *
     * @param key  对象键(Key)是对象在存储桶中的唯一标识。
     * @param file 文件
     * @return
     * @throws CosClientException
     * @throws CosServiceException
     */
    public PutObjectResult putObject( String key, File file)
            throws CosClientException, CosServiceException{
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        // 数据万象：PicOperations 是腾讯云对象存储（COS）SDK 中用于配置图片处理参数的核心类
        PicOperations picOperations = new PicOperations();

        picOperations.setIsPicInfo(1); // 1返回原图，0 不返回
        putObjectRequest.setPicOperations(picOperations);
        return   cosClient.putObject(putObjectRequest);

    }

    /**
     *
     * @param key 文件路径
     * @return
     * @throws CosClientException
     * @throws CosServiceException
     */
    public COSObject getObject(String key)
            throws CosClientException, CosServiceException{
        GetObjectRequest getObjectRequest = new GetObjectRequest(cosClientConfig.getBucket(), key);
        return  cosClient.getObject(getObjectRequest);


    }

}
