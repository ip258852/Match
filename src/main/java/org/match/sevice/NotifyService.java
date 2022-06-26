package org.match.sevice;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.match.interfaces.INotifyService;
import org.match.interfaces.IMsg;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotifyService implements INotifyService {

    private static Log log = LogFactory.getLog(NotifyService.class);

    @Async
    @Override
    public void notifyResult(IMsg result) {
        log.info("notify msg : [" + result + "].");
    }
}
