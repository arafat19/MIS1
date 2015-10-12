package com.athena.mis.accounting.actions.report.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.*
import com.athena.mis.accounting.model.AccVoucherModel
import com.athena.mis.accounting.utility.*
import com.athena.mis.application.entity.*
import com.athena.mis.application.utility.*
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
/**
 * Show UI for list of voucher
 * For details go through Use-Case doc named 'ShowForVoucherActionService'
 */
class ShowForVoucherActionService extends BaseService implements ActionIntf {

    @Autowired
    AppUserCacheUtility appUserCacheUtility
    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AccInstrumentTypeCacheUtility accInstrumentTypeCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    EmployeeCacheUtility employeeCacheUtility
    @Autowired
    AccSourceCacheUtility accSourceCacheUtility
    @Autowired
    CustomerCacheUtility customerCacheUtility
    @Autowired
    AccSubAccountCacheUtility accSubAccountCacheUtility
    @Autowired
    ItemCacheUtility itemCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccLcCacheUtility accLcCacheUtility
    @Autowired
    AccIpcCacheUtility accIpcCacheUtility
    @Autowired
    AccLeaseAccountCacheUtility accLeaseAccountCacheUtility

    private static final String FAILURE_MSG = "Fail to voucher."
    private static final String VOUCHER_MAP = "voucherMap"
    private static final String LABEL_TOTAL = "Total:"
    private static final String SORT_COLUMN = "rowId"

