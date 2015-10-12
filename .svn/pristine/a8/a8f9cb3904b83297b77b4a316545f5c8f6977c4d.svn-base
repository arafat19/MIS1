package com.athena.mis.accounting.actions.accchartofaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.utility.AccChartOfAccountCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
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
 *  Show list of chart of account for grid
 *  For details go through Use-Case doc named 'ListAccChartOfAccountActionService'
 */
class ListAccChartOfAccountActionService extends BaseService implements ActionIntf {

    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
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

    private final Logger log = Logger.getLogger(getClass())

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to search chart of account"
    private static final String ACC_CHART_OF_ACCOUNT_LIST = "accChartOfAccountList"
    private static final String COUNT = "count"

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
     * map contains isError(true/false) depending on method success & chart of account object
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            initPager(params)      // initialize parameters for flexGrid

            List<GroovyRowResult> accChartOfAccountList = listChartOfAccount()
            int count = accChartOfAccountCacheUtility.count()

            result.put(ACC_CHART_OF_ACCOUNT_LIST, accChartOfAccountList)
            result.put(COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
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
     * Wrap chart of account list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            Map executeResult = (Map) obj        // cast map returned from execute method
            List<GroovyRowResult> accChartOfAccountList = (List<GroovyRowResult>) executeResult.get(ACC_CHART_OF_ACCOUNT_LIST)
            int count = (int) executeResult.get(COUNT)
            List chartOfAccountList = wrapListInGridEntityList(accChartOfAccountList, start)
            return [page: pageNumber, total: count, rows: chartOfAccountList]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null]
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
     * Wrap list of chart of account in grid entity
     * @param accChartOfAccountList -list of coa object(s)
     * @param start -starting index of the page
     * @return -list of wrapped coa
     */
    private List wrapListInGridEntityList(List<GroovyRowResult> accChartOfAccountList, int start) {
        List accChartOfAccounts = [] as List
        int counter = start + 1

        for (int i = 0; i < accChartOfAccountList.size(); i++) {
            GroovyRowResult eachRow = accChartOfAccountList[i]
            String sourceCategoryName = Tools.EMPTY_SPACE      // Default value
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

    /**
     * List of chart of account
     * @return -GroovyRowResult(list) of chart of account
     */
    private List<GroovyRowResult> listChartOfAccount() {
        String strQuery = """
                SELECT coa.id, coa.code, coa.description, source.key AS source_name, coa.acc_source_id AS source_id,
                       coa.source_category_id, type.name AS type_name, acc_group.name AS group_name,
                       custom_group.name AS custom_group_name, coa.is_active
                FROM acc_chart_of_account coa
                LEFT JOIN system_entity source ON source.id = coa.acc_source_id
                LEFT JOIN acc_type type ON type.id = coa.acc_type_id
                LEFT JOIN acc_custom_group custom_group ON custom_group.id = coa.acc_custom_group_id
                LEFT JOIN acc_group ON acc_group.id = coa.acc_group_id
                WHERE coa.company_id =:companyId
                ORDER BY ${sortColumn} ${sortOrder}
                LIMIT :resultPerPage OFFSET :start
            """
        Map queryParams = [
                companyId: accSessionUtil.appSessionUtil.getAppUser().companyId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> resultList = executeSelectSql(strQuery, queryParams)
        return resultList
    }
    /**
     * Get source category against source id
     * @eachRow - groovy row result of coa object
     * @return - return source category name
     */
    private String getSourceCategoryName(GroovyRowResult eachRow){
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
        return  null
    }
}
