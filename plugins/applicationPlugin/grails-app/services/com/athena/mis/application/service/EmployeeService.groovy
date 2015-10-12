package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.Employee
import com.athena.mis.application.utility.EmployeeCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

/**
 * EmployeeService is used to handle only CRUD related object manipulation (e.g. list, read, create, delete etc.)
 */
class EmployeeService extends BaseService {

    static transactional = false
    @Autowired
    EmployeeCacheUtility employeeCacheUtility

    /**
     * @return - list of employee
     */
    public List list() {
        return Employee.list(sort: employeeCacheUtility.SORT_ON_NAME, order: employeeCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    private static final String INSERT_QUERY =
        """
            INSERT INTO employee(id, version, address, company_id, created_by, created_on,
                    date_of_birth, date_of_join, email, full_name, nick_name,designation_id, mobile_no, updated_by)
            VALUES (NEXTVAL('employee_id_seq'),:version, :address, :companyId, :createdBy,:createdOn,
                    :dateOfBirth ,:dateOfJoin,:email, :fullName, :nickName, :designationId, :mobileNo, :updatedBy);
    """

    /**
     * Save employee object into DB
     * @param employee -employee object
     * @return -saved employee object
     */
    public Employee create(Employee employee) {
        Map queryParams = [
                version: employee.version,
                address: employee.address,
                companyId: employee.companyId,
                email: employee.email,
                fullName: employee.fullName,
                nickName: employee.nickName,
                designationId: employee.designationId,
                mobileNo: employee.mobileNo,
                createdBy: employee.createdBy,
                createdOn: DateUtility.getSqlDateWithSeconds(employee.createdOn),
                dateOfBirth: DateUtility.getSqlDate(employee.dateOfBirth),
                dateOfJoin: DateUtility.getSqlDate(employee.dateOfJoin),
                updatedBy: employee.updatedBy,
        ]

        List result = executeInsertSql(INSERT_QUERY, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert employee information')
        }

        int employeeId = (int) result[0][0]
        employee.id = employeeId
        return employee
    }

    private static final String UPDATE_QUERY =
        """
                    UPDATE employee SET
                          version=:newVersion,
                          address=:address,
                          date_of_birth=:dateOfBirth,
                          email=:email,
                          full_name=:fullName,
                          nick_name=:nickName,
                          designation_id=:designationId,
                          mobile_no=:mobileNo,
                          updated_on=:updatedOn,
                          date_of_join=:dateOfJoin,
                          updated_by=:updatedBy
                    WHERE
                          id=:id AND
                          version=:version
    """

    /**
     * Update employee object in DB
     * @param employee -employee object
     * @return -an integer containing the value of update count
     */
    public int update(Employee employee) {

        Map queryParams = [
                id: employee.id,
                newVersion: employee.version + 1,
                version: employee.version,
                address: employee.address,
                dateOfBirth: DateUtility.getSqlDate(employee.dateOfBirth),
                dateOfJoin: DateUtility.getSqlDate(employee.dateOfJoin),
                email: employee.email,
                fullName: employee.fullName,
                nickName: employee.nickName,
                designationId: employee.designationId,
                mobileNo: employee.mobileNo,
                createdBy: employee.createdBy,
                updatedBy: employee.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(employee.updatedOn)
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);

        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update employee information')
        }
        employee.version = employee.version + 1
        return updateCount;
    }

    private static final String DELETE_QUERY =
        """
                    DELETE FROM employee
                       WHERE
                          id=:id
    """

    /**
     * Delete employee object from DB
     * @param id -id of employee object
     * @return -an integer containing the value of delete count
     */
    public int delete(long id) {

        Map queryParams = [
                id: id
        ]

        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete employee information')
        }
        return deleteCount;
    }

    /**
     * applicable only for create default employee
     */
    public void createDefaultData(long companyId) {
        new Employee(fullName: 'Mr. Rahim Khan', nickName: 'Mr. Khan', dateOfJoin: new Date(), designationId: 1, companyId: companyId, createdBy: 1, createdOn: new Date()).save()
    }
}
