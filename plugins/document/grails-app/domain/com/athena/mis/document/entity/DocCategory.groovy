package com.athena.mis.document.entity

class DocCategory {

    long id                   //Primary Key (Auto Generated By it's own sequence)
    int version
    String name               //Name of the Category
    int subCategoryCount      //Count of Sub Category
    String description        //Description of the Category
    String urlInName          //Category url parameter name
    String url                //Category URL
    boolean isActive          //Active/Inactive Category
    long companyId            //Company.id
    Date createdOn            //Created Datetime
    long createdBy            //AppUser.id
    Date updatedOn            //Updated datetime
    long updatedBy            //AppUser.id


    static mapping = {
        id generator: 'sequence', params: [sequence: 'doc_category_id_seq']
        companyId index: 'doc_category_company_id_idx'
        createdBy index: 'doc_category_created_by_idx'
        updatedBy index: 'doc_category_updated_by_idx'
    }
    static constraints = {
        name(nullable: false)
        subCategoryCount(nullable: false)
        description(nullable: true)
        urlInName(nullable: false)
        url(nullable: false)
        isActive(nullable: false)
        companyId(nullable: false)
        createdOn(nullable: false)
        createdBy(nullable: false)
        updatedOn(nullable: true)
        updatedBy(nullable: false)
        name unique: 'companyId'    //category name unique against companyId
    }
}
