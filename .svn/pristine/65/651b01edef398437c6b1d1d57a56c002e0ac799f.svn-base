package com.athena.mis.exchangehouse.actions.currencyconversion

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhCurrencyConversion
import com.athena.mis.exchangehouse.utility.ExhCurrencyConversionCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

class ExhSelectCurrencyConversionActionService extends BaseService implements ActionIntf {

    private static final String HAS_ACCESS = "hasAccess"
    @Autowired
    ExhCurrencyConversionCacheUtility exhCurrencyConversionCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
        try {
            Map outputMap = new HashMap()
            if (exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put(HAS_ACCESS, new Boolean(true))
            } else {
                outputMap.put(HAS_ACCESS, new Boolean(false))
            }
            return outputMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    /**
     * Select the currencyConversion by id (primary key) and returns
     *
     * @param params request parameters
     * @param obj additional parameters, not required for this action
     * @return return CurrencyConversion instance and version info in a map
     */
    public Object execute(Object parameters, Object obj) {
        try {
            Long id = Long.parseLong(parameters.id)
            ExhCurrencyConversion currencyConversionInstance = (ExhCurrencyConversion) exhCurrencyConversionCacheUtility.read(id)
            return currencyConversionInstance
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null
        }
    }

    /**
     * Select currencyConversion has not post-condition
     *
     * @param paramters
     * @param obj
     * @return nothing
     */
    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }


    public Object buildSuccessResultForUI(Object currencyConversionInstance) {
        Map result = [] as LinkedHashMap
        try {
            ExhCurrencyConversion currencyConversion = (ExhCurrencyConversion) currencyConversionInstance
            result = [entity: currencyConversion,
                    version: currencyConversion.version]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [entity: null]
            return result
        }
    }

    /**
     * Builds UI specific object on failure;
     *
     * @param obj Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public Object buildFailureResultForUI(Object obj) {

        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, true)
            result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
            return result

        }
    }

}
