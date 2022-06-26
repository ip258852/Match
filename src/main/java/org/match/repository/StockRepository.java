package org.match.repository;

import org.match.interfaces.IStockRepository;
import org.match.model.entity.Stock;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public class StockRepository implements IStockRepository {

    @Override
    public Stock findStock(String user, Integer itemId) {

        // mock .....
        return new Stock(user,itemId,BigDecimal.valueOf(999999999));
    }
}
