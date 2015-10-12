package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.exchangehouse.entity.ExhCustomer
import com.athena.mis.exchangehouse.utility.ExhPhotoIdTypeCacheUtility
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for customer  basic information and list of customer for grid
 *  For details go through Use-Case doc named 'ExhShowCustomerForAdminActionService'
 */
class ExhShowCustomerForAdminActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String FAILURE_MESSAGE = "Fail to load customer page"
    private static final String SORT_COLUMN_NAME = "name"
    private static final String CUSTOMER_LIST_JSON = "customerListJSON"

    @Autowired
    ExhPhotoIdTypeCacheUtility exhPhotoIdTypeCacheUtility
    @Autowired
    ExhSessionUtil exhSessionUtil

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get customer list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * a map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()

        try {
            GrailsParameterMap parameterMap=(GrailsParameterMap) params
            parameterMap.rp = DEFAULT_RESULT_PER_PAGE
            initPager(parameterMap)                                                   // initialize params for flexGrid

            sortColumn = SORT_COLUMN_NAME                                        // set sort column by 'name'
            sortOrder = ASCENDING_SORT_ORDER                                     // set sort column by 'asc'

            List<ExhCustomer> customerList = []
            int count = 0

            LinkedHashMap serviceReturn = listExhCustomer()                      // get list exhCustomer

            customerList = (List<ExhCustomer>) serviceReturn.customerList
            count = (int) serviceReturn.count
            List customers = wrapExhCustomerList(customerList, start)                // wrap customer list for grid
            Map gridOutput = [page: pageNumber, total: 0, rows: []]       // empty list of customer

            result.put(CUSTOMER_LIST_JSON, gridOutput)
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
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * do nothing for buildSuccessResultForUI operation
     */
    public Object buildSuccessResultForUI(Object customerResult) {
        return null
    }

    /**
     * do nothing for buildFailureResultForUI operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /**
     * Wrap list of customer in grid entity
     * @param customerList -list of customer object(s)
     * @param start -starting index of the page
     * @return -list of wrapped customers
     */
    private List wrapExhCustomerList(List<ExhCustomer> customerList, int start) {

        List customers = []
        int counter = start + 1
        for (int i = 0; i < customerList.size(); i++) {
            ExhCustomer customer = customerList[i]
            GridEntity obj = new GridEntity()
            obj.id = customer.id
            String dateOfBirth = DateUtility.getDateFormatAsString(customer.dateOfBirth)
            obj.cell = [counter, customer.code, customer.fullName,
                    dateOfBirth,
                    customer.photoIdTypeId ? exhPhotoIdTypeCacheUtility.read(customer.photoIdTypeId).name : Tools.NONE,
                    customer.photoIdNo ? customer.photoIdNo : Tools.NONE, customer.phone ? customer.phone : Tools.NONE]
            customers << obj
            counter++
        }
        return customers
    }

    /**
     * Get list of customer and count by company
     * */
    private LinkedHashMap listExhCustomer() {
        long companyId = exhSessionUtil.appSessionUtil.getCompanyId()
        List<ExhCustomer> customerList = ExhCustomer.findAllByCompanyId(companyId, [max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        int count = ExhCustomer.countByCompanyId(companyId)
        return [customerList: customerList, count: count]
    }
}
