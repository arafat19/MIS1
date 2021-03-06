package com.athena.mis.document.actions.taglib

import com.athena.mis.ActionIntf
import com.athena.mis.BaseService
import com.athena.mis.application.entity.AppUser
import com.athena.mis.document.config.DocSysConfigurationCacheUtility
import com.athena.mis.document.utility.DocSessionUtil
import com.athena.mis.utility.Tools
import grails.converters.JSON
import groovy.sql.GroovyRowResult
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired

/*Renders 'listView' for My Categories*/

class ListDocCategoriesTaglibActionService extends BaseService implements ActionIntf {
    private Logger log = Logger.getLogger(getClass())

    private static final String NAME = 'name'
    private static final String ON_CLICK = 'onclick'
    private static final String CATEGORY_ID = 'category_id'


    @Autowired
    DocSessionUtil docSessionUtil
    @Autowired
    DocSysConfigurationCacheUtility docSysConfigurationCacheUtility

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

            listAttributes.put(NAME, name)   // set name
            listAttributes.put(CATEGORY_ID, categoryId)   // set categoryId
            listAttributes.put(ON_CLICK, attrs.onclick ? attrs.onclick : null)
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
     * Get the list of Categories
     * build the buildLstCategory for 'listView'
     * @param parameters - map of given attributes
     * @param obj - N/A
     * @return - output string for 'listView'
     */
    public Object execute(Object parameters, Object obj) {
        try {
            String output
            Map lstAttributes = (Map) parameters
            List<GroovyRowResult> lstMappedCategories = listMappedCategories()
            output = buildCategoryList(lstMappedCategories, lstAttributes)
            return output
        } catch (Exception e) {
            log.error(e.message)
            return Boolean.FALSE
        }
    }

    /*
    * Get the Group Category Mapped list
    * @param lstGroupIds - list of groupId
    * @return lstAppGroupCategory - Group Category Mapped list
    * */

    private List<GroovyRowResult> listMappedCategories() {
        AppUser user = docSessionUtil.appSessionUtil.appUser
        String queryStr = """
            SELECT dc.id, dc.name, dc.description
            FROM doc_category dc
            WHERE dc.is_active=true
            AND dc.id
            IN(SELECT DISTINCT(cum.category_id) category_id
            FROM doc_category_user_mapping cum
            WHERE cum.user_id=:userId
            AND cum.company_id=:companyId)
            ORDER BY dc.name
        """
        Map queryParams = [
                userId   : user.id,
                companyId: user.companyId
        ]

        List<GroovyRowResult> lstUserCategory = executeSelectSql(queryStr, queryParams)
        return lstUserCategory
    }


    public Object buildSuccessResultForUI(Object obj) {
        return null
    }

    public Object buildFailureResultForUI(Object obj) {
        return null
    }

    /*
    * Build list of category with name,description
    * @param lstMappedCategories - List of Mapped Category
    * @param lstAttributes - List of attributes
    * @return - Html for My Category ListView
    * */

    private String buildCategoryList(List<GroovyRowResult> lstMappedCategories, Map lstAttributes) {
        long categoryId = Long.parseLong(lstAttributes.get(CATEGORY_ID).toString())
        // read map values
        String name = lstAttributes.get(NAME)
        String html = "<div name = '${name}' id = '${name}'></div>"
        String htmlPager = "<div name = 'pager' id = 'pager' class='k-pager-wrap' ></div>"

        String script = """ \n
                <script type="text/x-kendo-template" id="templateNotes">
                        <div class="popover" id="#:id#">
                            <div class="arrow"></div>
                            <h3 class="popover-title">#:name#</h3>
                            <div class="popover-content">
                            <p>#:description#</p>
                            </div>
                        </div>
                </script>

                <script type="text/javascript">
                    \$(document).ready(function () {
                          var dataSourceCategory = new kendo.data.DataSource({
                            data:${lstMappedCategories as JSON},
                            pageSize: 6
                        });

                        \$("#pager").kendoPager({
                            dataSource: dataSourceCategory
                        });

                        \$("#${name}").kendoListView({
                            dataSource: dataSourceCategory,
                            template: kendo.template(\$("#templateNotes").html()),
                            selectable: true,
                            change: showDetails,
                        });

                         markSelectedCategory(${categoryId});

                    });

                    function showDetails() {
                      var listView = \$("#${name}").data("kendoListView");
                      var selectedIdx = listView.select().index();
                      var categoryId = listView.dataSource.view()[selectedIdx].id;
                      \$(".popover").removeClass('left');
                      \$("div#" + categoryId + ".popover").addClass('left');

                        \$('#myCategoryDetails').hide();
                        showLoadingSpinner(true);
                        \$.ajax({
                            url: "/docCategory/viewCategoryDetails?categoryId="+categoryId,
                            success: executePostForShowDetails,
                            complete: onCompleteShowMyCategory,
                            type: 'post'
                        });
                    }
                    function executePostForShowDetails(data) {
                        \$('#myCategoryDetails').html(data);
                    }
                    function onCompleteShowMyCategory() {
                       \$('#myCategoryDetails').show();
                       showLoadingSpinner(false);
                    }

                           function markSelectedCategory(id){
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
                </style>
             """

        return html + htmlPager + script + style
    }
}
