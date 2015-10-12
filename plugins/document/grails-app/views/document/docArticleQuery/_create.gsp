<%@ page import="com.athena.mis.application.utility.SystemEntityTypeCacheUtility; com.athena.mis.document.config.DocSysConfigurationCacheUtility; com.athena.mis.PluginConnector" %>
<div class="row">
    <div class="col-md-6" id="searchResultDiv">
        <div id="listView"></div>

        <div id="pager" class="k-pager-wrap"></div>
    </div>

    <div class="col-md-6">
        <div id="application_top_panel" class="panel panel-primary">
            <div class="panel-heading">
                <div class="panel-title">
                    Search File/Article
                </div>
            </div>

            <form id='articleForm' class="form-horizontal form-widgets" name='articleForm' role="form" method="post">
                <input type="hidden" name="id" id="id"/>
                <input type="hidden" name="version" id="version"/>

                <div class="panel-body">
                    <div class="form-group">
                        <div class="col-md-3">
                            <label class="control-label label-required" for="name">Name:</label>
                        </div>

                        <div class="col-md-6">
                            <input type="text" class="k-textbox" id="name" name="name" tabindex="1"
                                   placeholder="Name of the article query" required
                                   validationMessage="Required"/>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="name"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-3">
                            <label class="control-label label-required" for="contentTypeId">Content Type:</label>
                        </div>

                        <div class="col-md-6">
                            <app:dropDownSystemEntity
                                    dataModelName="dropDownContentType"
                                    required="true"
                                    validationMessage="Required"
                                    name="contentTypeId"
                                    tabindex="2"
                                    showHints="false"
                                    typeId="${SystemEntityTypeCacheUtility.DOC_CONTENT_TYPE}">
                            </app:dropDownSystemEntity>
                        </div>

                        <div class="col-md-3 pull-left">
                            <span class="k-invalid-msg" data-for="contentTypeId"></span>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-12">
                            <label class="control-label label-required" for="criteria">Criteria:</label>
                        </div>
                    </div>

                    <div class="form-group">
                        <div class="col-md-12">
                            <textarea type="text" class="k-textbox" id="criteria" name="criteria" tabindex="3" rows="3"
                                      placeholder="Article query criteria" required validationMessage="Required"/>
                            <span class="k-invalid-msg" data-for="title"></span>
                        </div>
                    </div>

                </div>

                <div class="panel-footer">
                    <button name="create" id="create" type="submit" data-role="button"
                            class="k-button k-button-icontext"
                            role="button" tabindex="5"
                            aria-disabled="false"><span class="k-icon k-i-plus"></span>Save
                    </button>
                    <button id="clearFormButton" name="clearFormButton" type="button" data-role="button"
                            class="k-button k-button-icontext" role="button" tabindex="6"
                            aria-disabled="false" onclick='resetForm();'><span
                            class="k-icon k-i-close"></span>Cancel
                    </button>
                </div>
            </form>
        </div>

        <div id="articleQueryGrid">
            <table id="flexArticleQuery"></table>
        </div>
    </div>

</div>

<style>
#listView {
    overflow-y: auto;
}

.popover {
    display: block;
    position: relative;
    margin: 5px 10px 5px 5px;
    max-width: none;
    cursor: pointer;
}

.popover-title {
    background-color: #EAEAEA;
}
</style>