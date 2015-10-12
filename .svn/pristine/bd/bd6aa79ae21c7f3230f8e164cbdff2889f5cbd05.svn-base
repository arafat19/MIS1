package com.athena.mis.accounting.actions.accchartofaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.entity.AccTier1
import com.athena.mis.accounting.entity.AccTier2
import com.athena.mis.accounting.entity.AccTier3
import com.athena.mis.accounting.utility.*
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.SupplierTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Select chart of account object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectAccChartOfAccountActionService'
 */
class SelectAccChartOfAccountActionService extends BaseService implements ActionIntf {

    private static final String ACC_CHART_OF_ACCOUNT_NOT_FOUND_MASSAGE = "Selected chart of account is not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select chart of account"
    private static final String ACC_TIER1_LIST = "accTier1List"
    private static final String ACC_TIER2_LIST = "accTier2List"
    private static final String ACC_TIER3_LIST = "accTier3List"
    private static final String SOURCE_CATEGORY_LIST = "sourceCategoryList"

    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccTier1CacheUtility accTier1CacheUtility
    @Autowired
    AccTier2CacheUtility accTier2CacheUtility
    @Autowired
    AccTier3CacheUtility accTier3CacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    SupplierTypeCacheUtility supplierTypeCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    DesignationCacheUtility designationCacheUtility

    private final Logger log = Logger.getLogger(getClass())
    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Get chart of account object by id
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long accChartOfAccountId = Long.parseLong(parameterMap.id.toString())
            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(accChartOfAccountId)
            if (accChartOfAccount) {
                List<AccTier1> accTier1List = accTier1CacheUtility.listByAccTypeId(accChartOfAccount.accTypeId)
                List<AccTier2> accTier2List = accTier2CacheUtility.listByAccTier1Id(accChartOfAccount.tier1)
                List<AccTier3> accTier3List = accTier3CacheUtility.listByAccTier2Id(accChartOfAccount.tier2)
                result.put(Tools.ENTITY, accChartOfAccount)
                result.put(ACC_TIER1_LIST, Tools.listForKendoDropdown(accTier1List,null,null))
                result.put(ACC_TIER2_LIST, Tools.listForKendoDropdown(accTier2List,null,null))
                result.put(ACC_TIER3_LIST, Tools.listForKendoDropdown(accTier3List,null,null))

                List sourceCategoryList = getSourceCategoryList(accChartOfAccount.accSourceId)
                result.put(SOURCE_CATEGORY_LIST, Tools.listForKendoDropdown(sourceCategoryList,null,null))
            } else {
                result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_NOT_FOUND_MASSAGE)
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_CHART_OF_ACCOUNT_NOT_FOUND_MASSAGE)
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
     *
     * @param obj- received object
     * @return - same object received form execute method
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj

            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) executeResult.get(Tools.ENTITY)
            List<AccTier1> accTier1List = (List<AccTier1>) executeResult.get(ACC_TIER1_LIST)
            List<AccTier2> accTier2List = (List<AccTier2>) executeResult.get(ACC_TIER2_LIST)
            List<AccTier3> accTier3List = (List<AccTier3>) executeResult.get(ACC_TIER3_LIST)

            List sourceCategoryList = (List) executeResult.get(SOURCE_CATEGORY_LIST)

            result.put(Tools.ENTITY, accChartOfAccount)
            result.put(ACC_TIER1_LIST, accTier1List)
            result.put(ACC_TIER2_LIST, accTier2List)
            result.put(ACC_TIER3_LIST, accTier3List)
            result.put(SOURCE_CATEGORY_LIST, sourceCategoryList)
            result.put(Tools.VERSION, accChartOfAccount.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
    /**
     *
     * @param sourceId -source id
     * @return - source category list
     */
    private List getSourceCategoryList(long sourceId) {
        List sourceCategoryList = []
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(sourceId)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                sourceCategoryList = supplierTypeCacheUtility.list()
                sourceCategoryList = customSourceCategoryList(sourceCategoryList)
                break
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                sourceCategoryList = itemTypeCacheUtility.list()
                break
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                sourceCategoryList = designationCacheUtility.list()
                break
            default:
                break
        }
        return sourceCategoryList
    }
    /**
     *
     * @param sourceCategoryList
     * @return  - source category list as [id, name]
     */
    private List customSourceCategoryList(List sourceCategoryList){
        List lstSourceCategory = []
        for (int i = 0; i < sourceCategoryList.size(); i++) {
            long id = sourceCategoryList[i].id
            String name = sourceCategoryList[i].key
            lstSourceCategory << [id: id, name: name]
        }
        return lstSourceCategory
    }
}
