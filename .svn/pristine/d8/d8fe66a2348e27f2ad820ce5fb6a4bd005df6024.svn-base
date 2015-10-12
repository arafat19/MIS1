package com.athena.mis.arms.actions.rmsprocessinstrumentmapping

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.arms.utility.RmsSessionUtil
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/**
 *  Search specific list of ProcessInstrumentMapping
 *  For details go through Use-Case doc named 'SearchRmsProcessInstrumentMappingActionService'
 */
class SearchRmsProcessInstrumentMappingActionService extends BaseService implements ActionIntf {

    @Autowired
    RmsSessionUtil rmsSessionUtil

    private final Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = "Failed to load process instrument mapping List"
    private static final String LST_PRO_INS_MAPPING = "lstProInsMapping"

    /**
     * Do nothing for pre operation
     */
    Object executePreCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Do nothing for post operation
     */
    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get ProcessInstrumentMapping list through specific search
     * @param parameters -serialized parameters from UI
     * @param obj -N/A
     * @return -a map containing all objects necessary for buildSuccessResultForUI
     */
    @Transactional(readOnly = true)
    Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            initSearch(parameters)          // initialize parameters
            // Search ProcessInstrumentMapping from DB
            Map searchResult = search(this)
            List<GroovyRowResult> lstProInsMapping = (List<GroovyRowResult>) searchResult.lstProInsMapping
            Integer count = (Integer) searchResult.count
            result.put(LST_PRO_INS_MAPPING, lstProInsMapping)
            result.put(Tools.COUNT, count.toInteger())
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap ProcessInstrumentMapping list for grid
     * @param obj -map returned from execute method
     * @return -a map containing all objects necessary to indicate success event
     */
    Object buildSuccessResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            Map executeResult = (Map) obj
            List<GroovyRowResult> lstProInsMapping = (List<GroovyRowResult>) executeResult.get(LST_PRO_INS_MAPPING)
            Integer count = (Integer) executeResult.get(Tools.COUNT)
            List lstWrappedProInsMapping = wrapProInsMapping(lstProInsMapping, start)
            Map output = [page: pageNumber, total: count, rows: lstWrappedProInsMapping]
            return output
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError = true & relevant error message
     */
    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            if (obj) {
                LinkedHashMap preResult = (LinkedHashMap) obj
                if (preResult.get(Tools.MESSAGE)) {
                    result.put(Tools.MESSAGE, preResult.get(Tools.MESSAGE))
                    return result
                }
            }
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Wrap list of ProcessInstrumentMapping in grid entity
     * @param lstProInsMapping -list of ProcessInstrumentMapping object
     * @param start -starting index of the page
     * @return -list of wrapped ProcessInstrumentMapping
     */
    private List wrapProInsMapping(List<GroovyRowResult> lstProInsMapping, int start) {
        List lstWrappedProInsMapping = []
        int counter = start + 1
        for (int i = 0; i < lstProInsMapping.size(); i++) {
            GroovyRowResult groovyRowResult = lstProInsMapping[i]
            GridEntity obj = new GridEntity()
            obj.id = groovyRowResult.id
            obj.cell = [
                    counter,
                    groovyRowResult.id,
                    groovyRowResult.process_type,
                    groovyRowResult.instrument_type
            ]
            lstWrappedProInsMapping << obj
            counter++
        }
        return lstWrappedProInsMapping
    }

    /**
     * Get ProcessInstrumentMapping list through specific search
     * @param baseService
     * @return -a map containing ProcessInstrumentMapping list and search count
     */
    private Map search(BaseService baseService) {
        String searchQuery = """
            SELECT mapping.id, mapping.version, process.key process_type, instrument.key instrument_type
            FROM rms_process_instrument_mapping mapping
                LEFT JOIN system_entity process ON mapping.process_type = process.id
                LEFT JOIN system_entity instrument ON mapping.instrument_type = instrument.id
            WHERE ${baseService.queryType} ilike :query
            AND mapping.company_id = :companyId
            ORDER BY id
            LIMIT :resultPerPage OFFSET :start
        """
        String countQuery = """
            SELECT COUNT(mapping.id)
            FROM rms_process_instrument_mapping mapping
                LEFT JOIN system_entity process ON mapping.process_type = process.id
                LEFT JOIN system_entity instrument ON mapping.instrument_type = instrument.id
            WHERE ${baseService.queryType} ilike :query
            AND mapping.company_id = :companyId
        """
        Map queryParams = [
                companyId: rmsSessionUtil.appSessionUtil.getCompanyId(),
                query: Tools.PERCENTAGE + baseService.query + Tools.PERCENTAGE,
                resultPerPage: baseService.resultPerPage,
                start: baseService.start
        ]
        List<GroovyRowResult> lstProInsMapping = executeSelectSql(searchQuery, queryParams)
        int count = executeSelectSql(countQuery, queryParams).first().count

        return [lstProInsMapping : lstProInsMapping, count : count.toInteger()]
    }
}
