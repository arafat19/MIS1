package com.athena.mis.accounting.actions.acciouslip

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.service.AccIouSlipService
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show list of iou slip for grid
 *  For details go through Use-Case doc named 'ListEmployeeActionService'
 */
class ListAccIouSlipActionService extends BaseService implements ActionIntf {

    AccIouSlipService accIouSlipService
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load IOU slip list"
    private static final String ACC_IOU_SLIP_LIST = "accIouSlipList"
    private static final String COUNT = "count"

    private Logger log = Logger.getLogger(getClass())

    /**
     * do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get iou slip list for grid
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap parameterMap = (GrailsParameterMap) params
            initPager(parameterMap)     // initialize parameters for grid

            List<Long> projectIdList = []  //main list of projectIds
            List<Long> tempProjectIdList = accSessionUtil.appSessionUtil.getUserProjectIds()
            if (tempProjectIdList.size() <= 0) { //if tempList is null then set 0 at main list, So that cache don't update
                projectIdList << new Long(0)
            } else {  //if tempList is not null then set tempProjectIdList at main list
                projectIdList = tempProjectIdList
            }

            LinkedHashMap serviceReturn = list(projectIdList)

            int count = serviceReturn.count
            List accIouSlipList = serviceReturn.accIouSlipList
            result.put(ACC_IOU_SLIP_LIST, accIouSlipList)
            result.put(COUNT, count)
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

    /**
     * Wrap iou slip list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj       // cast map returned from execute method
            List<GroovyRowResult> accIouSlipList = (List<GroovyRowResult>) executeResult.get(ACC_IOU_SLIP_LIST)
            int count = (int) executeResult.get(COUNT)
            List accIouSlipListWrap = wrapAccIouSlipListInGrid(accIouSlipList, start)
            result = [page: pageNumber, total: count, rows: accIouSlipListWrap]
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
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj   // cast map returned from previous method
                if (preResult.get(Tools.MESSAGE)) {
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

    /**
     * Wrap list of iou slip in grid entity
     * @param accIouSlipList -list of iou slip object(s)
     * @param start -starting index of the page
     * @return -list of wrapped iou slip
     */
    private List wrapAccIouSlipListInGrid(List<GroovyRowResult> accIouSlipList, int start) {
        List lstAccIouSlips = []
        int counter = start + 1
        for (int i = 0; i < accIouSlipList.size(); i++) {
            GridEntity obj = new GridEntity()
            obj.id = accIouSlipList[i].id
            obj.cell = [counter,
                    accIouSlipList[i].id,
                    accIouSlipList[i].str_created_on,
                    accIouSlipList[i].employee_name_and_designation,
                    accIouSlipList[i].indent_details,
                    accIouSlipList[i].str_total_purpose_amount,
                    accIouSlipList[i].sent_for_approval ? Tools.YES : Tools.NO,
                    accIouSlipList[i].approved_by_name,
                    accIouSlipList[i].purpose_count,
                    accIouSlipList[i].project_code,
                    accIouSlipList[i].created_by_name
            ]
            lstAccIouSlips << obj
            counter++
        }
        return lstAccIouSlips
    }

    /**
     * Give AccIouSlip List and its count
     * @param projectIdList - list of project id(s)
     * @return - map of accIouSlipList
     */
    //@todo create view
    private LinkedHashMap list(List<Long> projectIdList) {
        String projectIds = Tools.buildCommaSeparatedStringOfIds(projectIdList)

        String queryStr = """
        SELECT ais.id, employee.full_name || ' ( ' || designation.name  || ' ) ' AS employee_name_and_designation,
               ais.indent_id ||' (' || to_char(indt.from_date,'dd-Mon-yyyy') ||' To '|| to_char(indt.to_date,'dd-Mon-yyyy') || ') ' AS indent_details,
               project.code AS project_code, user_approved_by.username AS approved_by_name,
               user_created_by.username AS created_by_name, user_updated_by.username AS updated_by_name,
               ais.sent_for_approval,to_char(ais.created_on,'dd-Mon-yyyy') AS str_created_on,
               to_char(ais.total_purpose_amount,'${Tools.DB_CURRENCY_FORMAT}') AS str_total_purpose_amount, ais.purpose_count
        FROM acc_iou_slip ais
            LEFT JOIN employee ON employee.id = ais.employee_id
            LEFT JOIN designation ON designation.id = employee.designation_id
            LEFT JOIN project ON project.id= ais.project_id
            LEFT JOIN proc_indent indt ON indt.id = ais.indent_id
            LEFT JOIN app_user user_approved_by ON user_approved_by.id=ais.approved_by
            LEFT JOIN app_user user_created_by ON user_created_by.id = ais.created_by
            LEFT JOIN app_user user_updated_by ON user_updated_by.id = ais.updated_by
        WHERE ais.project_id IN (${projectIds})
          AND ais.company_id =:companyId
        ORDER BY ${sortColumn} ${sortOrder}
        LIMIT :resultPerPage  OFFSET :start
        """

        String queryCount = """
        SELECT COUNT(id) count
        FROM acc_iou_slip
        WHERE project_id IN (${projectIds})
          AND company_id =:companyId
        """
        Map queryParams = [
                companyId: accSessionUtil.appSessionUtil.getAppUser().companyId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> result = executeSelectSql(queryStr, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(queryCount, queryParams)
        int total = countResult[0].count
        return [accIouSlipList: result, count: total]
    }
}
