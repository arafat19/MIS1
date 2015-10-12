package com.athena.mis.procurement.actions.report.supplierwisepo

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.service.SupplierService
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.procurement.model.ProcSupplierWisePOModel
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * Show Supplier Wise Purchase Order for report
 * For details go through Use-Case doc named 'ShowForSupplierWisePOActionService'
 */
class ShowForSupplierWisePOActionService extends BaseService implements ActionIntf {

    SupplierService supplierService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility
    @Autowired
    SupplierCacheUtility supplierCacheUtility

    private static final String FAILURE_MSG = "Fail to load supplier wise PO report."
    private static final String FROM_DATE = "fromDate"
    private static final String TO_DATE = "toDate"
    private static final String SORT_NAME = "poId"
    private static final String SUPPLIER_WISE_PO_LIST = "supplierWisePoList"
    private static final String COUNT = "count"
    private static final String SUPPLIER_ID = "supplierId"
    private static final String SUPPLIER_NAME = "supplierName"
    private static final String PROJECT_ID = "projectId"
    private static final String LABEL_FOR_OVER_SUPPLY = """<span style="color: #cd0a0a ">(Extra)</span>"""
    private static final String USER_HAS_NO_PROJECT = "User is not associated with any project"

    private final Logger log = Logger.getLogger(getClass())
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
     * 1. initialize params for pagination if necessary
     * 2. receive supplier id, project id, date range from UI
     * 3. pull item type ids
     * 4. pull projects
     * 5. pull supplier wise po
     * 6. wrap object for grid entity
     * @param parameters - serialized parameters from UI
     * @param obj - N/A
     * @return -a map containing supplier wise po list, supplier id, supplier name, project id, date range
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters

            if (params.projectId && params.supplierId && params.fromDate && params.toDate) {
                if (!params.rp) {
                    params.rp = 20
                    params.page = 1
                }

                if (!params.sortname) {
                    params.sortname = SORT_NAME
                    params.sortorder = ASCENDING_SORT_ORDER
                }
                initPager(params)

                long supplierId = Long.parseLong(params.supplierId.toString())
                long projectId = Long.parseLong(params.projectId.toString())
                Date fromDate = DateUtility.parseMaskedFromDate(params.fromDate.toString())
                Date toDate = DateUtility.parseMaskedToDate(params.toDate.toString())

                List<ProcSupplierWisePOModel> lstSupplierWisePo = []
                int count = 0

                List<Long> lstItemTypeIds = []
                List<Long> lstProjectIds = []

                lstItemTypeIds = itemTypeCacheUtility.getAllItemTypeIds()

                if (projectId <= 0) {
                    lstProjectIds = (List<Long>) procSessionUtil.appSessionUtil.getUserProjectIds()
                    if (lstProjectIds.size() == 0) {
                        result.put(Tools.MESSAGE, USER_HAS_NO_PROJECT)
                        return result
                    }
                } else {
                    lstProjectIds << new Long(projectId)
                }

                lstSupplierWisePo = ProcSupplierWisePOModel.listSupplierWisePO(supplierId, lstProjectIds, lstItemTypeIds, fromDate, toDate).list(offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder)
                count = (int) ProcSupplierWisePOModel.listSupplierWisePO(supplierId, lstProjectIds, lstItemTypeIds, fromDate, toDate).count()

                List supplierWisePOListWrap = wrapSupplierWisePOListInGridEntityList(lstSupplierWisePo, start)
                Map gridObjSupplierWisePO = [page: pageNumber, total: count, rows: supplierWisePOListWrap]
                Supplier supplier = supplierService.read(supplierId)
                result.put(SUPPLIER_WISE_PO_LIST, gridObjSupplierWisePO)
                result.put(COUNT, count)
                result.put(SUPPLIER_ID, supplierId)
                result.put(PROJECT_ID, projectId)
                result.put(FROM_DATE, DateUtility.getDateForUI(fromDate))
                result.put(TO_DATE, DateUtility.getDateForUI(toDate))
                result.put(SUPPLIER_NAME, supplier.name)
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
     * Get supplier wise supplier list
     * @param supplierWisePoList - supplier wise po list
     * @param start - starting point of index
     * @return - wrapped supplier wise supplier list
     */
    private List wrapSupplierWisePOListInGridEntityList(List<ProcSupplierWisePOModel> supplierWisePoList, int start) {
        List lstSupplierWisePo = [] as List

        int counter = start + 1
        ProcSupplierWisePOModel supplierWisePo
        GridEntity obj
        double poQuantity
        double storeInQuantity
        double fixedAssetQuantity
        double remainingQuantity
        double payableAmount
        String remainingQuantityStr
        for (int i = 0; i < supplierWisePoList.size(); i++) {
            supplierWisePo = supplierWisePoList[i]
            obj = new GridEntity()
            obj.id = supplierWisePo.poId

            poQuantity = supplierWisePo.quantity
            storeInQuantity = supplierWisePo.storeInQuantity
            fixedAssetQuantity = supplierWisePo.fixedAssetQuantity
            payableAmount = supplierWisePo.storeInAmount + supplierWisePo.fixedAssetAmount
            remainingQuantity = poQuantity - (storeInQuantity + fixedAssetQuantity)
            remainingQuantityStr = Tools.formatAmountWithoutCurrency(remainingQuantity) + Tools.SINGLE_SPACE + supplierWisePo.itemUnit
            if (remainingQuantity < 0) {
                remainingQuantity = (-1) * remainingQuantity
                remainingQuantityStr = Tools.formatAmountWithoutCurrency(remainingQuantity) + Tools.SINGLE_SPACE + supplierWisePo.itemUnit + Tools.SINGLE_SPACE + LABEL_FOR_OVER_SUPPLY
            }

            obj.cell = [
                    counter,
                    supplierWisePo.poId,
                    supplierWisePo.itemName,
                    supplierWisePo.strRate,
                    Tools.formatAmountWithoutCurrency(poQuantity) + Tools.SINGLE_SPACE + supplierWisePo.itemUnit,
                    Tools.formatAmountWithoutCurrency(storeInQuantity) + Tools.SINGLE_SPACE + supplierWisePo.itemUnit,
                    Tools.formatAmountWithoutCurrency(fixedAssetQuantity) + Tools.SINGLE_SPACE + supplierWisePo.itemUnit,
                    remainingQuantityStr,
                    supplierWisePo.strPoAmount,
                    Tools.makeAmountWithThousandSeparator(payableAmount)
            ]

            lstSupplierWisePo << obj
            counter++
        }
        return lstSupplierWisePo
    }
}
