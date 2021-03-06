package com.athena.mis.projecttrack.entity

class PtModule {

	long id			// object id
	int version     // object version
	String name     // module name
    String code     // module code

	long companyId	//Company.id

	static constraints = {
        name(unique: ['name', 'companyId'])
        code (unique: ['name', 'companyId'])
	}

	static mapping = {
		id generator: 'sequence', params: [sequence: 'pt_module_id_seq']   // id sequence generator of PtModule domain
		companyId index: 'pt_module_company_id_idx'     // indexing companyId
	}
}
