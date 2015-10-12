package com.athena.mis.application.actions.currency

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select specific object of currency
 *  For details go through Use-Case doc named 'SelectCurrencyActionService'
 */
class SelectCurrencyActionService extends BaseService implements ActionIntf {

    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private final Logger log = Logger.getLogger(getClass());
    /**
     * Check access of logged user
     * @param parameters -serialize parameters from UI
     * @param obj -N/A
     * @return - a map containing hasAccess(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        try {
            Map outputMap = new HashMap();
            if (appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put(Tools.HAS_ACCESS, Boolean.TRUE)
            } else {
                outputMap.put(Tools.HAS_ACCESS, Boolean.FALSE)
            }
            return outputMap;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    /**
     * 1. pull currency object
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing currency object
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Currency currencyInstance = (Currency) currencyCacheUtility.read(Long.parseLong(parameters.id.toString()))
            return currencyInstance;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null;
    }

    /**
     * Get currency object
     * @param obj - object receive from execute method
     * @return  - currency object as grid entity
     */
    public Object buildSuccessResultForUI(Object currencyInstance) {
        Map result = [] as LinkedHashMap
        try {
            result = [entity: currencyInstance, version: currencyInstance.version]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return result
        }
    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = [] as LinkedHashMap
        try {
            result = [isError: true, entity: null, errors: null, message: ENTITY_NOT_FOUND_ERROR_MESSAGE]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return result
        }
    }

}

