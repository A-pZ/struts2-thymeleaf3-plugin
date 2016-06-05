/**
 * 
 */
package serendip.struts.plugins.thymeleaf;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.context.ApplicationContext;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.IWebContext;

/**
 * @author A-pZ
 *
 */
public class SpringWebContext extends AbstractContext implements IWebContext {

    public static final String BEANS_VARIABLE_NAME = "beans";
    private static final ConcurrentHashMap<ApplicationContext, HashMap<String, Object>> variableMapPrototypes =
            new ConcurrentHashMap<ApplicationContext, HashMap<String, Object>>();
    
    private final ApplicationContext applicationContext;
    private final HttpServletRequest httpServletRequest;
    private final HttpServletResponse httpServletResponse;
    private final ServletContext servletContext;


    /**
     * <p>
     *   Creates a new instance of a SpringWebContext.
     * </p>
     * 
     * @param request the request object
     * @param response the response object
     * @param servletContext the servlet context
     * @param locale the locale
     * @param variables the variables to be included into the context
     * @param appctx the Spring application context
     */
    public SpringWebContext(final HttpServletRequest request,
                            final HttpServletResponse response,
                            final ServletContext servletContext ,
                            final Locale locale, 
                            final Map<String, ?> variables, 
                            final ApplicationContext appctx) {
        //super(request, response, servletContext, locale, addSpringSpecificVariables(variables, appctx));
    	
    	super(locale,addSpringSpecificVariables(variables, appctx));
    	this.httpServletRequest = request;
    	this.httpServletResponse = response;
    	this.servletContext = servletContext;
        this.applicationContext = appctx;
    }

    
    

    @SuppressWarnings("unchecked")
    private static Map<String,Object> addSpringSpecificVariables(final Map<String, ?> variables, final ApplicationContext appctx) {

        HashMap<String,Object> variableMapPrototype = variableMapPrototypes.get(appctx);
        if (variableMapPrototype == null) {
            variableMapPrototype = new HashMap<String, Object>(20, 1.0f);
            // We will use a singleton-per-appctx Beans instance, and that's alright
            final Beans beans = new Beans(appctx);
            variableMapPrototype.put(BEANS_VARIABLE_NAME, beans);
            variableMapPrototypes.put(appctx, variableMapPrototype);
        }

        final Map<String,Object> newVariables;
        synchronized (variableMapPrototype) {
            newVariables = (Map<String, Object>) variableMapPrototype.clone();
        }

        if (variables != null) {
            newVariables.putAll(variables);
        }

        return newVariables;
        
    }


    
    public ApplicationContext getApplicationContext() {
        return this.applicationContext;
    }
    
	
	@Override
	public HttpServletRequest getRequest() {
		return this.httpServletRequest;
	}

	@Override
	public HttpServletResponse getResponse() {
		return this.httpServletResponse;
	}

	@Override
	public HttpSession getSession() {
		return this.httpServletRequest.getSession(false);
	}

	@Override
	public ServletContext getServletContext() {
		return this.servletContext;
	}

}
