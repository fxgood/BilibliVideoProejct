package com.njust.bilibili.service.util;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.TimeUnit;

public class RocketMQUtil {
    //同步发送信息
    public static void syncSendMsg(DefaultMQProducer producer, Message msg) throws MQBrokerException, RemotingException, InterruptedException, MQClientException {
        SendResult result=producer.send(msg);
        System.out.println(result);
    }

    //异步发送消息
    public static void asyncSendMsg(DefaultMQProducer producer,Message msg) throws RemotingException, InterruptedException, MQClientException {
        int messageCount=2; //发送两次
        CountDownLatch2 countDownLatch2=new CountDownLatch2(messageCount);  //倒计时器
        for(int i=0;i<messageCount;i++){
            producer.send(msg, new SendCallback() { //发送消息的回调函数，成功或失败
                @Override
                public void onSuccess(SendResult sendResult) {
                    countDownLatch2.countDown();
                    System.out.println(sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    countDownLatch2.countDown();
                    System.out.println("发送消息时发生异常！"+e);
                    e.printStackTrace();
                }
            });
        }
        countDownLatch2.await(5, TimeUnit.SECONDS);
    }
}
