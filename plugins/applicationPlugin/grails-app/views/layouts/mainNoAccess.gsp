<%@ page import="com.athena.mis.utility.UIConstants; grails.util.Environment;" %>
<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="language" content="en"/>
    <title><g:layoutTitle default="MIS"/></title>
    <link rel="shortcut icon" href="${ConfigurationHolder.config.theme.application}/images/favicon.ico"
          type="image/x-icon"/>
    <g:render template='/layouts/commonInclude'/>
    <script type="text/javascript">

        $(document).ready(function () {
            var logoutUrl = "<g:createLink controller="logout"/>"
            onLoadNoAccessLayout(logoutUrl);
        });
        function onLoadNoAccessLayout() {
            myLayout = $('body').layout({

                east__size:            300
                // RESIZE Accordion widget when panes resize
                ,    west__onresize:        function () {
                    $("#accordion1").accordion("resize");
                }
                ,    east__onresize:        function () {
                    $("#accordion2").accordion("resize");
                }
                ,    south__resizable: false
                ,    south__slidable: false
                ,    south__togglerLength_open:        0
                ,    north__size:55
                ,    north__resizable: false
                ,   south__showOverflowOnHover: false
                ,   west__size: 230
                ,     center__contentSelector:'#contentHolder'


            });

            // ACCORDION - in the West pane
            $("#accordion1").accordion({
                fillSpace:    true
                ,    active:  0
            });

            setFavicon();
            $.ajaxSetup({
                error:function(x, e) {
                    if (x.status == 0) {
                        alert('You are offline now !!\n Please Check Your Network Connection.');
                    } else if (x.status == 404) {
                        showLoadingSpinner(false);
                        window.location = "<g:createLink controller="logout"/>";
                    } else if (x.status == 500) {
                        window.location = "<g:createLink controller="logout"/>";
                    } else if (e == 'parsererror') {
                        alert('Error.\nParsing JSON Request failed.');
                    } else if (e == 'timeout') {
                        alert('Request Time out.');
                    } else {
                        alert('Unknow Error.\n' + x.responseText);
                    }
                }
            });
            try {
                errorData = $("<span></span>").html(errorData).text();
                errorData = errorData.toJSON();
            } catch (e) {
                errorData = false;
            }

            if (errorData) {
                $(errorData).each(function(idx) {
                    showError(errorData[idx]);
                });
            }
        }

    </script>

    <g:layoutHead/>



</head>

<body>
<div class="ui-layout-north" style="background:none repeat scroll 0 0 #f2f5fa;margin:0;padding:0; ">
    <div class='app-logo'>
        <a id='home-link' title="Dashboard" href="${createLink(uri: '/')}"></a>
    </div>

    <div class='powered-by-athena'>
        <sec:ifLoggedIn>
            <div class="welcomeText">
                <div class="" style="font-weight: bold;">Welcome, <app:sessionUser property='username'></app:sessionUser>
                </div>
                <div class="buttonHolder">
                    <sec:access url="/appUser/managePassword">
                        <a id='managePassword' href="${createLink(controller: 'appUser', action: 'managePassword')}">Change Password</a>
                    </sec:access>    |
                    <a href="<g:createLink controller="logout"/>">Logout</a></div>
            </div>
        </sec:ifLoggedIn>
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


<div class="ui-layout-west" style="display: none;">
    <div id="accordion1" class="basic">
    </div>

    <div style="height:60px; background:url('${ConfigurationHolder.config.theme.application}/images/logobg.jpg') repeat-x;">
        <div style="width:55px; float:right"><a title="<app:themeContent name="app.companyName"></app:themeContent>" href="<app:themeContent name="app.companyWebsite"></app:themeContent>"
                                                target="_blank"><img
                    src="${ConfigurationHolder.config.theme.application}<app:themeContent name="app.leftMenu.CompanyLogo"></app:themeContent>"
                    width="55" height="60"
                    border="0"></a>
        </div>

        <div style="width:80%; float:left; padding-top:20px; padding-left:10px;position: absolute;"><table
                style="font-family : Arial, Helvetica, sans-serif;font-size : 12px;font-weight:bold;color : #3b5998;"
                width="100%" border="0" cellspacing="0" cellpadding="0">
            <tr>
                <td><app:themeContent name="app.leftMenu.companyCopyright"></app:themeContent></td>
            </tr>
        </table></div>
    </div>
</div>

<div id="temDiv" style="visibility: hidden;"></div>
</body>
</html>

