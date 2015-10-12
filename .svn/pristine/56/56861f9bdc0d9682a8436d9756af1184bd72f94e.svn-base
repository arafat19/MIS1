package com.athena.mis.application.utility

import com.athena.mis.CacheUtility
import com.athena.mis.application.service.RoleTypeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component('roleTypeCacheUtility')
class RoleTypeCacheUtility extends CacheUtility {

    @Autowired
    RoleTypeService roleTypeService

    /**
     * WHY RESERVED ROLE TYPE ALWAYS NEGATIVE ?
     * Reserved role types should be negative. Because when we create role type by application -
     * it takes its id from sequence(starts from 1. positive value).
     * So if we set reserved role types negative, it never contrast -
     * with non-reserved role type(created by application).
     */

    // constants for user Role
    public static final long ROLE_TYPE_ADMIN = -3
    public static final long ROLE_TYPE_DIRECTOR = -4
    public static final long ROLE_TYPE_PROJECT_DIRECTOR = -5
    public static final long ROLE_TYPE_PROJECT_MANAGER = -6
    public static final long ROLE_TYPE_ACCOUNTANT = -7
    public static final long ROLE_TYPE_CFO = -10
    public static final long ROLE_TYPE_INVENTORY_AUDITOR = -11
    public static final long ROLE_TYPE_DEVELOPMENT_USER = -12
    // only for Project Track
    public static final long ROLE_SOFTWARE_PROJECT_ADMIN = -16
    public static final long ROLE_SOFTWARE_ENGINEER = -17
    public static final long ROLE_SQA_ENGINEER = -18

    // requestMap id for root page which will be mapped with role on role-create
    public static final int REQUEST_MAP_ROOT = -2
    public static final int REQUEST_MAP_LOGOUT = -3

    public static final int MANAGE_PASSWORD = -13
    public static final int CHECK_PASSWORD = -14
    public static final int CHANGE_PASSWORD = -15

    // for exchangeHouse plugin (range -201 to -300)
    public static final long ROLE_TYPE_EXH_CASHIER = -201        //-4
    public static final long ROLE_TYPE_EXH_CUSTOMER = -202       //-5
    public static final long ROLE_TYPE_EXH_OTHER_BANK = -203     //-6
    public static final long ROLE_TYPE_EXH_AGENT = -204          //-7

    //Only for ARMS
    public static final long ROLE_ARMS_REMITTANCE_USER = -20
    public static final long ROLE_ARMS_BRANCH_USER = -21
    public static final long ROLE_ARMS_EXCHANGE_HOUSE_USER = -22
    public static final long ROLE_ARMS_OTHER_BANK_USER = -23

    //Only for Document
    public static final long ROLE_TYPE_DOCUMENT_MEMBER = -24

    static final String SORT_ON_NAME = "name"

    @Transactional(readOnly = true)
    public void init() {
        List list = roleTypeService.list()
        super.setList(list)
    }
}
