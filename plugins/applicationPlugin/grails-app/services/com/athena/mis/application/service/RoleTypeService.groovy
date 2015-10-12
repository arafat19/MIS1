package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.RoleType
import com.athena.mis.application.utility.RoleTypeCacheUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

class RoleTypeService extends BaseService {

    static transactional = true
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    /**
     * Method to get role type list
     * @return - list of role type
     */
    public List list() {
        return RoleType.list(sort: roleTypeCacheUtility.SORT_ON_NAME, order: roleTypeCacheUtility.SORT_ORDER_ASCENDING, readOnly: true)
    }

    /**
     * insert application module default role_type into database when application starts with bootstrap
     */
    public void createDefaultDataForApplication() {
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-3,'Administrator','ROLE_-3')""")
    }

    /**
     * insert Budget & Procurement module default role_type into database when application starts with bootstrap
     */
    @Transactional
    public void createDefaultDataForBudgetAndProcurement() {
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-4,'Director','ROLE_-4')""")
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-5,'Project Director','ROLE_-5')""")
    }

    /**
     * insert inventory module default role_type into database when application starts with bootstrap
     */
    @Transactional
    public void createDefaultDataForInventory() {
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-6,'Project Manager','ROLE_-6')""")
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-8,'Inventory Operator','ROLE_-8')""")
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-11,'Inventory Auditor','ROLE_-11')""")
    }

    /**
     * insert accounting module default role_type into database when application starts with bootstrap
     */
    @Transactional
    public void createDefaultDataForAccounting() {
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-10,'Chief Financial Officer','ROLE_-10')""")
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-7,'Accountant','ROLE_-7')""")
    }

    /**
     * insert default developmentUser role_type into database when application starts with bootstrap
     */
    @Transactional
    public void createDefaultDataForDevelopment() {
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-12,'Development','ROLE_-12')""")
    }

    /**
     * insert exchangeHouse module default role_type into database when application starts with bootstrap
     */
    @Transactional
    public void createDefaultDataForExh(long companyId) {
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-201,'Cashier','ROLE_-201')""")
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-202,'Customer','ROLE_-202')""")
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-203,'Other Bank','ROLE_-203')""")
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-204,'Agent','ROLE_-204')""")
    }

    /**
     * insert project track module default role_type into database when application starts with bootstrap
     */
    public void createDefaultDataForPT() {
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-16,'Software Project Admin','ROLE_-16')""")
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-17,'Software Engineer','ROLE_-17')""")
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-18,'SQA Engineer','ROLE_-18')""")
    }

    /**
     * Create default role_type for ARMS
     */
    public void createDefaultDataForArms() {
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-20,'ARMS Remittance User','ROLE_-20')""")
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-21,'ARMS Branch User','ROLE_-21')""")
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-22,'ARMS ExchangeHouse User','ROLE_-22')""")
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-23,'ARMS Other Bank User','ROLE_-23')""")
    }

    /**
     * Create default role_type for Document
     */
    public void createDefaultDataForDocument() {
        executeInsertSql("""INSERT INTO role_type(id, name, authority) VALUES (-24,'Member','ROLE_-24')""")
    }
}
