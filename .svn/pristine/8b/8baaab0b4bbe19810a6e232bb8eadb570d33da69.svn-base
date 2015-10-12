package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccIouPurpose
import com.athena.mis.utility.DateUtility

/**
 *  Service class for basic IouPurpose CRUD (Create, Update, Delete)
 *  For details go through Use-Case doc named 'AccIouPurposeService'
 */
class AccIouPurposeService extends BaseService {

    static transactional = false

    /**
     * get AccIouPurpose object by id
     * @param id -AccIouPurpose.id
     * @return -AccIouPurpose object
     */
    public AccIouPurpose read(long id) {
        return AccIouPurpose.read(id)
    }

    /**
     * Save AccIouPurpose object in database
     * @param accIouPurpose -AccIouPurpose object
     * @return -newly created accIouPurpose object
     */
    public AccIouPurpose create(AccIouPurpose accIouPurpose) {
        AccIouPurpose newAccIouPurpose = accIouPurpose.save(false)
        return newAccIouPurpose
    }

    private static final String QUERY_UPDATE = """
              UPDATE acc_iou_purpose
                SET version =:newVersion,
                    amount =:amount,
                    indent_details_id =:indentDetailsId,
                    comments =:comments,
                    updated_by =:updatedBy,
                    updated_on =:updatedOn
              WHERE
                  id =:id AND
                  version =:version
            """
    /**
     * SQL to update AccIouPurpose object in database
     * @param accIouPurpose -AccIouPurpose object
     * @return -updated AccIouPurpose object
     */
    public AccIouPurpose update(AccIouPurpose accIouPurpose) {
        Map queryParams = [
                id: accIouPurpose.id,
                version: accIouPurpose.version,
                newVersion: accIouPurpose.version + 1,
                amount: accIouPurpose.amount,
                indentDetailsId: accIouPurpose.indentDetailsId,
                comments: accIouPurpose.comments,
                updatedBy: accIouPurpose.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(accIouPurpose.updatedOn)
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update IOU purpose')
        }
        accIouPurpose.version = accIouPurpose.version + 1
        return accIouPurpose
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM acc_iou_purpose
                      WHERE id = :id
                          """
    /**
     * Delete AccIouPurpose object by id
     * @param id -AccIouPurpose.id
     * @return -boolean value
     */
    public boolean delete(long id) {
        int success = executeUpdateSql(QUERY_DELETE, [id: id])
        if (!success) {
            throw new RuntimeException('Failed to delete IOU purpose')
        }
        return true
    }
}
