package org.match.repository;

import org.match.interfaces.IOrder;
import org.match.interfaces.IRecordRepository;
import org.springframework.stereotype.Repository;

import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class RecordRepository implements IRecordRepository {

    private AtomicInteger recordCnt = new AtomicInteger();

    @Override
    public int save(IOrder sellOrder, IOrder buyOrder) {
        // mock .....
        recordCnt.incrementAndGet();
        return 1;
    }

    @Override
    public int size() {
        return recordCnt.intValue();
    }

    @Override
    public void truncate() {
        recordCnt.set(0);
    }
}
