package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Role
import com.athena.mis.application.utility.RoleCacheUtility
import com.athena.mis.application.utility.AppSessionUtil
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Service class for basic role CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'RoleService'
 */
class RoleService extends BaseService {

    static transactional = false

    @Autowired
    RoleCacheUtility roleCacheUtility
    @Autowired
    AppSessionUtil appSessionUtil

    private static final String SORT_ON_NAME = "name"

    /**
     * @return -list of role object
     */
    public List list() {
        return Role.list(sort: roleCacheUtility.NAME, order: roleCacheUtility.SORT_ORDER_ASCENDING, readOnly: true)
    }

    /**
     * Method to find the role object
     * @param companyId - company id
     * @param roleTypeId - role type id
     * @return - an object of role
     */
    public Role findByCompanyIdAndRoleTypeId(long companyId, long roleTypeId) {
        Role roleObject = Role.findByCompanyIdAndRoleTypeId(companyId, roleTypeId, [readOnly: true])
        return roleObject
    }

    /**
     * Method to count the role
     * @param roleName - role name
     * @param companyId - company id
     * @return - an integer value of role count
     */
    public int countByNameIlikeAndCompanyId(String roleName, long companyId) {
        int countDuplicate = Role.countByNameIlikeAndCompanyId(roleName, companyId)
        return countDuplicate
    }

    /**
     * @return -a map contains search result list and count
     */
    public Map search(BaseService bs) {
        List<Role> roleList = Role.withCriteria {
            ilike(bs.queryType, Tools.PERCENTAGE + bs.query + Tools.PERCENTAGE)
            eq('companyId', appSessionUtil.getCompanyId())
            maxResults(bs.resultPerPage)
            firstResult(bs.start)
            order(SORT_ON_NAME, ASCENDING_SORT_ORDER)
            setReadOnly(true)
        } as List

        List counts = Role.withCriteria {
            ilike(bs.queryType, Tools.PERCENTAGE + bs.query + Tools.PERCENTAGE)
            eq('companyId', appSessionUtil.getCompanyId())
            projections { rowCount() }
        }

        Integer total = (Integer) counts[0]
        return [roleList: roleList, count: total]
    }

    /**
     * Method to count role
     * @param name - role name
     * @param companyId - company id
     * @param id - role id
     * @return - an integer value of role count
     */
    public int countByNameIlikeAndCompanyIdAndIdNotEqual(String name, long companyId, long id) {
        int countDuplicate = Role.countByNameIlikeAndCompanyIdAndIdNotEqual(name, companyId, id)
        return countDuplicate
    }

    /**
     * Method to find the list of all role
     * @param companyId - company id
     * @param roleTypeId - role type id
     * @return - a list of role
     */
    public List<Role> findAllByCompanyIdAndRoleTypeIdNotEqual(long companyId, long roleTypeId) {
        List<Role> lstDefaultRole = Role.findAllByCompanyIdAndRoleTypeIdNotEqual(companyId, roleTypeId, [readOnly: true])
        return lstDefaultRole
    }

    /**
     * Method to find all role
     * @param companyId - company id
     * @return - a list of role
     */
    public List<Role> findAllByCompanyId(long companyId) {
        List<Role> lstRole = Role.findAllByCompanyId(companyId, [readOnly: true])
        return lstRole
    }

    private static final String INSERT_QUERY = """
            INSERT INTO role(id, version, authority, name, company_id, role_type_id)
            VALUES (:id,:version,:authority,:name, :companyId, :roleTypeId);
            """
    /**
     * SQL to save role object in database
     * @param role -Role object
     * @return -newly created role object
     */
    public Role create(Role role) {
        Map queryParams = [
                id: role.id,
                version: role.version,
                authority: role.authority,
                name: role.name,
                companyId: role.companyId,
                roleTypeId: role.roleTypeId
        ]
        List result = executeInsertSql(INSERT_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert role information')
        }
        return role
    }

    private static final String UPDATE_QUERY = """
                    UPDATE role SET
                          version= :newVersion,
                          name = :name
                    WHERE
                          id=:id AND
                          version=:version
                          """
    /**
     * SQL to update role object in database
     * @param role -Role object
     * @return -int value(updateCount)
     */
    public int update(Role role) {
        Map queryParams = [
                newVersion: role.version + 1,
                name: role.name,
                id: role.id,
                version: role.version
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update role')
        }
        return updateCount
    }

