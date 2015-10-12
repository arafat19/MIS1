package com.athena.mis.accounting.actions.accvoucher

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.*
import com.athena.mis.accounting.service.AccIouSlipService
import com.athena.mis.accounting.service.AccVoucherDetailsService
import com.athena.mis.accounting.service.AccVoucherService
import com.athena.mis.accounting.utility.*
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.service.SystemEntityService
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.integration.procurement.ProcurementPluginConnector
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.json.JSONElement
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Common class for updating all type of voucher. eg.Pay/Receive(Cash/Bank), Journal etc
 *  For details go through Use-Case doc named 'UpdateAccVoucherActionService'
 */
class UpdateAccVoucherActionService extends BaseService implements ActionIntf {

    SystemEntityService systemEntityService
    AccVoucherService accVoucherService
    AccVoucherDetailsService accVoucherDetailsService
    AccIouSlipService accIouSlipService
    ProcurementPluginConnector procurementImplService
    @Autowired
    AccChartOfAccountCacheUtility accChartOfAccountCacheUtility
    @Autowired
    AccVoucherTypeCacheUtility accVoucherTypeCacheUtility
    @Autowired
    AccInstrumentTypeCacheUtility accInstrumentTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil
    @Autowired
    AccFinancialYearCacheUtility accFinancialYearCacheUtility

    private static final String ACC_VOUCHER_UPDATE_FAILURE_MSG = "Voucher has not been saved"
    private static final String ACC_VOUCHER_UPDATE_SUCCESS_MSG = "Voucher has been successfully updated"
    private static final String INPUT_VALIDATION_ERROR_MSG = "Error occurred due to invalid input"
    private static final String PO_NOT_FOUND = "Purchase Order not found"
    private static final String PO_NOT_APPROVED = "Purchase Order not approved"
    private static final String DEFAULT_ERROR_MESSAGE = "Failed to update Voucher"
    private static final String ACC_VOUCHER = "accVoucher"
    private static final String LST_VOUCHER_DETAILS_DR = "lstVoucherDetailsDr"
    private static final String LST_VOUCHER_DETAILS_CR = "lstVoucherDetailsCr"
    private static final String EXCESS_PO_AMOUNT = "Voucher amount exceeds available PO amount"
    private static final String ACC_VOUCHER_POSTED_MASSAGE = "Selected voucher is Posted"
    private static final String IOU_NOT_FOUND = "IOU not found"
    private static final String IOU_NOT_APPROVED = "IOU not approved"
    private static final String EXCESS_IOU_AMOUNT = "Voucher amount exceeds available IOU amount"
    private static final String CHEQUE_NO_NOT_FOUND = "Enter cheque No"
    private static final String CHEQUE_DATE_NOT_FOUND = "Enter cheque date"
    private static final String MSG_PROJECT_NOT_SAME_FOR_PO = "Voucher project is not similar to PO project"
    private static final String MSG_PROJECT_NOT_SAME_FOR_IOU = "Voucher project is not similar to IOU project"
    private static final String PROJECT_NOT_FOUND = "Selected project not found"
    private static final String NOTE_NOT_FOUND = "Note not found"
    private static final String INVALID_INSTRUMENT_TYPE_ERROR_MSG = "Invalid instrument type "
    private static final String PO_SOURCE_NOT_MATCHED = "Supplier of given PO is not similar to source of : "
    private static final String IOU_SOURCE_NOT_MATCHED = "Employee of given IOU is not similar to source of : "
    private static final String SOURCE_NOT_FOUND = "Please select the corresponding source of instrument"
    private static final String DR_CR_NOT_SAME = "Debit and Credit amount are not same"

