package com.github.apz.struts.plugins.thymeleaf;

import com.github.apz.struts.plugins.thymeleaf.spi.TemplateEngineProvider;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.struts2.ActionInvocation;
import org.apache.struts2.ActionSupport;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.StrutsConstants;
import org.apache.struts2.inject.Inject;
import org.apache.struts2.locale.LocaleProvider;
import org.apache.struts2.ognl.OgnlUtil;
import org.apache.struts2.result.Result;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Renders a Thymeleaf template as the result of invoking a Struts action.
 *
 * @author A-pZ ( Original source : Steven Benitez )
 * @since 1.0.0
 */
public class ThymeleafResult implements Result {
	private String defaultEncoding = "UTF-8";
	private TemplateEngineProvider templateEngineProvider;
	private String templateName;
	private OgnlUtil ognlUtil;

	/**
	 * The result parameter name to set the name of the template to.
	 *
	 * IMPORTANT! Struts2 will look for this field reflectively to determine
	 * which parameter is the default. This allows us to have a simplified
	 * result configuration. Don't remove it!
	 */
	public static final String DEFAULT_PARAM = "templateName";

	/** instance name of struts2-action */
	public static final String ACTION_VARIABLE_NAME = "action";

	/** field errors */
	public static final String FIELD_ERRORS_NAME = "field";

	/** struts2 conversion errors fields and value */
	public static final String OVERRIDES_NAME = "overrides";

	public ThymeleafResult() {
	}

	public ThymeleafResult(String templateName) {
		this.templateName = templateName;
	}

	@Override
	public void execute(ActionInvocation actionInvocation) throws Exception {
		TemplateEngine templateEngine = templateEngineProvider.get();

		Object action = actionInvocation.getAction();

		// Action instance put to Thymeleaf context.
		Map<String, Object> variables = bindStrutsContext(action);

		Locale locale = null;
		if (action instanceof LocaleProvider) {
			locale = (((LocaleProvider) action).getLocale());
		}

		StrutsContext context = new StrutsContext(getWebExchange(), locale, variables);
		HttpServletResponse response = ServletActionContext.getResponse();

		response.setContentType("text/html");
		response.setCharacterEncoding(defaultEncoding);
		templateEngine.process(templateName, context, response.getWriter());
	}

	@Inject(StrutsConstants.STRUTS_I18N_ENCODING)
	public void setDefaultEncoding(String defaultEncoding) {
		this.defaultEncoding = defaultEncoding;
	}

	@Inject
	public void setTemplateEngineProvider(TemplateEngineProvider templateEngineProvider) {
		this.templateEngineProvider = templateEngineProvider;
	}

    @Inject
    public void setOgnlUtil(OgnlUtil util) {
        this.ognlUtil = util;
    }

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * Binding Struts2 action and context, and field-errors list binding
	 * "field".
	 *
	 * @param action
	 *            Action instance
	 * @return ContextMap
	 */
	Map<String, Object> bindStrutsContext(Object action) {
		Map<String, Object> variables = new HashMap<String, Object>();

		if ( !( action instanceof ActionSupport)) {
			return variables;
		}

		ActionSupport actSupport = (ActionSupport) action;

		// Struts2 field errors.( Map<fieldname , fielderrors>)
		Map<String, List<String>> fieldErrors = actSupport.getFieldErrors();
		variables.put(FIELD_ERRORS_NAME, fieldErrors);

		return variables;
	}

	private IWebExchange getWebExchange() {
		HttpServletRequest request = ServletActionContext.getRequest();
		HttpServletResponse response = ServletActionContext.getResponse();
		ServletContext servletContext = ServletActionContext.getServletContext();
		return JakartaServletWebApplication.buildApplication(servletContext)
				.buildExchange(request, response);
	}
}
