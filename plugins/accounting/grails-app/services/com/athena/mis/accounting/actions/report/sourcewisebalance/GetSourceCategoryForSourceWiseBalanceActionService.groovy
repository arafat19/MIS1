package com.athena.mis.accounting.actions.report.sourcewisebalance

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.SupplierTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class GetSourceCategoryForSourceWiseBalanceActionService extends BaseService implements ActionIntf {

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

    private static final String SOURCE_CATEGORY_LIST = "lstSourceCategory"
    private static final String FAILURE_MESSAGE = "Error occurred to get source ledger list"
    private static final String KEY = "key"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check input fields from UI
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return Map containing isError(true/false) & relevant message(in case)
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            // Check required parameters
            if (!parameters.sourceTypeId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Give Source Category List and source list for drop down
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params

            long accSourceTypeId = Long.parseLong(parameters.sourceTypeId.toString())
            List lstSourceCategory = getSourceCategoryList(accSourceTypeId)

            result.put(SOURCE_CATEGORY_LIST, Tools.listForKendoDropdown(lstSourceCategory,null,"ALL"))
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
     * do nothing for build success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }

    /**
     * Give Source Category List
     * @param accSourceTypeId - Source Type Id from params
     * @return - source category list
     */
    private List getSourceCategoryList(long accSourceTypeId) {
        List lstSourceCategory = []
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(accSourceTypeId)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                lstSourceCategory = supplierTypeCacheUtility.list()
                lstSourceCategory = Tools.buildSourceDropDown(lstSourceCategory, KEY)
                break
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                lstSourceCategory = itemTypeCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                lstSourceCategory = designationCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT:
                lstSourceCategory = customSourceCategoryListForSubAcc()
                break
            default:
                break
        }
        return lstSourceCategory
    }


    private static final String SELECT_SOURCE_CATEGORY_QUERY = """
        SELECT DISTINCT asa.coa_id,
        acoa.description || ' (' || acoa.code || ')' AS coa_head_with_code
        FROM acc_sub_account asa
        LEFT JOIN acc_chart_of_account acoa ON acoa.id = asa.coa_id
        WHERE asa.company_id=:companyId
        ORDER BY coa_head_with_code
        """

    /**
     * Give Custom Source Category List For Sub Account
     * @return - list of custom Source Category
     */
    private List customSourceCategoryListForSubAcc() {
        Map queryParams = [
                companyId: accSessionUtil.appSessionUtil.getAppUser().companyId
        ]
        List<GroovyRowResult> result = executeSelectSql(SELECT_SOURCE_CATEGORY_QUERY, queryParams)
        List lstSourceCategory = []
        for (int i = 0; i < result.size(); i++) {
            long id = result[i].coa_id
            String name = result[i].coa_head_with_code
            lstSourceCategory << [id: id, name: name]
        }
        return lstSourceCategory
    }
}
