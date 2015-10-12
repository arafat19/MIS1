package com.athena.mis.arms.controller

class ArmsController {

    static allowedMethods = [renderArmsMenu: "POST"]

    def springSecurityService

    def renderArmsMenu() {
        if (!springSecurityService.isLoggedIn()) {
            render('false')
            return
        }
        render(contentType: "text/json") {
            lstTemplates = array {
                element([name: 'menu', content: g.render(plugin: 'arms', template: '/arms/leftmenuArms')])
                element([name: 'dashBoard', content: g.render(plugin: 'arms', template: '/arms/dashBoardArms', model: null)])
            }
        }
    }
}
