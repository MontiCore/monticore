/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.freemarker;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;

import de.se_rwth.commons.logging.Log;
import freemarker.log.Logger;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Helps to process FreeMarker templates with MontiCore.
 * 
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class FreeMarkerTemplateEngine {
  
  public static final String FM_FILE_EXTENSION = ".ftl";
  private final Configuration configuration;
  
  public FreeMarkerTemplateEngine(Configuration configuration) {
    this.configuration = Log
        .errorIfNull(
            configuration,
            "0xA4048 Configuration must not be null in FreeMarkerTemplateEngine constructor.");
  }

  /**
   * Loads the template named qualifiedTemplateName from the class path.
   * 
   * @param qualifiedTemplateName full qualified template name EXCLUDING the
   *          file extension
   * @return the FreeMarker template or null, if the template is not available
   */
  public Template loadTemplate(String qualifiedTemplateName) {
    isNullOrEmpty(qualifiedTemplateName);
      
    // use empty logger to suppress default free marker log behaviour
    System.setProperty(Logger.SYSTEM_PROPERTY_NAME_LOGGER_LIBRARY, Logger.LIBRARY_NAME_NONE);
  
    Template result;
    try {
      result = configuration.getTemplate(qualifiedTemplateName);
    }
    catch (IOException e) {
      throw new MontiCoreFreeMarkerException("0xA0560 Unable to load template: " + e.getMessage());
    }
    return result;
  }
  
  /**
   * Runs the Template engine on the given template and data and writes the
   * result into the StringBuilder buffer
   * 
   * @param buffer contains the result
   * @param data data for the template
   * @param template the template file
   * @throws IOException
   */
  public void run(StringBuilder buffer, Object data, Template template) {
    Log.errorIfNull(template, "0xA0562 The given template must not be null");
    
    Writer w = new StringWriter();
    try {
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
      throw new MontiCoreFreeMarkerException("0xA0563 Could read template " + template.getName() + FM_FILE_EXTENSION);
    }
    buffer.append(w.toString());
  }
  
  /**
   * Loads the template first using the loadTemplate() method then runs the
   * template engine using the run() method.
   * 
   * @param qualifiedTemplateName full qualified template name EXCLUDING the
   *          file extension
   * @param buffer contains the result
   * @param data data for the template
   */
  public void loadAndRun(String qualifiedTemplateName, StringBuilder buffer, Object data) {
    Template t = loadTemplate(qualifiedTemplateName);
    run(buffer, data, t);
  }
}
