package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Role
import com.athena.mis.application.entity.UserRole
import com.athena.mis.application.utility.RoleCacheUtility
import com.athena.mis.application.utility.RoleTypeCacheUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Service class for basic User-Role mapping CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'UserRoleService'
 */
class UserRoleService extends BaseService {
    static transactional = false
    AppUserService appUserService
    @Autowired
    RoleCacheUtility roleCacheUtility
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    /**
     * Save userRole object in database
     * @param userRole -UserRole object
     * @return -newly created userRole object
     */
    public UserRole create(UserRole userRole) {
        UserRole newUserRole = userRole.save()
        return newUserRole
    }

    /**
     * SQL to update userRole object in database
     * @param oldUserRole -UserRole object
     * @param userId -AppUser.id
     * @return -int value(updateCount)
     */
    public boolean update(UserRole oldUserRole, long userId) {
        String query = """UPDATE user_role SET
                          user_id=:userId
                      WHERE
                          user_id=:oldUserId AND
                          role_id=:oldRoleId"""

        Map queryParams = [userId: userId, oldUserId: oldUserRole.user.id, oldRoleId: oldUserRole.role.id]
        int updateCount = executeUpdateSql(query, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("error occurred at userRoleService.update")
        }
        return true
    }

    /**
     * get assigned roleList of a specific AppUser
     * @param userId -AppUser.id
     * @return -list of role
     */

    public List<Role> getRolesByUser(long userId) {
        AppUser appUser = appUserService.read(userId)
        List<Role> lstRoles = UserRole.findAllByUser(appUser, [readOnly: true]).collect { it.role }
        return lstRoles
    }

    /**
     * get specific UserRole object
     * @param userId -AppUser.id
     * @param roleId -Role.id
     * @return -UserRole object
     */

    public UserRole read(long userId, long roleId) {
        Role role = (Role) roleCacheUtility.read(roleId)
        AppUser appUser = appUserService.read(userId)
        UserRole userRole = UserRole.findByUserAndRole(appUser, role, [readOnly: true])
        return userRole
    }

    /**
     * get specific UserRole object
     * @param userId -AppUser.id
     * @param roleId -Role.id
     * @param companyId - Company.id
     * @return -UserRole object
     */

    public UserRole read(long userId, long roleId, long companyId) {
        Role role = (Role) roleCacheUtility.read(roleId, companyId)
        AppUser appUser = appUserService.read(userId)
        UserRole userRole = UserRole.findByUserAndRole(appUser, role, [readOnly: true])
        return userRole
    }


    /**
     * delete specific userRole object from DB
     * @param userRole -UserRole object
     * @return boolean value (true/false)
     */
    public Boolean delete(UserRole userRole) {
        if (!userRole) {
            return new Boolean(false)
        }
        userRole.delete()
        return new Boolean(true)
    }

