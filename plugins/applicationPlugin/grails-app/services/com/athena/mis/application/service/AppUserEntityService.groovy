package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.AppUserEntity
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.application.utility.AppUserEntityTypeCacheUtility
import groovy.sql.GroovyRowResult
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Common service-class for any kind of User-Entity mapping(e.g : UserProject, UserInventory mapping etc)
 *  For details go through Use-Case doc named 'AppUserEntityService'
 */
class AppUserEntityService extends BaseService {

    SystemEntityService systemEntityService
    AppUserService appUserService
    ProjectService projectService
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    static transactional = false

    /**
     * Common method to read any AppUserEntity object by id (e.g : UserProject, UserInventory mapping etc)
     * @param id -AppUserEntity.id
     * @return -AppUserEntity object
     */
    public AppUserEntity read(long id) {
        AppUserEntity appUserEntity = AppUserEntity.read(id)
        return appUserEntity
    }

    /**
     * Method to get the count of user group
     * @param groupId - group id
     * @param entityTypeId - entity type id
     * @return - an integer value of count of user group
     */
    public int countByEntityIdAndEntityTypeId(long groupId, long entityTypeId) {
        int countUserGroup = (int) AppUserEntity.countByEntityIdAndEntityTypeId(groupId, entityTypeId)
        return countUserGroup
    }

    /**
     * Method to count entity type
     * @param systemEntityId - system entity id
     * @return - an integer value of count
     */
    public int countByEntityTypeId(long systemEntityId) {
        int count = AppUserEntity.countByEntityTypeId(systemEntityId)
        return count
    }

    /**
     * Method to find the existing user group mapping object
     * @param appUserId - app user id
     * @param entityTypeId - entity type id
     * @param entityId - entity id
     * @param companyId - company id
     * @return - the object of app user entity
     */
    public AppUserEntity findByAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(long appUserId, long entityTypeId, long entityId, long companyId) {
        AppUserEntity appUserEntity = AppUserEntity.findByAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(appUserId, entityTypeId, entityId, companyId, [readOnly: true])
        return appUserEntity
    }

    /**
     * Method to count existing user project mapping object
     * @param appUserId - app user id
     * @param entityTypeId - entity type id
     * @param entityId - entity id
     * @param companyId - company id
     * @return - the count value of app user entity
     */
    public int countByAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(long appUserId, long entityTypeId, long entityId, long companyId) {
        int appUserCount = AppUserEntity.countByAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(appUserId, entityTypeId, entityId, companyId, [readOnly: true])
        return appUserCount
    }

    /**
     * Method to find the existing user group mapping object
     * @param id - app user entity id
     * @param appUserId - app user id
     * @param entityTypeId - entity type id
     * @param entityId - entity id
     * @param companyId - company id
     * @return - the object of app user entity
     */
    public AppUserEntity findByIdNotEqualAndAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(long id, long appUserId, long entityTypeId, long entityId, long companyId) {
        AppUserEntity appUserEntity = AppUserEntity.findByIdNotEqualAndAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(id, appUserId, entityTypeId, entityId, companyId, [readOnly: true])
        return appUserEntity
    }

    /**
     * Method to find the existing user group mapping count
     * @param id - app user entity id
     * @param appUserId - app user id
     * @param entityTypeId - entity type id
     * @param entityId - entity id
     * @param companyId - company id
     * @return - the count value of app user entity
     */
    public int countByIdNotEqualAndAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(long id, long appUserId, long entityTypeId, long entityId, long companyId) {
        int appUserEntityCount = AppUserEntity.countByIdNotEqualAndAppUserIdAndEntityTypeIdAndEntityIdAndCompanyId(id, appUserId, entityTypeId, entityId, companyId, [readOnly: true])
        return appUserEntityCount
    }

    /**
     * Method to find existing user project object
     * @param existingUserId - user id
     * @param entityTypeId - entity type id
     * @param existingProjectId - project id
     * @return - the object of existing user project
     */
    public AppUserEntity findByAppUserIdAndEntityTypeIdAndEntityId(long existingUserId, long entityTypeId, long existingProjectId) {
        AppUserEntity existingUserProject = AppUserEntity.findByAppUserIdAndEntityTypeIdAndEntityId(existingUserId, entityTypeId, existingProjectId, [readOnly: true])
        return existingUserProject
    }

    /**
     * Method to find existing user agent object
     * @param appUserId - app user id
     * @param entityTypeId - entity type id
     * @return - the object of existing user agent
     */
    public AppUserEntity findByAppUserIdAndEntityTypeId(long appUserId, long entityTypeId) {
        AppUserEntity existingUserAgent = AppUserEntity.findByAppUserIdAndEntityTypeId(appUserId, entityTypeId, [readOnly: true])
        return existingUserAgent
    }

