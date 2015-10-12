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
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.SessionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Populate project,supplier drop-down & set from and to date according to financial year
 * For details go through Use-Case doc named 'ShowForSupplierWisePaymentActionService'
 */
class ShowForSupplierWisePaymentActionService extends BaseService implements ActionIntf {

    @Autowired
    SessionFactory sessionFactory
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSysConfigurationCacheUtility accSysConfigurationCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    SupplierCacheUtility supplierCacheUtility

    private static final String FAILURE_MSG = "Fail to load supplier wise payment report"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String SUPPLIER_PAYMENT_NOT_FOUND = "Payment for supplier not found within given dates"

    private static final String SUPPLIER_PAYMENT_LIST = "supplierPaymentList"

    private static final String COUNT = "count"
    private static final String PAID_TOTAL = "paidTotal"
    private static final String SUPPLIER_ID = "supplierId"
    private static final String SOURCE_ID = "sourceId"
    private static final String COMPANY_ID = "companyId"
    private static final String PROJECT_ID = "projectId"
    private static final String POSTED_BY = "postedParam"
    private static final String PROJECT_ID_LIST = "projectIdList"
    private static final String RESULT_PER_PAGE = "resultPerPage"
    private static final String START = "start"

    private Logger log = Logger.getLogger(getClass())
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
    /**
     * Get all objects related to display grid.
     * @param parameters - serialized parameters received from UI
     * @param obj - N/A
     * @return - a map containing project list, date range & isError msg(True/False)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            Date toDate
            Date fromDate

            if (params.supplierId && params.fromDate && params.toDate && params.projectId) {
                if (!params.rp) {
                    params.rp = 20
                    params.page = 1
                }
                initPager(params)

                long supplierId = Long.parseLong(params.supplierId.toString())
                long projectId = Long.parseLong(params.projectId.toString())

                result.put(SUPPLIER_ID, supplierId)
                result.put(PROJECT_ID, projectId)

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
                fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
                toDate = DateUtility.parseMaskedToDate(params.toDate.toString())

                List<Long> lstProjectIds = []
                if (projectId <= 0) {
                    lstProjectIds = accSessionUtil.appSessionUtil.getUserProjectIds()
                } else {
                    lstProjectIds << new Long(projectId)
                }

                /*if postedByParam  = 0 the show Only Posted Voucher
                  if postedByParam  = -1 the show both Posted & Unposted Voucher*/
                long postedByParam = new Long(-1)
                SysConfiguration sysConfiguration = accSysConfigurationCacheUtility.readByKeyAndCompanyId(accSysConfigurationCacheUtility.MIS_ACC_SHOW_POSTED_VOUCHERS)
                if (sysConfiguration) {
                    postedByParam = Long.parseLong(sysConfiguration.value)
                }

                result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
                result.put(TO_DATE, DateUtility.getDateForUI(toDate))

                LinkedHashMap serviceReturn = getSupplierWisePaidList(supplierId, lstProjectIds, fromDate, postedByParam, toDate)
                List<AccSupplierPaymentModel> supplierPaymentList = serviceReturn.supplierWisePaymentList
                int count = serviceReturn.count
                if (supplierPaymentList.size() <= 0) {
                    result.put(Tools.MESSAGE, SUPPLIER_PAYMENT_NOT_FOUND)
                    return result
                }

                String paidTotal = getTotalSupplierPaidAmountBySupplierAndDateRange(supplierId, lstProjectIds, fromDate, toDate, postedByParam)
                List supplierPaymentListWrap = wrapSupplierPaymentListInGridEntityList(supplierPaymentList, start)

                Map gridOutput = [page: pageNumber, total: count, rows: supplierPaymentListWrap]

