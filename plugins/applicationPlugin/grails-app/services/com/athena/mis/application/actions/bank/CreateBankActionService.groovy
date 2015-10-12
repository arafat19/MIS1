package com.athena.mis.application.actions.bank

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.service.BankService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create new bank object and show in grid
 *  For details go through Use-Case doc named 'CreateBankActionService'
 */
class CreateBankActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass());

    private static final String BANK = "exhBank"
    private static final String BANK_CREATE_SUCCESS_MSG = "Bank has been successfully saved!"
    private static final String BANK_CREATE_FAILURE_MSG = "Failed to save bank"
    private static final String BANK_NAME_MUST_BE_UNIQUE = "Bank name must be unique"
    private static final String BANK_CODE_MUST_BE_UNIQUE = "Bank code must be unique"
    private static final String SYSTEM_BANK_ALREADY_EXISTS = "System bank already exists"

    BankService bankService

    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * 1. Get parameters from UI and build bank object
     * 2. check if name duplicate
     * 3. check if code duplicate
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all object necessary for execute
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            Bank newBank  = buildBankObject(parameterMap)   // build initialize bank object

            if (bankService.countByName(newBank.name) > 0) {
                result.put(Tools.MESSAGE, BANK_NAME_MUST_BE_UNIQUE)
                return result
            }
            if (bankService.countByCode(newBank.code) > 0) {
                result.put(Tools.MESSAGE, BANK_CODE_MUST_BE_UNIQUE)
                return result
            }
            long companyId= appSessionUtil.getCompanyId()
            if(newBank.isSystemBank.booleanValue()){
                if(bankService.countByIsSystemBankAndCompanyId(true,companyId)>0){
                    result.put(Tools.MESSAGE,SYSTEM_BANK_ALREADY_EXISTS)
                    return result
                }
            }
            result.put(BANK, newBank)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BANK_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Save bank object in DB and update bankCacheUtility accordingly
     * This method is in transactional boundary and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -a Bank obj returned from controller
     * @return -a bank object necessary for buildSuccessResultForUI
     */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            // cast map returned from executePreCondition method
            Bank bank = (Bank) preResult.get(BANK)
            Bank bankObj = bankService.create(bank)      // save new Bank object in DB
            // add new Bank object in cache utility and keep the data sorted
            bankCacheUtility.add(bankObj, bankCacheUtility.DEFAULT_SORT_PROPERTY, bankCacheUtility.SORT_ORDER_ASCENDING)
            result.put(BANK, bankObj)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(BANK_CREATE_FAILURE_MSG)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BANK_CREATE_FAILURE_MSG)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null

    }

    /**
     * Show newly created bank object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for show
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj           // cast map returned from execute method
            Bank bank = (Bank) executeResult.get(BANK)
            GridEntity gridEntityObj = new GridEntity()                       //build grid object
            gridEntityObj.id = bank.id
            gridEntityObj.cell = [
                    Tools.LABEL_NEW,
                    bank.id,
                    bank.name,
                    bank.code
            ]
            result.put(Tools.ENTITY, gridEntityObj)
            result.put(Tools.MESSAGE, BANK_CREATE_SUCCESS_MSG)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BANK_CREATE_FAILURE_MSG)
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
                LinkedHashMap objMap = (LinkedHashMap) obj
                result.put(Tools.MESSAGE, objMap.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, BANK_CREATE_FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BANK_CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build Bank object
     * @param parameterMap -serialized parameters from UI
     * @return -new Bank object
     */
    private Bank buildBankObject(GrailsParameterMap parameterMap) {
        Bank bank = new Bank(parameterMap)
        bank.companyId = appSessionUtil.getCompanyId()
        bank.createdOn = new Date()
        bank.createdBy = appSessionUtil.getAppUser().id
        bank.updatedOn = null
        bank.updatedBy = 0L
        return bank
    }
}
