package com.athena.mis.procurement.actions.report.supplierwisepo

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.service.SupplierService
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import com.athena.mis.application.utility.ItemTypeCacheUtility
import com.athena.mis.procurement.model.ProcSupplierWisePOModel
import com.athena.mis.procurement.utility.ProcSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * List for Supplier Wise Purchase Order
 * For details go through Use-Case doc named 'ListForSupplierWisePOActionService'
 */
class ListForSupplierWisePOActionService extends BaseService implements ActionIntf {

    SupplierService supplierService
    @Autowired
    ProcSessionUtil procSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ItemTypeCacheUtility itemTypeCacheUtility

    private static final String INVALID_INPUT = "Error occurred due to invalid input"
    private static final String FAILURE_MSG = "Fail to generate supplier wise PO report."
    private static final String SUPPLIER_WISE_PO_LIST = "supplierWisePoList"
    private static final String INVALID_PO = "Please only digits in PO no"
    private static final String COUNT = "count"
    private static final String PO_NOT_FOUND_WITHIN_DATE = "PO list not found within given dates"
    private static final String LABEL_FOR_OVER_SUPPLY = """<span style="color: #cd0a0a ">(Extra)</span>"""
    private static final String SORT_NAME = "poId"
    private static final String SEARCH_BY_PO_ID = "poId"
    private static final String USER_HAS_NO_PROJECT = "User is not associated with any project"
    private static final String SUPPLIER_NAME = "supplierName"

    private final Logger log = Logger.getLogger(getClass())
    /**
     * Check input validation
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return - map containing isError(True/False)
     */
    public Object executePreCondition(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (!params.supplierId || !params.fromDate || !params.toDate) {
                result.put(Tools.MESSAGE, INVALID_INPUT)
                return result
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
            GrailsParameterMap parameterMap = (GrailsParameterMap) parameters
            if (!parameterMap.rp) {
                parameterMap.rp = 20
                parameterMap.page = 1
            }

            if (!parameterMap.sortname) {
                parameterMap.sortname = SORT_NAME
                parameterMap.sortorder = ASCENDING_SORT_ORDER
            }
            initSearch(parameterMap)

            if (parameterMap.projectId.equals(Tools.EMPTY_SPACE)) {
                parameterMap.projectId = 0L
            }
            if (parameterMap.itemTypeId.equals(Tools.EMPTY_SPACE)) {
                parameterMap.itemTypeId = 0L
            }
            List<ProcSupplierWisePOModel> lstSupplierWisePo = []
            long supplierId = Long.parseLong(parameterMap.supplierId.toString())
            long projectId = Long.parseLong(parameterMap.projectId.toString())
            long itemTypeId = Long.parseLong(parameterMap.itemTypeId.toString())
            Date fromDate = DateUtility.parseMaskedFromDate(parameterMap.fromDate.toString())
            Date toDate = DateUtility.parseMaskedToDate(parameterMap.toDate.toString())

            List<Long> lstProjectIds = []
            List<Long> lstItemTypeIds = []

            if (projectId <= 0) {
                lstProjectIds = (List<Long>) procSessionUtil.appSessionUtil.getUserProjectIds()
                if (lstProjectIds.size() == 0) {
                    result.put(Tools.MESSAGE, USER_HAS_NO_PROJECT)
                    return result
                }
            } else {
                lstProjectIds << new Long(projectId)
            }

            if (itemTypeId <= 0) {
                lstItemTypeIds = itemTypeCacheUtility.getAllItemTypeIds()
            } else {
                lstItemTypeIds << new Long(itemTypeId)
            }

            int count = 0
            if (query) {
                long poId = 0L
                if (queryType == SEARCH_BY_PO_ID) {
                    try {
                        poId = Long.parseLong(query)
                    } catch (Exception ex) {
                        result.put(Tools.MESSAGE, INVALID_PO)
                        return result
                    }
                }
                poId = Long.parseLong(query)

                lstSupplierWisePo = ProcSupplierWisePOModel.searchSupplierWisePO(supplierId, poId, lstProjectIds, lstItemTypeIds, fromDate, toDate).list(offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder)
                count = (int) ProcSupplierWisePOModel.searchSupplierWisePO(supplierId, poId, lstProjectIds, lstItemTypeIds, fromDate, toDate).count()
            } else {
                lstSupplierWisePo = ProcSupplierWisePOModel.listSupplierWisePO(supplierId, lstProjectIds, lstItemTypeIds, fromDate, toDate).list(offset: start, max: resultPerPage, sort: sortColumn, order: sortOrder)
                count = (int) ProcSupplierWisePOModel.listSupplierWisePO(supplierId, lstProjectIds, lstItemTypeIds, fromDate, toDate).count()
            }
            if (count == 0) {
                result.put(Tools.MESSAGE, PO_NOT_FOUND_WITHIN_DATE)
                return result
            }
            List supplierWisePOListWrap = wrapSupplierWisePOListInGridEntityList(lstSupplierWisePo, start)

            Supplier supplier = supplierService.read(supplierId)
            result.put(SUPPLIER_WISE_PO_LIST, supplierWisePOListWrap)
            result.put(COUNT, count)
            result.put(SUPPLIER_NAME, supplier.name)
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
     * Get supplier wise purchase order list
     * @param obj - receive object from execute method
     * @return - a map containing wrapped supplier wise purchase order list, supplier name and isError(True/False)
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj
            List supplierWisePoListWrap = (List) executeResult.get(SUPPLIER_WISE_PO_LIST)
            int count = (int) executeResult.get(COUNT)

            Map gridOutput = [page: pageNumber, total: count, rows: supplierWisePoListWrap]
            result.put(SUPPLIER_WISE_PO_LIST, gridOutput)
            result.put(SUPPLIER_NAME, executeResult.get(SUPPLIER_NAME))
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
    /**
     * Wrap supplier wise wise po for grid entity
     * @param supplierWisePoList - supplier wise wise po list
     * @param start - starting point of index
     * @return - wrapped supplier wise wise po list
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