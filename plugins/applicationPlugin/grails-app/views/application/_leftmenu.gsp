<app:ifAnyUrl urls="/vehicle/show,/project/show,/supplier/show,/employee/show,/customer/show,/costingType/show,
/itemType/show,/item/showInventoryItem,/item/showNonInventoryItem,/country/show,/designation/show,/currency/show,/bank/show,/bankBranch/show,/district/show,/appShellScript/show">
    <a href="#"><span><i class="tap-pre settings-tab"></i></span><span class="tabText">Settings</span></a>

    <div id='0'>
        <ul class="menuDiv">
            <sec:access url="/costingType/show">
                <li><a class='autoload' href="#costingType/show"><span><i class="pre-icon vehicle"></i></span> <span
                        class="menuText">Costing Type</span></a></li>
            </sec:access>
            <sec:access url="/costingDetails/show">
                <li><a class='autoload' href="#costingDetails/show"><span><i class="pre-icon vehicle"></i></span> <span
                        class="menuText">Costing Details</span></a></li>
            </sec:access>
            <sec:access url="/vehicle/show">
                <li><a class='autoload' href="#vehicle/show"><span><i class="pre-icon vehicle"></i></span> <span
                        class="menuText">Vehicle</span></a></li>
            </sec:access>
            <sec:access url="/project/show">
                <li><a class='autoload' href="#project/show"><span><i class="pre-icon project"></i></span> <span
                        class="menuText">Project</span></a></li>
            </sec:access>
            <sec:access url="/supplier/show">
                <li><a class='autoload' href="#supplier/show"><span><i class="pre-icon supplier"></i></span> <span
                        class="menuText">Supplier</span></a></li>
            </sec:access>
            <sec:access url="/customer/show">
                <li><a class='autoload' href="#customer/show"><span><i class="pre-icon customer"></i>
                </span> <span
                        class="menuText">Customer</span></a></li>
            </sec:access>
            <sec:access url="/employee/show">
                <li><a class='autoload' href="#employee/show"><span><i class="pre-icon employee"></i>
                </span> <span
                        class="menuText">Employee</span></a></li>
            </sec:access>
            <sec:access url="/itemType/show">
                <li><a class='autoload' href="#itemType/show"><span><i class="pre-icon employee"></i>
                </span> <span
                        class="menuText">Item Type</span></a></li>
            </sec:access>

            <app:ifAnyUrl urls="/item/showInventoryItem,/item/showNonInventoryItem">
                <li>&nbsp;</li>
                <li><span><i class="pre-icon item"></i></span> <span class="menuText"
                                                                     style="padding-left:36px">Item</span>
                    <ul class="menuDivSub">
                        <sec:access url="/item/showInventoryItem">
                            <li><a class='autoload' href="#item/showInventoryItem"><span><i
                                    class="pre-icon-sub pre-icon move"></i></span> <span
                                    class="menuTextSub">Inventory</span></a></li>
                        </sec:access>

                        <sec:access url="/item/showNonInventoryItem">
                            <li><a class='autoload' href="#item/showNonInventoryItem"><span><i
                                    class="pre-icon-sub pre-icon budget"></i></span> <span
                                    class="menuTextSub">Non Inventory</span></a></li>
                        </sec:access>
                    </ul>
                </li>
                <li>&nbsp;</li>
            </app:ifAnyUrl>

            <sec:access url="/country/show">
                <li><a class="autoload" href="#country/show"><span><i class="pre-icon country"></i>
                </span><span class="menuText">Country</span></a></li>
            </sec:access>
            <sec:access url="/designation/show">
                <li><a class="autoload" href="#designation/show"><span><i class="pre-icon designation"></i>
                </span><span class="menuText">Designation</span></a></li>
            </sec:access>
            <sec:access url="/currency/show">
                <li><a class='autoload' href="#currency/show"><span><i
                        class="pre-icon currency"></i></span> <span class="menuText">Currency</span></a>
                </li>
            </sec:access>
	        <sec:access url="/bank/show">
		        <li><a class='autoload' href="#bank/show"><span><i
				        class="pre-icon bank"></i></span> <span class="menuText">Bank</span></a>
		        </li>
	        </sec:access>
	        <sec:access url="/bankBranch/show">
		        <li><a class='autoload' href="#bankBranch/show"><span><i
				        class="pre-icon store"></i></span> <span class="menuText">Bank Branch</span></a>
		        </li>
	        </sec:access>
	        <sec:access url="/district/show">
		        <li><a class='autoload' href="#district/show"><span><i
				        class="pre-icon district"></i></span> <span class="menuText">District</span></a>
		        </li>
	        </sec:access>
            <sec:access url="/appShellScript/show">
                <li><a class='autoload' href="#appShellScript/show"><span><i class="pre-icon instance"></i>
                </span> <span class="menuText">Shell Script</span></a>
                </li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<app:ifAnyUrl urls="/appUser/showOnlineUser,/systemConfiguration/show,/systemEntity/show,/appUser/showForCompanyUser,
                 /systemEntityType/show,/theme/showTheme,/company/show,/contentCategory/show,/appMail/show">
    <a href="#"><span><i class="tap-pre development-tab"></i></span><span class="tabText">Development</span></a>

    <div id='1'>
        <ul class="menuDiv">

            <sec:access url="/appUser/showOnlineUser">
                <li><a class='autoload' href="#appUser/showOnlineUser"><span><i
                        class="pre-icon agent"></i></span> <span
                        class="menuText">Who's Online</span></a></li>
            </sec:access>

            <sec:access url="/systemConfiguration/show">
                <li><a class='autoload' href="#systemConfiguration/show?plugin=1"><span><i
                        class="pre-icon sys-configuration"></i></span> <span
                        class="menuText">System Configuration</span></a></li>
            </sec:access>

            <sec:access url="/systemEntityType/show">
                <li><a class='autoload' href="#systemEntityType/show"><span><i
                        class="pre-icon system-entity-type"></i></span> <span
                        class="menuText">System Entity Type</span></a></li>
            </sec:access>

            <sec:access url="/company/show">
                <li><a class='autoload' href="#company/show"><span><i class="pre-icon company"></i></span> <span
                        class="menuText">Company</span></a></li>
            </sec:access>

            <sec:access url="/appUser/showForCompanyUser">
                <li><a class='autoload' href="#appUser/showForCompanyUser"><span><i class="pre-icon app-user"></i>
                </span> <span
                        class="menuText">Company User</span></a></li>
            </sec:access>

            <sec:access url="/theme/showTheme">
                <li><a class='autoload' href="#theme/showTheme"><span><i
                        class="pre-icon custom-group-balance"></i></span> <span
                        class="menuText">Theme</span></a></li>
            </sec:access>

            <sec:access url="/appMail/show">
                <li><a class='autoload' href="#appMail/show?plugin=1"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">Mail</span></a></li>
            </sec:access>

            <sec:access url="/sms/showSms">
                <li><a class='autoload' href="#sms/showSms?plugin=1"><span><i
                        class="pre-icon message"></i></span> <span
                        class="menuText">SMS</span></a></li>
            </sec:access>

            <sec:access url="/contentCategory/show">
                <li><a class='autoload' href="#contentCategory/show"><span><i
                        class="pre-icon group-ledger"></i></span> <span
                        class="menuText">Content category</span></a></li>
            </sec:access>
        </ul>
    </div>

