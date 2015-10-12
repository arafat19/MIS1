package com.athena.mis.projecttrack.entity

class PtProjectModule {

	long id             // object id
	long projectId		// PtProject.id
	long moduleId       // PtModule.id

	long companyId		//Company.id

	static constraints = {
		projectId unique: 'moduleId'	// PtProject.id and PtModule.id - combine unique
	}

	static mapping = {

        version false		//version false for Project-Module mapping
        id generator: 'sequence', params: [sequence:'pt_project_module_id_seq']
        projectId index: 'pt_project_module_project_id_idx' 	// indexing projectId
		moduleId index: 'pt_project_module_module_id_idx'     // indexing moduleId
		companyId index: 'pt_project_module_company_id_idx'     // indexing companyId
	}
}
