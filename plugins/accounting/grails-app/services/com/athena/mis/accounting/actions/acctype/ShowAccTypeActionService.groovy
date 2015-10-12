package com.athena.mis.accounting.actions.acctype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccType
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for account type (accType) CRUD and list of  account type (accType) for grid
 *  For details go through Use-Case doc named 'ShowAccTypeActionService'
 */
class ShowAccTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AccTypeCacheUtility accTypeCacheUtility

    private static final String PAGE_LOAD_ERROR_MESSAGE = "Failed to load account type page"
    private static final String ACC_TYPE_LIST = "accTypeList"

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get accType list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)                // initialize parameters for flexGrid
            int count = accTypeCacheUtility.count()   // get count of total  account type (accType)
            List accTypeList = accTypeCacheUtility.list(this)   // get sub list of  account type (accType)
            result.put(ACC_TYPE_LIST, accTypeList)
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
     * Wrap  account type (accType) list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for show page
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj     // cast map returned from execute method

            List accTypeList = (List) executeResult.get(ACC_TYPE_LIST)

            int count = (int) executeResult.get(Tools.COUNT)
            List lstWrappedAccType = wrapListInGridEntityList(accTypeList, start)
            Map gridObj = [page: pageNumber, total: count, rows: lstWrappedAccType]
            result.put(ACC_TYPE_LIST, gridObj)
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
                LinkedHashMap preResult = (LinkedHashMap) obj    // cast map returned from previous method
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
     * Wrap list of  account type (accType) in grid entity
     * @param accTypeList -list of accType object(s)
     * @param start -starting index of the page
     * @return -list of wrapped  account type (accType)(s)
     */
    private List wrapListInGridEntityList(List<AccType> accTypeList, int start) {
        List lstAccType = []
        int counter = start + 1
        for (int i = 0; i < accTypeList.size(); i++) {
            AccType accType = accTypeList[i]
            String description = Tools.makeDetailsShort(accType.description, Tools.DEFAULT_LENGTH_DETAILS_OF_SYS_CONFIG)
            GridEntity obj = new GridEntity()
            obj.id = accType.id
            obj.cell = [
                    counter,
                    accType.id,
                    accType.systemAccountType,
                    accType.name,
                    accType.orderId,
                    accType.prefix,
                    description
            ]
            lstAccType << obj
            counter++
        }
        return lstAccType
    }
}
