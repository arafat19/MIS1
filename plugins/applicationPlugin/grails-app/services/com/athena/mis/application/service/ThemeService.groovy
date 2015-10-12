package com.athena.mis.application.service

import com.athena.mis.BaseService
import com.athena.mis.PluginConnector
import com.athena.mis.application.config.ThemeCacheUtility
import com.athena.mis.application.entity.Theme
import com.athena.mis.utility.DateUtility
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional

/**
 * ThemeService is used to handle only CRUD related object manipulation
 * (e.g. list, read, update etc.)
 */
class ThemeService extends BaseService {

    @Autowired
    ThemeCacheUtility themeCacheUtility

    private static final String SORT_BY_KEY = "key"

    /**
     * Method to get list of theme
     * @return - theme list
     */
    public List list() {
        return Theme.list(sort: "key", order: "asc", readOnly: true)
    }

    /**
     * Method to get the count of theme
     * @param companyId - company id
     * @return - an integer value of theme count
     */
    public int countByCompanyId(long companyId) {
        int count = Theme.countByCompanyId(companyId)
        return count
    }


    /**
     * Method to find the theme list
     * @param companyId - company id
     * @return - a list of theme
     */
    public List findAllByCompanyId(long companyId) {
        List themeList = Theme.findAllByCompanyId(companyId, [max: resultPerPage, offset: start, sort: SORT_BY_KEY, order: ASCENDING_SORT_ORDER, readOnly: true])
        return themeList
    }


    /**
     * Method to find theme object
     * @param themeId - theme id
     * @param companyId - company id
     * @return - an object of theme
     */
    public Theme findByIdAndCompanyId(long themeId, long companyId) {
        Theme theme = Theme.findByIdAndCompanyId(themeId, companyId, [readOnly: true])
        return theme
    }

    /**
     * Method to read Theme by id
     * @param id - Theme.id
     * @return - Theme object
     */
    public Theme read(long id) {
        Theme theme = Theme.read(id)
        return theme
    }

    private static final String UPDATE_QUERY = """
                    UPDATE theme SET
                          value=:value,
                          description=:description,
                          version=version+1,
                          updated_by=:updatedBy,
                          updated_on=:updatedOn
                      WHERE id=:id
    """

    /**
     * Method to update Theme object
     * @param theme - object of Theme
     * @return - updateCount(intValue) if updateCount <= 0 then throw exception to rollback transaction
     */
    public int update(Theme theme) {
        Map queryParams = [
                id: theme.id,
                value: theme.value,
                description: theme.description,
                version: theme.version,
                updatedBy: theme.updatedBy,
                updatedOn: DateUtility.getSqlDateWithSeconds(theme.updatedOn)
        ]
        int updateCount = executeUpdateSql(UPDATE_QUERY, queryParams)
        if (updateCount <= 0) {
            throw new RuntimeException('Error occurred while updating theme')
        }
        return updateCount
    }

