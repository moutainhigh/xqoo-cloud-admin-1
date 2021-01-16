package com.xqoo.paycenter.rocketmq.service;

import com.xqoo.paycenter.bean.AliPayPropertiesBean;
import com.xqoo.paycenter.bean.WxPayPropertiesBean;
import com.xqoo.paycenter.rocketmq.constant.MessageOrderConstant;
import com.xqoo.paycenter.service.PayConfigPropertiesService;
import com.xqoo.rocket.config.RocketmqConfig;
import com.xqoo.rocket.constant.MqTagsConstant;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 消息消费监听器
 * @author GaoYang
 * @since 2020-03-14 17:31:25
 */
@Service
public class ThirdPartyPayConsumerService {

    private final static Logger logger = LoggerFactory.getLogger(ThirdPartyPayConsumerService.class);

    @Autowired
    private RocketmqConfig rocketmqConfig;

    @Autowired
    @Qualifier("AliPayConfig")
    private AliPayPropertiesBean aliPayPropertiesBean;

    @Autowired
    @Qualifier("WxPayConfig")
    private WxPayPropertiesBean wxPayPropertiesBean;

    @Autowired
    private PayConfigPropertiesService payConfigPropertiesService;

    /**
     * 支付配置更新监听器
     */
    public void PayConfigRefreshListener(){
        //消费者的组名
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(rocketmqConfig.getConsumer().getPushConsumer());

        //指定NameServer地址，多个地址以 ; 隔开
        consumer.setNamesrvAddr(rocketmqConfig.getNamesrvAddr());
        try {
            //订阅支付模块相关付款配置刷新
            consumer.subscribe(rocketmqConfig.getTopic(), MqTagsConstant.PayModuleTopicTags.CONFIG_REFRESH);

            // 广播模式消费，所有节点均执行，默认为集群模式
            consumer.setMessageModel(MessageModel.BROADCASTING);
            //设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
            /*如果非第一次启动，那么按照上次消费的位置继续消费，此参数广播模式下无效，集群模式如若多台那滞留消费理应被其他服务器
            * 消耗，也无太大用处，不过为防止数据处理丢失集群模式还是开着吧
            * */
//            consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
            consumer.registerMessageListener((MessageListenerConcurrently) (list, context) -> {
                MessageExt messageExt = list.get(0);
                try {
                    String messageBody = new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET);
                    switch(messageBody){
                        case MessageOrderConstant
                                .PayConfigRefreshOrder
                                .ALI_CONFIG_REFRESH:
                            this.updateAliPayConfig();
                            break;
                        case MessageOrderConstant
                                .PayConfigRefreshOrder
                                .WX_CONFIG_REFRESH:
                            this.updateWxPayConfig();
                            break;
                        default:
                            logger.info("[支付模块]:刷新支付参数命令不明，未执行操作");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    int reconsumeTimes = messageExt.getReconsumeTimes();
                    if (reconsumeTimes >= 3) {
                        // 重试次数超过限制即通知broker已消费
                        // 此处补充业务代码处理消费产生异常的操作
                        return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                    }
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER; //稍后再试
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS; //消费成功
            });
            consumer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAliPayConfig(){
        logger.info("[支付模块]:刷新了支付宝的支付配置参数");
        Map<String, String> aliConfigMap = payConfigPropertiesService.getAliConigInit("PROD");
        aliPayPropertiesBean.fromMap(aliConfigMap);
    }
    private void updateWxPayConfig(){
        logger.info("[支付模块]:刷新了微信支付的支付配置参数");
        Map<String, String> wxConfigMap = payConfigPropertiesService.getWxConigInit("PROD");
        wxPayPropertiesBean.fromMap(wxConfigMap);
    }
}