    /**
     * Method to find existing user agent object
     * @param userId - user id
     * @param entityTypeId - entity type id
     * @param existingAppUserEntityId - existing app user entity id
     * @return - the object of existing user agent
     */
    public AppUserEntity findByAppUserIdAndEntityTypeIdAndIdNotEqual(long userId, long entityTypeId, long existingAppUserEntityId) {
        AppUserEntity existingUserAgent = AppUserEntity.findByAppUserIdAndEntityTypeIdAndIdNotEqual(userId, entityTypeId, existingAppUserEntityId, [readOnly: true])
        return existingUserAgent
    }

    /**
     * Method to find all app user list
     * @param appUserId - app user id
     * @return - list of app user
     */
    public List<AppUserEntity> findAllByAppUserId(long appUserId) {
        List<AppUserEntity> lstAppUserMapping = AppUserEntity.findAllByAppUserId(appUserId, [readOnly: true])
        return lstAppUserMapping
    }

    public List<AppUserEntity> findAllByAppUserIdAndEntityTypeId(long appUserId, long reservedId) {
        long companyId = appSessionUtil.getCompanyId()
        SystemEntity appUserEntityType = (SystemEntity) appUserEntityTypeCacheUtility.readByReservedAndCompany(reservedId, companyId)
        List<AppUserEntity> lstAppUserMapping = AppUserEntity.findAllByAppUserIdAndEntityTypeId(appUserId, appUserEntityType.id, [readOnly: true])
        return lstAppUserMapping
    }

    /**
     * Method to get list of AppUserEntity object by entityTypeId
     * @param entityTypeId -SystemEntity.id (e.g. Project,Inventory,Customer etc)
     * @return -list of AppUserEntity object(s)
     */
    public List listByType(long entityTypeId) {
        List<AppUserEntity> lst = lstAppUserEntity(entityTypeId)
        return lst
    }

    /**
     * Common method to save all kind of AppUserEntity object(s) (e.g : UserProject, UserInventory mapping etc)
     * @param entityTypeId -SystemEntity.id (e.g. Project,Inventory,Customer etc)
     * @return -newly created AppUserEntity object
     */
    public AppUserEntity create(AppUserEntity appUserEntity) {
        appUserEntity.companyId = appSessionUtil.getCompanyId() // give company id for the creation of appUserEntity object
        AppUserEntity newAppUserEntity = appUserEntity.save(false)
        return newAppUserEntity
    }

    public AppUserEntity create(AppUserEntity appUserEntity, long companyId) {
        appUserEntity.companyId = companyId
        AppUserEntity newAppUserEntity = appUserEntity.save(false)
        return newAppUserEntity
    }

    /**
     * Common method to update all kind of AppUserEntity object(s) (e.g : UserProject, UserInventory mapping etc)
     * @param appUserEntity -AppUserEntity object
     * @return -updateCount(if updateCount<=0 then throw exception to rollback whole DB transaction)
     */
    public int update(AppUserEntity appUserEntity) {
        String queryStr = """
                    UPDATE app_user_entity
                    SET
                          app_user_id=:userId
                    WHERE
                          id=:id
                    """
        Map queryParams = [
                id: appUserEntity.id,
                userId: appUserEntity.appUserId
        ]

        int updateCount = executeUpdateSql(queryStr, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update user-entity mapping information')
        }
        return updateCount
    }

