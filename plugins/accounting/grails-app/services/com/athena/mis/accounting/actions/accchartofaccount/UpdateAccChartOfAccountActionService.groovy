package com.athena.mis.accounting.actions.accchartofaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.*
import com.athena.mis.accounting.service.AccChartOfAccountService
import com.athena.mis.accounting.utility.*
import com.athena.mis.application.entity.Designation
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.SupplierTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Update chart of account & display in the grid
 *  For details go through Use-Case doc named 'UpdateAccChartOfAccountActionService'
 */
class UpdateAccChartOfAccountActionService extends BaseService implements ActionIntf {

    // constants
    private static final String ACC_CHART_OF_ACCOUNT_UPDATE_FAILURE_MESSAGE = "Chart of account could not be updated"
    private static
    final String ACC_CHART_OF_ACCOUNT_UPDATE_SUCCESS_MESSAGE = "Chart of account has been updated successfully"
    private static final String ACC_CHART_OF_ACCOUNT = "accChartOfAccount"
    private static final String ACC_CHART_OF_ACCOUNT_NOT_FOUND_MASSAGE = "Selected chart of account not found"
    private static final String CHART_OF_ACCOUNT_NAME_EXISTS = "Chart of Account name already exists within same tier"
    private static final String SOURCE_TYPE_NOT_CHANGEABLE = " associated voucher(s) found. Source type not changeable"
    private static final String SOURCE_CATEGORY_NOT_CHANGEABLE = " associated voucher(s) found. Source category not changeable"
    private static final String UPDATE_VOUCHER_DETAILS = "updateVoucherDetails"

    private final Logger log = Logger.getLogger(getClass())

