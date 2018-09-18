/**
 *
 */
package serendip.struts.plugins.thymeleaf.spi;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import com.opensymphony.xwork2.util.StrutsLocalizedTextProvider;

/**
 * For Struts2 MessageSource.
 *
 * @author a-pz
 *
 */
@Component(value="messageSource")
public class StrutsMessageSource implements MessageSource {

	@Override
	public String getMessage(String code, Object[] args, String defaultMessage, Locale locale) {
		StrutsLocalizedTextProvider textProvider = new StrutsLocalizedTextProvider();
		String message = textProvider.findDefaultText(code, locale, args);
		return (message != null) ? message : defaultMessage;
	}

	@Override
	public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
		StrutsLocalizedTextProvider textProvider = new StrutsLocalizedTextProvider();
		String message = textProvider.findDefaultText(code, locale, args);
		return message;
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		return null;
	}

}
