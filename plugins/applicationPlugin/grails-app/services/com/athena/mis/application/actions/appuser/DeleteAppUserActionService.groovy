package com.athena.mis.application.actions.appuser

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.AppUserService
import com.athena.mis.application.service.EntityContentService
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserCacheUtility
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ContentEntityTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Delete appUser object from DB and cache utility and remove it from grid
 *  For details go through Use-Case doc named 'DeleteAppUserActionService'
 */
class DeleteAppUserActionService extends BaseService implements ActionIntf {

    private static final String DELETE_USER_SUCCESS_MESSAGE = "User has been deleted successfully"
    private static final String DELETE_USER_FAILURE_MESSAGE = "User could not be deleted, Please refresh the user list"
    private static final String HAS_ASSOCIATION_USER_ROLE = " user role(s) associated with selected user"
    private static final String HAS_ASSOCIATION_USER_ENTITY = "(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_BUDGET = " budget(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_BUDGET_DETAILS = " budget details(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST = " purchase request(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_PURCHASE_ORDER = " purchase order(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION = " inventory transaction(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_COMPANY = " company(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_EMPLOYEE = " employee(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_PROJECT = " project(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_USER_GROUP = " user-group(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_ACC_CHART_OF_ACCOUNT = " chart of account(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_ACC_DIVISION = " division(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_ACC_FINANCIAL_YEAR = " financial year(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_ACC_IOU_PURPOSE = " iou purpose(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_ACC_IOU_SLIP = " iou slip(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_ACC_SUB_ACCOUNT = " sub account(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_ACC_VOUCHER_DETAILS = " voucher(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_ACC_VOUCHER_TYPE_COA = " voucher type(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_FXD_CATEGORY_MAINTENANCE_TYPE = " category maintenance type mapping(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_FXD_FIXED_ASSET_DETAILS = " fixed asset details associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_FXD_FIXED_ASSET_TRACE = " fixed asset trace(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_FXD_MAINTENANCE = " fixed asset maintenance(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_FXD_MAINTENANCE_TYPE = " fixed asset maintenance type(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_INV_INVENTORY_TRANSACTION_DETAILS = " inventory transaction(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_PROC_INDENT = " indent(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_PROC_INDENT_DETAILS = " indent item(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_PROC_PURCHASE_ORDER_DETAILS = " PO Item(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_PROC_PURCHASE_REQUEST_DETAILS = " PR Item(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_PROC_TERMS_AND_CONDITION = " terms & condition(s) associated with selected user"
    private static final String HAS_ASSOCIATION_MESSAGE_QS_MEASUREMENT = " QS measurement(s) associated with selected user"

    AppUserService appUserService
    EntityContentService entityContentService
    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    ContentEntityTypeCacheUtility contentEntityTypeCacheUtility

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check if user has access to delete appUser or not
     * Get appUser object from cache utility by appUserId
     *      1. check if appUser exists or not
     *      2. check association of appUser with relevant domains
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for execute
     * map contains isError(true/false) and hasAccess(true/false) depending on method success
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)  // default value
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            // only development role type user can delete appUser
            if (!appSessionUtil.getAppUser().isPowerUser) {
                result.put(Tools.HAS_ACCESS, Boolean.FALSE)
                return result
            }
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long appUserId = Long.parseLong(params.id.toString())
            // get appUser object from cache utility by appUserId
            AppUser appUser = (AppUser) appUserCacheUtility.read(appUserId)
            // check if appUser exists or not
            if (!appUser) {
                result.put(Tools.MESSAGE, ENTITY_NOT_FOUND_ERROR_MESSAGE)
                return result
            }
            // check association of appUser with relevant domains
            Map preResult = (Map) hasAssociation(appUser)
            Boolean hasAssociation = (Boolean) preResult.get(Tools.HAS_ASSOCIATION)
            if (hasAssociation.booleanValue()) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                return result
            }

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_USER_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Delete appUser object from DB and cache utility
     * This method is in transactional block and will roll back in case of any exception
     * Delete all images(e.g : Photo, signature, logo etc) of appUser
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -boolean value(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long appUserId = Long.parseLong(params.id.toString())
            AppUser appUser = appUserService.read(appUserId)
            boolean deleteSuccess = appUserService.delete(appUser)  // delete appUser object from DB
            // pull system entity type(AppUser) object
            SystemEntity contentEntityTypeAppUser = (SystemEntity) contentEntityTypeCacheUtility.readByReservedAndCompany(contentEntityTypeCacheUtility.CONTENT_ENTITY_TYPE_APPUSER, appUser.companyId)
            // delete all images(e.g : Photo, signature, logo etc) of this AppUser
            entityContentService.delete(appUserId, contentEntityTypeAppUser.id)
            appUserCacheUtility.delete(appUser.id) // delete appUser object from cache utility
            return new Boolean(deleteSuccess)
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DELETE_USER_FAILURE_MESSAGE)
            return new Boolean(false)
        }
    }

    /**
     * Do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Remove object from grid
     * Show success message
     * @param obj -N/A
     * @return -a map containing success message for UI
     */
    public Object buildSuccessResultForUI(Object obj) {
        return [deleted: Boolean.TRUE.booleanValue(), message: DELETE_USER_SUCCESS_MESSAGE]
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
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DELETE_USER_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DELETE_USER_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Check association of appUser with relevant domains
     *      1. check association with userId of UserRole
     *      2. check association with appUserId of AppUserEntity
     *      3. check association with createdBy and updatedBy of Company
     *      4. check association with createdBy and updatedBy of Employee
     *      5. check association with createdBy and updatedBy of Project
     *      6. check association with createdBy and updatedBy of AppGroup
     * If Budget plugin is installed then
     *      1. check association with createdBy and updatedBy of BudgBudget
     *      2. check association with createdBy and updatedBy of BudgBudgetDetails
     * If Procurement plugin is installed then
     *      1. check association with createdBy, updatedBy, approvedByDirectorId and approvedByProjectDirectorId of ProcPurchaseRequest
     *      2. check association with createdBy, updatedBy, approvedByDirectorId and approvedByProjectDirectorId of ProcPurchaseOrder
     *      3. check association with createdBy and updatedBy of ProcPurchaseRequestDetails
     *      4. check association with createdBy and updatedBy of ProcPurchaseOrderDetails
     *      5. check association with createdBy and updatedBy of ProcIndent
     *      6. check association with createdBy and updatedBy of ProcIndentDetails
     *      7. check association with createdBy and updatedBy of ProcTermsAndCondition
     * If Accounting plugin is installed then
     *      1. check association with createdBy of AccChartOfAccount
     *      2. check association with createdBy and updatedBy of AccDivision
     *      3. check association with createdBy and updatedBy of AccFinancialYear
     *      4. check association with createdBy and updatedBy of AccIouPurpose
     *      5. check association with createdBy and updatedBy of AccIouSlip
     *      6. check association with createdBy and updatedBy of AccSubAccount
     *      7. check association with createdBy of AccVoucherDetails
     *      8. check association with createdBy and updatedBy of AccVoucherTypeCoa
     * If QS plugin is installed then
     *      1. check association with createdBy and updatedBy of QsMeasurement
     * If Inventory plugin is installed then
     *      1. check association with createdBy and updatedBy of InvInventoryTransaction
     *      2. check association with createdBy and updatedBy of InvInventoryTransactionDetails
     * If FixedAsset plugin is installed then
     *      1. check association with createdBy and updatedBy of FxdCategoryMaintenanceType
     *      2. check association with createdBy and updatedBy of FxdFixedAssetDetails
     *      3. check association with createdBy of FxdFixedAssetTrace
     *      4. check association with createdBy and updatedBy of FxdMaintenance
     *      5. check association with createdBy and updatedBy of FxdMaintenanceType
     * @param appUser -object of AppUser
     * @return -a map containing hasAssociation(true/false) and relevant message
     */
    private LinkedHashMap hasAssociation(AppUser appUser) {

        LinkedHashMap result = new LinkedHashMap()
        long appUserId = appUser.id
        int count = 0
        result.put(Tools.HAS_ASSOCIATION, Boolean.TRUE)

        // count user-role mapping associated with user
        count = countUserRole(appUserId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_USER_ROLE)
            return result
        }
        // count user entity mapping associated with user
        String msg = countUserEntity(appUserId)
        if(msg) {
            result.put(Tools.MESSAGE, msg)
            return result
        }
        // count number of company created or updated by user
        count = countCompany(appUserId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_COMPANY)
            return result
        }
        // count number of employee created or updated by user
        count = countEmployee(appUserId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_EMPLOYEE)
            return result
        }
        // count number of project created or updated by user
        count = countProject(appUserId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_PROJECT)
            return result
        }
        // count number of group created or updated by user
        count = countAppGroup(appUserId)
        if (count > 0) {
            result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_USER_GROUP)
            return result
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.BUDGET)) {
            // count number of budget created or updated by user
            count = countBudget(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_BUDGET)
                return result
            }
            // count number of budget details created or updated by user
            count = countBudgetDetails(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_BUDGET_DETAILS)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.PROCUREMENT)) {
            // count number of purchase request created, updated or approved by user
            count = countPurchaseRequest(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_PURCHASE_REQUEST)
                return result
            }
            // count number of purchase order created, updated or approved by user
            count = countPurchaseOrder(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_PURCHASE_ORDER)
                return result
            }
            // count number of indent created or updated by user
            count = countProcIndent(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_PROC_INDENT)
                return result
            }
            // count number of indent details created or updated by user
            count = countProcIndentDetails(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_PROC_INDENT_DETAILS)
                return result
            }
            // count number of purchase order details created or updated by user
            count = countProcPurchaseOrderDetails(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_PROC_PURCHASE_ORDER_DETAILS)
                return result
            }
            // count number of purchase request details created or updated by user
            count = countProcPurchaseRequestDetails(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_PROC_PURCHASE_REQUEST_DETAILS)
                return result
            }
            // count number of terms and condition created or updated by user
            count = countProcTermsAndCondition(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_PROC_TERMS_AND_CONDITION)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
            // count number of chart of account created by user
            count = countAccChartOfAccount(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_CHART_OF_ACCOUNT)
                return result
            }
            // count number of division created or updated by user
            count = countAccDivision(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_DIVISION)
                return result
            }
            // count number of financial year created or updated by user
            count = countAccFinancialYear(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_FINANCIAL_YEAR)
                return result
            }
            // count number of iou purpose created or updated by user
            count = countAccIouPurpose(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_IOU_PURPOSE)
                return result
            }
            // count number of iou slip created or updated by user
            count = countAccIouSlip(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_IOU_SLIP)
                return result
            }
            // count number of sub account created or updated by user
            count = countAccSubAccount(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_SUB_ACCOUNT)
                return result
            }
            // count number of voucher details created by user
            count = countAccVoucherDetails(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_VOUCHER_DETAILS)
                return result
            }
            // count number of voucher type coa created or updated by user
            count = countAccVoucherTypeCoa(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_ACC_VOUCHER_TYPE_COA)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.QS)) {
            // count number of qs measurement created or updated by user
            count = countQsMeasurement(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_QS_MEASUREMENT)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
            // count number of inventory transaction created or updated by user
            count = countInventoryTransaction(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_INVENTORY_TRANSACTION)
                return result
            }
            // count number of inventory transaction details created or updated by user
            count = countInvInventoryTransactionDetails(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_INV_INVENTORY_TRANSACTION_DETAILS)
                return result
            }
        }

        if (PluginConnector.isPluginInstalled(PluginConnector.FIXED_ASSET)) {
            // count number of category maintenance type created or updated by user
            count = countFxdCategoryMaintenanceType(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_FXD_CATEGORY_MAINTENANCE_TYPE)
                return result
            }
            // count number of fixed asset details created or updated by user
            count = countFxdFixedAssetDetails(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_FXD_FIXED_ASSET_DETAILS)
                return result
            }
            // count number of fixed asset trace created by user
            count = countFxdFixedAssetTrace(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_FXD_FIXED_ASSET_TRACE)
                return result
            }
            // count number of fxd maintenance created or updated by user
            count = countFxdMaintenance(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_FXD_MAINTENANCE)
                return result
            }
            // count number of fxd maintenance type created or updated by user
            count = countFxdMaintenanceType(appUserId)
            if (count > 0) {
                result.put(Tools.MESSAGE, count.toString() + HAS_ASSOCIATION_MESSAGE_FXD_MAINTENANCE_TYPE)
                return result
            }
        }

        result.put(Tools.HAS_ASSOCIATION, Boolean.FALSE)
        return result
    }

    private static final String COUNT_USER_ROLE = """
            SELECT COUNT(user_id) AS count
            FROM user_role
            WHERE user_id = :appUserId
    """

    /**
     * Count user-role mapping associated with user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countUserRole(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_USER_ROLE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_USER_ENTITY = """
            SELECT COUNT(aue.app_user_id) AS count, se.key
            FROM app_user_entity aue
            LEFT JOIN system_entity se ON se.id = aue.entity_type_id
            WHERE aue.app_user_id = :appUserId
            GROUP BY se.key
    """

    /**
     * Count user-entity mapping associated with user
     * @param appUserId -id of user
     * @return -a string containing null value or message according to user association
     */
    private String countUserEntity(long appUserId) {
        String msg = null
        Map queryParams = [
                appUserId: appUserId,
        ]
        List results = executeSelectSql(COUNT_USER_ENTITY, queryParams)
        if (results.size() <= 0) {
            return msg
        }
        int count = results[0].count
        String entityType = results[0].key
        msg = count + Tools.SINGLE_SPACE + entityType + HAS_ASSOCIATION_USER_ENTITY
        return msg
    }

    private static final String COUNT_BUDGET = """
            SELECT COUNT(id) AS count
            FROM budg_budget
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of budget created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countBudget(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_BUDGET, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_BUDGET_DETAILS = """
            SELECT COUNT(id) AS count
            FROM budg_budget_details
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of budget details created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countBudgetDetails(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_BUDGET_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PURCHASE_REQUEST = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_request
            WHERE created_by = :appUserId OR updated_by = :appUserId OR
                  approved_by_director_id = :appUserId OR
                  approved_by_project_director_id = :appUserId
    """

    /**
     * Count number of purchase request created, updated or approved by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countPurchaseRequest(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PURCHASE_REQUEST, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PURCHASE_ORDER = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_order
            WHERE created_by = :appUserId OR
                  updated_by = :appUserId OR
                  approved_by_director_id = :appUserId OR
                  approved_by_project_director_id = :appUserId
    """

    /**
     * Count number of purchase order created, updated or approved by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countPurchaseOrder(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PURCHASE_ORDER, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_INVENTORY_TRANSACTION = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of inventory transaction created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countInventoryTransaction(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_INVENTORY_TRANSACTION, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_COMPANY = """
            SELECT COUNT(id) AS count
            FROM company
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of company created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countCompany(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_COMPANY, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_EMPLOYEE = """
            SELECT COUNT(id) AS count
            FROM employee
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of employee created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countEmployee(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_EMPLOYEE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PROJECT = """
            SELECT COUNT(id) AS count
            FROM project
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of project created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countProject(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PROJECT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_APP_GROUP = """
            SELECT COUNT(id) AS count
            FROM app_group
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of group created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAppGroup(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_APP_GROUP, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_CHART_OF_ACCOUNT = """
            SELECT COUNT(id) AS count
            FROM acc_chart_of_account
            WHERE created_by = :appUserId
    """

    /**
     * Count number of chart of account created by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccChartOfAccount(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_CHART_OF_ACCOUNT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_DIVISION = """
            SELECT COUNT(id) AS count
            FROM acc_division
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of division created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccDivision(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_DIVISION, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_FINANCIAL_YEAR = """
            SELECT COUNT(id) AS count
            FROM acc_financial_year
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of financial year created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccFinancialYear(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_FINANCIAL_YEAR, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_IOU_PURPOSE = """
            SELECT COUNT(id) AS count
            FROM acc_iou_purpose
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of iou purpose created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccIouPurpose(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_IOU_PURPOSE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_IOU_SLIP = """
            SELECT COUNT(id) AS count
            FROM acc_iou_slip
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of iou slip created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccIouSlip(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_IOU_SLIP, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_SUB_ACCOUNT = """
            SELECT COUNT(id) AS count
            FROM acc_sub_account
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of sub account created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccSubAccount(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_SUB_ACCOUNT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_VOUCHER_DETAILS = """
            SELECT COUNT(id) AS count
            FROM acc_voucher_details
            WHERE created_by = :appUserId
    """

    /**
     * Count number of voucher details created by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccVoucherDetails(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_VOUCHER_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_ACC_VOUCHER_TYPE_COA = """
            SELECT COUNT(id) AS count
            FROM acc_voucher_type_coa
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of voucher type coa created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countAccVoucherTypeCoa(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_ACC_VOUCHER_TYPE_COA, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_FXD_CATEGORY_MAINTENANCE_TYPE = """
            SELECT COUNT(id) AS count
            FROM fxd_category_maintenance_type
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of category maintenance type created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countFxdCategoryMaintenanceType(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_FXD_CATEGORY_MAINTENANCE_TYPE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_FXD_FIXED_ASSET_DETAILS = """
            SELECT COUNT(id) AS count
            FROM fxd_fixed_asset_details
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of fixed asset details created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countFxdFixedAssetDetails(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_FXD_FIXED_ASSET_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_FXD_FIXED_ASSET_TRACE = """
            SELECT COUNT(id) AS count
            FROM fxd_fixed_asset_trace
            WHERE created_by = :appUserId
    """

    /**
     * Count number of fixed asset trace created by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countFxdFixedAssetTrace(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_FXD_FIXED_ASSET_TRACE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_FXD_MAINTENANCE = """
            SELECT COUNT(id) AS count
            FROM fxd_maintenance
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of fxd maintenance created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countFxdMaintenance(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_FXD_MAINTENANCE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_FXD_MAINTENANCE_TYPE = """
            SELECT COUNT(id) AS count
            FROM fxd_maintenance_type
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of fxd maintenance type created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countFxdMaintenanceType(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_FXD_MAINTENANCE_TYPE, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_INVENTORY_TRANSACTION_DETAILS = """
            SELECT COUNT(id) AS count
            FROM inv_inventory_transaction_details
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of inventory transaction details created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countInvInventoryTransactionDetails(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_INVENTORY_TRANSACTION_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PROC_INDENT = """
            SELECT COUNT(id) AS count
            FROM proc_indent
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of indent created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countProcIndent(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PROC_INDENT, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PROC_INDENT_DETAILS = """
            SELECT COUNT(id) AS count
            FROM proc_indent_details
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of indent details created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countProcIndentDetails(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PROC_INDENT_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PROC_PURCHASE_ORDER_DETAILS = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_order_details
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of purchase order details created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countProcPurchaseOrderDetails(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PROC_PURCHASE_ORDER_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PROC_PURCHASE_REQUEST_DETAILS = """
            SELECT COUNT(id) AS count
            FROM proc_purchase_request_details
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of purchase request details created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countProcPurchaseRequestDetails(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PROC_PURCHASE_REQUEST_DETAILS, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_PROC_TERMS_AND_CONDITION = """
            SELECT COUNT(id) AS count
            FROM proc_terms_and_condition
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of terms and condition created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countProcTermsAndCondition(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_PROC_TERMS_AND_CONDITION, queryParams)
        int count = results[0].count
        return count
    }

    private static final String COUNT_QS_MEASUREMENT = """
            SELECT COUNT(id) AS count
            FROM qs_measurement
            WHERE created_by = :appUserId OR updated_by = :appUserId
    """

    /**
     * Count number of qs measurement created or updated by user
     * @param appUserId -id of user
     * @return -an integer containing the value of count
     */
    private int countQsMeasurement(long appUserId) {
        Map queryParams = [
                appUserId: appUserId
        ]
        List results = executeSelectSql(COUNT_QS_MEASUREMENT, queryParams)
        int count = results[0].count
        return count
    }
}
