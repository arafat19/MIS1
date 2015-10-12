package com.athena.mis.exchangehouse.actions.regularfee

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhRegularFee
import com.athena.mis.exchangehouse.utility.ExhRegularFeeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class CalculateExhRegularFeeActionService extends BaseService implements ActionIntf {
    private static final String AMOUNT = 'amount'
    private final Logger log = Logger.getLogger(getClass())

    private static final String CALCULATE_FAILURE_MESSAGE = "Error evaluating calculation logic"
    private static final String EXH_REGULAR_FEE = "exhRegularFee"
    private static final String REGULAR_FEE = "regularFee"
    private static final String AMOUNT_NOT_FOUND = "Amount required."
//    private static final String INVALID_INPUT = "Invalid amount."

    @Autowired
    ExhRegularFeeCacheUtility exhRegularFeeCacheUtility

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.amount) {
                result.put(Tools.MESSAGE, AMOUNT_NOT_FOUND)
                return result
            }
            double amount = Double.parseDouble(parameterMap.amount.toString())
            result.put(AMOUNT, amount)
            List<ExhRegularFee> lstRegularFee = exhRegularFeeCacheUtility.list()
            ExhRegularFee exhRegularFee = lstRegularFee[0]  // assuming that ExhRegularFee has one object for every company
            result.put(EXH_REGULAR_FEE, exhRegularFee)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CALCULATE_FAILURE_MESSAGE)
            return result
        }
    }

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            ExhRegularFee exhRegularFee = (ExhRegularFee)obj.exhRegularFee
            double amount = (double)obj.amount

            Map calculationResult = calculateRegularFee(amount, exhRegularFee)  // calculate through Logic
            if (calculationResult.message) {
                result.put(Tools.MESSAGE, calculationResult.message)
                return result
            }
            result.put(REGULAR_FEE, calculationResult.regularFee)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CALCULATE_FAILURE_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, CALCULATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CALCULATE_FAILURE_MESSAGE)
            return result
        }
    }

    private Map calculateRegularFee(double amount, ExhRegularFee exhRegularFee) {
        LinkedHashMap result = new LinkedHashMap()
        double totalFee = 0.0d
        try {
            Binding binding = new Binding()
            binding.setVariable(AMOUNT, amount)
            GroovyShell shell = new GroovyShell(binding)
            totalFee = (double)shell.evaluate(exhRegularFee.logic)
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.MESSAGE, e.message)
            return result
        }
        result.put(Tools.MESSAGE, null)
        result.put(REGULAR_FEE, totalFee.round(2))
        return result
    }
}
