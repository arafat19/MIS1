package com.athena.mis.accounting.actions.accsubaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccSubAccount
import com.athena.mis.accounting.entity.AccVoucherDetails
import com.athena.mis.accounting.service.AccSubAccountService
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.accounting.utility.AccSubAccountCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update AccSubAccount object and show on grid list
 *  For details go through Use-Case doc named 'UpdateAccSubAccountActionService'
 */
class UpdateAccSubAccountActionService extends BaseService implements ActionIntf {

    private static final String FAILURE_MESSAGE = "Sub account could not be updated"
    private static final String SUCCESS_MESSAGE = "Sub account has been updated successfully"
    private static final String SUB_ACCOUNT = "accSubAccount"
    private static final String ERROR_CHART_OF_ACCOUNT = "Chart of account code is invalid"
    private static final String ALREADY_EXISTS = "Same sub account already exists"
    private static final String OBJ_NOT_FOUND = "Selected sub account not exists"
    private static final String HAS_ASSOCIATION_MESSAGE = " voucher(s) are associated with selected sub-account"

    private final Logger log = Logger.getLogger(getClass())

    AccSubAccountService accSubAccountService
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    /**
     * Check different criteria for updating accSubAccount object
     *      1) Check existence of old AccSubAccount object
     *      2) If Chart of Account Code is changed then :
     *              -Check existence of given AccChartOfAccount object
     *              -Check association of old AccSubAccount object with AccVoucher
     *      3) Check existence of duplicate accSubAccount object
     *      4) Validate accSubAccount object
     * @param params -parameters send from UI
     * @param obj -AccSubAccount object send from controller
     * @return -a map containing AccSubAccount object for execute method
     * map -contains isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            AccSubAccount newSubAccount = (AccSubAccount) obj

            AccSubAccount oldSubAccount = (AccSubAccount) accSubAccountCacheUtility.read(newSubAccount.id)
            if (!oldSubAccount) {  //check existence of oldSubAccount object
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND)
                return result
            }

            AccChartOfAccount oldChartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(oldSubAccount.coaId)

            if (oldChartOfAccount.code.equals(parameterMap.code)) {//If Chart of Account Code don't change
                newSubAccount.coaId = oldChartOfAccount.id
            } else { //If Chart of Account Code is changed
                AccChartOfAccount accChartOfAccount = accChartOfAccountCacheUtility.readByCode(parameterMap.code.toString())
                if (!accChartOfAccount) {//Check existence of given AccChartOfAccount object
                    result.put(Tools.MESSAGE, ERROR_CHART_OF_ACCOUNT)
                    return result
                }

                //Check association with AccVoucher
                Map hasAssociationMap = (Map) hasAssociation(oldSubAccount)
                Boolean hasAssociation = (Boolean) hasAssociationMap.get(Tools.HAS_ASSOCIATION)
                if (hasAssociation.booleanValue()) {
                    result.put(Tools.MESSAGE, hasAssociationMap.get(Tools.MESSAGE))
                    return result
                }
                newSubAccount.coaId = accChartOfAccount.id
            }

            AccSubAccount existingObj = accSubAccountCacheUtility.readByDescriptionAndCoaIdForUpdate(newSubAccount.id, newSubAccount.description, newSubAccount.coaId)
            if (existingObj) {//Check existence of duplicate accSubAccount object
                result.put(Tools.MESSAGE, ALREADY_EXISTS)
                return result
            }

            newSubAccount.updatedOn = new Date()
            newSubAccount.updatedBy = accSessionUtil.appSessionUtil.getAppUser().id
            newSubAccount.createdOn = oldSubAccount.createdOn
            newSubAccount.createdBy = oldSubAccount.createdBy
            newSubAccount.companyId = oldSubAccount.companyId

            //Validate accSubAccount object
            newSubAccount.validate()
            if (newSubAccount.hasErrors()) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }

            result.put(SUB_ACCOUNT, newSubAccount)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update accSubAccount object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -accSubAccountObject send from execute method
     * @return -updated accSubAccount object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccSubAccount accSubAccount = (AccSubAccount) preResult.get(SUB_ACCOUNT)
            //Update in DB
            AccSubAccount newAccSubAccount = accSubAccountService.update(accSubAccount)
            //Update in cache and keep the data sorted
            accSubAccountCacheUtility.update(newAccSubAccount, accSubAccountCacheUtility.SORT_ON_DESCRIPTION, accSubAccountCacheUtility.SORT_ORDER_ASCENDING)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap updated accSubAccount object to show on grid
     * @param obj -updated accSubAccount object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result = new LinkedHashMap()
            AccSubAccount subAccount = (AccSubAccount) obj
            GridEntity object = new GridEntity()
            AccChartOfAccount coa = (AccChartOfAccount) accChartOfAccountCacheUtility.read(subAccount.coaId)
            object.id = subAccount.id
            object.cell = [
                    Tools.LABEL_NEW,
                    subAccount.id,
                    subAccount.description,
                    coa.code,
                    subAccount.isActive ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: subAccount.version]
            result.put(Tools.MESSAGE, SUCCESS_MESSAGE)
            result.put(SUB_ACCOUNT, resultMap)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
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
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Check association with accVoucher
     * @param accSubAccount -AccSubAccount
     * @return -a map contains booleanValue(true/false) and association message
     *          based on existence of association
     */
    private LinkedHashMap hasAssociation(AccSubAccount accSubAccount) {
        LinkedHashMap result = new LinkedHashMap()
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        long accSubAccountId = accSubAccount.id
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity accSourceTypeSubAccount = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT, companyId)
        int count = AccVoucherDetails.countBySourceTypeIdAndSourceId(accSourceTypeSubAccount.id, accSubAccountId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE)
            return result
        }
        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }
}
