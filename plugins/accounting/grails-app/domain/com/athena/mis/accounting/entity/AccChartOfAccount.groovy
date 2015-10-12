/**
 * Module Name - Accounting
 * Purpose - Entity of accounting head name
 * */

package com.athena.mis.accounting.entity

class AccChartOfAccount {
    long id                         // primary key (Auto generated by its own sequence)
    int version                     // keeps count of object modification
    String description              // head name of chart of account
    long accSourceId = 0L           // SystemEntity.id (Type= AccSource)
    long sourceCategoryId = 0L      // e.g. Item.itemTypeId, Employee.designationId etc
    long accTypeId = 0              // AccType.id
    long accGroupId = 0             // AccGroup.id
    long accCustomGroupId = 0       // AccCustomerGroup.id
    int tier1 = 0                   // AccTire1.id
    int tier2 = 0                   // AccTire2.id
    int tier3 = 0                   // AccTire3.id
    int tier4 = 0                   // AccTire4.id
    int tier5 = 0                   // AccTire5.id
    boolean isActive = false        // flag to consider/ignore the object
    long createdBy                  // AppUser.id
    Date createdOn                  // Object creation DateTime
    String code                     // autoGenerated (prefix of AccType + 4 digit ID)
    long companyId                  // Company.id

    static mapping = {
        id generator: 'sequence', params: [sequence: 'acc_chart_of_account_id_seq']
        code index: 'acc_chart_of_account_code_idx'
        accSourceId index: 'acc_chart_of_account_acc_source_id_idx'
        sourceCategoryId index: 'acc_chart_of_account_source_category_id_idx'
        accTypeId index: 'acc_chart_of_account_acc_type_id_idx'
        accGroupId index: 'acc_chart_of_account_acc_group_id_idx'
        accCustomGroupId index: 'acc_chart_of_account_acc_custom_group_id_idx'
        tier1 index: 'acc_chart_of_account_tier1_idx'
        tier2 index: 'acc_chart_of_account_tier2_idx'
        tier3 index: 'acc_chart_of_account_tier3_idx'
        tier4 index: 'acc_chart_of_account_tier4_idx'
        tier5 index: 'acc_chart_of_account_tier5_idx'
        isActive index: 'acc_chart_of_account_is_active_idx'
        createdBy index: 'acc_chart_of_account_created_by_idx'
        companyId index: 'acc_chart_of_account_company_id_idx'
    }

    static constraints = {
        description(nullable: false)
        code(nullable: false)
        accSourceId(nullable: false)
        sourceCategoryId(nullable: false)
        accTypeId(nullable: false)
        accGroupId(nullable: false)
        accCustomGroupId(nullable: false)
        tier1(nullable: false)
        tier2(nullable: false)
        tier3(nullable: false)
        isActive(nullable: false)
        createdOn(nullable: false)
        companyId(nullable: false)
        companyId unique: 'code'
    }

    public String toString() {
        return this.code
    }
}
