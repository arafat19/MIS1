package com.athena.mis.application.actions.currency

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.Currency
import com.athena.mis.application.service.CurrencyService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete currency object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteCurrencyActionService'
 */
class DeleteCurrencyActionService extends BaseService implements ActionIntf {

    CurrencyService currencyService
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private static String CURRENCY_DELETE_SUCCESS_MSG = "Currency has been successfully deleted!"
    private static String CURRENCY_DELETE_FAILURE_MSG = "Sorry! Currency has not been deleted."

    private final Logger log = Logger.getLogger(getClass());
    /**
     * Get association message
     * @param parameters -serialize parameters from UI
     * @param obj -N/A
     * @return - a map containing hasAccess(True/False) and association message (if any)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        try {
            Map outputMap = new HashMap();
            Currency currency = (Currency) currencyCacheUtility.read(Long.parseLong(parameters.id.toString()));
            String associationMessage = null;
            if (appSessionUtil.getAppUser().isPowerUser) {
                outputMap.put(Tools.HAS_ACCESS, Boolean.TRUE)
                associationMessage = isAssociated(currency)
            } else {
                outputMap.put(Tools.HAS_ACCESS, Boolean.FALSE)
            }
            if (associationMessage != null) {
                outputMap.put(Tools.HAS_ASSOCIATION, associationMessage);
            }
            return outputMap;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return null;
        }
    }
    /**
     * Delete currency object from DB and cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -N/A
     * @param obj - object receive from pre execute method
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            Integer currencyId = Integer.parseInt(parameters.id)
            Boolean result = (Boolean) currencyService.delete(currencyId);
            if (result.booleanValue()) {
                Currency currency = (Currency) currencyCacheUtility.read(currencyId)
                currencyCacheUtility.delete(currency.id)
            }
            return result
        } catch (Exception ex) {
            //@todo:rollback
            throw new RuntimeException('Failed to delete Currency')
            return Boolean.FALSE
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null

    }
    /**
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: CURRENCY_DELETE_SUCCESS_MSG]
    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result
        try {
            if (!obj) {
                return [deleted: Boolean.FALSE.booleanValue(), message: CURRENCY_DELETE_FAILURE_MSG]
            }
            return [deleted: Boolean.FALSE.booleanValue(), message: (String) obj]
        } catch (Exception ex) {
            log.error(ex.getMessage());
            return [deleted: Boolean.FALSE.booleanValue(), message: CURRENCY_DELETE_FAILURE_MSG]
        }
    }

    /**
     * Checking all association with currency table
     * @param currency - currency object
     * @return - int value of currency number
     */
    private String isAssociated(Currency currency) {
        Integer currencyId = currency.id;
        String currencyName = currency.name;
        Integer count = 0;

        // has task
        count = countCountry(currencyId);
        if (count.intValue() > 0) return Tools.getMessageOfAssociation(currencyName, count, Tools.DOMAIN_COUNTRY);

        if (PluginConnector.isPluginInstalled(PluginConnector.EXCHANGE_HOUSE)) {
            // has currency conversion
            count = countCurrencyConversion(currencyId);
            if (count.intValue() > 0) return Tools.getMessageOfAssociation(currencyName, count, Tools.DOMAIN_CURRENCY_CONVERSION);
            // has task
            count = countTask(currencyId);
            if (count.intValue() > 0) return Tools.getMessageOfAssociation(currencyName, count, Tools.DOMAIN_TASK);
        }
        return null;
    }
    /**
     * Get total currency number used in currency-conversion
     * @param currencyId - currency id
     * @return - int value of currency number
     */
    private int countCurrencyConversion(Integer currencyId) {
        String queryStr = """
            SELECT COUNT(id) as count
            FROM exh_currency_conversion
            WHERE from_currency = ${currencyId} OR to_currency = ${currencyId}
        """
        List currencyConversionCount = executeSelectSql(queryStr);
        int count = currencyConversionCount[0].count;
        return count;
    }
    /**
     * Get total currency number used in task
     * @param currencyId - currency id
     * @return - int value of currency number
     */
    private int countTask(Integer currencyId) {
        String queryStr = """
            SELECT COUNT(id) as count
            FROM exh_task
            WHERE from_currency_id = ${currencyId} OR to_currency_id = ${currencyId}
        """
        List currencyConversionCount = executeSelectSql(queryStr);
        int count = currencyConversionCount[0].count;
        return count;
    }
    /**
     * Get total currency number used in country
     * @param currencyId - currency id
     * @return - int value of currency number
     */
    private int countCountry(Integer currencyId) {
        String queryStr = """
            SELECT COUNT(id) as count
            FROM country
            WHERE currency_id = ${currencyId}
        """
        List currencyCount = executeSelectSql(queryStr);
        int count = currencyCount[0].count;
        return count;
    }
}


