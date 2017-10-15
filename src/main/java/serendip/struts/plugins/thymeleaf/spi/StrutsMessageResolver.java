package serendip.struts.plugins.thymeleaf.spi;

import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.messageresolver.AbstractMessageResolver;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.util.GlobalLocalizedTextProvider;

/**
 * For struts2 messages bridge to Thymeleaf3.
 * @author A-pZ
 *
 */
public class StrutsMessageResolver extends AbstractMessageResolver {

	@Override
	public String resolveMessage(ITemplateContext context, Class<?> origin, String key, Object[] messageParameters) {
		GlobalLocalizedTextProvider textProvider = new GlobalLocalizedTextProvider();
		return textProvider.findDefaultText(key, ActionContext.getContext().getLocale(), messageParameters);
	}

	@Override
	public String createAbsentMessageRepresentation(ITemplateContext context, Class<?> origin, String key,
			Object[] messageParameters) {
		return "??" + key + "??";
	}

}
