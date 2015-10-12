package com.athena.mis.application.actions.appuserentity

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.AppGroup
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.application.entity.Project
import com.athena.mis.application.entity.SystemEntity
import com.athena.mis.application.utility.*
import com.athena.mis.integration.arms.ArmsPluginConnector
import com.athena.mis.integration.inventory.InventoryPluginConnector
import com.athena.mis.integration.projecttrack.ProjectTrackPluginConnector
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Show UI for AppUserEntity(user entity mapping) CRUD & list of AppUserEntity object(s) for grid
 *  For details go through Use-Case doc named 'ShowAppUserEntityActionService'
 */
class ShowAppUserEntityActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    @Autowired
    AppSessionUtil appSessionUtil
    @Autowired
    AppUserEntityTypeCacheUtility appUserEntityTypeCacheUtility
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired(required = false)
    ArmsPluginConnector armsImplService
    @Autowired
    AppGroupCacheUtility appGroupCacheUtility
    @Autowired(required = false)
    InventoryPluginConnector inventoryImplService
    @Autowired(required = false)
    ProjectTrackPluginConnector projectTrackImplService
    @Autowired
    BankBranchCacheUtility bankBranchCacheUtility

    private static final String DEFAULT_ERROR_MESSAGE = "Can't load app user entity list"
    private static final String LST_APP_USER_ENTITY = "lstAppUserEntity"
    private static final String MAP_APP_USER_ENTITY = "appUserEntityMap"
    private static final String ENTITY_TYPE_ID = "entityTypeId"
    private static final String ENTITY_ID = "entityId"
    private static final String RESERVED_ID = "reservedId"

    /**
     * Do nothing for pre operation
     */
    public Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * 1. check required parameters
     * 2. get list of AppUserEntity(user entity mapping) object(s) for grid by entity type id and entity id
     * 3. build a map with necessary properties of AppUserEntity object to show on UI
     * @param parameters -parameters send from UI
     * @param obj -N/A
     * @return -a map containing list of necessary objects for buildSuccessResultForUI
     * map -contains isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object execute(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
            GrailsParameterMap parameters = (GrailsParameterMap) params
            initPager(parameters)
            // check required parameters
            if (!parameters.entityTypeId || !parameters.entityId) {
                result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
                return result
            }
            long entityTypeId = Long.parseLong(parameters.entityTypeId.toString())
            long entityId = Long.parseLong(parameters.entityId.toString())

            // get AppUserEntity(user entity mapping) list for grid
            Map appUserEntity = listOfAppUserEntity(entityTypeId, entityId)
            List<GroovyRowResult> lstAppUserEntity = appUserEntity.lstAppUserEntity
            int total = (int) appUserEntity.count

            SystemEntity appUserEntityType = (SystemEntity) appUserEntityTypeCacheUtility.read(entityTypeId)
            Map appUserEntityMap = buildAppUserEntityMap(appUserEntityType, entityId)
            result.put(MAP_APP_USER_ENTITY, appUserEntityMap)
            result.put(LST_APP_USER_ENTITY, lstAppUserEntity)
            result.put(Tools.COUNT, total)
            result.put(ENTITY_TYPE_ID, entityTypeId)
            result.put(ENTITY_ID, entityId)
            result.put(RESERVED_ID, appUserEntityType.reservedId)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Do nothing for post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap AppUserEntity(user entity mapping) object list
     * @param obj -a map received from execute method
     * @return -all necessary objects to show on UI
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
            List<GroovyRowResult> lstAppUserEntity = (List) executeResult.get(LST_APP_USER_ENTITY)
            int total = (int) executeResult.get(Tools.COUNT)
            List<GroovyRowResult> LstWrapAppUserEntity = wrapAppUserEntityList(lstAppUserEntity, start)
            Map gridObject = [page: pageNumber, total: total, rows: LstWrapAppUserEntity]
            result.put(MAP_APP_USER_ENTITY, executeResult.get(MAP_APP_USER_ENTITY))
            result.put(ENTITY_TYPE_ID, executeResult.get(ENTITY_TYPE_ID))
            result.put(ENTITY_ID, executeResult.get(ENTITY_ID))
            result.put(RESERVED_ID, executeResult.get(RESERVED_ID))
            result.put(LST_APP_USER_ENTITY, gridObject)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
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
            LinkedHashMap preResult = (LinkedHashMap) obj
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (preResult.get(Tools.MESSAGE)) {
                result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
            } else {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap AppUserEntity(user entity mapping) objects list
     * @param lstAppUserEntity -list of GroovyRowResult
     * @param start -start index
     * @return -wrapped AppUserEntity(user entity mapping) objects list
     */
    private List wrapAppUserEntityList(List<GroovyRowResult> lstAppUserEntity, int start) {
        List LstWrapAppUserEntity = []
        int counter = start + 1
        for (int i = 0; i < lstAppUserEntity.size(); i++) {
            GroovyRowResult appUserEntity = lstAppUserEntity[i]
            GridEntity obj = new GridEntity()
            obj.id = appUserEntity.id
            obj.cell = [
                    counter,
                    appUserEntity.id,
                    appUserEntity.username
            ]
            LstWrapAppUserEntity << obj
            counter++
        }
        return LstWrapAppUserEntity
    }

    private static final String LST_QUERY = """
        SELECT aue.id, au.username
        FROM app_user_entity aue
        LEFT JOIN app_user au ON au.id = aue.app_user_id
        WHERE aue.company_id = :companyId
        AND aue.entity_type_id = :entityTypeId
        AND entity_id = :entityId
        ORDER BY au.username
        LIMIT :resultPerPage OFFSET :start
    """

    private static final String COUNT_QUERY = """
        SELECT COUNT(aue.id) count
        FROM app_user_entity aue
        LEFT JOIN app_user au ON au.id = aue.app_user_id
        WHERE aue.company_id = :companyId
        AND aue.entity_type_id = :entityTypeId
        AND entity_id = :entityId
    """

    /**
     * Get list of AppUserEntity(user entity mapping) object(s) to show on grid
     * @return -a map contains list of AppUserEntity(user entity mapping) object(s) and count
     */
    private Map listOfAppUserEntity(long entityTypeId, long entityId) {
        long companyId = appSessionUtil.getCompanyId()

        Map queryParams = [
                entityTypeId: entityTypeId,
                entityId: entityId,
                companyId: companyId,
                resultPerPage: resultPerPage,
                start: start
        ]
        List<GroovyRowResult> lstResult = executeSelectSql(LST_QUERY, queryParams)
        List<GroovyRowResult> countResult = executeSelectSql(COUNT_QUERY, queryParams)

        int total = (int) countResult[0].count
        return [lstAppUserEntity: lstResult, count: total]
    }

    /**
     * Build app user entity map with necessary properties
     * @param appUserEntityType -object of SystemEntity (app user entity type e.g. PROJECT, INVENTORY etc.)
     * @param entityId -entity id
     * @return -a map containing entity type name, entity name, plugin id and corresponding left menu link
     */
    private Map buildAppUserEntityMap(SystemEntity appUserEntityType, long entityId) {
        String entityTypeName = appUserEntityType.key
        String entityName = Tools.EMPTY_SPACE
        String leftMenu = Tools.EMPTY_SPACE
        int pluginId = 0
        switch (appUserEntityType.reservedId) {
            case appUserEntityTypeCacheUtility.PROJECT:
                Project project = (Project) projectCacheUtility.read(entityId)
                entityName = project.name
                pluginId = PluginConnector.APPLICATION_ID
                leftMenu = '#project/show'
                break
            case appUserEntityTypeCacheUtility.EXCHANGE_HOUSE:
                Object rmsExchangeHouse = armsImplService.readByExchangeHouseId(entityId)
                entityName = rmsExchangeHouse.name
                pluginId = PluginConnector.ARMS_ID
                leftMenu = '#rmsExchangeHouse/show'
                break
            case appUserEntityTypeCacheUtility.BANK_BRANCH:
                BankBranch bankBranch = (BankBranch) bankBranchCacheUtility.read(entityId)
                entityName = bankBranch.name
                pluginId = PluginConnector.APPLICATION_ID
                leftMenu = '#bankBranch/show'
                break
            case appUserEntityTypeCacheUtility.GROUP:
                AppGroup group =(AppGroup) appGroupCacheUtility.read(entityId)
                entityName = group.name
                pluginId = PluginConnector.APPLICATION_ID
                leftMenu = '#appGroup/show'
                break
            case appUserEntityTypeCacheUtility.INVENTORY:
                Object inventory = inventoryImplService.readInventory(entityId)
                entityName = inventory.name
                pluginId = PluginConnector.INVENTORY_ID
                leftMenu = '#invInventory/show'
                break
            case appUserEntityTypeCacheUtility.PT_PROJECT:
                Object project = projectTrackImplService.readPtProject(entityId)
                entityName = project.name
                pluginId = PluginConnector.PROJECT_TRACK_ID
                leftMenu = '#ptProject/show'
                break
            default:
                break
        }
        Map entityContentMap = [
                entityTypeName: entityTypeName,
                entityName: entityName,
                pluginId: pluginId,
                leftMenu: leftMenu
        ]
        return entityContentMap
    }
}
