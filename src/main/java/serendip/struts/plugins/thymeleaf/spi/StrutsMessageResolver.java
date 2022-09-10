package serendip.struts.plugins.thymeleaf.spi;

import org.springframework.stereotype.Component;
import org.thymeleaf.spring4.messageresolver.SpringMessageResolver;

/**
 * For struts2 messages bridge to Thymeleaf3. Use SpringMVV Message Source.
 * @author A-pZ
 *
 */
@Component
public class StrutsMessageResolver extends SpringMessageResolver {

	public StrutsMessageResolver() {
		setMessageSource(new StrutsMessageSource());
	}

}
