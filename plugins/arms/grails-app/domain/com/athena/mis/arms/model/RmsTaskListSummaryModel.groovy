package com.athena.mis.arms.model

import com.athena.mis.arms.utility.RmsTaskStatusCacheUtility
import com.athena.mis.integration.application.AppDefaultDataBootStrapService

class RmsTaskListSummaryModel {

    private static final long companyId = AppDefaultDataBootStrapService.companyId

    public static final String SQL_TASK_LIST_SUMMARY = """
    DROP TABLE IF EXISTS vw_rms_task_list_summary_model;
    DROP VIEW IF EXISTS vw_rms_task_list_summary_model;
    CREATE OR REPLACE VIEW vw_rms_task_list_summary_model AS
    SELECT task_list.id AS task_list_id,task_list.name AS task_list_name, exh.id AS exchange_house_id,
    exh.name AS exchange_house_name, SUM(included_in_list) AS included_in_list_count,
    SUM(decision_taken) AS decision_taken_count, SUM(decision_approved) AS decision_approved_count,
    SUM(included_in_list+decision_taken+decision_approved) AS total_count,
    task_list.created_on, created_by
    FROM rms_task_list task_list
    LEFT JOIN(
    SELECT task_list_id,
    CASE WHEN task.current_status = (SELECT id FROM system_entity sys_en WHERE sys_en.reserved_id = ${
        RmsTaskStatusCacheUtility.INCLUDED_IN_LIST
    } and company_id = ${companyId}) THEN 1 ELSE 0 END AS included_in_list,
    CASE WHEN task.current_status = (SELECT id FROM system_entity sys_en WHERE sys_en.reserved_id = ${
        RmsTaskStatusCacheUtility.DECISION_TAKEN
    } and company_id = ${companyId}) THEN 1 ELSE 0 END AS  decision_taken,
    CASE WHEN task.current_status = (SELECT id FROM system_entity sys_en WHERE sys_en.reserved_id = ${
        RmsTaskStatusCacheUtility.DECISION_APPROVED
    } and company_id = ${companyId}) THEN 1 ELSE 0 END AS decision_approved
    FROM rms_task task
    WHERE task.task_list_id <> 0 AND company_id = ${companyId}
    ) count_table
    ON count_table.task_list_id = task_list.id
    LEFT JOIN rms_exchange_house exh ON exh.id = task_list.exchange_house_id
    GROUP BY task_list.id, task_list.name, created_on, created_by, exh.id, exh.name
    ORDER BY task_list_id
    """

    long taskListId             // RmsTaskList.id
    String taskListName         // RmsTaskList.name
    long exchangeHouseId        // RmsTaskList.exchangeHouseId
    String exchangeHouseName    // RmsExchangeHouse.name
    int includedInListCount     // count of RmsTask with current status INCLUDED_IN_LIST
    int decisionTakenCount      // count of RmsTask with current status DECISION_TAKEN
    int decisionApprovedCount   // count of RmsTask with current status DECISION_APPROVED
    int totalCount              // sum of includedInListCount, decisionTakenCount, decisionApprovedCount
    Date createdOn              // RmsTaskList.createdOn
    long createdBy              // RmsTaskList.createdBy

    static mapping = {
        table "vw_rms_task_list_summary_model"
        version false
        cache usage: "read-only"
        id name: 'taskListId'
    }
}
