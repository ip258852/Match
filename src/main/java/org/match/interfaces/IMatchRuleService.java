package org.match.interfaces;

import java.util.List;
import java.util.Map;

public interface IMatchRuleService {

    void exe(Map<Integer, List<IOrder>> orderMap);
}
