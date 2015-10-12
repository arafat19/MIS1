package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.entity.RequestMap
import com.athena.mis.application.entity.Role
import com.athena.mis.application.entity.RoleFeatureMapping
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.utility.Tools
import groovy.sql.GroovyRowResult
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * RequestMapService is used to handle only CRUD related object manipulation
 * (e.g. list, read, update etc.)
 */
class RequestMapService extends BaseService {

    RoleService roleService
    RoleFeatureMappingService roleFeatureMappingService
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    private static final String SORT_ON_URL = "url"

    /**
     * Method to read RequestMap by id
     * @param id - RequestMap.id
     * @return - RequestMap object
     */
    public RequestMap read(Long id) {
        RequestMap requestMap = RequestMap.read(id)
        return requestMap
    }

    /**
     * Method to get list of RequestMap
     */
    public List list() {
        return RequestMap.list(sort: SORT_ON_URL, order: ASCENDING_SORT_ORDER, readOnly: true);
    }

    /**
     * Method to update assigned or removed requestMap for particular role
     * @param lstAssignedFeatures - list of assigned features
     * @param roleAuthority - string of role authority
     * @param pluginId - plugin Id
     */
    public boolean update(List<Long> lstAssignedFeatures, String roleAuthority, int pluginId) {
        String queryStr = """
                SELECT id
                FROM request_map
                WHERE
                id NOT IN (${roleTypeCacheUtility.REQUEST_MAP_ROOT},${roleTypeCacheUtility.REQUEST_MAP_LOGOUT})
                AND plugin_id = ${pluginId}
                AND(
                config_attribute LIKE '%,${roleAuthority},%'
                OR
                config_attribute LIKE '${roleAuthority},%'
                OR
                config_attribute LIKE '%,${roleAuthority}'
                OR
                config_attribute =  '${roleAuthority}'
                )
        """

        // First get the current assigned role
        List<GroovyRowResult> result = executeSelectSql(queryStr)

        List<Long> lstOldFeatures = []
        for (int i = 0; i < result.size(); i++) {
            lstOldFeatures << result[i].id
        }

        // Find the common elements
        List lstCommonFeatures = lstOldFeatures.intersect(lstAssignedFeatures)

        List<Long> lstToRemove = (List<Long>) lstOldFeatures.clone()
        // Get the IDs of requestMap where current ROLE has lost the RIGHT
        lstToRemove.removeAll(lstCommonFeatures)      // i.e. ToBeRemoved=(Existing Feature) - (Common Features)
        // Get the IDs of requestMap where current ROLE has gain the RIGHT
        List<Long> lstToAdd = (List<Long>) lstAssignedFeatures.clone()
        lstToAdd.removeAll(lstCommonFeatures)        // i.e. ToBeAdded=(All assigned Feature) - (Common Features)

        // If something to add then execute sql
        if (lstToAdd.size() > 0) {
            String idsForAdd = Tools.buildCommaSeparatedStringOfIds(lstToAdd)
            String queryToAdd = """
                UPDATE request_map
                SET config_attribute =
                    CASE WHEN config_attribute IS NULL THEN '${roleAuthority}'
                    WHEN config_attribute ='' THEN '${roleAuthority}'
                    ELSE config_attribute || ',${roleAuthority}'
                    END
                WHERE id IN (${idsForAdd})
        """
            executeUpdateSql(queryToAdd)
        }

        // If something to remove then execute sql
        if (lstToRemove.size() > 0) {
            String idsForRemove = Tools.buildCommaSeparatedStringOfIds(lstToRemove)
            String queryToRemove = """
            UPDATE request_map
            SET config_attribute =
                CASE WHEN config_attribute LIKE '%${roleAuthority},%' THEN
                    REPLACE(config_attribute, '${roleAuthority},' , '')
                WHEN config_attribute LIKE '%,${roleAuthority}%' THEN
                    REPLACE(config_attribute, ',${roleAuthority}' , '')
                ELSE NULL
                END
            WHERE id IN (${idsForRemove})
        """
            executeUpdateSql(queryToRemove)
        }
        return true
    }

    /**
     * Method to update when role name is changed
     * @param previousRole - string of previous role from caller method
     * @param newRole - string of new role from caller method
     * @return
     */
    public boolean update(String previousRole, String newRole) {
        String queryStr = """
            UPDATE request_map
                SET config_attribute =
            CASE WHEN config_attribute LIKE '%,${previousRole},%' THEN
                REPLACE(config_attribute, ',${previousRole},' , ',${newRole},')
            WHEN config_attribute LIKE '%,${previousRole}' THEN
                REPLACE(config_attribute, ',${previousRole}' , ',${newRole}')
            ELSE config_attribute
            END
        """

        executeUpdateSql(queryStr)

        return true;
    }

