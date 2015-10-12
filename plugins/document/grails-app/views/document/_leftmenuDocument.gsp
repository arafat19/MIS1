<%@ page import="com.athena.mis.document.config.DocSysConfigurationCacheUtility; com.athena.mis.PluginConnector" %>

<app:ifAnyUrl
        urls="/docCategory/showCategories,/docInvitedMembers/show,
        /docInvitedMembers/showOutStandingInvitations,/docMemberJoinRequest/show">
    <a href="#"><span><i class="tap-pre categories-tab"></i></span><span class="tabText">Categories</span></a>

    <div id='0'>
        <ul class="menuDiv">
            <sec:access url="/docCategory/showCategories">
                <li><a class='autoload' href="#docCategory/showCategories"><span><i class="pre-icon category"></i>
                </span>
                    <span class="menuText">My <app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                                                 key="${DocSysConfigurationCacheUtility.DOC_CATEGORY_LABEL}"></app:showSysConfig></span>
                </a>
                </li>
            </sec:access>
            <sec:access url="/docInvitedMembers/show">
                <li><a class='autoload' href="#docInvitedMembers/show"><span><i class="pre-icon category"></i>
                </span>
                    <span class="menuText">Send Invitation</span>
                </a>
                </li>
            </sec:access>

            <sec:access url="/docInvitedMembers/showOutStandingInvitations">
                <li><a class='autoload' href="#docInvitedMembers/showOutStandingInvitations"><span><i
                        class="pre-icon category"></i>
                </span>
                    <span class="menuText">Outstanding Invitations</span>
                </a>
                </li>
            </sec:access>

            <sec:access url="/docMemberJoinRequest/show">
                <li><a class='autoload' href="#docMemberJoinRequest/show"><span><i class="pre-icon category"></i>
                </span>
                    <span class="menuText">Join Request</span>
                </a>
                </li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/docArticle/show,/docArticle/showTrash,/docArticleQuery/show">
    <a href="#"><span><i class="tap-pre document-tab"></i></span><span class="tabText">Document</span></a>

    <div id='0'>
        <ul class="menuDiv">
            <sec:access url="/docArticle/show">
                <li><a class='autoload' href="#"><span><i
                        class="pre-icon-sub pre-icon upload"></i></span> <span
                        class="menuTextSub">File</span></a></li>
            </sec:access>
            <sec:access url="/docArticle/show">
                <li><a class='autoload' href="#docArticle/show"><span><i
                        class="pre-icon-sub pre-icon add"></i></span> <span
                        class="menuTextSub">Article</span></a></li>
            </sec:access>
            <sec:access url="/docArticle/showTrash">
                <li><a class='autoload' href="#docArticle/showTrash"><span><i
                        class="pre-icon-sub pre-icon search"></i></span> <span
                        class="menuTextSub">Trash</span></a></li>
            </sec:access>
            <sec:access url="/docArticleQuery/show">
                <li><a class='autoload' href="#docArticleQuery/show"><span><i
                        class="pre-icon-sub pre-icon search"></i></span> <span
                        class="menuTextSub">Search</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/systemConfiguration/show,/appMail/show">
    <a href="#"><span><i class="tap-pre development-tab"></i></span><span class="tabText">Development</span></a>

    <div id='1'>
        <ul class="menuDiv">
            <sec:access url="/systemConfiguration/show">
                <li><a class='autoload' href="#systemConfiguration/show?plugin=13"><span><i
                        class="pre-icon sys-configuration"></i></span> <span
                        class="menuText">System Configuration</span></a></li>
            </sec:access>
            <sec:access url="/appMail/show">
                <li><a class='autoload' href="#appMail/show?plugin=13"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">Mail</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/docCategory/show,/docDbInstance/show">
    <a href="#"><span><i class="tap-pre settings-tab"></i></span><span class="tabText">Settings</span></a>

    <div id='0'>
        <ul class="menuDiv">
            <sec:access url="/docCategory/show">
                <li><a class='autoload' href="#docCategory/show"><span><i class="pre-icon category"></i></span> <span
                        class="menuText"><app:showSysConfig pluginId="${PluginConnector.DOCUMENT_ID}"
                                                            key="${DocSysConfigurationCacheUtility.DOC_CATEGORY_LABEL}"></app:showSysConfig></span>
                </a></li>
            </sec:access>
            <sec:access url="/docDbInstance/show">
                <li><a class='autoload' href="#docDbInstance/show"><span><i class="pre-icon instance"></i>
                </span> <span class="menuText">DB Instance</span></a>
                </li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<div id="menuBottomDiv" style=" height:60px; display: none;">
</div>



