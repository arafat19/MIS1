<%@ page import="com.athena.mis.application.config.ThemeCacheUtility; org.codehaus.groovy.grails.commons.ConfigurationHolder" %>

<script type="text/javascript" src="${ConfigurationHolder.config.theme.application}/js/jquery/jquery-1.8.1.min.js"></script>
<script type="text/javascript" src="${ConfigurationHolder.config.theme.application}/js/jquery/ui-1.8.1.custom.js"></script>
<script type="text/javascript" src="${ConfigurationHolder.config.theme.application}/js/jquery/layout-latest.js"></script>
<script type="text/javascript" src="${ConfigurationHolder.config.theme.application}/js/flexigrid/flexigrid.js"></script>
<script type="text/javascript" src="${ConfigurationHolder.config.theme.application}/js/flexigrid/extended.js"></script>
<script type="text/javascript" src="${ConfigurationHolder.config.theme.application}/js/jquery/maskedinput-1.2.2.js"></script>
<script type="text/javascript" src="${ConfigurationHolder.config.theme.application}/js/dateutil.js"></script>
<script type="text/javascript" src="${ConfigurationHolder.config.theme.application}/js/jquery/iframe-post-form.js"></script>
<script type="text/javascript" src="${ConfigurationHolder.config.theme.application}/js/jquery/jgrowl.js"></script>
<script type="text/javascript" src="${ConfigurationHolder.config.theme.application}/js/application.js"></script>
<script type="text/javascript" src="${ConfigurationHolder.config.theme.application}/js/jquery/jquery.history.js"></script>
<script type="text/javascript" src="${ConfigurationHolder.config.theme.application}/js/jquery/featureList-1.0.0.js"></script>
<script type="text/javascript" src="${ConfigurationHolder.config.theme.application}/js/kendo/kendo.web.min.js"></script>
<script type="text/javascript" src="${ConfigurationHolder.config.theme.application}/js/bootstrap/js/bootstrap.js"></script>

<link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css/jquery/accordion.css"/>
<link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css/layout-default-latest.css"/>
<link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css/jquery/jgrowl.css"/>
<link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css/jquery/ui-1.8.1.custom.css"/>
<link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css/jquery/featurelist.css"/>
<app:themeContent
        name="${ThemeCacheUtility.KEY_KENDO_THEME}">
</app:themeContent>
<link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css/kendo/kendo.common.min.css"/>
<link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css/font-awesome-4.1.0/css/font-awesome.min.css"/>
<link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css//bootstrap/css/bootstrap.css"/>
<link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css/main-image.css"/>
<link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css/jquery/flexigrid.css"/>
<app:themeContent name="${ThemeCacheUtility.KEY_CSS_MAIN_COMPONENTS}" css="true"></app:themeContent>
<app:themeContent name="${ThemeCacheUtility.KEY_CSS_KENDO_CUSTOM}" css="true"></app:themeContent>
<app:themeContent name="${ThemeCacheUtility.KEY_CSS_BOOTSTRAP_CUSTOM}" css="true"></app:themeContent>
