<script type="text/javascript">
    // init global vars
    var resetReqMapConfirmDialog = null;
    var taskCreateConfirmDialog = null;
    var taskCancelConfirmDialog = null;
    var myLayout,currentMenu = false;
    var MENU_ID_APPLICATION = '0';
    var MENU_ID_BUDGET = '1';
    var MENU_ID_PROCUREMENT = '2';
    var MENU_ID_INVENTORY = '3';
    var MENU_ID_ACCOUNTING = '4';
    var MENU_ID_QS = '5';
    var MENU_ID_FIXED_ASSET = '6';
    var MENU_ID_EXCHANGE_HOUSE = '9';
    var MENU_ID_PROJECT_TRACK = '10';
    var MENU_ID_ARMS = '11';
    var MENU_ID_SARB = '12';
    var MENU_ID_DOCUMENT = '13';

    $(document).ready(function () {
        var logoutUrl = "<g:createLink controller="logout"/>";
        onLoadMainLayout(logoutUrl);
        // Following line is unnecessary when jQuery layout will be cleaned
        //$('#contentHolder').css('position','absolute');
    });

    function onLoadMainLayout() {
        myLayout = $('body').layout({

            east__size: 300
            // RESIZE Accordion widget when panes resize
            , west__onresize: function () {
                $("#accordion1").accordion("resize");
            }, east__onresize: function () {
                $("#accordion2").accordion("resize");
            }, south__resizable: false, south__slidable: false, south__togglerLength_open: 0, north__size: 55, north__resizable: false, south__showOverflowOnHover: false, west__size: 230, center__contentSelector: '#contentHolder', spacing_open: 5	// ALL panes

        });

        // ACCORDION - in the West pane
        $("#accordion1").accordion({
            fillSpace: true, active: 0
        });

        myLayout.close('north');

        $.history.init(function (hash) {
            setFavicon();
            if (hash) {
                if (hash.endsWith('Menu')) {
                    loadLeftMenu(hash, null);
                } else {
                    load(hash);
                }
            } else {
                loadInitialLeftMenu();
            }
        });

        bindAutoLoadClass();

        $.ajaxSetup({
            error: function (x, e) {
                if (x.status == 0) {
                    redirectToLogoutPage(); // defined in main.gsp
                } else if (x.status == 404) {
                    //alert('Requested URL not found.');
                    showLoadingSpinner(false);
                    redirectToLogoutPage(); // defined in main.gsp
                } else if (x.status == 500) {
                    redirectToLogoutPage(); // defined in main.gsp
                } else if (e == 'parsererror') {
                    alert('Error.\nParsing JSON Request failed.');
                } else if (e == 'timeout') {
                    alert('Request Time out.');
                } else {
                    alert('Unknow Error.\n Exception:' + e + '\nResponse:' + + x.responseText + '\nStatus code:' + x.status);
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
            $(errorData).each(function (idx) {
                showError(errorData[idx]);
            });
        }
    }

    function clearDocMenuHover() {
        $('#dockMenuContainer > ul > li').removeClass('active');
    }

    function renderApplicationMenu(url, loadHistory) {      //@todo- remove when done
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'application', action: 'renderApplicationMenu')}";
        if (loadHistory) {
            if (currentMenu == MENU_ID_APPLICATION) {
                loadLeftMenu(loc, null);
            } else {
                currentMenu = MENU_ID_APPLICATION;
                $.history.load(loc);
            }
        } else {
            currentMenu = MENU_ID_APPLICATION;
            loadLeftMenu(loc, url);
        }
    }

    function renderBudgetMenu(url, loadHistory) {
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'budgBudget', action: 'renderBudgetMenu')}";
        if (loadHistory) {
            if (currentMenu == MENU_ID_BUDGET) {
                loadLeftMenu(loc, null);
            } else {
                currentMenu = MENU_ID_BUDGET;
                $.history.load(loc);
            }
        } else {
            currentMenu = MENU_ID_BUDGET;
            loadLeftMenu(loc, url);
        }
    }

    function renderProcurementMenu(url, loadHistory) {
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'procurement', action: 'renderProcurementMenu')}";
        if (loadHistory) {
            if (currentMenu == MENU_ID_PROCUREMENT) {
                loadLeftMenu(loc, null);
            } else {
                currentMenu = MENU_ID_PROCUREMENT;
                $.history.load(loc);
            }
        } else {
            currentMenu = MENU_ID_PROCUREMENT;
            loadLeftMenu(loc, url);
        }
    }

    function renderInventoryMenu(url, loadHistory) {
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'inventory', action: 'renderInventoryMenu')}";
        if (loadHistory) {
            if (currentMenu == MENU_ID_INVENTORY) {
                loadLeftMenu(loc, null);
            } else {
                currentMenu = MENU_ID_INVENTORY;
                $.history.load(loc);
            }
        } else {
            currentMenu = MENU_ID_INVENTORY;
            loadLeftMenu(loc, url);
        }
    }
    function renderAccountingMenu(url, loadHistory) {
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'accounting', action: 'renderAccountingMenu')}";
        if (loadHistory) {
            if (currentMenu == MENU_ID_ACCOUNTING) {
                loadLeftMenu(loc, null);
            } else {
                currentMenu = MENU_ID_ACCOUNTING;
                $.history.load(loc);
            }
        } else {
            currentMenu = MENU_ID_ACCOUNTING;
            loadLeftMenu(loc, url);
        }
    }
    function renderQsMenu(url, loadHistory) {
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'qs', action: 'renderQsMenu')}";
        if (loadHistory) {
            if (currentMenu == MENU_ID_QS) {
                loadLeftMenu(loc, null);
            } else {
                currentMenu = MENU_ID_QS;
                $.history.load(loc);
            }
        } else {
            currentMenu = MENU_ID_QS;
            loadLeftMenu(loc, url);
        }
    }
    function renderFixedAssetMenu(url, loadHistory) {
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'fixedAsset', action: 'renderFixedAssetMenu')}";
        if (loadHistory) {
            if (currentMenu == MENU_ID_FIXED_ASSET) {
                loadLeftMenu(loc, null);
            } else {
                currentMenu = MENU_ID_FIXED_ASSET;
                $.history.load(loc);
            }
        } else {
            currentMenu = MENU_ID_FIXED_ASSET;
            loadLeftMenu(loc, url);
        }
    }
    function renderExchangeHouseMenu(url, loadHistory) {
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'exhExchangeHouse', action: 'renderExchangeHouseMenu')}";
        if (loadHistory) {
            if (currentMenu == MENU_ID_EXCHANGE_HOUSE) {
                loadLeftMenu(loc, null);
            } else {
                currentMenu = MENU_ID_EXCHANGE_HOUSE;
                $.history.load(loc);
            }
        } else {
            currentMenu = MENU_ID_EXCHANGE_HOUSE;
            loadLeftMenu(loc, url);
        }
    }
    function renderProjectTrackMenu(url, loadHistory) {
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'projectTrack', action: 'renderProjectTrackMenu')}";
        if (loadHistory) {
            if (currentMenu == MENU_ID_PROJECT_TRACK) {
                loadLeftMenu(loc, null);
            } else {
                currentMenu = MENU_ID_PROJECT_TRACK;
                $.history.load(loc);
            }
        } else {
            currentMenu = MENU_ID_PROJECT_TRACK;
            loadLeftMenu(loc, url);
        }
    }

    function renderArmsMenu(url, loadHistory) {
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'arms', action: 'renderArmsMenu')}";
        if (loadHistory) {
            if (currentMenu == MENU_ID_ARMS) {
                loadLeftMenu(loc, null);
            } else {
                currentMenu = MENU_ID_ARMS;
                $.history.load(loc);
            }
        } else {
            currentMenu = MENU_ID_ARMS;
            loadLeftMenu(loc, url);
        }
    }

    function renderSarbMenu(url, loadHistory) {
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'sarb', action: 'renderSarbMenu')}";
        if (loadHistory) {
            if (currentMenu == MENU_ID_SARB) {
                loadLeftMenu(loc, null);
            } else {
                currentMenu = MENU_ID_SARB;
                $.history.load(loc);
            }
        } else {
            currentMenu = MENU_ID_SARB;
            loadLeftMenu(loc, url);
        }
    }

    function renderDocumentMenu(url, loadHistory) {
        showLoadingSpinner(true);
        var loc = "${createLink(controller: 'document', action: 'renderDocumentMenu')}";
        if (loadHistory) {
            if (currentMenu == MENU_ID_DOCUMENT) {
                loadLeftMenu(loc, null);
            } else {
                currentMenu = MENU_ID_DOCUMENT;
                $.history.load(loc);
            }
        } else {
            currentMenu = MENU_ID_DOCUMENT;
            loadLeftMenu(loc, url);
        }
    }

    function loadLeftMenu(loc, url) {
        selectDockMenu(loc);
        jQuery.ajax({
            type: 'post',
            url: loc,
            success: function (data, textStatus) {
                if (data == 'false') {
                    redirectToLogoutPage();
                } else {
                    populateLeftMenuAndDashBoard(data, url);
                }
            }
        });
    }

    function selectDockMenu(loc) {
        clearDocMenuHover();
        if (loc.endsWith('renderBudgetMenu')) {
            $('#dockMenuBudget').addClass('active');
        } else if (loc.endsWith('renderApplicationMenu')) {
            $('#dockMenuSettings').addClass('active');

        } else if (loc.endsWith('renderProcurementMenu')) {
            $('#dockMenuProc').addClass('active');

        } else if (loc.endsWith('renderInventoryMenu')) {
            $('#dockMenuInv').addClass('active');

        } else if (loc.endsWith('renderAccountingMenu')) {
            $('#dockMenuAcc').addClass('active');

        } else if (loc.endsWith('renderQsMenu')) {
            $('#dockMenuQs').addClass('active');
        }
        else if (loc.endsWith('renderFixedAssetMenu')) {
            $('#dockMenuFixedAsset').addClass('active');
        }
        else if (loc.endsWith('renderExchangeHouseMenu')) {
            $('#dockMenuExchangeHouse').addClass('active');
        }
        else if (loc.endsWith('renderProjectTrackMenu')) {
            $('#dockMenuProjectTrack').addClass('active');
        }
        else if (loc.endsWith('renderArmsMenu')) {
            $('#dockMenuArms').addClass('active');
        }
        else if (loc.endsWith('renderSarbMenu')) {
            $('#dockMenuSarb').addClass('active');
        }
        else if (loc.endsWith('renderDocumentMenu')) {
            $('#dockMenuDocument').addClass('active');
        }
    }

    function populateLeftMenuAndDashBoard(data, url) {
        var lstTemplates = $(data.lstTemplates);
        var menuData = lstTemplates[0].content;
        var dashBoardData = lstTemplates[1].content;

        $('#accordion1').html(menuData).accordion('destroy').accordion({autoHeight: true, fillSpace: true, active: 0});
        bindAutoLoadClass();
        if (url == null) {
            populateDashBoard(dashBoardData);     // load corresponding dashboard
        } else {
            findAndSetTabId(url)
            markSelectMenuItem(url);
        }
        showLoadingSpinner(false);
    }

    function populateDashBoard(data) {
        $('#contentHolder').html(data);
    }

    function loadInitialLeftMenu() {
        var childSize = $('#dockMenuContainer > ul').children('li').size();
        if (childSize > 0) {
            var firstChild = $('#dockMenuContainer >ul').children('li:first-child');
            firstChild.click();
        }
    }

    function loadNumberedMenu(menuNumber, url) {
        if ((currentMenu) && (menuNumber == currentMenu)) {
            if (url) {                       // url may not be supplied in case of non-menu contents(e.g. change pass)
                findAndSetTabId(url);       // set menu Tab
                markSelectMenuItem(url);    // mark menu selected
            }
            return false;
        }
        var childSize = $('#dockMenuContainer > ul').children('li').size();
        if (childSize > 0) {
            var specificChild = $('#dockMenuContainer > ul').children("li:nth-child(" + menuNumber + ")")

            // Now we have to reload a new menu(other than current one)
            clearDocMenuHover();
            specificChild.addClass('active');  // mark selected
            switch (menuNumber) {
                case MENU_ID_APPLICATION:
                    renderApplicationMenu(url, false);     // admin menu
                    break;
                case MENU_ID_BUDGET:
                    renderBudgetMenu(url, false);
                    break;
                case MENU_ID_PROCUREMENT:
                    renderProcurementMenu(url, false);
                    break;
                case MENU_ID_INVENTORY:
                    renderInventoryMenu(url, false);
                    break;
                case MENU_ID_ACCOUNTING:
                    renderAccountingMenu(url, false);
                    break;
                case MENU_ID_QS:
                    renderQsMenu(url, false);
                    break;
                case MENU_ID_FIXED_ASSET:
                    renderFixedAssetMenu(url, false);
                    break;
                case MENU_ID_EXCHANGE_HOUSE:
                    renderExchangeHouseMenu(url, false);
                    break;
                case MENU_ID_PROJECT_TRACK:
                    renderProjectTrackMenu(url, false);
                    break;
                case MENU_ID_ARMS:
                    renderArmsMenu(url, false);
                    break;
                case MENU_ID_SARB:
                    renderSarbMenu(url, false);
                    break;
                case MENU_ID_DOCUMENT:
                    renderDocumentMenu(url, false);
                    break;
                default:
                    renderApplicationMenu(url, false);
            }
        }
    }

    function findAndSetTabId(url) {
        var divObj = $("a[href='" + url + "']").parents('div:first');
        var tabid = parseInt($(divObj).prevAll("div").size());
        setTabId(tabid);
    }

    function markSelectMenuItem(url) {
        $('div.ui-layout-west a').removeClass('selected');
        $("a[href='" + url + "']").addClass('selected');
    }

    function redirectToLogoutPage() {
        window.location = "<g:createLink controller="logout"/>";
    }
</script>