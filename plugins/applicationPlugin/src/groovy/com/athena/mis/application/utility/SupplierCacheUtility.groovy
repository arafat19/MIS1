package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.service.SupplierService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *  For details go through Use-Case doc named 'SupplierCacheUtility'
 */
@Component("supplierCacheUtility")
class SupplierCacheUtility extends ExtendedCacheUtility {
    @Autowired
    SupplierService supplierService

    public static final String SORT_ON_NAME = "name";

    /**
     * pull all list of suppliers and store list in cache
     */
    public void init() {
        List list = supplierService.list();
        super.setList(list)
    }

    /**
     * get supplierList of a specific supplier type
     * @param supplierTypeId -SystemEntity.id
     * @return -list of supplier(s)
     */
    public List listBySupplierTypeId(long supplierTypeId) {
        List<Supplier> supplierList = (List<Supplier>) this.list()
        List newSupplierList = []
        for (int i = 0; i < supplierList.size(); i++) {
            if (supplierList[i].supplierTypeId == supplierTypeId)
                newSupplierList << supplierList[i]
        }
        return newSupplierList
    }

    public int countBySupplierTypeId(long supplierTypeId) {
        int count = 0
        List<Supplier> lstAll = (List<Supplier>) list()
        for (int i = 0; i < lstAll.size(); i++) {
            if (lstAll[i].supplierTypeId == supplierTypeId) {
                count++
            }
        }
        return count
    }
}