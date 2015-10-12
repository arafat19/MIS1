package com.athena.mis.accounting.actions.accioupurpose

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccIouSlip
import com.athena.mis.accounting.service.AccIouSlipService
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show UI for IOU-Purpose CRUD and list of IOU-Purpose object(s) to show on grid
 *  For details go through Use-Case doc named 'ShowAccIouPurposeActionService'
 */
class ShowAccIouPurposeActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Can not load IOU purpose page"
    private static final String INPUT_VALIDATION_FAIL_ERROR = "Error occurred due to invalid input"
    private static final String ACC_IOU_PURPOSE_LIST = "accIouPurposeList"
    private static final String ACC_IOU_OBJ = "accIouObj"
    private static final String PROJECT_NAME = "projectName"
    private static final String OBJ_NOT_FOUND_MESSAGE = "IOU slip not found"
    private static final String GRID_OUTPUT = "gridOutput"
    private static final String OBJECT_MAP = "objectMap"

    AccIouSlipService accIouSlipService
    @Autowired
    ProjectCacheUtility projectCacheUtility

    /**
     * Check different criteria to show accIouPurpose page
     *      1) Check existence of required parameter
     *      2) Check existing of Parent(AccIouSlip)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map containing accIouSlip object for execute method
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            if (!params.accIouSlipId) {//Check existence of required parameter
                result.put(Tools.MESSAGE, INPUT_VALIDATION_FAIL_ERROR)
                return result
            }

            long accIouSlipId = Long.parseLong(params.accIouSlipId.toString())
            AccIouSlip accIouSlip = accIouSlipService.read(accIouSlipId)
            if (!accIouSlip) {//Checking existing of Parent(AccIouSlip)
                result.put(Tools.MESSAGE, OBJ_NOT_FOUND_MESSAGE)
                return result
            }

            result.put(ACC_IOU_OBJ, accIouSlip)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * get list of accIouPurpose object(s)
     * @param parameters -N/A
     * @param obj -a map containing accIouSlip object from executePreCondition method
     * @return -a map contains accIouPurpose list and count
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map receiveResult = (Map) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            initPager(params)

            AccIouSlip accIouSlip = (AccIouSlip) receiveResult.get(ACC_IOU_OBJ)
            List<GroovyRowResult> accIouPurposeList = []
            int total = 0

            //get list of accIouPurpose
            LinkedHashMap serviceReturn = listByAccIouSlipId(accIouSlip.id)
            accIouPurposeList = (List<GroovyRowResult>) serviceReturn.accIouPurposeList
            total = (int) serviceReturn.count

            Project project = (Project) projectCacheUtility.read(accIouSlip.projectId)

            result.put(ACC_IOU_PURPOSE_LIST, accIouPurposeList)
            result.put(Tools.COUNT, total)
            result.put(ACC_IOU_OBJ, accIouSlip)
            result.put(PROJECT_NAME, project.name)
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
     * do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * wrap-accIouPurpose-list to show on grid
     * @param obj -a map contains accIouPurpose list and count
     * @return -wrapped-accIouPurpose-list to show on grid & itemList(indentDetails) for drop-down
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            AccIouSlip accIouSlip = (AccIouSlip) receiveResult.get(ACC_IOU_OBJ)
            String projectName = receiveResult.get(PROJECT_NAME)
            int count = (int) receiveResult.get(Tools.COUNT)
            List accIouPurposeList = (List) receiveResult.get(ACC_IOU_PURPOSE_LIST)

            List accIouPurposeListWrap = wrapAccIouPurposeListGrid(accIouPurposeList, start)
            Map gridOutput = [page: pageNumber, total: count, rows: accIouPurposeListWrap]

            //get list of indentDetails for drop-down
            List itemList = getItemList(accIouSlip.id)
            itemList = Tools.listForKendoDropdown(itemList, null, null)
            //map contains accIouSlip information to show on level
            Map objectMap = [slipId: accIouSlip.id, slipVersion: accIouSlip.version, indentId: accIouSlip.indentId, projectName: projectName, itemList: itemList]

            result.put(GRID_OUTPUT, gridOutput)
            result.put(OBJECT_MAP, objectMap)
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (receiveResult.get(Tools.MESSAGE)) {
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

    /**
     * wrapped AccIouPurpose object list for grid
     * @param accIouPurposeList -list of GroovyRowResult
     * @param start -start index
     * @return -wrapped AccIouPurpose object list
     */
    private List wrapAccIouPurposeListGrid(List<GroovyRowResult> accIouPurposeList, int start) {
        List lstAccIouPurpose = [] as List
        int counter = start + 1
        GroovyRowResult eachRow
        GridEntity object
        for (int i = 0; i < accIouPurposeList.size(); i++) {
            eachRow = accIouPurposeList[i]
            object = new GridEntity()
            object.id = eachRow.id
            object.cell = [counter,
                    eachRow.id,
                    eachRow.description,
                    eachRow.amount,
                    eachRow.created_by,
                    eachRow.updated_by ? eachRow.updated_by : Tools.EMPTY_SPACE
            ]
            lstAccIouPurpose << object
            counter++
        }
        return lstAccIouPurpose
    }

    private static final String COUNT_QUERY = """
                SELECT COUNT(id) count
                    FROM acc_iou_purpose
                WHERE acc_iou_slip_id =:accIouSlipId
        """

    //@todo: use existing sql for query but move code to corresponding actionService class

    /**
     * get list of Iou-Purpose By Iou-Slip-Id to show on grid
     * @param accIouSlipId -AccIouSlip.Id
     * @return -a map contains IOU-Purpose list and count
     */
    private LinkedHashMap listByAccIouSlipId(long accIouSlipId) {
        String queryStr = """
           SELECT purpose.id AS id, to_char(purpose.amount,'${Tools.DB_CURRENCY_FORMAT}') AS amount,
                     created.username AS created_by,
                     CASE
                       WHEN length(idls.item_description) > 93 THEN
                            rtrim(substr(idls.item_description,0,90)) || '${Tools.THREE_DOTS}'
                       ELSE
                            idls.item_description
                      END AS description,
                updated.username AS updated_by
           FROM acc_iou_purpose purpose
	       LEFT JOIN proc_indent_details idls ON idls.id = purpose.indent_details_id
           LEFT JOIN app_user created ON created.id = purpose.created_by
           LEFT JOIN app_user updated ON updated.id = purpose.updated_by
                WHERE acc_iou_slip_id =:accIouSlipId
           ORDER BY ${sortColumn} ${sortOrder}
           LIMIT :resultPerPage  OFFSET :start
        """

        Map queryParams = [
                accIouSlipId: accIouSlipId,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(COUNT_QUERY, queryParams)

        int total = (int) countResult[0].count
        return [accIouPurposeList: result, count: total]
    }

    /**
     * get list of indentDetails for drop-down
     * @param accIouSlipId -AccIouSlip.Id
     * @return -list of GroovyRowResult
     */
    private List<GroovyRowResult> getItemList(long accIouSlipId) {
        String queryStr = """
            SELECT idls.id,
                CASE
                   WHEN length(idls.item_description) > 25 THEN
                        rtrim(substr(idls.item_description,0,25)) || '${Tools.THREE_DOTS}'
                   ELSE
                        idls.item_description
                  END AS name,
             idls.item_description
            FROM proc_indent_details  idls
            LEFT JOIN acc_iou_slip ais ON ais.indent_id = idls.indent_id
            WHERE ais.id =:accIouSlipId
            AND idls.id NOT IN (SELECT indent_details_id FROM acc_iou_purpose WHERE acc_iou_slip_id =:accIouSlipId)
            """
        Map queryParams = [
                accIouSlipId: accIouSlipId
        ]
        List itemList = executeSelectSql(queryStr, queryParams)
        return itemList
    }
}
