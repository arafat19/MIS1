package com.athena.mis.procurement.service

import com.athena.mis.BaseService
import com.athena.mis.procurement.entity.ProcIndentDetails
import com.athena.mis.utility.DateUtility

class IndentDetailsService extends BaseService {

    static transactional = false

    /**
     * Method to read procurement indent details by id
     * @param id - ProcIndentDetails.id
     * @return - procurement indent details object
     */
    public ProcIndentDetails read(long id) {
        ProcIndentDetails indentDetails = ProcIndentDetails.read(id)
        return indentDetails
    }

    private static final String PROC_INDENT_DETAILS_CREATE_QUERY = """
            INSERT INTO proc_indent_details(id,version,item_description,unit,comments,created_by,created_on,
                              indent_id,project_id,quantity,rate,updated_by,updated_on,company_id)
            VALUES(NEXTVAL('proc_indent_details_id_seq'),0,:itemDescription,:unit,:comments,:createdBy,
            :createdOn,:indentId,:projectId,
            :quantity,:rate,0,null,:companyId)
        """

    /**
     * Method to save ProcIndentDetails object
     * @param indentDetails -ProcIndentDetails object
     * @return - newly created ProcIndentDetails object
     */
    public ProcIndentDetails create(ProcIndentDetails indentDetails) {
        Map queryParams = [
                itemDescription: indentDetails.itemDescription,
                unit: indentDetails.unit,
                comments: indentDetails.comments,
                createdBy: indentDetails.createdBy,
                indentId: indentDetails.indentId,
                projectId: indentDetails.projectId,
                quantity: indentDetails.quantity,
                rate: indentDetails.rate,
                companyId: indentDetails.companyId,
                createdOn: DateUtility.getSqlDateWithSeconds(indentDetails.createdOn)
        ]

        List result = executeInsertSql(PROC_INDENT_DETAILS_CREATE_QUERY, queryParams)
        if (result.size() <= 0) {  // if the result size is <= 0 then it will throw exception
            throw new RuntimeException('Error occurred while creating indent_details')
        }
        int indentDetailsId = (int) result[0][0]
        if (indentDetailsId > 0) {  // if indentDetailsId > 0 then the method returns the object of ProcIndentDetails otherwise it returns null
            indentDetails.id = indentDetailsId
            return indentDetails
        }
        return null
    }

    private static final String PROC_INDENT_DETAILS_UPDATE_QUERY = """
                UPDATE  proc_indent_details
                SET version =:newVersion,
                    item_description=:itemDescription,
                    unit=:unit,
                    quantity =:quantity,
                    rate=:rate,
                    updated_by=:updatedBy,
                    comments=:comments,
                    updated_on= :updatedOn
                WHERE id=:id AND
                      version =:version
        """
    /**
     * Method to update ProcIndentDetails object
     * @param indentDetails - ProcIndentDetails object
     * @return - updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(ProcIndentDetails indentDetails) {
        Map queryParams = [
                id: indentDetails.id,
                version: indentDetails.version,
                newVersion: indentDetails.version + 1,
                itemDescription: indentDetails.itemDescription,
                unit: indentDetails.unit,
                quantity: indentDetails.quantity,
                rate: indentDetails.rate,
                updatedBy: indentDetails.updatedBy,
                comments: indentDetails.comments,
                updatedOn: DateUtility.getSqlDateWithSeconds(indentDetails.updatedOn)
        ]
        int updateCount = executeUpdateSql(PROC_INDENT_DETAILS_UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update indent_details')
        }
        return updateCount
    }

    private static final String DELETE_QUERY = """
                                            DELETE FROM proc_indent_details
                                                WHERE id=:id
                                        """

    /**
     * Method to delete ProcIndentDetails object
     * @param id - ProcIndentDetails.id
     * @return - if deleteCount <= 0 then throw exception to rollback transaction
     */
    public int delete(long id) {
        int deleteCount = executeUpdateSql(DELETE_QUERY, [id: id])

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete indent_details')
        }
        return deleteCount
    }
}
