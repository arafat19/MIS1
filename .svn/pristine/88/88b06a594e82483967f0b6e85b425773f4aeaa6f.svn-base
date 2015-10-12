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
 *  Class to create AccLeaseAccount object and to show on grid list
 *  For details go through Use-Case doc named 'CreateAccLeaseAccountActionService'
 */
class CreateAccLeaseAccountActionService extends BaseService implements ActionIntf {

    AccLeaseAccountService accLeaseAccountService
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AccLeaseAccountCacheUtility accLeaseAccountCacheUtility

    private static final String CREATE_FAILURE_MSG = "Lease Account has not been saved"
    private static final String CREATE_SUCCESS_MSG = "Lease Account has been successfully saved"
    private static final String DEFAULT_ERROR_MESSAGE = "Can not create Lease Account"
    private static final String ACC_LEASE_ACCOUNT = "accLeaseAccount"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Build accLeaseAccount object to create
     * @param params -parameters send from UI
     * @param obj -N/A
     * @return -a map containing AccLeaseAccount object & isError(true/false) depending on method success
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            //Build accLeaseAccount object to create
            AccLeaseAccount accLeaseAccount = buildAccLeaseAccount(parameterMap)

            result.put(ACC_LEASE_ACCOUNT, accLeaseAccount)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Save AccLeaseAccount object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -a map containing AccLeaseAccount object send from executePreCondition
     * @return -newly created AccLeaseAccount object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            AccLeaseAccount accLeaseAccount = (AccLeaseAccount) receivedResult.get(ACC_LEASE_ACCOUNT)

            // save in DB
            AccLeaseAccount newAccLeaseAccount = accLeaseAccountService.create(accLeaseAccount)
            if (!newAccLeaseAccount) {
                result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
                return result
            }
            // add to cacheUtility and keep the data sorted
            accLeaseAccountCacheUtility.add(newAccLeaseAccount, accLeaseAccountCacheUtility.SORT_BY_INSTITUTION, accLeaseAccountCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_LEASE_ACCOUNT, newAccLeaseAccount)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
     * Wrap newly created AccLeaseAccount to show on grid
     * @param obj -newly created AccLeaseAccount object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            AccLeaseAccount leaseAccount = (AccLeaseAccount) receivedResult.get(ACC_LEASE_ACCOUNT)
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
            result.put(Tools.ENTITY, object)
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MSG)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
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
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * build accLeaseAccount object to create
     * @param params -GrailsParameterMap
     * @return -AccLeaseAccount
     */
    private AccLeaseAccount buildAccLeaseAccount(GrailsParameterMap params) {
        AccLeaseAccount accLeaseAccount = new AccLeaseAccount(params)

        AppUser user = accSessionUtil.appSessionUtil.getAppUser()

        accLeaseAccount.startDate = DateUtility.parseMaskedDate(params.startDate.toString())
        accLeaseAccount.endDate = DateUtility.parseMaskedDate(params.endDate.toString())
        accLeaseAccount.createdOn = new Date()
        accLeaseAccount.createdBy = user.id
        accLeaseAccount.updatedBy = 0L
        accLeaseAccount.updatedOn = null
        accLeaseAccount.companyId = user.companyId

        return accLeaseAccount
    }
}
