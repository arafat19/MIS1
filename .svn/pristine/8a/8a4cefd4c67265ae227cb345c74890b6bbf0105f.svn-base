package com.athena.mis.application.utility

import com.athena.mis.ExtendedCacheUtility
import com.athena.mis.application.entity.Customer
import com.athena.mis.application.service.CustomerService
import com.athena.mis.utility.Tools
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 *  For details go through Use-Case doc named 'CustomerCacheUtility'
 */
@Component('customerCacheUtility')
class CustomerCacheUtility extends ExtendedCacheUtility {

    @Autowired
    CustomerService customerService

    public final String SORT_ON_NAME = "fullName";

    /**
     * pull all list of customer and store list in cache
     */
    public void init() {
        List list = customerService.list();
        super.setList(list)
    }

    // used to populate drop-down with full name
    public List listByCompanyForDropDown() {
        List<Customer> lstCustomer = this.list()
        Map customCustomer
        List result = []
        for (int i = 0; i < lstCustomer.size(); i++) {
            customCustomer = [id: lstCustomer[i].id,
                    name: lstCustomer[i].fullName + Tools.PARENTHESIS_START + lstCustomer[i].id + Tools.PARENTHESIS_END]
            result << customCustomer
        }
        return result
    }
}
