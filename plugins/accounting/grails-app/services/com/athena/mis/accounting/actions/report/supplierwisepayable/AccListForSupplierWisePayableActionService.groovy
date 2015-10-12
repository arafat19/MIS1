package com.athena.mis.accounting.actions.report.supplierwisepayable

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccFinancialYear
import com.athena.mis.accounting.utility.AccFinancialYearCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.accounting.utility.AccSourceCacheUtility
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * List of suppliers with payable details
 * For details go through Use-Case doc named 'AccListForSupplierWisePayableActionService'
 */
class AccListForSupplierWisePayableActionService extends BaseService implements ActionIntf {

    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate supplier wise payable report"
    private static final String SUPPLIER_WISE_PAYABLE_LIST = "supplierWisePayableList"
    private static final String COUNT = "count"
    private static final String PAYABLE_SUPPLIER_NOT_FOUND = "Payable Supplier not found within given dates"
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String PROJECT_ID = "projectId"
    private static final String SPAN_START = "<span style='color:red'>"
    private static final String SPAN_END = "</span>"

    private Logger log = Logger.getLogger(getClass())
    /**
     *  Check some pre conditions
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return - Map containing isError(true/false) & relevant message(in case)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.fromDate || !params.toDate) {         // check required field
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
            }
            Date startDate = DateUtility.parseMaskedDate(params.fromDate.toString())
            Date endDate = DateUtility.parseMaskedDate(params.toDate.toString())
            AccFinancialYear accFinancialYear = accFinancialYearCacheUtility.getCurrentFinancialYear()
            // get current financial year
            if (accFinancialYear) {
                String exceedsFinancialYear = DateUtility.checkFinancialDateRange(startDate, endDate, accFinancialYear)
                // check date range with current financial year
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
     * Supplier wise payable list in a specific date range
     * @param parameters -serialized parameters from preExecute method
     * @param obj -N/A
     * @return - a map containing supplier wise payable list, project list & date range
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
            this.initPager(parameterMap)      // initialize parameters for flexGrid

            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())
            long projectId
            List<Long> projectIdList = []
            if (!parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {
                projectId = Long.parseLong(parameterMap.projectId.toString())
                projectIdList << new Long(projectId)
            } else {
                projectIdList = accSessionUtil.appSessionUtil.getUserProjectIds()
                // get project list
            }
            LinkedHashMap serviceReturn = getSupplierWisePayableList(projectIdList, fromDate, toDate)
            // get supplier wise payable list


            List<GroovyRowResult> supplierWisePayableList = serviceReturn.supplierWisePayableList
            int count = serviceReturn.count

            if (count <= 0) {
                result.put(Tools.MESSAGE, PAYABLE_SUPPLIER_NOT_FOUND)
                return result
            }
            // wrap for grid show
            List supplierWisePayableListWrap = wrapSupplierWisePayableListInGridEntityList(supplierWisePayableList, this.start)

            result.put(SUPPLIER_WISE_PAYABLE_LIST, supplierWisePayableListWrap)
            result.put(COUNT, count)
            result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
            result.put(PROJECT_ID, projectId)
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
     * Wrap supplier wise payable list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary for grid view
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List supplierPayableListWrap = (List) executeResult.get(SUPPLIER_WISE_PAYABLE_LIST)
            int count = (int) executeResult.get(COUNT)

            Map gridOutput = [page: this.pageNumber, total: count, rows: supplierPayableListWrap]
            result.put(SUPPLIER_WISE_PAYABLE_LIST, gridOutput)
            result.put(FROM_DATE, executeResult.get(FROM_DATE))
            result.put(TO_DATE, executeResult.get(TO_DATE))
            result.put(PROJECT_ID, executeResult.get(PROJECT_ID))
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
     * Wrap supplier wise payable list in grid entity
     * @param supplierWisePayableList -list of supplier wise payable object(s)
     * @param start -starting index of the page
     * @return -list of wrapped supplier wise payable
     */
    private static List wrapSupplierWisePayableListInGridEntityList(List<GroovyRowResult> supplierWisePayableList, int start) {
        List lstSupplierPo = [] as List

        int counter = start + 1
        GroovyRowResult supplierWisePayable
        GridEntity obj

        for (int i = 0; i < supplierWisePayableList.size(); i++) {
            supplierWisePayable = supplierWisePayableList[i]
            obj = new GridEntity()
            obj.id = supplierWisePayable.supplier_id
            obj.cell = [
                    counter,
                    supplierWisePayable.supplier_name,
                    supplierWisePayable.str_po_amount,
                    supplierWisePayable.str_received_in_inventory,
                    supplierWisePayable.str_received_in_fixed_asset,
                    supplierWisePayable.str_payable,
                    supplierWisePayable.str_paid,
                    supplierWisePayable.balance < 0 ? SPAN_START + supplierWisePayable.str_balance + SPAN_END : supplierWisePayable.str_balance
            ]
            lstSupplierPo << obj
            counter++
        }
        return lstSupplierPo
    }
    /**
     * Supplier wise payable list
     * @param projectIdList - project list received from execute method
     * @param fromDate - starting point of date range
     * @param toDate - ending point of date range
     * @return - supplier wise payable list and count
     */
    private LinkedHashMap getSupplierWisePayableList(List<Long> projectIdList, Date fromDate, Date toDate) {
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity accSourceTypeSupplier = (SystemEntity) accSourceCacheUtility.readByReservedAndCompany(accSourceCacheUtility.SOURCE_TYPE_SUPPLIER, companyId)
        String projectIds = Tools.buildCommaSeparatedStringOfIds(projectIdList)
        String queryStr = """
                SELECT
                supplier.id AS supplier_id,
                supplier.name AS supplier_name,
                TO_CHAR(COALESCE(total_po_price,0),'${Tools.DB_CURRENCY_FORMAT}') AS str_po_amount,
                TO_CHAR(COALESCE(total_received_in_inventory,0),'${Tools.DB_CURRENCY_FORMAT}') AS str_received_in_inventory,
                TO_CHAR(COALESCE(total_fad_cost,0),'${Tools.DB_CURRENCY_FORMAT}') AS str_received_in_fixed_asset,
                TO_CHAR(COALESCE(total_received_in_inventory,0)+COALESCE(total_fad_cost,0),'${Tools.DB_CURRENCY_FORMAT}') AS str_payable,
                TO_CHAR(COALESCE(total_paid,0),'${Tools.DB_CURRENCY_FORMAT}') AS str_paid,
                TO_CHAR(COALESCE(total_received_in_inventory,0)+COALESCE(total_fad_cost,0) - COALESCE(total_paid,0),'${Tools.DB_CURRENCY_FORMAT}') AS str_balance,
                COALESCE(total_received_in_inventory,0)+COALESCE(total_fad_cost,0) - COALESCE(total_paid,0) AS balance
                FROM  supplier
            
                FULL OUTER JOIN
                    (   
                               SELECT  supplier_id, SUM(total_price) total_po_price
                                from proc_purchase_order
                                where project_id IN (${projectIds})
                                AND (created_on BETWEEN :fromDate AND :toDate)
                                AND approved_by_director_id >0 AND approved_by_project_director_id >0
                                GROUP BY supplier_id
                    ) po
                ON po.supplier_id=supplier.id
                
                FULL OUTER JOIN
                    (   
                               SELECT  transaction_entity_id supplier_id, SUM(actual_quantity*rate) AS total_received_in_inventory
                                from vw_inv_inventory_transaction_with_details        
                                where project_id IN (${projectIds})
                                AND (transaction_date BETWEEN :fromDate AND :toDate)
                            AND transaction_type_id = :transactionTypeId
                            AND transaction_entity_type_id = :transactionEntityTypeId
                            AND is_current = true
                            AND approved_by > 0
                            GROUP BY supplier_id
                    ) iitd
                ON iitd.supplier_id=supplier.id
                
                FULL OUTER JOIN
                    (   
                               SELECT  supplier_id, SUM(cost) AS total_fad_cost
                               from fxd_fixed_asset_details        
                               where project_id IN (${projectIds})
                               AND (purchase_date BETWEEN :fromDate AND :toDate)
                               GROUP BY supplier_id
                    ) fad
                ON fad.supplier_id=supplier.id
                
                FULL OUTER JOIN
                    (   
                               SELECT source_id supplier_id, SUM(amount_dr) AS total_paid
                                from vw_acc_voucher_with_details        
                                where project_id IN (${projectIds})
                                AND (voucher_date BETWEEN :fromDate AND :toDate)
                                AND source_type_id = :sourceTypeId
                                GROUP BY supplier_id
                    ) avd
                ON avd.supplier_id=supplier.id
                
                                WHERE supplier.company_id  = :companyId
                                GROUP BY supplier.id, supplier.name, total_po_price, total_received_in_inventory, total_fad_cost, total_paid
                                HAVING (total_po_price >0) OR (total_received_in_inventory >0) OR (total_fad_cost>0) OR (total_paid>0)
                                ORDER BY supplier.name
                LIMIT :resultPerPage  OFFSET :start
        """

        String queryCount = """
            SELECT COUNT(1) count FROM
            (
                SELECT
                    COUNT(supplier.id)
                    FROM  supplier

                    FULL OUTER JOIN
                        (
                                   SELECT  supplier_id, SUM(total_price) total_po_price
                                    from proc_purchase_order
                                    where project_id IN (${projectIds})
                                    AND (created_on BETWEEN :fromDate AND :toDate)
                                    AND approved_by_director_id >0 AND approved_by_project_director_id >0
                                    GROUP BY supplier_id
                        ) po
                    ON po.supplier_id=supplier.id

                    FULL OUTER JOIN
                        (
                                   SELECT  transaction_entity_id supplier_id, SUM(actual_quantity*rate) AS total_received_in_inventory
                                    from vw_inv_inventory_transaction_with_details
                                    where project_id IN (${projectIds})
                                    AND (transaction_date BETWEEN :fromDate AND :toDate)
                                AND transaction_type_id = :transactionTypeId
                                AND transaction_entity_type_id = :transactionEntityTypeId
                                AND approved_by > 0
                                AND is_current = true
                                    GROUP BY supplier_id
                        ) iitd
                    ON iitd.supplier_id=supplier.id

                    FULL OUTER JOIN
                        (
                                   SELECT  supplier_id, SUM(cost) AS total_fad_cost
                                   from fxd_fixed_asset_details
                                   where project_id IN (${projectIds})
                                   AND (purchase_date BETWEEN :fromDate AND :toDate)
                                   GROUP BY supplier_id
                        ) fad
                    ON fad.supplier_id=supplier.id

                    FULL OUTER JOIN
                        (
                                   SELECT source_id supplier_id, SUM(amount_dr) AS total_paid
                                    from vw_acc_voucher_with_details
                                    where project_id IN (${projectIds})
                                    AND (voucher_date BETWEEN :fromDate AND :toDate)
                                    AND source_type_id = :sourceTypeId
                                    GROUP BY supplier_id
                        ) avd
                    ON avd.supplier_id=supplier.id

                                    WHERE supplier.company_id  = :companyId
                                    GROUP BY supplier.id, supplier.name, total_po_price, total_received_in_inventory, total_fad_cost, total_paid
                                    HAVING (total_po_price >0) OR (total_received_in_inventory >0) OR (total_fad_cost>0) OR (total_paid>0)
                                    ORDER BY supplier.name
            ) tmp
        """
        Map queryParams = [
                fromDate: DateUtility.getSqlDateWithSeconds(fromDate),
                toDate: DateUtility.getSqlDateWithSeconds(toDate),
                transactionTypeId: inventoryImplService.invTransactionTypeIdIn,
                transactionEntityTypeId: inventoryImplService.transactionEntityTypeIdSupplier,
                sourceTypeId: accSourceTypeSupplier.id,
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]

        List<GroovyRowResult> supplierWisePayableList = executeSelectSql(queryStr, queryParams)
        List countResults = executeSelectSql(queryCount, queryParams)

        int count = countResults[0].count
        return [supplierWisePayableList: supplierWisePayableList, count: count]
    }
}