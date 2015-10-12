<%@ page import="com.athena.mis.utility.UIConstants" %>

    <div class='app-logo'>
        <a id='home-link' title="Dashboard" href="<g:resource dir="/"/>"></a>
    </div>

    <div class='powered-by-athena'>
        <div class="welcomeText">
            <div class="textHolder"
                 style="font-weight: bold;  ">Welcome, <app:sessionUser property='username'></app:sessionUser></div>
            <div class="buttonHolder">
               <!-- <a id='managePassword'
                        href="#appUser/managePassword">Change Password</a> |//-->
                <a href="<g:createLink controller="logout"/>">Logout</a>
            </div>
        </div>
    </div>