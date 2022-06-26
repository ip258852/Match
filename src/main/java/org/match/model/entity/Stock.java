package org.match.model.entity;

import java.math.BigDecimal;

public class Stock {

    private String user;

    private Integer itemId;

    private BigDecimal amount;

    public Stock(String user, Integer itemId, BigDecimal amount) {
        this.user = user;
        this.itemId = itemId;
        this.amount = amount;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