    private Logger log = Logger.getLogger(getClass())
    /**
     *   validate different properties of voucher,
     *   validate chequeInfo(For PayCheck & ReceiveCheck),
     *   if instrument is given then Check existence of Instrument, AvailableAmount, if instrument belongs to selected project
     *
     * @Params parameters -Receives the serialized parameters sent from UI e.g inventoryId, projectId, voucherDate etc.
     * @Params obj -N/A
     *
     * @Return -Map containing isError(true/false), Parent(accVoucher), Children(lstVoucherDetailsDr & lstVoucherDetailsCr)
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)            // default value
            GrailsParameterMap params = (GrailsParameterMap) parameters

            long voucherId = Long.parseLong(params.id.toString())

            AccVoucher existingVoucher = accVoucherService.read(voucherId)
            if (existingVoucher.isVoucherPosted) {
                result.put(Tools.MESSAGE, ACC_VOUCHER_POSTED_MASSAGE)
                return result
            }

            long projectId = Long.parseLong(params.projectId.toString())
            if (projectId < 0) {
                result.put(Tools.MESSAGE, INPUT_VALIDATION_ERROR_MSG)
                return result
            }
            Project project = (Project) projectCacheUtility.read(projectId)
            if (!project) {
                result.put(Tools.MESSAGE, PROJECT_NOT_FOUND)
                return result
            }

            // Create voucher Object
            AccVoucher accVoucher = buildVoucherObject(params)

            //Check current financial year
            Date voucherDate = accVoucher.voucherDate
            AccFinancialYear accFinancialYear = accFinancialYearCacheUtility.getCurrentFinancialYear()
            if (accFinancialYear) {
                String exceedsFinancialYear = DateUtility.checkFinancialDate(voucherDate, accFinancialYear)
                if (exceedsFinancialYear) {
                    result.put(Tools.MESSAGE, exceedsFinancialYear)
                    return result
                }
            }

            if (accVoucher.note == null) {                   // Check- VoucherNote is given or not
                result.put(Tools.MESSAGE, NOTE_NOT_FOUND)
                return result
            }
            //For PayCheck & ReceiveCheck validate check information
            String chequeValidationMsg = validateCheque(accVoucher)
            if (chequeValidationMsg) {
                result.put(Tools.MESSAGE, chequeValidationMsg)
                return result
            }

//            Boolean isSourceFound = new Boolean(false)        // check if any source found either in Dr/Cr(only if PO/IOU exists)

            // Create voucher-details Objects for DEBIT
            JSONElement gridModelDr = JSON.parse(params.gridModelDr.toString())
            List lstRowsDr = (List) gridModelDr.rows             // List of grid object from UI
            List<AccVoucherDetails> lstVoucherDetailsDr = []     // List of voucher details(Dr)
            boolean isDebit = true
            Map drVoucherDetailsListMap = buildVoucherDetailsListAndCheckSource(accVoucher, lstVoucherDetailsDr, lstRowsDr, isDebit, params)
            String sourceErrorMsg = drVoucherDetailsListMap.sourceErrorMsg
            if (sourceErrorMsg) {
                result.put(Tools.MESSAGE, sourceErrorMsg)
                return result
            }
            boolean drSourceFound = drVoucherDetailsListMap.isSourceFound

            // Create voucher-details Objects for CREDIT
            JSONElement gridModelCr = JSON.parse(params.gridModelCr.toString())
            List lstRowsCr = (List) gridModelCr.rows              // List of grid object from UI
            List<AccVoucherDetails> lstVoucherDetailsCr = []      // List of voucher details(Cr)
            isDebit = false
            Map crVoucherDetailsListMap = buildVoucherDetailsListAndCheckSource(accVoucher, lstVoucherDetailsCr, lstRowsCr, isDebit, params)
            sourceErrorMsg = crVoucherDetailsListMap.sourceErrorMsg
            if (sourceErrorMsg) {
                result.put(Tools.MESSAGE, sourceErrorMsg)
                return result
            }

            boolean crSourceFound = drVoucherDetailsListMap.isSourceFound

            // instrumentId > 0 then check that at least 1 VoucherDetails has source
            if ((accVoucher.instrumentId > 0) && (!drSourceFound.booleanValue() && !crSourceFound.booleanValue())) {
                result.put(Tools.MESSAGE, SOURCE_NOT_FOUND)
                return result
            }

            //if IOU/PO is given : Check Available-Amount
            if (accVoucher.instrumentId > 0) {
                String errorMsg = checkInstrumentAvailableAmount(accVoucher.id, accVoucher.projectId, accVoucher.amount, accVoucher.instrumentTypeId, accVoucher.instrumentId)
                if (errorMsg) {
                    result.put(Tools.MESSAGE, errorMsg)
                    return result
                }
            }

            result.put(ACC_VOUCHER, accVoucher)
            result.put(LST_VOUCHER_DETAILS_DR, lstVoucherDetailsDr)
            result.put(LST_VOUCHER_DETAILS_CR, lstVoucherDetailsCr)
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
     * Method to save parents(AccVoucher) & Children(lstVoucherDetailsDr & lstVoucherDetailsCr)
     *
     * @Params parameters -N/A
     * @Params obj -Receives map from preCondition which contains parents(AccVoucher) & children(lstVoucherDetailsDr & lstVoucherDetailsCr)
     *
     * @Return -Map contains isError(true/false), parents(AccVoucher) object
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap preResult = (LinkedHashMap) obj
            AccVoucher accVoucher = (AccVoucher) preResult.get(ACC_VOUCHER)
            List<AccVoucherDetails> lstVoucherDetailsDr = (List<AccVoucherDetails>) preResult.get(LST_VOUCHER_DETAILS_DR)
            List<AccVoucherDetails> lstVoucherDetailsCr = (List<AccVoucherDetails>) preResult.get(LST_VOUCHER_DETAILS_CR)

            //Update Parent Object(AccVoucher)
            AccVoucher newAccVoucher = accVoucherService.update(accVoucher)

            // first eliminate all voucher details for this voucher
            deleteDetailsForVoucherUpdate(accVoucher.id)
            // Update child's Objects (Dr details)
            for (int i = 0; i < lstVoucherDetailsDr.size(); i++) {
                accVoucherDetailsService.create(lstVoucherDetailsDr[i])
            }
            // Update child's Objects (Cr details)
            for (int i = 0; i < lstVoucherDetailsCr.size(); i++) {
                accVoucherDetailsService.create(lstVoucherDetailsCr[i])
            }
            result.put(ACC_VOUCHER, newAccVoucher)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_VOUCHER_UPDATE_FAILURE_MSG)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj) {
        //there are not post condition
        return null
    }
    /**
     *  Method to wrap voucher for grid
     *
     * @Params obj -Receives map from execute which contains parents(accVoucher) object
     * @return -a map containing all objects necessary for grid data
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap executeResult = (LinkedHashMap) obj
            AccVoucher voucher = (AccVoucher) executeResult.get(ACC_VOUCHER)
            GridEntity object = new GridEntity()
            String voucherDate = DateUtility.getLongDateForUI(voucher.voucherDate)
            String instrument = null
            if (voucher.instrumentId > 0) {
                SystemEntity instrumentTypeName = systemEntityService.read(voucher.instrumentTypeId)
                instrument = instrumentTypeName.key + Tools.COLON + Tools.SINGLE_SPACE + voucher.instrumentId
            }
            object.id = voucher.id
            object.cell = [
                    Tools.LABEL_NEW,
                    voucher.id,
                    voucher.traceNo,
                    Tools.makeAmountWithThousandSeparator(voucher.amount),
                    voucher.drCount,
                    voucher.crCount,
                    voucherDate,
                    voucher.isVoucherPosted ? Tools.YES : Tools.NO,
                    instrument,
                    voucher.chequeNo ? voucher.chequeNo : Tools.EMPTY_SPACE
            ]
            Map resultMap = [entity: object, version: voucher.version]
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            result.put(Tools.MESSAGE, ACC_VOUCHER_UPDATE_SUCCESS_MSG)
            result.put(ACC_VOUCHER, resultMap)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_VOUCHER_UPDATE_FAILURE_MSG)
            return result
        }
    }
    /**
     * build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message to create AccVoucher
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
            result.put(Tools.MESSAGE, ACC_VOUCHER_UPDATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, ACC_VOUCHER_UPDATE_FAILURE_MSG)
            return result
        }
    }
    /**
     *  Method used to build accVoucherDetails Object, if source found the validate source, count voucherDetails and set AccVoucher.amount
     *
     * @Param accVoucher -AccVoucher object
     * @Param lstVoucherDetails -List of AccVoucherDetails object
     * @Param lstGridVoucherDetails -List of grid object from UI
     * @Param isDebit -isDebitVoucher or idCreditVoucher
     * @Param isSourceFound -used to match source(in case PO/IOU)
     * @Param params -GrailsParameterMap
     *
     * @Return -If any inconsistency found then return specific message Otherwise return null
     */
    private Map buildVoucherDetailsListAndCheckSource(AccVoucher accVoucher, List<AccVoucherDetails> lstVoucherDetails,
                                                      List lstGridVoucherDetails, boolean isDebit, GrailsParameterMap params) {
        boolean isSourceFound = Tools.FALSE

        double amount = 0.0d
        for (int i = 0; i < lstGridVoucherDetails.size(); i++) {
            AccVoucherDetails voucherDetails = createVoucherDetails(lstGridVoucherDetails[i], isDebit, params, accVoucher)

            if (accVoucher.instrumentId > 0) {
                if (voucherDetails.sourceId > 0) {
                    isSourceFound = Tools.FALSE   // at least a single source found
                    // now check if given source is similar to Po.supplier/IOU.employee
                    String sourceErrorMsg = isSameSource(accVoucher, voucherDetails)
                    if (sourceErrorMsg) {
                        return [sourceErrorMsg: sourceErrorMsg, isSourceFound: isSourceFound]
                    }
                }
            }

            lstVoucherDetails.add(voucherDetails)
            amount += (voucherDetails.amountDr + voucherDetails.amountCr)       // accumulate amount to set in parent Object
        }
        int detailsCount = lstVoucherDetails.size()
        if (isDebit) {
            accVoucher.drCount = detailsCount
        } else {
            accVoucher.crCount = detailsCount
        }
        if ((accVoucher.amount > 0.0d) && (accVoucher.amount != amount)) {   // check if sum(dr)==sum(cr)
            return [checkSourceMsg: DR_CR_NOT_SAME, isSourceFound: isSourceFound]
        }
        accVoucher.amount = amount  // set amount in parent object
        return [checkSourceMsg: null, isSourceFound: isSourceFound]
    }

