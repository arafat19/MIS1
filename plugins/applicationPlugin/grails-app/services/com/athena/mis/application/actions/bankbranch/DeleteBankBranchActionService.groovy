package com.athena.mis.application.actions.bankbranch

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.BankBranchService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.BankBranchCacheUtility
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.exchangehouse.ExchangeHousePluginConnector
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class DeleteBankBranchActionService extends BaseService implements ActionIntf {

    private static final String HAS_ACCESS = "hasAccess"
    private static final String HAS_ASSOCIATION = "hasAssociation"
    private static final String FAILURE_MESSAGE = 'Failed to delete Bank Branch'
    private static final String USER = "User"
    private static String BANK_DELETE_SUCCESS_MSG = "Bank Branch has been successfully deleted"
    private static String BANK_DELETE_FAILURE_MSG = "Sorry! Bank branch has not been deleted"
    public final static String BRANCH_NOT_FOUND_ERROR_MESSAGE = "Bank branch not found";
    private final Logger log = Logger.getLogger(getClass())

    BankBranchService bankBranchService
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired(required = false)
    ExchangeHousePluginConnector exhImplService
    @Autowired(required = false)
    ArmsPluginConnector armsImplService

    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        try {
            Map outputMap = new HashMap()
            boolean hasAccess = appSessionUtil.getAppUser().isPowerUser
            outputMap.put(HAS_ACCESS, hasAccess)
            if (!hasAccess) {
                return outputMap
            }

            BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.read(parameters.id.toLong())
            boolean isError = (bankBranch == null).booleanValue()
            outputMap.put(Tools.IS_ERROR, isError)
            if (isError) {
                outputMap.put(Tools.MESSAGE, BRANCH_NOT_FOUND_ERROR_MESSAGE)
                return outputMap
            }

            outputMap.put(HAS_ASSOCIATION, isAssociated(bankBranch))
            return outputMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return null;
        }
    }

    //implement the execute method of action class
    /*
    if precondition is ok. then save bankBranch info using
    execute method
     */

    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            Long bankBranchId = parameters.id.toLong()
            Boolean result = (Boolean) bankBranchService.delete(bankBranchId)
            if (result.booleanValue()) {
                BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.read(bankBranchId)
                bankBranchCacheUtility.delete(bankBranch.id)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(FAILURE_MESSAGE)
            return Boolean.FALSE
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
        return [deleted: Boolean.TRUE, message: BANK_DELETE_SUCCESS_MSG]
    }

    //implement the buildFailureResultForUI method of action class
    /*
    if bankBranch build failed then initiate failure message
     */

    public Object buildFailureResultForUI(Object obj) {
        // add try catch and add log entry
        LinkedHashMap result
        try {
            if (!obj) {
                return [deleted: Boolean.FALSE, message: BANK_DELETE_FAILURE_MSG]
            }
            return [deleted: Boolean.FALSE, message: obj]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [deleted: Boolean.FALSE, message: BANK_DELETE_FAILURE_MSG]
        }
    }

    private String isAssociated(BankBranch bankBranch) {
        Long bankBranchId = bankBranch.id
        String bankBranchName = bankBranch.name
        //hasArmsUser
        int count = countMappingBranchUser(bankBranchId)
        if (count > 0) {
            String strMessage = Tools.getMessageOfAssociation(bankBranchName, count, USER)
            return strMessage
        }

        //hasTask
        if(exhImplService){
            count = countTask(bankBranchId)
            if (count > 0) {
                String strMessage = Tools.getMessageOfAssociation(bankBranchName, count, Tools.DOMAIN_TASK)
                return strMessage
            }
        }
        if(armsImplService){
            count= countRmsTask(bankBranchId)
            if(count>0){
                String strMessage = Tools.getMessageOfAssociation(bankBranchName, count, Tools.DOMAIN_TASK)
                return strMessage
            }
            count=countRmsPurchaseInstrumentMapping(bankBranchId)
            if(count>0){
                String strMessage = Tools.getMessageOfAssociation(bankBranchName, count, Tools.DOMAIN_TASK)
                return strMessage
            }
        }

        return null;
    }

    //count number of row in branch table by bank id
    private int countMappingBranchUser(Long bankBranchId) {
        SystemEntity appUserSysEntityObject = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(appUserEntityTypeCacheUtility.BANK_BRANCH, appSessionUtil.getCompanyId())
        String query = """
            SELECT COUNT(id) as count
            FROM app_user_entity
            WHERE entity_id =:bankBranchId
            AND entity_type_id =:entityTypeId
        """
        Map queryParams = [
                entityTypeId: appUserSysEntityObject.id,
                bankBranchId: bankBranchId
        ]

        List bankBranchCount = executeSelectSql(query, queryParams)
        int count = bankBranchCount[0].count;
        return count;
    }

    //count number of rows in task table by bank branch id

    private int countTask(long bankBranchId) {
        String queryStrTask = """
            SELECT COUNT(id) as count
            FROM exh_task
            WHERE outlet_branch_id = :bankBranchId
        """
        List taskCount = executeSelectSql(queryStrTask, [bankBranchId: bankBranchId])
        int count = taskCount[0].count
        return count

    }
    private static final String QUERY_COUNT_RMS_BANK_BRANCH="""
        SELECT COUNT(id) as count
        FROM rms_task
        WHERE mapping_branch_id = :bankBranchId
    """
    private int countRmsTask(long bankBranchId) {
        List taskCount = executeSelectSql(QUERY_COUNT_RMS_BANK_BRANCH, [bankBranchId: bankBranchId])
        int count = taskCount[0].count
        return count

    }
    private static final String QUERY_COUNT_RMS_PURCHASE_INSTRUMENT_MAPPING="""
        SELECT COUNT(id) as count
        FROM rms_purchase_instrument_mapping
        WHERE bank_branch_id = :bankBranchId
    """
    private int countRmsPurchaseInstrumentMapping(long bankBranchId){
        List taskCount = executeSelectSql(QUERY_COUNT_RMS_PURCHASE_INSTRUMENT_MAPPING, [bankBranchId: bankBranchId])
        int count = taskCount[0].count
        return count
    }
}
