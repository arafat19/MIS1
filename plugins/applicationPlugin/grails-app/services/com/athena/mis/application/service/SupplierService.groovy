package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Supplier
import com.athena.mis.application.utility.SupplierCacheUtility
import com.athena.mis.application.utility.SupplierTypeCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Service class for basic supplier CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'SupplierService'
 */
class SupplierService extends BaseService {

    static transactional = false

    @Autowired
    SupplierCacheUtility supplierCacheUtility
    @Autowired
    SupplierTypeCacheUtility supplierTypeCacheUtility

    /**
     * @return -list of supplier object
     */
    public List list() {
        return Supplier.list(sort: supplierCacheUtility.SORT_ON_NAME, order: supplierCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    /**
     * get specific supplier object from cache by id
     * @param id -Supplier.id
     * @return -supplier object
     */
    public Supplier read(long id) {
        Supplier supplier = (Supplier) supplierCacheUtility.read(id)
        return supplier
    }

    /**
     * get specific supplier object
     * @param id -Supplier.id
     * @return -supplier object
     */
    public Supplier get(long id) {
        Supplier supplier = Supplier.get(id)
        return supplier
    }

    /**
     * Save supplier object in database
     * @param supplier -Supplier object
     * @return -newly created supplier object
     */
    public Supplier create(Supplier supplier) {
        Supplier newInventory = supplier.save();
        return newInventory;
    }

    /**
     * Method to get the count of suppliers
     * @param name - supplier name
     * @param companyId - company id
     * @return - an integer number of supplier count
     */
    public int countByNameIlikeAndCompanyId(String name, long companyId) {
        int supplierCount = Supplier.countByNameIlikeAndCompanyId(name, companyId)
        return supplierCount
    }

    /**
     * Method to get the count of suppliers
     * @param name - supplier name
     * @param companyId - company id
     * @param supplierId - supplier id
     * @return - an integer number of supplier count
     */
    public int countByNameIlikeAndCompanyIdAndIdNotEqual(String name, long companyId, long supplierId) {
        int count = Supplier.countByNameIlikeAndCompanyIdAndIdNotEqual(name, companyId, supplierId)
        return count
    }

    /**
     * Method to count suppliers
     * @param supplierTypeId - supplier type id
     * @param companyId - company id
     * @return - an integer number of supplier count
     */
    public int countBySupplierTypeIdAndCompanyId(long supplierTypeId, long companyId) {
        int count = Supplier.countBySupplierTypeIdAndCompanyId(supplierTypeId, companyId)
        return count
    }

    private static final String UPDATE_QUERY = """
                    UPDATE supplier SET
                          version= :newVersion,
                          name=:name,
                          address=:address,
                          bank_name=:bankName,
                          supplier_type_id=:supplierTypeId,
                          account_name=:accountName,
                          bank_account=:bankAccount,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy
                      WHERE
                          id=:id AND
                          version=:version
                          """
    /**
     * SQL to update supplier object in database
     * @param supplier -Supplier object
     * @return -int value(updateCount)
     */
    public int update(Supplier supplier) {
        Map queryParams = [
                id: supplier.id,
                version: supplier.version,
                newVersion: supplier.version + 1,
                name: supplier.name,
                address: supplier.address,
                bankName: supplier.bankName,
                accountName: supplier.accountName,
                bankAccount: supplier.bankAccount,
                supplierTypeId: supplier.supplierTypeId,
                updatedBy: supplier.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(supplier.updatedOn)
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('error occurred at supplierService.update')
        }
        supplier.version = supplier.version + 1
        return updateCount;
    }

    private static final String QUERY = """
                    UPDATE supplier SET
                          version= :newVersion,
                         item_count=:itemCount
                      WHERE
                          id=:id AND
                          version=:version
                          """
    /**
     * Update item_count field when new item is added
     * @param supplier -Supplier object
     * @return -int value(updateCount)
     */
    public int updateForSupplierItem(Supplier supplier) {
        Map queryParams = [
                newVersion: supplier.version + 1,
                itemCount: supplier.itemCount,
                id: supplier.id,
                version: supplier.version
        ]
        int updateCount = executeUpdateSql(QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('error occurred at supplierService.updateForSupplierItem')
        }
        supplier.version = supplier.version + 1
        return updateCount;
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM supplier
                      WHERE
                          id=:id
                          """
    /**
     * SQL to delete supplier object from database
     * @param id -Supplier.id
     * @return -boolean value
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('error occurred at supplierService.delete')
        }
        return Boolean.TRUE;
    }

    /**
     * insert default supplier into database when application starts with bootstrap
     */
    /*public void createDefaultData() {
        Supplier supplier1 = new Supplier(name: 'Mr. Abdur Rahim', address: '2/5, Housing Staff Quarter, Mirpur-14, Dhaka', bankName: 'Agrani Bank Limited', accountName: 'Mr. Abdur Rahim', bankAccount: 'a/c-547854247', supplierTypeId: supplierTypeCacheUtility.MATERIAL_PROVIDER, companyId: 1, createdBy: 1, createdOn: new Date());
        supplier1.save();
        Supplier supplier2 = new Supplier(name: 'S. M. Reaz Uddin', address: '85 Adabar', bankName: 'SEBL', accountName: 'S. M. Reaz Uddin', bankAccount: 'ACC: 12451215', supplierTypeId: supplierTypeCacheUtility.MATERIAL_PROVIDER, companyId: 1, createdBy: 1, createdOn: new Date());
        supplier2.save();
        Supplier supplier3 = new Supplier(name: 'Abul Kalam', address: 'AD Adabar', bankName: 'SEBL', accountName: 'Abul Kalam', bankAccount: 'ACC: 12451215', supplierTypeId: supplierTypeCacheUtility.MATERIAL_PROVIDER, companyId: 1, createdBy: 1, createdOn: new Date());
        supplier3.save()
        Supplier supplier4 = new Supplier(name: 'Mr. Korim', address: '78 Adabar, Mohammadpur, dhaka', bankName: 'Sonali bank Limited', accountName: 'Mr. Korim', bankAccount: 'ACC: 12451215', supplierTypeId: supplierTypeCacheUtility.MATERIAL_PROVIDER, companyId: 1, createdBy: 1, createdOn: new Date());
        supplier4.save()
        Supplier supplier5 = new Supplier(name: 'Raihan Islam', address: '152 Adabar', bankName: 'EBL', accountName: 'Raihan Islam', bankAccount: 'EPBL:7S5CSE', supplierTypeId: supplierTypeCacheUtility.MATERIAL_PROVIDER, companyId: 1, createdBy: 1, createdOn: new Date());
        supplier5.save()
        Supplier supplier6 = new Supplier(name: 'Nazrul Islam', address: '152 Jatrabary', bankName: 'JBL', accountName: 'Nazrul Islam', bankAccount: 'JBACC: 754', supplierTypeId: supplierTypeCacheUtility.MATERIAL_PROVIDER, companyId: 1, createdBy: 1, createdOn: new Date());
        supplier6.save()
    }*/
}