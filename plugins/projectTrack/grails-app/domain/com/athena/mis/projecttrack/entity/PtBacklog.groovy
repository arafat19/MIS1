/**
 * Module  - ProjectTrack
 * Purpose - Entity that contains all properties of Module
 */

package com.athena.mis.projecttrack.entity

class PtBacklog {

	long id				// object.id (primary key - auto generated by own id_seq)
	int version         // object.version
	String idea         // idea
	String actor        // As a (eg. admin)
	String purpose      // purpose (eg. I want to ..)
	String benefit      // benefit (eg. so that)
	float hours         // hours
	long ownerId        // AppUser.id
    String useCaseId    // Action service name(e.g- 'CreatePtProjectActionService')
	long priorityId     // SystemEntity.id
	long statusId       // SystemEntity.id
	long moduleId       // PtModule.id
	long sprintId       // PtSprint.id
	long companyId		//Company.id
	long createdBy      //AppUser.id
	Date createdOn
	long updatedBy = 0L     //AppUser.id
	Date updatedOn
	Date completedOn
    long acceptedBy = 0L    //AppUser.id
	Date acceptedOn
    String url              // url of backlog

	static constraints = {
		updatedOn nullable: true
        completedOn nullable: true
        acceptedOn nullable: true
        useCaseId nullable: true
        idea(nullable: false, blank: false,maxSize: 1000)
        url(nullable: true)
        url(unique: true)
	}

	static mapping = {
		id generator: 'sequence' , params: [sequence : 'pt_backlog_id_seq']		//id sequence generator of PtBacklog domain
		priorityId index: 'pt_backlog_priority_id_idx'
		statusId index: 'pt_backlog_status_id_idx'
		moduleId index: 'pt_backlog_module_id_idx'
		sprintId index: 'pt_backlog_sprint_id_idx'
		companyId index: 'pt_backlog_company_id_idx'
	}
}
