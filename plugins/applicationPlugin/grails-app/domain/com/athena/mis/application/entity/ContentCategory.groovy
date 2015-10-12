package com.athena.mis.application.entity

class ContentCategory {

    long id                 // primary key (Auto generated by its own sequence)
    int version
    String name             // Content Category Name(Signature,Image, PhotoId, Drawing, Achieve etc.)
    long contentTypeId      // SystemEntity.id (Document, Image)
    String systemContentCategory   // Type of Content(Photo, Signature)
    long maxSize            // Maximum file size (e.g : 1024*1024*1, 1024*1024*5 etc.)
    int width              // width of the Content(e.g : 512etc.)
    int height             // height of the Content(e.g : 1024etc.)
    String extension        /* Coma separated Extension of the content (e.g : doc, excel, pdf, png, jpeg, gif etc)
                               If extension is defined then Content should be exactly as declared,
                               Otherwise Content could be any type like doc, xls, pdf, png, jpeg, gif etc.
                            */

    Date createdOn       // Object created DateTime
    long createdBy       // AppUser.id
    Date updatedOn       // Object updated DateTime
    long updatedBy       // AppUser.id
    long companyId       // Company.id
    boolean isReserved = false

    static constraints = {
        name(nullable: false)
        contentTypeId(nullable: false)
        systemContentCategory(nullable: false)
        maxSize(nullable: false)
        width(nullable: false)
        height(nullable: false)
        extension(nullable: true)
        createdBy(nullable: false)
        createdOn(nullable: false)
        updatedBy(nullable: false)
        updatedOn(nullable: true)
        companyId(nullable: false)
        isReserved(nullable: false)
        systemContentCategory unique: 'companyId'
    }

    static mapping = {
        id generator: 'sequence', params: [sequence: 'content_category_id_seq']
        contentTypeId index: 'content_category_content_type_id_idx'
        createdBy index: 'content_category_created_by_idx'
        updatedBy index: 'content_category_updated_by_idx'
        companyId index: 'content_category_company_id_idx'
    }

}