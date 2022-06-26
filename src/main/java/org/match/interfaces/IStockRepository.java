package org.match.interfaces;

import org.match.model.entity.Stock;

public interface IStockRepository {

    Stock findStock(String user, Integer itemId);
}
