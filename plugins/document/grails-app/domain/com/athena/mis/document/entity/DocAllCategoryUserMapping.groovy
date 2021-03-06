package com.athena.mis.document.entity

class DocAllCategoryUserMapping {

    long id                         //Primary Key (Auto Generated By it's own sequence)
    int version
    long userId                     //AppUser.id
    long categoryId                 //DocCategory.id
    long subCategoryId              //DocSubCategory.id
    boolean isCategoryManager       //true/false category manager
    boolean isSubCategoryManager    //true/false sub category manager
    long companyId                  //Company.id
    long createdBy                  //AppUser.id
    Date createdOn                  //Created Datetime
    long updatedBy                  //AppUser.id
    Date updatedOn                  //updated Datetime

    static mapping = {
        id generator: 'sequence', params: [sequence: 'doc_all_category_user_mapping_id_seq']
        userId index: 'doc_all_category_user_mapping_user_id_idx'
        categoryId index: 'doc_all_category_user_mapping_category_id_idx'
        subCategoryId index: 'doc_all_category_user_mapping_sub_category_id_idx'
        companyId index: 'doc_all_category_user_mapping_company_id_idx'
        createdBy index: 'doc_all_category_user_mapping_created_by_idx'
        updatedBy index: 'doc_all_category_user_mapping_updated_by_idx'
    }
    static constraints = {
        userId(nullable: false)
        categoryId(nullable: false)
        subCategoryId(nullable: false)
        isCategoryManager(nullable: false)
        isSubCategoryManager(nullable: false)
        companyId(nullable: false)
        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
        userId(unique: ['categoryId', 'subCategoryId', 'companyId'])  //userId unique against categoryId,subCategoryId,companyId
    }
}
