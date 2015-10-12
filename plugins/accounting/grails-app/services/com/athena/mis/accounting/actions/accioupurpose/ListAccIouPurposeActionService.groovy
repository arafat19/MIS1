package com.athena.mis.accounting.actions.accioupurpose

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.transaction.annotation.Transactional


/**
 *  Class to get list of IOU-Purpose object(s) to show on grid
 *  For details go through Use-Case doc named 'ListAccIouPurposeActionService'
 */
class ListAccIouPurposeActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Can not load IOU purpose list"
    private static final String ACC_IOU_PURPOSE_LIST = "accIouPurposeList"

    /**
     * do nothing for pre condition
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * get list of accIouPurpose object(s)
     * @param parameters -parameters from UI
     * @param obj -N/A
     * @return -a map contains accIouPurpose list and count
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            initPager(params)


            List<GroovyRowResult> accIouPurposeList = []
            int total = 0

            long accIouSlipId = Long.parseLong(params.slipId.toString())
            if (accIouSlipId > 0) {
                LinkedHashMap serviceReturn = listByAccIouSlipId(accIouSlipId)
                accIouPurposeList = (List<GroovyRowResult>) serviceReturn.accIouPurposeList
                total = (int) serviceReturn.count
            }

            result.put(ACC_IOU_PURPOSE_LIST, accIouPurposeList)
            result.put(Tools.COUNT, total)
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
     * @return -wrapped-accIouPurpose-list to show on grid
     */
    public Object buildSuccessResultForUI(Object obj) {
        try {
            LinkedHashMap receiveResult = (LinkedHashMap) obj
            int count = (int) receiveResult.get(Tools.COUNT)
            List accIouPurposeList = (List) receiveResult.get(ACC_IOU_PURPOSE_LIST)

            List accIouPurposeListWrap = wrapAccIouPurposeListInGrid(accIouPurposeList, start)
            return [page: pageNumber, total: count, rows: accIouPurposeListWrap]
        } catch (Exception ex) {
            log.error(ex.getMessage())
            return [page: pageNumber, total: 0, rows: null, isError: Boolean.TRUE, message: DEFAULT_ERROR_MESSAGE]
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
    private List wrapAccIouPurposeListInGrid(List<GroovyRowResult> accIouPurposeList, int start) {
        List lstAccIouPurposes = [] as List
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
            lstAccIouPurposes << object
            counter++
        }
        return lstAccIouPurposes
    }

    private static final String ACC_IOU_PURPOSE_COUNT_QUERY = """
                SELECT COUNT(id) count
                    FROM acc_iou_purpose
                WHERE acc_iou_slip_id =:accIouSlipId
        """
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
        List<GroovyRowResult> countResult = executeSelectSql(ACC_IOU_PURPOSE_COUNT_QUERY, queryParams)

        int total = (int) countResult[0].count
        return [accIouPurposeList: result, count: total]
    }
}
