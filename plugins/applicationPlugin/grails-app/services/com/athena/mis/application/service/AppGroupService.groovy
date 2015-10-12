package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppGroup
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.utility.AppGroupCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * AppGroupService is used to handle only CRUD related object manipulation
 * (e.g. list, read, create, delete etc.)
 */
class AppGroupService extends BaseService {

    static transactional = false

    AppUserService appUserService

    @Autowired
    AppGroupCacheUtility appGroupCacheUtility
    /**
     * @return - list of appGroup
     */
    public List list() {
        return AppGroup.list(sort: appGroupCacheUtility.SORT_ON_NAME, order: appGroupCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    /**
     * Method to read App Group by id
     * @param id -AppGroup.id
     * @return -AppGroup object
     */
    public AppGroup read(long id) {
        AppGroup appGroup = AppGroup.read(id)
        return appGroup
    }

    /**
     * Method to count the number of app group by using dynamic finder
     * @param name - app group name
     * @param companyId - app group company id
     * @return - integer value of count
     */
    public int countByNameIlikeAndCompanyId(String name, long companyId) {
        int count = AppGroup.countByNameIlikeAndCompanyId(name, companyId)
        return count
    }

    /**
     * Method to count the number of app group by using dynamic finder
     * @param id - app group id
     * @param name - app group name
     * @param companyId - app group company id
     * @return - integer value of count app group
     */
    public int countByIdNotEqualAndNameIlikeAndCompanyId(long id, String name, long companyId) {
        int countAppGroup = AppGroup.countByIdNotEqualAndNameIlikeAndCompanyId(id, name, companyId)
        return countAppGroup
    }

    private static final String INSERT_QUERY =
            """
            INSERT INTO app_group(id, version, name, created_by, created_on,updated_by, company_id)
            VALUES (NEXTVAL('app_group_id_seq'),:version, :name, :createdBy,:createOn,:updatedBy, :companyId);
        """
    /**
     * Create appGroup
     * @param appGroup - appGroup object
     * @return - newly created appGroup object
     */
    public AppGroup create(AppGroup appGroup) {
        Map queryParams = [
                version: appGroup.version,
                name: appGroup.name,
                createdBy: appGroup.createdBy,
                updatedBy: appGroup.updatedBy,
                createOn: DateUtility.getSqlDateWithSeconds(appGroup.createdOn),
                companyId: appGroup.companyId
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while inserting Group information')
        }

        int userGroupId = (int) result[0][0]
        appGroup.id = userGroupId
        return appGroup
    }

    private static final String UPDATE_QUERY =
            """
                    UPDATE app_group SET
                          version=:newVersion,
                          name=:name,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy

                      WHERE
                          id=:id AND
                          version=:version
                """
    /**
     * Update appGroup
     * @param appGroup - appGroup object
     * @return- newly updated object of selected appGroup
     */
    public int update(AppGroup appGroup) {

        Map queryParams = [
                id: appGroup.id,
                newVersion: appGroup.version + 1,
                version: appGroup.version,
                name: appGroup.name,
                updatedBy: appGroup.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(appGroup.updatedOn)
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating group information')
        }
        appGroup.version = appGroup.version + 1
        return updateCount;
    }

    private static final String DELETE_QUERY =
            """
                    DELETE FROM app_group
                      WHERE
                          id=:id
                """
    /**
     * Delete group
     * @param id - selected group id
     * @return -an int value(e.g- 1 for success and 0 for failure)
     */
    public int delete(long id) {
        Map queryParams = [id: id]

        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete group information')
        }
        return deleteCount;
    }

    /**
     * Create default data for AppGroup
     */
    public void createDefaultDataForAppGroup() {
        Date currDate = new Date()
        AppUser adminUser = appUserService.findByLoginId('admin@athena.com')

        AppGroup appGroup = new AppGroup(version: 0, companyId: adminUser.companyId, createdBy: adminUser.id, createdOn: currDate, name: 'Admin Group', updatedBy: 0, updatedOn: null)
        appGroup.save()
    }
}
