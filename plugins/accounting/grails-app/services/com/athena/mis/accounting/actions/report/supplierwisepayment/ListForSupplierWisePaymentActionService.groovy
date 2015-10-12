package com.athena.mis.accounting.actions.report.supplierwisepayment

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.config.AccSysConfigurationCacheUtility
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.model.AccSupplierPaymentModel
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * List of all suppliers and their payment details.
 * For details go through Use-Case doc named 'ListForSupplierWisePaymentActionService'
 */
class ListForSupplierWisePaymentActionService extends BaseService implements ActionIntf {

    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    SessionFactory sessionFactory
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate supplier wise payment report"
    private static final String SUPPLIER_PAYMENT_LIST = "supplierPaymentList"
    private static final String SUPPLIER_PAYMENT_NOT_FOUND = "Payment for supplier not found within given dates"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String COUNT = "count"
    private static final String PAID_TOTAL = "paidTotal"
    private static final String SUPPLIER_ID = "supplierId"
    private static final String SOURCE_ID = "sourceId"
    private static final String COMPANY_ID = "companyId"
    private static final String PROJECT_ID = "projectId"
    private static final String PROJECT_ID_LIST = "projectIdList"
    private static final String POSTED_BY = "postedBy"
    private static final String RESULT_PER_PAGE = "resultPerPage"
    private static final String START = "start"

    private Logger log = Logger.getLogger(getClass())

    /**
     * Check pre-conditions for input data
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return - a map containing isError msg(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if ((!params.supplierId) || (!params.fromDate) || (!params.toDate)) {
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
    /**
     * Get supplier wise payment list
     * @param parameters - serialized parameters from UI
     * @param obj -N/A
     * @return - a map containing supplier wise payment for grid and error msg(True/False)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }
            initPager(parameterMap)

            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())
            long supplierId = Long.parseLong(parameterMap.supplierId.toString())
            long projectId = 0L
            List<Long> lstProjectIds = []
            if (parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {
                lstProjectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
            } else {
                projectId = Long.parseLong(parameterMap.projectId.toString())
                lstProjectIds << new Long(projectId)
            }
            /*if postedByParam  = 0 the show Only Posted Voucher
              if postedByParam  = -1 the show both Posted & Unposted Voucher*/
            long postedByParam = new Long(-1)
            SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
            if (sysConfiguration) {
                postedByParam = Long.parseLong(sysConfiguration.value)
            }

            LinkedHashMap serviceReturn = getSupplierWisePaidList(supplierId, lstProjectIds, fromDate, postedByParam, toDate)
            List<AccSupplierPaymentModel> supplierPaymentList = serviceReturn.supplierWisePaymentList
            int count = serviceReturn.count
            if (supplierPaymentList.size() <= 0) {
                result.put(Tools.MESSAGE, SUPPLIER_PAYMENT_NOT_FOUND)
                return result
            }

            String paidTotal = getTotalSupplierPaidAmountBySupplierAndDateRange(supplierId, lstProjectIds, fromDate, toDate, postedByParam)
            List supplierPaymentListWrap = wrapSupplierPaymentListInGridEntityList(supplierPaymentList, start)

            result.put(SUPPLIER_PAYMENT_LIST, supplierPaymentListWrap)
            result.put(COUNT, count)
            result.put(PAID_TOTAL, paidTotal)
            result.put(SUPPLIER_ID, supplierId)
            result.put(PROJECT_ID, projectId)
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(TO_DATE, DateUtility.getDateForUI(toDate))
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
     * Get supplier wise payment
     * @param obj -received parameters from execute method
     * @return - a map containing wrapped supplier payment list,supplier id,project id & date range  for grid and error msg(True/False)
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List supplierPoListWrap = (List) executeResult.get(SUPPLIER_PAYMENT_LIST)
            int count = (int) executeResult.get(COUNT)
            String paidTotal = executeResult.get(PAID_TOTAL)

            Map gridOutput = [page: pageNumber, total: count, rows: supplierPoListWrap]
            result.put(SUPPLIER_PAYMENT_LIST, gridOutput)
            result.put(PAID_TOTAL, paidTotal)
            result.put(SUPPLIER_ID, executeResult.get(SUPPLIER_ID))
            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
            result.put(FROM_DATE, executeResult.get(FROM_DATE))
            result.put(TO_DATE, executeResult.get(TO_DATE))
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
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj

