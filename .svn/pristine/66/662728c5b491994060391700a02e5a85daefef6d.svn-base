<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" %>
<app:ifAnyUrl urls="/budgBudget/show,/budgSprint/show">
    <a href="#"><span><i class="tap-pre budget-tab"></i></span><span class="tabText">Budget</span></a>

    <div id='1'>
        <ul class="menuDiv">

            <li><span><i class="pre-icon budget"></i></span> <span class="menuText"
                                                                     style="padding-left:36px">Budget</span>
                <ul class="menuDivSub">
                        <sec:access url="/budgBudget/show">
                            <li><a class='autoload' href="#budgBudget/show"><span><i
                                    class="pre-icon-sub pre-icon payable"></i></span> <span
                                    class="menuTextSub">Procurement</span></a></li>
                        </sec:access>

                        <sec:access url="/budgBudget/show">
                            <li><a class='autoload' href="#budgBudget/show?isProduction=true"><span><i
                                    class="pre-icon-sub pre-icon paid"></i></span> <span
                                    class="menuTextSub">Production</span></a></li>
                        </sec:access>
                </ul>
            <li>&nbsp;</li>
            </li>

            <sec:access url="/budgSprint/show">
                <li><a class='autoload' href="#budgSprint/show"><span><i class="pre-icon voucher-list"></i></span> <span
                        class="menuText">Sprint</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/budgReport/showBudgetRpt,/budgReport/showProjectStatus,
    /budgReport/showConsumptionDeviation,/budgReport/showProjectCosting,/budgReport/showBudgetSprint">
    <a href="#"><span><i class="tap-pre report-tab"></i></span><span class="tabText">Report</span></a>

    <div id='2'>
        <ul class="menuDiv">
            <sec:access url="/budgReport/showBudgetRpt">
                <li><a class='autoload' href="#budgReport/showBudgetRpt"><span><i
                        class="pre-icon paid"></i></span> <span
                        class="menuText">Budget Line Item</span></a></li>
            </sec:access>

            <sec:access url="/budgReport/showProjectBudget">
                <li><a class='autoload' href="#budgReport/showProjectBudget"><span><i
                        class="pre-icon budget"></i></span> <span
                        class="menuText">Budget</span></a></li>
            </sec:access>

            <sec:access url="/budgReport/showBudgetSprint">
                <li><a class='autoload' href="#budgReport/showBudgetSprint"><span><i
                        class="pre-icon voucher-list"></i></span> <span
                        class="menuText">Sprint</span></a></li>
            </sec:access>

            <app:ifAllPlugins names="Inventory,QS,FixedAsset">
                <sec:access url="/budgReport/showProjectStatus">
                    <li><a class='autoload' href="#budgReport/showProjectStatus"><span><i
                            class="pre-icon project-status"></i></span> <span
                            class="menuText">Project Status</span></a></li>
                </sec:access>
            </app:ifAllPlugins>

            <sec:access url="/budgReport/showProjectCosting">
                <li><a class='autoload' href="#budgReport/showProjectCosting"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">Project Costing</span></a></li>
            </sec:access>

            <app:ifPlugin name="Inventory">
                <sec:access url="/budgReport/showConsumptionDeviation">
                    <li><a class='autoload' href="#budgReport/showConsumptionDeviation"><span><i
                            class="pre-icon consumption-deviation"></i></span> <span
                            class="menuText">Consumption Deviation</span></a></li>
                </sec:access>
            </app:ifPlugin>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl
        urls="/systemConfiguration/show,/appMail/show,sms/showSms">
    <a href="#"><span><i class="tap-pre development-tab"></i></span><span class="tabText">Development</span></a>

    <div id='3'>
        <ul class="menuDiv">
            <sec:access url="/systemConfiguration/show">
                <li><a class='autoload' href="#systemConfiguration/show?plugin=3"><span><i
                        class="pre-icon sys-configuration"></i></span> <span
                        class="menuText">System Configuration</span></a></li>
            </sec:access>
            <sec:access url="/appMail/show">
                <li><a class='autoload' href="#appMail/show?plugin=3"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">Mail</span></a></li>
            </sec:access>
            <sec:access url="/sms/showSms">
                <li><a class='autoload' href="#sms/showSms?plugin=3"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">SMS</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/budgetScope/show,/projectBudgetScope/show">
    <a href="#">
        <span><i class="tap-pre settings-tab"></i></span>
        <span class="tabText">Settings</span>
    </a>

    <div id='4'>
        <ul class="menuDiv">
            <sec:access url="/budgetScope/show">
                <li><a class='autoload' href="#budgetScope/show"><span><i class="pre-icon budget_type"></i></span> <span
                        class="menuText">Scope of Work</span></a></li>
            </sec:access>
            <sec:access url="/projectBudgetScope/show">
                <li><a class='autoload' href="#projectBudgetScope/show"><span><i class="pre-icon project_bdget_type"></i>
                </span> <span
                        class="menuText">Project Scope Mapping</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<div id="menuBottomDiv" style="height:60px; display: none;">
</div>
