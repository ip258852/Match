package org.match.sevice;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.match.components.OrderStorage;
import org.match.enums.ErrorCode;
import org.match.enums.OrderType;
import org.match.enums.TransactionType;
import org.match.exception.MySelfException;
import org.match.interfaces.IMatchRuleService;
import org.match.interfaces.INotifyService;
import org.match.interfaces.IOrder;
import org.match.model.msg.MatchMsg;
import org.match.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MatchRuleService implements IMatchRuleService {

    private static Log log = LogFactory.getLog(MatchRuleService.class);

    @Autowired
    private OrderStorage orderStorage;

    @Autowired
    private RecordRepository recordRepository;

    @Autowired
    private INotifyService notifyService;

    @Override
    public void exe(Map<Integer,List<IOrder>> orderMap) {
        orderMap.entrySet().forEach(entry -> doExec(entry.getValue()));

        orderMap.keySet().forEach(itemId ->{
            var filter = clean(orderMap.get(itemId));
            orderMap.put(itemId, filter);
        });
    }

    private void doExec(List<IOrder> orderList){

        orderList.sort(Comparator.comparing(IOrder::getCreateTime));

        int len = orderList.size();

        for (int i = 0; i < len - 1; i++) {
            var order = orderList.get(i);
            if(order.getTransactionType().equals(TransactionType.DEAL)){
                continue;
            }

            for (int j = i+1 ; j < len; j++) {
                var compareOrder = orderList.get(j);
                if (isSuccessMatch(order, compareOrder)) {
                    record(order,compareOrder);
                    break;
                }
            }
        }
    }

    private boolean isSuccessMatch(IOrder order,IOrder compareOrder){

        return order.getPrice().equals(compareOrder.getPrice()) &&
                !order.getOrderType().equals(compareOrder.getOrderType()) &&
                !order.getUser().equals(compareOrder.getUser()) &&
                compareOrder.getTransactionType().equals(TransactionType.INIT);
    }

    private void record(IOrder order, IOrder compareOrder) {
        try{

            var sell = order.getOrderType().equals(OrderType.SELL) ? order : compareOrder;
            var buy = order.getOrderType().equals(OrderType.BUY) ? order : compareOrder;

            order.setTransactionType(TransactionType.DEAL);
            compareOrder.setTransactionType(TransactionType.DEAL);

            int cnt = recordRepository.save(sell, buy);
            if(cnt != 1){
                throw new MySelfException(ErrorCode.SQL_INSERT_CNT_VALID);
            }

            orderStorage.getOrderCnt(sell.getUser(), sell.getItemId()).addAndGet(-1);

            notifyService.notifyResult(new MatchMsg(sell.getUser(),"system","sell success"));
            notifyService.notifyResult(new MatchMsg(buy.getUser(),"system","buy success"));

            log.info("Deal success orderA :[" + order + "], orderB :[" + compareOrder + "]." );
        }catch (Exception e){
            order.setTransactionType(TransactionType.INIT);
            compareOrder.setTransactionType(TransactionType.INIT);
            log.info("Deal fail orderA :[" + order + "], orderB :[" + compareOrder + "]."  );
        }
    }

    private List<IOrder> clean(List<IOrder> orderList) {
        return orderList.stream().filter(order -> order.getTransactionType().equals(TransactionType.INIT)).collect(Collectors.toList());
    }
}