    /**
     * insert default UserRole object for application plugin when application starts with bootstrap
     */
    public void createDefaultDataForApplication() {
        AppUser adminUser = appUserService.findByLoginId('admin@athena.com')
        AppUser superAdmin = appUserService.findByLoginId('super@athena.com')

        if (adminUser) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${adminUser.id},-3)""")
        if (superAdmin) {
            executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${superAdmin.id},-3)""")
            executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${superAdmin.id},-12)""")
        }
    }

    /**
     * insert default UserRole objects for Budget & Procurement plugin when application starts with bootstrap
     */

    public void createDefaultDataForBudgetAndProcurement() {
        AppUser userDirector = appUserService.findByLoginId('dir@athena.com')
        AppUser userProjectDirector = appUserService.findByLoginId('pd@athena.com')
        AppUser superAdmin = appUserService.findByLoginId('admin@athena.com')

        if (userDirector) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userDirector.id},-4)""")
        if (userProjectDirector) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userProjectDirector.id},-5)""")
        if (superAdmin) {
            executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${superAdmin.id},-4)""")
            executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${superAdmin.id},-5)""")
        }
    }

    /**
     * insert default UserRole objects for inventory plugin when application starts with bootstrap
     */
    public void createDefaultDataForInventory() {
        AppUser userProjectManager = appUserService.findByLoginId('pm@athena.com')
        AppUser userInventoryOperator = appUserService.findByLoginId('io@athena.com')
        AppUser superAdmin = appUserService.findByLoginId('admin@athena.com')

        if (userProjectManager) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userProjectManager.id},-6)""")
        if (userInventoryOperator) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userInventoryOperator.id},-8)""")
        if (superAdmin) {
            executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${superAdmin.id},-6)""")
            executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${superAdmin.id},-8)""")
            executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${superAdmin.id},-11)""")
        }
    }

    /**
     * insert default UserRole objects for Accounting plugin when application starts with bootstrap
     */

    public void createDefaultDataForAccounting() {
        AppUser userCFO = appUserService.findByLoginId('cfo@athena.com')
        AppUser superAdmin = appUserService.findByLoginId('admin@athena.com')

        if (userCFO) {
            executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userCFO.id},-7)""")
            executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userCFO.id},-10)""")
        }
        if (superAdmin) {
            executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${superAdmin.id},-7)""")
            executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${superAdmin.id},-10)""")
        }
    }

    /**
     * insert default UserRole objects for exchangeHouse plugin when application starts with bootstrap
     */
    public void createDefaultDataExchangeHouse() {
        AppUser userCashier = appUserService.findByLoginId('cashier@athena.com')
        if (userCashier) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userCashier.id},${roleTypeCacheUtility.ROLE_TYPE_EXH_CASHIER})""")

        AppUser userAgent = appUserService.findByLoginId('agent@athena.com')
        if (userCashier) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userAgent.id},${roleTypeCacheUtility.ROLE_TYPE_EXH_AGENT})""")

        AppUser userOtherBank = appUserService.findByLoginId('other@athena.com')
        if (userOtherBank) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userOtherBank.id},${roleTypeCacheUtility.ROLE_TYPE_EXH_OTHER_BANK})""")
    }

    /**
     * Create default data for ARMS
     */
    public void createDefaultDataForArms() {

        AppUser userRemittance = appUserService.findByLoginId('remittance@athena.com')
        AppUser userAdmin = appUserService.findByLoginId('admin@athena.com')
        if (userRemittance) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userRemittance.id},${RoleTypeCacheUtility.ROLE_ARMS_REMITTANCE_USER})""")
        if (userAdmin) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userAdmin.id},${RoleTypeCacheUtility.ROLE_ARMS_REMITTANCE_USER})""")

        AppUser userBranch = appUserService.findByLoginId('branch_banani@athena.com')
        if (userBranch) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userBranch.id},${RoleTypeCacheUtility.ROLE_ARMS_BRANCH_USER})""")

        AppUser userExh = appUserService.findByLoginId('exh@athena.com')
        if (userExh) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userExh.id},${RoleTypeCacheUtility.ROLE_ARMS_EXCHANGE_HOUSE_USER})""")

        AppUser userOtherBank = appUserService.findByLoginId('other_gulshan@athena.com')
        if (userOtherBank) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${userOtherBank.id},${RoleTypeCacheUtility.ROLE_ARMS_OTHER_BANK_USER})""")
    }

    /**
     * insert default UserRole objects for Document plugin when application starts with bootstrap
     */
    public void createDefaultDataDocument() {
        AppUser member = appUserService.findByLoginId('member@athena.com')
        if (member) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${member.id},${RoleTypeCacheUtility.ROLE_TYPE_DOCUMENT_MEMBER})""")
    }

    /**
     * insert default UserRole object for project track plugin when application starts with bootstrap
     */
    public void createDefaultDataForProjectTrack() {
        AppUser adminUser = appUserService.findByLoginId('sadmin@athena.com')
        AppUser engineer = appUserService.findByLoginId('sengineer@athena.com')
        AppUser qa = appUserService.findByLoginId('sqa@athena.com')

        if (adminUser) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${adminUser.id},-16)""")
        if (engineer) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${engineer.id},-17)""")
        if (qa) executeInsertSql("""INSERT INTO user_role (user_id,role_id) VALUES(${qa.id},-18)""")
    }
}
