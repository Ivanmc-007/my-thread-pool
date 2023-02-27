package com.ivan.my.thread.pool.service.check;

public interface Check<CONSUMER, PRODUCER> {

    PRODUCER checkExecute();

}
