package com.athena.mis.document.actions.invitedmembers

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocInvitedMembers
import com.athena.mis.document.entity.DocInvitedMembersCategory
import com.athena.mis.document.service.DocInvitedMembersCategoryService
import com.athena.mis.document.service.DocInvitedMembersService
import com.athena.mis.document.utility.DocCategoryCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.utility.Tools
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 *  Show Send invitation UI to specific recipient
 *  For details go through Use-Case doc named 'ShowResendInvitationDocInvitedMembersActionService'
 */
class ShowResendInvitationDocInvitedMembersActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String DEFAULT_ERROR_MESSAGE = 'Failed to load show send invitation page'
    private static final String DOC_INVITED_MEMBER_MESSAGE = 'docInvitedMemberMessage'
    private static final String DOC_INVITED_MEMBER_EMAIL = 'docInvitedMemberEmail'
    private static final String CATEGORY_ID = 'categoryId'
    private static final String LST_DOC_SUB_CATEGORY = 'lstDocSubCategory'

    DocInvitedMembersService docInvitedMembersService
    DocInvitedMembersCategoryService docInvitedMembersCategoryService

    @Autowired
    DocCategoryCacheUtility docCategoryCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil

    public Object executePreCondition(Object parameters, Object obj) {
        //Do nothing for pre-operation
        return null
    }

    public Object executePostCondition(Object parameters, Object obj) {
        //Do nothing for post-operation
        return null
    }

    /*
    * @param parameters - from UI
    * @param obj - N/A
    * Get invited member
    * Update invited member info to DB
    * @return - A map containing all objects of categoryId,lstDocSubCategory, docInvitedMemberEmail for UI
    * */

    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        Map result = new LinkedHashMap()
        try {
            GrailsParameterMap params = (GrailsParameterMap) parameters
            long companyId = docSessionUtil.appSessionUtil.companyId
            long recipientId = Long.parseLong(params.id.toString())
            DocInvitedMembers docInvitedMember = docInvitedMembersService.readByIdAndCompany(recipientId, companyId)
            List<DocInvitedMembersCategory> lstInvitedMembersCategory = docInvitedMembersCategoryService.findAllByInvitedMemberId(docInvitedMember.id)
            long categoryId = lstInvitedMembersCategory[0].categoryId
            DocCategory category = (DocCategory) docCategoryCacheUtility.read(categoryId)
            List subCategoryIds = (List) lstInvitedMembersCategory.subCategoryId as List

            result.put(CATEGORY_ID, category.id)
            result.put(LST_DOC_SUB_CATEGORY, buildCommaSeparatedAndSingleQuoteStringOfIds(subCategoryIds))
            result.put(DOC_INVITED_MEMBER_EMAIL, docInvitedMember.email)
            result.put(DOC_INVITED_MEMBER_MESSAGE, docInvitedMember.message)
            result.put(Tools.IS_ERROR, Boolean.FALSE)
            return result
        } catch (Exception e) {
            log.error(e.getMessage())
            result.put(Tools.IS_ERROR, Boolean.TRUE)
            result.put(Tools.MESSAGE, DEFAULT_ERROR_MESSAGE)
            return result
        }

    }

    public Object buildSuccessResultForUI(Object obj) {
        //Do nothing for build successful result for ui
        return null
    }


    public Object buildFailureResultForUI(Object obj) {
        //Do nothing for build failure result for ui
        return null
    }

    /*
    * build coma separated and single quote id ie ['1','2'] for taglib
    * */

    public String buildCommaSeparatedAndSingleQuoteStringOfIds(List subCategoryIds) {
        String strIds = Tools.EMPTY_SPACE
        for (int i = 0; i < subCategoryIds.size(); i++) {
            if ((i + 1) < subCategoryIds.size()) strIds += (Tools.SINGLE_QUOTE + subCategoryIds[i] + Tools.SINGLE_QUOTE + Tools.COMA)
            else strIds += Tools.SINGLE_QUOTE + subCategoryIds[i] + Tools.SINGLE_QUOTE
        }
        return Tools.THIRD_BRACKET_START + strIds + Tools.THIRD_BRACKET_END
    }
}
