package com.athena.mis.accounting.actions.report.accledger

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
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

/*
 * Give Source List for source ledger in the drop down of UI using Source Type Id and Source category id
 * For details go through Use-Case doc named 'GetSourceListBySourceCategoryAndAccSourceIdActionService'
 */
class GetSourceListBySourceCategoryAndAccSourceIdActionService extends BaseService implements ActionIntf {

    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    CustomerCacheUtility customerCacheUtility
    @Autowired
    AccLcCacheUtility accLcCacheUtility
    @Autowired
    AccIpcCacheUtility accIpcCacheUtility
    @Autowired
    AccLeaseAccountCacheUtility accLeaseAccountCacheUtility
    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String SOURCE_LIST = "sourceList"
    private static final String FAILURE_MESSAGE = "Error occurred to get source list"
    private static final String NAME = "name"
    private static final String DESCRIPTION = "description"
    private static final String LC_NO = "lcNo"
    private static final String IPC_NO = "ipcNo"
    private static final String INSTITUTION = "institution"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check input fields from UI
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return Map containing isError(true/false) & relevant message(in case)
     */
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            // check required params
            if ((!parameters.accSourceTypeId)) {
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
     * Give Source List for drop down
     * @param params - serialized parameters from UI
     * @param obj - N/A
     * @return contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameters = (GrailsParameterMap) params
            long accSourceTypeId = Long.parseLong(parameters.accSourceTypeId.toString())
            long sourceCategoryId = parameters.sourceCategoryId.equals(Tools.EMPTY_SPACE)? -1: Long.parseLong(parameters.sourceCategoryId.toString())

            List sourceList = getSourceList(sourceCategoryId, accSourceTypeId) // get source list by sourceCategoryId and accSourceId

            result.put(SOURCE_LIST, Tools.listForKendoDropdown(sourceList,null,"ALL"))
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
     * Give Source List by using sourceCategoryId and accSourceTypeId
     * @param sourceCategoryId - source Category Id from params
     * @param accSourceTypeId -  Source Type Id from params
     * @return
     */
    private List getSourceList(long sourceCategoryId, long accSourceTypeId) {
        List sourceList = []
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(accSourceTypeId)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                sourceList = (sourceCategoryId > 0) ? supplierCacheUtility.listBySupplierTypeId(sourceCategoryId) : []
                sourceList = Tools.buildSourceDropDown(sourceList, NAME)
                break
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                sourceList = (sourceCategoryId > 0) ? itemCacheUtility.listByItemTypeId(sourceCategoryId) : []
                sourceList = Tools.buildSourceDropDown(sourceList, NAME)
                break
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                sourceList = (sourceCategoryId > 0) ? employeeCacheUtility.listByDesignationForDropDown(sourceCategoryId) : []
                break
            case accSourceCacheUtility.SOURCE_TYPE_CUSTOMER:
                sourceList = customerCacheUtility.listByCompanyForDropDown()
                break
            case accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT:
                sourceList = (sourceCategoryId > 0) ? accSubAccountCacheUtility.searchByCoaIdAndCompany(sourceCategoryId) : []
                sourceList = Tools.buildSourceDropDown(sourceList, DESCRIPTION)
                break
            case accSourceCacheUtility.SOURCE_TYPE_LC:
                sourceList = accLcCacheUtility.list()
                sourceList = Tools.buildSourceDropDown(sourceList, LC_NO)
                break
            case accSourceCacheUtility.SOURCE_TYPE_IPC:
                sourceList = accIpcCacheUtility.list()
                sourceList = Tools.buildSourceDropDown(sourceList, IPC_NO)
                break
            case accSourceCacheUtility.SOURCE_TYPE_LEASE_ACCOUNT:
                sourceList = accLeaseAccountCacheUtility.list()
                sourceList = Tools.buildSourceDropDown(sourceList, INSTITUTION)
                break
            default:
                break
        }
        return sourceList
    }
}
