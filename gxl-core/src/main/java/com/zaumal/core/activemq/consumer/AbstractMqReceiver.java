package com.zaumal.core.activemq.consumer;

public abstract class AbstractMqReceiver<T>{
    public abstract void receiveMessage(T t);
}