    protected final Logger log = Logger.getLogger(getClass())
    /**
     * nothing to do in pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * nothing to do in post operation
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }
    /**
     * Check pre conditions -and get voucher object
     * @param parameters -serialized parameters from UI
     * @param obj-N/A
     * @return -a map containing voucher object & isError msg(True/False)
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)

            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (params.voucherId) {
                long voucherId = Long.parseLong(params.voucherId.toString())
                AccVoucher accVoucher = AccVoucher.findByIdAndCompanyId(voucherId, accSessionUtil.appSessionUtil.getAppUser().companyId, [readOnly: true])
                if (accVoucher) {
                    LinkedHashMap voucherMap = buildVoucherMap(accVoucher)
                    result.put(VOUCHER_MAP, voucherMap)
                }
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
     * nothing to do in success operation
     */
    public Object buildSuccessResultForUI(Object obj) {
        return null
    }
    /**
     * nothing to do in failure operation
     */
    public Object buildFailureResultForUI(Object obj) {
        return null
    }
    /**
     * Build a map with necessary properties of voucher
     * @param accVoucher - object of AccVoucher
     * @return - a map containing voucher object
     */
    private LinkedHashMap buildVoucherMap(AccVoucher accVoucher) {
        AppUser preparedBy = (AppUser) appUserCacheUtility.read(accVoucher.createdBy)
        AppUser approvedBy = (AppUser) appUserCacheUtility.read(accVoucher.postedBy)

        List<AccVoucherModel> voucherList = AccVoucherModel.listByVoucherId(accVoucher.id, accSessionUtil.appSessionUtil.getAppUser().companyId).list(sort: SORT_COLUMN, order: this.ASCENDING_SORT_ORDER)
        List totalDebitedList = AccVoucherDetails.withCriteria {
            eq('voucherId', accVoucher.id)
            projections {
                sum('amountDr')
            }
        }
        double totalDebitedAmount = (double) totalDebitedList[0]
        List totalCreditedList = AccVoucherDetails.withCriteria {
            eq('voucherId', accVoucher.id)
            projections {
                sum('amountCr')
            }
        }
        double totalCreditedAmount = (double) totalCreditedList[0]
        List voucherListOutput = wrapVoucherList(voucherList, totalDebitedAmount, totalCreditedAmount)

        SystemEntity accVoucherType = (SystemEntity) accVoucherTypeCacheUtility.read(accVoucher.voucherTypeId)
        Project project = (Project) projectCacheUtility.read(accVoucher.projectId)

        String instrumentTypeName = Tools.NOT_APPLICABLE
        if (accVoucher.instrumentId > 0) {
            SystemEntity accInstrumentType = (SystemEntity) accInstrumentTypeCacheUtility.read(accVoucher.instrumentTypeId)
            instrumentTypeName = accInstrumentType.key
        }
        SystemEntity voucherTypeObj = (SystemEntity) accVoucherTypeCacheUtility.readByReservedAndCompany(accVoucherTypeCacheUtility.PAYMENT_VOUCHER_BANK_ID, accSessionUtil.appSessionUtil.getCompanyId())

        boolean isChequePrintable = false
        if (accVoucher.voucherTypeId == voucherTypeObj.id) {
            isChequePrintable = true
        }

        LinkedHashMap voucherMap = [
                voucherTypeName: accVoucherType.key,
                voucherId: accVoucher.id,
                traceNo: accVoucher.traceNo,
                chequeNo: accVoucher.chequeNo ? accVoucher.chequeNo : Tools.EMPTY_SPACE,
                voucherDate: DateUtility.getDateFormatAsString(accVoucher.voucherDate),
                voucherList: voucherListOutput as JSON,
                preparedBy: preparedBy.username,
                approvedBy: approvedBy ? approvedBy.username : Tools.EMPTY_SPACE,
                projectName: project.name,
                instrumentType: instrumentTypeName,
                instrumentNo: accVoucher.instrumentId > 0 ? accVoucher.instrumentId : Tools.NOT_APPLICABLE,
                isChequePrintable: isChequePrintable,
                isCancelledVoucher: false
        ]
        return voucherMap
    }
    /**
     * Wrap voucher list for grid entity
     * @param voucherList - list of voucher
     * @param totalDebitedAmount - total debit amount
     * @param totalCreditedAmount - total credit amount
     * @return - wrapped voucher list
     */
    private List wrapVoucherList(List<AccVoucherModel> voucherList, double totalDebitedAmount, double totalCreditedAmount) {
        List vouchers = [] as List
        AccVoucherModel voucher
        Map transactionMap
        String headName
        String particulars
        String sourceNameAndType
        for (int i = 0; i < voucherList.size(); i++) {
            voucher = voucherList[i]

            headName = Tools.EMPTY_SPACE
            particulars = Tools.EMPTY_SPACE
            if (voucher.coaDescription) {
                headName = voucher.coaDescription
            }

            if (voucher.particulars) {
                particulars = Tools.PARENTHESIS_START + voucher.particulars + Tools.PARENTHESIS_END
            }

            sourceNameAndType = getSourceTypeAndName(voucher)
            transactionMap = [
                    code: voucher.coaCode,
                    headName: headName,
                    particulars: particulars,
                    source: sourceNameAndType,
                    debit: voucher.strAmountDr,
                    credit: voucher.strAmountCr,
            ]
            vouchers << transactionMap
        }

        if (voucherList.size() > 0) {
            transactionMap = [
                    code: LABEL_TOTAL,
                    headName: Tools.EMPTY_SPACE,
                    particulars: Tools.EMPTY_SPACE,
                    source: Tools.EMPTY_SPACE,
                    debit: Tools.makeAmountWithThousandSeparator(totalDebitedAmount),
                    credit: Tools.makeAmountWithThousandSeparator(totalCreditedAmount),
            ]

            vouchers << transactionMap
        }

        return vouchers

    }
    /**
     * Get source type and name
     * @param accVoucherModel - voucher model object
     * @return - a string(sourceType + name)
     */
    private String getSourceTypeAndName(AccVoucherModel accVoucherModel) {
        String sourceTypeAndName = Tools.NONE
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(accVoucherModel.sourceTypeId)
        switch (accSourceType.reservedId) {
            case accSourceCacheUtility.SOURCE_TYPE_CUSTOMER:
                String sourceType = accSourceCacheUtility.read(accVoucherModel.sourceTypeId).key
                Customer customer = (Customer) customerCacheUtility.read(accVoucherModel.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + customer.fullName
                break;
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                String sourceType = accSourceCacheUtility.read(accVoucherModel.sourceTypeId).key
                Employee employee = (Employee) employeeCacheUtility.read(accVoucherModel.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + employee.fullName
                break;
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                String sourceType = accSourceCacheUtility.read(accVoucherModel.sourceTypeId).key
                Supplier supplier = (Supplier) supplierCacheUtility.read(accVoucherModel.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + supplier.name
                break;
            case accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT:
                String sourceType = accSourceCacheUtility.read(accVoucherModel.sourceTypeId).key
                AccSubAccount accSubAccount = (AccSubAccount) accSubAccountCacheUtility.read(accVoucherModel.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + accSubAccount.description
                break;
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                String sourceType = accSourceCacheUtility.read(accVoucherModel.sourceTypeId).key
                Item item = (Item) itemCacheUtility.read(accVoucherModel.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + item.name
                break;
            case accSourceCacheUtility.SOURCE_TYPE_LC:
                String sourceType = accSourceCacheUtility.read(accVoucherModel.sourceTypeId).key
                AccLc accLc = (AccLc) accLcCacheUtility.read(accVoucherModel.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + accLc.lcNo
                break;
            case accSourceCacheUtility.SOURCE_TYPE_IPC:
                String sourceType = accSourceCacheUtility.read(accVoucherModel.sourceTypeId).key
                AccIpc accIpc = (AccIpc) accIpcCacheUtility.read(accVoucherModel.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + accIpc.ipcNo
                break;
            case accSourceCacheUtility.SOURCE_TYPE_LEASE_ACCOUNT:
                String sourceType = accSourceCacheUtility.read(accVoucherModel.sourceTypeId).key
                AccLeaseAccount accLeaseAccount = (AccLeaseAccount) accLeaseAccountCacheUtility.read(accVoucherModel.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + accLeaseAccount.institution
                break;
            default:
                break
        }
        return sourceTypeAndName
    }
}