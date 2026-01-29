package com.example.websocket.redis;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
public class RedisPublisher {

    private final StringRedisTemplate stringRedisTemplate;
    private final ChannelTopic channelTopic;

    public RedisPublisher(StringRedisTemplate stringRedisTemplate, ChannelTopic channelTopic) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.channelTopic = channelTopic;
    }

    public void publish(String message) {
        stringRedisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
