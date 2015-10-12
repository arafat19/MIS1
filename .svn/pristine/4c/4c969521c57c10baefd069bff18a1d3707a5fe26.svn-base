package com.athena.mis.qs.service

import com.athena.mis.BaseService
import com.athena.mis.qs.entity.QsMeasurement
import com.athena.mis.utility.DateUtility

class QsMeasurementService extends BaseService {
    static transactional = false
    private static final String QS_MEASUREMENT_CREATE_QUERY = """
            INSERT INTO qs_measurement(id, version, budget_id, comments, company_id, created_by,
                 created_on, project_id, quantity, qs_measurement_date, site_id, updated_by, updated_on, is_govt_qs)
            VALUES (NEXTVAL('qs_measurement_id_seq'), :version, :budgetId, :comments, :companyId, :createdBy,
                :createdOn, :projectId, :quantity,
                :qsMeasurementDate, :siteId, :updatedBy, null, :isGovtQs);
                """

    public QsMeasurement create(QsMeasurement qsMeasurement) {
        Map queryParams = [
                version: 0,
                budgetId: qsMeasurement.budgetId,
                comments: qsMeasurement.comments,
                companyId: qsMeasurement.companyId,
                createdBy: qsMeasurement.createdBy,
                projectId: qsMeasurement.projectId,
                quantity: qsMeasurement.quantity,
                siteId: qsMeasurement.siteId,
                updatedBy: qsMeasurement.updatedBy,
                isGovtQs: qsMeasurement.isGovtQs,
                createdOn: DateUtility.getSqlDateWithSeconds(qsMeasurement.createdOn),
                qsMeasurementDate: DateUtility.getSqlDate(qsMeasurement.qsMeasurementDate)
        ]
        List result = executeInsertSql(QS_MEASUREMENT_CREATE_QUERY, queryParams)
        if (result.size() <= 0) {
            throw new RuntimeException("Fail to create QS Measurement")
        }
        int qsMeasurementId = (int) result[0][0]
        qsMeasurement.id = qsMeasurementId

        return qsMeasurement
    }

    private static final String QS_MEASUREMENT_UPDATE_QUERY = """
                    UPDATE qs_measurement
                      SET
                          budget_id =:budgetId,
                          qs_measurement_date =:qsMeasurementDate,
                          comments =:comments,
                          project_id =:projectId,
                          quantity =:quantity,
                          updated_on =:updatedOn,
                          updated_by =:updatedBy,
                          site_id =:siteId,
                          version =:newVersion
                      WHERE
                          id =:id AND
                          version =:version
                    """

    public int update(QsMeasurement qsMeasurement) {
        Map queryParams = [
                id: qsMeasurement.id,
                version: qsMeasurement.version,
                newVersion: qsMeasurement.version + 1,
                projectId: qsMeasurement.projectId,
                budgetId: qsMeasurement.budgetId,
                quantity: qsMeasurement.quantity,
                updatedBy: qsMeasurement.updatedBy,
                comments: qsMeasurement.comments,
                siteId: qsMeasurement.siteId,
                updatedOn: DateUtility.getSqlDateWithSeconds(qsMeasurement.updatedOn),
                qsMeasurementDate: DateUtility.getSqlDate(qsMeasurement.qsMeasurementDate)
        ]
        int updateCount = executeUpdateSql(QS_MEASUREMENT_UPDATE_QUERY, queryParams)

        if (updateCount <= 0) {
            throw new RuntimeException("Fail to update Qs Measurement")
        }
        return updateCount
    }

    // Delete Qs Measurement
    private static final String QUERY_DELETE = """
                    DELETE FROM qs_measurement
                    WHERE
                        id =:id
                    """

    public int delete(QsMeasurement qsMeasurement) {
        Map queryParams = [id: qsMeasurement.id]
        int deleteCount = executeUpdateSql(QUERY_DELETE, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException("Fail to delete Qs Measurement")
        }
        return deleteCount
    }

}
