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
package com.github.apz.struts.plugins.thymeleaf;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.thymeleaf.context.AbstractContext;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.web.IWebExchange;

import java.util.Locale;
import java.util.Map;

/**
 * Extends the {@link org.thymeleaf.context.WebContext} to provide access to the
 * Struts action.
 *
 * For actions that implement the {@link org.apache.struts2.locale.LocaleProvider}
 * interface (i.e., actions that extend ActionSupport), the action's locale will
 * be used in this context. Otherwise, the context will default to the
 * {@link jakarta.servlet.http.HttpServletRequest request's} locale.
 *
 * @author A-pZ
 * @version 1.0.0
 */
public class StrutsContext extends AbstractContext implements IWebContext {

	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private final ServletContext servletContext;

	public StrutsContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext,
			Locale locale,Map<String, Object> variables) {

		super(locale,variables);
		this.request = request;
		this.response = response;
		this.servletContext = servletContext;

	}

	public HttpServletRequest getRequest() {
		return this.request;
	}

	public HttpSession getSession() {
		return this.request.getSession(false);
	}

	public HttpServletResponse getResponse() {
		return this.response;
	}

	public ServletContext getServletContext() {
		return this.servletContext;
	}

	@Override
	public IWebExchange getExchange() {
		return null;
	}
}