    /**
     *  Method used to update accVoucher Object
     * @Param params -GrailsParameterMap
     * @Return AccVoucher Object
     */
    private AccVoucher buildVoucherObject(GrailsParameterMap params) {

        // If IOU/PO no. given then set TypeId otherwise set 0
        params.instrumentTypeId = params.instrumentId ? params.instrumentTypeId : 0
        AccVoucher accVoucher = new AccVoucher(params)

        String strVoucherDate = params.voucherDate
        Date voucherDate = DateUtility.parseMaskedDate(strVoucherDate)
        int financialMonth = Integer.parseInt(voucherDate.format('MM'))
        int financialYear = Integer.parseInt(voucherDate.format('yyyy'))

        accVoucher.id = Long.parseLong(params.id.toString())
        accVoucher.projectId = Long.parseLong(params.projectId.toString())
        accVoucher.version = Integer.parseInt(params.version.toString())
        accVoucher.companyId = accSessionUtil.appSessionUtil.getAppUser().companyId
        accVoucher.moduleId = 0
        accVoucher.financialMonth = financialMonth
        accVoucher.financialYear = financialYear
        accVoucher.isVoucherPosted = Boolean.parseBoolean(params.hidIsVoucherPosted.toString())
        accVoucher.traceNo = params.hidTraceNo
        accVoucher.chequeDate = params.chequeDate ? DateUtility.parseMaskedDate(params.chequeDate.toString()) : null
        accVoucher.voucherDate = voucherDate
        accVoucher.createdBy = accSessionUtil.appSessionUtil.getAppUser().id
        accVoucher.createdOn = new Date()
        accVoucher.updatedBy = 0
        accVoucher.postedBy = 0
        return accVoucher
    }
    /**
     *  Method used to update accVoucherDetails Object(Child)
     *
     * @Param accVoucher -AccVoucher Object
     * @Param params -GrailsParameterMap
     * @Param gridModelRow -Grid data receives from UI
     * @Param isDebit -boolean value to distinguish is it drAmount OR crAmount
     *
     * @Return AccVoucherDetails Object
     */
    private AccVoucherDetails createVoucherDetails(def gridModelRow, boolean isDebit, GrailsParameterMap params, AccVoucher accVoucher) {

        AccVoucherDetails accVoucherDetails = new AccVoucherDetails()
        accVoucherDetails.version = accVoucher.version
        AccChartOfAccount coa = (AccChartOfAccount) accChartOfAccountCacheUtility.read(gridModelRow.cell[0].toLong())

        accVoucherDetails.projectId = params.projectId.toLong()
        accVoucherDetails.divisionId = gridModelRow.cell[9].equals(Tools.EMPTY_SPACE) ? 0L : Long.parseLong(gridModelRow.cell[9].toString())
        accVoucherDetails.voucherId = accVoucher.id
        accVoucherDetails.rowId = (gridModelRow.cell[8]) ? Integer.parseInt(gridModelRow.cell[8].toString()) : 0
        accVoucherDetails.coaId = coa.id
        accVoucherDetails.groupId = coa.accGroupId
        accVoucherDetails.sourceId = Long.parseLong(gridModelRow.cell[7].toString()) //employee id, customer id  (0 if None)
        accVoucherDetails.sourceTypeId = coa.accSourceId   // None, Supplier, Employee etc.
        accVoucherDetails.sourceCategoryId = coa.sourceCategoryId   // None, Supplier, Employee etc.
        accVoucherDetails.amountDr = (isDebit ? Double.parseDouble(gridModelRow.cell[3].toString()) : 0.0d)
        accVoucherDetails.amountCr = (!isDebit ? Double.parseDouble(gridModelRow.cell[3].toString()) : 0.0d)
        accVoucherDetails.particulars = (gridModelRow.cell[4]) ? gridModelRow.cell[4] : null

        accVoucherDetails.createdBy = accSessionUtil.appSessionUtil.getAppUser().id
        accVoucherDetails.createdOn = new Date()
        //accVoucherDetails.updatedBy = 0L

        return accVoucherDetails
    }

