package com.athena.mis

import grails.util.Environment
import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.hibernate.Session
import org.hibernate.SQLQuery

import javax.sql.DataSource

//@CompileStatic        @todo: enable in grails-2
class BaseService {

    def sessionFactory

    static transactional = false

    public final static int DEFAULT_RESULT_PER_PAGE = 15;
    public final static String DESCENDING_SORT_ORDER = "desc";
    public final static String ASCENDING_SORT_ORDER = "asc";
    public final static String DEFAULT_SORT_ORDER = DESCENDING_SORT_ORDER


    public final static String PAGE_PARAM = "page";
    public final static String RESULT_PER_PAGE_PARAM = "rp";
    public final static String RESULT_PER_PAGE_KENDO = "take";
    public final static String OFFSET_KENDO = "skip";
    public final static String SORT_COLUMN_KENDO = "sort[0][field]";
    public final static String SORT_ORDER_KENDO = "sort[0][dir]";
    public final static String QUERY_COLUMN_KENDO = "filter[filters][0][field]";
    public final static String QUERY_STRING_KENDO = "filter[filters][0][value]";
    public final static String CURRENT_COUNT_ON_PAGE_PARAM = "currentCount";
    private final static String DEFAULT_FALSE_PARAM = 'false'
    public final static String ID = "id";
    public final static String DEFAULT_SORT_COLUMN = ID;
    public final
    static String ENTITY_NOT_FOUND_ERROR_MESSAGE = "No entity found with this id or might have been deleted by someone!";
    int pageNumber = 1;
    int currentCount = 0;
    int resultPerPage = DEFAULT_RESULT_PER_PAGE;
    String sortColumn = DEFAULT_SORT_COLUMN;
    String sortOrder = DEFAULT_SORT_ORDER;
    int start;

    // for search
    String queryType;
    String query;

    private static final String EXECUTING_SQL = " SQL : "
    private static final String VALUES = " Values : "

    DataSource dataSource

    public List executeInsertSql(String query) {
        Sql sql = new Sql(dataSource)
        return sql.executeInsert(query)
    }

    public List executeInsertSql(String query, Map params) {
        Sql sql = new Sql(dataSource)
        return sql.executeInsert(query, params)
    }

    public int executeUpdateSql(String query) {
        Sql sql = new Sql(dataSource)
        consolePrint(query)
        return sql.executeUpdate(query)
    }

    public int executeUpdateSql(String query, Map params) {
        Sql sql = new Sql(dataSource)
        consolePrint(query, params)
        return sql.executeUpdate(query, params)
    }

    public boolean executeSql(String query) {
        Sql sql = new Sql(dataSource)
        return sql.execute(query)
    }

    public boolean executeSql(String query, Map params) {
        Sql sql = new Sql(dataSource)
        return sql.execute(query, params)
    }

    public List<GroovyRowResult> executeSelectSql(String query) {
        Sql sql = new Sql(dataSource)
        consolePrint(query)
        return sql.rows(query)
    }

    public List<GroovyRowResult> executeSelectSql(String query, Map params) {
        Sql sql = new Sql(dataSource)
        consolePrint(query, params)
        return sql.rows(query, params)
    }

    public List getEntityList(String queryStr, Class entity) {
        Session session = sessionFactory.currentSession
        SQLQuery query = session.createSQLQuery(queryStr);
        query.addEntity(entity);
        query.setReadOnly(true);
        consolePrint(queryStr);
        return query.list();
    }

    // ... and add executeDeleteSql, executeInsertSql, count and so on as needed.

    private final void consolePrint(String query, Object params) {
        Environment currentEnv = Environment.current
        if (currentEnv == Environment.DEVELOPMENT) {
            println "${EXECUTING_SQL}${query}}"
            println "${VALUES}${params.toString()}"
        }
    }

    private final void consolePrint(String query) {
        Environment currentEnv = Environment.current
        if (currentEnv == Environment.DEVELOPMENT) {
            println "${EXECUTING_SQL}${query}"
        }
    }

    public Object formatValidationErrorsForUI(Object entity, Closure formatErrorMessage, boolean addAllErrorsIfOccurred) {

        def errors = []
        entity.errors.allErrors.each {
            errors << [it.field, formatErrorMessage(it)/*g.message(error: it)*/]
        }

        if (addAllErrorsIfOccurred) { // will include all errors in result
            return [isError: true, entity: entity, errors: errors, allErrors: entity.errors.allErrors]
        } else {
            return [isError: true, entity: entity, errors: errors]
        }

    }

    /**
     * Initialize pager parameters for pagination within an HTML grid (such as FlexiGrid)
     * Default sort column is domains id, and default sort order is DESCENDING
     *
     * @param params request parameters
     */
    void initPager(def params) {

        if (params.page) {
            this.pageNumber = params.int(PAGE_PARAM);
        }

        if (params.rp) {
            this.resultPerPage = params.int(RESULT_PER_PAGE_PARAM);
        }
        String currentCountStr = params.currentCount.toString()
        if (params.currentCount && (!currentCountStr.equalsIgnoreCase(DEFAULT_FALSE_PARAM))) {
            this.currentCount = params.int(CURRENT_COUNT_ON_PAGE_PARAM);
        }

        if (params.sortname) {
            this.sortColumn = params.sortname;
        }

        if (params.sortorder) {
            this.sortOrder = params.sortorder;
        }

        // calculating the start offset
        this.start = ((this.pageNumber - 1) * this.resultPerPage);
        if (this.start > 0) {
            this.start = this.start - (this.resultPerPage - this.currentCount);
        }
    }

    /**
     * Initialize pager parameters for pagination within an HTML grid (i.e. KendoGrid)
     * Default sort column is domains id, and default sort order is DESCENDING
     *
     * @param params request parameters
     */
    void initPagerKendo(GrailsParameterMap params) {
        this.resultPerPage = params.int(RESULT_PER_PAGE_KENDO)
        this.start = params.int(OFFSET_KENDO)    // query offset
        this.sortColumn = params.get(SORT_COLUMN_KENDO) ? params.get(SORT_COLUMN_KENDO) : DEFAULT_SORT_COLUMN
        this.sortOrder = params.get(SORT_ORDER_KENDO) ? params.get(SORT_ORDER_KENDO) : DEFAULT_SORT_ORDER
    }

    /**
     * Initializes search related request parameters with default sort column
     * and sort order
     *
     * @param params request parameters
     */
    void initSearch(def params) {

        this.initPager(params);

        this.queryType = params.qtype;
        this.query = params.query;

    }
    /**
     * Initializes search related request parameters with default sort column
     * and sort order
     *
     * @param params request parameters
     */
    void initSearchKendo(GrailsParameterMap params) {

        this.initPagerKendo(params)
        this.queryType = params.get(QUERY_COLUMN_KENDO)
        this.query = params.get(QUERY_STRING_KENDO)

    }


}
