package com.athena.mis.document.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.application.entity.SysConfiguration
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.entity.DocCategory
import com.athena.mis.document.entity.DocSubCategory
import com.athena.mis.document.service.DocCategoryService
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.document.utility.DocSubCategoryCacheUtility
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/*Renders 'listView' for My Sub Categories*/

class ListDocSubCategoriesTagLibActionService extends BaseService implements ActionIntf {

    private Logger log = Logger.getLogger(getClass())
    private static final String NAME = 'name'
    private static final String CATEGORY_ID = 'categoryId'
    private static final String SUBJ_CATEGORY_ID= 'subCategoryId'
    private static final String USER_NOT_ASSOCIATED_WITH_CATEGORY = 'You are not associated with any '

    DocCategoryService docCategoryService
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility
    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSubCategoryCacheUtility docSubCategoryCacheUtility

    /**
     * Build a map containing properties of html listView
     *  1. Set default values of properties
     *  2. Overwrite default properties if defined in parameters
     * @param parameters - a map of given attributes
     * @param obj - N/A
     * @return - a map containing all necessary properties with value
     */
    public Object executePreCondition(Object parameters, Object obj) {
        Map listAttributes = new LinkedHashMap()
        try {
            Map attrs = (Map) parameters
            String name = attrs.get(NAME)
            String categoryId = attrs.get(CATEGORY_ID)
            String subCategoryId = attrs.get(SUBJ_CATEGORY_ID)

            listAttributes.put(NAME, name)   // set name
            listAttributes.put(CATEGORY_ID, categoryId)   // set categoryId
            listAttributes.put(SUBJ_CATEGORY_ID, subCategoryId)   // set categoryId
            listAttributes.put(Tools.IS_ERROR, Boolean.FALSE)
            return listAttributes
        } catch (Exception e) {
            log.error(e.getMessage())
            listAttributes.put(Tools.IS_ERROR, Boolean.TRUE)
            return listAttributes
        }
    }


    public Object executePostCondition(Object parameters, Object obj) {
        return null
    }

