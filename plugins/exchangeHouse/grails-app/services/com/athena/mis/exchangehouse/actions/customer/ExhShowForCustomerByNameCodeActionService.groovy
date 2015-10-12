package com.athena.mis.exchangehouse.actions.customer

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger

/**
 *  Show UI filter panel for customer search
 *  For details go through Use-Case doc named 'ExhShowForCustomerByNameCodeActionService'
 */
class ExhShowForCustomerByNameCodeActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load Search Customer Page"
    private static final String LST_SEARCH_TYPE = "lstSearchType"
    private static final String DROP_DOWN_FULL_NAME = 'Full Name'
    private static final String DROP_DOWN_ACCOUNT_NO = 'Customer A/C No.'
    private static final String DROP_DOWN_PH_NO = 'Phone Number'
    private static final String DROP_DOWN_PHOTO_ID_NO = 'Photo ID No'

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Build list of search type for dropDown
     * @param params -N/A
     * @param obj -N/A
     * @return -a map containing all objects necessary for UI
     * map -contains isError(true/false) depending on method success
     */
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            List lstSearchType = []
            lstSearchType << [id: 1, name: DROP_DOWN_FULL_NAME]           // dropDown item name
            lstSearchType << [id: 2, name: DROP_DOWN_ACCOUNT_NO]     // dropDown item Customer A/C No.
            lstSearchType << [id: 3, name: DROP_DOWN_PH_NO]           // dropDown item Phone Number
            lstSearchType << [id: 4, name: DROP_DOWN_PHOTO_ID_NO]     // Photo ID No

            result.put(LST_SEARCH_TYPE, Tools.listForKendoDropdown(lstSearchType,null,null))
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
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

    public Object buildSuccessResultForUI(Object obj) {
        return null
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
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            if (receiveResult && receiveResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, receiveResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }
}
