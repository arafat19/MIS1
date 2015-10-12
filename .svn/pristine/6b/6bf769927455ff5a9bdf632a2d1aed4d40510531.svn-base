package com.athena.mis.arms.actions.rmspurchaseinstrumentmapping

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.District
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.arms.entity.RmsCommissionDetails
import com.athena.mis.arms.entity.RmsPurchaseInstrumentMapping
import com.athena.mis.arms.service.RmsPurchaseInstrumentMappingService
import com.athena.mis.arms.utility.RmsInstrumentTypeCacheUtility
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class CreateRmsPurchaseInstrumentMappingActionService extends BaseService implements ActionIntf{

    RmsPurchaseInstrumentMappingService rmsPurchaseInstrumentMappingService
    @Autowired
    RmsInstrumentTypeCacheUtility rmsInstrumentTypeCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    RmsSessionUtil rmsSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private final static String EXIST_MESSAGE = "Purchase instrument mapping already exists"
    private static final String INSTRUMENT_MAPPING = "instrumentMapping"
    private static final String FAILURE_MSG = "Failed to save purchase instrument mapping"
    private static final String CREATE_SUCCESS_MSG = "Purchase instrument mapping saved successfully"
    private static final String COMMISSION_DETAILS = "commissionDetails"
    private static final String P_AMOUNT = "pAmount"
    private static final String INVALID_LOGIC = "Invalid commission logic."

    /**
     * Get parameters from UI and build RmsPurchaseInstrumentMapping object
     * 1. Build RmsPurchaseInstrumentMapping object with new parameters
     * 2. Check existence of name
     * 3. Check existence of code
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            RmsPurchaseInstrumentMapping purchaseInstrumentMapping = buildRmsPurchaseInstrumentMapping(parameterMap)
            String errMsg = isValidLogic(purchaseInstrumentMapping, 100.0d) // test with arbitrary amount
            if (errMsg) {
                result.put(Tools.MESSAGE, errMsg)
                return result
            }
            int mapping = rmsPurchaseInstrumentMappingService.countExistingMapping(purchaseInstrumentMapping.bankBranchId, purchaseInstrumentMapping.instrumentTypeId)
            if (mapping > 0) {
                result.put(Tools.MESSAGE, EXIST_MESSAGE)
                return result
            }
            result.put(INSTRUMENT_MAPPING, purchaseInstrumentMapping)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Save RmsPurchaseInstrumentMapping object in DB
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            RmsPurchaseInstrumentMapping purchaseInstrumentMapping = (RmsPurchaseInstrumentMapping) preResult.get(INSTRUMENT_MAPPING)
            RmsPurchaseInstrumentMapping savedPurchaseInstrumentMapping = rmsPurchaseInstrumentMappingService.create(purchaseInstrumentMapping)
            result.put(INSTRUMENT_MAPPING, savedPurchaseInstrumentMapping)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Build grid object to show a single row (newly created object) in grid
     * 1. Get country object by id
     * 2. build success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary to indicate success event
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            RmsPurchaseInstrumentMapping purchaseInstrumentMapping = (RmsPurchaseInstrumentMapping) executeResult.get(INSTRUMENT_MAPPING)
            SystemEntity instrument = (SystemEntity) rmsInstrumentTypeCacheUtility.read(purchaseInstrumentMapping.instrumentTypeId)
            Bank bank = (Bank) bankCacheUtility.read(purchaseInstrumentMapping.bankId)
            BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.read(purchaseInstrumentMapping.bankBranchId)
            District district = (District) districtCacheUtility.read(purchaseInstrumentMapping.districtId)
            GridEntity object = new GridEntity()
            object.id = purchaseInstrumentMapping.id
            object.cell = [
                    Tools.LABEL_NEW,
                    instrument ? instrument.key : Tools.EMPTY_SPACE,
                    bank? bank.name : Tools.EMPTY_SPACE,
                    bankBranch? bankBranch.name : Tools.EMPTY_SPACE,
                    district? district.name : Tools.EMPTY_SPACE
            ]
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MSG)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
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
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * Build new RmsPurchaseInstrumentMapping object
     * @param parameterMap -serialized parameters from UI
     * @return -new RmsPurchaseInstrumentMapping object
     */
    private RmsPurchaseInstrumentMapping buildRmsPurchaseInstrumentMapping(GrailsParameterMap parameterMap) {
        RmsPurchaseInstrumentMapping rmsPurchaseInstrumentMapping = new RmsPurchaseInstrumentMapping(parameterMap)
        rmsPurchaseInstrumentMapping.companyId = rmsSessionUtil.appSessionUtil.getCompanyId()
        return rmsPurchaseInstrumentMapping
    }

    private String isValidLogic(RmsPurchaseInstrumentMapping rmsPurchaseInstrumentMapping, double amount) {
        try{
            String commissionScript = rmsPurchaseInstrumentMapping.commissionScript
            RmsCommissionDetails commissionDetails = new RmsCommissionDetails()
            Binding binding = new Binding()
            binding.setVariable(P_AMOUNT, amount)
            binding.setVariable(COMMISSION_DETAILS, commissionDetails)
            GroovyShell shell = new GroovyShell(binding)
            Object result = shell.evaluate(commissionScript)
            if (! result instanceof RmsCommissionDetails) {
                return INVALID_LOGIC
            }
            RmsCommissionDetails commissionDetailsObj = (RmsCommissionDetails) result
            if (!checkCommissionDetailsValidity(commissionDetailsObj)) {
                return INVALID_LOGIC
            }
        } catch(Exception ex) {
            log.error(ex.getMessage())
            return INVALID_LOGIC
        }
        return null
    }

    private boolean checkCommissionDetailsValidity(RmsCommissionDetails commissionDetails) {
        float totalCharge = (
                commissionDetails.comm +
                        commissionDetails.pNt +
                        commissionDetails.postage +
                        commissionDetails.serviceCharge +
                        commissionDetails.vat +
                        commissionDetails.vatOnPnt
        )
        if (totalCharge < 0) return false
        return (totalCharge.round() == commissionDetails.totalCharge.round())
    }
}
