package com.athena.mis.accounting.actions.accchartofaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccCustomGroup
import com.athena.mis.accounting.entity.AccGroup
import com.athena.mis.accounting.entity.AccType
import com.athena.mis.accounting.service.AccChartOfAccountService
import com.athena.mis.accounting.utility.*
import com.athena.mis.application.entity.Designation
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.SupplierTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.Sql
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Create chart of account & display in the grid
 *  For details go through Use-Case doc named 'CreateAccChartOfAccountActionService'
 */
class CreateAccChartOfAccountActionService extends BaseService implements ActionIntf {

    AccChartOfAccountService accChartOfAccountService
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccCustomGroupCacheUtility accCustomGroupCacheUtility
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

    private static final String ACC_CHART_OF_ACCOUNT_CREATE_FAILURE_MSG = "Chart of account has not been saved"
    private static final String ACC_CHART_OF_ACCOUNT_CREATE_SUCCESS_MSG = "Chart of account has been successfully saved"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to create chart of account"
    private static final String ACC_CHART_OF_ACCOUNT = "accChartOfAccount"
    private static final String CHART_OF_ACCOUNT_NAME_EXISTS = "Chart of Account name already exists within same tier"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Get parameters from UI and build COA object
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -Map containing isError(true/false) & COA object
     */
    @Transactional
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)         // default value
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters

            String headName = parameterMap.description.toString()

            int count = 0
            if (!parameterMap.tier3.equals(Tools.EMPTY_SPACE)) {
                int tier3 = Integer.parseInt(parameterMap.tier3.toString())
                count = AccChartOfAccount.countByTier3AndDescriptionIlike(tier3, headName)
                // check whether given head name already exists or not in tier3
            } else if (!parameterMap.tier2.equals(Tools.EMPTY_SPACE)) {
                int tier2 = Integer.parseInt(parameterMap.tier2.toString())
                count = AccChartOfAccount.countByTier2AndTier3AndDescriptionIlike(tier2, 0, headName)
                // check whether given head name already exists or not in tier2 whose tier3 is 0

            } else if (!parameterMap.tier1.equals(Tools.EMPTY_SPACE)) {
                int tier1 = Integer.parseInt(parameterMap.tier1.toString())
                count = AccChartOfAccount.countByTier1AndTier2AndTier3AndDescriptionIlike(tier1, 0, 0, headName)
                // check whether given head name already exists or not in tier1 whose tier2 & tier3 is 0
            }

            if (count > 0) {
                result.put(Tools.MESSAGE, CHART_OF_ACCOUNT_NAME_EXISTS)
                return result
            }
            AccChartOfAccount accChartOfAccount = buildAccChartOfAccountObject(parameterMap)   // build coa object
            accChartOfAccount.id = getChartOfAccountId()  // get last coa id
            AccType accType = (AccType) accTypeCacheUtility.read(accChartOfAccount.accTypeId)  // get accType object
            long chartOfAccountId = accType.coaCount + 1
            // Code start with A/C Type prefix + coa_count+1 . Must be min 4 char (e.g - A0002)
            accChartOfAccount.code = accType.prefix + chartOfAccountId.toString().padLeft(4, Tools.STR_ZERO)
            updateAccTypeCoaCounter(chartOfAccountId, accType)
            accType.coaCount = chartOfAccountId
            accTypeCacheUtility.update(accType, accTypeCacheUtility.SORT_ON_NAME, accTypeCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_CHART_OF_ACCOUNT, accChartOfAccount)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.HAS_ACCESS, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Save COA object in DB and update cache utility accordingly
     * This method is in transactional block and will roll back in case of any exception
     * @param parameters -N/A
     * @param obj -map returned from executePreCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (LinkedHashMap) obj
            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) preResult.get(ACC_CHART_OF_ACCOUNT)
            AccChartOfAccount newAccChartOfAccount = accChartOfAccountService.create(accChartOfAccount)
            // update cache utility and keep the data sorted
            accChartOfAccountCacheUtility.add(newAccChartOfAccount, accChartOfAccountCacheUtility.SORT_BY_ID, accChartOfAccountCacheUtility.SORT_ORDER_ASCENDING)
            result.put(ACC_CHART_OF_ACCOUNT, newAccChartOfAccount)
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

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Show newly created coa object in grid
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
            Map executeResult = (Map) obj                     // cast map returned from execute method
            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) executeResult.get(ACC_CHART_OF_ACCOUNT)
            accSource = (SystemEntity) accSourceCacheUtility.read(accChartOfAccount.accSourceId)
            String sourceCategoryName = Tools.EMPTY_SPACE      // Default value
            if (accChartOfAccount.sourceCategoryId > 0) {
                sourceCategoryName = getSourceCategoryName(accChartOfAccount)   // get source category name(key)
            }
            accType = (AccType) accTypeCacheUtility.read(accChartOfAccount.accTypeId)
            accGroup = (AccGroup) accGroupCacheUtility.read(accChartOfAccount.accGroupId)
            accCustomGroup = (AccCustomGroup) accCustomGroupCacheUtility.read(accChartOfAccount.accCustomGroupId)
            GridEntity object = new GridEntity()              //build grid object
            object.id = accChartOfAccount.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accChartOfAccount.id,
                    accChartOfAccount.code,
                    accChartOfAccount.description,
                    accSource.key,
                    sourceCategoryName ? sourceCategoryName : Tools.EMPTY_SPACE,
                    accType.name,
                    accGroup ? accGroup.name : Tools.EMPTY_SPACE,
                    accCustomGroup ? accCustomGroup.name : Tools.EMPTY_SPACE,
                    accChartOfAccount.isActive ? Tools.YES : Tools.NO
            ]
            Map resultMap = [entity: object, version: accChartOfAccount.version]

            result.put(ACC_CHART_OF_ACCOUNT, resultMap)
            result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_CREATE_SUCCESS_MSG)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_CREATE_FAILURE_MSG)
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
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build COA object
     * @param params - serialized parameters of UI
     * @return - return newly built object
     */
    private AccChartOfAccount buildAccChartOfAccountObject(GrailsParameterMap params) {
        AccChartOfAccount chartOfAccount = new AccChartOfAccount(params)
        // internally set Tier-1 to Tier-5 (for now)
        chartOfAccount.tier2 = chartOfAccount.tier2 > 0 ? chartOfAccount.tier2 : 0
        chartOfAccount.tier3 = chartOfAccount.tier3 > 0 ? chartOfAccount.tier3 : 0
        chartOfAccount.tier4 = 0
        chartOfAccount.tier5 = 0

        chartOfAccount.accCustomGroupId = chartOfAccount.accCustomGroupId > 0 ? chartOfAccount.accCustomGroupId : 0
        chartOfAccount.accGroupId = chartOfAccount.accGroupId > 0 ? chartOfAccount.accGroupId : 0

        chartOfAccount.sourceCategoryId = chartOfAccount.sourceCategoryId > 0 ? chartOfAccount.sourceCategoryId : 0
        chartOfAccount.createdOn = new Date()
        chartOfAccount.createdBy = accSessionUtil.appSessionUtil.getAppUser().id
        chartOfAccount.companyId = accSessionUtil.appSessionUtil.getCompanyId()

        return chartOfAccount
    }

    private static final String QUERY_NEXTVAL_SEQUENCE = "SELECT NEXTVAL('acc_chart_of_account_id_seq') as id"

    /**
     * Get id from dedicated sequence to generate code
     */
    public long getChartOfAccountId() {
        Sql sql = new Sql(dataSource)
        List results = sql.rows(QUERY_NEXTVAL_SEQUENCE)
        long chartOfAccountId = results[0].id
        return chartOfAccountId
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