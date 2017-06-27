package com.zaumal.core.activemq.converter;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component("messageConverter")
public class ObjectMessageConverter implements MessageConverter {   
    @Override 
    public Message toMessage(Object object, Session session)throws JMSException, 
    MessageConversionException { 
        ObjectMessage objectMessage = session.createObjectMessage((Serializable) object);   
        return objectMessage;   
    }   
    @Override
    public Object fromMessage(Message message) throws JMSException,
    MessageConversionException {   
        ObjectMessage objMessage = (ObjectMessage) message;  
        return objMessage.getObject();     
    }
}  
