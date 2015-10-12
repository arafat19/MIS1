<%@ page import="com.athena.mis.application.config.ThemeCacheUtility; grails.util.Environment;" %>
<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="cache-control" content="public"/>
    <link rel="shortcut icon" href="${ConfigurationHolder.config.theme.application}/images/favicon_arms.ico"
          type="image/x-icon"/>

    <g:render plugin="applicationplugin" template='/layouts/commonInclude'/>
    <app:themeContent name="${ThemeCacheUtility.KEY_CSS_MAIN_COMPONENTS}" css="true"></app:themeContent>
    <app:themeContent name="${ThemeCacheUtility.KEY_CSS_KENDO_CUSTOM}" css="true"></app:themeContent>
    <app:themeContent name="${ThemeCacheUtility.KEY_CSS_BOOTSTRAP_CUSTOM}" css="true"></app:themeContent>

    <g:layoutHead/>
</head>

<body>
<div id='contentHolder' style='overflow-y:auto;overflow-x:auto;padding:5px;width: 100%'>
    <g:layoutBody/>
</div>
</body>
</html>