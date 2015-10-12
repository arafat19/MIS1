package com.athena.mis.accounting.actions.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.*
import com.athena.mis.accounting.model.AccVoucherModel
import com.athena.mis.accounting.service.AccVoucherService
import com.athena.mis.accounting.utility.*
import com.athena.mis.application.entity.*
import com.athena.mis.application.utility.*
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Select object specific voucher while selecting grid row
 *  For details go through Use-Case doc named 'SelectAccVoucherActionService'
 */
class SelectAccVoucherActionService extends BaseService implements ActionIntf {

    private static final String ACC_VOUCHER_NOT_FOUND_MASSAGE = "Selected voucher not found"
    private static final String DEFAULT_ERROR_MASSAGE = "Failed to select voucher"
    private static final String LST_VOUCHER_DETAILS = "lstVoucherDetails"
    private static final String VOUCHER_DATE = "voucherDate"
    private static final String CHEQUE_DATE = "chequeDate"
    private static final String GRID_MODEL_DR = "gridModelDr"
    private static final String GRID_MODEL_CR = "gridModelCr"
    private static final String PROJECT_ID = "projectId"
    private static final String LST_DIVISION = "lstDivision"
    private static final String ACC_VOUCHER = "accVoucher"

    private Logger log = Logger.getLogger(getClass())

    AccVoucherService accVoucherService
    @Autowired
    AccDivisionCacheUtility accDivisionCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    CustomerCacheUtility customerCacheUtility
    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    AccLcCacheUtility accLcCacheUtility
    @Autowired
    AccIpcCacheUtility accIpcCacheUtility
    @Autowired
    AccLeaseAccountCacheUtility accLeaseAccountCacheUtility

