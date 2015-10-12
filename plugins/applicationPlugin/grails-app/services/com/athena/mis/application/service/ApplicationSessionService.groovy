package com.athena.mis.application.service

import org.apache.log4j.Logger

class ApplicationSessionService {
    static transactional = false
    private Logger log = Logger.getLogger(getClass());

    public List<Map> lstSessionModel = []         // list of all custom session objects
    /* Map-schema = [session:?,appUser:?,clientBrowser:?,clientIP:?,clientOS:?] */

    public void add(Map customSessionObj) {
        lstSessionModel.add(customSessionObj)
    }

    public void remove(Map customSessionObj) {
        try {
            boolean isRemoved = lstSessionModel.remove(customSessionObj)
        } catch (Exception e) {
            log.error('Error in ApplicationSessionService.remove() ' + e.getMessage())
        }
    }

    public Map readBySessionId(String sessionId) {
        for (int i = 0; i < lstSessionModel.size(); i++) {
            if (lstSessionModel[i].session.id.equals(sessionId)) {
                return lstSessionModel[i]
            }
        }
        return null
    }

    private static final String SCOPED_SESSION = 'scopedTarget.sessionUtility'

    public List<Map> list() {
        List<Map> lstInvalidSessions = []
        for (int i = 0; i < lstSessionModel.size(); i++) {
            try {       // check if any invalid/expired session exists
                def x = lstSessionModel[i].session.getAttribute(SCOPED_SESSION)
            } catch (Exception e) {
                lstInvalidSessions << lstSessionModel[i]
            }
        }
        if (lstInvalidSessions.size() > 0) {
            lstSessionModel.removeAll(lstInvalidSessions)
        }
        return lstSessionModel
    }

}
