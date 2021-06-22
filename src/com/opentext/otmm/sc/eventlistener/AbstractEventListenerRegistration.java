package com.opentext.otmm.sc.eventlistener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.artesia.common.exception.BaseTeamsException;
import com.artesia.event.services.EventServices;
import com.artesia.security.SecuritySession;
import com.opentext.otmm.sc.eventlistener.util.EventListenerUtils;

public abstract class AbstractEventListenerRegistration implements ServletContextListener {
	
	protected static final String USER_ALIAS_TSUPER = "tsuper";
	
	protected static String clientId;


	protected static final Log log = LogFactory.getLog(AbstractEventListenerRegistration.class);
	
	public AbstractEventListenerRegistration() {
		super();
	}

	@Override
	public void contextDestroyed(ServletContextEvent event) {
		log.info(">>> " + getClassName() + " >> contextDestroyed() Start >>>");
		
		try {
			SecuritySession session = EventListenerUtils.getLocalSession(USER_ALIAS_TSUPER);
			EventServices.getInstance().removeEventListener(clientId, session);
		} catch (BaseTeamsException e) {
			log.error("An exception occured while destroying the servlet context", e);
		}
		
		log.info("<<< " + getClassName() + " >> contextDestroyed() End <<<");		
	}

	protected String getClassName() {
		return getClass().getName();
	}

}