    /**
     * Method to create request map for application plugin
     */
    public boolean createRequestMapForApplicationPlugin() {

        // default
        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common, is_viewable)
        VALUES(-2,0,'/','ROLE_-2','INDEX PAGE',1,'APP-1', TRUE, FALSE)""")

        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common, is_viewable)
        VALUES(-3,0,'/logout/**','ROLE_-2','LOGOUT ACCESS',1,'APP-2', TRUE, FALSE)""")

        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
        VALUES(-4,0, '/plugins/applicationplugin-0.1/theme/application/js*//**','IS_AUTHENTICATED_ANONYMOUSLY',null,1,'APP-3',FALSE,FALSE)""")

        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
        VALUES(-5,0, '/plugins/applicationplugin-0.1/theme/application/css*//**','IS_AUTHENTICATED_ANONYMOUSLY',null,1,'APP-4',FALSE,FALSE)""")

        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
        VALUES(-6,0, '/plugins/applicationplugin-0.1/theme/application/images*//**','IS_AUTHENTICATED_ANONYMOUSLY',null,1,'APP-5',FALSE,FALSE)""")

        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
        VALUES(-7,0, '/login/**','IS_AUTHENTICATED_ANONYMOUSLY',null,1,'APP-6',FALSE,FALSE)""")

        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
        VALUES(-8,0,'/login/checklogin','IS_AUTHENTICATED_ANONYMOUSLY',null,1,'APP-7',FALSE,FALSE)""")

        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
        VALUES(-9,0,'/logout/index','IS_AUTHENTICATED_ANONYMOUSLY',null,1,'APP-8',FALSE,FALSE)""")

        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
        VALUES(-10,0,'/jcaptcha/**','IS_AUTHENTICATED_ANONYMOUSLY',null,1,'APP-9',FALSE,FALSE)""")

        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
        VALUES(-13,0, '/appUser/managePassword','ROLE_-2','MANAGE PASSWORD',1,'APP-16',TRUE,TRUE)""")

        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
        VALUES(-14,0, '/appUser/checkPassword','ROLE_-2','CHECK PASSWORD',1,'APP-17',FALSE,FALSE)""")

        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
        VALUES(-15,0, '/appUser/changePassword','ROLE_-2','CHANGE PASSWORD',1,'APP-18', TRUE, FALSE)""")

        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_common,is_viewable)
        VALUES(-23,0, '/application/renderApplicationMenu','ROLE_-2','Application Module',1,'APP-92',FALSE,FALSE);""")

        // AppUser
        new RequestMap(transactionCode: 'APP-10', url: '/appUser/show', featureName: 'Show App User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-11', url: '/appUser/create', featureName: 'Create App User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-12', url: '/appUser/select', featureName: 'Select App User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-13', url: '/appUser/update', featureName: 'update App User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-14', url: '/appUser/delete', featureName: 'Delete App User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-15', url: '/appUser/list', featureName: 'list App User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-154', url: '/appUser/showOnlineUser', featureName: 'Show online user', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-155', url: '/appUser/listOnlineUser', featureName: 'List online user', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-156', url: '/appUser/forceLogoutOnlineUser', featureName: 'Forced logout online user', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-184', url: '/appUser/sendPasswordResetLink', featureName: 'Send mail for reset password', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-185', url: '/appUser/showResetPassword', featureName: 'Show for reset password', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-186', url: '/appUser/resetPassword', featureName: 'Reset password', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-191', url: '/appUser/resetExpiredPassword', featureName: 'Reset expired password', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // App Mail
        new RequestMap(transactionCode: 'APP-187', url: '/appMail/show', featureName: 'Show App mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'APP-188', url: '/appMail/update', featureName: 'Update App mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-189', url: '/appMail/list', featureName: 'List of app mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-190', url: '/appMail/select', featureName: 'Select app mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-251', url: '/appMail/testAppMail', featureName: 'Test app mail', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

        //Document Shell Script Request Map
        new RequestMap(transactionCode: 'APP-253', url: '/appShellScript/show', featureName: 'Show Shell Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-254', url: '/appShellScript/create', featureName: 'Create Shell Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-255', url: '/appShellScript/list', featureName: 'List Shell Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-256', url: '/appShellScript/select', featureName: 'Select Shell Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-257', url: '/appShellScript/update', featureName: 'Update Shell Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-258', url: '/appShellScript/delete', featureName: 'Delete Shell Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-259', url: '/appShellScript/evaluate', featureName: 'Evaluate Shell Script', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // role
        new RequestMap(transactionCode: 'APP-19', url: '/role/show', featureName: 'Show Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'APP-20', url: '/role/create', featureName: 'Create Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-21', url: '/role/select', featureName: 'Select Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-22', url: '/role/update', featureName: 'Update Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-23', url: '/role/delete', featureName: 'Delete Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-24', url: '/role/list', featureName: 'List Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

        // requestMap
        new RequestMap(transactionCode: 'APP-25', url: '/requestMap/show', featureName: 'Show Request Map', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'APP-27', url: '/requestMap/select', featureName: 'Select Request Map', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-28', url: '/requestMap/update', featureName: 'Update Request Map', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'APP-31', url: '/requestMap/resetRequestMap', featureName: 'Reset Request Map By Plugin ID', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        // will not visible to assign

        // userRole
        new RequestMap(transactionCode: 'APP-32', url: '/userRole/show', featureName: 'Show User Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'APP-33', url: '/userRole/create', featureName: 'Create User Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-34', url: '/userRole/select', featureName: 'Select User Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-35', url: '/userRole/update', featureName: 'Update User Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-36', url: '/userRole/delete', featureName: 'Delete User Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-37', url: '/userRole/list', featureName: 'List User Role', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

        // customer
        new RequestMap(transactionCode: 'APP-38', url: '/customer/show', featureName: 'Show Customer', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'APP-39', url: '/customer/create', featureName: 'Create Customer', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-40', url: '/customer/select', featureName: 'Select Customer', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-41', url: '/customer/update', featureName: 'Update Customer', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-42', url: '/customer/delete', featureName: 'Delete Customer', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-43', url: '/customer/list', featureName: 'List Customer', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

        // employee
        new RequestMap(transactionCode: 'APP-44', url: '/employee/show', featureName: 'Show Employee', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-45', url: '/employee/create', featureName: 'Create Employee', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-46', url: '/employee/select', featureName: 'Select Employee', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-47', url: '/employee/update', featureName: 'Update Employee', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-48', url: '/employee/delete', featureName: 'Delete Employee', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-49', url: '/employee/list', featureName: 'List Employee', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // company
        new RequestMap(transactionCode: 'APP-50', url: '/company/show', featureName: 'Show Company', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-51', url: '/company/create', featureName: 'Create Company', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-52', url: '/company/select', featureName: 'Select Company', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-53', url: '/company/update', featureName: 'Update Company', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-54', url: '/company/delete', featureName: 'Delete Company', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-55', url: '/company/list', featureName: 'List Company', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // project
        new RequestMap(transactionCode: 'APP-56', url: '/project/show', featureName: 'Show Project', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-57', url: '/project/create', featureName: 'Create Project', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-58', url: '/project/select', featureName: 'Select Project', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-59', url: '/project/update', featureName: 'Update Project', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-60', url: '/project/delete', featureName: 'Delete Project', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-61', url: '/project/list', featureName: 'List Project', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // AppUserEntity
        new RequestMap(transactionCode: 'APP-238', url: '/appUserEntity/show', featureName: 'Show appUser Entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'APP-239', url: '/appUserEntity/create', featureName: 'Create appUser Entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-240', url: '/appUserEntity/update', featureName: 'Update appUser Entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-241', url: '/appUserEntity/delete', featureName: 'Delete appUser Entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-242', url: '/appUserEntity/list', featureName: 'List appUser Entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-243', url: '/appUserEntity/select', featureName: 'Select appUser Entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-244', url: '/appUserEntity/dropDownAppUserEntityReload', featureName: 'Reload data for app user entity drop down', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

        // ItemType
        new RequestMap(transactionCode: 'APP-174', url: '/itemType/show', featureName: 'Show item type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'APP-175', url: '/itemType/create', featureName: 'Create item type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-176', url: '/itemType/update', featureName: 'Update item type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-177', url: '/itemType/delete', featureName: 'Delete item type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-178', url: '/itemType/list', featureName: 'List item type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-179', url: '/itemType/select', featureName: 'Select item type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

        // item
        new RequestMap(transactionCode: 'APP-70', url: '/item/select', featureName: 'Select Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-157', url: '/item/listItemByItemTypeId', featureName: 'Get Item list by item type ID', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // inventory item
        new RequestMap(transactionCode: 'APP-158', url: '/item/showInventoryItem', featureName: 'Show Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-159', url: '/item/createInventoryItem', featureName: 'Create Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-160', url: '/item/updateInventoryItem', featureName: 'Update Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-161', url: '/item/deleteInventoryItem', featureName: 'Delete Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-162', url: '/item/listInventoryItem', featureName: 'List Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // non inventory item
        new RequestMap(transactionCode: 'APP-163', url: '/item/showNonInventoryItem', featureName: 'Show Non-Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-164', url: '/item/createNonInventoryItem', featureName: 'Create Non-Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-165', url: '/item/updateNonInventoryItem', featureName: 'Update Non-Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-166', url: '/item/deleteNonInventoryItem', featureName: 'Delete Non-Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-167', url: '/item/listNonInventoryItem', featureName: 'List Non-Inventory Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // fixed asset item
        new RequestMap(transactionCode: 'APP-168', url: '/item/showFixedAssetItem', featureName: 'Show Fixed Asset Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-169', url: '/item/createFixedAssetItem', featureName: 'Create Fixed Asset Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-170', url: '/item/updateFixedAssetItem', featureName: 'Update Fixed Asset Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-171', url: '/item/deleteFixedAssetItem', featureName: 'Delete Fixed Asset Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-172', url: '/item/listFixedAssetItem', featureName: 'List Fixed Asset Item', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // app group
        new RequestMap(transactionCode: 'APP-74', url: '/appGroup/show', featureName: 'Show Group', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-75', url: '/appGroup/create', featureName: 'Create Group', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-76', url: '/appGroup/select', featureName: 'Select Group', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-77', url: '/appGroup/update', featureName: 'Update Group', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-78', url: '/appGroup/delete', featureName: 'Delete Group', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-79', url: '/appGroup/list', featureName: 'List Group', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // app company user
        new RequestMap(transactionCode: 'APP-86', url: '/appUser/showForCompanyUser', featureName: 'Show Company User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-87', url: '/appUser/createForCompanyUser', featureName: 'Create Company User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-88', url: '/appUser/deleteForCompanyUser', featureName: 'Delete Company User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-89', url: '/appUser/listForCompanyUser', featureName: 'List Company User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-90', url: '/appUser/selectForCompanyUser', featureName: 'Select Company User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-91', url: '/appUser/updateForCompanyUser', featureName: 'Update Company User', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // country
        new RequestMap(transactionCode: 'APP-93', url: '/country/show', featureName: 'Show country', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-94', url: '/country/create', featureName: 'Create country', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-95', url: '/country/delete', featureName: 'Delete country', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-96', url: '/country/update', featureName: 'Update country', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-97', url: '/country/select', featureName: 'Select country', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-98', url: '/country/list', featureName: 'List country', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        // designation
        new RequestMap(transactionCode: 'APP-118', url: '/designation/show', featureName: 'Show designation', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'APP-119', url: '/designation/create', featureName: 'Create designation', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-120', url: '/designation/update', featureName: 'Update designation', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-121', url: '/designation/delete', featureName: 'Delete designation', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-122', url: '/designation/list', featureName: 'List designation', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-123', url: '/designation/select', featureName: 'Select designation', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // costing type
        new RequestMap(transactionCode: 'APP-261', url: '/costingType/show', featureName: 'Show costing Type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'APP-262', url: '/costingType/create', featureName: 'Create costing Type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-263', url: '/costingType/update', featureName: 'Update costing Type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-264', url: '/costingType/delete', featureName: 'Delete costing Type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-265', url: '/costingType/list', featureName: 'List costing Type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-266', url: '/costingType/select', featureName: 'Select costing Type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // system Configuration
        new RequestMap(transactionCode: 'APP-99', url: '/systemConfiguration/show', featureName: 'Show system configuration', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-100', url: '/systemConfiguration/list', featureName: 'List system configuration', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-101', url: '/systemConfiguration/select', featureName: 'Select system configuration', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-102', url: '/systemConfiguration/update', featureName: 'Update system configuration', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // systemEntity
        new RequestMap(transactionCode: 'APP-103', url: '/systemEntity/show', featureName: 'Show system entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-104', url: '/systemEntity/create', featureName: 'Create system entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-105', url: '/systemEntity/list', featureName: 'List system entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-106', url: '/systemEntity/select', featureName: 'Select system entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-107', url: '/systemEntity/update', featureName: 'Update system entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-173', url: '/systemEntity/delete', featureName: 'Delete system entity', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // systemEntityType
        new RequestMap(transactionCode: 'APP-108', url: '/systemEntityType/show', featureName: 'Show system entity type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-109', url: '/systemEntityType/list', featureName: 'List system entity type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-110', url: '/systemEntityType/select', featureName: 'Select system entity type ', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-111', url: '/systemEntityType/update', featureName: 'Update system entity type', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        //Theme
        new RequestMap(transactionCode: 'APP-124', url: '/theme/showTheme', featureName: 'Show Theme', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-125', url: '/theme/updateTheme', featureName: 'Update Theme', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-126', url: '/theme/listTheme', featureName: 'List Theme', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-127', url: '/theme/selectTheme', featureName: 'Select Theme', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        //SMS
        new RequestMap(transactionCode: 'APP-180', url: '/sms/showSms', featureName: 'Show sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-181', url: '/sms/updateSms', featureName: 'Update sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-182', url: '/sms/listSms', featureName: 'List sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-183', url: '/sms/selectSms', featureName: 'Select sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-237', url: '/sms/sendSms', featureName: 'Send sms', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        //setting currency
        new RequestMap(transactionCode: 'APP-128', url: '/currency/show', featureName: 'Show currency', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save()
        new RequestMap(transactionCode: 'APP-129', url: '/currency/create', featureName: 'Create currency', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-130', url: '/currency/update', featureName: 'Update currency', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-131', url: '/currency/list', featureName: 'List currency', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-132', url: '/currency/edit', featureName: 'Edit currency', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-133', url: '/currency/delete', featureName: 'Delete currency', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save()

        // Content Category
        new RequestMap(transactionCode: 'APP-138', url: '/contentCategory/show', featureName: 'Show content category', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-139', url: '/contentCategory/select', featureName: 'Select content category', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-140', url: '/contentCategory/list', featureName: 'List content category', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-141', url: '/contentCategory/update', featureName: 'Update content category', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-142', url: '/contentCategory/create', featureName: 'Create content category', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-143', url: '/contentCategory/delete', featureName: 'Delete content category', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-153', url: '/contentCategory/listContentCategoryByContentTypeId', featureName: 'Get content category list by contet type ID', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // Entity Content
        new RequestMap(transactionCode: 'APP-144', url: '/entityContent/show', featureName: 'Show entity content', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-145', url: '/entityContent/select', featureName: 'Select entity content', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-146', url: '/entityContent/list', featureName: 'List entity content', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-147', url: '/entityContent/update', featureName: 'Update entity content', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-148', url: '/entityContent/create', featureName: 'Create entity content', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-149', url: '/entityContent/delete', featureName: 'Delete entity content', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-152', url: '/entityContent/downloadContent', featureName: 'Download entity content', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // Entity Note
        new RequestMap(transactionCode: 'APP-245', url: '/entityNote/show', featureName: 'Show entity note', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'APP-246', url: '/entityNote/select', featureName: 'Select entity note', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-247', url: '/entityNote/list', featureName: 'List entity note', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-248', url: '/entityNote/update', featureName: 'Update entity note', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-249', url: '/entityNote/create', featureName: 'Create entity note', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-250', url: '/entityNote/delete', featureName: 'Delete entity note', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // bank
        new RequestMap(transactionCode: 'APP-192', url: '/bank/show', featureName: 'Show Bank', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-193', url: '/bank/create', featureName: 'Create Bank', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-194', url: '/bank/update', featureName: 'Update Bank', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-195', url: '/bank/edit', featureName: 'Select Bank', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-196', url: '/bank/list', featureName: 'List Bank', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-197', url: '/bank/delete', featureName: 'Delete Bank', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-260', url: '/bank/reloadBankDropDownTagLib', featureName: 'Refresh bank dropDown', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // bankBranch
        new RequestMap(transactionCode: 'APP-203', url: '/bankBranch/show', featureName: 'Show bank branch', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-204', url: '/bankBranch/create', featureName: 'Create bank branch', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-205', url: '/bankBranch/update', featureName: 'Update bank branch', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-206', url: '/bankBranch/edit', featureName: 'Select bank branch', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-207', url: '/bankBranch/list', featureName: 'List bank branch', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-208', url: '/bankBranch/delete', featureName: 'Delete bank branch', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-209', url: '/bankBranch/reloadBranchesDropDownByBankAndDistrict', featureName: 'Get dropdown branches by bank and district independently', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-210', url: '/bankBranch/listDistributionPoint', featureName: 'List & Search Distribution Point for Admin & Cashier', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // district
        new RequestMap(transactionCode: 'APP-211', url: '/district/show', featureName: 'Show District', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-212', url: '/district/create', featureName: 'Create District', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-213', url: '/district/update', featureName: 'Update District', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-214', url: '/district/edit', featureName: 'Select District', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-215', url: '/district/list', featureName: 'List District', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-216', url: '/district/delete', featureName: 'Delete District', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'APP-236', url: '/district/reloadDistrictDropDown', featureName: 'Reload District dropdown independently', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // vehicle
        new RequestMap(transactionCode: 'APP-217', url: '/vehicle/show', featureName: 'Show vehicle', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-218', url: '/vehicle/select', featureName: 'Select vehicle', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-219', url: '/vehicle/create', featureName: 'Create vehicle', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-220', url: '/vehicle/update', featureName: 'Update vehicle', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-221', url: '/vehicle/delete', featureName: 'Delete vehicle', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-222', url: '/vehicle/list', featureName: 'List vehicle', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // supplier
        new RequestMap(transactionCode: 'APP-223', url: '/supplier/show', featureName: 'Show Supplier', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-224', url: '/supplier/create', featureName: 'Create Supplier', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-225', url: '/supplier/select', featureName: 'Select Supplier', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-226', url: '/supplier/update', featureName: 'Update Supplier', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-227', url: '/supplier/delete', featureName: 'Delete Supplier', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-228', url: '/supplier/list', featureName: 'List Supplier', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // supplier item
        new RequestMap(transactionCode: 'APP-229', url: '/supplierItem/show', featureName: 'Show Supplier-Item', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-230', url: '/supplierItem/create', featureName: 'Create Supplier-Item', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-231', url: '/supplierItem/select', featureName: 'Select Supplier-Item', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-232', url: '/supplierItem/update', featureName: 'Update Supplier-Item', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-233', url: '/supplierItem/delete', featureName: 'Delete Supplier-Item', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-234', url: '/supplierItem/list', featureName: 'List Supplier-Item', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'APP-235', url: '/supplierItem/getItemListForSupplierItem', featureName: 'Get Item List For Supplier-Item', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //reload entity note taglib
        new RequestMap(transactionCode: 'APP-252', url: '/entityNote/reloadEntityNote', featureName: 'Refresh entity note tagLib', configAttribute: 'ROLE_-2', pluginId: 1, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        return true
    }

    /**
     * Method to create request map for budget
     */
    public boolean createRequestMapForBudget() {
        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
        VALUES(-19,0, '/budgBudget/renderBudgetMenu','ROLE_-2','Budget Module',3,'BUDG-38',FALSE,FALSE)""")

        // BOQ implementation
        new RequestMap(transactionCode: 'BUDG-1', url: '/budgBudget/show', featureName: 'Show Budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-2', url: '/budgBudget/create', featureName: 'Create Budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-3', url: '/budgBudget/select', featureName: 'Select Budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-4', url: '/budgBudget/update', featureName: 'Update Budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-5', url: '/budgBudget/delete', featureName: 'Delete Budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-6', url: '/budgBudget/list', featureName: 'List Budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'BUDG-8', url: '/budgBudget/getBudgetGridByProject', featureName: 'Get Budget Grid List By Project', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-29', url: '/budgBudget/getBudgetGridByInventory', featureName: 'Get Budget Grid List By Inventory', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-30', url: '/budgBudget/getBudgetListForQs', featureName: 'Get Budget Grid List For QS', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-37', url: '/budgBudget/getBudgetStatusForDashBoard', featureName: 'Get Budget Status in Dash Board', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-83', url: '/budgBudget/getBudgetGridForSprint', featureName: 'Get Budget List for Sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //budg task
        new RequestMap(transactionCode: 'BUDG-54', url: '/budgTask/show', featureName: 'Show budget task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-55', url: '/budgTask/create', featureName: 'Create budget task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-56', url: '/budgTask/select', featureName: 'Select budget task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-57', url: '/budgTask/update', featureName: 'Update budget task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-58', url: '/budgTask/delete', featureName: 'Delete budget task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-59', url: '/budgTask/list', featureName: 'List budget task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-85', url: '/budgTask/showTaskForSprint', featureName: 'Show list of budget for task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-88', url: '/budgTask/listTaskForSprint', featureName: 'Get list of budget for task', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-89', url: '/budgTask/updateTaskForSprint', featureName: 'update task list status', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // BudgetDetails
        new RequestMap(transactionCode: 'BUDG-9', url: '/budgBudgetDetails/show', featureName: 'Show Budget Details', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-10', url: '/budgBudgetDetails/create', featureName: 'Create Budget Details', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-11', url: '/budgBudgetDetails/select', featureName: 'Select Budget Details', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-12', url: '/budgBudgetDetails/update', featureName: 'Update Budget Details', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-13', url: '/budgBudgetDetails/delete', featureName: 'Delete Budget Details', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-14', url: '/budgBudgetDetails/list', featureName: 'List Budget Details', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'BUDG-15', url: '/budgBudgetDetails/getItemListBudgetDetails', featureName: 'Get Inventory Item by Budget and Item Type', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-90', url: '/budgBudgetDetails/generateBudgetRequirement', featureName: 'Generate budget requirements according to schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();

        // budget schema
        new RequestMap(transactionCode: 'BUDG-60', url: '/budgSchema/show', featureName: 'Show budget schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-61', url: '/budgSchema/create', featureName: 'Create budget schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-62', url: '/budgSchema/select', featureName: 'Select budget schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-63', url: '/budgSchema/update', featureName: 'Update budget schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-64', url: '/budgSchema/delete', featureName: 'Delete budget schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-65', url: '/budgSchema/list', featureName: 'List budget schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-66', url: '/budgSchema/listItemForBudgetSchema', featureName: 'Get list of item for budget schema', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // sprint
        new RequestMap(transactionCode: 'BUDG-67', url: '/budgSprint/show', featureName: 'Show sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-68', url: '/budgSprint/create', featureName: 'Create sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-69', url: '/budgSprint/select', featureName: 'Select sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-70', url: '/budgSprint/update', featureName: 'Update sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-71', url: '/budgSprint/delete', featureName: 'Delete sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-72', url: '/budgSprint/list', featureName: 'List sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-84', url: '/budgSprint/setCurrentBudgSprint', featureName: 'Set Current Budget sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-86', url: '/budgSprint/showForCurrentSprint', featureName: 'Show current sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-87', url: '/budgSprint/listForCurrentSprint', featureName: 'List current sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // sprint budget
        new RequestMap(transactionCode: 'BUDG-73', url: '/budgSprintBudget/show', featureName: 'Show sprint budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-74', url: '/budgSprintBudget/create', featureName: 'Create sprint budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-75', url: '/budgSprintBudget/select', featureName: 'Select sprint budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-76', url: '/budgSprintBudget/update', featureName: 'Update sprint budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-77', url: '/budgSprintBudget/delete', featureName: 'Delete sprint budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-78', url: '/budgSprintBudget/list', featureName: 'List sprint budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //budgetType
        new RequestMap(transactionCode: 'BUDG-16', url: '/budgetScope/show', featureName: 'Show Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-17', url: '/budgetScope/create', featureName: 'Create Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-18', url: '/budgetScope/select', featureName: 'Select Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-19', url: '/budgetScope/update', featureName: 'Update Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-20', url: '/budgetScope/delete', featureName: 'Delete Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-21', url: '/budgetScope/list', featureName: 'List Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //projectBudgetScope
        new RequestMap(transactionCode: 'BUDG-22', url: '/projectBudgetScope/show', featureName: 'Show Project Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-23', url: '/projectBudgetScope/select', featureName: 'Select Project Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-24', url: '/projectBudgetScope/update', featureName: 'Update Project Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'BUDG-25', url: '/projectBudgetScope/getBudgetScope', featureName: 'Get Budget Scope', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // report
        new RequestMap(transactionCode: 'BUDG-26', url: '/budgReport/showBudgetRpt', featureName: 'Show Budget Report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-27', url: '/budgReport/searchBudgetRpt', featureName: 'Search Budget Report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-28', url: '/budgReport/downloadBudgetRpt', featureName: 'Download Budget Report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // project status
        new RequestMap(transactionCode: 'BUDG-31', url: '/budgReport/showProjectStatus', featureName: 'Show Project Status', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-32', url: '/budgReport/searchProjectStatus', featureName: 'Search Project Status', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-36', url: '/budgReport/downloadProjectStatus', featureName: 'Download Project Status', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // project costing
        new RequestMap(transactionCode: 'BUDG-51', url: '/budgReport/listProjectCosting', featureName: 'List Project Costing', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-52', url: '/budgReport/downloadProjectCosting', featureName: 'Download Project Costing', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-53', url: '/budgReport/showProjectCosting', featureName: 'Show Project Costing', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // consumption deviation
        new RequestMap(transactionCode: 'BUDG-40', url: '/budgReport/showConsumptionDeviation', featureName: 'Show Consumption Deviation', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-41', url: '/budgReport/listConsumptionDeviation', featureName: 'List Consumption Deviation', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-42', url: '/budgReport/downloadConsumptionDeviation', featureName: 'Download Consumption Deviation', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-43', url: '/budgReport/downloadConsumptionDeviationCsv', featureName: 'Download Consumption Deviation Csv Report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // budget sprint
        new RequestMap(transactionCode: 'BUDG-80', url: '/budgReport/downloadSprintReport', featureName: 'Download budget sprint report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-81', url: '/budgReport/showBudgetSprint', featureName: 'Show budget sprint report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-82', url: '/budgReport/searchBudgetSprint', featureName: 'Search budget sprint report', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-94', url: '/budgReport/downloadForecastingReport', featureName: 'Download forecasting report for sprint', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // project budget
        new RequestMap(transactionCode: 'BUDG-91', url: '/budgReport/showProjectBudget', featureName: 'Show project budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-92', url: '/budgReport/listProjectBudget', featureName: 'List project budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'BUDG-93', url: '/budgReport/downloadProjectBudget', featureName: 'Download project budget', configAttribute: 'ROLE_-2', pluginId: 3, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        return true
    }

    /**
     * Method to create request map for procurement
     */
    public boolean createRequestMapForProcurement() {
        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
        VALUES(-16,0, '/procurement/renderProcurementMenu','ROLE_-2','Procurement Module',5,'PROC-78',FALSE,FALSE)""")

        // purchaseRequest
        new RequestMap(transactionCode: 'PROC-1', url: '/procPurchaseRequest/show', featureName: 'Show Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-2', url: '/procPurchaseRequest/create', featureName: 'Create Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-3', url: '/procPurchaseRequest/select', featureName: 'Select Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-4', url: '/procPurchaseRequest/update', featureName: 'Update Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-5', url: '/procPurchaseRequest/delete', featureName: 'Delete Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-6', url: '/procPurchaseRequest/list', featureName: 'List Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-7', url: '/procPurchaseRequest/approve', featureName: 'Approve Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-93', url: '/procPurchaseRequest/sentMailForPRApproval', featureName: 'Send mail for approval of Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // get budget info  for Purchase Request
        new RequestMap(transactionCode: 'PROC-69', url: '/procPurchaseRequest/getBudgetForPR', featureName: 'Get Budget Info for Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-83', url: '/procPurchaseRequest/listUnApprovedPR', featureName: 'List For All Unapproved PR To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-84', url: '/procPurchaseRequest/approvePRDashBoard', featureName: 'Approve PR From Dash Board', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-96', url: '/procPurchaseRequest/unApprovePR', featureName: 'Un Approve Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // purchaseRequestDetails
        new RequestMap(transactionCode: 'PROC-8', url: '/procPurchaseRequestDetails/show', featureName: 'Show Purchase Request Details of Material', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-9', url: '/procPurchaseRequestDetails/create', featureName: 'Create Purchase Request Details of Material', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-10', url: '/procPurchaseRequestDetails/select', featureName: 'Select Purchase Request Details of Material', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-11', url: '/procPurchaseRequestDetails/update', featureName: 'Update Purchase Request Details of Material', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-12', url: '/procPurchaseRequestDetails/delete', featureName: 'Delete Purchase Request Details of Material', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-13', url: '/procPurchaseRequestDetails/list', featureName: 'List Purchase Request Details of Material', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-14', url: '/procPurchaseRequestDetails/getItemList', featureName: 'Get Item List for Purchase Request', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // purchaseOrder

        new RequestMap(transactionCode: 'PROC-80', url: '/procPurchaseOrder/approvePODashBoard', featureName: 'Approved PO For Dash Board', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'PROC-15', url: '/procPurchaseOrder/show', featureName: 'Show Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-16', url: '/procPurchaseOrder/create', featureName: 'Create Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-94', url: '/procPurchaseOrder/cancelPO', featureName: 'Cancel Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-17', url: '/procPurchaseOrder/select', featureName: 'Select Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-18', url: '/procPurchaseOrder/update', featureName: 'Update Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-19', url: '/procPurchaseOrder/delete', featureName: 'Delete Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-20', url: '/procPurchaseOrder/list', featureName: 'List Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-95', url: '/procPurchaseOrder/unApprovePO', featureName: 'Un Approve Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'PROC-22', url: '/procPurchaseOrder/approve', featureName: 'Approve Purchase Order', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-79', url: '/procPurchaseOrder/listUnApprovedPO', featureName: 'List Un-Approved PO', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-90', url: '/procPurchaseOrder/getPOStatusForDashBoard', featureName: 'Get PO Status in Dash Board', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-92', url: '/procPurchaseOrder/sendForPOApproval', featureName: 'Send for PO Approval', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // cancelled PO
        new RequestMap(transactionCode: 'PROC-97', url: '/procCancelledPO/show', featureName: 'Show all cancelled PO', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-98', url: '/procCancelledPO/list', featureName: 'List of all cancelled PO', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // purchaseOrderDetails
        new RequestMap(transactionCode: 'PROC-23', url: '/procPurchaseOrderDetails/show', featureName: 'Show Purchase Order Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-24', url: '/procPurchaseOrderDetails/create', featureName: 'Create Purchase Order Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-25', url: '/procPurchaseOrderDetails/select', featureName: 'Select Purchase Order Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-26', url: '/procPurchaseOrderDetails/update', featureName: 'Update Purchase Order Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-27', url: '/procPurchaseOrderDetails/delete', featureName: 'Delete Purchase Order Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-28', url: '/procPurchaseOrderDetails/list', featureName: 'List Purchase Order Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-30', url: '/procPurchaseOrderDetails/getItemListPurchaseOrderDetails', featureName: 'Get item list for purchase order details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // procIndent
        new RequestMap(transactionCode: 'PROC-31', url: '/procIndent/show', featureName: 'Show Indent', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PROC-32', url: '/procIndent/create', featureName: 'Create Indent', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PROC-33', url: '/procIndent/select', featureName: 'Select Indent', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PROC-34', url: '/procIndent/update', featureName: 'Update Indent', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PROC-35', url: '/procIndent/delete', featureName: 'Delete Indent', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PROC-36', url: '/procIndent/list', featureName: 'List Indent', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'PROC-81', url: '/procIndent/approve', featureName: 'Approve Indent', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PROC-85', url: '/procIndent/listOfUnApprovedIndent', featureName: 'List Of Unapproved Indent at Dash Board', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PROC-86', url: '/procIndent/approveIndentDashBoard', featureName: 'Approve Indent at DashBoard', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // indentDetails
        new RequestMap(transactionCode: 'PROC-37', url: '/procIndentDetails/show', featureName: 'Show Indent Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-38', url: '/procIndentDetails/create', featureName: 'Create Indent Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-39', url: '/procIndentDetails/select', featureName: 'Select Indent Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-40', url: '/procIndentDetails/update', featureName: 'Update Indent Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-41', url: '/procIndentDetails/delete', featureName: 'Delete Indent Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-42', url: '/procIndentDetails/list', featureName: 'List Indent Details', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // purchaseRequestReport
        new RequestMap(transactionCode: 'PROC-44', url: '/procReport/showPurchaseRequestRpt', featureName: 'Show Purchase Request Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-45', url: '/procReport/searchPurchaseRequestRpt', featureName: 'Search Purchase Request Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-46', url: '/procReport/downloadPurchaseRequestRpt', featureName: 'Download Purchase Request Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // purchaseOrderReport
        new RequestMap(transactionCode: 'PROC-47', url: '/procReport/showPurchaseOrderRpt', featureName: 'Show Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-48', url: '/procReport/searchPurchaseOrderRpt', featureName: 'Search Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-49', url: '/procReport/downloadPurchaseOrderRpt', featureName: 'Download Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // mdfReport
        new RequestMap(transactionCode: 'PROC-50', url: '/procReport/showIndentRpt', featureName: 'Show Indent Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-51', url: '/procReport/searchIndentRpt', featureName: 'Search Indent Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-52', url: '/procReport/downloadIndentRpt', featureName: 'Download Indent Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // procTransportCost
        new RequestMap(transactionCode: 'PROC-53', url: '/procTransportCost/show', featureName: 'Show Transport Cost', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-54', url: '/procTransportCost/create', featureName: 'Create Transport Cost', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-55', url: '/procTransportCost/edit', featureName: 'Select Transport Cost', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-56', url: '/procTransportCost/update', featureName: 'Update Transport Cost', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-57', url: '/procTransportCost/delete', featureName: 'Delete Transport Cost', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-58', url: '/procTransportCost/list', featureName: 'List Transport Cost', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // get budget info  for Purchase Request
        new RequestMap(transactionCode: 'PROC-70', url: '/procTermsAndCondition/show', featureName: 'Show Procurement Terms and Condition', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-71', url: '/procTermsAndCondition/create', featureName: 'Create Procurement Terms and Condition', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-72', url: '/procTermsAndCondition/select', featureName: 'Select Procurement Terms and Condition', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-73', url: '/procTermsAndCondition/update', featureName: 'Update Procurement Terms and Condition', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-74', url: '/procTermsAndCondition/delete', featureName: 'Delete Procurement Terms and Condition', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-75', url: '/procTermsAndCondition/list', featureName: 'List Procurement Terms and Condition', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // Supplier Wise Purchase Order Report
        new RequestMap(transactionCode: 'PROC-76', url: '/procReport/showSupplierWisePO', featureName: 'Show Supplier wise Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-77', url: '/procReport/listSupplierWisePO', featureName: 'List Supplier wise Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-82', url: '/procReport/downloadSupplierWisePO', featureName: 'Download Supplier wise Purchase Order Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'PROC-91', url: '/procReport/downloadSupplierWisePOCsv', featureName: 'Download Supplier wise Purchase Order Csv Report', configAttribute: 'ROLE_-2', pluginId: 5, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        return true
    }

    /**
     * Method to create request map for accounting plugin
     */
    public boolean createRequestMapForAccounting() {
        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
        VALUES(-18,0, '/accounting/renderAccountingMenu','ROLE_-2','Accounting Module',2,'ACC-157',FALSE,FALSE)""")

        // Acc Custom Group
        new RequestMap(transactionCode: 'ACC-1', url: '/accCustomGroup/show', featureName: 'Show Custom Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-2', url: '/accCustomGroup/create', featureName: 'Create Custom Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-3', url: '/accCustomGroup/select', featureName: 'Select Custom Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-4', url: '/accCustomGroup/update', featureName: 'Update Custom Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-5', url: '/accCustomGroup/delete', featureName: 'Delete Custom Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-6', url: '/accCustomGroup/list', featureName: 'List Custom Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // Acc Chart of Account
        new RequestMap(transactionCode: 'ACC-7', url: '/accChartOfAccount/show', featureName: 'Show Chart of Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-8', url: '/accChartOfAccount/create', featureName: 'Create Chart of Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-9', url: '/accChartOfAccount/select', featureName: 'Select Chart of Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-10', url: '/accChartOfAccount/update', featureName: 'Update Chart of Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-11', url: '/accChartOfAccount/delete', featureName: 'Delete Chart of Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-12', url: '/accChartOfAccount/list', featureName: 'List Chart of Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-148', url: '/accChartOfAccount/getSourceCategoryByAccSource', featureName: 'Get Source-Category By AccSourceId', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-13', url: '/accChartOfAccount/listForVoucher', featureName: 'List For Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-14', url: '/accChartOfAccount/listSourceByCoaCode', featureName: 'Get source list by acc-chart-of-account code', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-158', url: '/accChartOfAccount/listForVoucherByBankCashGroup', featureName: 'List Of Voucher By Bank-Cash-Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-147', url: '/accChartOfAccount/listAccChartOfAccountByAccGroupId', featureName: 'List Of COA By Group Id', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // Acc Group
        new RequestMap(transactionCode: 'ACC-15', url: '/accGroup/show', featureName: 'Show Account Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-16', url: '/accGroup/create', featureName: 'Create Account Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-17', url: '/accGroup/select', featureName: 'Select Account Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-18', url: '/accGroup/update', featureName: 'Update Account Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-19', url: '/accGroup/delete', featureName: 'Delete Account Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-20', url: '/accGroup/list', featureName: 'List Account Group', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // AccTier1
        new RequestMap(transactionCode: 'ACC-21', url: '/accTier1/show', featureName: 'Show Tier 1', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-22', url: '/accTier1/create', featureName: 'Create Tier 1', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-23', url: '/accTier1/select', featureName: 'Select Tier 1', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-24', url: '/accTier1/update', featureName: 'Update Tier 1', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-25', url: '/accTier1/delete', featureName: 'Delete Tier 1', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-26', url: '/accTier1/list', featureName: 'List Tier 1', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-27', url: '/accTier1/getTier1ByAccTypeId', featureName: 'Get Tier 1 List by Account Type Id', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // AccTier3
        new RequestMap(transactionCode: 'ACC-28', url: '/accTier3/show', featureName: 'Show Tier 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-29', url: '/accTier3/create', featureName: 'Create Tier 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-30', url: '/accTier3/select', featureName: 'Select Tier 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-31', url: '/accTier3/update', featureName: 'Update Tier 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-32', url: '/accTier3/delete', featureName: 'Delete Tier 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-33', url: '/accTier3/list', featureName: 'List Tier 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'ACC-34', url: '/accTier3/getTier3ByAccTier2Id', featureName: 'Populate Tier3 for Tier2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // AccTier2
        new RequestMap(transactionCode: 'ACC-35', url: '/accTier2/show', featureName: 'Show Tier 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-36', url: '/accTier2/create', featureName: 'Create Tier 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-37', url: '/accTier2/select', featureName: 'Select Tier 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-38', url: '/accTier2/update', featureName: 'Update Tier 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-39', url: '/accTier2/delete', featureName: 'Delete Tier 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-40', url: '/accTier2/list', featureName: 'List Tier 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-41', url: '/accTier2/getTier2ByAccTier1Id', featureName: 'Get Tier 2 List by Tier 1 Id', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // AccIpc
        new RequestMap(transactionCode: 'ACC-185', url: '/accIpc/show', featureName: 'Show IPC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-186', url: '/accIpc/create', featureName: 'Create IPC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-187', url: '/accIpc/select', featureName: 'Select IPC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-188', url: '/accIpc/update', featureName: 'Update IPC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-189', url: '/accIpc/delete', featureName: 'Delete IPC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-190', url: '/accIpc/list', featureName: 'List IPC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // AccType
        new RequestMap(transactionCode: 'ACC-206', url: '/accType/show', featureName: 'Show Account Type', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-207', url: '/accType/list', featureName: 'List Account Type', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-208', url: '/accType/select', featureName: 'Select Account Type', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-209', url: '/accType/update', featureName: 'Update Account Type', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-252', url: '/accType/delete', featureName: 'Delete Account Type', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // AccLc
        new RequestMap(transactionCode: 'ACC-191', url: '/accLc/show', featureName: 'Show LC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-192', url: '/accLc/create', featureName: 'Create LC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-193', url: '/accLc/select', featureName: 'Select LC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-194', url: '/accLc/update', featureName: 'Update LC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-195', url: '/accLc/delete', featureName: 'Delete LC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-196', url: '/accLc/list', featureName: 'List LC', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // AccLeaseAccount
        new RequestMap(transactionCode: 'ACC-198', url: '/accLeaseAccount/show', featureName: 'Show Lease Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-199', url: '/accLeaseAccount/create', featureName: 'Create Lease Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-200', url: '/accLeaseAccount/select', featureName: 'Select Lease Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-201', url: '/accLeaseAccount/update', featureName: 'Update Lease Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-202', url: '/accLeaseAccount/delete', featureName: 'Delete Lease Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-203', url: '/accLeaseAccount/list', featureName: 'List Lease Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // Voucher-Type implementation
        new RequestMap(transactionCode: 'ACC-42', url: '/accVoucherTypeCoa/show', featureName: 'Show Acc Voucher Type Coa', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-43', url: '/accVoucherTypeCoa/create', featureName: 'Create Acc Voucher Type Coa', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-44', url: '/accVoucherTypeCoa/select', featureName: 'Select Acc Voucher Type Coa', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-45', url: '/accVoucherTypeCoa/update', featureName: 'Update Acc Voucher Type Coa', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-46', url: '/accVoucherTypeCoa/delete', featureName: 'Delete Acc Voucher Type Coa', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-47', url: '/accVoucherTypeCoa/list', featureName: 'List Acc Voucher Type Coa', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // AccSubAccount implementation
        new RequestMap(transactionCode: 'ACC-48', url: '/accSubAccount/show', featureName: 'Show Sub Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-49', url: '/accSubAccount/create', featureName: 'Create Sub Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-50', url: '/accSubAccount/select', featureName: 'Select Sub Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-51', url: '/accSubAccount/update', featureName: 'Update Sub Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-52', url: '/accSubAccount/delete', featureName: 'Delete Sub Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-53', url: '/accSubAccount/list', featureName: 'List Sub Account', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-54', url: '/accSubAccount/getListByCoaId', featureName: 'List By Chart Of Accounts', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // AccDivision implementation
        new RequestMap(transactionCode: 'ACC-55', url: '/accDivision/show', featureName: 'Show Division', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-56', url: '/accDivision/create', featureName: 'Create Division', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-57', url: '/accDivision/select', featureName: 'Select Division', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-58', url: '/accDivision/update', featureName: 'Update Division', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-59', url: '/accDivision/delete', featureName: 'Delete Division', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-60', url: '/accDivision/list', featureName: 'List Division', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-61', url: '/accDivision/getDivisionListByProjectId', featureName: 'Get Division List By Project', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // voucher
        new RequestMap(transactionCode: 'ACC-62', url: '/accVoucher/show', featureName: 'Show Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-63', url: '/accVoucher/create', featureName: 'Create Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-251', url: '/accVoucher/cancelVoucher', featureName: 'Cancel Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-64', url: '/accVoucher/select', featureName: 'Select Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-65', url: '/accVoucher/update', featureName: 'Update Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-66', url: '/accVoucher/list', featureName: 'List Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'ACC-67', url: '/accVoucher/postVoucher', featureName: 'Post Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'ACC-68', url: '/accVoucher/showPayCash', featureName: 'Show Pay Cash', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-69', url: '/accVoucher/listPayCash', featureName: 'List Pay Cash', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'ACC-70', url: '/accVoucher/showPayBank', featureName: 'Show Pay Bank', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-71', url: '/accVoucher/listPayBank', featureName: 'List Pay Bank', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'ACC-72', url: '/accVoucher/showReceiveCash', featureName: 'Show Receive Cash', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-73', url: '/accVoucher/listReceiveCash', featureName: 'List Receive Cash', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'ACC-74', url: '/accVoucher/showReceiveBank', featureName: 'Show Receive Bank', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-75', url: '/accVoucher/listReceiveBank', featureName: 'List Receive Bank', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'ACC-76', url: '/accReport/downloadChartOfAccounts', featureName: 'Download Chart Of Accounts', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'ACC-152', url: '/accVoucher/listOfUnApprovedPayCash', featureName: 'List Of UnApproved Pay Cash To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-153', url: '/accVoucher/listOfUnApprovedPayBank', featureName: 'List Of UnApproved Pay Bank To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-154', url: '/accVoucher/listOfUnApprovedReceiveCash', featureName: 'List Of UnApproved Receive Cash To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-155', url: '/accVoucher/listOfUnApprovedReceiveBank', featureName: 'List Of UnApproved Receive Bank To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'ACC-156', url: '/accVoucher/listOfUnApprovedJournal', featureName: 'List Of UnApproved Journal To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //Acc cancelled voucher
        new RequestMap(transactionCode: 'ACC-261', url: '/accCancelledVoucher/showCancelledVoucher', featureName: 'Show Cancelled voucher', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.TRUE).save();
        new RequestMap(transactionCode: 'ACC-262', url: '/accCancelledVoucher/listCancelledVoucher', featureName: 'List Cancelled voucher', configAttribute: 'ROLE_-2', pluginId: 1, isCommon: Boolean.FALSE, isViewable: Boolean.FALSE).save();

        // voucher report
        new RequestMap(transactionCode: 'ACC-77', url: '/accReport/showVoucher', featureName: 'Show Voucher Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-78', url: '/accReport/searchVoucher', featureName: 'Search Voucher Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-79', url: '/accReport/downloadVoucher', featureName: 'Download Voucher Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-129', url: '/accReport/downloadVoucherBankCheque', featureName: 'Download Voucher Bank Cheque Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-130', url: '/accReport/downloadVoucherBankChequePreview', featureName: 'Download Voucher Bank Cheque Preview Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-257', url: '/accReport/sendMailForUnpostedVoucher', featureName: 'Send mail for un-posted voucher report', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // voucherList
        new RequestMap(transactionCode: 'ACC-80', url: '/accReport/showVoucherList', featureName: 'Show Voucher List Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-81', url: '/accReport/searchVoucherList', featureName: 'Search Voucher List Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-82', url: '/accReport/downloadVoucherList', featureName: 'Download Voucher List Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-83', url: '/accReport/listForVoucherDetails', featureName: 'Show Voucher Details List Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // ledger
        new RequestMap(transactionCode: 'ACC-84', url: '/accReport/showLedger', featureName: 'Show Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-85', url: '/accReport/listLedger', featureName: 'List Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-86', url: '/accReport/downloadLedger', featureName: 'Download Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-175', url: '/accReport/downloadLedgerCsv', featureName: 'Download Ledger CSV Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // sourceLedger
        new RequestMap(transactionCode: 'ACC-87', url: '/accReport/showSourceLedger', featureName: 'Show Source Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-88', url: '/accReport/listSourceLedger', featureName: 'List Source Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-89', url: '/accReport/downloadSourceLedger', featureName: 'Download Source Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-174', url: '/accReport/downloadSourceLedgerCsv', featureName: 'Download Source Ledger CSV Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-149', url: '/accReport/listSourceByCategoryAndType', featureName: 'List Source By Type and Category', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-215', url: '/accReport/listSourceCategoryForSourceLedger', featureName: 'List Source Category for Source Ledger', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-259', url: '/accReport/downloadSourceLedgeReportGroupBySource', featureName: 'Download Source Ledger Report Group By Source', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // groupLedger
        new RequestMap(transactionCode: 'ACC-90', url: '/accReport/showForGroupLedgerRpt', featureName: 'Show Group Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-91', url: '/accReport/listForGroupLedgerRpt', featureName: 'List Group Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-92', url: '/accReport/downloadForGroupLedgerRpt', featureName: 'Download Group Ledger Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-171', url: '/accReport/downloadForGroupLedgerCsvRpt', featureName: 'Download Group Ledger CSV Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // Level-2 trialBalance
        new RequestMap(transactionCode: 'ACC-247', url: '/accReport/showTrialBalanceOfLevel2', featureName: 'Show level 2 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-248', url: '/accReport/listTrialBalanceOfLevel2', featureName: 'List level 2 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-249', url: '/accReport/downloadTrialBalanceOfLevel2', featureName: 'Download level 2 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-250', url: '/accReport/downloadTrialBalanceCsvOfLevel2', featureName: 'Download level 2 trial balance Csv report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // Level-3 trialBalance
        new RequestMap(transactionCode: 'ACC-216', url: '/accReport/showTrialBalanceOfLevel3', featureName: 'Show level 3 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-217', url: '/accReport/listTrialBalanceOfLevel3', featureName: 'List level 3 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-218', url: '/accReport/downloadTrialBalanceOfLevel3', featureName: 'Download level 3 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-219', url: '/accReport/downloadTrialBalanceCsvOfLevel3', featureName: 'Download level 3 trial balance Csv report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // Level-4 trialBalance
        new RequestMap(transactionCode: 'ACC-220', url: '/accReport/showTrialBalanceOfLevel4', featureName: 'Show level 4 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-221', url: '/accReport/listTrialBalanceOfLevel4', featureName: 'List level 4 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-222', url: '/accReport/downloadTrialBalanceOfLevel4', featureName: 'Download level 4 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-223', url: '/accReport/downloadTrialBalanceCsvOfLevel4', featureName: 'Download level 4 trial balance Csv report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // Level-5 trialBalance
        new RequestMap(transactionCode: 'ACC-224', url: '/accReport/showTrialBalanceOfLevel5', featureName: 'Show level 5 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-225', url: '/accReport/listTrialBalanceOfLevel5', featureName: 'List level 5 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-226', url: '/accReport/downloadTrialBalanceOfLevel5', featureName: 'Download level 5 trial balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-227', url: '/accReport/downloadTrialBalanceCsvOfLevel5', featureName: 'Download level 5 trial balance Csv report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'ACC-96', url: '/accVoucher/unPostedVoucher', featureName: 'Un-Post Acc Voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // supplier payment report
        new RequestMap(transactionCode: 'ACC-97', url: '/accReport/showSupplierWisePayment', featureName: 'Show Supplier Purchase Order report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-98', url: '/accReport/listSupplierWisePayment', featureName: 'List Supplier Purchase Order report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-99', url: '/accReport/downloadSupplierWisePayment', featureName: 'Download Supplier Purchase Order report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-173', url: '/accReport/downloadSupplierWisePaymentCsv', featureName: 'Download Supplier Purchase Order CSV report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // incomeStatement level 4
        new RequestMap(transactionCode: 'ACC-235', url: '/accReport/showIncomeStatementOfLevel4', featureName: 'Show income statement report of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-236', url: '/accReport/listIncomeStatementOfLevel4', featureName: 'List income statement report of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-237', url: '/accReport/downloadIncomeStatementOfLevel4', featureName: 'Download income statement report of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-238', url: '/accReport/downloadIncomeStatementCsvOfLevel4', featureName: 'Download income statement CSV report of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // incomeStatement level 5
        new RequestMap(transactionCode: 'ACC-239', url: '/accReport/showIncomeStatementOfLevel5', featureName: 'Show income statement report of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-240', url: '/accReport/listIncomeStatementOfLevel5', featureName: 'List income statement report of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-241', url: '/accReport/downloadIncomeStatementOfLevel5', featureName: 'Download income statement report of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-242', url: '/accReport/downloadIncomeStatementCsvOfLevel5', featureName: 'Download income statement CSV report of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // financialStatement level 5
        new RequestMap(transactionCode: 'ACC-210', url: '/accReport/showFinancialStatementOfLevel5', featureName: 'Show financial statement of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-211', url: '/accReport/listFinancialStatementOfLevel5', featureName: 'List financial statement of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-212', url: '/accReport/downloadFinancialStatementOfLevel5', featureName: 'Download financial statement of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-234', url: '/accReport/downloadFinancialStatementCsvOfLevel5', featureName: 'Download financial statement csv of level 5', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // financialStatement level 4
        new RequestMap(transactionCode: 'ACC-213', url: '/accReport/showFinancialStatementOfLevel4', featureName: 'Show financial statement of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-214', url: '/accReport/listFinancialStatementOfLevel4', featureName: 'List financial statement of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-228', url: '/accReport/downloadFinancialStatementOfLevel4', featureName: 'Download financial statement of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-233', url: '/accReport/downloadFinancialStatementCsvOfLevel4', featureName: 'Download financial statement csv of level 4', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // financialStatement level 3
        new RequestMap(transactionCode: 'ACC-229', url: '/accReport/showFinancialStatementOfLevel3', featureName: 'Show financial statement of level 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-230', url: '/accReport/listFinancialStatementOfLevel3', featureName: 'List financial statement of level 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-231', url: '/accReport/downloadFinancialStatementOfLevel3', featureName: 'Download financial statement of level 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-232', url: '/accReport/downloadFinancialStatementCsvOfLevel3', featureName: 'Download financial statement csv of level 3', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // financialStatement level 2
        new RequestMap(transactionCode: 'ACC-243', url: '/accReport/showFinancialStatementOfLevel2', featureName: 'Show financial statement of level 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-244', url: '/accReport/listFinancialStatementOfLevel2', featureName: 'List financial statement of level 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-245', url: '/accReport/downloadFinancialStatementOfLevel2', featureName: 'Download financial statement of level 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-246', url: '/accReport/downloadFinancialStatementCsvOfLevel2', featureName: 'Download financial statement csv of level 2', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //project wise expense
        new RequestMap(transactionCode: 'ACC-106', url: '/accReport/showProjectWiseExpense', featureName: 'Show project wise expense ', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-107', url: '/accReport/listProjectWiseExpense', featureName: 'List project wise expense ', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-108', url: '/accReport/listProjectWiseExpenseDetails', featureName: 'List project wise expense details ', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-109', url: '/accReport/downloadProjectWiseExpense', featureName: 'Download project wise expense', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-179', url: '/accReport/downloadProjectWiseExpenseCsv', featureName: 'Download project wise expense CSV report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-256', url: '/accReport/sendMailForProjectWiseExpense', featureName: 'Send mail for Project wise expense', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //project fund flow
        new RequestMap(transactionCode: 'ACC-253', url: '/accReport/downloadProjectFundFlowReport', featureName: 'Download project fund flow report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-254', url: '/accReport/showProjectFundFlowReport', featureName: 'Show project fund flow', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-255', url: '/accReport/listProjectFundFlowReport', featureName: 'List project fund flow ', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //delete voucher
        new RequestMap(transactionCode: 'ACC-110', url: '/accVoucher/deleteVoucher', featureName: 'Delete voucher', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // source wise balance
        new RequestMap(transactionCode: 'ACC-111', url: '/accReport/showSourceWiseBalance', featureName: 'Show Source Balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-112', url: '/accReport/listSourceWiseBalance', featureName: 'List Source Balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-113', url: '/accReport/downloadSourceWiseBalance', featureName: 'Download Source Wise Balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-180', url: '/accReport/downloadSourceWiseBalanceCsv', featureName: 'Download Source Wise Balance Csv report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-205', url: '/accReport/downloadVoucherListBySourceId', featureName: 'Download Voucher List By SourceId', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-260', url: '/accReport/listSourceCategoryForSourceWiseBalance', featureName: 'List Source Category for Source wise balance', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //Acc IOU Slip Report
        new RequestMap(transactionCode: 'ACC-141', url: '/accReport/showAccIouSlipRpt', featureName: 'Show Acc Iou Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-142', url: '/accReport/listAccIouSlipRpt', featureName: 'List Acc Iou Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-143', url: '/accReport/downloadAccIouSlipRpt', featureName: 'Download Acc Iou Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // get total of voucher
        new RequestMap(transactionCode: 'ACC-160', url: '/accReport/retrieveTotalOfPayCash', featureName: 'Get total amount of posted Pay Cash Voucher', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-161', url: '/accReport/retrieveTotalOfPayCheque', featureName: 'Get total amount of posted Pay Cheque Voucher', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-162', url: '/accReport/retrieveTotalOfPayChequeOnChequeDate', featureName: 'Get total amount of posted Pay Cheque Voucher on cheque date', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //For Acc-Iou-Slip
        new RequestMap(transactionCode: 'ACC-121', url: '/accIouSlip/show', featureName: 'Show IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-122', url: '/accIouSlip/create', featureName: 'Create IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-123', url: '/accIouSlip/select', featureName: 'Select IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-124', url: '/accIouSlip/update', featureName: 'Update IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-125', url: '/accIouSlip/delete', featureName: 'Delete IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-126', url: '/accIouSlip/list', featureName: 'List IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-127', url: '/accIouSlip/sentNotification', featureName: 'Send for Approval of IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-128', url: '/accIouSlip/approve', featureName: 'Approve IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-151', url: '/accIouSlip/getIndentList', featureName: 'Get Indent List for IOU Slip', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //For Acc-Iou-Purpose
        new RequestMap(transactionCode: 'ACC-131', url: '/accIouPurpose/show', featureName: 'Show For Acc Iou Purpose', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-132', url: '/accIouPurpose/create', featureName: 'Create For Acc Iou Purpose', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-133', url: '/accIouPurpose/select', featureName: 'Select For Acc Iou Purpose', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-134', url: '/accIouPurpose/update', featureName: 'Update For Acc Iou Purpose', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-135', url: '/accIouPurpose/delete', featureName: 'Delete For Acc Iou Purpose', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-136', url: '/accIouPurpose/list', featureName: 'List For Acc Iou Purpose', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //acc financial year
        new RequestMap(transactionCode: 'ACC-114', url: '/accFinancialYear/show', featureName: 'Show For Acc Financial Year', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-115', url: '/accFinancialYear/list', featureName: 'List For Acc Financial Year', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-116', url: '/accFinancialYear/create', featureName: 'Create For Acc Financial Year', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-117', url: '/accFinancialYear/update', featureName: 'Update For Acc Financial Year', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-118', url: '/accFinancialYear/delete', featureName: 'Delete For Acc Financial Year', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-119', url: '/accFinancialYear/select', featureName: 'Select For Acc Financial Year', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-150', url: '/accFinancialYear/setCurrentFinancialYear', featureName: 'Set Current  Financial Year', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // supplier payable report
        new RequestMap(transactionCode: 'ACC-137', url: '/accReport/showSupplierWisePayable', featureName: 'Show Supplier Wise Payable Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-138', url: '/accReport/listSupplierWisePayable', featureName: 'List Supplier Wise Payable Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-139', url: '/accReport/downloadSupplierWisePayable', featureName: 'Download Supplier Wise Payable Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-172', url: '/accReport/downloadSupplierWisePayableCsv', featureName: 'Download Supplier Wise Payable CSV Report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-258', url: '/accReport/sendMailForSupplierWisePayable', featureName: 'Send mail for Supplier wise payable report', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // Bank Statement
        new RequestMap(transactionCode: 'ACC-166', url: '/accBankStatement/show', featureName: 'Show Bank Statement', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-167', url: '/accBankStatement/uploadBankStatementFile', featureName: 'Upload Bank Statement', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // trialBalance
        new RequestMap(transactionCode: 'ACC-168', url: '/accReport/showCustomGroupBalance', featureName: 'Show Custom Group Balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-169', url: '/accReport/listCustomGroupBalance', featureName: 'List Custom Group Balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-170', url: '/accReport/downloadCustomGroupBalance', featureName: 'Download Custom Group Balance report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-176', url: '/accReport/downloadCustomGroupBalanceCsv', featureName: 'Download Custom Group Balance Csv report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // trialBalance
        new RequestMap(transactionCode: 'ACC-144', url: '/accReport/showBankReconciliationCheque', featureName: 'Show Bank Reconciliation Cheque report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-145', url: '/accReport/listBankReconciliationCheque', featureName: 'List Bank Reconciliation Cheque report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        // bank cheque reconcilliation
        new RequestMap(transactionCode: 'ACC-146', url: '/accReport/downloadBankReconciliationCheque', featureName: 'Download Bank Reconciliation Cheque report', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'ACC-178', url: '/accReport/downloadBankReconciliationChequeCsv', featureName: 'Download Bank Reconciliation Cheque report in CSV Format', configAttribute: 'ROLE_-2', pluginId: 2, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        return true
    }

    public boolean createRequestMapForQs() {
        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
        VALUES(-20,0, '/qs/renderQSMenu','ROLE_-2','QS Module',6,'QS-41',FALSE,FALSE)""")

        //qs Measurement
        new RequestMap(transactionCode: 'QS-1', url: '/qsMeasurement/show', featureName: 'Show Qs Measurement', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-2', url: '/qsMeasurement/create', featureName: 'Create Qs Measurement', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-3', url: '/qsMeasurement/select', featureName: 'Select Qs Measurement', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-4', url: '/qsMeasurement/update', featureName: 'Update Qs Measurement', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-5', url: '/qsMeasurement/delete', featureName: 'Delete Qs Measurement', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-6', url: '/qsMeasurement/list', featureName: 'List Qs Measurement', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-7', url: '/qsMeasurement/showGovt', featureName: 'Show Qs Measurement(Govt)', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-40', url: '/qsMeasurement/getQsStatusForDashBoard', featureName: 'Show Qs Status in Dash Board', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //budget Contract details report
        new RequestMap(transactionCode: 'QS-15', url: '/qsReport/showBudgetContractDetails', featureName: 'Show Budget Contract Details', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-16', url: '/qsReport/listBudgetContractDetails', featureName: 'List Budget Contract Details', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-23', url: '/qsReport/downloadBudgetContractDetails', featureName: 'Download Budget Contract Details', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-42', url: '/qsReport/downloadBudgetContractCsvDetails', featureName: 'Download Budget Contract CSV Details', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        //budget financial summary report
        new RequestMap(transactionCode: 'QS-17', url: '/qsReport/showBudgetFinancialSummary', featureName: 'Show Budget Financial Summary', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-18', url: '/qsReport/listBudgetFinancialSummary', featureName: 'List Budget Financial Summary', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-24', url: '/qsReport/downloadBudgetFinancialSummary', featureName: 'Download Budget Financial Summary', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-43', url: '/qsReport/downloadBudgetFinancialCsvSummary', featureName: 'Download Budget Financial Summary CSV Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //get Budget for QS
        new RequestMap(transactionCode: 'QS-19', url: '/qsMeasurement/getBudgetForQS', featureName: 'Get budget for QS', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //QS Measurement report
        new RequestMap(transactionCode: 'QS-13', url: '/qsReport/showQsMeasurementRpt', featureName: 'Show Qs-Measurement Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-14', url: '/qsReport/listQsMeasurementRpt', featureName: 'List Qs-Measurement Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-33', url: '/qsReport/downloadQsMeasurementRpt', featureName: 'Download Qs-Measurement Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-45', url: '/qsReport/downloadQsMeasurementCsvRpt', featureName: 'Download Qs-Measurement CSV Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //budget wise qs report
        new RequestMap(transactionCode: 'QS-20', url: '/qsReport/showBudgetWiseQs', featureName: 'Show Budget Wise Qs Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-21', url: '/qsReport/listBudgetWiseQs', featureName: 'List Budget Wise Qs Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-22', url: '/qsReport/downloadBudgetWiseQs', featureName: 'Download Budget Wise Qs Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-46', url: '/qsReport/downloadBudgetWiseQsCsv', featureName: 'Download Budget Wise Qs Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //Combined QS Measurement Report
        new RequestMap(transactionCode: 'QS-30', url: '/qsReport/showCombinedQSM', featureName: 'Show Combined QS Measurement Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-31', url: '/qsReport/listCombinedQSM', featureName: 'List Combined QS Measurement Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-32', url: '/qsReport/downloadCombinedQSM', featureName: 'Download Combined QS Measurement Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'QS-44', url: '/qsReport/downloadCombinedQSMCsv', featureName: 'Download Combined QS Measurement CSV Report', configAttribute: 'ROLE_-2', pluginId: 6, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        return true
    }

    public boolean createRequestMapForInventory() {
        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
        VALUES(-17,0, '/inventory/renderInventoryMenu','ROLE_-2','Inventory Module',4,'INV-150',FALSE,FALSE)""")

        //For Dash Board
        new RequestMap(transactionCode: 'INV-151', url: '/invInventoryTransaction/listOfUnApprovedConsumption', featureName: 'List For All Unapproved Consumption To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-186', url: '/invInventoryTransaction/listOfUnApprovedInFromSupplier', featureName: 'List For All Unapproved In from Supplier To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-187', url: '/invInventoryTransaction/listOfUnApprovedInventoryOut', featureName: 'List For All Unapproved Inventory Out To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-188', url: '/invInventoryTransaction/listOfUnApprovedInFromInventory', featureName: 'List For All Unapproved Inventory In From Inventory To Show On Dash Board', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //For inv inventory
        new RequestMap(transactionCode: 'INV-1', url: '/invInventory/show', featureName: 'Show inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-2', url: '/invInventory/select', featureName: 'select inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-3', url: '/invInventory/create', featureName: 'Create inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-4', url: '/invInventory/update', featureName: 'Update inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-5', url: '/invInventory/delete', featureName: 'Delete inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-6', url: '/invInventory/list', featureName: 'List inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //-------------- > For Inventory Transaction < --------------\\

        //for sending mail for all pending Inventory Transaction
        new RequestMap(transactionCode: 'INV-231', url: '/invInventoryTransaction/sendMailForInventoryTransaction', featureName: 'Send mail for all pending inventory transaction', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();

        //for Inventory Transaction-in (from supplier)
        new RequestMap(transactionCode: 'INV-32', url: '/invInventoryTransaction/showInventoryInFromSupplier', featureName: 'Show Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-33', url: '/invInventoryTransaction/createInventoryInFromSupplier', featureName: 'Create Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-34', url: '/invInventoryTransaction/selectInventoryInFromSupplier', featureName: 'Select Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-35', url: '/invInventoryTransaction/updateInventoryInFromSupplier', featureName: 'Update Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-36', url: '/invInventoryTransaction/deleteInventoryInFromSupplier', featureName: 'Delete Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-37', url: '/invInventoryTransaction/listInventoryInFromSupplier', featureName: 'List Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'INV-38', url: '/invInventoryTransaction/listInventoryByType', featureName: 'Get Inventory List By Type', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-183', url: '/invInventoryTransaction/listInventoryByTypeAndProject', featureName: 'Get Inventory List By Type And Project', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-217', url: '/invInventoryTransaction/listFixedAssetByItemAndProject', featureName: 'Get Fixed Asset List By Item And Project', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-39', url: '/invInventoryTransaction/listPOBySupplier', featureName: 'Get PO List Of Supplier', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //for Inventory Transaction-in (from Inventory)
        new RequestMap(transactionCode: 'INV-40', url: '/invInventoryTransaction/showInventoryInFromInventory', featureName: 'Show Inventory-In From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-41', url: '/invInventoryTransaction/createInventoryInFromInventory', featureName: 'Create Inventory-In From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-42', url: '/invInventoryTransaction/selectInventoryInFromInventory', featureName: 'Select Inventory-In From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-43', url: '/invInventoryTransaction/updateInventoryInFromInventory', featureName: 'Update Inventory-In From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-44', url: '/invInventoryTransaction/deleteInventoryInFromInventory', featureName: 'Delete Inventory-In From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-45', url: '/invInventoryTransaction/listInventoryInFromInventory', featureName: 'List Inventory-In From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'INV-46', url: '/invInventoryTransaction/listInventoryOfTransactionOut', featureName: 'Get Inventory Transaction Out List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-47', url: '/invInventoryTransaction/listInvTransaction', featureName: 'Get Inventory Transaction List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //for Inventory Consumption
        new RequestMap(transactionCode: 'INV-48', url: '/invInventoryTransaction/showInventoryConsumption', featureName: 'Show Inventory-Consumption Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-49', url: '/invInventoryTransaction/createInventoryConsumption', featureName: 'Create Inventory-Consumption Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-50', url: '/invInventoryTransaction/selectInventoryConsumption', featureName: 'Select Inventory-Consumption Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-51', url: '/invInventoryTransaction/updateInventoryConsumption', featureName: 'Update Inventory-Consumption Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-52', url: '/invInventoryTransaction/deleteInventoryConsumption', featureName: 'Delete Inventory-Consumption Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-53', url: '/invInventoryTransaction/listInventoryConsumption', featureName: 'List Inventory-Consumption Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //for Inventory Out
        new RequestMap(transactionCode: 'INV-54', url: '/invInventoryTransaction/showInventoryOut', featureName: 'Show Inventory-Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-55', url: '/invInventoryTransaction/createInventoryOut', featureName: 'Create Inventory-Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-56', url: '/invInventoryTransaction/selectInventoryOut', featureName: 'Select Inventory-Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-57', url: '/invInventoryTransaction/updateInventoryOut', featureName: 'Update Inventory-Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-58', url: '/invInventoryTransaction/deleteInventoryOut', featureName: 'Delete Inventory-Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-59', url: '/invInventoryTransaction/listInventoryOut', featureName: 'List Inventory-Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //For Inventory -Production
        new RequestMap(transactionCode: 'INV-66', url: '/invInventoryTransaction/showInvProductionWithConsumption', featureName: 'Show Inventory production', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-67', url: '/invInventoryTransaction/createInvProductionWithConsumption', featureName: 'Create Inventory production', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-68', url: '/invInventoryTransaction/updateInvProductionWithConsumption', featureName: 'Update Inventory production', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-69', url: '/invInventoryTransaction/deleteInvProductionWithConsumption', featureName: 'Delete Inventory production', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-70', url: '/invInventoryTransaction/selectInvProductionWithConsumption', featureName: 'Select Inventory production', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-71', url: '/invInventoryTransaction/listInvProductionWithConsumption', featureName: 'List Inventory production', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'INV-179', url: '/invInventoryTransaction/approveInvProdWithConsumption', featureName: 'Approve Inventory Production With Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-180', url: '/invInventoryTransaction/showApprovedProdWithConsump', featureName: 'Show For Approved Prod.With Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-181', url: '/invInventoryTransaction/listApprovedProdWithConsump', featureName: 'List For Approved Prod.With Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-182', url: '/invInventoryTransaction/adjustInvProductionWithConsumption', featureName: 'Adjust Inv.Production With Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-184', url: '/invInventoryTransaction/reverseAdjust', featureName: 'Reverse Adjustment for Inv.Production With Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //For Production Line Item
        new RequestMap(transactionCode: 'INV-72', url: '/invProductionLineItem/show', featureName: 'Show Production Line Item', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-73', url: '/invProductionLineItem/create', featureName: 'Create Production Line Item', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-74', url: '/invProductionLineItem/select', featureName: 'Select Production Line Item', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-75', url: '/invProductionLineItem/update', featureName: 'Update Production Line Item', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-76', url: '/invProductionLineItem/delete', featureName: 'Delete Production Line Item', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-77', url: '/invProductionLineItem/list', featureName: 'List Production Line Item', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //For Inventory Transaction Details
        //For Inventory-out-details
        new RequestMap(transactionCode: 'INV-78', url: '/invInventoryTransactionDetails/showUnApprovedInventoryOutDetails', featureName: 'Show Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-79', url: '/invInventoryTransactionDetails/createInventoryOutDetails', featureName: 'Create Unapproved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-80', url: '/invInventoryTransactionDetails/selectInventoryOutDetails', featureName: 'Select Unapproved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-81', url: '/invInventoryTransactionDetails/updateInventoryOutDetails', featureName: 'Update Unapproved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-82', url: '/invInventoryTransactionDetails/deleteInventoryOutDetails', featureName: 'Delete Unapproved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-83', url: '/invInventoryTransactionDetails/listUnApprovedInventoryOutDetails', featureName: 'List Unapproved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'INV-171', url: '/invInventoryTransactionDetails/approveInventoryOutDetails', featureName: ' Approved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-172', url: '/invInventoryTransactionDetails/showApprovedInventoryOutDetails', featureName: 'Show Approved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-173', url: '/invInventoryTransactionDetails/listApprovedInventoryOutDetails', featureName: 'List Approved Inventory-Out Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-174', url: '/invInventoryTransactionDetails/adjustInvOut', featureName: 'Adjustment For Inv. Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-192', url: '/invInventoryTransactionDetails/reverseAdjustInvOut', featureName: 'Reverse Adjustment For Inventory Out', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //For Inventory-in-details (from supplier)
        new RequestMap(transactionCode: 'INV-90', url: '/invInventoryTransactionDetails/showUnapprovedInvInFromSupplier', featureName: 'Show Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-91', url: '/invInventoryTransactionDetails/createInventoryInDetailsFromSupplier', featureName: 'Create Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-92', url: '/invInventoryTransactionDetails/selectInventoryInDetailsFromSupplier', featureName: 'Select Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-93', url: '/invInventoryTransactionDetails/updateInventoryInDetailsFromSupplier', featureName: 'Update Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-94', url: '/invInventoryTransactionDetails/deleteInventoryInDetailsFromSupplier', featureName: 'Delete Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-95', url: '/invInventoryTransactionDetails/listUnapprovedInvInFromSupplier', featureName: 'List Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'INV-164', url: '/invInventoryTransactionDetails/approveInventoryInDetailsFromSupplier', featureName: 'Approve Inventory In Details From Supplier', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-165', url: '/invInventoryTransactionDetails/showApprovedInvInFromSupplier', featureName: 'Show approved Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-166', url: '/invInventoryTransactionDetails/listApprovedInvInFromSupplier', featureName: 'List approved Inventory-In Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-167', url: '/invInventoryTransactionDetails/adjustInvInFromSupplier', featureName: 'Adjustment For Inv. In From Supplier', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-190', url: '/invInventoryTransactionDetails/reverseAdjustInvInFromSupplier', featureName: 'Reverse Adjustment For Inv. In From Supplier', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //For Inventory-in-details (from Inventory)
        new RequestMap(transactionCode: 'INV-96', url: '/invInventoryTransactionDetails/showUnapprovedInvInFromInventory', featureName: 'Show Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-97', url: '/invInventoryTransactionDetails/createInventoryInDetailsFromInventory', featureName: 'Create Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-98', url: '/invInventoryTransactionDetails/selectInventoryInDetailsFromInventory', featureName: 'Select Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-99', url: '/invInventoryTransactionDetails/updateInventoryInDetailsFromInventory', featureName: 'Update Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-100', url: '/invInventoryTransactionDetails/deleteInventoryInDetailsFromInventory', featureName: 'Delete Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-101', url: '/invInventoryTransactionDetails/listUnapprovedInvInFromInventory', featureName: 'List Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'INV-168', url: '/invInventoryTransactionDetails/approveInventoryInDetailsFromInventory', featureName: 'Approve Inventory In Details From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-169', url: '/invInventoryTransactionDetails/showApprovedInvInFromInventory', featureName: 'Show approved Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-170', url: '/invInventoryTransactionDetails/listApprovedInvInFromInventory', featureName: 'List approved Inventory-In From Inventory Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-191', url: '/invInventoryTransactionDetails/reverseAdjustInvInFromInventory', featureName: 'Reverse Adjustment For Inv. In From Inventory', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //For Inventory consumption Details
        new RequestMap(transactionCode: 'INV-102', url: '/invInventoryTransactionDetails/showUnApprovedInventoryConsumptionDetails', featureName: 'Show Un approved Inv.Consumption Details Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-103', url: '/invInventoryTransactionDetails/createInventoryConsumptionDetails', featureName: 'Create Inventory-Consumption Details Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-104', url: '/invInventoryTransactionDetails/selectInventoryConsumptionDetails', featureName: 'Select Inventory-Consumption Details Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-105', url: '/invInventoryTransactionDetails/updateInventoryConsumptionDetails', featureName: 'Update Inventory-Consumption Details Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-106', url: '/invInventoryTransactionDetails/deleteInventoryConsumptionDetails', featureName: 'Delete Inventory-Consumption Details Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-107', url: '/invInventoryTransactionDetails/listUnApprovedInventoryConsumptionDetails', featureName: 'List Un approved Inv.Consumption Details Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'INV-108', url: '/invInventoryTransactionDetails/approveInventoryConsumptionDetails', featureName: 'Approve Inventory-Consumption Details Transaction', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-176', url: '/invInventoryTransactionDetails/showApprovedInventoryConsumptionDetails', featureName: 'Show Approved Inventory-Consumption Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-177', url: '/invInventoryTransactionDetails/listApprovedInventoryConsumptionDetails', featureName: 'List Approved Inventory-Consumption Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-178', url: '/invInventoryTransactionDetails/adjustInvConsumption', featureName: 'Adjust For Inv. Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-185', url: '/invInventoryTransactionDetails/reverseAdjustInvConsumption', featureName: 'Reverse adjust For Inv. Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'INV-158', url: '/invInventoryTransactionDetails/listFixedAssetByInventoryId', featureName: 'Get Fixed Asset list by inventory ID', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-159', url: '/invInventoryTransactionDetails/listFixedAssetByInventoryIdAndItemId', featureName: 'Get Fixed Asset list by inventory ID and item ID', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // refresh dropDown using remote url
        new RequestMap(transactionCode: 'INV-233', url: '/invInventoryTransactionDetails/dropDownInventoryItemConsumptionReload', featureName: 'Reload inventory item consumption dropDown', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-234', url: '/invInventoryTransactionDetails/dropDownInventoryItemInFromInventoryReload', featureName: 'Reload inventory item in from inventory dropDown', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-235', url: '/invInventoryTransactionDetails/dropDownInventoryItemInFromSupplierReload', featureName: 'Reload inventory item in from supplier dropDown', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-236', url: '/invInventoryTransactionDetails/dropDownInventoryItemOutReload', featureName: 'Reload inventory item out dropDown', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // For Get All Supplier List
        new RequestMap(transactionCode: 'INV-111', url: '/supplier/getAllSupplierList', featureName: 'Get Supplier List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //For Production Line Item Details
        new RequestMap(transactionCode: 'INV-112', url: '/invProductionDetails/show', featureName: 'Show Production Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-113', url: '/invProductionDetails/create', featureName: 'Create Production Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-114', url: '/invProductionDetails/select', featureName: 'Select Production Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-115', url: '/invProductionDetails/update', featureName: 'Update Production Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-116', url: '/invProductionDetails/delete', featureName: 'Delete Production Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-117', url: '/invProductionDetails/list', featureName: 'List Production Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'INV-118', url: '/invProductionDetails/getBothMaterials', featureName: 'Get Both Materials for Production Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // Recalculate valuation
        new RequestMap(transactionCode: 'INV-148', url: '/invInventoryTransaction/reCalculateValuation', featureName: 'Re-Calculate ALL valuation', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-203', url: '/invInventoryTransaction/showReCalculateValuation', featureName: 'Show Re-Calculate-Valuation', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();

        // Production Overhead Cost modification
        new RequestMap(transactionCode: 'INV-210', url: '/invInventoryTransactionDetails/showInvModifyOverheadCost', featureName: 'Show Production Overhead Cost for Modification', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-211', url: '/invInventoryTransactionDetails/searchInvModifyOverheadCost', featureName: 'Search Production Overhead Cost for Modification', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-212', url: '/invInventoryTransactionDetails/updateInvModifyOverheadCost', featureName: 'Modify Overhead Cost in Production', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-213', url: '/invInventoryTransactionDetails/getInvProdFinishedMaterialByLineItemId', featureName: 'Get Finished Material by Prodution Line Item', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //**********************************
        //Inventory Report
        //*******************************

        //for inventory invoice
        new RequestMap(transactionCode: 'INV-119', url: '/invReport/showInvoice', featureName: 'Show Invoice of Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-120', url: '/invReport/searchInvoice', featureName: 'Search Invoice of Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-121', url: '/invReport/downloadInvoice', featureName: 'Download Invoice of Inventory-In', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        //for inventory stock
        new RequestMap(transactionCode: 'INV-122', url: '/invReport/inventoryStock', featureName: 'Show Inventory Stock', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-124', url: '/invReport/listInventoryStock', featureName: 'list Inventory Stock', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-125', url: '/invReport/downloadInventoryStock', featureName: 'Download Inventory Stock', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-223', url: '/invReport/downloadInventoryStockCsv', featureName: 'Download Inventory Stock Csv Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        //for item stock
        new RequestMap(transactionCode: 'INV-126', url: '/invReport/showItemStock', featureName: 'Show Item Stock List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-127', url: '/invReport/listItemStock', featureName: 'List Item Stock', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-128', url: '/invReport/getStockDetailsListByItemId', featureName: 'Get Item Stock Details', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //For Inventory Status
        new RequestMap(transactionCode: 'INV-160', url: '/invReport/showInventoryStatusWithQuantityAndValue', featureName: 'Show Inventory Status with quantity And Value Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-161', url: '/invReport/listInventoryStatusWithQuantityAndValue', featureName: 'List Inventory Status with quantity And Value Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-162', url: '/invReport/downloadInventoryStatusWithQuantityAndValue', featureName: 'Download Inventory Status with quantity And Value Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-224', url: '/invReport/downloadInventoryStatusWithQuantityAndValueCsv', featureName: 'Download Inventory Status with quantity And Value Csv Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // for Inventory valuation
        new RequestMap(transactionCode: 'INV-129', url: '/invReport/showInventoryValuation', featureName: 'Show Inventory Valuation', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-130', url: '/invReport/searchInventoryValuation', featureName: 'Search Inventory Valuation', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-131', url: '/invReport/downloadInventoryValuation', featureName: 'Download Inventory Valuation', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-227', url: '/invReport/downloadInventoryValuationCsv', featureName: 'Download Inventory Valuation', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'INV-132', url: '/invReport/showInventoryTransactionList', featureName: 'Show all Inventory Transaction List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-133', url: '/invReport/searchInventoryTransactionList', featureName: 'Search all Inventory Transaction List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-134', url: '/invReport/downloadInventoryTransactionList', featureName: 'Download all Inventory Transaction List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-226', url: '/invReport/downloadInventoryTransactionListCsv', featureName: 'Download all Inventory Transaction List Csv Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // for Inventory summary
        new RequestMap(transactionCode: 'INV-138', url: '/invReport/showInventorySummary', featureName: 'Show Inventory Summary', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-139', url: '/invReport/getInventorySummary', featureName: 'Search Inventory Summary', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-140', url: '/invReport/downloadInventorySummary', featureName: 'Download Inventory Summary', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-222', url: '/invReport/downloadInventorySummaryCsv', featureName: 'Download Inventory Csv Summary', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'INV-141', url: '/invReport/showConsumedItemList', featureName: 'Show Consumed Item List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-142', url: '/invReport/listBudgetOfConsumption', featureName: 'Budget List of Consumption', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-143', url: '/invReport/getConsumedItemList', featureName: 'Get Consumed Item List', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-220', url: '/invReport/downloadForConsumedItemList', featureName: 'Download Consumed Item List Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'INV-144', url: '/invInventoryTransaction/listAllInventoryByType', featureName: 'Get All Inventory List by Inventory type', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-149', url: '/invInventoryTransaction/listInventoryIsFactoryByType', featureName: 'Get Inventory List of is factory by Inventory type', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'INV-152', url: '/invReport/showItemReceivedStock', featureName: 'Show Item Received Stock Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-153', url: '/invReport/listItemReceivedStock', featureName: 'List Item Received Stock Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-232', url: '/invReport/downloadItemReceivedGroupBySupplier', featureName: 'Download Item Received Stock Group By Supplier Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-154', url: '/invReport/downloadItemReceivedStock', featureName: 'Download Item Received Stock Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-228', url: '/invReport/downloadItemReceivedStockCsv', featureName: 'Download Item Received Stock Csv Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'INV-155', url: '/invReport/showItemWiseBudgetSummary', featureName: 'Show Item Wise Budget Summary Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-156', url: '/invReport/listItemWiseBudgetSummary', featureName: 'List Item Wise Budget Summary Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-157', url: '/invReport/downloadItemWiseBudgetSummary', featureName: 'Download Item Wise Budget Summary Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-229', url: '/invReport/downloadItemWiseBudgetSummaryCsv', featureName: 'Download Item Wise Budget Summary Csv Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //For production Report
        new RequestMap(transactionCode: 'INV-193', url: '/invReport/showInventoryProductionRpt', featureName: 'Show Inventory Production Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-194', url: '/invReport/searchInventoryProductionRpt', featureName: 'Search Inventory Production Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-195', url: '/invReport/downloadInventoryProductionRpt', featureName: 'Download Inventory Production Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // Supplier Challlan Report
        new RequestMap(transactionCode: 'INV-196', url: '/invReport/showSupplierChalan', featureName: 'Show Supplier Chalan Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-197', url: '/invReport/listSupplierChalan', featureName: 'List Supplier Chalan Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // PO Item Received Report
        new RequestMap(transactionCode: 'INV-198', url: '/invReport/showPoItemReceived', featureName: 'Show PO Item Received Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-199', url: '/invReport/listPoItemReceived', featureName: 'List PO Item Received Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-200', url: '/invReport/downloadPoItemReceived', featureName: 'Download PO Item Received Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-230', url: '/invReport/downloadPoItemReceivedCsv', featureName: 'Download PO Item Received Csv Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // Acknowledge & download supplier chalan report
        new RequestMap(transactionCode: 'INV-201', url: '/invInventoryTransactionDetails/acknowledgeInvoiceFromSupplier', featureName: 'Acknowledge Chalan', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-202', url: '/invReport/downloadSupplierChalanReport', featureName: 'Download Chalan Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-221', url: '/invReport/downloadSupplierChalanCsvReport', featureName: 'Download Chalan CSV Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // inventory status with value report
        new RequestMap(transactionCode: 'INV-204', url: '/invReport/showInventoryStatusWithValue', featureName: 'Show Inventory Status With Value', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-205', url: '/invReport/listInventoryStatusWithValue', featureName: 'List Inventory Status With Value', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-206', url: '/invReport/downloadInventoryStatusWithValue', featureName: 'Download Inventory Status With Value', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-218', url: '/invReport/downloadInventoryStatusWithValueCsv', featureName: 'Download Inventory Status With Value CSV Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // inventory status report with Quantity
        new RequestMap(transactionCode: 'INV-207', url: '/invReport/showInventoryStatusWithQuantity', featureName: 'Show Inventory Status With Quantity', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-208', url: '/invReport/listInventoryStatusWithQuantity', featureName: 'List Inventory Status With Quantity', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-209', url: '/invReport/downloadInventoryStatusWithQuantity', featureName: 'Download Inventory Status With Quantity', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-219', url: '/invReport/downloadInventoryStatusWithQuantityCsv', featureName: 'Download Inventory Status With Quantity CSV Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // Item-Reconciliation Report
        new RequestMap(transactionCode: 'INV-214', url: '/invReport/showForItemReconciliation', featureName: 'Show Item Reconciliation Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-215', url: '/invReport/listForItemReconciliation', featureName: 'List Item Reconciliation Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-216', url: '/invReport/downloadForItemReconciliation', featureName: 'Download Item Reconciliation Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'INV-225', url: '/invReport/downloadForItemReconciliationCsv', featureName: 'Download Item Reconciliation Csv Report', configAttribute: 'ROLE_-2', pluginId: 4, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        return true
    }

    public boolean createRequestMapForFixedAsset() {
        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
        VALUES(-21,0, '/fixedAsset/renderFixedAssetMenu','ROLE_-2','Fixed Asset Module',7,'FA-47',FALSE,FALSE)""")

        //for Fixed Asset Details
        new RequestMap(transactionCode: 'FA-1', url: '/fxdFixedAssetDetails/show', featureName: 'Show fixed asset details', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-2', url: '/fxdFixedAssetDetails/create', featureName: 'Create fixed asset details', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-3', url: '/fxdFixedAssetDetails/delete', featureName: 'Delete fixed asset details', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-4', url: '/fxdFixedAssetDetails/list', featureName: 'List fixed asset details', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-5', url: '/fxdFixedAssetDetails/select', featureName: 'Select fixed asset details', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-6', url: '/fxdFixedAssetDetails/update', featureName: 'Update fixed asset details', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-7', url: '/fxdFixedAssetDetails/getFixedAssetList', featureName: 'Get fixed asset List Of PO and Inventory', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //For Fixed Asset Trace
        new RequestMap(transactionCode: 'FA-16', url: '/fxdFixedAssetTrace/show', featureName: 'Show Fixed Asset Trace', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-17', url: '/fxdFixedAssetTrace/list', featureName: 'List Fixed Asset Trace', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-18', url: '/fxdFixedAssetTrace/create', featureName: 'Create Fixed Asset Trace', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-19', url: '/fxdFixedAssetTrace/getItemList', featureName: 'Get Item List of Fixed Asset', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //for Maintenance Type
        new RequestMap(transactionCode: 'FA-24', url: '/fxdMaintenanceType/show', featureName: 'Show fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-25', url: '/fxdMaintenanceType/create', featureName: 'Create fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-26', url: '/fxdMaintenanceType/delete', featureName: 'Delete fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-27', url: '/fxdMaintenanceType/list', featureName: 'List fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-28', url: '/fxdMaintenanceType/select', featureName: 'Select fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-29', url: '/fxdMaintenanceType/update', featureName: 'Update fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //for Category Maintenance Type
        new RequestMap(transactionCode: 'FA-30', url: '/fxdCategoryMaintenanceType/show', featureName: 'Show fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-31', url: '/fxdCategoryMaintenanceType/create', featureName: 'Create fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-32', url: '/fxdCategoryMaintenanceType/delete', featureName: 'Delete fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-33', url: '/fxdCategoryMaintenanceType/list', featureName: 'List fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-34', url: '/fxdCategoryMaintenanceType/select', featureName: 'Select fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-35', url: '/fxdCategoryMaintenanceType/update', featureName: 'Update fixed maintenance type', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-54', url: '/fxdCategoryMaintenanceType/dropDownFxdMaintenanceTypeReload', featureName: 'Reload fixed maintenance type dropDown', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //for Maintenance
        new RequestMap(transactionCode: 'FA-40', url: '/fxdMaintenance/show', featureName: 'Show fixed asset maintenance', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-41', url: '/fxdMaintenance/create', featureName: 'Create fixed asset maintenance', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-42', url: '/fxdMaintenance/delete', featureName: 'Delete fixed asset maintenance', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-43', url: '/fxdMaintenance/list', featureName: 'List fixed asset maintenance', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-44', url: '/fxdMaintenance/select', featureName: 'Select fixed asset maintenance', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-45', url: '/fxdMaintenance/update', featureName: 'Update fixed asset maintenance', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-46', url: '/fxdMaintenance/getMaintenanceTypeAndModelListByItemId', featureName: 'Get Maintenance Type and Model List by item', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        //** ****************************
        //Fixed Asset Report
        //*******************************

        new RequestMap(transactionCode: 'FA-9', url: '/fixedAssetReport/showConsumptionAgainstAsset', featureName: 'Show Consumption Against Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-10', url: '/fixedAssetReport/listConsumptionAgainstAsset', featureName: 'List Consumption Against Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-11', url: '/fixedAssetReport/getConsumptionAgainstAssetDetails', featureName: 'Get Consumption Against fixed Asset Details', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-12', url: '/fixedAssetReport/downloadConsumptionAgainstAsset', featureName: 'Download Consumption Against Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-53', url: '/fixedAssetReport/downloadConsumptionAgainstAssetDetailsCsv', featureName: 'Download Consumption Against Fixed Asset CSV Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'FA-13', url: '/fixedAssetReport/showPendingFixedAsset', featureName: 'Show Pending Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-14', url: '/fixedAssetReport/listPendingFixedAsset', featureName: 'List Pending Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-15', url: '/fixedAssetReport/downloadPendingFixedAsset', featureName: 'Download Pending Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'FA-20', url: '/fixedAssetReport/showCurrentFixedAsset', featureName: 'Show Current Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-21', url: '/fixedAssetReport/listCurrentFixedAsset', featureName: 'List Current Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-22', url: '/fixedAssetReport/searchCurrentFixedAsset', featureName: 'Search Current Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-23', url: '/fixedAssetReport/downloadCurrentFixedAsset', featureName: 'Download Current Fixed Asset Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-51', url: '/fixedAssetReport/downloadCurrentFixedAssetCsv', featureName: 'Download Current Fixed Asset Csv Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        new RequestMap(transactionCode: 'FA-48', url: '/fixedAssetReport/showConsumptionAgainstAssetDetails', featureName: 'Show Consumption Against Asset Details Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-49', url: '/fixedAssetReport/listConsumptionAgainstAssetDetails', featureName: 'List Consumption Against Asset Details Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();
        new RequestMap(transactionCode: 'FA-50', url: '/fixedAssetReport/downloadConsumptionAgainstAssetDetails', featureName: 'Download Consumption Against Asset Details Report', configAttribute: 'ROLE_-2', pluginId: 7, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        return true
    }

    public boolean createRequestMapForExchangeHouse() {
        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
        VALUES(-25,0,'/exhExchangeHouse/renderExchangeHouseMenu','ROLE_-2','Exchange House Module',9,'EXH-160',FALSE,FALSE)""")

        // Customer
        new RequestMap(transactionCode: 'EXH-1', url: '/exhCustomer/show', featureName: 'show customer for cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-2', url: '/exhCustomer/showForAdmin', featureName: 'Show customer for admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-133', url: '/exhCustomer/showForAgent', featureName: 'show customer for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-3', url: '/exhCustomer/create', featureName: 'Create customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-4', url: '/exhCustomer/update', featureName: 'Update customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-5', url: '/exhCustomer/edit', featureName: 'Select customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-6', url: '/exhCustomer/list', featureName: 'List customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-136', url: '/exhCustomer/listForAgent', featureName: 'List customer for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-7', url: '/exhCustomer/delete', featureName: 'Delete customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-8', url: '/exhCustomer/showCustomerUser', featureName: 'Show for customer-user', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-9', url: '/exhCustomer/searchCustomerUser', featureName: 'Search for customer-user', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-10', url: '/exhCustomer/createCustomerUser', featureName: 'Create for customer-user', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-151', url: '/exhCustomer/showForCustomerByNameAndCode', featureName: 'Show for customer search', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-152', url: '/exhCustomer/searchForCustomerByNameAndCode', featureName: 'Search customer by name,code etc.', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-257', url: '/exhCustomer/blockExhCustomer', featureName: 'Block exchange house customer.', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-259', url: '/exhCustomer/unblockExhCustomer', featureName: 'Block exchange house customer.', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-260', url: '/exhCustomer/reloadCustomerDetails', featureName: 'Reload Customer Details', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        /*Customer Entity Note*/
        new RequestMap(transactionCode: 'EXH-213', url: '/exhCustomer/showEntityNote', featureName: 'Show Customer Entity Note', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-214', url: '/exhCustomer/createEntityNote', featureName: 'Create Customer Entity Note', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-215', url: '/exhCustomer/listEntityNote', featureName: 'List Customer Entity Note', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-216', url: '/exhCustomer/deleteEntityNote', featureName: 'Delete Customer Entity Note', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-217', url: '/exhCustomer/updateEntityNote', featureName: 'Update Customer Entity Note', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-218', url: '/exhCustomer/editEntityNote', featureName: 'Edit Customer Entity Note', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        /*Customer Content/Attachment*/
        new RequestMap(transactionCode: 'EXH-219', url: '/exhCustomer/showEntityContent', featureName: 'Show Customer Attachment', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-220', url: '/exhCustomer/createEntityContent', featureName: 'Create Customer Attachment', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-221', url: '/exhCustomer/listEntityContent', featureName: 'List Customer Attachment', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-222', url: '/exhCustomer/deleteEntityContent', featureName: 'Delete Customer Attachment', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-223', url: '/exhCustomer/updateEntityContent', featureName: 'Update Customer Attachment', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-224', url: '/exhCustomer/selectEntityContent', featureName: 'Select Customer Attachment', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-225', url: '/exhCustomer/downloadEntityContent', featureName: 'Download Customer Attachment', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // Customer Registration
        new RequestMap(transactionCode: 'EXH-159', url: '/exhCustomer/registration', featureName: 'Show Customer Registration.', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-161', url: '/exhCustomer/signup', featureName: 'Customer Registration.', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-162', url: '/exhCustomer/activation', featureName: 'Customer Activation', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-186', url: '/exhCustomer/displayPhotoIdImage', featureName: 'Display Photo Id Image of Customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // Beneficiary
        new RequestMap(transactionCode: 'EXH-11', url: '/exhBeneficiary/show', featureName: 'Show Beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-12', url: '/exhBeneficiary/create', featureName: 'Create Beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-233', url: '/exhBeneficiary/createForCustomer', featureName: 'Create Beneficiary for Customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-13', url: '/exhBeneficiary/update', featureName: 'Update Beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-234', url: '/exhBeneficiary/updateForCustomer', featureName: 'Update Beneficiary for Customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-14', url: '/exhBeneficiary/edit', featureName: 'Select Beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-15', url: '/exhBeneficiary/list', featureName: 'List Beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-207', url: '/exhBeneficiary/listLinkedBeneficiary', featureName: 'List of Linked Beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-16', url: '/exhBeneficiary/showNewForCustomer', featureName: 'Show new beneficiary for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-232', url: '/exhBeneficiary/showApprovedForCustomer', featureName: 'Show approved beneficiary for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-17', url: '/exhBeneficiary/detailsForCustomer', featureName: 'Show beneficiary details for customer login', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-18', url: '/exhBeneficiary/listForCustomer', featureName: 'List beneficiary details for customer login', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-19', url: '/exhBeneficiary/selectForCustomer', featureName: 'Select beneficiary details for customer login', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-255', url: '/exhBeneficiary/approveBeneficiary', featureName: 'Approve beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // Customer  Beneficiary Mapping
        new RequestMap(transactionCode: 'EXH-208', url: '/exhCustomerBeneficiary/create', featureName: 'Map an existing beneficiary with customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // Task
        new RequestMap(transactionCode: 'EXH-170', url: '/exhTask/showExhTaskForAdmin', featureName: 'Show Exh Task for Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-171', url: '/exhTask/showAgentTaskForAdmin', featureName: 'Show Agent Task for Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-172', url: '/exhTask/showCustomerTaskForAdmin', featureName: 'Show Customer Task for Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-176', url: '/exhTask/showExhTaskForCashier', featureName: 'Show Exh Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-177', url: '/exhTask/showAgentTaskForCashier', featureName: 'Show Agent Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-178', url: '/exhTask/showCustomerTaskForCashier', featureName: 'Show Customer Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-21', url: '/exhTask/create', featureName: 'Create Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-22', url: '/exhTask/update', featureName: 'Update Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-23', url: '/exhTask/edit', featureName: 'Select Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-24', url: '/exhTask/list', featureName: 'List Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-173', url: '/exhTask/listExhTaskForAdmin', featureName: 'List Exh Task for Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-174', url: '/exhTask/listAgentTaskForAdmin', featureName: 'List Agent Task for Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-175', url: '/exhTask/listCustomerTaskForAdmin', featureName: 'List Customer Task for Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-179', url: '/exhTask/listExhTaskForCashier', featureName: 'List Exh Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-180', url: '/exhTask/listAgentTaskForCashier', featureName: 'List Agent Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-181', url: '/exhTask/listCustomerTaskForCashier', featureName: 'List Customer Task for Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-182', url: '/exhTask/approveTaskForCashier', featureName: 'Approve Task For Cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-26', url: '/exhTask/showForTaskSearch', featureName: 'Show for task search', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-165', url: '/exhTask/showForTaskSearchForAgent', featureName: 'Show for task search(agent)', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-166', url: '/exhTask/searchTaskWithRefOrPinForAgent', featureName: 'Show for task details(agent)', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-196', url: '/exhTask/searchTaskWithRefOrPinForCustomer', featureName: 'Show for task details(Customer)', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-134', url: '/exhTask/showForAgent', featureName: 'Show task for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-135', url: '/exhTask/listForAgent', featureName: 'List task for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-138', url: '/exhTask/editForAgent', featureName: 'Edit task for agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'EXH-185', url: '/exhTask/calculateFeesAndCommission', featureName: 'Calculate fees and commission', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'EXH-137', url: '/exhTask/sendToExchangeHouse', featureName: 'Send task to exchange house', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-168', url: '/exhTask/sendToExhForCustomer', featureName: 'Send task to exchange house for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-261', url: '/exhTask/reloadShowTaskDetailsTagLib', featureName: 'Reload task details Taglib', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //task for admin
        new RequestMap(transactionCode: 'EXH-29', url: '/exhTask/sendToBank', featureName: 'Send task to Bank', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-30', url: '/exhTask/cancelSpecificTask', featureName: 'Cancel task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-31', url: '/exhTask/showTaskDetails', featureName: 'Show task details', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-32', url: '/exhTask/showTaskDetailsForAdmin', featureName: 'Show task details for admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-33', url: '/exhTask/searchTaskWithRefOrPin', featureName: 'Search task with ref or pin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-34', url: '/exhTask/showForCustomer', featureName: 'Show task for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-192', url: '/exhTask/showUnApprovedTaskForCustomer', featureName: 'Show UnApproved Customer Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-193', url: '/exhTask/listUnApprovedTaskForCustomer', featureName: 'List UnApproved Customer Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-167', url: '/exhTask/showApprovedTaskForCustomer', featureName: 'Show Approved Customer Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-169', url: '/exhTask/listApprovedTaskForCustomer', featureName: 'List Approved Customer Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-187', url: '/exhTask/showDisbursedTaskForCustomer', featureName: 'Show Disbursed Customer Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-198', url: '/exhTask/listDisbursedTaskForCustomer', featureName: 'List Disbursed Customer Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-35', url: '/exhTask/listForCustomer', featureName: 'List task for customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-36', url: '/exhTask/showForOtherBankUser', featureName: 'Show task for other bank user', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-37', url: '/exhTask/listForOtherBankUser', featureName: 'List task for other bank user', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-38', url: '/exhTask/resolveTaskForOtherBank', featureName: 'Resolve task for other bank user', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-39', url: '/exhTask/downloadCsvForOtherBank', featureName: 'Download CSV for other bank user', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-258', url: '/exhTask/showDetailsForReplaceTask', featureName: 'Show Details For Replace Task', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //bank drop down for admin and other bank user
        new RequestMap(transactionCode: 'EXH-256', url: '/exhTask/reloadBankByTaskStatusAndTaskType', featureName: 'Reload bank drop down independently', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        /*Make payment*/
        new RequestMap(transactionCode: 'EXH-239', url: '/exhTask/showForMakePayment', featureName: 'Show For make payment', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-240', url: '/exhTask/callbackForPayPointUserReturn', featureName: 'Callback For PayPoint User Return', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-241', url: '/exhTask/showForPayPointUserReturn', featureName: 'Show For PayPoint User Return', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-242', url: '/exhTask/callbackForPayPointPRN', featureName: 'Callback For PayPointPRN', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

        /*Task Entity Note*/
        new RequestMap(transactionCode: 'EXH-226', url: '/exhTask/showEntityNoteForTask', featureName: 'Show Task Note', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-227', url: '/exhTask/createEntityNoteForTask', featureName: 'Create Task Note', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-228', url: '/exhTask/listEntityNoteForTask', featureName: 'List Task Note', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-230', url: '/exhTask/deleteEntityNoteForTask', featureName: 'Delete Task Note', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-231', url: '/exhTask/updateEntityNoteForTask', featureName: 'Update Task Note', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-229', url: '/exhTask/selectEntityNoteForTask', featureName: 'Edit Task Note', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //setting remitance purpose
        new RequestMap(transactionCode: 'EXH-56', url: '/exhRemittancePurpose/show', featureName: 'Show remittance purpose', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-57', url: '/exhRemittancePurpose/create', featureName: 'Create remittance purpose', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-58', url: '/exhRemittancePurpose/update', featureName: 'Update remittance purpose', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-59', url: '/exhRemittancePurpose/edit', featureName: 'Select remittance purpose', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-60', url: '/exhRemittancePurpose/list', featureName: 'List remittance purpose', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-61', url: '/exhRemittancePurpose/delete', featureName: 'Delete remittance purpose', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //setting photo id type
        new RequestMap(transactionCode: 'EXH-62', url: '/exhPhotoIdType/show', featureName: 'Show Photo-Id-Type', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-63', url: '/exhPhotoIdType/create', featureName: 'Create Photo-Id-Type', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-64', url: '/exhPhotoIdType/update', featureName: 'Update Photo-Id-Type', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-65', url: '/exhPhotoIdType/edit', featureName: 'Select Photo-Id-Type', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-66', url: '/exhPhotoIdType/list', featureName: 'List Photo-Id-Type', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-67', url: '/exhPhotoIdType/delete', featureName: 'Delete Photo-Id-Type', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //setting country
        new RequestMap(transactionCode: 'EXH-74', url: '/exhCountry/show', featureName: 'Show Country', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-75', url: '/exhCountry/create', featureName: 'Create Country', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-76', url: '/exhCountry/update', featureName: 'Update Country', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-77', url: '/exhCountry/edit', featureName: 'Select Country', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-78', url: '/exhCountry/list', featureName: 'List Country', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-79', url: '/exhCountry/delete', featureName: 'Delete Country', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //setting currency conversion
        new RequestMap(transactionCode: 'EXH-95', url: '/exhCurrencyConversion/show', featureName: 'Show currency conversion', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-96', url: '/exhCurrencyConversion/create', featureName: 'Create currency conversion', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-97', url: '/exhCurrencyConversion/update', featureName: 'Update currency conversion', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-98', url: '/exhCurrencyConversion/list', featureName: 'List currency conversion', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-99', url: '/exhCurrencyConversion/edit', featureName: 'Select currency conversion', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // Setting Exh User Agent Mapping
        new RequestMap(transactionCode: 'EXH-153', url: '/exhUserAgent/show', featureName: 'Show User Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-154', url: '/exhUserAgent/create', featureName: 'Create User Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-155', url: '/exhUserAgent/update', featureName: 'Update User Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-156', url: '/exhUserAgent/delete', featureName: 'Delete User Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-157', url: '/exhUserAgent/list', featureName: 'List User Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-158', url: '/exhUserAgent/select', featureName: 'Select User Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save();

        // setting Exh Agent
        new RequestMap(transactionCode: 'EXH-145', url: '/exhAgent/show', featureName: 'Show Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-146', url: '/exhAgent/create', featureName: 'Create Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-147', url: '/exhAgent/list', featureName: 'List Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-148', url: '/exhAgent/select', featureName: 'Select Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-149', url: '/exhAgent/update', featureName: 'Update Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-150', url: '/exhAgent/delete', featureName: 'Delete Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // setting Exh Agent Currency Posting
        new RequestMap(transactionCode: 'EXH-139', url: '/exhAgentCurrencyPosting/show', featureName: 'Show Agent Currency Posting', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-140', url: '/exhAgentCurrencyPosting/create', featureName: 'Create Agent Currency Posting', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-141', url: '/exhAgentCurrencyPosting/list', featureName: 'List Agent Currency Posting', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-142', url: '/exhAgentCurrencyPosting/select', featureName: 'Select Agent Currency Posting', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-143', url: '/exhAgentCurrencyPosting/update', featureName: 'Update Agent Currency Posting', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-144', url: '/exhAgentCurrencyPosting/delete', featureName: 'Delete Agent Currency Posting', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // settings Exh Regular Fee
        new RequestMap(transactionCode: 'EXH-20', url: '/exhRegularFee/show', featureName: 'Show Regular Fee for Super Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-27', url: '/exhRegularFee/update', featureName: 'Update Regular Fee', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-28', url: '/exhRegularFee/calculate', featureName: 'Calculate Regular Fee', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // Reports
        new RequestMap(transactionCode: 'EXH-107', url: '/exhReport/showCustomerHistory', featureName: 'Show Customer History', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-205', url: '/exhReport/showCustomerRemittanceSummary', featureName: 'Show Customer remittance Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-204', url: '/exhReport/downloadCustomerHistory', featureName: 'Download Customer History', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-206', url: '/exhReport/downloadCustomerRemittanceSummary', featureName: 'Download Customer Customer Remittance Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-108', url: '/exhReport/getForCustomerRemittance', featureName: 'Get remittance details by customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-209', url: '/exhReport/listForCustomerRemittanceSummary', featureName: 'List Customer Remittance Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-109', url: '/exhReport/listForCustomerRemittance', featureName: 'List remittance details by customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-110', url: '/exhReport/showRemittanceSummary', featureName: 'Show remittance summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-111', url: '/exhReport/getRemittanceSummaryReport', featureName: 'Get remittance summary report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-112', url: '/exhReport/showInvoice', featureName: 'Show Invoice', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-200', url: '/exhReport/showInvoiceForCustomer', featureName: 'Show Invoice for Customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-113', url: '/exhReport/showInvoiceFromTaskGrid', featureName: 'Show invoice from task grid', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-199', url: '/exhReport/showInvoiceFromGridForCustomer', featureName: 'Invoice Details for Customer from Grid', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-114', url: '/exhReport/getInvoiceDetails', featureName: 'Get invoice details', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-201', url: '/exhReport/invoiceDetailsForCustomer', featureName: 'Invoice Details for Customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-115', url: '/exhReport/downloadInvoice', featureName: 'Download invoice', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-202', url: '/exhReport/downloadInvoiceForCustomer', featureName: 'Download invoice', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-235', url: '/exhReport/downloadRemittanceTransactionCsv', featureName: 'Download Remittance Transaction CSV', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-237', url: '/exhReport/downloadCustomerCSV', featureName: 'Download Customer in CSV', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'EXH-251', url: '/exhReport/listTransactionSummary', featureName: 'List Transaction Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-252', url: '/exhReport/downloadTransactionSummary', featureName: 'Download Transaction Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-253', url: '/exhReport/listRemittanceTransaction', featureName: 'List Remittance Transaction', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-254', url: '/exhReport/downloadRemittanceTransaction', featureName: 'Download Remittance Transaction', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // Agent wise commission
        new RequestMap(transactionCode: 'EXH-188', url: '/exhReport/showAgentWiseCommissionForAdmin', featureName: 'Show Agent Wise Commission', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-189', url: '/exhReport/listAgentWiseCommissionForAdmin', featureName: 'List Agent Wise Commission', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-190', url: '/exhReport/showAgentWiseCommissionForAgent', featureName: 'Show Agent Commission', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-191', url: '/exhReport/listAgentWiseCommissionForAgent', featureName: 'List Commission', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-195', url: '/exhReport/downloadAgentWiseCommissionForAdmin', featureName: 'Download Agent Wise Commission for Admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-194', url: '/exhReport/downloadAgentWiseCommissionForAgent', featureName: 'Download Agent Wise Commission for Agent', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'EXH-116', url: '/exhReport/showCashierWiseReportForAdmin', featureName: 'Show cashier wise report for admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-117', url: '/exhReport/showCashierWiseReportForCashier', featureName: 'Show cashier wise report for cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-118', url: '/exhReport/listCashierWiseReportForAdmin', featureName: 'List cashier wise report for admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-119', url: '/exhReport/listCashierWiseReportForCashier', featureName: 'List cashier wise report for cashier', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'EXH-120', url: '/exhReport/showSummaryReportForAdmin', featureName: 'Show summary report for admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-121', url: '/exhReport/listReportSummaryForAdmin', featureName: 'List summary report for admin', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-122', url: '/exhReport/downloadRemittanceSummaryReport', featureName: 'Download remittance summary report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'EXH-123', url: '/exhReport/downloadCashierWiseTaskReport', featureName: 'Download cashier wise task report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'EXH-212', url: '/exhReport/showCustomerTransactionSummary', featureName: 'Show customer wise transaction report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-210', url: '/exhReport/listCustomerTransactionSummary', featureName: 'List customer wise transaction report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-211', url: '/exhReport/downloadCustomerTransactionSummary', featureName: 'Download customer wise transaction report', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //sanction
        new RequestMap(transactionCode: 'EXH-124', url: '/exhSanction/sanctionCountFromBeneficiary', featureName: 'Count sanction from beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-125', url: '/exhSanction/sanctionCountFromCustomer', featureName: 'Count sanction from customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-126', url: '/exhSanction/showFromBeneficiary', featureName: 'Navigate to sanction from beneficiary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-127', url: '/exhSanction/showFromCustomer', featureName: 'Navigate to sanction from customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-128', url: '/exhSanction/show', featureName: 'Show Sanction', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-129', url: '/exhSanction/list', featureName: 'List Sanction', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-130', url: '/exhSanction/listForCustomer', featureName: 'List sanction from customer', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-131', url: '/exhSanction/showSanctionUpload', featureName: 'Show for sanction upload', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-132', url: '/exhSanction/uploadSanctionFile', featureName: 'Upload sanction', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'EXH-249', url: '/exhReport/showTransactionSummary', featureName: 'Transaction Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-250', url: '/exhReport/showRemittanceTransaction', featureName: 'Transaction Summary', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        //postal code
        new RequestMap(transactionCode: 'EXH-243', url: '/exhPostalCode/show', featureName: 'Show Postal Code', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-244', url: '/exhPostalCode/create', featureName: 'Create Postal Code', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-245', url: '/exhPostalCode/select', featureName: 'Select Postal Code', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-246', url: '/exhPostalCode/update', featureName: 'Update Postal Code', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-247', url: '/exhPostalCode/delete', featureName: 'Delete Postal Code', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'EXH-248', url: '/exhPostalCode/list', featureName: 'List Postal Code', configAttribute: 'ROLE_-2', pluginId: 9, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        return true
    }

    public boolean createRequestMapForProjectTrack() {
        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
        VALUES(-26,0,'/projectTrack/renderProjectTrackMenu','ROLE_-2','Project Track Module Menu',10,'PT-1',TRUE,FALSE)""")

        //PtBacklog
        new RequestMap(transactionCode: 'PT-2', url: '/ptBacklog/show', featureName: 'Show all backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-3', url: '/ptBacklog/list', featureName: 'List all backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-4', url: '/ptBacklog/select', featureName: 'Select all backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-5', url: '/ptBacklog/create', featureName: 'Create a backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-6', url: '/ptBacklog/update', featureName: 'Update a backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-7', url: '/ptBacklog/delete', featureName: 'Delete a backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'PT-57', url: '/ptBacklog/addToMyBacklog', featureName: 'add to my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-52', url: '/ptBacklog/showMyBacklog', featureName: 'Show all my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-53', url: '/ptBacklog/listMyBacklog', featureName: 'List all my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-54', url: '/ptBacklog/selectMyBacklog', featureName: 'Select my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-55', url: '/ptBacklog/updateMyBacklog', featureName: 'Update my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-56', url: '/ptBacklog/removeMyBacklog', featureName: 'Remove from my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'PT-39', url: '/ptBacklog/showBackLogForSprint', featureName: 'Show Backlog for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-46', url: '/ptBacklog/createBackLogForSprint', featureName: 'Create Backlog for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-47', url: '/ptBacklog/deleteBackLogForSprint', featureName: 'Delete Backlog for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-48', url: '/ptBacklog/listBackLogForSprint', featureName: 'List Backlog for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-71', url: '/ptBacklog/backlogListForModule', featureName: 'Get Backlog By moduleId', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'PT-83', url: '/ptBacklog/showForActive', featureName: 'Show Active Backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-84', url: '/ptBacklog/listForActive', featureName: 'List Active Backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-85', url: '/ptBacklog/showForInActive', featureName: 'Show Inactive Backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-86', url: '/ptBacklog/listForInActive', featureName: 'List Inactive Backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-96', url: '/ptBacklog/acceptStory', featureName: 'Accept Story(Backlog)', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //PtAcceptanceCriteria
        new RequestMap(transactionCode: 'PT-40', url: '/ptAcceptanceCriteria/show', featureName: 'Show all acceptance criteria', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-41', url: '/ptAcceptanceCriteria/list', featureName: 'List all acceptance criteria', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-42', url: '/ptAcceptanceCriteria/select', featureName: 'Select all acceptance criteria', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-43', url: '/ptAcceptanceCriteria/create', featureName: 'Create an acceptance criteria', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-44', url: '/ptAcceptanceCriteria/update', featureName: 'Update an acceptance criteria', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-45', url: '/ptAcceptanceCriteria/delete', featureName: 'Delete an acceptance criteria', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-98', url: '/ptAcceptanceCriteria/listForMyBacklog', featureName: 'List all acceptance criteria for my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-72', url: '/ptAcceptanceCriteria/showForMyBacklog', featureName: 'Show all acceptance criteria for my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-73', url: '/ptAcceptanceCriteria/updateForMyBacklog', featureName: 'Update an acceptance criteria for my backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //PtFlow
        new RequestMap(transactionCode: 'PT-109', url: '/ptFlow/show', featureName: 'Show all flow', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-110', url: '/ptFlow/list', featureName: 'List all flow', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-111', url: '/ptFlow/select', featureName: 'Select all flow', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-112', url: '/ptFlow/create', featureName: 'Create a flow', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-113', url: '/ptFlow/update', featureName: 'Update a flow', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-114', url: '/ptFlow/delete', featureName: 'Delete a flow', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //PtModule
        new RequestMap(transactionCode: 'PT-21', url: '/ptModule/show', featureName: 'Show all Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-22', url: '/ptModule/create', featureName: 'Create a Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-23', url: '/ptModule/select', featureName: 'Select a Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-24', url: '/ptModule/update', featureName: 'Update a Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-25', url: '/ptModule/delete', featureName: 'Delete a Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-26', url: '/ptModule/list', featureName: 'List all Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-68', url: '/ptModule/getModuleList', featureName: 'Get Module By projectId', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //ptProjectModule
        new RequestMap(transactionCode: 'PT-27', url: '/ptProjectModule/show', featureName: 'Show all Project Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-28', url: '/ptProjectModule/create', featureName: 'Create a Project Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-29', url: '/ptProjectModule/select', featureName: 'Select a Project Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-30', url: '/ptProjectModule/update', featureName: 'Update a Project Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-31', url: '/ptProjectModule/delete', featureName: 'Delete a Project Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-32', url: '/ptProjectModule/list', featureName: 'List all Project Module', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // ptBug
        new RequestMap(transactionCode: 'PT-8', url: '/ptBug/show', featureName: 'Show Project track Bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-9', url: '/ptBug/create', featureName: 'Create Project track Bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-10', url: '/ptBug/update', featureName: 'Update Project track Bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-11', url: '/ptBug/delete', featureName: 'Delete Project track Bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-12', url: '/ptBug/select', featureName: 'Select Project track Bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-13', url: '/ptBug/list', featureName: 'List Project track Bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-74', url: '/ptBug/downloadBugContent', featureName: 'Download entity content for bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'PT-75', url: '/ptBug/showBugForMyTask', featureName: 'Show Bug List For My Task', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-76', url: '/ptBug/updateBugForMyTask', featureName: 'Update Bug For My Task', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-77', url: '/ptBug/selectBugForMyTask', featureName: 'Select Bug For My Task', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'PT-78', url: '/ptBug/showBugForSprint', featureName: 'Show Bug for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-79', url: '/ptBug/createBugForSprint', featureName: 'Create Bug for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-80', url: '/ptBug/deleteBugForSprint', featureName: 'Delete Bug for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-81', url: '/ptBug/listBugForSprint', featureName: 'List Bug for sprint', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'PT-99', url: '/ptBug/reOpenBug', featureName: 'Bug re-open', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-100', url: '/ptBug/closeBug', featureName: 'Set bug status close', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'PT-102', url: '/ptBug/showBugDetails', featureName: 'Show bug details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-117', url: '/ptBug/searchBugDetails', featureName: 'Search individual bug details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'PT-104', url: '/ptBug/showOrphanBug', featureName: 'Show orphan bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-105', url: '/ptBug/createOrphanBug', featureName: 'Create orphan bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-106', url: '/ptBug/updateOrphanBug', featureName: 'Update orphan bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-107', url: '/ptBug/listOrphanBug', featureName: 'List orphan bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-108', url: '/ptBug/addToMyBug', featureName: 'Add bug to my bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'PT-115', url: '/ptBug/showMyBug', featureName: 'Shoe my bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-116', url: '/ptBug/listMyBug', featureName: 'List my bug', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'PT-118', url: '/ptBug/bugListForModule', featureName: 'Get Bug By moduleId', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // PtProject
        new RequestMap(transactionCode: 'PT-33', url: '/ptProject/show', featureName: 'Show Project of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-34', url: '/ptProject/create', featureName: 'Create Project of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-35', url: '/ptProject/update', featureName: 'Update Project of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-36', url: '/ptProject/delete', featureName: 'Delete Project of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-37', url: '/ptProject/select', featureName: 'Select Project of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-38', url: '/ptProject/list', featureName: 'List Project of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // PtSprint
        new RequestMap(transactionCode: 'PT-14', url: '/ptSprint/show', featureName: 'Show Sprint Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-15', url: '/ptSprint/create', featureName: 'Create Sprint of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-16', url: '/ptSprint/update', featureName: 'Update Sprint of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-17', url: '/ptSprint/delete', featureName: 'Delete Sprint of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-18', url: '/ptSprint/list', featureName: 'List Sprint of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-20', url: '/ptSprint/select', featureName: 'Select Sprint of Project track', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-67', url: '/ptSprint/listSprintByProjectId', featureName: 'Get Sprint By projectId', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-87', url: '/ptSprint/listInActiveSprintByProjectId', featureName: 'Get Inactive Sprint By Project ID', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //PtReport
        new RequestMap(transactionCode: 'PT-61', url: '/ptReport/showReportOpenBacklog', featureName: 'Show all open backlog', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-62', url: '/ptReport/downloadOpenBacklogReport', featureName: 'download open backlog report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-63', url: '/ptReport/listReportOpenBacklog', featureName: 'list open backlog report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-58', url: '/ptReport/downloadSprintDetails', featureName: 'Download Sprint Details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-59', url: '/ptReport/showReportSprint', featureName: 'Show sprint report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-60', url: '/ptReport/listSprintDetails', featureName: 'List Sprint Details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-64', url: '/ptReport/downloadBugDetails', featureName: 'Download Bug Details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-65', url: '/ptReport/showReportBug', featureName: 'Show Bug Details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-66', url: '/ptReport/listBugDetails', featureName: 'List Bug Details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-94', url: '/ptReport/showForBacklogDetails', featureName: 'Show for backlog details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-95', url: '/ptReport/searchForBacklogDetails', featureName: 'Search for backlog details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-101', url: '/ptReport/downloadBacklogDetailsReport', featureName: 'Download backlog details report', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'PT-103', url: '/ptReport/downloadPtBugDetails', featureName: 'Download individual Pt Bug Details', configAttribute: 'ROLE_-2', pluginId: 10, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        return true
    }

    public boolean createRequestMapForARMS() {
        executeInsertSql("""INSERT INTO request_map (id,version,url,config_attribute,feature_name,plugin_id,transaction_code,is_viewable,is_common)
        VALUES(-27,0,'/arms/renderArmsMenu','ROLE_-2','ARMS Module Menu',11,'RMS-1',TRUE,FALSE)""")

        //taglib refresh
        new RequestMap(transactionCode: 'RMS-88', url: '/rmsExchangeHouse/reloadExchangeHouseDropDown', featureName: 'Refresh exchange house dropdown', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-89', url: '/rmsExchangeHouse/reloadExchangeHouseFilteredDropDown', featureName: 'Refresh filtered exchange house dropdown', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-122', url: '/rmsTask/reloadTaskDetailsTagLib', featureName: 'Refresh task details', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // rmsExchangeHouse
        new RequestMap(transactionCode: 'RMS-2', url: '/rmsExchangeHouse/show', featureName: 'Show Exchange House', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-3', url: '/rmsExchangeHouse/create', featureName: 'Create Exchange House', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-4', url: '/rmsExchangeHouse/update', featureName: 'Update Exchange House', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-5', url: '/rmsExchangeHouse/delete', featureName: 'Delete Exchange House', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-6', url: '/rmsExchangeHouse/select', featureName: 'Select Exchange House', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-7', url: '/rmsExchangeHouse/list', featureName: 'List Exchange House', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // rmsExchangeHouseCurrencyPosting
        new RequestMap(transactionCode: 'RMS-10', url: '/rmsExchangeHouseCurrencyPosting/show', featureName: 'Show Exchange House Currency Posting', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-11', url: '/rmsExchangeHouseCurrencyPosting/create', featureName: 'Create Exchange House Currency Posting', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-12', url: '/rmsExchangeHouseCurrencyPosting/update', featureName: 'Update Exchange House Currency Posting', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-13', url: '/rmsExchangeHouseCurrencyPosting/delete', featureName: 'Delete Exchange House Currency Posting', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-14', url: '/rmsExchangeHouseCurrencyPosting/select', featureName: 'Select Exchange House Currency Posting', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-15', url: '/rmsExchangeHouseCurrencyPosting/list', featureName: 'List Exchange House Currency Posting', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // rmsProcessInstrumentMapping
        new RequestMap(transactionCode: 'RMS-16', url: '/rmsProcessInstrumentMapping/show', featureName: 'Show Process Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-17', url: '/rmsProcessInstrumentMapping/create', featureName: 'Create Process Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-18', url: '/rmsProcessInstrumentMapping/update', featureName: 'Update Process Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-19', url: '/rmsProcessInstrumentMapping/delete', featureName: 'Delete Process Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-20', url: '/rmsProcessInstrumentMapping/select', featureName: 'Select Process Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-21', url: '/rmsProcessInstrumentMapping/list', featureName: 'List Process Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // rmsTask
        new RequestMap(transactionCode: 'RMS-23', url: '/rmsTask/show', featureName: 'Show Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-84', url: '/rmsTask/showForExh', featureName: 'Show Task ExchangeHouse User', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-24', url: '/rmsTask/create', featureName: 'Create Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-25', url: '/rmsTask/update', featureName: 'Update Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-26', url: '/rmsTask/delete', featureName: 'Delete Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-27', url: '/rmsTask/select', featureName: 'Select Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-28', url: '/rmsTask/list', featureName: 'List Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-29', url: '/rmsTask/showForUploadTask', featureName: 'Show upload Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-30', url: '/rmsTask/createForUploadTask', featureName: 'Create Tasks for upload Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-31', url: '/rmsTask/listTaskForTaskList', featureName: 'List Task to create TaskList', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-32', url: '/rmsTask/showForMapTask', featureName: 'Show for map Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-33', url: '/rmsTask/listTaskForMap', featureName: 'List Task to map Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-34', url: '/rmsTask/mapTask', featureName: 'Map Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-35', url: '/rmsTask/showForApproveTask', featureName: 'Show for approve Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-36', url: '/rmsTask/listTaskForApprove', featureName: 'List Task to approve Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-37', url: '/rmsTask/approve', featureName: 'Approve Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-38', url: '/rmsTask/reviseTask', featureName: 'Revise Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-44', url: '/rmsTask/showTaskDetailsWithNote', featureName: 'Show task details', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-45', url: '/rmsTask/searchTaskDetailsWithNote', featureName: 'Search task details', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-64', url: '/rmsTask/createRmsTaskNote', featureName: 'Create Rms Task note', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-78', url: '/rmsTask/renderTaskDetails', featureName: 'Show Task Details', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-79', url: '/rmsTask/disburseRmsTask', featureName: 'Disburse RMS task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-85', url: '/rmsTask/showForUploadTaskForExh', featureName: 'Show upload Task for exh user', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-86', url: '/rmsTask/sendRmsTaskToBank', featureName: 'Send Rms Task to bank for Exh user', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //forward task
        new RequestMap(transactionCode: 'RMS-71', url: '/rmsTask/showTaskDetailsForForward', featureName: 'Show task details for forward', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-72', url: '/rmsTask/searchTaskDetailsForForward', featureName: 'Search task details for forward', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-73', url: '/rmsTask/forwardRmsTask', featureName: 'Forward task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //manage task
        new RequestMap(transactionCode: 'RMS-91', url: '/rmsTask/showForManageTask', featureName: 'Show for manage task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-106', url: '/rmsTask/cancelRmsTask', featureName: 'Cancel rms task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // rmsTaskList
        new RequestMap(transactionCode: 'RMS-39', url: '/rmsTaskList/show', featureName: 'Show Task List', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-40', url: '/rmsTaskList/create', featureName: 'Create Task List', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-42', url: '/rmsTaskList/showSearchTaskList', featureName: 'Show Task List for search', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-43', url: '/rmsTaskList/listSearchTaskList', featureName: 'List for search Task List', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-90', url: '/rmsTaskList/reloadTaskListDropDown', featureName: 'Reload task list drop down', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // rmsInstrument
        new RequestMap(transactionCode: 'RMS-46', url: '/rmsInstrument/listTaskForProcessInstrument', featureName: 'List Task for process instrument', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-47', url: '/rmsInstrument/showForIssuePo', featureName: 'Show for issue PO', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-48', url: '/rmsInstrument/downloadTaskReportForIssuePo', featureName: 'Download task for issue PO', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-49', url: '/rmsInstrument/showForIssueEft', featureName: 'Show for issue EFT', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-50', url: '/rmsInstrument/downloadTaskReportForIssueEft', featureName: 'Download task for issue EFT', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-51', url: '/rmsInstrument/showForIssueOnline', featureName: 'Show for issue online', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-52', url: '/rmsInstrument/downloadTaskReportForIssueOnline', featureName: 'Download task for issue online', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-118', url: '/rmsInstrument/showForInstrumentPurchase', featureName: 'Show for instrument purchase', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-119', url: '/rmsInstrument/downloadTaskReportForPurchaseInstrument', featureName: 'Download csv task report for instrument purchase', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'RMS-55', url: '/rmsInstrument/showForForwardCashCollection', featureName: 'Show for forward cash collection', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-56', url: '/rmsInstrument/downloadTaskReportForForwardCashCollection', featureName: 'Download task for forward cash collection', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-57', url: '/rmsInstrument/showForForwardOnline', featureName: 'Show for forward online', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-58', url: '/rmsInstrument/downloadTaskReportForForwardOnline', featureName: 'Download task for forward online', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //for dropdown refresh
        new RequestMap(transactionCode: 'RMS-120', url: '/rmsInstrument/reloadInstrumentDropDown', featureName: 'Refresh Instrument dropdown', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-121', url: '/rmsInstrument/reloadBankListFilteredDropDown', featureName: 'Refresh bank dropdown for instrument purchase', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        // rmsReport
        new RequestMap(transactionCode: 'RMS-59', url: '/rmsReport/showForListWiseStatusReport', featureName: 'Show for list wise status report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-60', url: '/rmsReport/listForListWiseStatusReport', featureName: 'List for list wise status report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-61', url: '/rmsReport/downloadListWiseStatusReport', featureName: 'Download list wise status report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-62', url: '/rmsReport/showBeneficiaryDetails', featureName: 'Show beneficiary details', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-63', url: '/rmsReport/searchBeneficiaryDetails', featureName: 'Search task for beneficiary details', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-83', url: '/rmsReport/searchBeneficiaryForGrid', featureName: 'Search beneficiary for Grid', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-65', url: '/rmsReport/showTaskListPlan', featureName: 'Show Task List Plan', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-66', url: '/rmsReport/searchTaskListPlan', featureName: 'Search Task List Plan', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-123', url: '/rmsReport/showForViewCancelTask', featureName: 'Show For View Cancel Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-124', url: '/rmsReport/listForViewCancelTask', featureName: 'List For View Cancel Task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

        //forwarded unpaid task
        new RequestMap(transactionCode: 'RMS-74', url: '/rmsReport/showForForwardUnpaidTask', featureName: 'Show for forwarded unpaid task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-75', url: '/rmsReport/listTaskForForwardUnpaidTask', featureName: 'List forwarded unpaid task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-126', url: '/rmsReport/listTaskDetailsForForwardedUnpaidTasks', featureName: 'List forwarded unpaid task details', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //taskTrace
        new RequestMap(transactionCode: 'RMS-76', url: '/rmsTaskTrace/showRmsTaskHistory', featureName: 'Show Task History', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-77', url: '/rmsTaskTrace/searchRmsTaskHistory', featureName: 'Search Task History', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //disburse cash collection task
        new RequestMap(transactionCode: 'RMS-80', url: '/rmsTask/showDisburseCashCollection', featureName: 'Show for disburse cash collection task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-81', url: '/rmsTask/searchDisburseCashCollection', featureName: 'Search for disburse cash collection task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-82', url: '/rmsTask/disburseCashCollectionRmsTask', featureName: 'Disburse cash collection rms task', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //manage task list
        new RequestMap(transactionCode: 'RMS-93', url: '/rmsTaskList/showForManageTaskList', featureName: 'Show for manage task list', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-94', url: '/rmsTaskList/listForManageTaskList', featureName: 'List for manage task list', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-105', url: '/rmsTaskList/removeFromList', featureName: 'Remove from task list', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-108', url: '/rmsTaskList/renameTaskList', featureName: 'Rename task list', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-109', url: '/rmsTaskList/moveTaskToAnotherList', featureName: 'Move task from task list', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //RmsTransactionDay
        new RequestMap(transactionCode: 'RMS-95', url: '/rmsTransactionDay/show', featureName: 'Show transaction day', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-96', url: '/rmsTransactionDay/list', featureName: 'List transaction day', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-97', url: '/rmsTransactionDay/openTransactionDay', featureName: 'Open transcation day', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-98', url: '/rmsTransactionDay/closeTransactionDay', featureName: 'Close transaction day', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-99', url: '/rmsTransactionDay/reOpenTransactionDay', featureName: 'Reopen transaction day', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //RmsTaskListSummaryModel
        new RequestMap(transactionCode: 'RMS-107', url: '/rmsTaskListSummaryModel/listUnResolvedTaskList', featureName: 'List un-resolved task list', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //RmsPurchaseInstrumentMapping
        new RequestMap(transactionCode: 'RMS-110', url: '/rmsPurchaseInstrumentMapping/show', featureName: 'Show Purchase Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-111', url: '/rmsPurchaseInstrumentMapping/create', featureName: 'Create Purchase Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-112', url: '/rmsPurchaseInstrumentMapping/update', featureName: 'Update Purchase Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-113', url: '/rmsPurchaseInstrumentMapping/delete', featureName: 'Delete Purchase Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-114', url: '/rmsPurchaseInstrumentMapping/select', featureName: 'Select Purchase Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-115', url: '/rmsPurchaseInstrumentMapping/list', featureName: 'List Purchase Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-125', url: '/rmsPurchaseInstrumentMapping/evaluateLogic', featureName: 'List Purchase Instrument Mapping', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //rmsTask for view notes
        new RequestMap(transactionCode: 'RMS-116', url: '/rmsTask/showForViewNotes', featureName: 'Show for view notes', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-117', url: '/rmsTask/listForViewNotes', featureName: 'Show for view notes', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //decision summary
        new RequestMap(transactionCode: 'RMS-127', url: '/rmsReport/showDecisionSummary', featureName: 'Show decision summary', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-128', url: '/rmsReport/listDecisionSummary', featureName: 'List decision summary', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'RMS-129', url: '/rmsReport/downloadDecisionSummaryReport', featureName: 'Download decision summary pdf report', configAttribute: 'ROLE_-2', pluginId: 11, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //last available value of RMS request map RMS-130
        return true
    }

    public boolean createRequestMapForSARB() {

        new RequestMap(transactionCode: 'SARB-1', url: '/sarb/renderSarbMenu', featureName: 'Render menu for province', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //province

        new RequestMap(transactionCode: 'SARB-2', url: '/sarbProvince/show', featureName: 'Show province', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-3', url: '/sarbProvince/create', featureName: 'Create province', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-4', url: '/sarbProvince/select', featureName: 'Select province', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-5', url: '/sarbProvince/update', featureName: 'Update province', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-6', url: '/sarbProvince/delete', featureName: 'Delete province', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-7', url: '/sarbProvince/list', featureName: 'List province', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'SARB-8', url: '/sarbTaskModel/showForSendTaskToSarb', featureName: 'Show For Send task to SARB', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-9', url: '/sarbTaskModel/listForSendTaskToSarb', featureName: 'List for Send task to SARB', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-13', url: '/sarbTaskModel/sendTaskToSarb', featureName: 'Send task to SARB', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //showTaskStatus
        new RequestMap(transactionCode: 'SARB-10', url: '/sarbTaskModel/showTaskStatus', featureName: 'Show Task Status', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-11', url: '/sarbTaskModel/listTaskStatus', featureName: 'List Task Status', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-14', url: '/sarbTaskModel/showTaskStatusDetails', featureName: 'Show Task Status Details', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'SARB-15', url: '/sarbTaskModel/showTaskForRetrieveResponse', featureName: 'Show Retrieve response', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-16', url: '/sarbTaskModel/retrieveResponse', featureName: 'Send Retrieve response', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-12', url: '/sarbTaskModel/listSarbTaskForRetrieveResponse', featureName: 'List Retrieve response', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-17', url: '/sarbTaskModel/retrieveResponseAgain', featureName: 'Retrieve SARB task response again', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'SARB-18', url: '/sarbTaskModel/moveForResend', featureName: 'Move task to send again', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-19', url: '/sarbTaskModel/moveForCancel', featureName: 'Move Sarb Task For Cancel', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-34', url: '/sarbTaskModel/moveForReplace', featureName: 'Move task to replace task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-35', url: '/sarbTaskModel/moveForRefund', featureName: 'Move Sarb Task refund task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-20', url: '/sarbTaskModel/sendCancelTaskToSarb', featureName: 'Send Cancel SarbTask To Sarb', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-36', url: '/sarbTaskModel/sendReplaceTaskToSarb', featureName: 'Send Replace SarbTask To Sarb', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-37', url: '/sarbTaskModel/sendRefundTaskToSarb', featureName: 'Send Refund SarbTask To Sarb', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'SARB-24', url: '/sarbTaskModel/listForCancelTask', featureName: 'List For Cancel Task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-25', url: '/sarbTaskModel/showTaskForCancel', featureName: 'Show For Cancel Task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'SARB-26', url: '/sarbTaskModel/listForReplaceTask', featureName: 'List For Replace Task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-27', url: '/sarbTaskModel/showForReplaceTask', featureName: 'Show For Replace task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'SARB-30', url: '/sarbTaskModel/showDetailsForRefundTask', featureName: 'Show Task details for refund task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-31', url: '/sarbTaskModel/createSarbTaskForRefundTask', featureName: 'Create new task for send to sarb for refund', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'SARB-28', url: '/sarbTaskModel/listForRefundTask', featureName: 'List For Refund Task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-29', url: '/sarbTaskModel/showForRefundTask', featureName: 'Show For Refund task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'SARB-32', url: '/sarbTaskModel/updateTaskForReplaceTask', featureName: 'Update Task For Replace Task', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-33', url: '/sarbTaskModel/listRefundTaskForShowStatus', featureName: 'List Refund Task For Show Status', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        //report
        new RequestMap(transactionCode: 'SARB-21', url: '/sarbReport/showSarbTransactionSummary', featureName: 'Show SARB Transaction Summary', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-22', url: '/sarbReport/listSarbTransactionSummary', featureName: 'List SARB Transaction Summary', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'SARB-23', url: '/sarbReport/downloadSarbTransactionSummary', featureName: 'Download SARB Transaction Summary', configAttribute: 'ROLE_-2', pluginId: 12, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
    }

    //Request Map For Document Plugin
    public boolean createRequestMapForDocument() {

        new RequestMap(transactionCode: 'DOC-1', url: '/document/renderDocumentMenu', featureName: 'Render menu for Document', configAttribute: 'ROLE_-3', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //Document Category Request Map
        new RequestMap(transactionCode: 'DOC-2', url: '/docCategory/show', featureName: 'Show Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-3', url: '/docCategory/list', featureName: 'List/Search Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-4', url: '/docCategory/create', featureName: 'Create Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-5', url: '/docCategory/update', featureName: 'Update Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-6', url: '/docCategory/delete', featureName: 'Delete Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-7', url: '/docCategory/select', featureName: 'Select Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        //Document Sub Category Request Map
        new RequestMap(transactionCode: 'DOC-8', url: '/docSubCategory/show', featureName: 'Show Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-9', url: '/docSubCategory/list', featureName: 'List/Search Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-10', url: '/docSubCategory/create', featureName: 'Create Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-11', url: '/docSubCategory/update', featureName: 'Update Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-12', url: '/docSubCategory/delete', featureName: 'Delete Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-13', url: '/docSubCategory/select', featureName: 'Select Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-14', url: '/docCategory/showCategories', featureName: 'Show Categories', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

        //Document App Group Entity Request Map
        new RequestMap(transactionCode: 'DOC-15', url: '/docCategoryUserMapping/showForCategory', featureName: 'Show App User Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-16', url: '/docCategoryUserMapping/listForCategory', featureName: 'List/Search App User Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-17', url: '/docCategoryUserMapping/createForCategory', featureName: 'Create App User Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-18', url: '/docCategoryUserMapping/updateForCategory', featureName: 'Update App User Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-19', url: '/docCategoryUserMapping/deleteForCategory', featureName: 'Delete App User Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-20', url: '/docCategoryUserMapping/selectForCategory', featureName: 'Select App User Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-21', url: '/docSubCategoryUserMapping/showForSubCategory', featureName: 'Show App User Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-22', url: '/docSubCategoryUserMapping/listForSubCategory', featureName: 'List/Search App User Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-23', url: '/docSubCategoryUserMapping/createForSubCategory', featureName: 'Create App User Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-24', url: '/docSubCategoryUserMapping/updateForSubCategory', featureName: 'Update App User Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-25', url: '/docSubCategoryUserMapping/deleteForSubCategory', featureName: 'Delete App User Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-26', url: '/docSubCategoryUserMapping/selectForSubCategory', featureName: 'Select App User Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-27', url: '/docSubCategory/showSubCategories', featureName: 'Show Sub Categories', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-28', url: '/docCategory/viewCategoryDetails', featureName: 'View Category details', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-29', url: '/docSubCategory/viewSubCategoryDetails', featureName: 'View Sub Category Details', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-30', url: '/docCategoryUserMapping/dropDownAppUserForCategoryReload', featureName: 'View App User For Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-31', url: '/docSubCategoryUserMapping/dropDownAppUserForSubCategoryReload', featureName: 'View App User For Sub Category', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

        //Document DB Connection Request Map
        new RequestMap(transactionCode: 'DOC-32', url: '/docDbInstance/show', featureName: 'Show DB Instance', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-33', url: '/docDbInstance/list', featureName: 'List/Search DB Instance', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-34', url: '/docDbInstance/create', featureName: 'Create DB Instance', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-35', url: '/docDbInstance/update', featureName: 'Update DB Instance', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-36', url: '/docDbInstance/delete', featureName: 'Delete DB Instance', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-37', url: '/docDbInstance/select', featureName: 'Select DB Instance', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()


        new RequestMap(transactionCode: 'DOC-38', url: '/docInvitedMembers/showResendInvitation', featureName: 'Show resend invitation', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-39', url: '/docSubCategory/uploadDocSubCategoryDocument', featureName: 'Upload Sub Category Document', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-40', url: '/docCategory/showCategory/**', featureName: 'Show Category For Generic User', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-55', url: '/docSubCategory/showSubCategory/**', featureName: 'Show Sub Category For Generic User', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-41', url: '/docInvitedMembers/show', featureName: 'Show send Invitation', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-42', url: '/docInvitedMembers/sendInvitation', featureName: 'Send Invitation', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-43', url: '/docCategory/showAcceptInvitation', featureName: 'Show Invitation Accept Page', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-44', url: '/docCategory/acceptInvitation', featureName: 'Accept Invitation', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-45', url: '/docDbInstance/showResult', featureName: 'List/Search DB Instance', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-46', url: '/docDbInstance/listResult', featureName: 'List/Search DB Instance', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-47', url: '/docDbInstance/downloadResultCsv', featureName: 'List/Search DB Instance', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-48', url: '/docInvitedMembers/outStandingInvitations', featureName: 'List of Outstanding Invitations', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-49', url: '/docInvitedMembers/showOutStandingInvitations', featureName: 'Show Outstanding Invitations', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-50', url: '/docMemberJoinRequest/show', featureName: 'Show Requested Members', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-51', url: '/docMemberJoinRequest/applyForMembership', featureName: 'Sent Request for Membership', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-56', url: '/docMemberJoinRequest/applyForSubCategoryMembership', featureName: 'Sent Request for Membership', configAttribute: 'IS_AUTHENTICATED_ANONYMOUSLY', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-52', url: '/docMemberJoinRequest/approvedForMembership', featureName: 'Approved Request for Member', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-53', url: '/docMemberJoinRequest/searchRequestedMembers', featureName: 'Search Requested Member', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-54', url: '/docSubCategory/dropDownSubCategoryReload', featureName: 'DropDown Sub Category reload', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-57', url: '/docArticle/show', featureName: 'Show Online Articles', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.TRUE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-58', url: '/docArticle/list', featureName: 'List of Articles', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-59', url: '/docArticle/create', featureName: 'Create Article', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-60', url: '/docArticle/select', featureName: 'Select Article', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-61', url: '/docArticle/update', featureName: 'Update Article', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-62', url: '/docArticle/movedToTrash', featureName: 'Moved to Trash Article', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-63', url: '/docArticle/showTrash', featureName: 'Show Trash Articles', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-64', url: '/docArticle/listTrash', featureName: 'List Of Trash Articles', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-65', url: '/docArticle/restoreFromTrash', featureName: 'Restore Article From Trash', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-66', url: '/docArticle/delete', featureName: 'Delete Article', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

        new RequestMap(transactionCode: 'DOC-67', url: '/docArticleQuery/show', featureName: 'Show Article Query', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-68', url: '/docArticleQuery/list', featureName: 'List Article Query', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-69', url: '/docArticleQuery/create', featureName: 'Create Article Query', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-70', url: '/docArticleQuery/select', featureName: 'Select Article Query', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-71', url: '/docArticleQuery/update', featureName: 'Update Article Query', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-72', url: '/docArticleQuery/delete', featureName: 'Delete Article Query', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-73', url: '/docSubCategory/addOrRemoveSubCategoryFavourite', featureName: 'Add or Remove Sub Category to Favourite List', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()
        new RequestMap(transactionCode: 'DOC-74', url: '/docSubCategory/listSubCategoryFavourite', featureName: 'List and Search Sub Category from Favourite List', configAttribute: 'ROLE_-2', pluginId: 13, isViewable: Boolean.FALSE, isCommon: Boolean.FALSE).save()

    }

    /**
     * Method to reset default request maps by plugin Id
     * @param companyId - company id
     * @param pluginId - plugin id
     * @return - a boolean value
     */
    @Transactional
    public boolean resetDefaultRequestMapsByPluginId(long companyId, long pluginId) {
        switch (pluginId) {
            case PluginConnector.APPLICATION_ID:
                if (PluginConnector.isPluginInstalled(PluginConnector.APPLICATION)) {
                    resetRequestMapToDefault(companyId, pluginId)

                    List<Role> lstDefaultRole = roleService.findAllByCompanyIdAndRoleTypeIdNotEqual(companyId, 0)
                    for (int i = 0; i < lstDefaultRole.size(); i++) {
                        Role role = lstDefaultRole[i]
                        createApplicationRequestMap(role.authority)
                    }
                }
                break
            case PluginConnector.BUDGET_ID:
                if (PluginConnector.isPluginInstalled(PluginConnector.BUDGET)) {
                    resetRequestMapToDefault(companyId, pluginId)
                }
                break
            case PluginConnector.PROCUREMENT_ID:
                if (PluginConnector.isPluginInstalled(PluginConnector.PROCUREMENT)) {
                    resetRequestMapToDefault(companyId, pluginId)
                }
                break
            case PluginConnector.ACCOUNTING_ID:
                if (PluginConnector.isPluginInstalled(PluginConnector.ACCOUNTING)) {
                    resetRequestMapToDefault(companyId, pluginId)
                }
                break
            case PluginConnector.QS_ID:
                if (PluginConnector.isPluginInstalled(PluginConnector.QS)) {
                    resetRequestMapToDefault(companyId, pluginId)
                }
                break
            case PluginConnector.INVENTORY_ID:
                if (PluginConnector.isPluginInstalled(PluginConnector.INVENTORY)) {
                    resetRequestMapToDefault(companyId, pluginId)
                }
                break
            case PluginConnector.FIXED_ASSET_ID:
                if (PluginConnector.isPluginInstalled(PluginConnector.FIXED_ASSET)) {
                    resetRequestMapToDefault(companyId, pluginId)
                }
                break
            case PluginConnector.EXCHANGE_HOUSE_ID:
                if (PluginConnector.isPluginInstalled(PluginConnector.EXCHANGE_HOUSE)) {
                    resetRequestMapToDefault(companyId, pluginId)
                }
                break
            case PluginConnector.PROJECT_TRACK_ID:
                if (PluginConnector.isPluginInstalled(PluginConnector.PROJECT_TRACK)) {
                    resetRequestMapToDefault(companyId, pluginId)
                }
                break
            case PluginConnector.SARB_ID:
                if (PluginConnector.isPluginInstalled(PluginConnector.SARB_ID)) {
                    resetRequestMapToDefault(companyId, pluginId)
                }
                break
            case PluginConnector.ARMS_ID:
                if (PluginConnector.isPluginInstalled(PluginConnector.ARMS_ID)) {
                    resetRequestMapToDefault(companyId, pluginId)
                }
                break
            case PluginConnector.DOCUMENT_ID:
                if (PluginConnector.isPluginInstalled(PluginConnector.DOCUMENT_ID)) {
                    resetRequestMapToDefault(companyId, pluginId)
                }
                break
            default:
                break
        }
        return true
    }

    /**
     * Method to reset request map to default
     * @param companyId - company id
     * @param pluginId - plugin id
     * @return - a boolean value
     */
    private boolean resetRequestMapToDefault(long companyId, long pluginId) {
        // remove all role from request map
        List<Role> lstRole = roleService.findAllByCompanyId(companyId)
        for (int i = 0; i < lstRole.size(); i++) {
            Role role = lstRole[i]
            removeAllRoleFromRequestMap(role.authority, pluginId)
        }

        // append default role in request map
        List<Role> lstDefaultRole = roleService.findAllByCompanyIdAndRoleTypeIdNotEqual(companyId, 0)
        for (int i = 0; i < lstDefaultRole.size(); i++) {
            Role role = lstDefaultRole[i]

            List<RoleFeatureMapping> lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.roleTypeId, pluginId)
            for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                RoleFeatureMapping roleFeatureMapping = lstRoleFeatureMapping[j]
                appendDefaultRoleInRequestMap(role.authority, roleFeatureMapping.transactionCode)
            }
        }
        return true
    }

    /**
     * Method for giving access in root, logout, manage password & change password pages for default roles
     * @param role - string of role from caller method
     * @return - a boolean value
     */
    private boolean createApplicationRequestMap(String role) {
        String updateQuery = """
           UPDATE request_map
            SET config_attribute =
                CASE WHEN config_attribute LIKE '%${role},%' THEN
                    config_attribute
                WHEN config_attribute LIKE '%,${role}' THEN
                    config_attribute
                WHEN config_attribute LIKE '${role},%' THEN
                    config_attribute
                ELSE config_attribute || ',${role}'
                END
            WHERE is_common = true;
        """
        executeUpdateSql(updateQuery)
        return true
    }

    /**
     * Method to remove all role from request map
     * @param roleAuthority - string of role authority from caller method
     * @param pluginId - plugin id
     * @return - a boolean value
     */
    private boolean removeAllRoleFromRequestMap(String roleAuthority, long pluginId) {
        String queryRemoveRole = """
            UPDATE request_map
            SET config_attribute =
                CASE WHEN config_attribute LIKE '%${roleAuthority},%' THEN
                    REPLACE(config_attribute, '${roleAuthority},' , '')
                WHEN config_attribute LIKE '%,${roleAuthority}' THEN
                    REPLACE(config_attribute, ',${roleAuthority}' , '')
                WHEN config_attribute LIKE '${roleAuthority},%' THEN
                    REPLACE(config_attribute, '${roleAuthority},' , '')
                ELSE config_attribute
                END
            WHERE plugin_id = ${pluginId}
        """
        executeUpdateSql(queryRemoveRole)
        return true
    }

    /**
     * Method to append default role in request map
     * @param role - string of role from caller method
     * @param transactionCode - string of transaction code from caller method
     * @return - a boolean value
     */
    private boolean appendDefaultRoleInRequestMap(String role, String transactionCode) {

        String updateQuery = """
           UPDATE request_map
           SET
           config_attribute = config_attribute || ',${role}'
           WHERE
           transaction_code = '${transactionCode}';
        """
        executeUpdateSql(updateQuery)
        return true
    }

    public boolean appendRoleInRequestMap(long pluginId) {
        List<Role> lstRole = roleService.list()
        for (int i = 0; i < lstRole.size(); i++) {
            Role role = lstRole[i]
            createApplicationRequestMap(role.authority)

            List<RoleFeatureMapping> lstRoleFeatureMapping = roleFeatureMappingService.findAllByRoleTypeIdAndPluginId(role.roleTypeId, pluginId)
            for (int j = 0; j < lstRoleFeatureMapping.size(); j++) {
                RoleFeatureMapping roleFeatureMapping = lstRoleFeatureMapping[j]
                appendDefaultRoleInRequestMap(role.authority, roleFeatureMapping.transactionCode)
            }
        }
        return true
    }
}
