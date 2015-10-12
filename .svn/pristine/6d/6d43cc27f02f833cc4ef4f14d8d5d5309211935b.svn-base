package com.athena.mis.application.actions.bankbranch

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.District
import com.athena.mis.application.service.BankBranchService
import com.athena.mis.application.service.DistrictService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.application.utility.DistrictCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class UpdateBankBranchActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    // constants
    private static String BANK_UPDATE_FAILURE_MESSAGE = "Bank branch could not be updated"
    private static String BANK_UPDATE_SUCCESS_MESSAGE = "Bank branch has been updated successfully"
    private static final String BANK_BRANCH_OBJECT = "bankBranchObject"
    private static final String OBJ_NOT_FOUND = "Selected bank branch not found"
    private static final String DUPLICATED_PRINCIPLE_BRANCH = "Bank already has a principle branch"
    private static final String BANK_BRANCH_MUST_BE_UNIQUE = "Bank branch must be unique"
    private static final String BRANCH_CAN_NOT_MAKE_GLOBAL = "Global branch exists for this bank"
    private static final String DISTRICT_IS_NOT_GLOBAL = "District is not global"
    private static final String HAS_GLOBAL = "hasGlobal"

    BankBranchService bankBranchService
    DistrictService districtService
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)                            // set default
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            if (!parameterMap.id) {                                             // check required parameters
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long bankBranchId = Long.parseLong(parameterMap.id.toString())

            BankBranch oldBankBranch = (BankBranch) bankBranchService.read(bankBranchId)     // get bank object from cache utitlity

            int count=bankBranchService.countByCodeAndIdNotEqual(oldBankBranch)
            if(count>0){
                result.put(Tools.MESSAGE,BANK_BRANCH_MUST_BE_UNIQUE)
                return result
            }
            if (!oldBankBranch) {                                                  // check whether selected bank object exists or not
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            BankBranch bankBranch = buildBankBranch(parameterMap, oldBankBranch)

            if(bankBranch.isGlobal.booleanValue()){
                Map hasGlobalBranch = (Map)checkGlobalBranch(bankBranch)
                Boolean isGlobalBranch= (Boolean)hasGlobalBranch.get(HAS_GLOBAL)
                if(isGlobalBranch.booleanValue()){
                    result.put(Tools.MESSAGE,hasGlobalBranch.get(Tools.MESSAGE))
                    return result
                }
            }
            if(bankBranch.isPrincipleBranch) {
                BankBranch principleBranch = bankBranchCacheUtility.getPrincipleBankBranch(bankBranch.bankId)
                if(principleBranch) {
                    result.put(Tools.IS_ERROR, Boolean.TRUE)
                    result.put(Tools.MESSAGE, DUPLICATED_PRINCIPLE_BRANCH)
                    return result
                }
            }
            result.put(BANK_BRANCH_OBJECT, bankBranch)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BANK_UPDATE_FAILURE_MESSAGE)
            return result
        }

    }

    @Transactional
    public Object execute(Object parameters, Object obj) {

        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj                    // cast map returned from executePreCondition method
            BankBranch bankBranch = (BankBranch) preResult.get(BANK_BRANCH_OBJECT)
            int updateCount = bankBranchService.update(bankBranch)                // update Bank object in DB
            bankBranchCacheUtility.update(bankBranch, bankBranchCacheUtility.DEFAULT_SORT_PROPERTY, bankBranchCacheUtility.SORT_ORDER_ASCENDING)
            result.put(BANK_BRANCH_OBJECT, bankBranch)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            throw new RuntimeException(BANK_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BANK_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj                   // cast map returned from execute method
            BankBranch bankBranch = (BankBranch) executeResult.get(BANK_BRANCH_OBJECT)             // get bank object from executeResult
            GridEntity object = new GridEntity();                                // build grid entity object
            object.id = bankBranch.id;
            Bank bank = (Bank) bankCacheUtility.read(bankBranch.bankId)
            District district = (District) districtCacheUtility.read(bankBranch.districtId)
            object.cell = [
                    Tools.LABEL_NEW,
                    bankBranch.id,
                    bank.name,
                    district.name,
                    bankBranch.name,
                    bankBranch.code ? bankBranch.code : Tools.EMPTY_SPACE,
                    bankBranch.address ? bankBranch.address : Tools.EMPTY_SPACE,
                    bankBranch.isPrincipleBranch? Tools.YES : Tools.NO,
                    bankBranch.isSmeServiceCenter? Tools.YES : Tools.NO
            ];
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, bankBranch.version)
            result.put(Tools.MESSAGE, BANK_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BANK_UPDATE_FAILURE_MESSAGE)
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
                result.put(Tools.MESSAGE, BANK_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, true)
            result.put(Tools.MESSAGE, BANK_UPDATE_FAILURE_MESSAGE)
            return result

        }
    }

    private BankBranch buildBankBranch(GrailsParameterMap parameterMap, BankBranch oldExhBankBranch) {
        BankBranch newExhBankBranch = new BankBranch(parameterMap)
        oldExhBankBranch.name = newExhBankBranch.name
        oldExhBankBranch.code = newExhBankBranch.code
        oldExhBankBranch.address = newExhBankBranch.address
        oldExhBankBranch.isGlobal = newExhBankBranch.isGlobal
        oldExhBankBranch.districtId = Long.parseLong(parameterMap.districtId.toString())
        oldExhBankBranch.bankId = Long.parseLong(parameterMap.bankId.toString())
        oldExhBankBranch.isSmeServiceCenter = newExhBankBranch.isSmeServiceCenter
        oldExhBankBranch.isPrincipleBranch = newExhBankBranch.isPrincipleBranch
        oldExhBankBranch.updatedOn = new Date()
        oldExhBankBranch.updatedBy = appSessionUtil.getAppUser().id
        return oldExhBankBranch
    }
    private Map checkGlobalBranch(BankBranch bankBranch){
        Map result = new LinkedHashMap()
        result.put(HAS_GLOBAL,Boolean.TRUE)
        District district=(District)districtService.read(bankBranch.districtId)
        if(!district.isGlobal.booleanValue()){
            result.put(Tools.MESSAGE,DISTRICT_IS_NOT_GLOBAL)
            return result
        }

        int count= bankBranchService.countByIsGlobalAndDistrictIdAndBankIdAndIdNotEqual(true,bankBranch.districtId,bankBranch.bankId,bankBranch.id)
        if(count>0){
            result.put(Tools.MESSAGE,BRANCH_CAN_NOT_MAKE_GLOBAL)
            return result
        }
        result.put(HAS_GLOBAL,Boolean.FALSE)
        return result
    }
}
