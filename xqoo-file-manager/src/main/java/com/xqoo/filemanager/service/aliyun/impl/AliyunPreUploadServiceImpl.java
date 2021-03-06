package com.xqoo.filemanager.service.aliyun.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ListObjectsV2Result;
import com.aliyun.oss.model.OSSObjectSummary;
import com.fasterxml.jackson.databind.JsonNode;
import com.xqoo.common.core.utils.JacksonUtils;
import com.xqoo.common.core.utils.MD5Util;
import com.xqoo.common.core.utils.StringUtils;
import com.xqoo.common.dto.SystemCommunicateDTO;
import com.xqoo.common.entity.ResultEntity;
import com.xqoo.common.enums.CommunicateStatusEnum;
import com.xqoo.filemanager.enums.UploadBucketTypeEnum;
import com.xqoo.filemanager.enums.UploadPlatEnum;
import com.xqoo.filemanager.service.FileRecordService;
import com.xqoo.filemanager.service.aliyun.AliyunPreUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author gaoyang
 */
@Service("aliyunPreUploadService")
public class AliyunPreUploadServiceImpl extends AliyunOssBaseServiceImpl implements AliyunPreUploadService {

    private final static Logger logger = LoggerFactory.getLogger(AliyunPreUploadServiceImpl.class);

    @Autowired
    private FileRecordService fileRecordService;

    @Override
    public ResultEntity<Map<String, String>> getUploadSign(String path, UploadBucketTypeEnum uploadBucketTypeEnum) {
        if(CollUtil.isEmpty(super.getAliyunOssConfig())){
            return new ResultEntity<>(HttpStatus.NOT_ACCEPTABLE, "未找到相应配置参数，生成签名失败");
        }
        String accessKey = super.getAliyunOssConfig().getOrDefault("accessKey", "");
        String accessSecret = super.getAliyunOssConfig().getOrDefault("accessSecret", "");
        String endpoint = super.getAliyunOssConfig().getOrDefault("endpoint", "");
        String bucket = UploadBucketTypeEnum.PUBLIC.equals(uploadBucketTypeEnum) ? super.getAliyunOssConfig().getOrDefault("bucketPublic", "")
                : super.getAliyunOssConfig().getOrDefault("bucketProtected", "");
        if(!super.existsBucketName(accessKey, accessSecret, endpoint, bucket)){
            return new ResultEntity<>(HttpStatus.NOT_ACCEPTABLE, "当前oss账户下没有可用的存储桶【" + bucket + "】");
        }
        String host = "http://" + bucket + "." + endpoint;
        // callbackUrl为 上传回调服务器的URL，请将下面的IP和Port配置为您自己的真实信息。
        String callbackUrl = super.getAliyunOssConfig().getOrDefault("callbackUrl", "");
        String dir = StringUtils.isNotEmpty(path) ? path : super.getAliyunOssConfig().getOrDefault("defaultPath", "");
        long expireTime = Long.parseLong(super.getAliyunOssConfig().getOrDefault("accessSignExpire", "30"));
        SystemCommunicateDTO<Map<String, String>> dto = super.getUploadFileSign(accessKey, accessSecret, endpoint, dir, host, callbackUrl, expireTime);

        if(CommunicateStatusEnum.SUCCESS.equals(dto.getStatus())){
            String fileId = MD5Util.MD5Encode(dto.getResult().getOrDefault("callback", "")
                    + System.currentTimeMillis(), StandardCharsets.UTF_8.name());
            boolean success = fileRecordService.addFilePreUploadRecord(fileId, UploadPlatEnum.ALI, uploadBucketTypeEnum);
            dto.getResult().put("fileId", fileId);
            if(!success){
                return new ResultEntity<>(HttpStatus.NOT_ACCEPTABLE, "获取签名失败，生成记录失败");
            }
            return new ResultEntity<>(HttpStatus.OK, "ok", dto.getResult());
        }
        return new ResultEntity<>(HttpStatus.NOT_ACCEPTABLE, dto.getMessage());
    }

    @Override
    public ResultEntity<JsonNode> showOssFileList() {
        if(CollUtil.isEmpty(super.getAliyunOssConfig())){
            return new ResultEntity<>(HttpStatus.NOT_ACCEPTABLE, "未找到相应配置参数，列举文件失败");
        }
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String accessKey = super.getAliyunOssConfig().getOrDefault("accessKey", "");
        String accessSecret = super.getAliyunOssConfig().getOrDefault("accessSecret", "");
        String endpoint = super.getAliyunOssConfig().getOrDefault("endpoint", "");
        String bucketName = super.getAliyunOssConfig().getOrDefault("bucketName", "");

        if(!super.existsBucketName(accessKey, accessSecret, endpoint, bucketName)){
            return new ResultEntity<>(HttpStatus.NOT_ACCEPTABLE, "当前oss账户下没有可用的存储桶【" + bucketName + "】");
        }
        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKey, accessSecret);
        // 列举文件。如果不设置KeyPrefix，则列举存储空间下的所有文件。如果设置KeyPrefix，则列举包含指定前缀的文件。
        ListObjectsV2Result result = ossClient.listObjectsV2(bucketName);
        List<OSSObjectSummary> ossObjectSummaries = result.getObjectSummaries();
        // 关闭OSSClient。
        ossClient.shutdown();
        return new ResultEntity<>(HttpStatus.OK, "ok", JacksonUtils.transferToJsonNode(ossObjectSummaries));
    }

    @Override
    public ResultEntity<String> getFileAccessSign(String pathFile, String process) {
        if(CollUtil.isEmpty(super.getAliyunOssConfig())){
            return new ResultEntity<>(HttpStatus.NOT_ACCEPTABLE, "未找到相应配置参数，生成访问签名失败");
        }
        String accessKey = super.getAliyunOssConfig().getOrDefault("accessKey", "");
        String accessSecret = super.getAliyunOssConfig().getOrDefault("accessSecret", "");
        String endpoint = super.getAliyunOssConfig().getOrDefault("endpoint", "");
        String bucketName = super.getAliyunOssConfig().getOrDefault("bucketProtected", "");
        String accessSignExpire = super.getAliyunOssConfig().getOrDefault("accessSignExpire", "30");
        if(!super.existsBucketName(accessKey, accessSecret, endpoint, bucketName)){
            return new ResultEntity<>(HttpStatus.NOT_ACCEPTABLE, "当前oss账户下没有可用的存储桶【" + bucketName + "】");
        }
        Date expireDate = DateUtil.offsetSecond(new Date(), Integer.parseInt(accessSignExpire));
        OSSClient client = new OSSClient(endpoint, new DefaultCredentialProvider(accessKey, accessSecret), null);
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, pathFile);
        request.setExpiration(expireDate);
        if(StringUtils.isNotEmpty(process)){
            request.setProcess(process);
        }
        URL url = client.generatePresignedUrl(request);
        return new ResultEntity<>(HttpStatus.OK, "ok", url.toString());
    }

}
