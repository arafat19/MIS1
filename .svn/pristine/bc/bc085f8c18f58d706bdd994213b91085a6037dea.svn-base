package com.athena.mis.accounting.actions.accchartofaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.utility.*
import com.athena.mis.application.entity.Designation
import com.athena.mis.application.entity.ItemType
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.SupplierTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for COA CRUD and list of chart of account for grid
 *  For details go through Use-Case doc named 'ShowAccChartOfAccountActionService'
 */
class ShowAccChartOfAccountActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load chart of account page"
    private static final String ACC_CHART_OF_ACCOUNT_LIST = "accChartOfAccountList"

    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccCustomGroupCacheUtility accCustomGroupCacheUtility
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility
    @Autowired
    AccTypeCacheUtility accTypeCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    SupplierTypeCacheUtility supplierTypeCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    DesignationCacheUtility designationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get chart of account list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initPager(params)
            int count = accChartOfAccountCacheUtility.count()
            List<GroovyRowResult> accChartOfAccountList = listChartOfAccount()
            result.put(ACC_CHART_OF_ACCOUNT_LIST, accChartOfAccountList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return null
        }
    }
    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Wrap coa list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj
            List<GroovyRowResult> accChartOfAccountList = (List<GroovyRowResult>) executeResult.get(ACC_CHART_OF_ACCOUNT_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List resultList = wrapListInGridEntityList(accChartOfAccountList, start)
            Map output = [page: pageNumber, total: count, rows: resultList]
            result.put(ACC_CHART_OF_ACCOUNT_LIST, output)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
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
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, PAGE_LOAD_ERROR_MESSAGE)
            return result
        }
    }
    /**
     * Wrap list of coa in grid entity
     * @param accChartOfAccountList -list of coa object(s)
     * @param start -starting index of the page
     * @return -list of wrapped coa
     */
    private List wrapListInGridEntityList(List<GroovyRowResult> accChartOfAccountList, int start) {
        List accChartOfAccounts = [] as List
        int counter = start + 1

        for (int i = 0; i < accChartOfAccountList.size(); i++) {
            GroovyRowResult eachRow = accChartOfAccountList[i]
            String sourceCategoryName = Tools.EMPTY_SPACE
            long sourceCategoryId = (long) eachRow.source_category_id
            if (sourceCategoryId > 0) {
                sourceCategoryName = getSourceCategoryName(eachRow)
            }

            GridEntity obj = new GridEntity()
            obj.id = eachRow.id
            obj.cell = [
                    counter,
                    eachRow.id,
                    eachRow.code,
                    eachRow.description,
                    eachRow.source_name,
                    sourceCategoryName,
                    eachRow.type_name,
                    eachRow.group_name ? eachRow.group_name : Tools.EMPTY_SPACE,
                    eachRow.custom_group_name ? eachRow.custom_group_name : Tools.EMPTY_SPACE,
                    eachRow.is_active ? Tools.YES : Tools.NO
            ]
            accChartOfAccounts << obj
            counter++
        }
        return accChartOfAccounts
    }

    private static final String SELECT_QUERY = """
                SELECT coa.id, coa.code, coa.description, source.key AS source_name,coa.acc_source_id AS source_id,
                       coa.source_category_id, type.name AS type_name, acc_group.name AS group_name,
                       custom_group.name AS custom_group_name, coa.is_active
                FROM acc_chart_of_account coa
                LEFT JOIN system_entity source ON source.id = coa.acc_source_id
                LEFT JOIN acc_type type ON type.id = coa.acc_type_id
                LEFT JOIN acc_custom_group custom_group ON custom_group.id = coa.acc_custom_group_id
                LEFT JOIN acc_group ON acc_group.id = coa.acc_group_id
                WHERE coa.company_id =:companyId
                ORDER BY coa.code
                LIMIT :resultPerPage OFFSET :start
            """
    /**
     *
     * @return - chart of account list
     */
    private List<GroovyRowResult> listChartOfAccount() {
        Map queryParams = [
                companyId: accSessionUtil.appSessionUtil.getAppUser().companyId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> resultList = executeSelectSql(SELECT_QUERY, queryParams)
        return resultList
    }

    /**
     * Get source category against source id
     * @eachRow - groovy row result of coa object
     * @return - return source category name
     */
    private String getSourceCategoryName(GroovyRowResult eachRow) {
        SystemEntity accSourceCategory
        long sourceCategoryId = (long) eachRow.source_category_id
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(eachRow.source_id)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                accSourceCategory = (SystemEntity) supplierTypeCacheUtility.read(sourceCategoryId)
                return accSourceCategory.key
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                ItemType itemType = (ItemType) itemTypeCacheUtility.read(sourceCategoryId)
                return itemType.name
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                Designation designation = (Designation) designationCacheUtility.read(sourceCategoryId)
                return designation.name
            default:
                break
        }
        return null
    }

}
