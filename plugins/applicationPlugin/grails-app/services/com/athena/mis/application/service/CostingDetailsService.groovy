package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.CostingDetails
import com.athena.mis.application.utility.CostingDetailsCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

class CostingDetailsService extends BaseService {

    static transactional = false

    @Autowired
    CostingDetailsCacheUtility costingDetailsCacheUtility

    public CostingDetails read(long id) {
        CostingDetails costingDetails = CostingDetails.read(id)
        return costingDetails
    }

    public List list() {
        return CostingDetails.list(sort: costingDetailsCacheUtility.SORT_ON_NAME, order: costingDetailsCacheUtility.SORT_ORDER_ASCENDING, readOnly: true);
    }

    private static final String QUERY_CREATE = """
            INSERT INTO costing_type(id,version,name,description,company_id,created_on,created_by,updated_by,updated_on)
            VALUES (NEXTVAL('costing_details_id_seq'),:version,:description,:costingTypeId,:companyId,:createdOn,:createdBy,:updatedBy,null,:costingAmount,:costingDate);
            """
    /**
     * Create new costingType
     * @param costingType -costingType  object
     * @return -create vehicle if saved successfully, otherwise throw RuntimeException
     */
    public CostingDetails create(CostingDetails costingDetails) {

        Map queryParams = [
                version         : costingDetails.version,
                description     : costingDetails.description,
                costingTypeId   : costingDetails.costingTypeId,
                companyId       : costingDetails.companyId,
                createdBy       : costingDetails.createdBy,
                createdOn       : DateUtility.getSqlDateWithSeconds(costingDetails.createdOn),
                updatedBy       : costingDetails.updatedBy,
                costingAmount   : costingDetails.costingAmount,
                costingDate     : DateUtility.getSqlDateWithSeconds(costingDetails.createdOn)

        ]

        List result = executeInsertSql(QUERY_CREATE, queryParams)

        if (result.size() <= 0) {
            throw new RuntimeException('Error occurred while insert costing Type information')
        }

        int costingTypeId = (int) result[0][0]
        costingType.id = costingTypeId
        return costingType
    }
}
