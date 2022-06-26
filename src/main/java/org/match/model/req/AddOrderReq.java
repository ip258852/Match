package org.match.model.req;

import org.match.enums.OrderType;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AddOrderReq {

    @NotNull
    private Integer itemId;

    @NotNull
    private BigDecimal price;

    @NotEmpty
    private String user;

    @NotNull
    private OrderType orderType;

    @NotNull
    @Min(1)
    private Integer times;

    public AddOrderReq() {
    }

    public AddOrderReq(Integer itemId, BigDecimal price, String user, OrderType orderType, Integer times) {
        this.itemId = itemId;
        this.price = price;
        this.user = user;
        this.orderType = orderType;
        this.times = times;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
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

    public Integer getTimes() {
        return times;
    }

    public void setTimes(Integer times) {
        this.times = times;
    }
}
