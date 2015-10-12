package com.athena.mis.exchangehouse.service

import com.athena.mis.BaseService
import com.athena.mis.exchangehouse.entity.ExhSanction

class ExhSanctionService extends BaseService {

    static transactional = false
    // save bulk sanction from csv
    public boolean create(List<ExhSanction> lstSanction) {
        // First reset sanction sequence
        createSanctionSequence()
        // Truncate table Sanction
        truncateSanction()

        // now save each sanction
        for (int i = 0; i < lstSanction.size(); i++) {
            ExhSanction sanction = lstSanction[i]
            sanction.save()
        }
        return true
    }

    private static final String SQL_DELETE_SANCTION_SEQ = """ DROP SEQUENCE IF EXISTS exh_sanction_id_seq """
    private static final String SQL_CREATE_SANCTION_SEQ = """
        CREATE SEQUENCE exh_sanction_id_seq
        INCREMENT 1
        MINVALUE 1
        MAXVALUE 2147483647
        START 1
        CACHE 1 """
    // Create sequence for Sanction
    public void createSanctionSequence() {
        executeSql(SQL_DELETE_SANCTION_SEQ)
        executeSql(SQL_CREATE_SANCTION_SEQ)
    }

    private static final String SQL_TRUNCATE_SANCTION = """TRUNCATE TABLE exh_sanction"""

    private void truncateSanction() {
        boolean success = executeSql(SQL_TRUNCATE_SANCTION)
    }

    //get sanction object
    public ExhSanction read(Long id) {
        ExhSanction sanction = ExhSanction.read(id)
        return sanction
    }

	public void createDefaultData() {
		ExhSanction sanction = new ExhSanction()
		sanction.name = 'abc,xyz'
		sanction.save()
	}

}







