package com.athena.mis.exchangehouse.service

import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhBeneficiary
import com.athena.mis.utility.DateUtility

class ExhBeneficiaryService extends BaseService {
    static transactional = false

    public ExhBeneficiary read(Long id) {
        ExhBeneficiary beneficiary = ExhBeneficiary.read(id)
        return beneficiary
    }

    /**
     * Save beneficiary object into DB
     * @param customer -ExhBeneficiary object
     * @return -saved newBeneficiary object
     */
    public ExhBeneficiary create(ExhBeneficiary beneficiary) {
        ExhBeneficiary newBeneficiary = beneficiary.save()
        return newBeneficiary
    }

    private static final String UPDATE_BENEFICIARY_QUERY =
        """
              UPDATE exh_beneficiary SET
                  version=:newVersion,
                  first_name=:firstName,
                  middle_name=:middleName,
                  last_name=:lastName,
                  photo_id_type=:photoIdType,
                  photo_id_no=:photoIdNo,
                  phone=:phone,
                  account_no=:accountNo,
                  email=:email,
                  bank=:bank,
                  bank_branch=:bankBranch,
                  district=:district,
                  thana=:thana,
                  address=:address,
                  is_sanction_exception=:isSanctionException,
                  relation=:relation,
                  approved_by =:approvedBy,
                  approved_on =:approvedOn,
                  updated_by =:updatedBy,
                  updated_on=:updatedOn
              WHERE
                  id=:id AND
                  version=:oldVersion
        """
    /**
     * Update beneficiary object in DB
     * @param beneficiary -ExhBeneficiary object
     * @return -updated beneficiary object
     */
    public Integer update(ExhBeneficiary beneficiary) {

        Map queryParams = [
                newVersion: beneficiary.version + 1,
                firstName: beneficiary.firstName,
                middleName: beneficiary.middleName,
                lastName: beneficiary.lastName,
                photoIdType: beneficiary.photoIdType,
                photoIdNo: beneficiary.photoIdNo,
                phone: beneficiary.phone,
                accountNo: beneficiary.accountNo,
                email: beneficiary.email,
                bank: beneficiary.bank,
                bankBranch: beneficiary.bankBranch,
                district: beneficiary.district,
                isSanctionException: beneficiary.isSanctionException,
                thana: beneficiary.thana,
                address: beneficiary.address,
                relation: beneficiary.relation,
                approvedBy: beneficiary.approvedBy,
                approvedOn: DateUtility.getSqlDateWithSeconds(beneficiary.approvedOn),
                updatedBy: beneficiary.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(beneficiary.updatedOn),
                id: beneficiary.id,
                oldVersion: beneficiary.version
        ]

        int updateCount = executeUpdateSql(UPDATE_BENEFICIARY_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Beneficiary")
        }
        beneficiary.version = beneficiary.version + 1
        return (new Integer(updateCount));
    }

//    // Method used to update customer name of beneficiary while customer is updated
//    public Integer updateForCustomerUpdate(ExhCustomer customer) {
//        String query = """UPDATE exh_beneficiary SET
//                          customer_name=:customerName
//                      WHERE
//                          customer_id=${customer.id}"""
//        Map queryParams = [customerName: customer.name]
//
//        int updateCount = executeUpdateSql(query, queryParams);
//        // @todo: Customer may/may not have beneficiary, optimize this query by implementing beneficiary count in customer
///*        if (updateCount <= 0) {
//            throw new RuntimeException("Failed to update Beneficiary")
//        }*/
//        return (new Integer(updateCount));
//    }

    /*//checking association of beneficiary
    public String isAssociated(ExhBeneficiary beneficiary) {
        Long beneficiaryId = beneficiary.id;
        String beneficiaryName = beneficiary.firstName;
        Integer count = 0;

        // has task
        count = countTask(beneficiaryId)
        if (count.intValue() > 0) return Tools.getMessageOfAssociation(beneficiaryName, count, Tools.DOMAIN_TASK);

        return null;
    }*/

    /*//count number of row in task table by beneficiary id
    private int countTask(Long beneficiaryId) {
        String query = """
            SELECT COUNT(id) as count
            FROM exh_task
            WHERE beneficiary_id = ${beneficiaryId}
        """
        List beneficiaryCount = executeSelectSql(query);
        int count = beneficiaryCount[0].count;
        return count;
    }*/
}
