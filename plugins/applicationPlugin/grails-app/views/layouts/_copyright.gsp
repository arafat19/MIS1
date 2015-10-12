<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" %>
<div style="height:60px; background:url('${ConfigurationHolder.config.theme.application}/images/logobg.jpg') repeat-x;">
    <div style="width:55px; float:right"><a title="<app:themeContent name="app.companyName"></app:themeContent>"
                                            href="<app:themeContent name="app.companyWebsite"></app:themeContent>"
                                            target="_blank"><img
                src="${ConfigurationHolder.config.theme.application}<app:themeContent
                        name="app.leftMenu.CompanyLogo"></app:themeContent>"
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