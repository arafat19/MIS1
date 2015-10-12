package com.athena.mis.arms.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.arms.entity.RmsExchangeHouse
import com.athena.mis.arms.service.RmsExchangeHouseService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Created by user on 3/24/14.
 */
@Component("rmsExchangeHouseCacheUtility")
class RmsExchangeHouseCacheUtility extends ExtendedCacheUtility {

    @Autowired
    RmsExchangeHouseService rmsExchangeHouseService

    public final String SORT_ON_NAME = "name";

    public void init() {
        List list = rmsExchangeHouseService.list();
        super.setList(list)
    }

    // return number of same exchange house code in a specific company
    public int countByCode(String code) {
        int count = 0;
        List<RmsExchangeHouse> lstExchangeHouse = (List<RmsExchangeHouse>) list()
        for (int i = 0; i < lstExchangeHouse.size(); i++) {
            if (lstExchangeHouse[i].code.equalsIgnoreCase(code))
                count++
        }
        return count
    }

    // return number of same exchange house code and id is not equal in a specific company
    public int countByCodeAndIdNotEqual(String code, long id) {
        int count = 0;
        List<RmsExchangeHouse> lstExchangeHouse = (List<RmsExchangeHouse>) list()
        for (int i = 0; i < lstExchangeHouse.size(); i++) {
            if (lstExchangeHouse[i].code.equalsIgnoreCase(code) && lstExchangeHouse[i].id != id)
                count++
        }
        return count
    }

    //update exchange house balance property
    public void updateBalance(long exhId, double amount) {
        List<RmsExchangeHouse> lstExchangeHouse = (List<RmsExchangeHouse>) list()
        for (int i = 0; i < lstExchangeHouse.size(); i++) {
            RmsExchangeHouse exchangeHouse = lstExchangeHouse[i]
            if (exchangeHouse.id == exhId) {
                exchangeHouse.balance = amount
            }
        }
    }

}
