package com.athena.mis.application.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class GetLocalCurrencyTagLibActionService extends BaseService implements ActionIntf{

    @Autowired
    CurrencyCacheUtility currencyCacheUtility

    private static final String PROPERTY = 'property'
    private Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
       return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /** Get currency object of native country
     *  pull the value of given property
     * @param parameters -  a map of given attributes
     * @param obj - N/A
     * @return - value of given property
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Map attrs = (Map) parameters
            String property = attrs.get(PROPERTY)
            Currency currency = currencyCacheUtility.getLocalCurrency()
            String propValue = currency.properties.get(property)
            return propValue
        } catch (Exception e) {
            log.error(e.message)
            return Tools.EMPTY_SPACE
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

}
