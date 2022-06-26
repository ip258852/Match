package org.match.components;

import org.match.interfaces.IOrder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class OrderStorage {

    private Map<Integer,List<IOrder>> data = new HashMap<>();

    private Map<String, Map<Integer, AtomicInteger>> userOrderCnt = new HashMap<>();

    private Lock lock = new ReentrantLock();

    public void insert(List<IOrder> orders){

        if(orders.size()==0){
            return;
        }

        String user = orders.get(0).getUser();
        Integer itemId = orders.get(0).getItemId();

        lock.lock();
        try {
            List<IOrder> orderList = getOrderList(itemId);
            orderList.addAll(orders);
            getOrderCnt(user, itemId).addAndGet(orders.size());
        }finally {
            lock.unlock();
        }
    }

    public void flush(){
        data.clear();
        userOrderCnt.clear();
    }

    public Map<Integer,List<IOrder>> getData(){
        return data;
    }

    public Lock getLock() {
        return lock;
    }

    public AtomicInteger getOrderCnt(String user, Integer itemId){
        AtomicInteger cnt;
        Map<Integer, AtomicInteger> orderMappingCnt;

        if(!userOrderCnt.containsKey(user)){
            cnt = new AtomicInteger();
            orderMappingCnt = new HashMap<>();
            orderMappingCnt.put(itemId, cnt);
            userOrderCnt.put(user,orderMappingCnt);
        }else {
            orderMappingCnt = userOrderCnt.get(user);

            if(!orderMappingCnt.containsKey(itemId)){
                cnt = new AtomicInteger();
                orderMappingCnt.put(itemId, cnt);
            }else {
                cnt = orderMappingCnt.get(itemId);
            }
        }

        return cnt;
    }

    private List<IOrder> getOrderList(Integer itemId){

        List<IOrder> orderList;
        if(!data.containsKey(itemId)){
            orderList = new ArrayList<>();
            data.put(itemId, orderList);
        }else {
            orderList = data.get(itemId);
        }

        return orderList;
    }
}
