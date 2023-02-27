package com.ivan.my.thread.pool.controller;

import com.ivan.my.thread.pool.service.event.CustomSpringEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ClickController {

    private final CustomSpringEventPublisher eventPublisher;

    @GetMapping(value = "/click/event")
    public void clickEvent() {
        eventPublisher.publishCustomEvent("click");
    }

}
