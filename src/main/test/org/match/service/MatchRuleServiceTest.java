package org.match.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.match.components.OrderStorage;
import org.match.enums.OrderType;
import org.match.interfaces.IRecordRepository;
import org.match.model.req.AddOrderReq;
import org.match.sevice.MatchRuleService;
import org.match.sevice.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class MatchRuleServiceTest {

    @Autowired
    private MatchRuleService matchRuleService;
    @Autowired
    private IRecordRepository recordRepository;
    @Autowired
    private OrderStorage orderStorage;
    @Autowired
    private OrderService orderService;
    @Autowired
    @Qualifier("matchExecutor")
    private ThreadPoolTaskExecutor matchExecutor;

    @BeforeEach
    public void before(){
        orderStorage.flush();
        recordRepository.truncate();
    }

    @Test
    public void giveTwoOrder_thenMatch_returnSuccess(){

        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"kobe",OrderType.SELL,1));
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"Peter",OrderType.BUY,1));

        var storeData =orderStorage.getData();
        matchRuleService.exe(storeData);

        assertThat(recordRepository.size()).isEqualTo(1);
    }

    @Test
    public void exeTwiceMatchAction_returnSuccess(){

        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"kobe",OrderType.SELL,2));
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"Peter",OrderType.BUY,1));

        var storeData =orderStorage.getData();
        matchRuleService.exe(storeData);

        assertThat(recordRepository.size()).isEqualTo(1);

        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"Sam",OrderType.BUY,1));
        matchRuleService.exe(storeData);

        assertThat(recordRepository.size()).isEqualTo(2);
    }

    @Test
    public void giveDiffItemOrder_thenMatch_returnNoMatchSuccess(){
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"kobe",OrderType.SELL,1));
        orderService.addOrder(new AddOrderReq(6, BigDecimal.valueOf(5),"Peter",OrderType.BUY,1));


        var storeData =orderStorage.getData();
        matchRuleService.exe(storeData);

        assertThat(recordRepository.size()).isZero();
    }

    @Test
    public void giveDiffPriceOrder_thenMatch_returnNoMatchSuccess(){
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"kobe",OrderType.SELL,1));
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(6),"Peter",OrderType.SELL,1));


        var storeData =orderStorage.getData();
        matchRuleService.exe(storeData);

        assertThat(recordRepository.size()).isZero();
    }

    @Test
    public void giveTheSameUserOrder_thenMatch_returnNoMatchSuccess(){
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"kobe",OrderType.SELL,1));
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"kobe",OrderType.BUY,1));

        var storeData =orderStorage.getData();
        matchRuleService.exe(storeData);

        assertThat(recordRepository.size()).isZero();
    }

    @Test
    public void giveTheSameOrderTypeOrder_thenMatch_returnNoMatchSuccess(){

        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"kobe",OrderType.SELL,1));
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"Peter",OrderType.SELL,1));
        orderService.addOrder(new AddOrderReq(6, BigDecimal.valueOf(5),"kobe",OrderType.BUY,1));
        orderService.addOrder(new AddOrderReq(6, BigDecimal.valueOf(5),"Peter",OrderType.BUY,1));


        var storeData =orderStorage.getData();
        matchRuleService.exe(storeData);

        assertThat(recordRepository.size()).isZero();
    }

    @Test
    public void giveDuplicateBuyOrder_thenMatch_returnEarlyCreateMatchSuccess() throws InterruptedException {

        String buyer = "Peter";

        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"kobe",OrderType.SELL,1));
        Thread.sleep(100);
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),buyer,OrderType.BUY,1));
        Thread.sleep(100);
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"Sam",OrderType.BUY,1));
        Thread.sleep(100);
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"Andy",OrderType.BUY,1));


        var storeData =orderStorage.getData();
        matchRuleService.exe(storeData);

        assertThat(recordRepository.size()).isEqualTo(1);
        assertThat(storeData.get(5).stream().filter(order -> order.getUser().equals(buyer)).count()).isEqualTo(0);
    }

    @Test
    public void giveDuplicateSellOrder_thenMatch_returnEarlyCreateMatchSuccess() throws InterruptedException {

        String buyer = "Peter";

        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"kobe",OrderType.BUY,1));
        Thread.sleep(100);
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),buyer,OrderType.SELL,1));
        Thread.sleep(100);
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"Sam",OrderType.SELL,1));
        Thread.sleep(100);
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"Andy",OrderType.SELL,1));


        var storeData =orderStorage.getData();
        matchRuleService.exe(storeData);

        assertThat(recordRepository.size()).isEqualTo(1);
        assertThat(storeData.get(5).stream().filter(order -> order.getUser().equals(buyer)).count()).isEqualTo(0);
    }

    @Test
    public void giveDuplicateOrder_thenMatch_returnEarlyCreateMatchSuccess() throws InterruptedException {

        String noMatcher = "July";

        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"kobe",OrderType.BUY,1));
        Thread.sleep(100);
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"Justin",OrderType.BUY,1));
        Thread.sleep(100);
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"Jam",OrderType.BUY,1));
        Thread.sleep(100);
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),noMatcher,OrderType.BUY,1));
        Thread.sleep(100);
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"Peter",OrderType.SELL,1));
        Thread.sleep(100);
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"Sam",OrderType.SELL,1));
        Thread.sleep(100);
        orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"Andy",OrderType.SELL,1));


        var storeData =orderStorage.getData();
        matchRuleService.exe(storeData);

        assertThat(recordRepository.size()).isEqualTo(3);
        assertThat(storeData.get(5).stream().filter(order -> order.getUser().equals(noMatcher)).count()).isEqualTo(1);
    }

    @Test
    public void giveBulkOrder_thenMatch_returnEarlyCreateMatchSuccess() throws InterruptedException {

        int times = 200;
        var exe =  Executors.newScheduledThreadPool(5);

        matchExecutor.execute(()-> {
            for (int i = 0; i < times ; i++) {
                orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"kobe",OrderType.SELL,1));
            }
        });
        matchExecutor.execute(()-> {
            for (int i = 0; i < times ; i++) {
                orderService.addOrder(new AddOrderReq(5, BigDecimal.valueOf(5),"Jam",OrderType.BUY,1));
            }
        });

        exe.schedule(()-> matchRuleService.exe(orderStorage.getData()),10, TimeUnit.MILLISECONDS);

        Instant now = Instant.now();

        while (now.plus(30, ChronoUnit.SECONDS).isAfter(Instant.now())){
            if(recordRepository.size() == times){
                return;
            }
        }

        throw  new RuntimeException("fail");
    }
}
