package com.athena.mis.application.actions.bank

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.Bank
import com.athena.mis.application.service.BankService
import com.athena.mis.application.utility.BankCacheUtility
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DeleteBankActionService extends BaseService implements ActionIntf {
    private final Logger log = Logger.getLogger(getClass())

    private static String BANK_DELETE_SUCCESS_MSG = "Bank has been successfully deleted!"
    private static String BANK_DELETE_FAILURE_MSG = "Bank has not been deleted."
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to delete bank"

    //call bank service
    BankService bankService

    @Autowired
    BankCacheUtility bankCacheUtility
    @Autowired(required = false)
    ExchangeHousePluginConnector exhImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService

    /**
     * Checking pre condition and association before deleting the bank object
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)     // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.id) {            // check required parameters
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long bankId = Long.parseLong(params.id.toString())
            Bank bank = (Bank) bankCacheUtility.read(bankId)

            if (!bank) {        // check whether selected bank object exists or not
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }

            Map associationResult = isAssociated(bank)    // check association of bank with relevant domains
            Boolean hasAssociation = (Boolean) associationResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, associationResult.get(Tools.MESSAGE))
                return result
            }
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
     * Delete Bank object from DB and cache utility
     * This function is in transactional boundary and will roll back in case of any exception
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long bankId = Long.parseLong(parameterMap.id.toString())
            bankService.delete(bankId)    // delete bank object from DB
            Bank bank = (Bank) bankCacheUtility.read(bankId)
            bankCacheUtility.delete(bank.id)  // delete bank object from cache utility
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        //there are not post condition
        return null

    }

    /**
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        result.put(Tools.DELETED, Boolean.TRUE)
        result.put(Tools.MESSAGE, BANK_DELETE_SUCCESS_MSG)
        return result
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
                result.put(Tools.MESSAGE, BANK_DELETE_FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, BANK_DELETE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Check association of Bank with relevant domains
     * @param bank -bank object
     * @return -a map containing hasAssociation(true/false) depending on association and relevant message
     */
    private Map isAssociated(Bank bank) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)
        long bankId = bank.id
        String bankName = bank.name
        int count=0
        if(exhImplService){
            count= countTask(bankId)
            if(count.intValue()>0){
                result.put(Tools.MESSAGE,Tools.getMessageOfAssociation(bankName,count,Tools.DOMAIN_TASK))
                return result
            }
        }
        if(armsImplService){
            count=countRmsTask(bankId)
            if(count.intValue()>0){
                result.put(Tools.MESSAGE,Tools.getMessageOfAssociation(bankName,count,Tools.DOMAIN_TASK))
                return result
            }
            count=countRmsPurchaseInstrumentMapping(bankId)
            if(count.intValue()>0){
                result.put(Tools.MESSAGE,Tools.getMessageOfAssociation(bankName,count,Tools.DOMAIN_TASK))
                return result
            }
        }

        count = countBranch(bankId)                                      // hasBranch
        if (count.intValue() > 0) {
            result.put(Tools.MESSAGE, Tools.getMessageOfAssociation(bankName, count, Tools.DOMAIN_BANK_BRANCH))
            return result
        }
        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    //count number of row in branch table by bank id
    private static final String QUERY_COUNT_BRANCH = """
            SELECT COUNT(id) as count
            FROM bank_branch
            WHERE bank_id = :bankId
        """

    private int countBranch(long bankId) {
        List bankCount = executeSelectSql(QUERY_COUNT_BRANCH, [bankId: bankId]);
        int count = bankCount[0].count;
        return count;
    }

    //count number of rows in task table by bank id
    private static final String QUERY_COUNT_TASK = """
            SELECT COUNT(id) as count
            FROM exh_task
            WHERE outlet_bank_id = :bankId
        """

    private int countTask(long bankId){
        List taskCount=executeSelectSql(QUERY_COUNT_TASK,[bankId:bankId])
        int count=taskCount[0].count
        return count

    }

    //count number of rows in rms_task table by bank id
    private static final String QUERY_COUNT_RMS_TASK = """
            SELECT COUNT(id) as count
            FROM rms_task
            WHERE mapping_bank_id = :bankId
        """
    private int countRmsTask(long bankId){
        List taskCount=executeSelectSql(QUERY_COUNT_RMS_TASK,[bankId:bankId])
        int count=taskCount[0].count
        return count
    }
    private static final String QUERY_COUNT_RMS_PURCHASE_INSTRUMENT_MAPPING = """
            SELECT COUNT(id) as count
            FROM rms_purchase_instrument_mapping
            WHERE bank_id = :bankId
        """
    private int countRmsPurchaseInstrumentMapping(long bankId){
        List taskCount=executeSelectSql(QUERY_COUNT_RMS_PURCHASE_INSTRUMENT_MAPPING,[bankId:bankId])
        int count=taskCount[0].count
        return count
    }

}