    /**
     * Get object of selected voucher
     * @param params -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)     // default value
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            long voucherId = Long.parseLong(parameterMap.id.toString())
            AccVoucher accVoucher = accVoucherService.read(voucherId)      // get voucher object
            if (!accVoucher) {    // check whether this voucher exits or not
                result.put(Tools.MESSAGE, ACC_VOUCHER_NOT_FOUND_MASSAGE)
                return result
            }
            result.put(ACC_VOUCHER, accVoucher)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
    /**
     * Get voucher details of selected voucher
     * @param params -N/A
     * @param obj -Receives map from preCondition method
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map preResult = (Map) obj                  // cast object received from preCondition method
            AccVoucher accVoucher = (AccVoucher) preResult.get(ACC_VOUCHER)
            // retrieve voucher details list filtered by specific voucher id
            List<AccVoucherModel> lstVoucherDetails = (List<AccVoucherModel>) AccVoucherModel.listByVoucherId(accVoucher.id, accSessionUtil.appSessionUtil.getAppUser().companyId).list()
            result.put(Tools.ENTITY, accVoucher)
            result.put(LST_VOUCHER_DETAILS, lstVoucherDetails)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        // do nothing for post operation
        return null
    }

    /**
     *  Wrap debit,credit for voucher details
     * @param obj -map returned from execute method
     * @return -a map containing all objects  related to selected voucher
     * map -contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            Map executeResult = (LinkedHashMap) obj
            AccVoucher accVoucher = (AccVoucher) executeResult.get(Tools.ENTITY)
            List<AccVoucherModel> lstVoucherDetails = (List<AccVoucherModel>) executeResult.get(LST_VOUCHER_DETAILS)
            List lstDebits = []
            List lstCredits = []
            Map gridObjects = wrapDebitCredit(lstVoucherDetails)  // return debits & credits of a voucher details
            if (gridObjects) {
                lstDebits = gridObjects.lstDebits
                lstCredits = gridObjects.lstCredits
            }
            Map gridModelDr = [page: 1, total: lstDebits.size(), rows: lstDebits]
            Map gridModelCr = [page: 1, total: lstCredits.size(), rows: lstCredits]

            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(GRID_MODEL_DR, gridModelDr)
            result.put(GRID_MODEL_CR, gridModelCr)
            result.put(Tools.ENTITY, accVoucher)
            result.put(Tools.VERSION, accVoucher.version)
            long projectId = accVoucher.projectId
            result.put(PROJECT_ID, projectId)
            List<AccDivision> lstAccDivision = accDivisionCacheUtility.listByProjectIdAndIsActive(projectId)
            result.put(LST_DIVISION, Tools.listForKendoDropdown(lstAccDivision, null, null))
            result.put(VOUCHER_DATE, accVoucher.voucherDate.format(DateUtility.dd_MM_yyyy_SLASH))
            if (accVoucher.chequeDate) {
                result.put(CHEQUE_DATE, accVoucher.chequeDate.format(DateUtility.dd_MM_yyyy_SLASH))
            }
            return result

        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to display on page load
     */
    public Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
            return result
        }
    }
    /**
     * Wrap voucher details with debits & credits
     * @param lstVoucherDetails -map returned from buildSuccess method
     * @return -a map containing debits & credits
     */
    private Map wrapDebitCredit(List<AccVoucherModel> lstVoucherDetails) {
        List lstDebits = []
        List lstCredits = []
        Map gridObjects = new LinkedHashMap()
        AccVoucherModel singleRow
        for (int i = 0; i < lstVoucherDetails.size(); i++) {
            singleRow = lstVoucherDetails[i]
            GridEntity obj = new GridEntity()
            obj.id = singleRow.coaId
            String particulars = singleRow.particulars ? singleRow.particulars : Tools.EMPTY_SPACE // null value making string null in model
            String sourceName = getSourceName(singleRow.sourceTypeId, singleRow.sourceId)
            if (singleRow.amountDr > 0.0d) {
                obj.cell = [
                        singleRow.coaId,
                        singleRow.coaCode,
                        singleRow.coaDescription,
                        singleRow.amountDr,
                        particulars,
                        sourceName,
                        singleRow.sourceTypeId,
                        singleRow.sourceId,
                        singleRow.rowId,
                        singleRow.divisionId
                ]
                lstDebits << obj
            } else if (singleRow.amountCr > 0.0d) {
                obj.cell = [
                        singleRow.coaId,
                        singleRow.coaCode,
                        singleRow.coaDescription,
                        singleRow.amountCr,
                        particulars,
                        sourceName,
                        singleRow.sourceTypeId,
                        singleRow.sourceId,
                        singleRow.rowId,
                        singleRow.divisionId
                ]
                lstCredits << obj
            }
        }
        gridObjects = [lstDebits: lstDebits, lstCredits: lstCredits]
        return gridObjects
    }
    /**
     * Get source name filtered by source type & id
     * @param sourceTypeId -source type
     * @param sourceId -source id
     * @return -source name based on source type & id
     */
    private String getSourceName(long sourceTypeId, long sourceId) {
        String sourceName = null
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(sourceTypeId)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.ACC_SOURCE_NAME_NONE:
                sourceName = Tools.NOT_APPLICABLE
                break
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                Employee employee = (Employee) employeeCacheUtility.read(sourceId)
                sourceName = employee.fullName + Tools.PARENTHESIS_START + employee.id + Tools.PARENTHESIS_END
                break
            case accSourceCacheUtility.SOURCE_TYPE_CUSTOMER:
                Customer customer = (Customer) customerCacheUtility.read(sourceId)
                sourceName = customer.fullName + Tools.PARENTHESIS_START + customer.id + Tools.PARENTHESIS_END
                break
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                Supplier supplier = (Supplier) supplierCacheUtility.read(sourceId)
                sourceName = supplier.name + Tools.PARENTHESIS_START + supplier.id + Tools.PARENTHESIS_END
                break
            case accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT:
                AccSubAccount accSubAccount = (AccSubAccount) accSubAccountCacheUtility.read(sourceId)
                sourceName = accSubAccount.description
                break
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                Item item = (Item) itemCacheUtility.read(sourceId)
                sourceName = item.name
                break
            case accSourceCacheUtility.SOURCE_TYPE_LC:
                AccLc accLc = (AccLc) accLcCacheUtility.read(sourceId)
                sourceName = accLc.lcNo
                break
            case accSourceCacheUtility.SOURCE_TYPE_IPC:
                AccIpc accIpc = (AccIpc) accIpcCacheUtility.read(sourceId)
                sourceName = accIpc.ipcNo
                break
            case accSourceCacheUtility.SOURCE_TYPE_LEASE_ACCOUNT:
                AccLeaseAccount accLeaseAccount = (AccLeaseAccount) accLeaseAccountCacheUtility.read(sourceId)
                sourceName = accLeaseAccount.institution
                break
            default:
                sourceName = Tools.NOT_APPLICABLE
        }
        return sourceName
    }
}
