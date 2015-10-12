package com.athena.mis.accounting.actions.report.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.*
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

class ShowForCancelledVoucherActionService extends BaseService implements ActionIntf {

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
            boolean isCancelledVoucher = false

            if (params.voucherId) {
                long voucherId = Long.parseLong(params.voucherId.toString())
                if(params.cancelledVoucher){
                    isCancelledVoucher = Boolean.parseBoolean(params.cancelledVoucher.toString())
                }
                AccCancelledVoucher accCancelledVoucher = AccCancelledVoucher.findByIdAndCompanyId(voucherId, accSessionUtil.appSessionUtil.getAppUser().companyId, [readOnly: true])
                if (accCancelledVoucher) {
                    LinkedHashMap cancelledVoucherMap = buildVoucherMap(accCancelledVoucher, isCancelledVoucher)
                    result.put(VOUCHER_MAP, cancelledVoucherMap)
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
    private LinkedHashMap buildVoucherMap(AccCancelledVoucher accVoucher, boolean isCancelledVoucher) {
        AppUser preparedBy = (AppUser) appUserCacheUtility.read(accVoucher.createdBy)
        AppUser approvedBy = (AppUser) appUserCacheUtility.read(accVoucher.postedBy)
        AppUser cancelledBy = (AppUser) appUserCacheUtility.read(accVoucher.cancelledBy)

        List<AccCancelledVoucherDetails> voucherList = AccCancelledVoucherDetails.findAllByVoucherId(accVoucher.id)
        List totalDebitedList = AccCancelledVoucherDetails.withCriteria {
            eq('voucherId', accVoucher.id)
            projections {
                sum('amountDr')
            }
        }
        double totalDebitedAmount = (double) totalDebitedList[0]
        List totalCreditedList = AccCancelledVoucherDetails.withCriteria {
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

        boolean isChequePrintable = false
        SystemEntity voucherTypeObj = (SystemEntity) accVoucherTypeCacheUtility.readByReservedAndCompany(accVoucherTypeCacheUtility.PAYMENT_VOUCHER_BANK_ID, accSessionUtil.appSessionUtil.getCompanyId())

        if (accVoucher.voucherTypeId == voucherTypeObj.id) {
            isChequePrintable = true
        }

        LinkedHashMap voucherMap = [
                voucherTypeName: accVoucherType.key,
                voucherId: accVoucher.id,
                traceNo: accVoucher.traceNo,
                chequeNo: accVoucher.chequeNo ? accVoucher.chequeNo : Tools.EMPTY_SPACE,
                voucherDate: DateUtility.getDateFormatAsString(accVoucher.voucherDate),
                cancelledOn: DateUtility.getDateFormatAsString(accVoucher.cancelledOn),
                cancelledReason: accVoucher.cancelReason,
                voucherList: voucherListOutput as JSON,
                preparedBy: preparedBy.username,
                cancelledBy: cancelledBy.username,
                approvedBy: approvedBy ? approvedBy.username : Tools.EMPTY_SPACE,
                projectName: project.name,
                instrumentType: instrumentTypeName,
                instrumentNo: accVoucher.instrumentId > 0 ? accVoucher.instrumentId : Tools.NOT_APPLICABLE,
                isChequePrintable: isChequePrintable,
                isCancelledVoucher: isCancelledVoucher
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
    private List wrapVoucherList(List<AccCancelledVoucherDetails> voucherList, double totalDebitedAmount, double totalCreditedAmount) {
        List vouchers = [] as List
        Map transactionMap
        AccCancelledVoucherDetails cancelledVoucherDetails
        String headName
        String particulars
        String sourceNameAndType
        for (int i = 0; i < voucherList.size(); i++) {
            cancelledVoucherDetails = voucherList[i]
            AccChartOfAccount coa = AccChartOfAccount.read(voucherList[i].coaId)
            headName = Tools.EMPTY_SPACE
            particulars = Tools.EMPTY_SPACE
            if (coa.description) {
                headName = coa.description
            }

            if (cancelledVoucherDetails.particulars) {
                particulars = Tools.PARENTHESIS_START + cancelledVoucherDetails.particulars + Tools.PARENTHESIS_END
            }

            sourceNameAndType = getSourceTypeAndName(cancelledVoucherDetails)
            transactionMap = [
                    code: coa.code,
                    headName: headName,
                    particulars: particulars,
                    source: sourceNameAndType,
                    debit: Tools.makeAmountWithThousandSeparator(cancelledVoucherDetails.amountDr),
                    credit: Tools.makeAmountWithThousandSeparator(cancelledVoucherDetails.amountCr),
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
     * @param accVoucherDetails - voucher model object
     * @return - a string(sourceType + name)
     */
    private String getSourceTypeAndName(AccCancelledVoucherDetails accVoucherDetails) {
        String sourceTypeAndName = Tools.NONE
        SystemEntity accSourceType = (SystemEntity) accSourceCacheUtility.read(accVoucherDetails.sourceTypeId)
        switch (accSourceType.reservedId) {   // if voucher object has source id, retrieve its source type & name
            case accSourceCacheUtility.SOURCE_TYPE_CUSTOMER:
                String sourceType = accSourceCacheUtility.read(accVoucherDetails.sourceTypeId).key
                Customer customer = (Customer) customerCacheUtility.read(accVoucherDetails.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + customer.fullName
                break;
            case accSourceCacheUtility.SOURCE_TYPE_EMPLOYEE:
                String sourceType = accSourceCacheUtility.read(accVoucherDetails.sourceTypeId).key
                Employee employee = (Employee) employeeCacheUtility.read(accVoucherDetails.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + employee.fullName
                break;
            case accSourceCacheUtility.SOURCE_TYPE_SUPPLIER:
                String sourceType = accSourceCacheUtility.read(accVoucherDetails.sourceTypeId).key
                Supplier supplier = (Supplier) supplierCacheUtility.read(accVoucherDetails.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + supplier.name
                break;
            case accSourceCacheUtility.SOURCE_TYPE_SUB_ACCOUNT:
                String sourceType = accSourceCacheUtility.read(accVoucherDetails.sourceTypeId).key
                AccSubAccount accSubAccount = (AccSubAccount) accSubAccountCacheUtility.read(accVoucherDetails.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + accSubAccount.description
                break;
            case accSourceCacheUtility.SOURCE_TYPE_ITEM:
                String sourceType = accSourceCacheUtility.read(accVoucherDetails.sourceTypeId).key
                Item item = (Item) itemCacheUtility.read(accVoucherDetails.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + item.name
                break;
            case accSourceCacheUtility.SOURCE_TYPE_LC:
                String sourceType = accSourceCacheUtility.read(accVoucherDetails.sourceTypeId).key
                AccLc accLc = (AccLc) accLcCacheUtility.read(accVoucherDetails.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + accLc.lcNo
                break;
            case accSourceCacheUtility.SOURCE_TYPE_IPC:
                String sourceType = accSourceCacheUtility.read(accVoucherDetails.sourceTypeId).key
                AccIpc accIpc = (AccIpc) accIpcCacheUtility.read(accVoucherDetails.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + accIpc.ipcNo
                break;
            case accSourceCacheUtility.SOURCE_TYPE_LEASE_ACCOUNT:
                String sourceType = accSourceCacheUtility.read(accVoucherDetails.sourceTypeId).key
                AccLeaseAccount accLeaseAccount = (AccLeaseAccount) accLeaseAccountCacheUtility.read(accVoucherDetails.sourceId)
                sourceTypeAndName = sourceType + Tools.COLON + Tools.SINGLE_SPACE + accLeaseAccount.institution
                break;
            default:
                break
        }
        return sourceTypeAndName
    }
}
