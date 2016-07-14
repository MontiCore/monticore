/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.generating.templateengine;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;

import de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine;
import de.monticore.generating.templateengine.freemarker.MontiCoreFreeMarkerException;
import de.se_rwth.commons.logging.Log;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 * @since   TODO: add version number
 *
 */
public class MyFreeMarkerTemplateEngine extends FreeMarkerTemplateEngine{

  /**
   * Constructor for de.monticore.generating.templateengine.MyFreemarkerTemplateEngine
   * @param configuration
   */
  public MyFreeMarkerTemplateEngine(Configuration configuration) {
    super(configuration);
  }
  
  /**
   * @see de.monticore.generating.templateengine.freemarker.FreeMarkerTemplateEngine#run(java.lang.StringBuilder, java.lang.Object, freemarker.template.Template)
   */
  @Override
  public void run(StringBuilder buffer, Object data, Template template) {
    Log.errorIfNull(template, "0xA0562 The given template must not be null");
    Writer w = new StringWriter();
    try {
      TemplateLoader l = template.getConfiguration().getTemplateLoader();
      template.process(data, w);
      w.flush();
    }
    catch (TemplateException e) {
      StringBuilder causedExceptionInfo = new StringBuilder();
      if (e.getCause() instanceof MontiCoreFreeMarkerException) {
        throw (MontiCoreFreeMarkerException)e.getCause();
      }

      Throwable targetException;
      if (e.getCause() instanceof InvocationTargetException) {
        targetException =  ((InvocationTargetException)e.getCause()).getTargetException();
        if (targetException != null) {
          causedExceptionInfo.append("\n").append(targetException);
        }
      }
      throw new MontiCoreFreeMarkerException("0xA0561 Unable to execute template " + template.getName() + FM_FILE_EXTENSION + " : " + e.getLocalizedMessage() + 
          System.getProperty("line.separator") + "Exception-type: " + e.getCause() + causedExceptionInfo.toString() + 
          System.getProperty("line.separator") + "Caused by " + System.getProperty("line.separator") + e.getFTLInstructionStack(),
          e.getCause());
    }
    catch (IOException e) {
      throw new MontiCoreFreeMarkerException("0xA0563 Could read template " + template.getName() + e.getMessage() + FM_FILE_EXTENSION);
    }
    buffer.append(w.toString());
  }
  
}
