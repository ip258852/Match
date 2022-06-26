package org.match.interfaces;

import org.match.enums.OrderType;
import org.match.enums.TransactionType;

import java.math.BigDecimal;
import java.time.Instant;

public interface IOrder {

    int getItemId();

    BigDecimal getPrice();

    String getUser();

    OrderType getOrderType();

    TransactionType getTransactionType();

    void setTransactionType(TransactionType transactionType);

    Instant getCreateTime();
}
