package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccIpc
import com.athena.mis.accounting.utility.AccIpcCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Service class for basic AccIpc CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'AccIpcService'
 */
class AccIpcService extends BaseService {

    @Autowired
    AccIpcCacheUtility accIpcCacheUtility

    static transactional = false

    /**
     * get list of accIpc objects
     * @return -list of accIpc objects
     */
    public List list() {
        return AccIpc.list(sort: accIpcCacheUtility.SORT_BY_ID, order: accIpcCacheUtility.SORT_ORDER_DESCENDING)
    }

    /**
     * get AccIpc object by id
     * @param id -AccIpc.id
     * @return -AccIpc object
     */
    public AccIpc read(long id) {
        AccIpc accIpc = AccIpc.read(id)
        return accIpc
    }

    /**
     * Save AccIpc object in database
     * @param accIpc -AccIpc object
     * @return -newly created accIpc object
     */
    public AccIpc create(AccIpc accIpc) {
        AccIpc newAccIpc = accIpc.save(false)
        return newAccIpc
    }

    private static final String QUERY_UPDATE = """
                        UPDATE acc_ipc SET
                            version=:newVersion,
                            ipc_no=:ipcNo,
                            project_id=:projectId,
                            updated_on=:updatedOn,
                            updated_by=:updatedBy
                        WHERE id=:id AND
                          version=:version
                          """
    /**
     * SQL to update AccIpc object in database
     * @param accIpc -AccIpc object
     * @return -updated accIpc object
     */
    public AccIpc update(AccIpc accIpc) {
        Map queryParams = [
                id: accIpc.id,
                version: accIpc.version,
                newVersion: accIpc.version + 1,
                ipcNo: accIpc.ipcNo,
                updatedBy: accIpc.updatedBy,
                projectId: accIpc.projectId,
                updatedOn: DateUtility.getSqlDateWithSeconds(accIpc.updatedOn)
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update AccIpc')
        }
        accIpc.version = accIpc.version + 1
        return accIpc
    }

    private static final String QUERY_DELETE = """
                     DELETE FROM acc_ipc
                       WHERE id=:id """
    /**
     * Delete accIpc object by id
     * @param id -accIpc.id
     * @return -boolean value
     */
    public boolean delete(long id) {
        int updateCount = executeUpdateSql(QUERY_DELETE, [id: id])
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to delete AccIpc')
        }
        return true
    }
}
