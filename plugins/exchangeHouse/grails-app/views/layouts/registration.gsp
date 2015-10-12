<%@ page import="com.athena.mis.application.config.ThemeCacheUtility; grails.util.Environment;" %>
<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="cache-control" content="public"/>
    <link rel="shortcut icon" href="${ConfigurationHolder.config.theme.application}/images/favicon_arms.ico" type="image/x-icon"/>

    <g:render plugin="applicationplugin" template='/layouts/commonInclude'/>
    <app:themeContent name="${ThemeCacheUtility.KEY_CSS_MAIN_COMPONENTS}" css="true"></app:themeContent>
    <app:themeContent name="${ThemeCacheUtility.KEY_CSS_KENDO_CUSTOM}" css="true"></app:themeContent>
    <app:themeContent name="${ThemeCacheUtility.KEY_CSS_BOOTSTRAP_CUSTOM}" css="true"></app:themeContent>

    <script type="text/javascript">

        var myLayout; // init global vars

        $(document).ready(function () {
            var logoutUrl = '<g:createLink controller="logout"/>';
            onLoadNoAccessLayout(logoutUrl);
        });
        function onLoadNoAccessLayout() {
            myLayout = $('body').layout({

                east__size: 300
                // RESIZE Accordion widget when panes resize
                , west__onresize: function () {
                    $("#accordion1").accordion("resize");
                }, east__onresize: function () {
                    $("#accordion2").accordion("resize");
                }, south__resizable: false, south__slidable: false, south__togglerLength_open: 0, north__size: 55, north__resizable: false, south__showOverflowOnHover: false, west__size: 0, center__contentSelector: '#contentHolder'


            });

            // ACCORDION - in the West pane
            $("#accordion1").accordion({
                fillSpace: true, active: 0
            });

            setFavicon();
        }

    </script>
    <g:layoutHead/>
</head>

<body>

<div class="ui-layout-north" style="background:none repeat scroll 0 0 #f2f5fa;margin:0;padding:0; ">
    <div class='app-logo'>
        <div class='app-logo' style="background-image:url(${ConfigurationHolder.config.theme.application}<app:themeContent
                name="app.imgTopPanelLeft"></app:themeContent>)">
            <a id='home-link' title="HomePage" href="${createLink(uri: '/')}"></a>
        </div>
    </div>

    <div class='powered-by-athena'>

    </div>
</div>

<div class="ui-layout-center" style="display: none;">
    <div class='pane_title' style="min-width:550px;">
        <div class="heading">
        </div>

        <div id="spinner" style='float:left;margin-left:4px;display:none; margin-top: 1px'><img
                src="${ConfigurationHolder.config.theme.application}/images/spinner.gif" alt="Spinner"/></div>

        <div id="dockMenuContainer" style="float: right;">
        </div>

    </div>

    <div id='contentHolder' style='padding:5px; overflow: auto;position: absolute'>
        <g:layoutBody/>
    </div>
</div>

<div id="temDiv" style="visibility: hidden;"></div>
</body>
</html>