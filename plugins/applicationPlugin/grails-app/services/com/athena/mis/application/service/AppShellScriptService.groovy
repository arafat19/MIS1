package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppShellScript
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

// AppShellScriptService is used to handle only CRUD related object manipulation (e.g. read, create, update, delete etc.)
class AppShellScriptService extends BaseService {

    private final String SORT_ON_NAME = "name";

    @Autowired
    AppSessionUtil appSessionUtil

    /**
     * @return - a map contain list of shellScript object
     */
    public Map list() {
        long companyId = appSessionUtil.appUser.getCompanyId()
        List<AppShellScript> lstShellScript = AppShellScript.findAllByCompanyId(companyId, [max: resultPerPage, offset: start, sort: SORT_ON_NAME, order: ASCENDING_SORT_ORDER, readOnly: true])
        int count = AppShellScript.countByCompanyId(companyId)
        return [lstShellScript: lstShellScript, count: count]
    }

    /*
    * Insert ShellScript Object
    * @params appShellScript - AppShellScript object
    * @return docShellScriptObj - saved AppShellScript object
    * */

    public AppShellScript create(AppShellScript appShellScript) {
        AppShellScript appShellScriptObj = appShellScript.save()
        return appShellScriptObj
    }
    /*
    * Retrive ShellScript object by id
    * @params id - object id
    * @return appShellScript - DB instance object
    * */

    public AppShellScript read(long id) {
        AppShellScript appShellScript = AppShellScript.read(id)
        return appShellScript
    }

    public static final String UPDATE_QUERY = """
        UPDATE app_shell_script SET
            version=version+1,
            name=:name,
            script=:script,
            updated_on=:updatedOn,
            updated_by=:updatedBy
        WHERE
            id=:id AND
            version=:oldVersion
    """

    /*
    * Update ShellScript object
    * @params appShellScript - ShellScript object for update
    * @return updateCount - count of updated object
    * */

    public int update(AppShellScript appShellScript) {
        Map queryParams = [
                id        : appShellScript.id,
                oldVersion: appShellScript.version,
                name      : appShellScript.name,
                script    : appShellScript.script,
                updatedOn : DateUtility.getSqlDateWithSeconds(appShellScript.updatedOn),
                updatedBy : appShellScript.updatedBy
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Shell Script")
        }

        appShellScript.version = appShellScript.version + 1
        return updateCount
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM app_shell_script WHERE id=:id
                 """

    /*
    * Delete ShellScript object by id
    * @params id - object id
    * @return deleteCount - count of deleted object
    * */

    public int delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete Shell Script information')
        }
        return deleteCount;
    }
}
