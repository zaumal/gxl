package com.zaumal.common.activemq;

import com.zaumal.core.activemq.consumer.AbstractMqReceiver;
/**
 * @description  队列消息监听器
 */
public class LogMqReceiver extends AbstractMqReceiver<String>{
    @Override
    public void receiveMessage(String str) {
    }
}
