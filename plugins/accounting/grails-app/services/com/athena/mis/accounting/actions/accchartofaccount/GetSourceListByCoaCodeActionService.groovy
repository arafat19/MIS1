package com.athena.mis.accounting.actions.accchartofaccount

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccChartOfAccount
import com.athena.mis.accounting.utility.*
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.CustomerCacheUtility
import com.athena.mis.application.utility.EmployeeCacheUtility
import com.athena.mis.application.utility.ItemCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Get source list by chart of account id and
 *  used to populate source drop-down against coa code in COA UI(pay/receive-cash/bank & journal)
 *  For details go through Use-Case doc named 'GetSourceListByCoaCodeActionService'
 */
class GetSourceListByCoaCodeActionService extends BaseService implements ActionIntf {

    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    CustomerCacheUtility customerCacheUtility
    @Autowired
    AccIpcCacheUtility accIpcCacheUtility
    @Autowired
    AccLcCacheUtility accLcCacheUtility
    @Autowired
    AccLeaseAccountCacheUtility accLeaseAccountCacheUtility

    private static final String ACC_CHART_OF_ACCOUNT = "coa"
    private static final String SOURCE_LIST = "sourceList"
    private static final String ACCOUNT_NOT_FOUND = "Account not found"
    private static final String FAILURE_MESSAGE = "Failed to search account code"
    private static final String NAME = "name"
    private static final String DESCRIPTION = "description"
    private static final String LC_NO = "lcNo"
    private static final String IPC_NO = "ipcNo"
    private static final String INSTITUTION = "institution"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * Get parameters from UI
     * @Params parameters -serialized parameters from UI
     * @Params obj -N/A
     * @Return -Map containing isError(true/false)
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            if (!parameters.code) {
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
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }
    /**
     * Get parameters from UI
     * @Params parameters -serialized parameters from UI
     * @Params obj -N/A
     * @Return -Map containing isError(true/false) & source category list
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            String code = parameters.code.toString()
            AccChartOfAccount accChartOfAccount = accChartOfAccountCacheUtility.readByCode(code)
            if (!accChartOfAccount) {
                result.put(Tools.MESSAGE, ACCOUNT_NOT_FOUND)
                return result
            }
            result.put(ACC_CHART_OF_ACCOUNT, accChartOfAccount)
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
     * Get source list by source category
     * @param obj -map returned from execute method
     * @return -a map containing source list & chart of account object
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map preResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            AccChartOfAccount accChartOfAccount = (AccChartOfAccount) preResult.get(ACC_CHART_OF_ACCOUNT)
            List sourceList = getSourceListBySourceCategoryId(accChartOfAccount)
            result.put(SOURCE_LIST, sourceList)
            result.put(ACC_CHART_OF_ACCOUNT, accChartOfAccount)
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
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj.message) {
                Map previousResult = (Map) obj
                result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            }
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MESSAGE)
            return result
        }
    }
    /**
     * Get source list by source category
     * @param coa -chart of account object
     * @return -a list containing source name
     */
    private List getSourceListBySourceCategoryId(AccChartOfAccount coa) {
        List sourceList = []
        List tmp
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(coa.accSourceId)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT:
                tmp = accSubAccountCacheUtility.searchByCoaIdAndCompany(coa.id)
                sourceList = Tools.buildSourceDropDown(tmp, DESCRIPTION)    // list--->  [id, name]
                sourceList = Tools.listForKendoDropdown(sourceList, null, null)
                break
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                long supplierTypeId = coa.sourceCategoryId
                tmp = (supplierTypeId > 0) ? supplierCacheUtility.listBySupplierTypeId(supplierTypeId) : supplierCacheUtility.list()
                sourceList = Tools.buildSourceDropDown(tmp, NAME)
                sourceList = Tools.listForKendoDropdown(sourceList, null, null)
                break
            case accSourceCacheUtility.SOURCE_TYPE_CUSTOMER:
                sourceList = customerCacheUtility.listByCompanyForDropDown()
                sourceList = Tools.listForKendoDropdown(sourceList, null, null)
                break
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                long itemTypeId = coa.sourceCategoryId
                tmp = (itemTypeId > 0) ? itemCacheUtility.listByItemTypeId(itemTypeId) : itemCacheUtility.list()
                sourceList = Tools.buildSourceDropDown(tmp, NAME)
                sourceList = Tools.listForKendoDropdown(sourceList, null, null)
                break
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                long designationId = coa.sourceCategoryId
                sourceList = (designationId > 0) ? employeeCacheUtility.listByDesignationForDropDown(designationId) : employeeCacheUtility.listByCompanyForDropDown()
                sourceList = Tools.listForKendoDropdown(sourceList, null, null)
                break
            case accSourceCacheUtility.SOURCE_TYPE_LC:
                tmp = accLcCacheUtility.list()
                sourceList = Tools.buildSourceDropDown(tmp, LC_NO)
                sourceList = Tools.listForKendoDropdown(sourceList, null, null)
                break
            case accSourceCacheUtility.SOURCE_TYPE_IPC:
                tmp = accIpcCacheUtility.list()
                sourceList = Tools.buildSourceDropDown(tmp, IPC_NO)
                sourceList = Tools.listForKendoDropdown(sourceList, null, null)
                break
            case accSourceCacheUtility.SOURCE_TYPE_LEASE_ACCOUNT:
                tmp = accLeaseAccountCacheUtility.list()
                sourceList = Tools.buildSourceDropDown(tmp, INSTITUTION)
                sourceList = Tools.listForKendoDropdown(sourceList, null, null)
                break
            default:
                break
        }
        return sourceList
    }
}
