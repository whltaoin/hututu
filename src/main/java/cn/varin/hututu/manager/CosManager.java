package cn.varin.hututu.manager;


import cn.hutool.core.io.FileUtil;
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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

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

        // 参考链接：https://cloud.tencent.com/document/product/436/55377#.E4.B8.8A.E4.BC.A0.E6.97.B6.E5.9B.BE.E7.89.87.E6.8C.81.E4.B9.85.E5.8C.96.E5.A4.84.E7.90.86
        //将原图转为webp格式（将图片转为webp格式，其实就是对图片进行压缩了。）
        // 添加图片处理规则
        List<PicOperations.Rule> ruleList = new LinkedList<>();
        // 转换格式压缩规则
        PicOperations.Rule rule1 = new PicOperations.Rule();
        rule1.setBucket(cosClientConfig.getBucket());
        rule1.setFileId(FileUtil.mainName(key)+".webp");// 测试图片名称
        rule1.setRule("imageMogr2/format/webp");  // 规则
        ruleList.add(rule1);
        picOperations.setRules(ruleList);

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
