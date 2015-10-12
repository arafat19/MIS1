/**
 * Module Name - QsMeasurement
 * Purpose - Entity of Qs Measurement information
 **/

package com.athena.mis.qs.entity

class QsMeasurement {

    long id
    int version
    long budgetId                           // Budget.id
    long projectId                          // Project.id
    double quantity                         // QS Measurement quantity
    String comments

    long siteId                             // inventory id of type site
    Date qsMeasurementDate                  // QS measurement date
    boolean isGovtQs                       // is Government Qs or Internal Qs

    long companyId                          // Company.id
    Date createdOn                          // Object creation DateTime
    long createdBy                          // AppUser.id
    Date updatedOn                          // Object updated DateTime
    long updatedBy                          // AppUser.id

    static constraints = {
        qsMeasurementDate(nullable: false)
        isGovtQs(nullable: false)
        updatedOn(nullable: true)
        comments(nullable: true)
    }

    static mapping = {
        id generator: 'sequence', params: [sequence: 'qs_measurement_id_seq']
        quantity sqlType: "numeric(16,4)"
        qsMeasurementDate type: 'date'

        budgetId index: 'qs_measurement_budget_id_idx'
        projectId index: 'qs_measurement_project_id_idx'
        siteId index: 'qs_measurement_site_id_idx'
        companyId index: 'qs_measurement_company_id_idx'
        isGovtQs index: 'qs_measurement_is_govt_qs_idx'
        qsMeasurementDate index: 'qs_measurement_qs_measurement_date_idx'
        quantity index: 'qs_measurement_quantity_idx'
        createdBy index: 'qs_measurement_created_by_idx'
        updatedBy index: 'qs_measurement_updated_by_idx'
    }

    static namedQueries = {

        getQsSumOfBudget { long budgetId, boolean isGovtQs ->
            eq('budgetId', new Long(budgetId))
            eq('isGovtQs', new Boolean(isGovtQs))
            projections {
                sum('quantity')
            }
        }
    }
}