    private static final String AVAILABLE_PO_AMOUNT_QUERY = """
                   SELECT (po.total_amount - coalesce(av.total_amount,0)) AS available_amount
                            FROM
                         (SELECT total_price as total_amount
                            FROM proc_purchase_order
                            WHERE id=:purchaseOrderId) po,
                            (SELECT sum(amount) as total_amount
                             FROM acc_voucher
                             WHERE instrument_id =:purchaseOrderId
                             AND instrument_type_id= :instrumentTypeId
                             AND id <>:accVoucherId) av
                """
    /**
     *  Method returns available PO Amount of a particular PO
     * @Params purchaseOrderId -id of PurchaseOrder Object (PurchaseOrder.Id)
     * @Return -availablePOAmount
     */
    //@todo adjust using existing sql query
    private double getAvailablePOAmountForUpdate(long purchaseOrderId, long accVoucherId, long instrumentPoId) {
        Map queryParams = [
                purchaseOrderId: purchaseOrderId,
                accVoucherId: accVoucherId,
                instrumentTypeId: instrumentPoId
        ]
        List<GroovyRowResult> result = executeSelectSql(AVAILABLE_PO_AMOUNT_QUERY, queryParams)
        double totalCost = 0D
        if (result[0].available_amount) {
            totalCost = Double.parseDouble(result[0].available_amount.toString())
        }
        return totalCost
    }

