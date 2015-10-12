package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Designation
import com.athena.mis.application.utility.DesignationCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

class DesignationService extends BaseService {

    static transactional = false

    @Autowired
    DesignationCacheUtility designationCacheUtility

    /**
     * @return - list of designation
     */
    public List list() {
        return Designation.list(sort: designationCacheUtility.SORT_ON_NAME, order: designationCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    /**
     * Pull designation object
     * @param id - designation id
     * @return - designation object
     */
    public Designation read(long id) {
        return Designation.read(id)
    }

    private static final String INSERT_QUERY = """
            INSERT INTO designation(id, version, company_id, created_by, created_on,
                                    name, short_name, updated_by, updated_on)
            VALUES (NEXTVAL('designation_id_seq'),:version, :companyId, :createdBy,
                    :createdOn,:name, :shortName, :updatedBy, null);
    """

    /**
     * Create new designation
     * @param designation - designation object
     * @return - newly created designation object
     */
    public Designation create(Designation designation) {

        Map queryParams = [
                version: designation.version,
                companyId: designation.companyId,
                name: designation.name,
                shortName: designation.shortName,
                createdBy: designation.createdBy,
                createdOn: DateUtility.getSqlDateWithSeconds(designation.createdOn),
                updatedBy: designation.updatedBy
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while creating designation information')
        }

        int designationId = (int) result[0][0]
        designation.id = designationId
        return designation
    }

    private static final String UPDATE_QUERY = """
                    UPDATE designation SET
                          version=:newVersion,
                          name=:name,
                          short_name=:shortName,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy
                    WHERE
                          id=:id AND
                          version=:version
    """

    /**
     * Update designation
     * @param designation - designation object
     * @return- newly updated object of selected designation
     */
    public int update(Designation designation) {

        Map queryParams = [
                id: designation.id,
                newVersion: designation.version + 1,
                version: designation.version,
                name: designation.name,
                shortName: designation.shortName,
                updatedBy: designation.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(designation.updatedOn)
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating designation information')
        }
        designation.version = designation.version + 1
        return updateCount;
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM designation
                      WHERE id=:id
    """

    /**
     * Delete designation
     * @param id - selected designation id
     * @return -an int value(e.g- 1 for success and 0 for failure)
     */
    public int delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete designation information')
        }
        return deleteCount;
    }

    /**
     * applicable only for create default designation
     */
    public void createDefaultData(long companyId) {
        new Designation(name: 'General Manager', shortName: 'GM', companyId: companyId, createdBy: 1, createdOn: new Date()).save()
        new Designation(name: 'Manager', shortName: 'M', companyId: companyId, createdBy: 1, createdOn: new Date()).save()
        new Designation(name: 'Managing Director', shortName: 'MD', companyId: companyId, createdBy: 1, createdOn: new Date()).save()
    }
}
