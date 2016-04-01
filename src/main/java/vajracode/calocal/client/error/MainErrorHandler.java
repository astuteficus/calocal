package vajracode.calocal.client.error;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.http.client.Response;
import com.google.gwt.logging.client.SimpleRemoteLogHandler;
import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.StatusCodeException;

import fr.putnami.pwt.core.error.client.ErrorDisplayer.Severity;
import fr.putnami.pwt.core.error.client.ErrorHandler;
import fr.putnami.pwt.core.error.client.ErrorManager;
import gwt.material.design.client.ui.MaterialToast;
import vajracode.calocal.client.elements.Header;
import vajracode.calocal.client.framework.CalocalEventBus;
import vajracode.calocal.client.i18n.I18nConstants;
import vajracode.calocal.client.modals.WaitModal;
import vajracode.calocal.client.utils.RestUtils;
import vajracode.calocal.shared.exceptions.FieldException;

public class MainErrorHandler implements ErrorHandler {

	private I18nConstants msgs = GWT.create(I18nConstants.class);	
	private Logger logger;
	private CalocalEventBus eventBus;
	
	public MainErrorHandler() {
		logger = Logger.getLogger("critical");
		for (Handler h: logger.getHandlers())
			logger.removeHandler(h);
		logger.addHandler(new SimpleRemoteLogHandler());
	}
	
	
	@Override
	public boolean handle(Throwable e) {
		WaitModal.hide();
		
		if (e instanceof IncompatibleRemoteServiceException) {
			eventBus.setBody(new Header(msgs.updateApplication()));
			GWT.log(e.getMessage());
			return true;
		}				
		
//		String cause = null, message = null;
//		if (e instanceof CommandException){
//			CommandException ce = (CommandException)e; 
//			cause = ce.getCauseClassName();
//			message = ce.getCauseMessage();			
//		}
		GWT.log(e.toString());	
			
		switch(RestUtils.getStatuCode(e)) {		
			case Response.SC_UNAUTHORIZED:
				eventBus.authError(msgs.needBeAuth());
				return true;				
			case Response.SC_FORBIDDEN:			
				MaterialToast.fireToast(msgs.noAccess());
				return true;			
			case Response.SC_NOT_FOUND:			
				MaterialToast.fireToast(msgs.notFound());
				return true;
			case Response.SC_BAD_REQUEST:			
				MaterialToast.fireToast(msgs.badRequest());
				return true;
			case Response.SC_CONFLICT:			
				MaterialToast.fireToast(msgs.conflict());
				return true;			
			case Response.SC_INTERNAL_SERVER_ERROR:			
				MaterialToast.fireToast(msgs.unknownError());
				return true;			
			case 0:
				MaterialToast.fireToast(msgs.noNetwork());
				return true;
		} 
		
		e = unwrap(e);
		
		if (e instanceof FieldException) {
			MaterialToast.fireToast(e.getMessage());
			return true;
		}
		
		if (isNetworkException(e)){				
			ErrorManager.get().getErrorDisplayer().display(msgs.networkError(), e, Severity.DANGER);
		} else {
			logger.log(Level.SEVERE, "Client side exception", e);
			ErrorManager.get().getErrorDisplayer().display(e, Severity.DANGER);
		}
		
		return true;
	}
	
	private boolean isNetworkException(Throwable e) {
		if (e instanceof StatusCodeException) return true;
		String msg = e.getMessage();
		if (msg == null) return false;
		return "Незавершенная строковая константа".equals(msg) || "Синтаксическая ошибка".equals(msg);
	}


	@Override
	public int getPriority() {				
		return Integer.MAX_VALUE;
	}
	
	public static Throwable unwrap(Throwable e) {
		while (e instanceof UmbrellaException) {
			UmbrellaException ue = (UmbrellaException) e;
			if (ue.getCauses().size() == 1) {
				e = unwrap(ue.getCauses().iterator().next());
			} else 
				return e;
		}
		return e;
	}


	public void setEventBus(CalocalEventBus eventBus) {
		this.eventBus = eventBus;
	}

}
