<%@ page import="org.codehaus.groovy.grails.commons.ConfigurationHolder" %>

<script type="text/javascript">
    $(document).ready(function () {
        $('#divFeatureList').fadeIn();
        $.featureList(
                $("#tabs li a"),
                $("#output li"), {start_item:0, transition_interval:-1}
        );
        $(document).attr('title', "Welcome to ARMS");
    });
</script>

<div id="divFeatureList" style='width:98%;min-width:250px; display: none'>
    <div id="content">
        <div id="feature_list">
            <ul id="tabs">
                <li><a href="javascript:;">
                    <span class="dashboard_icon my_favourites"></span>
                    <h5 class="feature">MY FAVOURITES</h5>
                    <span>Your favourite content here</span></a>
                </li>
            </ul>
            <ul id="output">
                <li><img src="${ConfigurationHolder.config.theme.application}/images/featurelist/Representation.jpg"/></li>
            </ul>
        </div>
    </div>
</div>
