package com.athena.mis.arms.actions.rmstask

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.BankBranch
import com.athena.mis.utility.Tools
import grails.transaction.Transactional
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class PopulateBranchForForwardActionService extends BaseService implements ActionIntf{

    private static final String BANK_COULD_NOT_FOUND="Bank could not found"
    private static final String DISTRICT_COULD_NOT_FOUND="Bank could not found"
    private static final String BRANCH_LIST_COULD_NOT_LOADED="Branch could not be loaded"
    private static final String BRANCH_NAME="branch_name"
    private static final String LST_BRANCH="lstBranch"

    private Logger log = Logger.getLogger(getClass())

    public Object executePreCondition(Object parameters, Object obj){
        Map result= new LinkedHashMap()
        try {
            result.put(Tools.IS_ERROR,Boolean.TRUE)
            GrailsParameterMap params= (GrailsParameterMap) parameters
            if(!params.bankId){
                result.put(Tools.MESSAGE, BANK_COULD_NOT_FOUND)
                return result
            }
            if(!params.districtId){
                result.put(Tools.MESSAGE,DISTRICT_COULD_NOT_FOUND)
                return result
            }
            result.put(Tools.IS_ERROR,Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR,Boolean.TRUE)
            return result
        }
    }

    public Object executePostCondition(Object parameters, Object obj){
        return null
    }

    @Transactional(readOnly = true)
    public  Object execute(Object parameters, Object obj){

        Map result= new LinkedHashMap()
        try {
            GrailsParameterMap params= (GrailsParameterMap) parameters
            long bankId = Long.parseLong(params.bankId)
            long district = Long.parseLong(params.districtId)
            List lstBranch=populateBranchDropDown(bankId,district)
            result.put(LST_BRANCH,Tools.listForKendoDropdown(lstBranch,BRANCH_NAME,null))
            result.put(Tools.IS_ERROR,Boolean.FALSE)
            return result
        }
        catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR,Boolean.TRUE)
            return result
        }
    }

    public  Object buildSuccessResultForUI(Object obj){
        return null
    }

    public  Object buildFailureResultForUI(Object obj){
        Map result= new LinkedHashMap()
        result.put(Tools.MESSAGE,BRANCH_LIST_COULD_NOT_LOADED)
        return result
    }

    private static final String QUERY_FOR_BRANCH_DROP_DOWN="""
        SELECT name branch_name FROM bank_branch
        WHERE bank_id= :bankId
        AND district_id= :districtId
    """
    private List<BankBranch> populateBranchDropDown(long bankId, long districtId){

    }
}
