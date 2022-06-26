package org.match.sevice;

import org.match.components.OrderStorage;
import org.match.enums.ErrorCode;
import org.match.enums.OrderType;
import org.match.exception.MySelfException;
import org.match.interfaces.IOrder;
import org.match.interfaces.IStockRepository;
import org.match.model.bean.Order;
import org.match.model.req.AddOrderReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService{

    @Autowired
    private OrderStorage orderStorage;

    @Autowired
    private IStockRepository stockRepository;

    public void addOrder(AddOrderReq req) {
        List<IOrder> orders = new ArrayList<>();

        if (req.getOrderType().equals(OrderType.SELL)) {
            checkStock(req);
        }

        checkPrice(req);

        for (int i = 0; i < req.getTimes(); i++) {
            orders.add(new Order(req));
        }

        orderStorage.insert(orders);
    }

    private void checkStock(AddOrderReq req){
        var stock = stockRepository.findStock(req.getUser(),req.getItemId());
        var orderCnt =orderStorage.getOrderCnt(req.getUser(), req.getItemId()).intValue();

        if(stock.getAmount().intValue() < req.getTimes() + orderCnt){
            throw new MySelfException(ErrorCode.STOCK_NOT_ENOUGH);
        }
    }

    private void checkPrice(AddOrderReq req){
        if(req.getPrice().compareTo(BigDecimal.ZERO) != 1){
            throw new MySelfException(ErrorCode.ORDER_PRICE_MUST_POSITIVE);
        }
    }
}
