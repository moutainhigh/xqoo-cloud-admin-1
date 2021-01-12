package com.xqoo.sms.bean;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.Map;


public class ALiSmsConfigBean {
    String accessKeyId;//用户名
    String accessKeySecret;//密钥
    String regionId;//服务地区id
    String sysDomain;//服务器名称
    String version;//服务器名称
    Map<String, String> errCodeMap;

    public ALiSmsConfigBean() {
    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getRegionId() {
        return regionId;
    }

    public void setRegionId(String regionId) {
        this.regionId = regionId;
    }

    public String getSysDomain() {
        return sysDomain;
    }

    public void setSysDomain(String sysDomain) {
        this.sysDomain = sysDomain;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getErrCodeMap() {
        return errCodeMap;
    }

    public void setErrCodeMap(Map<String, String> errCodeMap) {
        this.errCodeMap = errCodeMap;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ALiSmsConfigBean").append('[')
                .append("accessKeyId=")
                .append(accessKeyId)
                .append(",accessKeySecret=")
                .append(accessKeySecret)
                .append(",regionId=")
                .append(regionId)
                .append(",sysDomain=")
                .append(sysDomain)
                .append(",version=")
                .append(version)
                .append(']');
        return sb.toString();
    }
}