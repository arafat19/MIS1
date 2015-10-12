package com.athena.mis.accounting.actions.accleaseaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccLeaseAccount
import com.athena.mis.accounting.service.AccLeaseAccountService
import com.athena.mis.accounting.utility.AccLeaseAccountCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Item
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to update AccLeaseAccount object and to show on grid list
 *  For details go through Use-Case doc named 'UpdateAccLeaseAccountActionService'
 */
class UpdateAccLeaseAccountActionService extends BaseService implements ActionIntf {

    AccLeaseAccountService accLeaseAccountService
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccLeaseAccountCacheUtility accLeaseAccountCacheUtility

    private final Logger log = Logger.getLogger(getClass())

    private static final String UPDATE_FAILURE_MESSAGE = "Lease Account could not be updated"
    private static final String UPDATE_SUCCESS_MESSAGE = "Lease Account has been updated successfully"
    private static final String ACC_LEASE_ACCOUNT = "accLeaseAccount"
    private static final String NOT_FOUND_MASSAGE = "Selected Lease Account is not found"

    /**
     * Check existence of old leaseAccount object and build accLeaseAccount object to update
     * @param params -parameters send from UI
     * @param obj -N/A
     * @return -a map containing AccLeaseAccount object & isError(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            long accLeaseId = Long.parseLong(parameterMap.id.toString())
            AccLeaseAccount oldAccLeaseObject = accLeaseAccountService.read(accLeaseId)
            if (!oldAccLeaseObject) {//Check existence of old leaseAccount object
                result.put(Tools.MESSAGE, NOT_FOUND_MASSAGE)
                return result
            }

            //Build accLeaseAccount object to update
            AccLeaseAccount accLeaseAccount = buildAccLeaseAccountObject(parameterMap, oldAccLeaseObject)

            result.put(ACC_LEASE_ACCOUNT, accLeaseAccount)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * update AccLeaseAccount object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -a map containing AccLeaseAccount object send from executePreCondition
     * @return -Updated AccLeaseAccount object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccLeaseAccount accLeaseAccount = (AccLeaseAccount)preResult.get(ACC_LEASE_ACCOUNT)
            //update in DB
            accLeaseAccountService.update(accLeaseAccount)
            //update in cache and keep the data sorted
            accLeaseAccountCacheUtility.update(accLeaseAccount, accLeaseAccountCacheUtility.SORT_BY_INSTITUTION, accLeaseAccountCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_LEASE_ACCOUNT, accLeaseAccount)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
     * Wrap updated AccLeaseAccount to show on grid
     * @param obj -updated AccLeaseAccount object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            AccLeaseAccount leaseAccount = (AccLeaseAccount) receiveResult.get(ACC_LEASE_ACCOUNT)
            Item item = (Item) itemCacheUtility.read(leaseAccount.itemId)
            GridEntity object = new GridEntity()
            object.id = leaseAccount.id
            object.cell = [
                    Tools.LABEL_NEW,
                    leaseAccount.id,
                    leaseAccount.institution,
                    item.name,
                    Tools.formatAmountWithoutCurrency(leaseAccount.amount),
                    Tools.formatAmountWithoutCurrency(leaseAccount.interestRate),
                    leaseAccount.noOfInstallment,
                    Tools.formatAmountWithoutCurrency(leaseAccount.installmentVolume),
                    DateUtility.getLongDateForUI(leaseAccount.startDate),
                    DateUtility.getLongDateForUI(leaseAccount.endDate)
            ]
            result.put(Tools.MESSAGE, UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.ENTITY, object)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
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
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * build accLeaseAccount object to update
     * @param params -GrailsParameterMap
     * @param oldLeaseAccount -AccLeaseAccount
     * @return -AccLeaseAccount
     */
    private AccLeaseAccount buildAccLeaseAccountObject(GrailsParameterMap parameterMap, AccLeaseAccount oldLeaseAccount) {
        AccLeaseAccount leaseAccount = new AccLeaseAccount(parameterMap)
        AppUser systemUser = accSessionUtil.appSessionUtil.getAppUser()

        leaseAccount.id = oldLeaseAccount.id
        leaseAccount.version = oldLeaseAccount.version
        leaseAccount.updatedBy = systemUser.id
        leaseAccount.updatedOn = new Date()
        leaseAccount.createdOn = oldLeaseAccount.createdOn
        leaseAccount.createdBy = oldLeaseAccount.createdBy
        leaseAccount.companyId = oldLeaseAccount.companyId

        leaseAccount.startDate = DateUtility.parseMaskedDate(parameterMap.startDate.toString())
        leaseAccount.endDate = DateUtility.parseMaskedDate(parameterMap.endDate.toString())

        return leaseAccount
    }
}
