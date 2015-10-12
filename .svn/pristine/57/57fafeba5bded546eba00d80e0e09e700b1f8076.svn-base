package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccType
import com.athena.mis.accounting.utility.AccTypeCacheUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class AccTypeService extends BaseService {

    static transactional = false

    @Autowired
    AccTypeCacheUtility accTypeCacheUtility

    public List list() {
        return AccType.list(sort: accTypeCacheUtility.SORT_ON_NAME, order: accTypeCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    public AccType read(long id) {
        return AccType.read(id)
    }

    public AccType create(AccType accType) {
        AccType newAccType = accType.save(false)
        return newAccType
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM acc_type
                      WHERE
                          id=:id
    """

    public boolean delete(long id) {
        int updateCount = executeUpdateSql(QUERY_DELETE, [id: id])
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to delete account type')
        }
        return true
    }

    public void createDefaultData(long companyId) {
        AccType accType1 = new AccType()
        accType1.name = "Asset"
        accType1.prefix = 'A'
        accType1.description = "Description of asset"
        accType1.orderId = 0
        accType1.systemAccountType = accTypeCacheUtility.ASSET
        accType1.companyId = companyId
        accType1.coaCount = 0
        accType1.save()

        AccType accType2 = new AccType()
        accType2.name = "Liabilities"
        accType2.prefix = 'L'
        accType2.description = "Description of liabilities"
        accType2.orderId = 0
        accType2.systemAccountType = accTypeCacheUtility.LIABILITIES
        accType2.companyId = companyId
        accType2.coaCount = 0
        accType2.save()

        AccType accType3 = new AccType()
        accType3.name = "Income"
        accType3.prefix = 'I'
        accType3.description = "Description of income"
        accType3.orderId = 0
        accType3.systemAccountType = accTypeCacheUtility.INCOME
        accType3.companyId = companyId
        accType3.coaCount = 0
        accType3.save()

        AccType accType4 = new AccType()
        accType4.name = "Expenses"
        accType4.prefix = 'E'
        accType4.description = "Description of expenses"
        accType4.orderId = 0
        accType4.systemAccountType = accTypeCacheUtility.EXPENSE
        accType4.companyId = companyId
        accType4.coaCount = 0
        accType4.save()
    }

    private static final String ACC_TYPE_UPDATE_QUERY = """
                    UPDATE acc_type
                    SET
                          version=:newVersion,
                          name=:name,
                          order_id=:orderId,
                          prefix=:prefix,
                          description=:description
                    WHERE
                          id=:id AND
                          version=:version
    """

    public int update(AccType accType) {
        Map queryParams = [
                id: accType.id,
                version: accType.version,
                newVersion: accType.version + 1,
                name: accType.name,
                orderId: accType.orderId,
                prefix: accType.prefix,
                description: accType.description
        ]
        int updateCount = executeUpdateSql(ACC_TYPE_UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating Acc Type information')
        }
        return updateCount
    }
}
