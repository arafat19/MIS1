package com.athena.mis.inventory.actions.invproductiondetails

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

// Search Inventory Production Details
class SearchInvProductionDetailsActionService extends BaseService implements ActionIntf {

    @Autowired
    InvSessionUtil invSessionUtil

    protected final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load User Production Details List"

    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initSearch(params)
            long productionLineItemId = Long.parseLong(parameterMap.productionLineItemId.toString())
            Map serviceReturn = (Map) searchByLineItem(productionLineItemId)
            return serviceReturn
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> productionDetailsList = (List<GroovyRowResult>) executeResult.invProductionDetailsList
            int count = (int) executeResult.count
            List productionListWrap = wrapListInGridEntityList(productionDetailsList, start)
            result = [page: pageNumber, total: count, rows: productionListWrap]
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result = [page: pageNumber, total: 0, rows: null, isError: Boolean.TRUE, message: DEFAULT_ERROR_MESSAGE]
            return result
        }
    }

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
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    private List wrapListInGridEntityList(List<GroovyRowResult> productionDetailsList, int start) {
        List productionDetails = [] as List
        int counter = start + 1
        for (int i = 0; i < productionDetailsList.size(); i++) {
            GroovyRowResult singleRow = productionDetailsList[i]
            GridEntity obj = new GridEntity()
            obj.id = singleRow.id
            obj.cell = [counter,
                    singleRow.id,
                    singleRow.type_name,
                    singleRow.material_name,
                    Tools.makeAmountWithThousandSeparator(Double.parseDouble(singleRow.overhead_cost.toString()))
            ]
            productionDetails << obj
            counter++
        }
        return productionDetails
    }

    private static final String COUNT_QUERY = """
        SELECT
        COUNT(details.id) count
        FROM inv_production_details details
        LEFT JOIN item material ON material.id = details.material_id
        WHERE
        material.name ilike :name
        AND details.production_line_item_id = :productionLineItemId
        AND details.company_id=:companyId
    """

    // search by line Item
    private Map searchByLineItem(long productionLineItemId) {
        String queryStr = """
                SELECT
                item.name item_name,
                type.key type_name,
                material.name material_name,
                details.id, details.overhead_cost
                FROM inv_production_details details
                LEFT JOIN inv_production_line_item item ON item.id = details.production_line_item_id
                LEFT JOIN system_entity type ON type.id = details.production_item_type_id
                LEFT JOIN item material ON material.id = details.material_id
                WHERE
                 material.name ilike :name
                 AND details.production_line_item_id = :productionLineItemId
                 AND details.company_id =:companyId
                ORDER BY ${sortColumn} ${sortOrder} LIMIT ${resultPerPage} OFFSET ${start}
        """
        Map queryParams = [
                name: Tools.PERCENTAGE + query + Tools.PERCENTAGE,
                productionLineItemId: productionLineItemId,
                companyId: invSessionUtil.appSessionUtil.getCompanyId()
        ]


        List<GroovyRowResult> lstInvProductionDetails = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> resultCount = executeSelectSql(COUNT_QUERY, queryParams)
        int count = (int) resultCount[0][0]
        return [invProductionDetailsList: lstInvProductionDetails, count: count]
    }
}