    /**
     * Common method to delete all kind of AppUserEntity object(s) by id (e.g : UserProject, UserInventory mapping etc)
     * @param id -AppUserEntity.id
     * @return -deleteCount(if deleteCount<=0 then throw exception to rollback whole DB transaction)
     */
    public int delete(long id) {
        String queryStr = """
                    DELETE FROM app_user_entity
                      WHERE id=${id} """
        int deleteCount = executeUpdateSql(queryStr)
        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete user-entity mapping information')
        }
        return deleteCount
    }

    /**
     * Method to create default data (User-Project mapping) when application will start from bootstrap
     */
    public void createDefaultDataForUserProject(long companyId) {
        AppUser adminUser = appUserService.findByLoginId('administrator@athena.com')
        AppUser superAdmin = appUserService.findByLoginId('admin@athena.com')
        AppUser userDirector = appUserService.findByLoginId('dir@athena.com')
        AppUser userProjectDirector = appUserService.findByLoginId('pd@athena.com')
        AppUser userProjectManager = appUserService.findByLoginId('pm@athena.com')

        Project project = projectService.findByName('Dhaka Flyover')

        if (project) {
            SystemEntity appUserSysEntityObject = systemEntityService.findByReservedIdAndCompanyId(appUserEntityTypeCacheUtility.PROJECT, companyId)

            if (adminUser) new AppUserEntity(appUserId: adminUser.id, entityId: project.id, entityTypeId: appUserSysEntityObject.id, companyId: companyId).save()
            if (superAdmin) new AppUserEntity(appUserId: superAdmin.id, entityId: project.id, entityTypeId: appUserSysEntityObject.id, companyId: companyId).save()
            if (userDirector) new AppUserEntity(appUserId: userDirector.id, entityId: project.id, entityTypeId: appUserSysEntityObject.id, companyId: companyId).save()
            if (userProjectDirector) new AppUserEntity(appUserId: userProjectDirector.id, entityId: project.id, entityTypeId: appUserSysEntityObject.id, companyId: companyId).save()
            if (userProjectManager) new AppUserEntity(appUserId: userProjectManager.id, entityId: project.id, entityTypeId: appUserSysEntityObject.id, companyId: companyId).save()
        }
    }

    /**
     * Method to create default data (User-Inventory mapping) when application will start from bootstrap
     */
    public void createDefaultDataForUserInventory(long companyId) {
        AppUser projectManager = appUserService.findByLoginId('pm@athena.com')
        AppUser inventoryOperator = appUserService.findByLoginId('io@athena.com')
        AppUser superAdmin = appUserService.findByLoginId('admin@athena.com')

        SystemEntity inventory = systemEntityService.findByReservedIdAndCompanyId(appUserEntityTypeCacheUtility.INVENTORY, companyId)

        new AppUserEntity(appUserId: projectManager.id, entityId: 1, entityTypeId: inventory.id, companyId: companyId).save()
        new AppUserEntity(appUserId: projectManager.id, entityId: 2, entityTypeId: inventory.id, companyId: companyId).save()
        new AppUserEntity(appUserId: inventoryOperator.id, entityId: 1, entityTypeId: inventory.id, companyId: companyId).save()
        new AppUserEntity(appUserId: inventoryOperator.id, entityId: 2, entityTypeId: inventory.id, companyId: companyId).save()
        new AppUserEntity(appUserId: superAdmin.id, entityId: 1, entityTypeId: inventory.id, companyId: companyId).save()
        new AppUserEntity(appUserId: superAdmin.id, entityId: 2, entityTypeId: inventory.id, companyId: companyId).save()
    }

    public void createDefaultDataForUserArms(long companyId) {
        AppUser branchUser = appUserService.findByLoginId('branch_banani@athena.com')
        AppUser otherBankUser = appUserService.findByLoginId('other_gulshan@athena.com')
        AppUser exhUser = appUserService.findByLoginId('exh@athena.com')

        SystemEntity bankBranch = systemEntityService.findByReservedIdAndCompanyId(appUserEntityTypeCacheUtility.BANK_BRANCH, companyId)
        SystemEntity exchangeHouse = systemEntityService.findByReservedIdAndCompanyId(appUserEntityTypeCacheUtility.EXCHANGE_HOUSE, companyId)

        new AppUserEntity(appUserId: branchUser.id, entityId: 3, entityTypeId: bankBranch.id, companyId: companyId).save()
        new AppUserEntity(appUserId: otherBankUser.id, entityId: 4, entityTypeId: bankBranch.id, companyId: companyId).save()
        new AppUserEntity(appUserId: exhUser.id, entityId: 1, entityTypeId: exchangeHouse.id, companyId: companyId).save()
    }

    private static final String SELECT_QUERY = """
                SELECT * FROM app_user_entity
                WHERE entity_type_id
                IN(SELECT id from system_entity  WHERE reserved_id =:entityTypeId)
    """

    /**
     * Get List of application user entity
     * @param entityTypeId - entity type id from caller method
     * @return - a list of app user entity
     */
    private List<AppUserEntity> lstAppUserEntity(long entityTypeId) {
        Map queryParams = [
                entityTypeId: entityTypeId
        ]
        List<GroovyRowResult> listAppUserEntity = executeSelectSql(SELECT_QUERY, queryParams)
        List<AppUserEntity> lstReturn = []
        for (int i = 0; i < listAppUserEntity.size(); i++) {
            GroovyRowResult eachRow = listAppUserEntity[i]
            AppUserEntity appUserEntity = new AppUserEntity()
            appUserEntity.id = eachRow.id
            appUserEntity.appUserId = eachRow.app_user_id
            appUserEntity.entityId = eachRow.entity_id
            appUserEntity.entityTypeId = eachRow.entity_type_id
            appUserEntity.companyId = eachRow.company_id

            lstReturn << appUserEntity
        }
        return lstReturn
    }

    public void createDefaultDataForUserGroup(long companyId) {
        AppUser superUser = appUserService.findByLoginId('super@athena.com')
        AppUser adminUser = appUserService.findByLoginId('admin@athena.com')

        SystemEntity groupEntityObj = systemEntityService.findByReservedIdAndCompanyId(appUserEntityTypeCacheUtility.GROUP, companyId)

        new AppUserEntity(appUserId: superUser.id, entityId: 1, entityTypeId: groupEntityObj.id, companyId: companyId).save()
        new AppUserEntity(appUserId: adminUser.id, entityId: 1, entityTypeId: groupEntityObj.id, companyId: companyId).save()

    }

}