            result.put(Tools.IS_ERROR, executeResult.get(Tools.IS_ERROR))
            if (executeResult.get(Tools.MESSAGE)) {
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
    /**
     * Wrap supplier payment list for grid
     * @param supplierPaymentList -supplier payment list received from execute method
     * @start - starting index for page
     * @return -a map containing all objects necessary for grid view
     */
    private List wrapSupplierPaymentListInGridEntityList(List<GroovyRowResult> supplierPaymentList, int start) {
        List lstSupplierPayment = [] as List

        int counter = start + 1
        GroovyRowResult supplierPayment
        GridEntity obj
        String totalPaidStr
        String totalPoStr
        String remaining
        for (int i = 0; i < supplierPaymentList.size(); i++) {
            supplierPayment = supplierPaymentList[i]
            obj = new GridEntity()
            obj.id = supplierPayment.id
            totalPaidStr = Tools.makeAmountWithThousandSeparator(supplierPayment.total_paid)
            totalPoStr = Tools.makeAmountWithThousandSeparator(supplierPayment.total_po)
            remaining = Tools.makeAmountWithThousandSeparator(supplierPayment.remaining)
            Project project = (Project) projectCacheUtility.read(supplierPayment.po_project_id)
            obj.cell = [
                    counter,
                    (supplierPayment.id > 0 ? supplierPayment.id : Tools.NONE),
                    project ? project.name : Tools.NONE, totalPoStr,
                    totalPaidStr, remaining
            ]
            lstSupplierPayment << obj
            counter++
        }
        return lstSupplierPayment
    }
    /**
     * Get total supplier paid amount
     * @param supplierId - supplier id
     * @param lstProjectIds - list of project ids
     * @param fromVoucherDate - start date
     * @param toVoucherDate - current date
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @return -total paid amount against supplier
     */
    private String getTotalSupplierPaidAmountBySupplierAndDateRange(long supplierId, List<Long> lstProjectIds, Date fromVoucherDate, Date toVoucherDate, long postedByParam) {
        String projectIdList = Tools.buildCommaSeparatedStringOfIds(lstProjectIds)
        String queryStr = """
            SELECT to_char(SUM(total_paid),'${Tools.DB_CURRENCY_FORMAT}') AS str_total_paid
            FROM vw_acc_supplier_payment
            WHERE voucher_date BETWEEN :fromVoucherDate AND :toVoucherDate
            AND source_id = :supplierId
            AND posted_by > :postedByParam
            AND company_id = :companyId
            AND project_id IN (${projectIdList})
        """
        Map queryParams = [
                fromVoucherDate: DateUtility.getSqlDateWithSeconds(fromVoucherDate),
                toVoucherDate: DateUtility.getSqlDateWithSeconds(toVoucherDate),
                supplierId: supplierId,
                postedByParam: postedByParam,
                companyId: accSessionUtil.appSessionUtil.getAppUser().companyId
        ]
        List<GroovyRowResult> paidTotal = executeSelectSql(queryStr,queryParams)
        return paidTotal[0].str_total_paid ? paidTotal[0].str_total_paid : Tools.EMPTY_SPACE
    }
    /**
     * Get supplier wise paid list
     * @param supplierId - supplier id
     * @param lstProjectIds - list of project ids
     * @param fromVoucherDate - start date
     * @param toVoucherDate - current date
     * @param postedByParam - determines whether fetch posted voucher or both posted & un-posted voucher
     * @return -supplier wise paid list
     */
    private LinkedHashMap getSupplierWisePaidList(long supplierId, List<Long> lstProjectIds, Date fromDate, long postedParam, Date toDate) {
        String projectIdList = Tools.buildCommaSeparatedStringOfIds(lstProjectIds)
        String queryStr = """
            SELECT id, po_project_id, total_po, COALESCE(SUM(total_paid),0) total_paid,
            total_po-COALESCE(SUM(total_paid),0) remaining, source_id
            FROM vw_acc_supplier_payment
            WHERE voucher_date BETWEEN :fromDate AND :toDate
            AND source_id = :sourceId
            AND posted_by > :postedBy
            AND company_id = :companyId
            AND project_id IN (${projectIdList})
            GROUP BY id,po_project_id, total_po, source_id
            ORDER BY id
            LIMIT :resultPerPage  OFFSET :start
        """

        long companyId = accSessionUtil.appSessionUtil.getAppUser().companyId
     /*   Session session = sessionFactory.currentSession
        SQLQuery query = session.createSQLQuery(queryStr)
        query.addEntity(AccSupplierPaymentModel.class)
        query.setLong(SOURCE_ID, supplierId)
        query.setLong(POSTED_BY, postedParam)
        query.setLong(COMPANY_ID, companyId)
        query.setParameterList(PROJECT_ID_LIST, lstProjectIds)
        query.setDate(FROM_DATE, fromDate)
        query.setDate(TO_DATE, toDate)
        query.setInteger(RESULT_PER_PAGE, resultPerPage)
        query.setInteger(START, start)
        List<AccSupplierPaymentModel> accSupplierPaymentModelList = query.list()*/
        Map queryParams = [
                sourceId: supplierId,
                fromDate: DateUtility.getSqlDateWithSeconds(fromDate),
                toDate: DateUtility.getSqlDateWithSeconds(toDate),
                postedBy: postedParam,
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]

        List accSupplierPaymentModelList = executeSelectSql(queryStr, queryParams)

        String queryCount = """
                    SELECT COUNT(DISTINCT id)
                    FROM vw_acc_supplier_payment
                    WHERE voucher_date BETWEEN :fromDate AND :toDate
                    AND source_id = :sourceId
                    AND posted_by > :postedBy
                    AND company_id = :companyId
                    AND project_id IN (${projectIdList})
        """
        Map queryParam = [
                fromDate: DateUtility.getSqlDateWithSeconds(fromDate),
                toDate: DateUtility.getSqlDateWithSeconds(toDate),
                sourceId: supplierId,
                postedBy: postedParam,
                companyId: companyId
        ]
        List countResults = executeSelectSql(queryCount, queryParam)
        int count = countResults[0].count
        return [supplierWisePaymentList: accSupplierPaymentModelList.size() > 0 ? accSupplierPaymentModelList : [], count: count]
    }
}