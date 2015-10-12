package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccVoucherTypeCoa
import com.athena.mis.accounting.utility.AccVoucherTypeCoaCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional


/**
 *  Service class for basic AccVoucherTypeCoaMapping CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'AccVoucherTypeCoaService'
 */
class AccVoucherTypeCoaService extends BaseService {

    static transactional = false

    @Autowired
    AccVoucherTypeCoaCacheUtility accVoucherTypeCoaCacheUtility

    /**
     * get list of AccVoucherTypeCoa objects
     * @return -list of AccVoucherTypeCoa objects
     */
    public List list() {
        return AccVoucherTypeCoa.list(sort: accVoucherTypeCoaCacheUtility.SORT_BY_VOUCHER_ID, order: accVoucherTypeCoaCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    /**
     * Save accVoucherTypeCoa object in database
     * @param accVoucherTypeCoa -AccVoucherTypeCoa object
     * @return -newly created accVoucherTypeCoa object
     */
    public AccVoucherTypeCoa create(AccVoucherTypeCoa accVoucherTypeCoa) {
        accVoucherTypeCoa.save(false)
        return accVoucherTypeCoa
    }

    /**
     * get accVoucherTypeCoa object by id
     * @param id -AccVoucherTypeCoa.id
     * @return -AccVoucherTypeCoa object
     */
    public AccVoucherTypeCoa read(long id) {
        return (AccVoucherTypeCoa) accVoucherTypeCoaCacheUtility.read(id)
    }

    private static final String QUERY_UPDATE =
        """
                    UPDATE acc_voucher_type_coa SET
                          version=:newVersion,
                          acc_voucher_type_id=:accVoucherTypeId,
                          coa_id=:coaId,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy
                      WHERE
                          id=:id AND
                          version=:version
                          """
    /**
     * SQL to update accVoucherTypeCoa object in database
     * @param accVoucherTypeCoa -AccVoucherTypeCoa object
     * @return -updated accVoucherTypeCoa object
     */
    public AccVoucherTypeCoa update(AccVoucherTypeCoa accVoucherTypeCoa) {
        Map queryParams = [
                newVersion: accVoucherTypeCoa.version + 1,
                accVoucherTypeId: accVoucherTypeCoa.accVoucherTypeId,
                coaId: accVoucherTypeCoa.coaId,
                id: accVoucherTypeCoa.id,
                version: accVoucherTypeCoa.version,
                updatedBy: accVoucherTypeCoa.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(accVoucherTypeCoa.updatedOn)]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update voucher type mapping')
        }
        accVoucherTypeCoa.version = accVoucherTypeCoa.version + 1
        return accVoucherTypeCoa
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM acc_voucher_type_coa
                      WHERE id=:id
                    """
    /**
     * Delete AccVoucherTypeCoa object by id
     * @param id -AccVoucherTypeCoa.id
     * @return -boolean value
     */
    public boolean delete(long id) {
        int deleteCount = executeUpdateSql(QUERY_DELETE, [id: id])
        if (deleteCount <= 0) {
            throw new RuntimeException('Failed to delete voucher type mapping')
        }
        return true
    }
}