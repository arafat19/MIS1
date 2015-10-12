/**
 * Module Name - Accounting
 * Purpose - Entity of accounting tire1 name
 **/

package com.athena.mis.accounting.entity

class AccTier1 {

    int id                          // primary key (Auto generated by its own sequence)
    int version
    String name                     // Unique name
    long accTypeId                   // AccType.id
    boolean isActive = false        // flag to consider/ignore the object
    long companyId                  // Company.id

    static mapping = {
        id generator: 'sequence', params: [sequence: 'acc_tier1_id_seq']

        accTypeId index: 'acc_tier1_acc_type_id_idx'
        companyId index: 'acc_tier1_company_id_idx'
    }

    public String toString() {
        return this.name
    }

    static constraints = {
        companyId(nullable: false)
    }
}