    private static final String VALUE_CSS_MAIN_COMPONENTS = "/*\n" +
            "select {\n" +
            "     border: 1px solid #BDC7D8;\n" +
            "     border-left: 4px solid #BDC7D8;\n" +
            "     width: 150px;\n" +
            " }\n" +
            "\n" +
            "input[readOnly=\"readonly\"], input[readonly=\"true\"] {\n" +
            "    background-color: #eee !important;\n" +
            "    color: #000 !important;\n" +
            "    cursor: default !important;\n" +
            "}\n" +
            "\n" +
            "input[type=text].error, select.error, textarea.error, input[type=password].error {\n" +
            "    border-color: #f9dd34;\n" +
            "    background-color: #fff9d7;\n" +
            "    color: #000000;\n" +
            "}\n" +
            "\n" +
            "input[type=text], input[type=password] {\n" +
            "    border: 1px solid #BDC7D8;\n" +
            "    border-left: 4px solid #BDC7D8;\n" +
            "    width: 145px;\n" +
            "}\n" +
            "\n" +
            "textarea {\n" +
            "    border: 1px solid #BDC7D8;\n" +
            "    border-left: 4px solid #BDC7D8;\n" +
            "}\n" +
            "*/\n" +
            "\n" +
            "/*Various classes for buttons */\n" +
            ".save, .clear-form, .search {\n" +
            "    padding: 0.3em 0.5em;\n" +
            "    height: 2.0833em;\n" +
            "    border: 1px solid #ccc;\n" +
            "    background: #f6f6f6;\n" +
            "    background-image: -moz-linear-gradient(top, #ffffff, #efefef);\n" +
            "    background-image: -webkit-gradient(linear, left top, left bottom, from(#ffffff), to(#efefef));\n" +
            "    filter: progid:DXImageTransform.Microsoft.Gradient(startColorStr=#ffffff, endColorStr=#efefef);\n" +
            "    -ms-filter: \"progid:DXImageTransform.Microsoft.Gradient(startColorStr=#FFFFFF, endColorStr=#EFEFEF)\";\n" +
            "    -moz-border-radius: 5px;\n" +
            "    -webkit-border-radius: 5px;\n" +
            "    border-radius: 5px;\n" +
            "    white-space: nowrap;\n" +
            "    vertical-align: middle;\n" +
            "    cursor: pointer;\n" +
            "    overflow: visible;\n" +
            "}\n" +
            "\n" +
            ".save:hover, .clear-form:hover, .search:hover {\n" +
            "    border-color: #999;\n" +
            "    background: #f3f3f3;\n" +
            "    outline: 0;\n" +
            "    -moz-box-shadow: 0 0 3px #999;\n" +
            "    -webkit-box-shadow: 0 0 3px #999;\n" +
            "    box-shadow: 0 0 3px #999\n" +
            "}\n" +
            "\n" +
            ".save:disabled {\n" +
            "    border-color: #999;\n" +
            "    background: #f3f3f3;\n" +
            "    outline: 0;\n" +
            "    -moz-box-shadow: 0 0 3px #999;\n" +
            "    -webkit-box-shadow: 0 0 3px #999;\n" +
            "    box-shadow: 0 0 3px #999\n" +
            "}\n" +
            "\n" +
            "/*for small button in voucher Dr/Cr create*/\n" +
            ".small_btn {\n" +
            "    padding: 0.1em 0.5em;\n" +
            "    border: 1px solid #ccc;\n" +
            "    background: #f6f6f6;\n" +
            "    background-image: -moz-linear-gradient(top, #ffffff, #efefef);\n" +
            "    background-image: -webkit-gradient(linear, left top, left bottom, from(#ffffff), to(#efefef));\n" +
            "    filter: progid:DXImageTransform.Microsoft.Gradient(startColorStr=#ffffff, endColorStr=#efefef);\n" +
            "    -ms-filter: \"progid:DXImageTransform.Microsoft.Gradient(startColorStr=#FFFFFF, endColorStr=#EFEFEF)\";\n" +
            "    -moz-border-radius: 5px;\n" +
            "    -webkit-border-radius: 5px;\n" +
            "    border-radius: 5px;\n" +
            "    white-space: nowrap;\n" +
            "    vertical-align: middle;\n" +
            "    cursor: pointer;\n" +
            "    overflow: visible;\n" +
            "}\n" +
            "\n" +
            "/*for arrow button in role-right mapping*/\n" +
            ".arrow_btn {\n" +
            "    padding: 0.1em 0.5em;\n" +
            "    border: 1px solid #ccc;\n" +
            "    background: #f6f6f6;\n" +
            "    background-image: -moz-linear-gradient(top, #ffffff, #efefef);\n" +
            "    background-image: -webkit-gradient(linear, left top, left bottom, from(#ffffff), to(#efefef));\n" +
            "    filter: progid:DXImageTransform.Microsoft.Gradient(startColorStr=#ffffff, endColorStr=#efefef);\n" +
            "    -ms-filter: \"progid:DXImageTransform.Microsoft.Gradient(startColorStr=#FFFFFF, endColorStr=#EFEFEF)\";\n" +
            "    -moz-border-radius: 5px;\n" +
            "    -webkit-border-radius: 5px;\n" +
            "    border-radius: 5px;\n" +
            "    white-space: nowrap;\n" +
            "    vertical-align: middle;\n" +
            "    cursor: pointer;\n" +
            "    overflow: visible;\n" +
            "    width: 40px;\n" +
            "    height: 22px;\n" +
            "    text-align: center;\n" +
            "}\n" +
            "\n" +
            ".small_btn:hover {\n" +
            "    border-color: #999;\n" +
            "    background: #f3f3f3;\n" +
            "    outline: 0;\n" +
            "    -moz-box-shadow: 0 0 3px #999;\n" +
            "    -webkit-box-shadow: 0 0 3px #999;\n" +
            "    box-shadow: 0 0 3px #999\n" +
            "}\n" +
            "\n" +
            ".small_btn_top:hover {\n" +
            "    border-color: #999;\n" +
            "    background: #f3f3f3;\n" +
            "    outline: 0;\n" +
            "    -moz-box-shadow: 0 0 3px #999;\n" +
            "    -webkit-box-shadow: 0 0 3px #999;\n" +
            "    box-shadow: 0 0 3px #999\n" +
            "}\n" +
            "\n" +
            "/*for button container*/\n" +
            ".buttons {\n" +
            "    border-bottom: 1px solid #BDC7D8;\n" +
            "    border-top: 1px solid #BDC7D8;\n" +
            "    clear: right;\n" +
            "    color: #666666;\n" +
            "    padding: 5px 8px;\n" +
            "}\n" +
            "\n" +
            "/* class for rolling spinner */\n" +
            ".spinner {\n" +
            "    padding: 5px;\n" +
            "    right: 0;\n" +
            "    clear: both;\n" +
            "    float: left;\n" +
            "    margin-bottom: 30px;\n" +
            "    position: relative;\n" +
            "}\n" +
            "\n" +
            "/*for top right welcome panel*/\n" +
            "div.welcomeText {\n" +
            "    float: right;\n" +
            "    border-bottom: 1px solid #BDC7D8;\n" +
            "    border-left: 1px solid #BDC7D8;\n" +
            "    padding: 3px;\n" +
            "    background-color: #f0f0f2;\n" +
            "    font-size: 12px;\n" +
            "}\n" +
            "\n" +
            "div.welcomeText .textHolder {\n" +
            "    border-bottom-color: #BDC7D8;\n" +
            "    border-bottom-style: solid;\n" +
            "    border-bottom-width: 1px;\n" +
            "    font-weight: bold;\n" +
            "    padding-bottom: 3px;\n" +
            "    padding-left: 6px;\n" +
            "}\n" +
            "\n" +
            "div.welcomeText .buttonHolder {\n" +
            "    padding-top: 3px;\n" +
            "}\n" +
            "\n" +
            "/*for dock menu buttons*/\n" +
            "span.dockMenuText {\n" +
            "    -moz-border-radius: 5px;\n" +
            "    border-radius: 4px;\n" +
            "    padding: 5px 6px;\n" +
            "    margin-left: 15px;\n" +
            "    cursor: pointer;\n" +
            "}\n" +
            "\n" +
            "span.dockMenuTextActive {\n" +
            "    background: #5b74a8;\n" +
            "    padding: 5px 6px;\n" +
            "    color: #FFF;\n" +
            "}\n" +
            "\n" +
            "span.dockMenuText:hover {\n" +
            "    background: #5b74a8;\n" +
            "    padding: 5px 6px;\n" +
            "    color: #FFF;\n" +
            "}\n" +
            "\n" +
            "/*for heading text of each page\n" +
            "currently not used (@todo:clean if not used in future)\n" +
            "*/\n" +
            "div.heading {\n" +
            "    float: left;\n" +
            "    cursor: pointer;\n" +
            "}\n" +
            "\n" +
            "span.headingText {\n" +
            "    -moz-border-radius: 5px 5px 5px 5px;\n" +
            "    border-radius: 7px;\n" +
            "    font-weight: bold;\n" +
            "    padding: 2px 5px 2px 5px;\n" +
            "    margin-left: 15px;\n" +
            "}\n" +
            "\n" +
            "span.headingTextActive {\n" +
            "    background: #5b74a8;\n" +
            "    padding: 2px 5px 2px 5px;\n" +
            "    color: #FFF;\n" +
            "}\n" +
            "\n" +
            "span.headingText:hover {\n" +
            "    background: #5b74a8;\n" +
            "    padding: 2px 5px 2px 5px;\n" +
            "    color: #FFF;\n" +
            "}\n" +
            "\n" +
            "/* External table style of each form*/\n" +
            ".create-form-table {\n" +
            "    border: 1px solid #BDC7D8;\n" +
            "    width: 100%;\n" +
            "    height: 100%;\n" +
            "    border-spacing: 0;\n" +
            "    border-collapse: collapse;\n" +
            "}\n" +
            "\n" +
            "/* internal table style of each form*/\n" +
            ".create-internal-table {\n" +
            "    width: 100%;\n" +
            "    height: 100%;\n" +
            "    border-spacing: 0;\n" +
            "    border-collapse: collapse;\n" +
            "}\n" +
            "\n" +
            "/*style for optional label*/\n" +
            ".label-holder {\n" +
            "    background: none repeat scroll 0 0 #f5f8fb;\n" +
            "    border-bottom: 1px solid #FFFFFF;\n" +
            "    border-right: 1px solid #BDC7D8;\n" +
            "    color: #000000;\n" +
            "    font-size: 12px;\n" +
            "    margin-right: 7px;\n" +
            "    padding: 3px 3px;\n" +
            "    text-align: right;\n" +
            "    vertical-align: top;\n" +
            "    width: 110px;\n" +
            "    white-space: nowrap;\n" +
            "    height: 17px;\n" +
            "}\n" +
            "\n" +
            "/*style for optional label of bottom-most cell*/\n" +
            ".label-holder-last {\n" +
            "    background: none repeat scroll 0 0 #f5f8fb;\n" +
            "    border-bottom: 1px solid #BDC7D8;\n" +
            "    border-right: 1px solid #BDC7D8;\n" +
            "    color: #000000;\n" +
            "    font-size: 12px;\n" +
            "    margin-right: 7px;\n" +
            "    padding: 3px 3px;\n" +
            "    text-align: right;\n" +
            "    vertical-align: top;\n" +
            "    width: 110px;\n" +
            "    white-space: nowrap;\n" +
            "    height: 17px;\n" +
            "}\n" +
            "\n" +
            "/*style for report labels with variable width*/\n" +
            ".label-holder-variable {\n" +
            "    background: none repeat scroll 0 0 #f5f8fb;\n" +
            "    border-bottom: 1px solid #FFFFFF;\n" +
            "    border-right: 1px solid #BDC7D8;\n" +
            "    color: #000000;\n" +
            "    font-size: 12px;\n" +
            "    margin-right: 7px;\n" +
            "    padding: 3px 3px;\n" +
            "    text-align: right;\n" +
            "    vertical-align: top;\n" +
            "    white-space: nowrap;\n" +
            "    height: 17px;\n" +
            "}\n" +
            "\n" +
            "/*for report column header with left alignment(used in budget report)*/\n" +
            ".columnHeaderLeft {\n" +
            "    background: none repeat scroll 0 0 #f5f8fb;\n" +
            "    border-right: 1px solid #BDC7D8;\n" +
            "    color: #333333;\n" +
            "    font-size: 13px;\n" +
            "    margin-right: 7px;\n" +
            "    padding: 3px 3px;\n" +
            "    text-align: left;\n" +
            "    vertical-align: top;\n" +
            "    white-space: nowrap;\n" +
            "    height: 17px;\n" +
            "}\n" +
            "\n" +
            "/*for report column header with right alignment(used in budget report)*/\n" +
            ".columnHeaderRight {\n" +
            "    background: none repeat scroll 0 0 #f5f8fb;\n" +
            "    border-right: 1px solid #BDC7D8;\n" +
            "    color: #333333;\n" +
            "    font-size: 13px;\n" +
            "    margin-right: 7px;\n" +
            "    padding: 3px 3px;\n" +
            "    text-align: right;\n" +
            "    vertical-align: top;\n" +
            "    white-space: nowrap;\n" +
            "    height: 17px;\n" +
            "}\n" +
            "\n" +
            "/*style for mandatory label*/\n" +
            ".label-holder-req {\n" +
            "    background: none repeat scroll 0 0 #ffdfdf;\n" +
            "    border-bottom: 1px solid #FFFFFF;\n" +
            "    border-right: 1px solid #BDC7D8;\n" +
            "    color: #000000;\n" +
            "    font-size: 12px;\n" +
            "    margin-right: 7px;\n" +
            "    padding: 3px 3px;\n" +
            "    text-align: right;\n" +
            "    vertical-align: top;\n" +
            "    width: 110px;\n" +
            "    white-space: nowrap;\n" +
            "    height: 17px;\n" +
            "}\n" +
            "\n" +
            "/*style for mandatory label of bottom-most cell*/\n" +
            ".label-holder-req-last {\n" +
            "    background: none repeat scroll 0 0 #ffdfdf;\n" +
            "    border-bottom: 1px solid #BDC7D8;\n" +
            "    border-right: 1px solid #BDC7D8;\n" +
            "    color: #000000;\n" +
            "    font-size: 12px;\n" +
            "    margin-right: 7px;\n" +
            "    padding: 3px 3px;\n" +
            "    text-align: right;\n" +
            "    vertical-align: top;\n" +
            "    width: 110px;\n" +
            "    white-space: nowrap;\n" +
            "    height: 17px;\n" +
            "}\n" +
            "\n" +
            "/*style for flexgrid buttons*/\n" +
            ".flexigrid div.nBtn,\n" +
            ".flexigrid div.nBtn div {\n" +
            "    height: 28px !important;\n" +
            "}\n" +
            "\n" +
            "/*for jquery validator style*/\n" +
            "li.error {\n" +
            "    color: #ff0000;\n" +
            "    font: Arial, Helvetica, sans-serif;\n" +
            "}\n" +
            "\n" +
            ".error {\n" +
            "    color: #ff0000;\n" +
            "    font: Arial, Helvetica, sans-serif;\n" +
            "}\n" +
            "\n" +
            "#errorList li {\n" +
            "    color: #ff0000;\n" +
            "}\n" +
            "\n" +
            "/* style for left menu link */\n" +
            ".menuText {\n" +
            "    padding-left: 32px;\n" +
            "    color: #333333;\n" +
            "}\n" +
            "\n" +
            "/* style for left sub-menu link */\n" +
            ".menuTextSub {\n" +
            "    padding-left: 50px;\n" +
            "    color:#333333;\n" +
            "}\n" +
            "\n" +
            "/*style for date text fields*/\n" +
            "input[type=text].dateMask {\n" +
            "    width: 75px;\n" +
            "}\n" +
            "\n" +
            "/*for table cell*/\n" +
            ".create-form-field {\n" +
            "    padding: 3px;\n" +
            "    vertical-align: top;\n" +
            "    font-size: 12px;\n" +
            "}\n" +
            "\n" +
            "/*for top-right logout,change password links*/\n" +
            ".linkText a:link, .linkText a:visited {\n" +
            "    -moz-border-radius: 4px;\n" +
            "    border-radius: 4px;\n" +
            "    padding: 2px 6px\n" +
            "}\n" +
            "\n" +
            ".linkText a:hover {\n" +
            "    background: #5b74a8;\n" +
            "    color: #FFF;\n" +
            "}\n" +
            "\n" +
            "/*for top left company logo*/\n" +
            "div.app-logo {\n" +
            "    float: left;\n" +
            "    width: 425px;\n" +
            "    height: 53px;\n" +
            "    background: #f2f5fa 0 2px no-repeat;\n" +
            "}\n" +
            "\n" +
            "/*for top left dash board link*/\n" +
            "a#home-link {\n" +
            "    display: block;\n" +
            "    height: 53px;\n" +
            "    left: 0;\n" +
            "    outline: medium none;\n" +
            "    position: absolute;\n" +
            "    top: 0;\n" +
            "    width: 425px;\n" +
            "}\n" +
            "\n" +
            "/*for top right header place holder*/\n" +
            "div.powered-by-athena {\n" +
            "    float: right;\n" +
            "    width: 526px;\n" +
            "    height: 53px;\n" +
            "    background: no-repeat right;\n" +
            "}\n" +
            "\n" +
            ".flexDiv div {\n" +
            "    margin: 0 0 0 0;\n" +
            "}\n" +
            "\n" +
            "/*custom css for jquery layout; used in voucher,PR & other pages*/\n" +
            ".outer-east, .outer-center, .middle-north, .middle-center {\n" +
            "    width: 100%;\n" +
            "    height: 100%;\n" +
            "    padding: 0;\n" +
            "    margin: 0;\n" +
            "    overflow: hidden;\n" +
            "    position: relative;\n" +
            "    border-color: #FFFFFF;\n" +
            "}\n" +
            "\n" +
            "/*for dash board container*/\n" +
            "div.dashboardContainer {\n" +
            "    width: 100%;\n" +
            "    height: 100%;\n" +
            "    margin: 0 0 0 20px\n" +
            "}\n" +
            "\n" +
            "/* custom css for jquery UI\n" +
            "remove padding and scrolling from elements that contain an Accordion OR a content-div */\n" +
            ".ui-layout-center, /* has content-div */\n" +
            ".ui-layout-west, /* has Accordion */\n" +
            ".ui-layout-east, /* has content-div ... */\n" +
            ".ui-layout-east .ui-layout-content {\n" +
            "    /* content-div has Accordion */\n" +
            "    padding: 0;\n" +
            "    overflow: hidden;\n" +
            "}\n" +
            "\n" +
            ".ui-layout-center P.ui-layout-content {\n" +
            "    line-height: 1.4em;\n" +
            "    margin: 0; /* remove top/bottom margins from <P> used as content-div */\n" +
            "}\n" +
            "\n" +
            "/*for left menu*/\n" +
            ".menuDiv {\n" +
            "    margin: 0;\n" +
            "    padding: 0;\n" +
            "    line-height: 14px;\n" +
            "    /*font-size: 12px;*/\n" +
            "    color: #385495;\n" +
            "    text-decoration: none;\n" +
            "    white-space: nowrap;\n" +
            "}\n" +
            "\n" +
            "ul.menuDiv {\n" +
            "    list-style: none;\n" +
            "    margin: 0;\n" +
            "    padding: 0;\n" +
            "}\n" +
            "\n" +
            "ul.menuDivSub {\n" +
            "    list-style: none;\n" +
            "    margin: 0;\n" +
            "    padding: 1px 0 0;\n" +
            "}\n" +
            "\n" +
            "/* unvisited link */\n" +
            "ul.menuDiv li a:link {\n" +
            "    /*font-size: 80%;*/\n" +
            "    display: block;\n" +
            "    padding: 3px 0 5px 4px;\n" +
            "    text-decoration: none;\n" +
            "    color: #666666;\n" +
            "    width: 98%;\n" +
            "}\n" +
            "\n" +
            "ul.menuDiv li a:visited {\n" +
            "    /*font-size: 80%;*/\n" +
            "    display: block;\n" +
            "    padding: 3px 0 5px 4px;\n" +
            "    text-decoration: none;\n" +
            "    color: #666666;\n" +
            "    width: 98%;\n" +
            "}\n" +
            "\n" +
            "ul.menuDiv li a.selected {\n" +
            "    color: #000000;\n" +
            "    background-color: #eeeeff;\n" +
            "}\n" +
            "\n" +
            "ul.menuDiv li a:hover {\n" +
            "    color: #000000;\n" +
            "    background-color: #eeeeff;\n" +
            "}\n" +
            "\n" +
            "ul.menuDivSub li a:link {\n" +
            "    /*font-size: 80%;*/\n" +
            "    display: block;\n" +
            "    padding: 3px 0 3px 4px;\n" +
            "    text-decoration: none;\n" +
            "    color: #666666;\n" +
            "    width: 98%;\n" +
            "}\n" +
            "\n" +
            "ul.menuDivSub li a:visited {\n" +
            "    /*font-size: 80%;*/\n" +
            "    display: block;\n" +
            "    padding: 3px 0 3px 4px;\n" +
            "    text-decoration: none;\n" +
            "    color: #666666;\n" +
            "    width: 98%;\n" +
            "}\n" +
            "\n" +
            "ul.menuDivSub li a:hover {\n" +
            "    color: #000000;\n" +
            "    background-color: #c7f2f3;\n" +
            "}\n" +
            "\n" +
            "div.pGroup > select {\n" +
            "    width: auto;\n" +
            "}\n" +
            "span.pcontrol > input[type=\"text\"] {\n" +
            "    width: auto;\n" +
            "}\n" +
            "\n" +
            "/*Temporary fix For Tab\n" +
            "@todo: eliminate when bootstrap-tab implemented\n" +
            "*/\n" +
            ".ui-helper-clearfix{\n" +
            "    font-size: 12px;\n" +
            "}\n" +
            ".ui-helper-reset{\n" +
            "    line-height: 1;\n" +
            "}\n" +
            ".ui-tabs .ui-tabs-nav li a {\n" +
            "    padding: 0.5em 1.5em;\n" +
            "}\n" +
            "\n" +
            "/*Override label of bootstrap\n" +
            "@todo: eliminate when bootstrap implemented\n" +
            "*/\n" +
            "label{\n" +
            "    font-weight: normal;\n" +
            "    margin-bottom: 0;\n" +
            "}\n" +
            "\n" +
            "/*Fix the jquery accordion\n" +
            "@todo: eliminate when bootstrap implemented\n" +
            "*/\n" +
            "#accordion1 > .ui-helper-reset{\n" +
            "    line-height: 1.6;\n" +
            "}\n" +
            "\n" +
            "/*For dashboard fix\n" +
            "@todo: temp fix for bootstrap implemented\n" +
            "*/\n" +
            "ul#tabs li a {\n" +
            "    height: 58px;\n" +
            "}\n" +
            "div#content{\n" +
            "    width: 875px;\n" +
            "    margin: 50px auto auto 65px;\n" +
            "}\n" +
            "\n" +
            "/*flex grid adjustments\n" +
            "@todo: temp adjustment due to bootstrap\n" +
            "*/\n" +
            "div.flexigrid {\n" +
            "    width: 100%;\n" +
            "}\n" +
            ".flexigrid {\n" +
            "    font-size: 12px;\n" +
            "    font-family: \"lucida grande\",tahoma,verdana,arial,sans-serif;\n" +
            "}\n" +
            ".flexigrid table {\n" +
            "    border-collapse: inherit;\n" +
            "}\n" +
            ".flexigrid div.hDiv th div, .flexigrid div.bDiv td div, div.colCopy div {\n" +
            "    padding: 0.40em;\n" +
            "}\n" +
            ".flexigrid div.fbutton span {\n" +
            "    /*padding: 0;*/\n" +
            "}\n" +
            "\n" +
            "/*For html report fields; temp fix for bootstrap*/\n" +
            ".info_box{\n" +
            "    font-size: 12px;\n" +
            "}\n" +
            "\n" +
            "/* Style for label required */\n" +
            ".label-required {\n" +
            "  color:red\n" +
            "}\n" +
            "\n" +
            "#dockMenuContainer ul li {\n" +
            "  cursor:pointer;\n" +
            "}\n" +
            "#dockMenuContainer ul.nav > li > a {\n" +
            "  padding: 7px 14px;\n" +
            "}"