</app:ifAnyUrl>

<app:ifAnyUrl
        urls="/appUser/show,/role/show,/requestMap/show,/appGroup/show">
    <a href="#"><span><i class="tap-pre user-management"></i></span><span class="tabText">User Management</span></a>

    <div id='2'>
        <ul class="menuDiv">
            <sec:access url="/appUser/show">
                <li><a class='autoload' href="#appUser/show"><span><i class="pre-icon application_user"></i>
                </span> <span
                        class="menuText">User</span></a></li>
            </sec:access>
            <sec:access url="/role/show">
                <li><a class='autoload' href="#role/show"><span><i class="pre-icon role"></i></span> <span
                        class="menuText">Role</span></a></li>
            </sec:access>
            <sec:access url="/requestMap/show">
                <li><a class='autoload' href="#requestMap/show"><span><i class="pre-icon role-right"></i></span> <span
                        class="menuText">Role Right Mapping</span></a></li>
            </sec:access>
            <sec:access url="/appGroup/show">
                <li><a class="autoload" href="#appGroup/show"><span><i class="pre-icon user-group"></i>
                </span><span class="menuText">Group</span></a></li>
            </sec:access>
        </ul>
    </div>
</app:ifAnyUrl>

<div id="menuBottomDiv" style=" height:60px; display: none;">
</div>



