package com.xqoo.filemanager.service.aliyun;

import com.fasterxml.jackson.databind.JsonNode;
import com.xqoo.common.dto.SystemCommunicateDTO;

import java.util.Map;

public interface AliyunOssBaseService {

    /**
     * 获取阿里云配置参数
     * @return
     */
    Map<String, String> getAliyunOssConfig();

    /**
     * 获取上传文件的许可签名
     * @param accessKey
     * @param accessSecret
     * @param endpoint
     * @param dirPath
     * @param host
     * @param callbackUrl
     * @param expire
     * @return
     */
    SystemCommunicateDTO<JsonNode> getUploadFileSign(String accessKey, String accessSecret,
                                                     String endpoint, String dirPath, String host, String callbackUrl, long expire);

    /**
     * 判断存储空间是否存在
     * @param accessKey
     * @param accessSecret
     * @param endpoint
     * @param bucketName
     * @return
     */
    boolean existsBucketName(String accessKey, String accessSecret, String endpoint, String bucketName);
}
