package com.athena.mis.document.service

import com.athena.mis.BaseService
import com.athena.mis.document.entity.DocDbInstance
import com.athena.mis.document.utility.DocDbInstanceCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

class DocDbInstanceService extends BaseService {

    @Autowired
    DocDbInstanceCacheUtility docDbInstanceCacheUtility

    public List<DocDbInstance> list() {
        List<DocDbInstance> lstDbInstance = DocDbInstance.list(
                sort: docDbInstanceCacheUtility.DEFAULT_SORT_NAME,
                order: docDbInstanceCacheUtility.SORT_ORDER_ASCENDING,
                readOnly: true
        )
        return lstDbInstance
    }

    /*
    * Insert DbInstance Object
    * @params docDbInstance - DocDbInstance object
    * @return docDbInstance - saved DocDbInstance object
    * */

    public DocDbInstance create(DocDbInstance docDbInstance) {
        DocDbInstance docDbInstance1 = docDbInstance.save()
        return docDbInstance1
    }
    /*
    * Retrive DbInstance object by id
    * @params id - object id
    * @return docDbInstance - DB instance object
    * */

    public DocDbInstance read(long id) {
        DocDbInstance docDbInstance = DocDbInstance.read(id)
        return docDbInstance
    }

    public static final String UPDATE_QUERY = """
        UPDATE doc_db_instance SET
            version=version+1,
            instance_name=:instanceName,
            vendor_id=:vendorId,
            driver=:driver,
            connection_string=:connectionString,
            sql_query=:sqlQuery,
            updated_on=:updatedOn,
            updated_by=:updatedBy
        WHERE
            id=:id AND
            version=:oldVersion
    """

    /*
    * Update DB instance object
    * @params docDbInstance - DB instance object for update
    * @return updateCount - count of updated object
    * */

    public int update(DocDbInstance docDbInstance) {
        Map queryParams = [
                id: docDbInstance.id,
                oldVersion: docDbInstance.version,
                instanceName: docDbInstance.instanceName,
                vendorId: docDbInstance.vendorId,
                driver: docDbInstance.driver,
                connectionString: docDbInstance.connectionString,
                sqlQuery: docDbInstance.sqlQuery,
                updatedOn: DateUtility.getSqlDateWithSeconds(docDbInstance.updatedOn),
                updatedBy: docDbInstance.updatedBy
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update DB instance")
        }

        docDbInstance.version = docDbInstance.version + 1
        return updateCount
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM doc_db_instance WHERE id=:id
                 """

    /*
    * Delete Db instance object by id
    * @params id - object id
    * @return deleteCount - count of deleted object
    * */

    public int delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete DB instance information')
        }
        return deleteCount;
    }
}
