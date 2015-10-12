package com.athena.mis.application.actions.costingtype

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class SearchCostingTypeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load costing type List"
    private static final String COSTING_TYPE_LIST = "costingTypeList"

    @Autowired
    AppSessionUtil appSessionUtil

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

    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            initSearch(params)

            List<GroovyRowResult> lstOfCostingType = search()
            int count = count()
            result.put(Tools.COUNT, count)
            result.put(COSTING_TYPE_LIST, lstOfCostingType)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> costingTypeList = (List<GroovyRowResult>) executeResult.get(COSTING_TYPE_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List<GroovyRowResult> costingTypeListWrap = wrapListInGridEntityList(costingTypeList, start)
            result = [page: pageNumber, total: count, rows: costingTypeListWrap]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return null
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.message) {
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

    // It will return list of Costing type
    private List<GroovyRowResult> search() {
        String strQuery =
                """
                 SELECT co.id,co.name,co.description
                    FROM costing_type as co
                    WHERE ${queryType} ilike :query
                      AND co.company_id = :companyId
                    LIMIT :resultPerPage OFFSET :start
            """

        Map queryParams = [
                query        : Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                resultPerPage: resultPerPage,
                start        : start,
                companyId    : appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> lstCostingTypeDetails = executeSelectSql(strQuery, queryParams)
        return lstCostingTypeDetails
    }

    // It will return list of Costing type
    private int count() {
        String queryCount =
                """
                SELECT COUNT(co.id) FROM costing_type  co
                    WHERE ${queryType} ilike :query
                         AND co.company_id = :companyId
            """

        Map queryParams = [
                query    : Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                companyId: appSessionUtil.getCompanyId()
        ]
        List<GroovyRowResult> result = executeSelectSql(queryCount, queryParams)
        int count = (int) result[0][0]
        return count
    }

    private List wrapListInGridEntityList(List<GroovyRowResult> costingTypeList, int start) {
        List<GroovyRowResult> lstCostingType = []
        int counter = start + 1
        for (int i = 0; i < costingTypeList.size(); i++) {
            GridEntity obj = new GridEntity()
            GroovyRowResult eachRow = costingTypeList[i]
            obj.id = costingTypeList[i].id
            obj.cell = [
                    counter,
                    eachRow.name,
                    eachRow.description
            ]
            lstCostingType << obj
            counter++
        }
        return lstCostingType
    }
}
