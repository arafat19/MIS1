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

class CreateBankBranchActionService extends BaseService implements ActionIntf {

    BankBranchService bankBranchService
    DistrictService districtService
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    DistrictCacheUtility districtCacheUtility
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    //assign bankBranch information saving information
    private static final String BANK_CREATE_SUCCESS_MSG = "Bank branch has been successfully saved!"
    private static final String BANK_CREATE_FAILURE_MSG = "Failed to save bank branch"
    private static final String DUPLICATED_PRINCIPLE_BRANCH = "Bank already has a principle branch"
    private static final String HAS_ACCESS = 'hasAccess'
    private static final String BANK_BRANCH_CODE_MUST_BE_UNIQUE = 'Bank branch code must be unique'
    private static final String BRANCH_CAN_NOT_MAKE_GLOBAL = "Global branch exists for this bank"
    private static final String DISTRICT_IS_NOT_GLOBAL = "District is not global"
    private static final String HAS_GLOBAL = "hasGlobal"

    //define logger to set all log message
    private Logger log = Logger.getLogger(getClass())

    //implement the precondition method of action class
    /*
    Get all pre condition to save bankBranch info
     */

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            //set a map object to send all information to caller
            GrailsParameterMap params = (GrailsParameterMap) parameters
            //only admin can create new bankBranch
            boolean hasAccess = appSessionUtil.getAppUser().isPowerUser
            result.put(HAS_ACCESS, hasAccess)
            if (!hasAccess) {
                return result
            }

            //create a bankBranch instance
            BankBranch bankBranchInstance = buildBankBranch(params)

            int count = bankBranchService.countByCode(bankBranchInstance)
            if (count > 0) {
                result.put(Tools.MESSAGE, BANK_BRANCH_CODE_MUST_BE_UNIQUE)
                return result
            }
            if (bankBranchInstance.isPrincipleBranch) {
                BankBranch principleBranch = bankBranchCacheUtility.getPrincipleBankBranch(bankBranchInstance.bankId)
                if (principleBranch) {
                    result.put(Tools.MESSAGE, DUPLICATED_PRINCIPLE_BRANCH)
                    return result
                }
            }
            if (bankBranchInstance.isGlobal.booleanValue()) {
                Map globalBranch = (Map) checkGlobalBranch(bankBranchInstance)
                boolean isGlobalBranch = globalBranch.get(HAS_GLOBAL)
                if (isGlobalBranch.booleanValue()) {
                    result.put(Tools.MESSAGE, globalBranch.get(Tools.MESSAGE))
                    return result
                }
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, bankBranchInstance)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BANK_CREATE_FAILURE_MSG)
            return result
        }
    }

    //implement the execute method of action class
    /*
    if precondition is ok. then save bankBranch info using
    execute method
     */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (Map) obj
            BankBranch objBankBranch = (BankBranch) preResult.get(Tools.ENTITY)
            BankBranch bankBranchServiceReturn = bankBranchService.create(objBankBranch)
            bankBranchCacheUtility.add(bankBranchServiceReturn, bankBranchCacheUtility.DEFAULT_SORT_PROPERTY, bankBranchCacheUtility.SORT_ORDER_ASCENDING)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.ENTITY, objBankBranch)
            return result
        }
        catch (Exception e) {
            log.error(e.message)
            //@todo:rollback
            throw new RuntimeException(BANK_CREATE_FAILURE_MSG)
        }
    }

    //implement the executePostCondition method of action class
    /*
    if execute is ok. then executePostCondition method will be checked
     */

    public Object executePostCondition(Object parameters, Object obj) {
        //there are not post condition
        return null

    }

    //implement the buildSuccessResultForUI method of action class
    /*
    if bankBranch build successfully then initiate success message
     */

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result

        try {

            Map executeResult = (Map) obj
            BankBranch objBankBranch = (BankBranch) executeResult.get(Tools.ENTITY)
            GridEntity objGrid = new GridEntity()
            objGrid.id = objBankBranch.id
            Bank bank = (Bank) bankCacheUtility.read(objBankBranch.bankId)
            District district = (District) districtCacheUtility.read(objBankBranch.districtId)
            objGrid.cell = [Tools.LABEL_NEW, objBankBranch.id, bank.name,
                    district.name, objBankBranch.name,
                    objBankBranch.code ? objBankBranch.code : Tools.EMPTY_SPACE,
                    objBankBranch.address ? objBankBranch.address : Tools.EMPTY_SPACE,
                    objBankBranch.isPrincipleBranch ? Tools.YES : Tools.NO,
                    objBankBranch.isSmeServiceCenter ? Tools.YES : Tools.NO
            ]
            result = [isError: false, entity: objGrid, version: objBankBranch.version, message: BANK_CREATE_SUCCESS_MSG];
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result = [isError: true, entity: obj, version: 0, message: BANK_CREATE_FAILURE_MSG]
            return result
        }
    }

    //implement the buildFailureResultForUI method of action class
    /*
    if bankBranch build failed then initiate failure message
     */

    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                Map preResult = (Map) obj
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, BANK_CREATE_FAILURE_MSG)
            }
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, true)
            result.put(Tools.MESSAGE, BANK_CREATE_FAILURE_MSG)
            return result

        }
    }

    private BankBranch buildBankBranch(GrailsParameterMap params) {
        BankBranch bankBranch = new BankBranch(params)
        bankBranch.companyId = appSessionUtil.getCompanyId()
        bankBranch.createdOn = new Date()
        bankBranch.createdBy = appSessionUtil.getAppUser().id
        bankBranch.updatedOn = null
        bankBranch.updatedBy = 0L
        return bankBranch
    }

    private Map checkGlobalBranch(BankBranch bankBranchInstance) {
        Map result = new LinkedHashMap()
        result.put(HAS_GLOBAL, Boolean.TRUE)
        District district = districtService.read(bankBranchInstance.districtId)
        if (!district.isGlobal.booleanValue()) {
            result.put(Tools.MESSAGE, DISTRICT_IS_NOT_GLOBAL)
            return result
        }
        int count = bankBranchService.countByIsGlobalAndBankIdAndDistrictId(true, bankBranchInstance.bankId, bankBranchInstance.districtId)
        if (count > 0) {
            result.put(Tools.MESSAGE, BRANCH_CAN_NOT_MAKE_GLOBAL)
            return result
        }
        result.put(HAS_GLOBAL, Boolean.FALSE)
        return result
    }
}
