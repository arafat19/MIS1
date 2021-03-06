package com.athena.mis.exchangehouse.actions.sanction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional

class ExhGetSanctionCountForCustomerActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())
    private static final String SANCTION_COUNT_FAILURE_MSG = "Failed to get sanction info"
    private static final String SANCTION_COUNT = "sanctionCount"
    private static final String DUMMY_SQL_STRING = "@#~"

    Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap preResult = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.name) {
                preResult.put(Tools.MESSAGE, SANCTION_COUNT_FAILURE_MSG)
                preResult.put(SANCTION_COUNT, 0)
                return preResult
            }
            preResult.put(Tools.IS_ERROR, Boolean.FALSE)
            return preResult
        } catch (Exception ex) {
            log.error(ex.getMessage())
            preResult.put(Tools.MESSAGE, SANCTION_COUNT_FAILURE_MSG)
            preResult.put(SANCTION_COUNT, 0)
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)
            return preResult
        }
    }

    Object executePostCondition(Object parameters, Object obj) {
        return null  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            String customerName = params.name
            int count = listMatchNamesForCustomer(customerName)
            result.put(SANCTION_COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.MESSAGE, SANCTION_COUNT_FAILURE_MSG)
            result.put(SANCTION_COUNT, 0)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            return result
        }
    }

    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap successResultMap = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            successResultMap = [isError: executeResult.get(Tools.IS_ERROR), count: executeResult.get(SANCTION_COUNT), message: executeResult.get(Tools.MESSAGE)]
            return successResultMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            successResultMap.put(Tools.MESSAGE, SANCTION_COUNT_FAILURE_MSG)
            successResultMap.put(Tools.IS_ERROR, Boolean.TRUE)
            return successResultMap
        }
    }

    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap failureResultMap = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            failureResultMap = [isError: executeResult.get(Tools.IS_ERROR), count: executeResult.get(SANCTION_COUNT), message: executeResult.get(Tools.MESSAGE)]
            return failureResultMap
        } catch (Exception ex) {
            log.error(ex.getMessage())
            failureResultMap.put(Tools.MESSAGE, SANCTION_COUNT_FAILURE_MSG)
            failureResultMap.put(Tools.IS_ERROR, Boolean.TRUE)
            return failureResultMap
        }
    }

    // Get All Sanction List to check with customer name
    private int listMatchNamesForCustomer(String customerName) {
        customerName = customerName.trim()
        customerName = customerName.toLowerCase()
        String[] customerNameArray = customerName.split(' ', 3)

        String fName = Tools.EMPTY_SPACE;
        String mName = DUMMY_SQL_STRING;
        String lName = DUMMY_SQL_STRING;

        switch (customerNameArray.size()) {
            case 1:
                fName = customerNameArray[0];
                break;
            case 2:
                fName = customerNameArray[0];
                mName = customerNameArray[1];
                break;
            case 3:
                fName = customerNameArray[0];
                mName = customerNameArray[1];
                lName = customerNameArray[2];
                break;
        }

        String query = """SELECT count(id) as count
                           FROM exh_sanction
                           WHERE
                           (name like :fName_1 AND name like :mName_1) OR
                           (name like :mName_2 AND name like :lName_1) OR
                           (name like :lName_2 AND name like :fName_2)"""

        Map queryParams = [
                fName_1: Tools.PERCENTAGE + fName + Tools.PERCENTAGE,
                fName_2: Tools.PERCENTAGE + fName + Tools.PERCENTAGE,
                mName_1: Tools.PERCENTAGE + mName + Tools.PERCENTAGE,
                mName_2: Tools.PERCENTAGE + mName + Tools.PERCENTAGE,
                lName_1: Tools.PERCENTAGE + lName + Tools.PERCENTAGE,
                lName_2: Tools.PERCENTAGE + lName + Tools.PERCENTAGE
        ]

        List countOfSanctions = executeSelectSql(query, queryParams)
        int count = countOfSanctions[0].count
        return count
    }
}
