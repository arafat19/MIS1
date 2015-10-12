package com.athena.mis.document.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.service.AppUserService
import com.athena.mis.document.entity.DocAllCategoryUserMapping
import com.athena.mis.utility.DateUtility

class DocAllCategoryUserMappingService extends BaseService {

    AppUserService appUserService

    public DocAllCategoryUserMapping read(long id) {
        DocAllCategoryUserMapping docAllCategoryUserMapping = DocAllCategoryUserMapping.read(id)
        return docAllCategoryUserMapping
    }


    public DocAllCategoryUserMapping findByCategoryIdAndUserIdAndSubCategoryId(long categoryId, long userId, long subCategoryId) {
        DocAllCategoryUserMapping docAllCategoryUserMapping = DocAllCategoryUserMapping.findByCategoryIdAndUserIdAndSubCategoryId(categoryId, userId, subCategoryId)
        return docAllCategoryUserMapping
    }

    private static final String INSERT_QUERY = """
                    INSERT INTO doc_all_category_user_mapping
                        (id,version,user_id,category_id,sub_category_id,is_category_manager,is_sub_category_manager,
                         company_id,created_on,created_by,updated_by)
                    VALUES(NEXTVAL('doc_all_category_user_mapping_id_seq'),:version,:userId,:categoryId,:subCategoryId,
                            :isCategoryManager,:isSubCategoryManager,:companyId,:createdOn,:createdBy,:updatedBy)
                """


    public DocAllCategoryUserMapping create(DocAllCategoryUserMapping allCategoryUserMapping) {
        Map queryParams = [
                version: allCategoryUserMapping.version,
                userId: allCategoryUserMapping.userId,
                categoryId: allCategoryUserMapping.categoryId,
                subCategoryId: allCategoryUserMapping.subCategoryId,
                isCategoryManager: allCategoryUserMapping.isCategoryManager,
                isSubCategoryManager: allCategoryUserMapping.isSubCategoryManager,
                companyId: allCategoryUserMapping.companyId,
                createdOn: DateUtility.getSqlDateWithSeconds(allCategoryUserMapping.createdOn),
                createdBy: allCategoryUserMapping.createdBy,
                updatedBy: allCategoryUserMapping.updatedBy
        ]
        List lstUserCategoryMapping = executeInsertSql(INSERT_QUERY, queryParams)
        if (lstUserCategoryMapping.size() <= 0) {
            throw new RuntimeException("Error occurred while insert member information")
        }
        allCategoryUserMapping.id = (long) lstUserCategoryMapping[0][0]
        return allCategoryUserMapping
    }

    private static final String UPDATE_QUERY = """
                    UPDATE doc_all_category_user_mapping
                    SET
                          user_id=:userId,
                          is_category_manager=:isCategoryManager,
                          is_sub_category_manager=:isSubCategoryManager,
                          updated_by=:updatedBy,
                          updated_on=:updatedOn
                    WHERE
                          id=:id
                    """

    public int update(DocAllCategoryUserMapping docAllCategoryUserMapping) {

        Map queryParams = [
                id: docAllCategoryUserMapping.id,
                userId: docAllCategoryUserMapping.userId,
                isCategoryManager: docAllCategoryUserMapping.isCategoryManager,
                isSubCategoryManager: docAllCategoryUserMapping.isSubCategoryManager,
                updatedBy: docAllCategoryUserMapping.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(docAllCategoryUserMapping.updatedOn)
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while update member information')
        }
        return updateCount
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM doc_all_category_user_mapping
                      WHERE id=:id
                 """

    public int delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)
        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete member information')
        }
        return deleteCount
    }

    public void createDefaultDataForAllCategoryUserMapping() {
        Date currDate = new Date()
        AppUser appUser = appUserService.findByLoginId('admin@athena.com')

        DocAllCategoryUserMapping docAllCategoryUserMapping = new DocAllCategoryUserMapping(version: 0, categoryId: 1, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, isCategoryManager: true, isSubCategoryManager: false, subCategoryId: 0, updatedBy: 0, updatedOn: null, userId: 1)
        docAllCategoryUserMapping.save()

        DocAllCategoryUserMapping docAllCategoryUserMapping1 = new DocAllCategoryUserMapping(version: 0, categoryId: 2, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, isCategoryManager: false, isSubCategoryManager: true, subCategoryId: 4, updatedBy: 0, updatedOn: null, userId: 1)
        docAllCategoryUserMapping1.save()

        DocAllCategoryUserMapping docAllCategoryUserMapping2 = new DocAllCategoryUserMapping(version: 0, categoryId: 2, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, isCategoryManager: false, isSubCategoryManager: true, subCategoryId: 7, updatedBy: 0, updatedOn: null, userId: 2)
        docAllCategoryUserMapping2.save()

        DocAllCategoryUserMapping docAllCategoryUserMapping3 = new DocAllCategoryUserMapping(version: 0, categoryId: 3, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, isCategoryManager: false, isSubCategoryManager: true, subCategoryId: 8, updatedBy: 0, updatedOn: null, userId: 1)
        docAllCategoryUserMapping3.save()
    }

}