    private static final String VALUE_CSS_BOOTSTRAP_CUSTOM = ".panel-primary > .panel-heading {\n" +
            "     background-color: #F5F5F5;\n" +
            "     border-color: #CCCCCC;\n" +
            "     color: #515967;\n" +
            "}\n" +
            "\n" +
            ".panel-footer {\n" +
            "     border-top: 1px solid #cccccc;\n" +
            " }\n" +
            "\n" +
            ".panel-title {\n" +
            "    font-size: 15px;\n" +
            "}\n" +
            "\n" +
            ".panel-primary {\n" +
            "    border-color: #cccccc;\n" +
            "}\n" +
            "\n" +
            ".panel-heading {\n" +
            "    padding: 5px 15px;\n" +
            "}\n" +
            "\n" +
            ".panel-footer {\n" +
            "    padding: 5px 15px;\n" +
            "}\n" +
            "\n" +
            ".form-group {\n" +
            "    margin-bottom: 7px;\n" +
            "}\n" +
            "\n" +
            "body {\n" +
            "    font-size: 13px;\n" +
            "     line-height: 1.2;\n" +
            "}\n" +
            "\n" +
            ".form-horizontal .control-label {\n" +
            "   padding-top: 2px;\n" +
            "}\n" +
            "\n" +
            ".panel-body {\n" +
            "    padding: 7px;\n" +
            "}\n" +
            "\n" +
            ".panel {\n" +
            "    margin-bottom: 7px;\n" +
            "}\n" +
            "\n" +
            ".table {\n" +
            "  margin-bottom:7px;\n" +
            "}\n" +
            "\n" +
            "/* For fixed width of html report label */\n" +
            "td.active {\n" +
            "  width:15%\n" +
            "}"

