package org.match.schedule;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.match.components.OrderStorage;
import org.match.interfaces.IMatchRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class MatchSchedule {

    private static Log log = LogFactory.getLog(MatchSchedule.class);

    @Autowired
    private OrderStorage orderStorage;

    @Autowired
    @Qualifier("matchExecutor")
    private ThreadPoolTaskExecutor matchExecutor;

    @Autowired
    private IMatchRuleService matchRuleService;

    @Scheduled(cron = "0/15 * * * * ?")
    public void exeSchedule() {
        log.info("exe schedule");
        matchExecutor.execute(()-> match());
    }

    private void match(){
        var lock = orderStorage.getLock();
        var data = orderStorage.getData();

        lock.lock();
        try {
            matchRuleService.exe(data);
        }finally {
            lock.unlock();
        }
    }
}
