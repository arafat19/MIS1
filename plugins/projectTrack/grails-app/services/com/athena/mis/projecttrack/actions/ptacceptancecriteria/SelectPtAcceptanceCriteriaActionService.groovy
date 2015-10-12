package com.athena.mis.projecttrack.actions.ptacceptancecriteria

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.projecttrack.entity.PtAcceptanceCriteria
import com.athena.mis.projecttrack.service.PtAcceptanceCriteriaService
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

/**
 *  Select AcceptanceCriteria object and show in UI for editing
 *  For details go through Use-Case doc named 'SelectPtAcceptanceCriteriaActionService'
 */
class SelectPtAcceptanceCriteriaActionService extends BaseService implements ActionIntf {

	PtAcceptanceCriteriaService ptAcceptanceCriteriaService

    private final Logger log = Logger.getLogger(getClass())

	private static final String ACCEPTANCE_CRITERIA_NOT_FOUND_MASSAGE = "Selected acceptance criteria is not found"
	private static final String DEFAULT_ERROR_MASSAGE = "Failed to select acceptance criteria"

	/**
	 * Get parameters from UI and check if id exists in parameterMap
	 * @param parameters -serialized parameters from UI
	 * @param obj -N/A
	 * @return -a map containing all objects necessary for execute
	 * map contains isError(true/false) depending on method success
	 */
	public Object executePreCondition(Object params, Object obj) {
		LinkedHashMap result = new LinkedHashMap()
		try {
			result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
			GrailsParameterMap parameterMap = (GrailsParameterMap) params
			// check required parameters
			if (!parameterMap.id) {
				result.put(Tools.MESSAGE, Tools.ERROR_FOR_INVALID_INPUT)
				return result
			}
			result.put(Tools.IS_ERROR, Boolean.FALSE)
			return result
		} catch (Exception e) {
			log.error(e.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, ACCEPTANCE_CRITERIA_NOT_FOUND_MASSAGE)
			return result
		}
	}

	/**
	 * Get AcceptanceCriteria object by id
	 * @param parameters -parameters from UI
	 * @param obj -N/A
	 * @return -a map containing all objects necessary for buildSuccessResultForUI
	 * map contains isError(true/false) depending on method success
	 */
	@Transactional(readOnly = true)
	public Object execute(Object params, Object obj) {
		LinkedHashMap result = new LinkedHashMap()
		try {
			result.put(Tools.IS_ERROR, Boolean.TRUE)    // default value
			GrailsParameterMap parameterMap = (GrailsParameterMap) params

			long ptAcceptanceCriteriaId = Long.parseLong(parameterMap.id.toString())
			PtAcceptanceCriteria acceptanceCriteria = (PtAcceptanceCriteria) ptAcceptanceCriteriaService.read(ptAcceptanceCriteriaId)    // get ptAcceptanceCriteria object
			// check whether the ptAcceptanceCriteria object exists or not
			if (!acceptanceCriteria) {
                result.put(Tools.MESSAGE, ACCEPTANCE_CRITERIA_NOT_FOUND_MASSAGE)
                return result
			}
            result.put(Tools.ENTITY, acceptanceCriteria)
			result.put(Tools.IS_ERROR, Boolean.FALSE)
			return result
		} catch (Exception e) {
			log.error(e.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, ACCEPTANCE_CRITERIA_NOT_FOUND_MASSAGE)
			return result
		}
	}

	/**
	 * do nothing for post condition
	 */
	public Object executePostCondition(Object params, Object obj) {
		return null
	}

	/**
	 * Build a map with AcceptanceCriteria object & other related properties to show on UI
	 * @param obj -map returned from execute method
	 * @return -a map containing all objects necessary for show
	 * map contains isError(true/false) depending on method success
	 */
	public Object buildSuccessResultForUI(Object obj) {
		LinkedHashMap result = new LinkedHashMap()
		try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
			LinkedHashMap executeResult = (LinkedHashMap) obj   // cast map returned from execute method
			PtAcceptanceCriteria acceptanceCriteria = (PtAcceptanceCriteria) executeResult.get(Tools.ENTITY)
			result.put(Tools.ENTITY, acceptanceCriteria)
			result.put(Tools.VERSION, acceptanceCriteria.version)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
			return result
		} catch (Exception e) {
			log.error(e.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
			return result
		}
	}

	/**
	 * Build failure result in case of any error
	 * @param obj -map returned from previous methods, can be null
	 * @return -a map containing isError = true & relevant error message
	 */
	public Object buildFailureResultForUI(Object obj) {
		LinkedHashMap result = new LinkedHashMap()
		try {
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			if (!obj) {
                result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
			}
            Map previousResult = (Map) obj  // cast map returned from previous method
            result.put(Tools.MESSAGE, previousResult.get(Tools.MESSAGE))
			return result
		}
		catch (Exception e) {
			log.error(e.getMessage())
			result.put(Tools.IS_ERROR, Boolean.TRUE)
			result.put(Tools.MESSAGE, DEFAULT_ERROR_MASSAGE)
			return result
		}
	}
}
