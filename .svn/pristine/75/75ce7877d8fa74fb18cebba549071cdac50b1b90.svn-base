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
 *  Update bank object and grid data
 *  For details go through Use-Case doc named 'ExhUpdateBankActionService'
 */
class UpdateBankActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass());

    // constants
    private static String BANK_UPDATE_FAILURE_MESSAGE = "Bank could not be updated"
    private static String BANK_UPDATE_SUCCESS_MESSAGE = "Bank has been updated successfully"
    private static final String BANK_NAME_MUST_BE_UNIQUE = "Bank name must be unique"
    private static final String BANK_CODE_MUST_BE_UNIQUE = "Bank code must be unique"
    private static final String OBJ_NOT_FOUND = "Selected bank not found"
    private static final String EXH_BANK = "exhBank"
    private static final String ALREADY_SYSTEM_BANK_EXISTS = "Already system bank exists"

    BankService bankService
    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * 1.Get parameters from UI and build bank object for update
     * 2. check if name duplicate
     * 3. check if code duplicate
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)                            // set default
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            if (!parameterMap.id) {                                             // check required parameters
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            long bankId = Long.parseLong(parameterMap.id.toString())
            Bank oldBank = (Bank) bankService.read(bankId)     // get bank object from cache utitlity

            if (!oldBank) {
                // check whether selected bank object exists or not
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }
            Bank bank = buildBank(parameterMap, oldBank)             // build bank object for update
            if (bankService.countByNameAndIdNotEqual(bank.name, bank.id) > 0) {
                result.put(Tools.MESSAGE, BANK_NAME_MUST_BE_UNIQUE)
                return result
            }
            if (bankService.countByCodeAndIdNotEqual(bank.code, bank.id) > 0) {
                result.put(Tools.MESSAGE, BANK_CODE_MUST_BE_UNIQUE)
                return result
            }
            long companyId= appSessionUtil.getCompanyId()
            if(bank.isSystemBank.booleanValue()){
                if (bankService.countByIsSystemBankAndCompanyIdAndIdNotEqual(true,companyId,bankId) > 0) {
                    result.put(Tools.MESSAGE, ALREADY_SYSTEM_BANK_EXISTS)
                    return result
                }
            }

            result.put(EXH_BANK, bank)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BANK_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update Bank object in DB & update cache utility accordingly
     * This function is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for execute
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap preResult = (LinkedHashMap) obj
            // cast map returned from executePreCondition method
            Bank bank = (Bank) preResult.get(EXH_BANK)
            int updateCount = bankService.update(bank)                // update Bank object in DB
            // update cache utility and keep the data sorted
            bankCacheUtility.update(bank, bankCacheUtility.DEFAULT_SORT_PROPERTY, bankCacheUtility.SORT_ORDER_ASCENDING)
            result.put(EXH_BANK, bank)
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
        return null;
    }

    /**
     * Builds success result map to returned to the UI layer
     * @param obj -Bank instance
     * @return result map wrapped within a grid entity
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj                   // cast map returned from execute method
            Bank bank = (Bank) executeResult.get(EXH_BANK)             // get bank object from executeResult
            GridEntity object = new GridEntity()                                // build grid entity object
            object.id = bank.id
            object.cell = [Tools.LABEL_NEW, bank.id, bank.name, bank.code]
            result.put(Tools.ENTITY, object)
            result.put(Tools.VERSION, bank.version)
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
     * @param obj Object to be used to determine building of UI result
     * @return Object to be used for rendering at UI level
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap objMap = (LinkedHashMap) obj
                result.put(Tools.MESSAGE, objMap.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, BANK_UPDATE_FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BANK_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build Bank object for update
     * @param parameterMap -serialized parameters from UI
     * @param oldBank -old Bank object
     * @return -updated Bank object
     */
    private Bank buildBank(GrailsParameterMap parameterMap, Bank oldBank) {
        Bank newBank = new Bank(parameterMap)
        oldBank.name = newBank.name
        oldBank.code = newBank.code
        oldBank.isSystemBank = newBank.isSystemBank
        oldBank.updatedOn = new Date()
        oldBank.updatedBy = appSessionUtil.getAppUser().id
        return oldBank
    }

}