package com.athena.mis.projecttrack.controller

class ProjectTrackController {
	static allowedMethods = [renderApplicationMenu: "POST"]

	def springSecurityService

	def renderProjectTrackMenu() {
		try{
		if (!springSecurityService.isLoggedIn()) {
			render('false')
			return
		}
		render(contentType: "text/json") {
			lstTemplates = array {
				element([name: 'menu', content: g.render(plugin: 'projectTrack', template: '/projectTrack/leftmenuProjectTrack')])
				element([name: 'dashBoard', content: g.render(plugin: 'projectTrack', template: '/projectTrack/dashBoardProjectTrack', model: null)])
			}
		}
		}catch (Exception e){
			println e
		}
	}

}
