package com.athena.mis.projecttrack.service

import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtModule
import com.athena.mis.projecttrack.utility.PtSessionUtil
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired

/**
 * PtModuleService is used to handle only CRUD related object manipulation (e.g. list, read,search, create, delete etc.)
 */

class PtModuleService extends BaseService {

    @Autowired
    PtSessionUtil ptSessionUtil

    /**
     * return all list of ptModule object
     */
    public List list() {
        List lstModule = PtModule.list(readOnly: true)
        return lstModule
    }

    /**
     * Segmented/Sub list of ptModule objects based on baseService pagination criteria
     * @param baseService - BaseService object
     * @return a map containing [lstPtModule - List of PtModule objects , count: int count of ptModule objects]
     */
    public Map list(BaseService baseService) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        List lstModule = PtModule.withCriteria {
            eq('companyId', companyId)
            setReadOnly(true)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
            // order(baseService.sortColumn, baseService.sortOrder)
        }

        List counts = PtModule.withCriteria {
            eq('companyId', companyId)
            projections { rowCount() }
        }

        int total = counts[0]
        return [lstModule: lstModule, count: total.toInteger()]
    }

    private static final String INSERT_QUERY = """
        INSERT INTO pt_module(id, version, company_id, name, code)
        VALUES ( NEXTVAL('pt_module_id_seq'), :version, :companyId, :name, :code);
    """

    /**
     * Save PtModule object into DB
     * @param module - PtModule object
     * @return - saved ptModule object
     */
    public PtModule create(PtModule module) {
        Map queryParams = [
                version: module.version,
                companyId: module.companyId,
                name: module.name,
                code: module.code
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert module information')
        }
        int moduleId = (int) result[0][0]
        module.id = moduleId
        return module
    }

    /**
     * read single ptModule object and return the object
     * @param id - ptModule id
     * @return ptModule object
     */

    public PtModule read(long id) {
        PtModule module = PtModule.read(id)
        return module
    }

    private static final String UPDATE_QUERY =
            """
			UPDATE pt_module SET
				  version=:newVersion,
				  name=:name,
				  code=:code
			WHERE
				  id=:id AND
				  version=:version
	"""

    /**
     * update ptModule object in DB
     * @param module - PtModule Object
     * @return - int containing no of update count
     */
    public int update(PtModule module) {
        Map queryParams = [
                id: module.id,
                newVersion: module.version + 1,
                version: module.version,
                name: module.name,
                code: module.code
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update module information')
        }
        module.version = module.version + 1
        return updateCount
    }

    private static final String DELETE_QUERY =
            """
			DELETE FROM pt_module
			   WHERE
				  id=:id
    """

    /**
     * Delete ptModule object from DB
     * @param id -id of ptModule object
     * @return -an integer containing the value of delete count
     */
    public int delete(long id) {

        Map queryParams = [
                id: id
        ]

        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete module information')
        }
        return deleteCount;
    }

    /**
     * Search Sub list of ptModule objects based on baseService pagination & search criteria
     * @param baseService - BaseService object
     * @return a map containing [lstPtModule - List of PtModule objects , count: int count of PtModule objects]
     */

    public Map search(BaseService baseService) {
        long companyId = ptSessionUtil.appSessionUtil.getCompanyId()
        String searchQuery = Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE
        List lstModule = PtModule.withCriteria {
            eq('companyId', companyId)
            ilike(baseService.queryType, searchQuery)
            maxResults(baseService.resultPerPage)
            firstResult(baseService.start)
        }
        List countResult = PtModule.withCriteria {
            ilike(baseService.queryType, searchQuery)
            projections { rowCount() }
        }
        Integer count = (Integer) countResult[0]
        return [lstPtModule: lstModule, count: count]
    }

    /**
     * Count total id if name exists
     * @param ptModule - object after execute buildObject
     * @return count
     */
    public int countByNameAndCompanyId(PtModule ptModule) {
        int count = PtModule.countByNameAndCompanyId(ptModule.name, ptModule.companyId)
        return count
    }

    /**
     * Count total id if name exists
     * @param ptModule -object after execute buildObject
     * @return count
     */
    public int countByIdNotEqualAndNameAndCompanyId(PtModule ptModule) {
        int count = PtModule.countByIdNotEqualAndNameAndCompanyId(ptModule.id, ptModule.name, ptModule.companyId)
        return count
    }

    /**
     * Get list of modules by ids
     * @param lstModuleIds -list of ids
     * @return -a list of module objects
     */
    public List<PtModule> findAllByIdInList(List<Long> lstModuleIds) {
        List<PtModule> lstModule = PtModule.findAllByIdInList(lstModuleIds)
        return lstModule
    }

    public void createDefaultDataForPtModule(long companyId) {
        new PtModule(version: 0, companyId: companyId, name: 'Budget', code: 'Budget').save()
        new PtModule(version: 0, companyId: companyId, name: 'Procurement', code: 'Procurement').save()
        new PtModule(version: 0, companyId: companyId, name: 'Inventory', code: 'Inventory').save()
        new PtModule(version: 0, companyId: companyId, name: 'Accounting', code: 'Accounting').save()
        new PtModule(version: 0, companyId: companyId, name: 'Fixed Asset', code: 'Fixed Asset').save()
        new PtModule(version: 0, companyId: companyId, name: 'QS', code: 'QS').save()
        new PtModule(version: 0, companyId: companyId, name: 'Application', code: 'Application').save()
        new PtModule(version: 0, companyId: companyId, name: 'ARMS', code: 'ARMS').save()
        new PtModule(version: 0, companyId: companyId, name: 'SARB', code: 'SARB').save()
        new PtModule(version: 0, companyId: companyId, name: 'Exchange House', code: 'Exchange House').save()
    }
}
