<div id="application_top_panel" class="panel panel-primary">

    <div class="panel-heading">
        <div class="panel-title">
            Search Customer
        </div>
    </div>

    <form id='frmCustomerSearch' name='frmCustomerSearch' class="form-horizontal form-widgets" role="form">
        <div class="panel-body">
            <div class="form-group">
                <label class="col-md-2 control-label label-required" for="searchTypeId">Search Type:</label>

                <div class="col-md-3">
                    <select id="searchTypeId"
                            name="searchTypeId"
                            tabindex="1"></select>
                </div>
                <label class="col-md-2 control-label label-required" for="searchTypeId">Search For:</label>
                <div class="col-md-3">
                    <input type="text" id="queryString" class="k-textbox" name="queryString" tabindex="2"
                           maxlength="30"/>
                </div>
	        </div>
        </div>

        <div class="panel-footer">
            <button  name="search" id="search" type="submit" data-role="button"
                    class="k-button k-button-icontext"
                    role="button" tabindex="3"
                    aria-disabled="false"><span class="k-icon k-i-search"></span>Search
            </button>

        </div>
    </form>
</div>


<div id="divFundTransferInfo" style="padding-bottom: 5px; visibility: visible;"></div>
<table id="flex1" style="display:none"></table>

<g:render template='/exchangehouse/exhCustomer/scriptForCustomerSearch'/>


