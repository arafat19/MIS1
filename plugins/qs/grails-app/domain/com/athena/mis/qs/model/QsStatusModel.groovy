package com.athena.mis.qs.model

class QsStatusModel implements Serializable {

    public static final String SQL_QS_STATUS_MODEL = """
            DROP TABLE IF EXISTS vw_qs_status;
            DROP VIEW IF EXISTS vw_qs_status;
            CREATE OR REPLACE view vw_qs_status AS
            SELECT id AS project_id, version AS project_version, code AS project_code,
            TO_CHAR(((SELECT COALESCE(sum(quantity),0) FROM qs_measurement WHERE project_id = project.id AND is_govt_qs='f')
            / (SELECT COALESCE(sum(budget_quantity),1) FROM budg_budget WHERE project_id = project.id and billable = 't') )*100,'FM99,99,999,99,99,990.0099') AS str_achieved_intern,
            TO_CHAR((SELECT COALESCE(SUM(qsm.quantity*budget.contract_rate),0) FROM qs_measurement qsm
            LEFT JOIN budg_budget budget ON budget.id = qsm.budget_id
            WHERE qsm.project_id = project.id
            AND qsm.is_govt_qs = 'f'),'FM৳ 99,99,999,99,99,990.0099') AS work_certified_intern
            FROM project
            ORDER BY code;
    """

    long projectId                      //project.id
    int projectVersion                      //project.version
    String projectCode                  //project.code
    String strAchievedIntern                 //str_achieved_intern
    String workCertifiedIntern               //work_certified_intern


    static mapping = {
        table 'vw_qs_status'  //database view
        version false
        id composite: ['projectId', 'projectVersion']
        cache usage: 'read-only'
    }

    static namedQueries = {

        listByProjectIds { List<Long> projectIds ->
            'in'('projectId', projectIds)
        }
    }
}
