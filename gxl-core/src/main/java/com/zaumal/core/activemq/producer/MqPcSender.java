package com.zaumal.core.activemq.producer;

import javax.annotation.Resource;
import javax.jms.Destination;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

//生产消费模式
@Component
public class MqPcSender{
	@Resource(name = "jmsPcTemplate")
	private JmsTemplate jmsTemplate;
	
	public void send(final Object message,String destination){
		try{
			jmsTemplate.convertAndSend(destination,message);
		} catch (Exception e){
			throw new RuntimeException("ActiveMq 无法连接！");
		}
		
	}
	
	public void send(final Object message,Destination destination){
		try{
			jmsTemplate.convertAndSend(destination,message);
		} catch (Exception e){
			throw new RuntimeException("ActiveMq 无法连接！");
		}
		
	}

	public JmsTemplate getJmsTemplate(){
		return jmsTemplate;
	}

	public void setJmsTemplate(JmsTemplate jmsTemplate){
		this.jmsTemplate = jmsTemplate;
	}
}