    private static final String VALUE_CSS_KENDO_CUSTOM = "/*Application Kendo CSS is used to overwrite the default behaviour of kendo ui components*/\n" +
            "\n" +
            "input[type=text].k-textbox,input[type=password].k-textbox {\n" +
            "    height: 1.8em;\n" +
            "}\n" +
            ".k-textbox[type=text]:disabled,.k-textbox[type=password]:disabled,textarea:hover:disabled,textarea:disabled {\n" +
            "    background-color: #E3E3E3;\n" +
            "    border-color: #C5C5C5;\n" +
            "    color: #9F9E9E;\n" +
            "    opacity: 0.7;\n" +
            "    cursor: default;\n" +
            "    outline: 0 none\n" +
            "}\n" +
            ".k-menu .k-item > .k-link {\n" +
            "    padding: 0 0.9em 0;\n" +
            "}\n" +
            "\n" +
            ".panel .k-widget,\n" +
            ".panel .k-textbox {\n" +
            "    width: 100%;\n" +
            "}\n" +
            "\n" +
            "/* Omit kendo behaviors on List view of html Reports */\n" +
            "tbody.k-widget {\n" +
            "  border-style:none;\n" +
            "  position:static;\n" +
            "}\n"+
            "span.k-tooltip {\n" +
            "    padding: 2px 0;\n" +
            "}"

