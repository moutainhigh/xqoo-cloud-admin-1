package com.xqoo.paycenter.abstracts;

import com.xqoo.paycenter.bean.WxPayPropertiesBean;
import com.xqoo.paycenter.dto.FinalUnifiedOrderResponse;
import com.xqoo.paycenter.dto.PaymentNotification;
import com.xqoo.paycenter.dto.WechatAppPayProtocolHandler;
import com.xqoo.paycenter.exception.MalformedPduException;
import com.xqoo.paycenter.exception.WechatAppPayServiceException;

public abstract class WxAbsPayService {


    // 仿真测试环境
    protected static final String UNIFIED_ORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
    protected static final String ORDER_QUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";

    /**
     *
     * @param wxpayConfig
     * @param body
     * @param amount
     * @param payTransactionId
     * @param type
     * @return
     * @throws WechatAppPayServiceException
     */
    public abstract FinalUnifiedOrderResponse createOrder(WxPayPropertiesBean wxpayConfig,
                                                          String body,
                                                          Double amount,
                                                          String payTransactionId,
                                                          String type) throws WechatAppPayServiceException;

    /**
     * 将收到的异步通知xml转成Java对象以供业务处理
     *
     * @param xml
     * @return
     * @throws MalformedPduException
     */
    public static PaymentNotification parsePaymentNotificationXml(String xml, String key) throws MalformedPduException {
        return WechatAppPayProtocolHandler.unmarshalFromXmlAndValidateSignature(xml,
                PaymentNotification.class, key);
    }
}
