package org.match.model.bean;

import org.match.enums.OrderType;
import org.match.enums.TransactionType;
import org.match.interfaces.IOrder;
import org.match.model.req.AddOrderReq;

import java.math.BigDecimal;
import java.time.Instant;

public class Order implements IOrder {

    private int itemId;

    private BigDecimal price;

    private String user;

    private OrderType orderType;

    private TransactionType transactionType;

    private Instant createTime;

    public Order() {
        this.transactionType = TransactionType.INIT;
        this.createTime = Instant.now();
    }

    public Order(int itemId, BigDecimal price, String user, OrderType orderType) {
        this();
        this.itemId = itemId;
        this.price = price;
        this.user = user;
        this.orderType = orderType;
    }

    public Order(AddOrderReq req) {
        this();
        this.itemId = req.getItemId();
        this.price = req.getPrice();
        this.user = req.getUser();
        this.orderType = req.getOrderType();
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public Instant getCreateTime() {
        return createTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "itemId=" + itemId +
                ", price=" + price +
                ", user='" + user + '\'' +
                ", orderType=" + orderType +
                ", transactionType=" + transactionType +
                ", createTime=" + createTime +
                '}';
    }
}
