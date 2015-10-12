<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta http-equiv="cache-control" content="public"/>
    <title>Change Your Password</title>
    <script type="text/javascript"
            src="${ConfigurationHolder.config.theme.application}/js/jquery/jquery-1.8.1.min.js"></script>
    <script type="text/javascript"
            src="${ConfigurationHolder.config.theme.application}/js/kendo/kendo.web.min.js"></script>
    <script type="text/javascript"
            src="${ConfigurationHolder.config.theme.application}/js/bootstrap/js/bootstrap.min.js"></script>
    <link rel="shortcut icon" href="${ConfigurationHolder.config.theme.application}/images/favicon_arms.ico"
          type="image/x-icon"/>
    <link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css//bootstrap/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css/kendo/kendo.common.min.css"/>
    <link rel="stylesheet" href="${ConfigurationHolder.config.theme.application}/css/kendo/kendo.silver.min.css"/>

</head>

<body>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-3">
            <app:themeContent
                    name="app.login.companyVerisignUrl">
            </app:themeContent>
        </div>

        <div class="col-md-5">
            <div class="panel panel-default">
                <div class="panel-heading">
                    <div class="panel-title">
                        <div style="font-weight: bold">Change Password</div>
                    </div>
                </div>

                <div class="panel-body">
                    <form onsubmit="" action='${createLink(controller: 'appUser', action: 'resetPassword')}'
                          method='POST' id='loginForm' class="form-horizontal form-widgets" role="form"
                          autocomplete='on'>

                        <div class="form-group">
                            <label class="col-md-4 control-label" for="username">User Name:</label>

                            <div class="col-md-8">
                                <span>${userInfoMap.username}</span>
                                <input type="hidden" name='link' id='link' value="${userInfoMap.passwordResetLink}"/>
                                <input type="hidden" name='username' id='username' value="${userInfoMap.username}"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-4 control-label" for="code">Security Code:</label>

                            <div class="col-md-8">
                                <input type="text" class="k-textbox" id="code" name="code" tabindex="1"
                                       placeholder="Copy from your mail"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-4 control-label" for="password">New Password:</label>

                            <div class="col-md-8">
                                <input type="password" class="k-textbox" id="password" name="password"
                                       tabindex="2" placeholder="Type Password"/>

                            </div>
                        </div>

                        <div class="form-group">
                            <label class="col-md-4 control-label" for="password">Retype Password:</label>

                            <div class="col-md-8">
                                <input type="password" class="k-textbox" id="retypePassword" name="retypePassword"
                                       tabindex="3" placeholder="Type the password again"/>
                            </div>
                        </div>

                        <div class="form-group">
                            <div class="col-md-4">&nbsp;</div>

                            <div class="col-md-3">
                                <button id="create" name="create" type="submit" data-role="button"
                                        class="k-button k-button-icontext"
                                        role="button" tabindex="4"
                                        aria-disabled="false"><span class="k-icon k-i-plus"></span>Change
                                </button>
                            </div>

                            <div class="col-md-5">
                                <a href="${createLink(controller: 'login', action: 'auth')}">Back to Sign In Page</a>
                            </div>
                        </div>

                        <div class="form-group" style="margin-bottom: 0px">
                            <g:if test="${flash.message && !flash.success}">
                                <div class='alert alert-danger col-md-12' id="login_msg_">${flash.message}</div>
                            </g:if>
                            <g:if test="${flash.message && flash.success}">
                                <div class='alert alert-success col-md-12' id="login_msg_">${flash.message}</div>
                            </g:if>
                        </div>
                    </form>
                </div>
            </div>
        </div>

        <div class="col-md-4">
            <img src="${ConfigurationHolder.config.theme.application}<app:themeContent
                    name="app.login.imgTopRight"></app:themeContent>"
                 align="right"/>
        </div>
    </div>

    <div class="row">
        <div class="panel panel-default">
            <div class="panel-body">
                <app:themeContent name="app.login.advertisingPhrase"></app:themeContent>
            </div>
        </div>
    </div>

</div>
</body>
</html>