package com.xqoo.filemanager.service.aliyun;

import com.fasterxml.jackson.databind.JsonNode;
import com.xqoo.common.entity.ResultEntity;
import com.xqoo.filemanager.enums.UploadBucketTypeEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author gaoyang
 */
public interface AliyunPreUploadService extends AliyunOssBaseService{

    /**
     * 获取阿里云oss上传签名
     * @param path
     * @param uploadBucketTypeEnum
     * @return
     */
    ResultEntity<JsonNode> getUploadSign(String path, UploadBucketTypeEnum uploadBucketTypeEnum);

    /**
     * 获取阿里云oss文件列表
     * @return
     */
    ResultEntity<JsonNode> showOssFileList();

    /**
     * 获取文件访问签名
     * @param pathFile 文件对象名(路径+文件名)
     * @param process (文件处理参数，例如图片的缩放参数 image/resize,p_30 表示图片缩放至30%，其他参数查看阿里云)
     * @return
     */
    ResultEntity<String> getFileAccessSign(String pathFile, String process);

    /**
     * 处理上传回调
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    boolean handleUploadCallback(HttpServletRequest request, HttpServletResponse response) throws IOException;
}