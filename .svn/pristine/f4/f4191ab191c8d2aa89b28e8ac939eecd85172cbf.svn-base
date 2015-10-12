package com.athena.mis.inventory.actions.invinventorytransaction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.inventory.utility.InvSessionUtil
import com.athena.mis.inventory.utility.InvTransactionTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ListUnApprovedConsumptionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    InvTransactionTypeCacheUtility invTransactionTypeCacheUtility
    @Autowired
    InvSessionUtil invSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to populate un-approved Consumption list"
    private static final String UNAPPROVED_LIST = "unApprovedList"
    private static final String GRID_OBJ = "gridObj"
    private static final String ID = "iit.id"

    public Object executePreCondition(Object parameters, Object obj) {
        // Do Nothing
    }

    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            Map serviceReturn = new LinkedHashMap()
            List unApprovedList = []
            int count = 0

            parameterMap.sortname = ID
            if (!parameterMap.rp) {
                parameterMap.rp = 10
                parameterMap.page = 1
            }
            initPager(parameterMap)

            List<Long> inventoryIds = invSessionUtil.getUserInventoryIds()
            if (inventoryIds.size() > 0) {
                serviceReturn = listUnApprovedConsumption(inventoryIds)
                unApprovedList = serviceReturn.unApprovedList
                count = serviceReturn.count
            }

            result.put(UNAPPROVED_LIST, unApprovedList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
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
        Map result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (Map) obj

            List<GroovyRowResult> unApprovedList = (List<GroovyRowResult>) executeResult.get(UNAPPROVED_LIST)
            int count = (int) executeResult.get(Tools.COUNT)
            List gridRows = wrapConsumptionList(unApprovedList, this.start)
            Map gridObj = [page: pageNumber, total: count, rows: gridRows]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_OBJ, gridObj)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
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

    private List wrapConsumptionList(List<GroovyRowResult> unApprovedList, int start) {
        List lstUnapprovedConsumption = []
        int counter = this.start + 1
        for (int i = 0; i < unApprovedList.size(); i++) {
            GroovyRowResult eachRow = unApprovedList[i]
            GridEntity obj = new GridEntity()
            obj.id = eachRow.id
            String inventoryName = eachRow.inventory_type + " : " + eachRow.inventory_name
            obj.cell = [counter,
                    eachRow.id,
                    inventoryName,
                    eachRow.item_count,
                    eachRow.total_pending
            ]
            counter++
            lstUnapprovedConsumption << obj
        }
        return lstUnapprovedConsumption
    }

    // List for Un-approved Consumption for dash board
    private LinkedHashMap listUnApprovedConsumption(List<Long> lstInventoryIds) {
        String inventoryIds = Tools.buildCommaSeparatedStringOfIds(lstInventoryIds)
        // pull transactionType object
        long companyId = invSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity transactionTypeCons = (SystemEntity) invTransactionTypeCacheUtility.readByReservedAndCompany(invTransactionTypeCacheUtility.TRANSACTION_TYPE_CONSUMPTION, companyId)

        String queryStr = """
           SELECT iit.id, se.key AS inventory_type, inventory.name AS inventory_name,
                  iit.item_count, COALESCE(COUNT(iitd.id), 0) AS total_pending
           FROM inv_inventory_transaction iit
                  LEFT JOIN inv_inventory inventory ON inventory.id = iit.inventory_id
                  LEFT JOIN inv_inventory_transaction_details iitd ON iitd.inventory_transaction_id = iit.id
                  LEFT JOIN system_entity se ON se.id = inventory.type_id
           WHERE iit.inventory_id IN (${inventoryIds}) AND
                 iit.transaction_type_id=:transactionTypeId AND
                 iit.budget_id > 0 AND
                 iitd.approved_by = 0
           GROUP BY  iit.id,inventory_type, inventory_name, iit.item_count
           ORDER BY ${sortColumn} ${sortOrder} LIMIT :resultPerPage OFFSET :start
        """

        String queryCount = """
           SELECT COALESCE(COUNT(DISTINCT(iit.id)),0) AS total FROM inv_inventory_transaction iit
                 LEFT JOIN inv_inventory_transaction_details iitd ON iitd.inventory_transaction_id = iit.id
           WHERE iit.inventory_id IN (${inventoryIds}) AND
                 iit.transaction_type_id=:transactionTypeId AND
                 iit.budget_id > 0 AND
                 iitd.approved_by = 0
        """

        Map queryParams = [
                transactionTypeId: transactionTypeCons.id,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List resultCount = executeSelectSql(queryCount, queryParams)
        int count = resultCount[0].total
        return [unApprovedList: result, count: count]
    }
}

