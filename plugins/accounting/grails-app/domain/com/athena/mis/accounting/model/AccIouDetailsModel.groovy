package com.athena.mis.accounting.model

// AccIouDetailsModel is the model for database view vw_acc_iou_details,
// which is primarily used to list IOU purpose

class AccIouDetailsModel implements Serializable {

    public static final String SQL_ACC_IOU_DETAILS_MODEL = """
            DROP TABLE IF EXISTS vw_acc_iou_details;
            DROP VIEW IF EXISTS vw_acc_iou_details;
            CREATE OR REPLACE view vw_acc_iou_details AS
            SELECT aip.acc_iou_slip_id AS iou_id, aip.id AS iou_purpose_id, p.name AS project_name, indent_details.item_description AS purpose_description,
            aip.amount AS purpose_amount, to_char(aip.amount,'FM৳ 99,99,999,99,99,990.00') AS str_purpose_amount
            FROM acc_iou_purpose aip
            LEFT JOIN proc_indent_details indent_details ON indent_details.id = aip.indent_details_id
            LEFT JOIN acc_iou_slip ais ON ais.id= aip.acc_iou_slip_id
            LEFT JOIN project p ON p.id= ais.project_id
   """

    long iouId                      // accIouPurpose.accIouSlipId
    long iouPurposeId               // accIouPurpose.id
    String purposeDescription      // accIouPurpose.description
    String projectName              // project.name
    double purposeAmount            // accIouPurpose.amount
    String strPurposeAmount         // formatted purposeAmount

    static mapping = {
        table 'vw_acc_iou_details'  //database view
        version false
        id composite: ['iouId', 'iouPurposeId']
        cache usage: 'read-only'
    }

    static namedQueries = {
        listPurposeByIou { long iouId ->
            eq('iouId', iouId)
        }
    }
}
