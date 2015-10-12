package com.athena.mis.exchangehouse.actions.sanction

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.GridEntity
import com.athena.mis.application.utility.RoleTypeCacheUtility
import com.athena.mis.exchangehouse.entity.ExhSanction
import com.athena.mis.exchangehouse.utility.ExhSessionUtil
import com.athena.mis.utility.DateUtility
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired

class ExhShowSanctionActionService extends BaseService implements ActionIntf {

    private final Logger log = Logger.getLogger(getClass())

    private static final String EXCEPTION_MESSAGE = "Failed to load sanction list"
    private static final String SERVICE_RETURN_OBJ = "serviceReturn"
    private static final String DEFAULT_ERROR_MESSAGE = "Can not load sanction list"

    private static final String GRID_OUT_PUT = "sanctionListJSON"

    @Autowired
    ExhSessionUtil exhSessionUtil
    @Autowired
    RoleTypeCacheUtility roleTypeCacheUtility

    Object executePreCondition(Object parameters, Object obj) {

        LinkedHashMap preResult = new LinkedHashMap()
        try {
            preResult.put(Tools.IS_ERROR, Boolean.FALSE)
            preResult.put(Tools.HAS_ACCESS, Boolean.FALSE)

            if (exhSessionUtil.appSessionUtil.getAppUser().isPowerUser) {
                preResult.put(Tools.HAS_ACCESS, Boolean.TRUE)
            }
            return preResult
        } catch (Exception ex) {
            preResult.put(Tools.IS_ERROR, Boolean.TRUE)
            preResult.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            log.error(ex.getMessage())
            return preResult
        }
    }

    Object execute(Object parameters, Object obj) {
        LinkedHashMap executeResult = new LinkedHashMap()
        try {
            executeResult.put(Tools.IS_ERROR, Boolean.TRUE)
            GrailsParameterMap params = (GrailsParameterMap) parameters
            params.rp = 25
            initPager(params)
            LinkedHashMap serviceReturn = list()
            executeResult.put(SERVICE_RETURN_OBJ, serviceReturn)
            executeResult.put(Tools.IS_ERROR, Boolean.FALSE)
            return executeResult
        } catch (Exception ex) {
            executeResult.put(Tools.IS_ERROR, Boolean.TRUE)
            executeResult.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            log.error(ex.getMessage())
            return executeResult
        }

    }


    Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    public Object buildSuccessResultForUI(Object result) {
        LinkedHashMap successResult = new LinkedHashMap()
        try {
            successResult.put(Tools.IS_ERROR, Boolean.TRUE)
            LinkedHashMap serviceReturn = (LinkedHashMap) result.serviceReturn
            List<ExhSanction> listOfSanction = (List<ExhSanction>) serviceReturn.sanctionList

            List sanctionList = wrapSanctionInGrid(listOfSanction, start)

            int count = (int) serviceReturn.count
            Map gridOutput = [page: pageNumber, total: count, rows: sanctionList]

            successResult.put(GRID_OUT_PUT, gridOutput)
            successResult.put(Tools.IS_ERROR, Boolean.FALSE)
            return successResult
        } catch (Exception ex) {
            successResult.put(Tools.IS_ERROR, Boolean.TRUE)
            successResult.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            log.error(ex.getMessage())
            return successResult
        }
    }

    private List wrapSanctionInGrid(List<ExhSanction> listOfSanction, int start) {

        List sanctionInfos = []
        Integer counter = start + 1
        for (int i = 0; i < listOfSanction.size(); i++) {
            ExhSanction sanction = listOfSanction[i]
            GridEntity obj = new GridEntity()
            obj.id = sanction.id

            String listedOnStr = DateUtility.getDateForUI(sanction.createdOn)
            String lastUpdatedStr = DateUtility.getDateForUI(sanction.lastUpdate)

            obj.cell = [counter,
                    sanction.name,
                    sanction.title, sanction.dob, sanction.townOfBirth,
                    sanction.countryOfBirth, sanction.nationality, sanction.passportDetails,
                    sanction.niNumber, sanction.position,
                    sanction.address1, sanction.address2, sanction.address3, sanction.address4, sanction.address5, sanction.address6,
                    sanction.postOrZip, sanction.country, sanction.otherInformation,
                    sanction.groupType, sanction.aliasType, sanction.regime,
                    listedOnStr, lastUpdatedStr,
                    sanction.groupId]
            sanctionInfos << obj
            counter++
        }

        return sanctionInfos
    }

    Object buildFailureResultForUI(Object obj) {
        LinkedHashMap failureResult = new LinkedHashMap()
        try {
            LinkedHashMap returnResult = (LinkedHashMap) obj

            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            if (returnResult.message) {
                failureResult.put(Tools.MESSAGE, returnResult.get(Tools.MESSAGE))
            } else {
                failureResult.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            }
            return failureResult
        } catch (Exception ex) {
            failureResult.put(Tools.IS_ERROR, Boolean.TRUE)
            failureResult.put(Tools.MESSAGE, EXCEPTION_MESSAGE)
            log.error(ex.getMessage())
            return failureResult
        }
    }

    // List Sanction for Admin
    private LinkedHashMap list() {
        List<ExhSanction> sanctionList = ExhSanction.list([max: resultPerPage, offset: start, sort: sortColumn, order: sortOrder, readOnly: true])
        int count = ExhSanction.count()
        return [sanctionList: sanctionList, count: count]
    }
}