    // delete all voucher details for a given voucher (used to update voucher)
    private static final String QUERY_DELETE = """
            DELETE FROM acc_voucher_details
            WHERE voucher_id = :voucherId
        """

    private void deleteDetailsForVoucherUpdate(long voucherId) {
        Map queryParams = [voucherId: voucherId]
        int deleteCount = executeUpdateSql(QUERY_DELETE, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('Failed to delete voucher details')
        }
    }

    private static final String SELECT_QUERY = """
                       SELECT (iou_slip.total_purpose_amount - coalesce(av.total_amount,0)) AS available_amount
                            FROM
                         (SELECT total_purpose_amount
                            FROM acc_iou_slip
                            WHERE id =:iouSlipId) iou_slip,
                            (SELECT sum(amount) as total_amount
                             FROM acc_voucher
                             WHERE instrument_id =:iouSlipId
                             AND instrument_type_id= :instrumentTypeId
                             AND id <>:accVoucherId) av;
                """
    /**
     *  Method returns available IOU Amount of a particular IOU slip
     * @Params iouSlipId -id of IouSlip Object (IouSlip.Id)
     * @Return -availableIouAmount
     */
    private double getAvailableIOUAmountForUpdate(long iouSlipId, long accVoucherId, long instrumentIouId) {
        Map queryParams = [
                iouSlipId: iouSlipId,
                accVoucherId: accVoucherId,
                instrumentTypeId: instrumentIouId
        ]
        List<GroovyRowResult> result = executeSelectSql(SELECT_QUERY, queryParams)
        double totalCost = 0D
        if (result[0].available_amount) {
            totalCost = Double.parseDouble(result[0].available_amount.toString())
        }
        return totalCost
    }
    /**
     *  Method checks if given source is similar to PO.supplier/IOU.employee
     *
     *  if INSTRUMENT_TYPE = PO :  Check if PO supplier and selected supplier is same or not
     *  if INSTRUMENT_TYPE = IOU :  Check if IOU employee and selected employee is same or not
     *
     * @Params accVoucher -AccVoucher Object
     * @Params accVoucherDetails -AccVoucherDetails Object
     *
     * @Return -If any inconsistency found then return specific message
     *           Otherwise return null
     */
    private String isSameSource(AccVoucher accVoucher, AccVoucherDetails accVoucherDetails) {
        String msg = null
        // pull system_entity object
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity instrumentPoObj = (SystemEntity) accInstrumentTypeCacheUtility.readByReservedAndCompany(accInstrumentTypeCacheUtility.INSTRUMENT_PO_ID, companyId)
        SystemEntity instrumentIouObj = (SystemEntity) accInstrumentTypeCacheUtility.readByReservedAndCompany(accInstrumentTypeCacheUtility.INSTRUMENT_IOU_ID, companyId)

        switch (accVoucher.instrumentTypeId) {
        // For PO check if : given SupplierId == SupplierId of given PO
            case instrumentPoObj.id:
                Object purchaseOrder = procurementImplService.readPO(accVoucher.instrumentId)
                if (!purchaseOrder) {
                    return PO_NOT_FOUND
                }
                long supplierId = purchaseOrder.supplierId
                if (supplierId != accVoucherDetails.sourceId) {
                    AccChartOfAccount chartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(accVoucherDetails.coaId)
                    msg = PO_SOURCE_NOT_MATCHED + chartOfAccount.code
                }
                break
        // For IOU check if : given EmployeeId == EmployeeId of given IouSlip
            case instrumentIouObj.id:
                AccIouSlip accIouSlip = accIouSlipService.read(accVoucher.instrumentId)
                if (!accIouSlip) {
                    return IOU_NOT_FOUND
                }
                if (accIouSlip.employeeId != accVoucherDetails.sourceId) {
                    AccChartOfAccount chartOfAccount = (AccChartOfAccount) accChartOfAccountCacheUtility.read(accVoucherDetails.coaId)
                    msg = IOU_SOURCE_NOT_MATCHED + chartOfAccount.code
                }
                break
            default:
                msg = INVALID_INSTRUMENT_TYPE_ERROR_MSG
        }
        return msg
    }
    /**
     *  Method checks the available instrument amount
     *
     * @Params voucherProjectId -id of Project Object (Project.Id)
     * @Params instrumentTypeId -id of SystemEntity Object (SystemEntity.Id)
     * @Params instrumentId -id of corresponding instrument Object (PO.Id, IouSlip.id etc)
     *
     * @Return -If any inconsistency found then return specific message
     *           Otherwise return null
     */
    private String checkInstrumentAvailableAmount(long accVoucherId, long voucherProjectId, double voucherAmount, long instrumentTypeId, long instrumentId) {
        // pull system_entity object
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity instrumentPoObj = (SystemEntity) accInstrumentTypeCacheUtility.readByReservedAndCompany(accInstrumentTypeCacheUtility.INSTRUMENT_PO_ID, companyId)
        SystemEntity instrumentIouObj = (SystemEntity) accInstrumentTypeCacheUtility.readByReservedAndCompany(accInstrumentTypeCacheUtility.INSTRUMENT_IOU_ID, companyId)

        switch (instrumentTypeId) {
        //if the voucher is related with po then check po amount
            case instrumentPoObj.id:
                Object purchaseOrder = procurementImplService.readPO(instrumentId)
                if (!purchaseOrder) {
                    return PO_NOT_FOUND
                }
                if (purchaseOrder.projectId != voucherProjectId) {
                    return MSG_PROJECT_NOT_SAME_FOR_PO
                }
                if (purchaseOrder.approvedByDirectorId <= 0 || purchaseOrder.approvedByProjectDirectorId <= 0) {
                    return PO_NOT_APPROVED
                }
                double availablePOAmount = getAvailablePOAmountForUpdate(instrumentId, accVoucherId, instrumentPoObj.id)
                if (voucherAmount > availablePOAmount) {
                    return EXCESS_PO_AMOUNT
                }
                break
        //if the voucher is related with IOU Slip then check IOU amount
            case instrumentIouObj.id:

                AccIouSlip accIouSlip = accIouSlipService.read(instrumentId)
                if (!accIouSlip) {
                    return IOU_NOT_FOUND
                }
                if (accIouSlip.projectId != voucherProjectId) {
                    return MSG_PROJECT_NOT_SAME_FOR_IOU
                }
                if (accIouSlip.approvedBy <= 0) {
                    return IOU_NOT_APPROVED
                }

                double availableIOUAmount = getAvailableIOUAmountForUpdate(instrumentId, accVoucherId, instrumentIouObj.id)
                if (voucherAmount > availableIOUAmount) {
                    return EXCESS_IOU_AMOUNT
                }
                break
            default:
                return INVALID_INSTRUMENT_TYPE_ERROR_MSG
                break
        }
        return null
    }