    private static final String VALUE_BUSINESS_SUPPORT = "<div class=\"col-md-4\">\n" +
            "    <div class=\"panel panel-default\">\n" +
            "        <div class=\"panel-heading\">\n" +
            "            <div class=\"panel-title\">Service</div>\n" +
            "        </div>\n" +
            "\n" +
            "        <div class=\"panel-body\">\n" +
            "            <div>No two businesses are alike.</div>\n" +
            "\n" +
            "            <div>&nbsp;</div>\n" +
            "\n" +
            "            <div>Athena offers fully customized solutions to meet the needs of your growing business and to help your company provide a unique and exceptional service to your customers</div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"col-md-4\">\n" +
            "    <div class=\"panel panel-default\">\n" +
            "        <div class=\"panel-heading\">\n" +
            "            <div class=\"panel-title\">Modules</div>\n" +
            "        </div>\n" +
            "\n" +
            "        <div class=\"panel-body\">\n" +
            "            <div>1. Budget</div>\n" +
            "\n" +
            "            <div>2. Procurement</div>\n" +
            "\n" +
            "            <div>3. Inventory</div>\n" +
            "\n" +
            "            <div>4. Accounting</div>\n" +
            "\n" +
            "            <div>&nbsp;</div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</div>\n" +
            "\n" +
            "<div class=\"col-md-4\">\n" +
            "    <div class=\"panel panel-default\">\n" +
            "        <div class=\"panel-heading\">\n" +
            "            <div class=\"panel-title\">Support</div>\n" +
            "        </div>\n" +
            "\n" +
            "        <div class=\"panel-body\">\n" +
            "            <div>Financial software needs support and updates as always.</div>\n" +
            "\n" +
            "            <div>&nbsp;</div>\n" +
            "\n" +
            "            <div>Our support includes data security, on demand report generation, regular   backup and periodic updates to ensure safely, security and keep you   always on track.</div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "</div>"

