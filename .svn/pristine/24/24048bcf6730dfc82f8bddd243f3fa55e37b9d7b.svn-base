package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Customer
import com.athena.mis.application.utility.CustomerCacheUtility
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Service class for basic customer CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'CustomerService'
 */
class CustomerService extends BaseService {

    static transactional = false
    @Autowired
    CustomerCacheUtility customerCacheUtility

    /**
     * @return -list of customer
     */
    public List list() {
        return Customer.list(sort: customerCacheUtility.SORT_ON_NAME, order: customerCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }
    private static final String INSERT_QUERY =
        """
            INSERT INTO customer(id, version,company_id,full_name,nick_name,email,date_of_birth,address,phone_no,created_on,created_by,updated_by,updated_on)
            VALUES (NEXTVAL('customer_id_seq'),:version,:companyId,:fullName,:nickName,:email,:dateOfBirth,
            :address,:phoneNo,:createdOn,:createdBy,:updatedBy,null);
        """
    /**
     * SQL to save customer object in database
     * @param customer -Customer object
     * @return -newly created customer object
     */
    public Customer create(Customer customer) {
        Map queryParams = [
                version: customer.version,
                companyId: customer.companyId,
                fullName: customer.fullName,
                nickName: customer.nickName,
                email: customer.email,
                address: customer.address,
                dateOfBirth: DateUtility.getSqlDate(customer.dateOfBirth),
                phoneNo: customer.phoneNo,
                createdOn: DateUtility.getSqlDateWithSeconds(customer.createdOn),
                createdBy: customer.createdBy,
                updatedBy: customer.updatedBy
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert customer information')
        }

        int customerId = (int) result[0][0]
        customer.id = customerId
        return customer
    }

    /**
     * get specific customer object from cache by id
     * @param id -Customer.id
     * @return -Customer object
     */
    public Customer read(long id) {
        return (Customer) customerCacheUtility.read(id);
    }

    private static final String UPDATE_QUERY =
                     """
                        UPDATE customer SET
                            version=:newVersion,
                            company_id=:companyId,
                            full_name=:fullName,
                            nick_name=:nickName,
                            email=:email,
                            date_of_birth=:dateOfBirth,
                            address=:address,
                            phone_no=:phoneNo,
                            updated_on=:updatedOn,
                            updated_by=:updatedBy
                        WHERE id=:id AND
                          version=:version
                     """
    /**
     * SQL to update customer object in database
     * @param customer -Customer object
     * @return -int value(updateCount)
     */
    public int update(Customer customer) {
        Map queryParams = [
                id: customer.id,
                version: customer.version,
                newVersion: customer.version + 1,
                companyId: customer.companyId,
                fullName: customer.fullName,
                nickName: customer.nickName,
                email: customer.email,
                dateOfBirth: DateUtility.getSqlDate(customer.dateOfBirth),
                address: customer.address,
                phoneNo: customer.phoneNo,
                updatedBy: customer.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(customer.updatedOn)
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update customer information')
        }
        return updateCount;
    }
    private static final String DELETE_QUERY =
                """
                    DELETE FROM customer
                       WHERE
                          id=:id
                """
    /**
     * SQL to delete customer object from database
     * @param id -Customer.id
     * @return -boolean value
     */
    public Boolean delete(long id) {
        Map queryParams = [id: id]
        log.debug(query)
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete customer information')
        }
        return Boolean.TRUE;
    }
}
