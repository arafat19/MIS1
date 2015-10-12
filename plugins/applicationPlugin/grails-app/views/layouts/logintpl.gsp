<%@ page import="com.athena.mis.application.config.ThemeCacheUtility; com.athena.mis.PluginConnector; grails.util.Environment;" %>
<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder;" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns="http://www.w3.org/1999/html">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="cache-control" content="public"/>
    <link rel="shortcut icon" href="${ConfigurationHolder.config.theme.application}/images/favicon_arms.ico"
          type="image/x-icon"/>
    <title>Application Login</title>
    <script type="text/javascript"
            src="${ConfigurationHolder.config.theme.application}/js/jquery/jquery-1.8.1.min.js"></script>
    <script type="text/javascript"
            src="${ConfigurationHolder.config.theme.application}/js/kendo/kendo.web.min.js"></script>
    <script type="text/javascript"
            src="${ConfigurationHolder.config.theme.application}/js/bootstrap/js/bootstrap.min.js"></script>
    <link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css//bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css/kendo/kendo.common.min.css"/>
    <link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css/kendo/kendo.silver.min.css"/>
    <link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css/main-image.css"/>
    <app:themeContent name="${ThemeCacheUtility.KEY_CSS_BOOTSTRAP_CUSTOM}" css="true"></app:themeContent>


    <script type="text/javascript">
        $(document).ready(function () {
            $('.carousel').carousel();
        });
    </script>
</head>

<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-5">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <div class="panel-title">
                        <div class="row">
                            <div class="col-md-8" style="font-weight: bold">Sign In</div>
                            <div class="col-md-4">
                                <app:isUserRegistrationEnabled name="ExchangeHouse">
                                    <a href="${createLink(controller: 'exhCustomer', action: 'registration')}" class="small">New User? Click Here</a>
                                </app:isUserRegistrationEnabled>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="panel-body">
                    <form action='${createLink(controller: 'login', action: 'checklogin')}' autocomplete='on'
                          method="post" class="form-horizontal form-widgets" role="form" id='loginForm'>

                        <div class="form-group">
                            <label class="col-md-3 control-label" for="username">Login ID:</label>

                            <div class="col-md-9">
                                <%
                                    if (Environment.currentEnvironment == Environment.DEVELOPMENT) {
                                %>
                                <input type="text" class="k-textbox" id="username" name="j_username" tabindex="1"
                                       placeholder="Login Id" value="admin@arafat.com"/>
                                <% } else { %>
                                <input type="text" class="k-textbox" id="username" name="j_username" tabindex="1"
                                       placeholder="Login Id" value=""/>
                                <% } %>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-3 control-label" for="password">Password:</label>

                            <div class="col-md-9">
                                <%
                                    if (Environment.currentEnvironment == Environment.DEVELOPMENT) {
                                %>
                                <input type="password" class="k-textbox" id="password" name="j_password"
                                       tabindex="2"
                                       placeholder="Password" value="arafat@123"/>
                                <% } else { %>
                                <input type="password" class="k-textbox" id="password" name="j_password"
                                       tabindex="2" placeholder="Password" value=""/>
                                <% } %>
                            </div>
                        </div>

                        <% if (Environment.currentEnvironment == Environment.PRODUCTION) { %>
                        <div class="form-group">
                            <div class="col-md-3">&nbsp;</div>

                            <div class="col-md-5">
                                <jcaptcha:jpeg name="image"/>
                            </div>

                            <div class="col-md-4">
                                <a id="refreshCaptcha" title="Refresh security image" class="k-button">
                                    <span class="k-icon k-i-refresh"></span>
                                </a>
                                <jq:jquery>
                                    $("#refreshCaptcha").click(function() {
                                       $("#image").fadeOut(500, function() {
                                          var captchaURL = $("#image").attr("src");
                                          captchaURL = captchaURL.replace(captchaURL.substring(captchaURL.indexOf("=")+1, captchaURL.length), Math.floor(Math.random()*9999999999));
                                          $("#image").attr("src", captchaURL);
                                       });
                                       $("#image").fadeIn(300);
                                 });
                                </jq:jquery>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="control-label col-md-3" for="captcha">Security ID:</label>

                            <div class="col-md-9">
                                <input type="text" class="k-textbox" id="captcha" name="captcha" tabindex="3"
                                       value=""/>
                            </div>
                        </div>
                        <% } %>

                        <div class="form-group">
                            <div class="col-md-3">&nbsp;</div>

                            <div class="col-md-5">
                                <button id="create" name="create" type="submit" data-role="button"
                                        class="k-button k-button-icontext"
                                        role="button" tabindex="4"
                                        aria-disabled="false"><span class="k-icon k-i-arrow-e"></span>Login
                                </button>
                            </div>

                            <div class="col-md-4">
                                <a href="#"
                                   onclick="javascript:$('#forgotPassPanel').toggle(400);">Forgot Password?</a>
                            </div>
                        </div>
                    </form>

                    <form onsubmit="" action='${createLink(controller: 'appUser', action: 'sendPasswordResetLink')}'
                          method='POST' class="form-horizontal form-widgets" role="form" style="padding-bottom: 4px">
                        <div class="form-group" id="forgotPassPanel" style="display: none;">
                            <div class="col-md-3">&nbsp;</div>

                            <div class="col-md-5">
                                <input type="text" class="k-textbox" id="loginId" name="loginId" tabindex="5"
                                       placeholder="Enter your login ID"/>
                            </div>

                            <div class="col-md-4">
                                <button type="submit" data-role="button"
                                        class="k-button k-button-icontext"
                                        role="button" tabindex="6"
                                        aria-disabled="false"><span class="k-icon k-i-arrow-e"></span>Send Mail
                                </button>
                            </div>
                        </div>
                    </form>

                    <div>
                        <g:if test="${flash.message && !flash.success}">
                            <div class='alert alert-danger col-md-12' id="login_msg_"
                                 style="margin-bottom: 0">${flash.message}</div>
                        </g:if>
                        <g:if test="${flash.message && flash.success}">
                            <div class='alert alert-success col-md-12' id="login_msg_"
                                 style="margin-bottom: 0">${flash.message}</div>
                        </g:if>
                    </div>
                </div>
            </div>
        </div>

        <div class="col-md-7">
            <img class="img-responsive" src="${ConfigurationHolder.config.theme.application}<app:themeContent
                    name="app.login.imgTopRight"></app:themeContent>"
                 align="right"/>
        </div>
    </div>

    <div class="row">
        <div class="panel panel-default">
            <div class="panel-body">
                <app:themeContent name="app.login.pageCaution"></app:themeContent>
            </div>
        </div>
    </div>


    <div class="row">
        <app:themeContent name="app.login.businessSupport"></app:themeContent>
    </div>

    <div class="row">
        <div class="panel panel-default">
            <div class="panel-body">
                <app:themeContent name="app.login.advertisingPhrase"></app:themeContent>
            </div>
        </div>
    </div>

    <div class="row">
        <div>
            <app:themeContent name="app.login.productName"></app:themeContent><br/>
            <app:themeContent name="app.login.companyCopyright"></app:themeContent>
        </div>
    </div>

</div>
</body>
</html>