    private static final String VALUE_BUSINESS_SUPPORT_FOR_EXH = "<div class=\"col-md-6\">\n" +
            "        <div class=\"form-group\">\n" +
            "            <div class=\"flexslider\">\n" +
            "                <ul class=\"slides\">\n" +
            "                    <li>\n" +
            "                        <img src=\"/plugins/applicationplugin-0.1/theme/application/images/flexslider/slide1.jpg\"\n" +
            "                             width=\"100%\"/>\n" +
            "                    </li>\n" +
            "                    <li>\n" +
            "                        <img src=\"/plugins/applicationplugin-0.1/theme/application/images/flexslider/slide2.jpg\"\n" +
            "                             width=\"100%\"/>\n" +
            "                    </li>\n" +
            "                </ul>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "\n" +
            "    <div class=\"col-md-6\">\n" +
            "        <div class=\"panel panel-default\">\n" +
            "            <div class=\"panel-heading\">\n" +
            "                <div class=\"panel-title\">Service</div>\n" +
            "            </div>\n" +
            "\n" +
            "            <div class=\"panel-body\">\n" +
            "                <div>No two businesses are alike.</div>\n" +
            "\n" +
            "                <div>&nbsp;</div>\n" +
            "\n" +
            "                <div>Athena offers fully\n" +
            "                customized solutions to meet the needs of your growing business and to\n" +
            "                help your company provide a unique and exceptional service to your\n" +
            "                customers.</div>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "\n" +
            "        <div class=\"panel panel-default\">\n" +
            "            <div class=\"panel-heading\">\n" +
            "                <div class=\"panel-title\">Support</div>\n" +
            "            </div>\n" +
            "\n" +
            "            <div class=\"panel-body\">\n" +
            "                <div>Financial software needs support and updates as always.</div>\n" +
            "\n" +
            "                <div>&nbsp;</div>\n" +
            "\n" +
            "                <div>Our support\n" +
            "                includes data security, on demand report generation, regular   backup\n" +
            "                and periodic updates to ensure safely, security and keep you   always on\n" +
            "                track.</div>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "    </div>"

