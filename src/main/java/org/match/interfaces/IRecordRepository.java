package org.match.interfaces;

public interface IRecordRepository {

    int save(IOrder sellOrder, IOrder buyOrder);

    int size();

    void truncate();
}
