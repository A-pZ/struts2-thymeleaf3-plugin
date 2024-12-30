/**
 *
 */
package com.github.apz.struts.plugins.thymeleaf.diarect;

import java.util.LinkedHashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * diarect:sth.
 * 
 * @author A-pZ
 * @version 3.0.0.BETA03
 *
 */
public class FieldDialect extends AbstractProcessorDialect  {
	
    public static final String NAME = "Struts2Standard";
    public static final String PREFIX = "sth";
    public static final int PROCESSOR_PRECEDENCE = 1000;

	public FieldDialect(final TemplateMode templateMode, final String dialectPrefix) {
		super(NAME, PREFIX, PROCESSOR_PRECEDENCE);
	}
	
	@Override
    public Set<IProcessor> getProcessors(final String dialectPrefix) {
		Set<IProcessor> processors = new LinkedHashSet<IProcessor>();
		processors.add(new FieldErrorAttributeProcessor(dialectPrefix));
		
		return processors;
    }
}