    private static final String VALUE_ADVERTISING_PHRASE = "The Most Secured, Reliable, Scalable, Web2.0 Solution"

    private static final String VALUE_KENDO_THEME = '<link rel="stylesheet" href="/plugins/applicationplugin-0.1/theme/application/css/kendo/kendo.silver.min.css"/>'

    /**
     * Method to create default data for MIS plugin
     */
    @Transactional
    public void createDefaultData(long companyId) {
        if (PluginConnector.isPluginInstalled(PluginConnector.EXCHANGE_HOUSE)){
            createDefaultDataExchangeHouse(companyId)
            return
        }
        new Theme(key: themeCacheUtility.KEY_WELCOME_TEXT, value: "Welcome to MIS", companyId: companyId, description: 'Welcome title of the company').save(false)
        new Theme(key: themeCacheUtility.KEY_COMPANY_LOGO_LEFTMENU, value: "/images/athena_log.jpg", companyId: companyId, description: 'Company logo on left menu panel').save(false)
        new Theme(key: themeCacheUtility.KEY_COMPANY_NAME, value: "Athena Software Associates Ltd.", companyId: companyId, description: 'Name of the company').save(false)
        new Theme(key: themeCacheUtility.KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel').save(false)
        new Theme(key: themeCacheUtility.KEY_COMPANY_COPYRIGHT_LOGIN, value: "Copyright &copy; 2012 Athena Software Associates Ltd. All rights reserved.", companyId: companyId, description: 'Company copy right text for login page').save(false)
        new Theme(key: themeCacheUtility.KEY_PRODUCT_NAME, value: "Corolla MIS", companyId: companyId, description: 'Name of the product').save(false)
        new Theme(key: themeCacheUtility.KEY_COMPANY_WEBSITE, value: "http://www.athena.com.bd", companyId: companyId, description: 'URL of the company web site').save(false)
        new Theme(key: themeCacheUtility.KEY_IMG_LOGIN_TOP_RIGHT, value: "/images/login/top_right_mis.png", companyId: companyId, description: 'Top right image of login page').save(false)
        new Theme(key: themeCacheUtility.KEY_IMG_TOP_PANEL_LEFT, value: "/images/corolla_index_logo.png", companyId: companyId, description: 'Top left panel image').save(false)
        new Theme(key: themeCacheUtility.KEY_IMG_TOP_PANEL_RIGHT, value: "/images/corolla_header_banner.jpg", companyId: companyId, description: 'Top right panel image').save(false)
        new Theme(key: themeCacheUtility.KEY_CSS_MAIN_COMPONENTS, value: VALUE_CSS_MAIN_COMPONENTS, companyId: companyId, description: 'The CSS that defines styles of html controls throughout the project. Whole CSS is rendered dynamically in main template').save(false)
        new Theme(key: themeCacheUtility.KEY_CSS_BOOTSTRAP_CUSTOM, value: VALUE_CSS_BOOTSTRAP_CUSTOM, companyId: companyId, description: 'The CSS overwrites styles of default Bootstrap. Whole CSS is rendered dynamically in main template').save(false)
        new Theme(key: themeCacheUtility.KEY_CSS_KENDO_CUSTOM, value: VALUE_CSS_KENDO_CUSTOM, companyId: companyId, description: 'The CSS overwrites styles of default Kendo. Whole CSS is rendered dynamically in main template').save(false)
        new Theme(key: themeCacheUtility.KEY_LOGIN_PAGE_CAUTION, value: "&nbsp;", companyId: companyId, description: 'The text displayed above business support on login page').save(false)
        new Theme(key: themeCacheUtility.KEY_BUSINESS_SUPPORT, value: VALUE_BUSINESS_SUPPORT, companyId: companyId, description: 'Description of business support').save(false)
        new Theme(key: themeCacheUtility.KEY_ADVERTISING_PHRASE, value: VALUE_ADVERTISING_PHRASE, companyId: companyId, description: 'Advertising phrase').save(false)
        new Theme(key: themeCacheUtility.KEY_KENDO_THEME, value: VALUE_KENDO_THEME, companyId: companyId, description: 'DEFAULT = /plugins/applicationplugin-0.1/css/kendo/kendo.default.min.css<br>\n' +
                'SILVER = /plugins/applicationplugin-0.1/theme/application/css/kendo/kendo.silver.min.css<br>\n' +
                'BOOTSTRAP = /plugins/applicationplugin-0.1/theme/application/css/kendo/kendo.bootstrap.min.css<br>\n' +
                'BLUEOPAL = /plugins/applicationplugin-0.1/theme/application/css/kendo/kendo.blueopal.min.css<br>\n' +
                'BLACK = /plugins/applicationplugin-0.1/theme/application/css/kendo/kendo.black.min.css').save(false)
    }

    /**
     * Method to create default data for Exchange house plugin
     */
    public void createDefaultDataExchangeHouse(long companyId) {
        new Theme(key: themeCacheUtility.KEY_WELCOME_TEXT, value: "Welcome to ARMS(Agent)", companyId: companyId, description: 'Welcome title of the company').save(false)
        new Theme(key: themeCacheUtility.KEY_COMPANY_LOGO_LEFTMENU, value: "/images/athena_log.jpg", companyId: companyId, description: 'Company logo on left menu panel').save(false)
        new Theme(key: themeCacheUtility.KEY_COMPANY_NAME, value: "Athena Software Associates Ltd.", companyId: companyId, description: 'Name of the company').save(false)
        new Theme(key: themeCacheUtility.KEY_COMPANY_COPYRIGHT_LEFTMENU, value: "Athena Software </br>Associates Ltd.© 2012", companyId: companyId, description: 'Company copy right text for left menu panel').save(false)
        new Theme(key: themeCacheUtility.KEY_COMPANY_COPYRIGHT_LOGIN, value: "Copyright &copy; 2012 Athena Software Associates Ltd. All rights reserved.", companyId: companyId, description: 'Company copy right text for login page').save(false)
        new Theme(key: themeCacheUtility.KEY_PRODUCT_NAME, value: "Athena Remittance Management System", companyId: companyId, description: 'Name of the product').save(false)
        new Theme(key: themeCacheUtility.KEY_COMPANY_WEBSITE, value: "http://www.athena.com.bd", companyId: companyId, description: 'URL of the company web site').save(false)
        new Theme(key: themeCacheUtility.KEY_IMG_LOGIN_TOP_RIGHT, value: "/images/login/top_right_exh.png", companyId: companyId, description: 'Top right image of login page').save(false)
        new Theme(key: themeCacheUtility.KEY_IMG_TOP_PANEL_LEFT, value: "/images/sfsa_index_logo.png", companyId: companyId, description: 'Top left panel image').save(false)
        new Theme(key: themeCacheUtility.KEY_IMG_TOP_PANEL_RIGHT, value: "/images/sebl_header_banner.jpg", companyId: companyId, description: 'Top right panel image').save(false)
        new Theme(key: themeCacheUtility.KEY_CSS_MAIN_COMPONENTS, value: VALUE_CSS_MAIN_COMPONENTS, companyId: companyId, description: 'The CSS that defines styles of html controls throughout the project. Whole CSS is rendered dynamically in main template').save(false)
        new Theme(key: themeCacheUtility.KEY_CSS_BOOTSTRAP_CUSTOM, value: VALUE_CSS_BOOTSTRAP_CUSTOM, companyId: companyId, description: 'The CSS overwrites styles of default Bootstrap. Whole CSS is rendered dynamically in main template').save(false)
        new Theme(key: themeCacheUtility.KEY_CSS_KENDO_CUSTOM, value: VALUE_CSS_KENDO_CUSTOM, companyId: companyId, description: 'The CSS overwrites styles of default Kendo. Whole CSS is rendered dynamically in main template').save(false)
        new Theme(key: themeCacheUtility.KEY_LOGIN_PAGE_CAUTION, value: "'Southeast Financial Services- 02077902434' will be displayed on your bank or card statement for all transactions processed through this website.", companyId: companyId, description: 'The text displayed above business support on login page').save(false)
        new Theme(key: themeCacheUtility.KEY_BUSINESS_SUPPORT, value: VALUE_BUSINESS_SUPPORT_FOR_EXH, companyId: companyId, description: 'Description of business support').save(false)
        new Theme(key: themeCacheUtility.KEY_ADVERTISING_PHRASE, value: VALUE_ADVERTISING_PHRASE, companyId: companyId, description: 'Advertising phrase').save(false)
        new Theme(key: themeCacheUtility.KEY_KENDO_THEME, value: VALUE_KENDO_THEME, companyId: companyId, description: 'DEFAULT = /plugins/applicationplugin-0.1/css/kendo/kendo.default.min.css<br>\n' +
                'SILVER = /plugins/applicationplugin-0.1/theme/application/css/kendo/kendo.silver.min.css<br>\n' +
                'BOOTSTRAP = /plugins/applicationplugin-0.1/theme/application/css/kendo/kendo.bootstrap.min.css<br>\n' +
                'BLUEOPAL = /plugins/applicationplugin-0.1/theme/application/css/kendo/kendo.blueopal.min.css<br>\n' +
                'BLACK = /plugins/applicationplugin-0.1/theme/application/css/kendo/kendo.black.min.css').save(false)
    }
}
