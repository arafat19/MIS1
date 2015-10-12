package com.athena.mis.exchangehouse.actions.task

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.CurrencyCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.exchangehouse.config.ExhSysConfigurationCacheUtility
import com.athena.mis.exchangehouse.entity.ExhAgent
import com.athena.mis.exchangehouse.entity.ExhRegularFee
import com.athena.mis.exchangehouse.utility.ExhAgentCacheUtility
import com.athena.mis.exchangehouse.utility.ExhCurrencyConversionCacheUtility
import com.athena.mis.exchangehouse.utility.ExhRegularFeeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Calculate fees and commission for task object for cashier, agent & customer
 *  For details go through Use-Case doc named 'ExhGetFeeAndCommissionForTaskActionService'
 */
class ExhGetFeeAndCommissionForTaskActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String FAILED_TO_GET_FEES = "Failed to get regular fee and commission"
    private static final String AMOUNT_TO_EVALUATE = "amount"
    private static final String VALUE_1 = "1"

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    ExhAgentCacheUtility exhAgentCacheUtility
    @Autowired
    ExhRegularFeeCacheUtility exhRegularFeeCacheUtility
    @Autowired
    ExhCurrencyConversionCacheUtility exhCurrencyConversionCacheUtility
    @Autowired
    CurrencyCacheUtility currencyCacheUtility
    @Autowired
    ExhSysConfigurationCacheUtility exhSysConfigurationCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    /**
     * Check pre condition
     * 1. check required parameter ie amount, currency and rate
     * 2. pull currency conversion rate through its currency e.g GBP or AUD
     * 3. check configuration of calculate regular fee based on local currency
     * 4. conversion of amount based on rate
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map output = new HashMap<String, Object>()
        try {
            output.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap paramMap = (GrailsParameterMap) parameters
            if ((!paramMap.amount) || (!paramMap.currency)) {
                output.put(Tools.MESSAGE, FAILED_TO_GET_FEES)
                return output
            }
            long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
            double amount = Double.parseDouble(paramMap.amount)
            long currency = Long.parseLong(paramMap.currency)        // GBP or AUD
            long localCurrency = currencyCacheUtility.localCurrency.id
            long foreignCurrency = currencyCacheUtility.foreignCurrency.id
            boolean isParamCurrencyLocal = (currency == localCurrency)  // GBP as a local Currency
            double rate = 0.0d
            if (paramMap.rate) {
                rate = Double.parseDouble(paramMap.rate)
            } else {
                if (isParamCurrencyLocal) { // e.g. GBP
                    rate = exhCurrencyConversionCacheUtility.readByCurrencies((int) localCurrency, (int) foreignCurrency).rate
                } else {  // convert from BDT
                    rate = exhCurrencyConversionCacheUtility.readByCurrencies((int) foreignCurrency, (int) localCurrency).rate
                }
            }
            // Now read the config
            boolean evaluateAsLocalCurrency = true // Default value , if config not found
            SysConfiguration sysConfig = (SysConfiguration) exhSysConfigurationCacheUtility.readByKeyAndCompanyId(exhSysConfigurationCacheUtility.EXH_REGULAR_FEE_EVALUATE_ON_LOCAL_CURRENCY, companyId)
            if (sysConfig) {
                evaluateAsLocalCurrency = sysConfig.value.equals(VALUE_1)      // 1 = local(GBP) , 0 = foreign(BDT)
            }

            // Now determine the amount to evaluate. (need conversion or not)
            double amountToEvaluate = 0.0d

            if ((isParamCurrencyLocal && evaluateAsLocalCurrency) ||
                    (!isParamCurrencyLocal && !evaluateAsLocalCurrency)) {
                amountToEvaluate = amount
            } else {
                amountToEvaluate = amount * rate      // conversion
            }
            output.put(AMOUNT_TO_EVALUATE, amountToEvaluate)
            output.put(Tools.IS_ERROR, Boolean.FALSE)
            return output
        } catch (Exception e) {
            output.put(Tools.IS_ERROR, Boolean.TRUE)
            output.put(Tools.MESSAGE, FAILED_TO_GET_FEES)
            log.error(e.getMessage())
            return output
        }
    }

    /**
     * Get regular fee of Task amount for common ie cashier, agent or customer
     * Get commission of task amount for agent
     * @param params
     * @param obj -map returned from executePreCondition method
     * @return -map contains isError(true/false) and commission and regular fee
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        try {
            Map preResult = (Map) obj           // cast map return from pre condition
            double amountToEvaluate = (double) preResult.get(AMOUNT_TO_EVALUATE)
            double commission = 0.0d
            double fee = getRegularFee(amountToEvaluate)                  // get regular fee
            if (exhSessionUtil.appSessionUtil.hasRole(roleTypeCacheUtility.ROLE_TYPE_EXH_AGENT)) {
                commission = getCommission(amountToEvaluate)         // get commission for agent
            }
            return [isError: Boolean.FALSE, commission: commission, regularFee: fee]
        } catch (Exception e) {
            log.error(e.getMessage())
            return [isError: Boolean.TRUE, message: FAILED_TO_GET_FEES]
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for build success result for UI
     */
    public Object buildSuccessResultForUI(Object taskInstance) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj && obj.message) {
                result.put(Tools.MESSAGE, obj.message)
            } else {
                result.put(Tools.MESSAGE, FAILED_TO_GET_FEES)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILED_TO_GET_FEES)
            return result
        }
    }

    /**
     * Evaluate logic & amount by groovy shell
     * @param amount
     * @param logic -logic of commission or regular fee
     * @return totalFee
     */
    private double evaluateLogic(double amount, String logic) {
        double totalFee = 0.0d
        try {
            if (!logic || logic.isEmpty()) return totalFee
            Binding binding = new Binding()
            binding.setVariable(AMOUNT_TO_EVALUATE, amount)
            GroovyShell shell = new GroovyShell(binding)
            totalFee = (double) shell.evaluate(logic)
        } catch (Exception e) {
            log.error(e.getMessage())
        }
        return totalFee
    }

    /**
     * Get commission for agent
     * @param amount
     * @return commission
     */
    private double getCommission(double amount) {
        ExhAgent agent = (ExhAgent) exhAgentCacheUtility.read(exhSessionUtil.getUserAgentId())
        double commission = evaluateLogic(amount, agent.commissionLogic)
        return commission.round(2)
    }

    /**
     * Get regular fee for task
     * @param amount
     * @return regularFee
     */
    private double getRegularFee(double amount) {
        List<ExhRegularFee> lstRegularFee = exhRegularFeeCacheUtility.list()
        ExhRegularFee exhRegularFee = lstRegularFee[0]  // assuming that ExhRegularFee has one object for every company
        double regularFee = evaluateLogic(amount, exhRegularFee.logic)      // evaluate regular fee
        return regularFee.round(2)
    }
}