    /**
     * get specific role object from cache by id
     * @param id -Role.id
     * @return -role object
     */
    public Role read(long id) {
        Role role = Role.read(id)
        return role
    }

    private static final String QUERY_STR = """
                        DELETE FROM role
                          WHERE id=:id
                      """
    /**
     * SQL to delete role object from database
     * @param id -Role.id
     * @return -boolean value
     */
    public Boolean delete(long id) {
        Map queryParams = [
                id: id
        ]
        int deleteCount = executeUpdateSql(QUERY_STR, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException("error occurred at roleService.delete")
        }
        return Boolean.TRUE
    }

    /**
     * insert application module default role into database when application starts with bootstrap
     */
    public void createDefaultDataForApplication(long companyId) {
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-3,0,-3,'ROLE_-3_${companyId}','Administrator', ${companyId})""")
    }

    /**
     * insert Budget & Procurement module default role into database when application starts with bootstrap
     */
    public void createDefaultDataForBudgetAndProcurement(long companyId) {
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-4,0,-4,'ROLE_-4_${companyId}','Director',${companyId})""")
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-5,0,-5,'ROLE_-5_${companyId}','Project Director',${companyId})""")
    }

    /**
     * insert inventory module default role into database when application starts with bootstrap
     */

    public void createDefaultDataForInventory(long companyId) {
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-6,0,-6,'ROLE_-6_${companyId}','Project Manager',${companyId})""")
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-8,0,-8,'ROLE_-8_${companyId}','Inventory Operator',${companyId})""")
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-11,0,-11,'ROLE_-11_${companyId}','Inventory Auditor',${companyId})""")
    }

    /**
     * insert accounting module default role into database when application starts with bootstrap
     */
    public void createDefaultDataForAccounting(long companyId) {
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-10,0,-10,'ROLE_-10_${companyId}','Chief Financial Officer',${companyId})""")
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-7,0,-10,'ROLE_-7_${companyId}','Accountant',${companyId})""")
    }

    /**
     * insert default developmentUser role into database when application starts with bootstrap
     */
    public void createDefaultDataForDevelopment(long companyId) {
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-12,0,-12,'ROLE_-12_${companyId}','Development',${companyId})""")
    }

    /**
     * insert exchangeHouse module default role into database when application starts with bootstrap
     */
    @Transactional
    public void createDefaultDataForExh(long companyId) {
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-201,0,-201,'ROLE_-201_${companyId}','Cashier',${
            companyId
        })""")
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-202,0,-202,'ROLE_-202_${companyId}','Customer',${
            companyId
        })""")
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-203,0,-203,'ROLE_-203_${companyId}','Other Bank',${
            companyId
        })""")
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-204,0,-204,'ROLE_-204_${companyId}','Agent',${
            companyId
        })""")

    }

    /**
     * insert project track module default role into database when application starts with bootstrap
     */
    public void createDefaultDataForPT(long companyId) {
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-16,0,-16,'ROLE_-16_${companyId}','Software Project Admin',${companyId})""")
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-17,0,-17,'ROLE_-17_${companyId}','Software Engineer',${companyId})""")
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-18,0,-18,'ROLE_-18_${companyId}','SQA Engineer',${companyId})""")
    }

    /**
     * create default role for ARMS
     */
    public void createDefaultDataForArms(long companyId) {
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-20,0,-20,'ROLE_-20_${companyId}','ARMS Remittance User',${companyId})""")
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-21,0,-21,'ROLE_-21_${companyId}','ARMS Branch User',${companyId})""")
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-22,0,-22,'ROLE_-22_${companyId}','ARMS ExchangeHouse User',${companyId})""")
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-23,0,-23,'ROLE_-23_${companyId}','ARMS Other Bank User',${companyId})""")
    }

    /**
     * create default role for Document
     */
    public void createDefaultDataForDocument(long companyId) {
        executeInsertSql("""INSERT INTO role (id,version,role_type_id,authority,name,company_id) VALUES(-24,0,-24,'ROLE_-24_${companyId}','Member',${companyId})""")
    }
}
