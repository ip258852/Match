package org.match.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.match.components.OrderStorage;
import org.match.enums.ErrorCode;
import org.match.enums.OrderType;
import org.match.exception.MySelfException;
import org.match.interfaces.IStockRepository;
import org.match.model.entity.Stock;
import org.match.model.req.AddOrderReq;
import org.match.sevice.OrderService;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderStorage orderStorage;

    @MockBean
    private IStockRepository stockRepository;

    @Test
    public void giveOrder_thenSuccess(){
        String user = "kobe";
        Integer itemId = 5;
        Integer times = 9 ;
        Mockito.when(stockRepository.findStock(Mockito.any(),Mockito.any())).thenReturn(new Stock(user,itemId, BigDecimal.valueOf(999999999)));

        AddOrderReq req = new AddOrderReq();
        req.setItemId(itemId);
        req.setOrderType(OrderType.SELL);
        req.setUser(user);
        req.setPrice(BigDecimal.valueOf(50.0));
        req.setTimes(times);

        orderService.addOrder(req);

       assertThat(orderStorage.getOrderCnt(user, itemId).intValue()).isEqualTo(times);
    }

    @Test
    public void giveOutOfStockAmountOrder_thenFail(){
        String user = "kobe";
        Integer itemId = 5;
        Integer times = 9 ;
        Mockito.when(stockRepository.findStock(Mockito.any(),Mockito.any())).thenReturn(new Stock(user,itemId, BigDecimal.valueOf(1)));

        AddOrderReq req = new AddOrderReq();
        req.setItemId(itemId);
        req.setOrderType(OrderType.SELL);
        req.setUser(user);
        req.setPrice(BigDecimal.valueOf(50.0));
        req.setTimes(times);

        try {
            orderService.addOrder(req);
        }catch (MySelfException exception){
            assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.STOCK_NOT_ENOUGH.getCode());
            assertThat(exception.getErrorCode().getMsg()).isEqualTo(ErrorCode.STOCK_NOT_ENOUGH.getMsg());
        }
    }

    @Test
    public void giveNegativePrice_thenFail(){
        String user = "kobe";
        Integer itemId = 5;
        Integer times = 9 ;
        Mockito.when(stockRepository.findStock(Mockito.any(),Mockito.any())).thenReturn(new Stock(user,itemId, BigDecimal.valueOf(999999999)));

        AddOrderReq req = new AddOrderReq();
        req.setItemId(itemId);
        req.setOrderType(OrderType.SELL);
        req.setUser(user);
        req.setPrice(BigDecimal.valueOf(-50.0));
        req.setTimes(times);

        try {
            orderService.addOrder(req);
        }catch (MySelfException exception){
            assertThat(exception.getErrorCode().getCode()).isEqualTo(ErrorCode.ORDER_PRICE_MUST_POSITIVE.getCode());
            assertThat(exception.getErrorCode().getMsg()).isEqualTo(ErrorCode.ORDER_PRICE_MUST_POSITIVE.getMsg());
        }
    }
}
