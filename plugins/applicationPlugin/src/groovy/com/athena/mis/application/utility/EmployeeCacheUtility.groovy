package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.Employee
import com.athena.mis.application.service.EmployeeService
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component('employeeCacheUtility')
class EmployeeCacheUtility extends ExtendedCacheUtility {

    @Autowired
    EmployeeService employeeService

    public final String SORT_ON_NAME = "fullName";

    public void init() {
        List list = employeeService.list();
        super.setList(list)
    }

    // used to populate drop-down with full name
    public List listByCompanyForDropDown() {
        List<Employee> lstEmployee = this.list()
        Map customEmployee
        List result = []
        for (int i = 0; i < lstEmployee.size(); i++) {
            customEmployee = [id: lstEmployee[i].id,
                    name: lstEmployee[i].fullName + Tools.PARENTHESIS_START + lstEmployee[i].id + Tools.PARENTHESIS_END]
            result << customEmployee
        }
        return result
    }

    // used to populate drop-down by designation
    public List listByDesignationForDropDown(long designationId) {
        List<Employee> lstEmployee = this.list()
        Map customEmployee
        List result = []
        for (int i = 0; i < lstEmployee.size(); i++) {
            if (lstEmployee[i].designationId == designationId) {
                customEmployee = [id: lstEmployee[i].id,
                        name: lstEmployee[i].fullName + Tools.PARENTHESIS_START + lstEmployee[i].id + Tools.PARENTHESIS_END]
                result << customEmployee
            }
        }
        return result
    }
}
