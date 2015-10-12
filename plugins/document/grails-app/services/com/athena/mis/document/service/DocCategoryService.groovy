package com.athena.mis.document.service

import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Company
import com.athena.mis.application.service.AppUserService
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired

class DocCategoryService extends BaseService {

    AppUserService appUserService
    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility


    public List<DocCategory> list() {
        List<DocCategory> categoryList = DocCategory.list(
                sort: docCategoryCacheUtility.DEFAULT_SORT_ORDER,
                order: docCategoryCacheUtility.SORT_ORDER_ASCENDING,
                readOnly: true
        )

        return categoryList
    }

    private static final String INSERT_QUERY = """
                    INSERT INTO doc_category
                        (id,version,name,sub_category_count,description,url_in_name,url,is_active,company_id,created_on,created_by,updated_by)
                    VALUES(NEXTVAL('doc_category_id_seq'),:version,:name,:subCategoryCount,
                            :description,:urlInName,:url,:isActive,:companyId,:createdOn,:createdBy,:updatedBy)
                """
    /**
     *   Save new category to DB
     * @params category - category object for save
     * @return saved new category object
     * */

    public List create(DocCategory category) {
        Map queryParams = [
                version: category.version,
                name: category.name,
                subCategoryCount: category.subCategoryCount,
                description: category.description,
                urlInName: category.urlInName,
                url: category.url,
                isActive: category.isActive,
                companyId: category.companyId,
                createdOn: DateUtility.getSqlDateWithSeconds(category.createdOn),
                createdBy: category.createdBy,
                updatedBy: category.updatedBy
        ]
        List lstCategory = executeInsertSql(INSERT_QUERY, queryParams)
        if (lstCategory.size() <= 0) {
            throw new RuntimeException("Error occurred while insert category information")
        }

        int categoryId = (int) lstCategory[0][0]
        category.id = categoryId

        return lstCategory
    }

    /**
     * @params id - Category id
     * @return - A category object
     * */

    public DocCategory read(long id) {
        DocCategory category = DocCategory.read(id)
        return category
    }

    public static final String UPDATE_QUERY = """
        UPDATE doc_category SET
            version=version+1,
            name=:name,
            sub_category_count=:subCategoryCount,
            description=:description,
            url_in_name=:urlInName,
            url=:url,
            is_active=:isActive,
            updated_on=:updatedOn,
            updated_by=:updatedBy
        WHERE
            id=:id AND
            version=:oldVersion
    """

    /**
     * Update Category object in DB
     * @param category - Category object
     * @return -an int containing the value of update count
     */
    public int update(DocCategory category) {
        Map queryParams = [
                name: category.name,
                subCategoryCount: category.subCategoryCount,
                description: category.description,
                urlInName: category.urlInName,
                url: category.url,
                id: category.id,
                oldVersion: category.version,
                isActive: category.isActive,
                updatedOn: DateUtility.getSqlDateWithSeconds(category.updatedOn),
                updatedBy: category.updatedBy
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Category")
        }

        category.version = category.version + 1
        return updateCount
    }


    public static final String UPDATE_SUB_CAT_COUNT = """
        UPDATE doc_category SET
            version=version+1,
            sub_category_count=sub_category_count + :incr
        WHERE
            id=:id AND
            version=:oldVersion
    """

    /**
     *  Update subCategory count
     * @param category - category object
     * @param incr - increment/decrement value
     * @return an integer containing the value of update count
     * */
    public int updateSubCategoryCount(DocCategory category, int incr) {
        Map queryParams = [
                incr: incr,
                id: category.id,
                oldVersion: category.version
        ]
        int updateCount = executeUpdateSql(UPDATE_SUB_CAT_COUNT, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update Category")
        }
        category.version = category.version + 1
        category.subCategoryCount = category.subCategoryCount + incr
        return updateCount
    }

    private static final String DELETE_QUERY = """
                    DELETE FROM doc_category WHERE id=:id
                 """

    /**
     * Delete Category object from DB
     * @param id -id of Category object
     * @return -an integer containing the value of update count
     */
    public int delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete category information')
        }
        return deleteCount;
    }

    /**
     * Create default data for DocCategory
     */
    public void createDefaultDataForDocCategory(Company company) {
        Date currDate = new Date()
        AppUser appUser = appUserService.findByLoginId('admin@athena.com')
        String webUrl = company.webUrl
        DocCategory category = new DocCategory(version: 0, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, description: 'Description for category_1', urlInName: 'category_1', url: webUrl + '/docCategory/showCategory/category_1', isActive: true, name: 'Category_1', subCategoryCount: 3, updatedBy: 0, updatedOn: null)
        category.save()

        DocCategory category1 = new DocCategory(version: 0, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, description: 'Description for category_2', urlInName: 'category_2', url: webUrl + '/docCategory/showCategory/category_2', isActive: true, name: 'Category_2', subCategoryCount: 2, updatedBy: 0, updatedOn: null)
        category1.save()

        DocCategory category2 = new DocCategory(version: 0, companyId: appUser.companyId, createdBy: appUser.id, createdOn: currDate, description: 'Description for category_3', urlInName: 'category_3', url: webUrl + '/docCategory/showCategory/category_3', isActive: true, name: 'Category_3', subCategoryCount: 5, updatedBy: 0, updatedOn: null)
        category2.save()
    }

}