    AccChartOfAccountService accChartOfAccountService
    @Autowired
    AccCustomGroupCacheUtility accCustomGroupCacheUtility
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    SupplierTypeCacheUtility supplierTypeCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    DesignationCacheUtility designationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    /**
     * Get parameters from UI and build COA object
     * @Params parameters -serialized parameters from UI
     * @Params obj -N/A
     * @Return -Map containing isError(true/false) & COA object
     */
    @Transactional
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            AccChartOfAccount newCOA = buildAccChartOfAccountObject(parameterMap)   // build coa object with new value
            AccChartOfAccount oldCOA = (AccChartOfAccount) accChartOfAccountCacheUtility.read(newCOA.id)
            // previous value of that coa
            if (!oldCOA) {
                result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_NOT_FOUND_MASSAGE)
                return result
            }

            String headName = parameterMap.description.toString()
            long coaId = newCOA.id

            int count = 0
            if (!parameterMap.tier3.equals(Tools.EMPTY_SPACE)) {
                int tier3 = Integer.parseInt(parameterMap.tier3.toString())
                // check whether given head name already exists or not in tier3
                count = AccChartOfAccount.countByTier3AndDescriptionIlikeAndIdNotEqual(tier3, headName, coaId)
            } else if (!parameterMap.tier2.equals(Tools.EMPTY_SPACE)) {
                int tier2 = Integer.parseInt(parameterMap.tier2.toString())
                // check whether given head name already exists or not in tier2 whose tier3 is 0
                count = AccChartOfAccount.countByTier2AndDescriptionIlikeAndIdNotEqualAndTier3(tier2, headName, coaId, 0)
            } else if (!parameterMap.tier1.equals(Tools.EMPTY_SPACE)) {
                int tier1 = Integer.parseInt(parameterMap.tier1.toString())
                // check whether given head name already exists or not in tier1 whose tier2 & tier3 is 0
                count = AccChartOfAccount.countByTier1AndDescriptionIlikeAndIdNotEqualAndTier2AndTier3(tier1, headName, coaId, 0, 0)
            }
            if (count > 0) {
                result.put(Tools.MESSAGE, CHART_OF_ACCOUNT_NAME_EXISTS)
                return result
            }
            /**
             * if account type is not same then coa code is re-create with related A/C type prefix
             */
            if (newCOA.accTypeId != oldCOA.accTypeId) {
                AccType accType = (AccType) accTypeCacheUtility.read(newCOA.accTypeId)
                long chartOfAccountId = accType.coaCount + 1
                newCOA.code = accType.prefix + chartOfAccountId.toString().padLeft(4, Tools.STR_ZERO)  // Code start with A/C Type prefix + Coa.id . Must be min 4 char (e.g - A0002)
                updateAccTypeCoaCounter(chartOfAccountId, accType)
                accType.coaCount = chartOfAccountId
                accTypeCacheUtility.update(accType, accTypeCacheUtility.SORT_ON_NAME, accTypeCacheUtility.SORT_ORDER_ASCENDING)
            }
            /**
             * if source id is changed then check whether it is used in voucher details or not
             * if already used stop editing with a relevant message
             */

            if (newCOA.accSourceId != oldCOA.accSourceId) {
                int voucherCount = AccVoucherDetails.countByCoaId(newCOA.id)
                if (voucherCount > 0) {
                    result.put(Tools.MESSAGE, voucherCount + SOURCE_TYPE_NOT_CHANGEABLE)
                    return result
                }
            }
            /**
             * if source category is changed then check whether it is used in voucher details or not
             * if already used stop editing with a relevant message
             */
            if (newCOA.sourceCategoryId != oldCOA.sourceCategoryId) {
                int voucherCount = AccVoucherDetails.countByCoaId(newCOA.id)
                if (voucherCount > 0) {
                    result.put(Tools.MESSAGE, voucherCount + SOURCE_CATEGORY_NOT_CHANGEABLE)
                    return result
                }
            }

            result.put(UPDATE_VOUCHER_DETAILS, Boolean.FALSE)
            if (newCOA.accGroupId != oldCOA.accGroupId) {
                result.put(UPDATE_VOUCHER_DETAILS, Boolean.TRUE)
            }

            result.put(ACC_CHART_OF_ACCOUNT, newCOA)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Update COA object in DB and update cache utility accordingly
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (LinkedHashMap) obj
            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) preResult.get(ACC_CHART_OF_ACCOUNT)
            Boolean updateVoucherDetails = (Boolean) preResult.get(UPDATE_VOUCHER_DETAILS)
            AccChartOfAccount updatedCOA = accChartOfAccountService.update(accChartOfAccount)
            if (updateVoucherDetails.booleanValue()) {
                updateVoucherDetailsList(updatedCOA.id, updatedCOA.accGroupId)
            }
            accChartOfAccountCacheUtility.update(updatedCOA, accChartOfAccountCacheUtility.SORT_BY_ID, accChartOfAccountCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_CHART_OF_ACCOUNT, updatedCOA)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(ACC_CHART_OF_ACCOUNT_UPDATE_FAILURE_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show newly updated coa object in grid
     * Show success message
     * @param obj -map from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            SystemEntity accSource
            AccType accType
            AccGroup accGroup
            AccCustomGroup accCustomGroup
            Map executeResult = (Map) obj
            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) executeResult.get(ACC_CHART_OF_ACCOUNT)
            accSource = (SystemEntity) accSourceCacheUtility.read(accChartOfAccount.accSourceId)
            String sourceCategoryName = Tools.EMPTY_SPACE    // Default Value
            if (accChartOfAccount.sourceCategoryId > 0) {
                sourceCategoryName = getSourceCategoryName(accChartOfAccount)
            }
            accType = (AccType) accTypeCacheUtility.read(accChartOfAccount.accTypeId)
            accGroup = (AccGroup) accGroupCacheUtility.read(accChartOfAccount.accGroupId)
            accCustomGroup = (AccCustomGroup) accCustomGroupCacheUtility.read(accChartOfAccount.accCustomGroupId)
            GridEntity object = new GridEntity()
            object.id = accChartOfAccount.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accChartOfAccount.id,
                    accChartOfAccount.code,
                    accChartOfAccount.description,
                    accSource.key,
                    sourceCategoryName,
                    accType.name,
                    accGroup ? accGroup.name : Tools.EMPTY_SPACE,
                    accCustomGroup ? accCustomGroup.name : Tools.EMPTY_SPACE,
                    accChartOfAccount.isActive ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: accChartOfAccount.version]
            result.put(ACC_CHART_OF_ACCOUNT, resultMap)
            result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_UPDATE_SUCCESS_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_UPDATE_FAILURE_MESSAGE)
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
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.message) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_UPDATE_FAILURE_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_UPDATE_FAILURE_MESSAGE)
            return result
        }
    }

    private static final String QUERY_UPDATE = """
                UPDATE acc_voucher_details
                 SET group_id = :accGroupId
                WHERE  coa_id = :coaId
    """

    /**
     *
     * @param coaId - chart of account id
     * @param accGroupId - group id
     * @return - int value(1 for success)
     */
    private int updateVoucherDetailsList(long coaId, long accGroupId) {
        Map queryParams = [
                accGroupId: accGroupId,
                coaId: coaId
        ]
        int updateStatus = executeUpdateSql(QUERY_UPDATE, queryParams)
        return updateStatus
    }

    /**
     * Get source category against source id
     * @param accChartOfAccount - coa object
     * @return - return source category name
     */
    private String getSourceCategoryName(AccChartOfAccount accChartOfAccount) {
        SystemEntity accSourceCategory
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(accChartOfAccount.accSourceId)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                accSourceCategory = (SystemEntity) supplierTypeCacheUtility.read(accChartOfAccount.sourceCategoryId)
                return accSourceCategory.key
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                ItemType itemType = (ItemType) itemTypeCacheUtility.read(accChartOfAccount.sourceCategoryId)
                return itemType.name
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                Designation designation = (Designation) designationCacheUtility.read(accChartOfAccount.sourceCategoryId)
                return designation.name
            default:
                break
        }
        return null
    }

    /**
     * Build COA object
     * @param params - serialized parameters of UI
     * @return - return newly built object
     */
    private AccChartOfAccount buildAccChartOfAccountObject(GrailsParameterMap params) {
        AccChartOfAccount chartOfAccount = new AccChartOfAccount(params);
        chartOfAccount.id = Long.parseLong(params.id.toString());
        chartOfAccount.version = Integer.parseInt(params.version.toString());
        // internally set Tier-1 to Tier-5 (for now)
        chartOfAccount.tier2 = chartOfAccount.tier2 > 0 ? chartOfAccount.tier2 : 0
        chartOfAccount.tier3 = chartOfAccount.tier3 > 0 ? chartOfAccount.tier3 : 0
        chartOfAccount.tier4 = 0
        chartOfAccount.tier5 = 0

        chartOfAccount.accCustomGroupId = chartOfAccount.accCustomGroupId > 0 ? chartOfAccount.accCustomGroupId : 0
        chartOfAccount.accGroupId = chartOfAccount.accGroupId > 0 ? chartOfAccount.accGroupId : 0

        chartOfAccount.sourceCategoryId = chartOfAccount.sourceCategoryId > 0 ? chartOfAccount.sourceCategoryId : 0
        chartOfAccount.createdOn = new Date();
        chartOfAccount.createdBy = accSessionUtil.appSessionUtil.getAppUser().id
        chartOfAccount.companyId = accSessionUtil.appSessionUtil.getCompanyId()
        return chartOfAccount
    }

    private Boolean updateAccTypeCoaCounter(long chartOfAccountId, AccType accType) {
        String strQuery = """
                        UPDATE acc_type SET
                        coa_count=:chartOfAccountId
                        WHERE id=:id AND
                        system_account_type='${accType.systemAccountType}' AND
                        company_id=:companyId
    """
        Map queryParams = [
                id: accType.id,
                chartOfAccountId: chartOfAccountId,
                companyId: accType.companyId
        ]

        int updateCount = executeUpdateSql(strQuery, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Acc Type update failed')
        }
        return Boolean.TRUE
    }
}