    /**
     * Get the list of SubCategories
     *  1. Get Category & Sub Category label from SysConfiguration
     *  2. Get My Sub Category List related with the mapped category
     * build the listSubCategories for 'listView'
     * @param parameters - map of given attributes
     * @param obj - N/A
     * @return - output string for 'listView'
     */
    @Transactional(readOnly = true)
    public Object execute(Object parameters, Object obj) {
        try {
            String output
            Map lstAttributes = (Map) parameters
            long categoryId = Long.parseLong(lstAttributes.get(CATEGORY_ID).toString())
            DocCategory category = docCategoryService.read(categoryId)
            AppUser user = docSessionUtil.appSessionUtil.getAppUser()
            SysConfiguration categoryLabelObj = (SysConfiguration) docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_CATEGORY_LABEL, user.companyId)
            SysConfiguration subCategoryLabelObj = (SysConfiguration) docSysConfigurationCacheUtility.readByKeyAndCompanyId(docSysConfigurationCacheUtility.DOC_SUB_CATEGORY_LABEL, user.companyId)
            String msgUserNotMapped = USER_NOT_ASSOCIATED_WITH_CATEGORY + (subCategoryLabelObj ? subCategoryLabelObj.value : Tools.EMPTY_SPACE)

           /* List<GroovyRowResult> lstMappedCategories = listMappedCategory()
            List<DocSubCategory> lstMySubCategories = []

            for (int i = 0; i < lstMappedCategories.size(); i++) {
                if (categoryId == lstMappedCategories[i].id) {
                    lstMySubCategories = (List<DocSubCategory>) docSubCategoryCacheUtility.listByCategoryIdAndActive(categoryId)
                    break
                }
            }*/

            List<GroovyRowResult>lstMySubCategories = listSubCategory(categoryId)
            output = buildLstCategory(lstMySubCategories, lstAttributes, categoryLabelObj.value, category)
            return output
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    public Object buildSuccessResultForUI(Object obj) {
        return null
    }


    public Object buildFailureResultForUI(Object obj) {
        return null
    }


    private static final String MAPPED_CATEGORY_LIST = """
                         SELECT dc.id,dc.name,dc.description
                            FROM doc_category dc
                            WHERE dc.is_active=true
                            AND dc.id
                            IN(SELECT DISTINCT(cum.category_id) category_id
                            FROM doc_sub_category_user_mapping cum
                            WHERE cum.user_id=:userId
                            AND cum.company_id=:companyId
                            AND cum.category_id = dc.id)
                            ORDER BY dc.name
                    """
    /*
    * Get List of Mapped Categories
    * @return lstCategory - mapped category List
    * */

    private List<GroovyRowResult> listMappedCategory() {
        AppUser appUser = docSessionUtil.appSessionUtil.getAppUser()
        Map queryParams = [
                companyId: appUser.companyId,
                userId: appUser.id
        ]

        List<GroovyRowResult> lstCategory = executeSelectSql(MAPPED_CATEGORY_LIST, queryParams)
        return lstCategory
    }


    private static final String MAPPED_SUB_CATEGORY_LIST = """
                         SELECT DISTINCT(sc.id) id, sc.name, sc.description, scum.is_favourite, scum.category_id,
                         CASE WHEN scum.is_favourite THEN 'fa-thumbs-o-down' ELSE 'fa-thumbs-o-up' END AS css_class
                         FROM doc_sub_category sc
                         LEFT JOIN doc_sub_category_user_mapping scum ON scum.sub_category_id=sc.id
                         WHERE scum.user_id=:userId
                         AND scum.category_id=:categoryId
                         AND sc.is_active = true
                    """
    /*
    * Get List of Mapped Sub Categories
    * @param categoryId
    * @return lstMappedSubCategory - mapped subcategory List
    * */

    private List<GroovyRowResult> listSubCategory(long categoryId) {
        AppUser appUser = docSessionUtil.appSessionUtil.getAppUser()
        Map queryParams = [
                userId: appUser.id,
                categoryId: categoryId
        ]

        List<GroovyRowResult> result = executeSelectSql(MAPPED_SUB_CATEGORY_LIST, queryParams)
        return result
    }

    /*
    * Build list of my subcategory with name, description
    * @param lstMySubCategories - List of my subcategories
    * @param lstAttributes - Map of attributes
    * @param categoryLabel - Category Label from System Configuration
    * @param category - DocCategory object
    * @return - Html for My Sub Category ListView
    * */

    private String buildLstCategory(List<GroovyRowResult> lstMySubCategories, Map lstAttributes, String categoryLabel, DocCategory category) {
        // read map values
        String name = lstAttributes.get(NAME)
        long subCategoryId = Long.parseLong(lstAttributes.get(SUBJ_CATEGORY_ID).toString())
        String htmlContentDescription = """
                    <form id="frmContentDescription" class="form-horizontal form-widgets" role="form">
                        <div class="form-group">
                            <label class="col-md-2 control-label">${categoryLabel} :</label>
                            <div class="col-md-10">${category.name}</div>
                             <input type="hidden" name="categoryId" categoryId="${category.id}"/>
                        </div>
                        <div class="form-group">
                            <label class="col-md-2 control-label">Description:</label>

                            <div class="col-md-10">${category.description ? category.description : Tools.EMPTY_SPACE}</div>
                        </div>
                    </form>
                  """

        String htmlSubCategoryContainer = """<div class='row'> <div class='col-md-12'>
                <div name = '${name}' id = '${name}'></div>"""
        String htmlPager = "<div name = 'pager' id = 'pager' class='k-pager-wrap' ></div>"

        String script = """ \n
                <script type="text/x-kendo-template" id="templateNotes">
                         <div class="popover" id="#:id#" category_id="#:category_id#">
                            <div class="arrow"></div>
                            <h3 class="popover-title" id="#:id#">#:name#
                                <button class="btnLike isLike btn btn-default pull-right">
                                    <span class="fa #:css_class#"></span>
                                </button>
                            </h3>
                            <div class="popover-content">
                            <p>#:description#</p>
                            </div>
                        </div>
                </script>

                <script type="text/javascript">
                    \$(document).ready(function () {
                          var dataSourceNote = new kendo.data.DataSource({
                            data:${lstMySubCategories as JSON},
                            pageSize: 5
                        });

                        \$("#pager").kendoPager({
                            dataSource: dataSourceNote
                        });

                        \$("#${name}").kendoListView({
                            dataSource: dataSourceNote,
                            template: kendo.template(\$("#templateNotes").html()),
                            selectable:true,
                            change: showSubCategoryDetails
                        });

                        \$( ".btnLike").click(function(e) {
                            var isLike = \$(this).hasClass('isLike');
                            var subCategoryId = \$(this).closest('div.popover').attr('id');

                              var isFavourite = \$(this).closest('div.popover').attr('isFavourite');

                            addOrRemoveFavourite(subCategoryId, categoryId, isFavourite,\$(this));
                        });

                        markSelectedSubCategory(${subCategoryId});
                    });

                     function showSubCategoryDetails() {
                      var listView = \$("#${name}").data("kendoListView");
                      var selectedIdx = listView.select().index();
                      var subCategoryId = listView.dataSource.view()[selectedIdx].id;
                      var categoryId = listView.dataSource.view()[selectedIdx].category_id;
                      \$(".popover").removeClass('left');
                      \$("div#" + subCategoryId + ".popover").addClass('left');
                         //var categoryId = \$("#categoryId").val();

                       showLoadingSpinner(true);
                       \$.ajax({
                            url: "/docSubCategory/viewSubCategoryDetails?categoryId="+categoryId + "&subCategoryId="+subCategoryId,
                            success: executePostForShowDetails,
                            complete: onCompleteShowMySubCategory,
                            type: 'post'
                        });

                    }

                    function executePostForShowDetails(data) {
                        \$('#subCategoryDetails').html(data);

                    }

                    function onCompleteShowMySubCategory() {
                       \$('#subCategoryDetails').show();
                       showLoadingSpinner(false);
                    }

                    function addOrRemoveFavourite(subCategoryId, categoryId, isFavourite,ctl) {

                     var url = "/docSubCategory/addOrRemoveSubCategoryFavourite?subCategoryId="+subCategoryId + "&categoryId="+categoryId + "&isLike=" + isFavourite;
                      alert(url);
                        showLoadingSpinner(true);
                       \$.ajax({
                            url: url,
                            success: executePostForAddOrRemoveFavourite(\$(ctl)),
                            complete:  showLoadingSpinner(false),
                            type: 'post'
                        });
                    }
                    function executePostForAddOrRemoveFavourite(ctl) {
                             if(data.isError == true){
                             showError("Success");
                            return false;
                        }
                        var iconObj = \$(ctl).find("span.fa");
                        iconObj.removeClass("fa-thumbs-o-up");
                        iconObj.removeClass("fa-thumbs-o-down");
                        if(\$(ctl).hasClass('isLike')){
                            iconObj.addClass("fa-thumbs-o-down");
                        }else{
                            iconObj.addClass("fa-thumbs-o-up");
                        }
                        \$(ctl).toggleClass('isLike');
                         showSuccess(data.message);
                         \$('#subCategoryDetails').show();

                    }
                    function markSelectedSubCategory(id){
                        if (id <= 0) return false;
                        var listView = \$("#${name}").data("kendoListView");
                         var elements = listView.element.children();
                         \$(elements).each(function () {
                             var eachId = \$(this).attr("id");
                             if (eachId == id) {
                                listView.select(\$(this));
                                return false;
                            }
                         });
                    }
                </script>
        """

        String style = """
                <style>
                    #${name}{
                        overflow-y:auto;
                    }
                     #${name} .popover {
                        display:block;
                        position: relative;
                        margin:5px 10px 5px 5px;
                        max-width:none;
                        cursor:pointer;
                    }
                    #${name} .k-state-selected {
                        color: inherit;
                    }
                    #${name} .k-state-selected .popover-title{
                        background-color: #EAEAEA;
                    }
                    #${name} .popover > h3 > button {
                        padding:0px 7px;
                    }
                </style>
             """

        return htmlContentDescription + htmlSubCategoryContainer + htmlPager + script + style + "</div></div>"
    }
}
