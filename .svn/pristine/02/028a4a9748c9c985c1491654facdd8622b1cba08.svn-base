package com.athena.mis.arms.actions.rmspurchaseinstrumentmapping

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.arms.entity.RmsCommissionDetails
import com.athena.mis.arms.entity.RmsPurchaseInstrumentMapping
import com.athena.mis.arms.service.RmsPurchaseInstrumentMappingService
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap


class EvaluateLogicForRmsPurchaseInstrumentMappingActionService extends BaseService implements ActionIntf {

    RmsPurchaseInstrumentMappingService rmsPurchaseInstrumentMappingService
    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Logic evaluation failed"
    private static final String RMS_PURCHASE_INSTRUMENT_MAPPING_OBJ = "rmsPurchaseInstrumentMapping"
    private static final String AMOUNT = "amount"
    private static final String COMMISSION_DETAILS = "commissionDetails"
    private static final String P_AMOUNT = "pAmount"
    private static final String COMM = "comm"
    private static final String PNT = "pNt"
    private static final String POSTAGE = "postage"
    private static final String SERVICE_CHARGE = "serviceCharge"
    private static final String VAT = "vat"
    private static final String VAT_ON_PNT = "vatOnPnt"
    private static final String TOTAL = "total"

    /**
     * @param parameters -params from  UI
     * @param obj -N/A
     * @return a map necessary for execute method
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.id) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            if (!parameterMap.amount) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long id = Long.parseLong(parameterMap.id)
            RmsPurchaseInstrumentMapping rmsPurchaseInstrumentMapping = rmsPurchaseInstrumentMappingService.read(id)
            Double amount = Double.parseDouble(parameterMap.amount)
            result.put(RMS_PURCHASE_INSTRUMENT_MAPPING_OBJ, rmsPurchaseInstrumentMapping)
            result.put(AMOUNT, amount)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for execute post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Execute logic for commission scripts
     * @param parameters -parameters returned from UI
     * @param obj -N/A
     * @return- * @return -a map containing all objects necessary for UI
     */
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) parameters
            Double amount = (Double) executeResult.get(AMOUNT)
            RmsPurchaseInstrumentMapping rmsPurchaseInstrumentMapping = (RmsPurchaseInstrumentMapping) executeResult.get(RMS_PURCHASE_INSTRUMENT_MAPPING_OBJ)
            Map logicEvaluation = (Map) evaluateLogic(rmsPurchaseInstrumentMapping, amount)
            double comm = logicEvaluation.comm
            double pNt = logicEvaluation.pNt
            double postage = logicEvaluation.postage
            double serviceCharge = logicEvaluation.serviceCharge
            double vat = logicEvaluation.vat
            double vatOnPnt = logicEvaluation.vatOnPnt
            double total = logicEvaluation.total
            result.put(COMM, comm)
            result.put(PNT, pNt)
            result.put(POSTAGE, postage)
            result.put(SERVICE_CHARGE, serviceCharge)
            result.put(VAT, vat)
            result.put(VAT_ON_PNT, vatOnPnt)
            result.put(TOTAL, total)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Do nothing for buildSuccessResultForUI
     */

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Build failure message
     * @param obj -returned from previous method may be null
     * @return- failure message to indicate failure event
     */
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    private Map evaluateLogic(RmsPurchaseInstrumentMapping rmsPurchaseInstrumentMapping, Double amount) {
        double comm
        double pNt
        double postage
        double serviceCharge
        double vat
        double vatOnPnt
        double total

        String commissionScript = rmsPurchaseInstrumentMapping.commissionScript
        RmsCommissionDetails commissionDetails = new RmsCommissionDetails()
        Binding binding = new Binding()
        binding.setVariable(P_AMOUNT, amount)
        binding.setVariable(COMMISSION_DETAILS, commissionDetails)
        GroovyShell shell = new GroovyShell(binding)
        Object result = shell.evaluate(commissionScript)
        RmsCommissionDetails commissionDetailsObj = (RmsCommissionDetails) result
        comm = commissionDetailsObj.comm
        pNt = commissionDetailsObj.pNt
        postage = commissionDetailsObj.postage
        serviceCharge = commissionDetailsObj.serviceCharge
        vat = commissionDetailsObj.vat
        vatOnPnt = commissionDetailsObj.vatOnPnt
        total = commissionDetailsObj.totalCharge

        Map logicEvaluation = [comm: comm, pNt: pNt, postage: postage, serviceCharge: serviceCharge, vat: vat, vatOnPnt: vatOnPnt, total: total]
        return logicEvaluation
    }
}
