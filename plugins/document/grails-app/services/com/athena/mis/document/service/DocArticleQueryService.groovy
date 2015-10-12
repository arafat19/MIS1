package com.athena.mis.document.service

import com.athena.mis.BaseService
import com.athena.mis.document.entity.DocArticleQuery
import com.athena.mis.utility.DateUtility

class DocArticleQueryService extends BaseService {

    public DocArticleQuery create(DocArticleQuery articleQuery) {
        DocArticleQuery newArticleQuery = articleQuery.save()
        return newArticleQuery
    }

    public DocArticleQuery read(long id) {
        DocArticleQuery articleQuery = DocArticleQuery.read(id)
        return articleQuery
    }

    public static final String UPDATE_QUERY = """
        UPDATE doc_article_query SET
            version=version+1,
            name=:name,
            content_type_id=:contentTypeId,
            criteria=:criteria,
            updated_on=:updatedOn,
            updated_by=:updatedBy
        WHERE
            id=:id AND
            version=:oldVersion
    """

    /**
     * Update Article object in DB
     * @param article - Article object
     * @return -an int containing the value of update count
     */
    public int update(DocArticleQuery articleQuery) {
        Map queryParams = [
                name: articleQuery.name,
                contentTypeId: articleQuery.contentTypeId,
                criteria: articleQuery.criteria,
                id: articleQuery.id,
                oldVersion: articleQuery.version,
                updatedOn: DateUtility.getSqlDateWithSeconds(articleQuery.updatedOn),
                updatedBy: articleQuery.updatedBy
        ]

        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams);
        if (updateCount <= 0) {
            throw new RuntimeException("Failed to update article query")
        }

        articleQuery.version = articleQuery.version + 1
        return updateCount
    }


    private static final String DELETE_QUERY = """
                    DELETE FROM doc_article_query WHERE id=:id
                 """

    /**
     * Delete Article object from DB
     * @param id -id of Article object
     * @return -an integer containing the value of update count
     */
    public int delete(long id) {
        Map queryParams = [id: id]
        int deleteCount = executeUpdateSql(DELETE_QUERY, queryParams)

        if (deleteCount <= 0) {
            throw new RuntimeException('Error occurred while delete article query')
        }
        return deleteCount;
    }
}
