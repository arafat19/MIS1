package com.athena.mis.accounting.actions.accipc

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.accounting.entity.AccIpc
import com.athena.mis.accounting.service.AccIpcService
import com.athena.mis.accounting.utility.AccIpcCacheUtility
import com.athena.mis.accounting.utility.AccSessionUtil
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.Project
import com.athena.mis.application.utility.ProjectCacheUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Class to create AccIpc object and to show on grid list
 *  For details go through Use-Case doc named 'CreateAccIpcActionService'
 */
class CreateAccIpcActionService extends BaseService implements ActionIntf {

    AccIpcService accIpcService
    @Autowired
    ProjectCacheUtility projectCacheUtility
    @Autowired
    AccIpcCacheUtility accIpcCacheUtility
    @Autowired
    AccSessionUtil accSessionUtil

    private static final String CREATE_FAILURE_MSG = "IPC has not been saved"
    private static final String CREATE_SUCCESS_MSG = "IPC has been successfully saved"
    private static final String DEFAULT_ERROR_MESSAGE = "Can not create IPC"
    private static final String IPC_ALREADY_EXISTS = "This IPC already exists in this project"
    private static final String ACC_IPC = "accIpcList"

    private final Logger log = Logger.getLogger(getClass())

    /**
     * Check existence of same AccIpc object for creating new AccIpc object
     * @param params -parameters send from UI
     * @param obj -N/A
     * @return -a map containing AccIpc object & isError(true/false) depending on method success
     */
    @Transactional(readOnly = true)
    public Object executePreCondition(Object params, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap parameterMap = (GrailsParameterMap) params

            //build AccIpc object to create
            AccIpc accIpc = buildAccIpc(parameterMap)

            int totalDuplicateIpcNO = AccIpc.countByIpcNoAndProjectId(accIpc.ipcNo, accIpc.projectId)
            if (totalDuplicateIpcNO > 0) {//Check existence of same AccIpc object
                result.put(Tools.MESSAGE, IPC_ALREADY_EXISTS)
                return result
            }
            result.put(ACC_IPC, accIpc)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }
    }

    /**
     * Save AccIpc object in DB as well as in cache
     * @param parameters -N/A
     * @param obj -a map containing AccIpc object send from executePreCondition
     * @return -newly created AccIpc object for buildSuccessResultForUI
     * map contains isError(true/false) depending on method success
     */
    @Transactional
    public Object execute(Object parameters, Object obj) {
        LinkedHashMap result = new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            AccIpc accIpc = (AccIpc) receivedResult.get(ACC_IPC)

            // save in DB
            AccIpc newAccIpc = accIpcService.create(accIpc)
            if (!newAccIpc) {
                result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
                return result
            }

            // add to cache utility and keep the data sorted
            accIpcCacheUtility.add(newAccIpc, accIpcCacheUtility.SORT_BY_ID, accIpcCacheUtility.SORT_ORDER_DESCENDING)
            result.put(ACC_IPC, newAccIpc)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            //@todo:rollback
            throw new RuntimeException(DEFAULT_ERROR_MESSAGE)
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * do nothing at post condition
     */
    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Wrap newly created AccIpc to show on grid
     * @param obj -newly created AccIpc object from execute method
     * @return -a map containing all objects necessary for grid view
     * map contains isError(true/false) depending on method success
     */
    public Object buildSuccessResultForUI(Object obj) {
        Map result = new LinkedHashMap()
        try {
            LinkedHashMap receivedResult = (LinkedHashMap) obj
            AccIpc accIpc = (AccIpc) receivedResult.get(ACC_IPC)
            Project project = (Project) projectCacheUtility.read(accIpc.projectId)
            GridEntity object = new GridEntity()
            object.id = accIpc.id
            object.cell = [
                    Tools.LABEL_NEW,
                    accIpc.id,
                    accIpc.ipcNo,
                    project.name
            ]
            Map resultMap = [entity: object, version: accIpc.version]
            result.put(Tools.ENTITY, resultMap)
            result.put(Tools.MESSAGE, CREATE_SUCCESS_MSG)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * Build failure result in case of any error
     * @param obj -map returned from previous methods, can be null
     * @return -a map containing isError(true) & relevant error message
     */
    public Object buildFailureResultForUI(Object obj) {
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
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        } catch (Exception ex) {
            log.error(ex.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, CREATE_FAILURE_MSG)
            return result
        }
    }

    /**
     * build accIpc object to create
     * @param params -GrailsParameterMap
     * @return -AccIpc
     */
    private AccIpc buildAccIpc(GrailsParameterMap params) {
        AccIpc accIpc = new AccIpc()

        AppUser user = accSessionUtil.appSessionUtil.getAppUser()

        accIpc.id = 0
        accIpc.version = 0
        accIpc.ipcNo = params.ipcNo
        accIpc.projectId = Long.parseLong(params.projectId.toString())

        accIpc.createdBy = user.id
        accIpc.createdOn = new Date()
        accIpc.updatedBy = 0L
        accIpc.updatedOn = null

        accIpc.companyId = user.companyId

        return accIpc
    }
}