                result.put(SUPPLIER_PAYMENT_LIST, gridOutput)
                result.put(COUNT, count)
                result.put(PAID_TOTAL, paidTotal)
            }
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
    /**
     * do nothing for success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }
    /**
     * do nothing for failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
    /**
     * Wrap supplier payment list for grid entity
     * @param supplierPaymentList -list of supplier payment object(s)
     * @param start -starting index of the page
     * @return - list of wrapped supplier payment list
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
     * @param fromVoucherDate - starting point of voucher
     * @param toVoucherDate - ending point of voucher
     * @param postedByParam -determines whether fetch posted voucher or both posted & un-posted voucher
     * @return - a string of total supplier paid amount
     */
    private String getTotalSupplierPaidAmountBySupplierAndDateRange(long supplierId, List<Long> lstProjectIds, Date fromVoucherDate, Date toVoucherDate, long postedByParam) {
        String strProjectIds = Tools.buildCommaSeparatedStringOfIds(lstProjectIds)
        String queryStr = """
            SELECT to_char(SUM(total_paid),'${Tools.DB_CURRENCY_FORMAT}') AS str_total_paid
            FROM vw_acc_supplier_payment
            WHERE voucher_date BETWEEN :fromVoucherDate AND :toVoucherDate
            AND source_id = :supplierId
            AND posted_by > :postedByParam
            AND company_id = :companyId
            AND project_id IN (${strProjectIds})
        """
        Map queryParams = [
                supplierId: supplierId,
                fromVoucherDate: DateUtility.getSqlDateWithSeconds(fromVoucherDate),
                toVoucherDate: DateUtility.getSqlDateWithSeconds(toVoucherDate),
                postedByParam: postedByParam,
                companyId: accSessionUtil.appSessionUtil.getAppUser().companyId
        ]
        List<GroovyRowResult> paidTotal = executeSelectSql(queryStr, queryParams)

        return paidTotal[0].str_total_paid ? paidTotal[0].str_total_paid : Tools.EMPTY_SPACE
    }
    /**
     * Get supplier wise paid list
     * @param supplierId - supplier id
     * @param lstProjectIds - list of project ids
     * @param fromDate - starting point of voucher
     * @param postedParam -determines whether fetch posted voucher or both posted & un-posted voucher
     * @param toDate - ending point of voucher
     * @return - a map containing supplier wise paid list
     */
    private LinkedHashMap getSupplierWisePaidList(long supplierId, List<Long> lstProjectIds, Date fromDate, long postedParam, Date toDate) {
        String strProjectIds = Tools.buildCommaSeparatedStringOfIds(lstProjectIds)
        long companyId = accSessionUtil.appSessionUtil.getAppUser().companyId

        String queryStr = """
            SELECT id, po_project_id, total_po, COALESCE(SUM(total_paid),0) total_paid,
            total_po-COALESCE(SUM(total_paid),0) remaining, source_id
            FROM vw_acc_supplier_payment
            WHERE voucher_date BETWEEN :fromDate AND :toDate
            AND source_id = :sourceId
            AND posted_by > :postedParam
            AND company_id = :companyId
             AND project_id IN (${strProjectIds})
            GROUP BY id,po_project_id, total_po, source_id
            ORDER BY id
            LIMIT :resultPerPage  OFFSET :start
        """


        Map queryParam = [
                sourceId: supplierId,
                fromDate: DateUtility.getSqlDateWithSeconds(fromDate),
                toDate: DateUtility.getSqlDateWithSeconds(toDate),
                postedParam: postedParam,
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]
        /*Session session = sessionFactory.currentSession
        SQLQuery query = session.createSQLQuery(queryStr)
        query.addEntity(AccSupplierPaymentModel.class)
        query.setLong(SOURCE_ID, supplierId)
        query.setLong(POSTED_BY, postedParam)
        query.setLong(COMPANY_ID, companyId)
        query.setParameterList(PROJECT_ID_LIST, lstProjectIds)
        query.setParameter(FROM_DATE, fromDate)
        query.setParameterList(TO_DATE, toDate)
        query.setInteger(RESULT_PER_PAGE, resultPerPage)
        query.setInteger(START, start)

        List<AccSupplierPaymentModel> accSupplierPaymentModelList = query.list()*/
        List<GroovyRowResult> accSupplierPaymentModelList = executeSelectSql(queryStr, queryParam)

        String queryCount = """
                    SELECT COUNT(DISTINCT id)
                    FROM vw_acc_supplier_payment
                    WHERE voucher_date BETWEEN :fromDate AND :toDate
                    AND source_id = :sourceId
                    AND posted_by > :postedParam
                    AND company_id = :companyId
                    AND project_id IN (${strProjectIds})
        """

        Map queryParams = [
                sourceId: supplierId,
                fromDate: DateUtility.getSqlDateWithSeconds(fromDate),
                toDate: DateUtility.getSqlDateWithSeconds(toDate),
                postedParam: postedParam,
                companyId: companyId
        ]
        List countResults = executeSelectSql(queryCount, queryParams)
        int count = countResults[0].count
        return [supplierWisePaymentList: accSupplierPaymentModelList.size() > 0 ? accSupplierPaymentModelList : [], count: count]
    }
}
