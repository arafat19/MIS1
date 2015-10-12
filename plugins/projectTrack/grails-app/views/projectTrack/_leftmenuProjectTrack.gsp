<%@ page import="com.athena.mis.application.utility.RoleTypeCacheUtility" %>
<app:ifAnyUrl
        urls="/ptBacklog/show,/ptSprint/show,/ptBacklog/showForActive,/ptBacklog/showForInActive,/ptBacklog/showMyBacklog,/ptReport/showForBacklogDetails">
    <a href="#"><span><i class="tap-pre project-tab"></i></span><span class="tabText">Project</span></a>

    <div id='0'>
        <ul class="menuDiv">
            <sec:access url="/ptSprint/show">
                <li><a class='autoload' href="#ptSprint/show"><span><i class="pre-icon voucher-list"></i></span> <span
                        class="menuText">Sprint</span></a></li>
            </sec:access>

            <sec:access url="/ptBacklog/show">
                <li><a class='autoload' href="#ptBacklog/show"><span><i class="pre-icon voucher"></i></span> <span
                        class="menuText">Backlog</span></a></li>
            </sec:access>

            <sec:access url="/ptBug/showOrphanBug">
                <li><a class='autoload' href="#ptBug/showOrphanBug"><span><i class="pre-icon group-ledger"></i>
                </span> <span
                        class="menuText">Bug</span></a></li>
                <li>&nbsp;</li>
            </sec:access>

            <app:ifAnyUrl urls="/ptBacklog/showForActive,/ptBacklog/showForInActive">

                <li><span><i class="pre-icon inventory-status-report"></i></span> <span class="menuText"
                                                                                        style="padding-left:36px">Use Case</span>
                    <ul class="menuDivSub">
                        <sec:access url="/ptBacklog/showForActive">
                            <li><a class='autoload' href="#ptBacklog/showForActive"><span><i
                                    class="pre-icon-sub pre-icon payable"></i></span><span
                                    class="menuTextSub">Active</span></a></li>
                        </sec:access>
                        <sec:access url="/ptBacklog/showForInActive">
                            <li><a class='autoload' href="#ptBacklog/showForInActive"><span><i
                                    class="pre-icon-sub pre-icon paid"></i></span><span
                                    class="menuTextSub">Inactive</span></a></li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>

            <app:ifAnyUrl urls="/ptBacklog/showMyBacklog,/ptBug/showMyBug">
                <li><span><i class="pre-icon supplier"></i></span> <span class="menuText"
                                                                         style="padding-left:36px">My Assignment</span>
                    <ul class="menuDivSub">
                        <sec:access url="/ptBacklog/showMyBacklog">
                            <li><a class='autoload' href="#ptBacklog/showMyBacklog"><span><i
                                    class="pre-icon-sub pre-icon budget"></i></span><span
                                    class="menuTextSub">Task</span></a></li>
                        </sec:access>
                        <sec:access url="/ptBug/showMyBug">
                            <li><a class='autoload' href="#ptBug/showMyBug"><span><i
                                    class="pre-icon-sub ledger"></i></span><span
                                    class="menuTextSub">Bug</span></a></li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>

            <app:ifAnyUrl urls="/ptReport/showForBacklogDetails,/ptBug/showBugDetails">
                <li><span><i class="pre-icon search-member"></i></span> <span class="menuText"
                                                                              style="padding-left:36px">Search</span>
                    <ul class="menuDivSub">
                        <sec:access url="/ptReport/showForBacklogDetails">
                            <li><a class='autoload' href="#ptReport/showForBacklogDetails"><span><i
                                    class="pre-icon-sub pre-icon item"></i></span><span
                                    class="menuTextSub">Task</span></a></li>
                        </sec:access>
                        <sec:access url="/ptBug/showBugDetails">
                            <li><a class='autoload' href="#ptBug/showBugDetails"><span><i
                                    class="pre-icon-sub group-ledger"></i></span><span
                                    class="menuTextSub">Bug</span></a></li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/ptReport/showReportSprint,/ptReport/showReportOpenBacklog,/ptReport/showReportBug">
    <a href="#"><span><i class="tap-pre disbursement-tab"></i></span><span class="tabText">Report</span></a>

    <div id='1'>
        <ul class="menuDiv">
            <sec:access url="/ptReport/showReportSprint">
                <li><a class='autoload' href="#ptReport/showReportSprint"><span><i class="pre-icon voucher-list"></i>
                </span> <span
                        class="menuText">Sprint</span></a></li>
            </sec:access>

            <sec:access url="/ptReport/showReportOpenBacklog">
                <li><a class='autoload' href="#ptReport/showReportOpenBacklog"><span><i class="pre-icon budget"></i>
                </span> <span
                        class="menuText">Backlog</span></a></li>
            </sec:access>
            <sec:access url="/ptReport/showReportBug">
                <li><a class='autoload' href="#ptReport/showReportBug"><span><i class="pre-icon group-ledger"></i>
                </span> <span
                        class="menuText">Bug</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl
        urls="/systemConfiguration/show,/sms/showSms,/appMail/show">
    <a href="#"><span><i class="tap-pre development-tab"></i></span><span class="tabText">Development</span></a>


    <div id='2'>
        <ul class="menuDiv">
            <sec:access url="/systemConfiguration/show">
                <li><a class='autoload' href="#systemConfiguration/show?plugin=10"><span><i
                        class="pre-icon sys-configuration"></i></span> <span
                        class="menuText">System Configuration</span></a></li>
            </sec:access>

            <sec:access url="/appMail/show">
                <li><a class='autoload' href="#appMail/show?plugin=10"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">Mail</span></a></li>
            </sec:access>

            <sec:access url="/sms/showSms">
                <li><a class='autoload' href="#sms/showSms?plugin=10"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">SMS</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/ptProject/show,/ptModule/show">

    <a href="#"><span><i class="tap-pre settings-tab"></i></span><span class="tabText">Settings</span></a>

    <div id='3'>
        <ul class="menuDiv">
            <sec:access url="/ptProject/show">
                <li><a class='autoload' href="#ptProject/show"><span><i class="pre-icon project"></i></span> <span
                        class="menuText">Project</span></a></li>
            </sec:access>
            <sec:access url="/ptModule/show">
                <li><a class='autoload' href="#ptModule/show"><span><i
                        class="pre-icon division"></i></span> <span
                        class="menuText">Module</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<div id="menuBottomDiv" style=" height:60px; display: none;">
</div>



