package com.ivan.my.thread.pool.service.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class CustomSpringEventListener implements ApplicationListener<CustomSpringEvent> {
    @Override
    public void onApplicationEvent(CustomSpringEvent event) {
        // сюда попадём, когда издатель (publisher) опубликует событие
        if("click".equalsIgnoreCase(event.getMessage()))
            System.out.println("click event happened");
        System.out.println("Received spring custom event - " + event.getMessage());
    }
}
