/*
 * Copyright 2013 Steven Benitez.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.apz.struts.plugins.thymeleaf.spi;

import com.github.apz.struts.plugins.thymeleaf.diarect.FieldDialect;
import jakarta.servlet.ServletContext;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.inject.Container;
import org.apache.struts2.inject.Inject;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.messageresolver.SpringMessageResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A default implementation of {@link TemplateEngineProvider}.
 *
 * @author A-pZ ( Original source : Steven Benitez )
 */
public class DefaultTemplateEngineProvider implements TemplateEngineProvider {
	// HTML5 is the future!
	private String templateMode = "HTML5";
	private String characterEncoding = "UTF-8";
	// This will convert "home" to "/WEB-INF/templates/home.html"
	private String prefix = "/WEB-INF/templates/";
	private String suffix = ".html";
	private boolean cacheable = true;

	// Default template cache TTL to 1 hour. If not set, entries would live in
	// cache until expelled by LRU.
	private Long cacheTtlMillis = 3600000L;

	protected TemplateEngine templateEngine;

	private String templateEngineName = "default";
	private Container container;
	private Map<String, TemplateEngine> templateEngines = new HashMap<String, TemplateEngine>();

	/**
	 * Configure settings from the struts.xml or struts.properties, using
	 * sensible defaults if values are not provided.
	 */
	public void configure() {
		WebApplicationTemplateResolver templateResolver = getWebApplicationTemplateResolver();
		templateEngine.setTemplateResolver(templateResolver);
		if (templateEngine instanceof SpringTemplateEngine) {
			SpringMessageResolver springMessageResolver = new SpringMessageResolver();
			((SpringTemplateEngine) templateEngine).setMessageSource(springMessageResolver.getMessageSource());
		} else {
			StandardMessageResolver messageResolver = new StandardMessageResolver();
			templateEngine.setMessageResolver(messageResolver);
		}

		FieldDialect fieldDialect = new FieldDialect(TemplateMode.HTML ,"sth");
		templateEngine.addDialect(fieldDialect);
	}

	private WebApplicationTemplateResolver getWebApplicationTemplateResolver() {
		ServletContext servletContext = ServletActionContext.getServletContext();
		IWebApplication iWebApplication = JakartaServletWebApplication.buildApplication(servletContext);
		WebApplicationTemplateResolver templateResolver =
				new WebApplicationTemplateResolver(iWebApplication);

		templateResolver.setTemplateMode(templateMode);
		templateResolver.setCharacterEncoding(characterEncoding);
		templateResolver.setPrefix(prefix);
		templateResolver.setSuffix(suffix);
		templateResolver.setCacheable(cacheable);
		templateResolver.setCacheTTLMs(cacheTtlMillis);
		return templateResolver;
	}

	@Override
	public TemplateEngine get() {
		if ( templateEngine == null ) {
			Container container = Dispatcher.getInstance().getContainer();
			setContainer(container);

			// loading template engine from struts2 di container.
			this.templateEngine = templateEngines.get(templateEngineName);
			configure();
		}

		return templateEngine;
	}

	@Inject(value = "struts.thymeleaf.templateMode", required = false)
	public void setTemplateMode(String templateMode) {
		this.templateMode = templateMode;
	}

	@Inject(value = "struts.thymeleaf.encoding", required = false)
	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	@Inject(value = "struts.thymeleaf.prefix", required = false)
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	@Inject(value = "struts.thymeleaf.suffix", required = false)
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	@Inject(value = "struts.thymeleaf.cacheable", required = false)
	public void setCacheable(String cacheable) {
		this.cacheable = Boolean.parseBoolean(cacheable);
	}

	@Inject(value = "struts.thymeleaf.cacheTtlMillis", required = false)
	public void setCacheTtlMillis(String cacheTtlMillis) {
		this.cacheTtlMillis = Long.parseLong(cacheTtlMillis);
	}

	/**
	 * loading di container configuration from struts-plugin.xml , choose thymeleaf template engine.
	 * @param container Struts2Container
	 */
	public void setContainer(Container container) {
		this.container = container;

		Map<String, TemplateEngine> map = new HashMap<String, TemplateEngine>();

		// loading TemplateEngine class from DI Container.
		Set<String> prefixes = container.getInstanceNames(TemplateEngine.class);
		for (String prefix : prefixes) {
			TemplateEngine engine = container.getInstance(TemplateEngine.class, prefix);
			map.put(prefix, engine);
		}
		this.templateEngines = Collections.unmodifiableMap(map);
	}

	/**
	 * Thymeleaf template type loading from struts.properties.
	 * @param templateEngineName ( default | spring )
	 */
	@Inject(value = "struts.thymeleaf.templateEngineName")
	public void setTemplateEngineName(String templateEngineName) {
		this.templateEngineName = templateEngineName;
	}
}
