package com.athena.mis.exchangehouse.entity


/**
 ExhCustomerBeneficiaryMapping entity is used to map different ExhCustomer with ExhBeneficiary
 * */

class ExhCustomerBeneficiaryMapping implements Serializable {
   long customerId              // ExhCustomer.id
   long beneficiaryId           // ExhBeneficiary.id

   static mapping = {
       version false
       id composite: ['customerId', 'beneficiaryId']
       customerId index: 'exh_customer_beneficiary_mapping_customer_id_idx'
       beneficiaryId index: 'exh_customer_beneficiary_mapping_beneficiary_id_idx'
   }

   static constraints = {
       customerId(nullable: false)
       beneficiaryId(nullable: false)
   }
}
