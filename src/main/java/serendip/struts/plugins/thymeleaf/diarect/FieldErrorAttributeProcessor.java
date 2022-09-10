/**
 *
 */
package serendip.struts.plugins.thymeleaf.diarect;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.AttributeName;
import org.thymeleaf.model.IAttribute;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractAttributeTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;
import org.unbescape.html.HtmlEscape;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * Struts2 Field-error attribute processor.
 *
 * This processor output field-error as css-class 'errorclass'.
 *
 * @author A-pZ
 *
 */
public class FieldErrorAttributeProcessor extends AbstractAttributeTagProcessor {

	public FieldErrorAttributeProcessor(final String dialectPrefix) {
		super(
	            TemplateMode.HTML, // This processor will apply only to HTML mode
	            dialectPrefix,     // Prefix to be applied to name for matching
	            null,              // No tag name: match any tag name
	            false,             // No prefix to be applied to tag name
	            ATTR_NAME,         // Name of the attribute that will be matched
	            true,              // Apply dialect prefix to attribute name
	            PRECEDENCE,        // Precedence (inside dialect's own precedence)
	            true);             // Remove the matched attribute afterwards
	}

	private static final String ATTR_NAME = "value";
    private static final int PRECEDENCE = 1010;



	/* (非 Javadoc)
	 * @see org.thymeleaf.processor.element.AbstractAttributeTagProcessor#doProcess(org.thymeleaf.context.ITemplateContext, org.thymeleaf.model.IProcessableElementTag, org.thymeleaf.engine.AttributeName, java.lang.String, org.thymeleaf.processor.element.IElementTagStructureHandler)
	 */
	@Override
	protected void doProcess(ITemplateContext context, IProcessableElementTag tag, AttributeName attributeName,
			String attributeValue, IElementTagStructureHandler structureHandler) {
		final IEngineConfiguration configuration = context.getConfiguration();

        /*
         * Obtain the Thymeleaf Standard Expression parser
         */
        final IStandardExpressionParser parser =
                StandardExpressions.getExpressionParser(configuration);

        /*
         * Parse the attribute value as a Thymeleaf Standard Expression
         */
        //final IStandardExpression expression = parser.parseExpression(context, attributeValue);

        // get field name.
        String fieldname = tag.getAttributeValue(null, "name");

        // get field value from struts2 ognl
        Object parameterValue = getFieldValue(fieldname);
        if ( parameterValue != null ) {
        	structureHandler.setAttribute("value", HtmlEscape.escapeHtml5(parameterValue.toString()));
        }
        if ( !hasFieldError(fieldname)) {
        	return;
        }

        // add field-error css class.
        IAttribute cssClass = tag.getAttribute("class");
        String css = cssClass.getValue();
        if ( StringUtils.isBlank(css)) {
        	structureHandler.setAttribute("class", fieldErrorClass(tag));
        } else {
        	structureHandler.setAttribute("class", fieldErrorClass(tag) + " " + css);
        }
    }

	/**
	 * If Struts2 has field-error for request parameter name , return true.
	 * @param fieldname request-parameter name
	 * @return if field-error has target field name, return true.
	 */
	protected boolean hasFieldError(String fieldname) {
		if ( StringUtils.isEmpty(fieldname)) {
			return false;
		}

		Object action = ActionContext.getContext().getActionInvocation().getAction();
		// check action instance 'ActionSupport'.
		if (!(action instanceof ActionSupport)) {
			return false;
		}

		ActionSupport asupport = (ActionSupport) action;
		Map<String, List<String>> fieldErrors = asupport.getFieldErrors();
		if (CollectionUtils.isEmpty(fieldErrors)) {
			return false;
		}
		List<String> targetFieldErrors = fieldErrors.get(fieldname);
		if (CollectionUtils.isEmpty(targetFieldErrors)) {
			return false;
		}

		return true;
	}

	/**
	 * Return Strus2 field value.
	 * @param fieldname fieldname
	 * @return fieldvalue
	 */
	protected Object getFieldValue(String fieldname) {
		ActionContext actionCtx = ActionContext.getContext();
		ValueStack valueStack = actionCtx.getValueStack();
		Object value = valueStack.findValue(fieldname, false);

		String overwriteValue = getOverwriteValue(fieldname);

		if ( overwriteValue != null ) {
			return overwriteValue;
		}
		return value;
	}

	protected String fieldErrorClass(IProcessableElementTag tag) {
		if ( tag.getAttribute("error-css") == null) {
			return "field-error";
		}

		String css = tag.getAttribute("error-css").getValue();
		return css;
	}
	/**
	 * If Type-Convertion Error found at Struts2, overwrite request-
	 * parameter same name.
	 *
	 * @param fieldname parameter-name
	 * @return request-parameter-value(if convertion error occurs,return from struts2 , not else thymeleaf.)
	 */
	protected String getOverwriteValue(String fieldname) {
		ActionContext ctx = ServletActionContext.getActionContext();
		ValueStack stack = ctx.getValueStack();
		Map<Object ,Object> overrideMap = stack.getExprOverrides();

		// If convertion error has not, do nothing.
		if ( overrideMap == null || overrideMap.isEmpty()) {
			return null;
		}

		if (! overrideMap.containsKey(fieldname)) {
			return null;
		}

		String convertionValue = (String)overrideMap.get(fieldname);

		// Struts2-Conponent is wrapped String quote, which erase for output value.
		String altString =  StringEscapeUtils.unescapeJava(convertionValue);
		altString = altString.substring(1, altString.length() -1);

		return altString;
	}
}
