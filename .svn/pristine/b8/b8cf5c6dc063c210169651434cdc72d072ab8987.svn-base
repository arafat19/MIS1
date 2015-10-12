package com.athena.mis.accounting.service

import com.athena.mis.BaseService
import com.athena.mis.accounting.entity.AccIouSlip
import com.athena.mis.utility.DateUtility

class AccIouSlipService extends BaseService {

    static transactional = false

    public AccIouSlip read(long id) {
        return AccIouSlip.read(id)
    }

    public AccIouSlip create(AccIouSlip accIouSlip) {
        AccIouSlip newAccIouSlip = accIouSlip.save(false)
        if (newAccIouSlip) {
            return newAccIouSlip
        }
        return null
    }

    private static final String QUERY_UPDATE = """
                    UPDATE acc_iou_slip SET
                          version=:newVersion,
                          employee_id=:employeeId,
                          project_id=:projectId,
                          indent_id=:indentId,
                          updated_on=:updatedOn,
                          updated_by=:updatedBy
                      WHERE
                          id=:id AND
                          version=:version
                          """
    public AccIouSlip update(AccIouSlip accIouSlip) {

        Map queryParams = [
                id: accIouSlip.id,
                newVersion: accIouSlip.version + 1,
                employeeId: accIouSlip.employeeId,
                projectId: accIouSlip.projectId,
                indentId: accIouSlip.indentId,
                updatedBy: accIouSlip.updatedBy,
                version: accIouSlip.version,
                updatedOn: DateUtility.getSqlDateWithSeconds(accIouSlip.updatedOn)
        ]
        int updateCount = executeUpdateSql(QUERY_UPDATE, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Failed to update IOU slip')
        }
        return accIouSlip
    }

    private static final String QUERY_DELETE = """
                    DELETE FROM acc_iou_slip
                      WHERE
                          id=:id
                          """
    public Boolean delete(long id) {
        int success = executeUpdateSql(QUERY_DELETE, [id: id])
        if (!success) {
            throw new RuntimeException('IOU delete failed')
        }
        return Boolean.TRUE
    }

    public Boolean delete(AccIouSlip accIouSlip) {
        accIouSlip.delete()
        return Boolean.TRUE
    }
}