    /**
     * For PayCheck & ReceiveCheck validate cheque information
     * @param accVoucher - voucher object
     * @return -If any inconsistency found then return specific message
     *          Otherwise return null
     */
    private String validateCheque(AccVoucher accVoucher) {
        // pull voucher type object
        long companyId = accSessionUtil.appSessionUtil.getCompanyId()
        SystemEntity paymentBank = (SystemEntity) accVoucherTypeCacheUtility.readByReservedAndCompany(accVoucherTypeCacheUtility.PAYMENT_VOUCHER_BANK_ID, companyId)
        SystemEntity receivedBank = (SystemEntity) accVoucherTypeCacheUtility.readByReservedAndCompany(accVoucherTypeCacheUtility.RECEIVED_VOUCHER_BANK_ID, companyId)
        if ((accVoucher.voucherTypeId == paymentBank.id || accVoucher.voucherTypeId == receivedBank.id)
                && !accVoucher.chequeNo) {
            return INPUT_VALIDATION_ERROR_MSG
        }
        if (accVoucher.chequeNo || accVoucher.chequeDate) {
            if (!accVoucher.chequeNo) {
                return CHEQUE_NO_NOT_FOUND
            }
            if (!accVoucher.chequeDate) {
                return CHEQUE_DATE_NOT_FOUND
            }
        }
        return null
    }
}
