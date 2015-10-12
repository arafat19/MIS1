package com.athena.mis.arms.controller

import org.codehaus.groovy.grails.web.servlet.mvc.GrailsParameterMap

class RmsTaskTraceController {
    static allowedMethods = [
            showRmsTaskHistory: "POST",
            searchRmsTaskHistory: "POST"
    ]

    def showRmsTaskHistory() {
        render(view: '/arms/rmsTaskTrace/showRmsTaskHistory')
    }

    def searchRmsTaskHistory() {
        GrailsParameterMap parameterMap = (GrailsParameterMap) params
        render(template: '/arms/rmsTaskTrace/taskHistory', model: [model: parameterMap])
    }
}
