package com.athena.mis.accounting.actions.report.projectfundflow

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccGroupCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class ListForProjectFundFlowActionService extends BaseService implements ActionIntf {

    private static final String FAILURE_MSG = "Fail to load project fund flow report"
    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String GRID_OBJ_PROJECT_FUND = "gridObj"
    private static final String NOT_FOUND = "Project fund flow not found"
    private static final String PROJECT_FUND_FLOW_LIST_WRAP = "projectFundFlowListWrap"
    private static final String SORT_NAME = "ag.name"
    private static final String SORT_ORDER = "ASC"

    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccGroupCacheUtility accGroupCacheUtility

    private Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.fromDate || !params.toDate) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            Date startDate = DateUtility.parseMaskedDate(params.fromDate.toString())
            Date endDate = DateUtility.parseMaskedDate(params.toDate.toString())
            AccFinancialYear accFinancialYear = accFinancialYearCacheUtility.getCurrentFinancialYear()
            if (accFinancialYear) {
                String exceedsFinancialYear = DateUtility.checkFinancialDateRange(startDate, endDate, accFinancialYear)
                if (exceedsFinancialYear) {
                    result.put(Tools.MESSAGE, exceedsFinancialYear)
                    return result
                }
            }
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    /**
     * do nothing for post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (!params.rp) {
                params.rp = 20
                params.page = 1
                params.sortname = SORT_NAME
                params.sortorder = SORT_ORDER

            }

            initPager(params)

            Date fromDate = DateUtility.parseMaskedDate(params.fromDate.toString())
            Date toDate = DateUtility.parseMaskedDate(params.toDate.toString())

            List<Long> projectIds = [] //main list of projectIds
            if (params.projectId.equals(Tools.EMPTY_SPACE)) {
                List<Long> tempProjectIdList = accSessionUtil.appSessionUtil.getUserProjectIds()
                if (tempProjectIdList.size() <= 0) {
                    //if tempList is null then set 0 at main list, So that cache don't update
                    projectIds << new Long(0)
                } else {  //if tempList is not null then set tempProjectIdList at main list
                    projectIds = tempProjectIdList
                }
            } else {
                long projectId = Long.parseLong(params.projectId.toString())
                projectIds << new Long(projectId)
            }

            /*if postedByParam  = 0 the show Only Posted Voucher
              if postedByParam  = -1 the show both Posted & Un posted Voucher*/
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }

            LinkedHashMap serviceReturn = getProjectFundFlow(toDate, fromDate, postedByParam, projectIds)
            List<GroovyRowResult> projectFundFlowList = serviceReturn.projectFundFlowList
            if (projectFundFlowList.size() <= 0) {
                result.put(Tools.MESSAGE, NOT_FOUND)
                return result
            }
            int count = serviceReturn.count
            List wrapProjectFundFlowList = wrapProjectFundFlowList(projectFundFlowList, start)

            result.put(PROJECT_FUND_FLOW_LIST_WRAP, wrapProjectFundFlowList)
            result.put(Tools.COUNT, count)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        }
        catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List wrapProjectFundFlowList = (List) executeResult.get(PROJECT_FUND_FLOW_LIST_WRAP)
            int count = (int) executeResult.get(Tools.COUNT)
            Map gridObjProjectFundFlow = [page: pageNumber, total: count, rows: wrapProjectFundFlowList]
            result.put(GRID_OBJ_PROJECT_FUND, gridObjProjectFundFlow)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.message) {
                result.put(Tools.MESSAGE, executeResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, FAILURE_MSG)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, FAILURE_MSG)
            return result
        }
    }

    private List wrapProjectFundFlowList(List<GroovyRowResult> projectFundFlowList, int start) {
        List lstProjectFund = [] as List

        int counter = start + 1
        GroovyRowResult projectFundFlow
        GridEntity obj

        for (int i = 0; i < projectFundFlowList.size(); i++) {
            projectFundFlow = projectFundFlowList[i]
            obj = new GridEntity()
            obj.id = counter
            obj.cell = [
                    counter,
                    projectFundFlow.group_name,
                    projectFundFlow.coa_code,
                    projectFundFlow.opening_balance,
                    projectFundFlow.dr_balance,
                    projectFundFlow.cr_balance,
                    projectFundFlow.closing_balance
            ]
            lstProjectFund << obj
            counter++
        }
        return lstProjectFund
    }

    private LinkedHashMap getProjectFundFlow(Date toDate, Date fromDate, long postedByParam, List projectIds) {

        List accGroupIds = accGroupCacheUtility.listOfActiveGroupIds()
        String lstProject = Tools.buildCommaSeparatedStringOfIds(projectIds)
        String lstAccGroup = Tools.buildCommaSeparatedStringOfIds(accGroupIds)

        long companyId = accSessionUtil.appSessionUtil.getCompanyId()


        String queryStr = """
            SELECT coa.id, coa.code AS coa_code, ag.name AS group_name,
                to_char(coalesce(previous_balance.balance,0),'FM৳ 99,99,999,99,99,990.0099') AS opening_balance,
                to_char(coalesce(SUM(avd.amount_dr),0),'FM৳ 99,99,999,99,99,990.0099') AS dr_balance,
                to_char(coalesce(SUM(avd.amount_cr),0),'FM৳ 99,99,999,99,99,990.0099') AS cr_balance,
                to_char((coalesce(SUM(avd.amount_dr-avd.amount_cr),0)+coalesce(previous_balance.balance,0)),'FM৳ 99,99,999,99,99,990.0099') AS closing_balance

            FROM acc_voucher_details avd

                LEFT JOIN
                (
                    SELECT avd.coa_id, SUM(avd.amount_dr-avd.amount_cr) AS balance
                    FROM acc_voucher_details avd
                    LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
                    LEFT JOIN acc_chart_of_account coa on coa.id = avd.coa_id
                    WHERE coa.acc_group_id IN (${lstAccGroup})
                    AND av.voucher_date < :fromDate
                    AND av.company_id = :companyId
                    AND av.posted_by > :postedByParam
                    AND av.project_id IN (${lstProject})
                    GROUP BY avd.coa_id
                ) AS previous_balance
                    ON previous_balance.coa_id = avd.coa_id

                LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
                LEFT JOIN acc_chart_of_account coa ON coa.id = avd.coa_id
                LEFT JOIN acc_group ag ON ag.id = coa.acc_group_id
            WHERE coa.acc_group_id IN (${lstAccGroup})
                AND av.voucher_date BETWEEN :fromDate AND :toDate
                AND av.company_id = :companyId
                AND av.posted_by > :postedByParam
                AND av.project_id IN (${lstProject})
                GROUP BY ag.name, coa.id, coa.code, previous_balance.balance
            ORDER BY ag.name ASC
            LIMIT :resultPerPage OFFSET :start
        """

        String queryCount = """
            SELECT count(DISTINCT avd.coa_id)
            FROM acc_voucher_details avd
            LEFT JOIN acc_voucher av ON av.id = avd.voucher_id
            LEFT JOIN acc_chart_of_account coa ON coa.id = avd.coa_id
            LEFT JOIN acc_group ag ON ag.id = coa.acc_group_id
            WHERE av.voucher_date BETWEEN :fromDate AND :toDate
            AND avd.group_id IN (${lstAccGroup})
            AND av.company_id = :companyId
            AND av.posted_by > :postedByParam
            AND av.project_id IN (${lstProject})
        """

        Map queryParams = [
                toDate: DateUtility.getSqlDate(toDate),
                fromDate: DateUtility.getSqlDate(fromDate),
                companyId: companyId,
                postedByParam: postedByParam,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> projectFundFlowList = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(queryCount, queryParams)
        int count = countResults[0].count
        return [projectFundFlowList: projectFundFlowList, count: count]
    }
}
