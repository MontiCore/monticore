// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */
package de.monticore;

import com.google.common.collect.Sets;
import de.monticore.generating.templateengine.reporting.commons.IASTNodeIdentHelper;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.Marker;

import java.util.Set;
import java.util.regex.Pattern;

@Deprecated
public class ReportingRepositoryFix extends ReportingRepository {

  private Set<String> allTemplateNames = Sets.newLinkedHashSet();


  @Override
  public void initAllTemplates() {
    // it's a kind of magic
    Reflections.log = new Helper();
    Reflections helper = new Reflections(new ConfigurationBuilder()
            .addClassLoader(ClasspathHelper.contextClassLoader())
            .addUrls(ClasspathHelper.forClassLoader())
            .addUrls(ClasspathHelper.forPackage(""))
            .setScanners(new ResourcesScanner()));

    this.allTemplateNames = helper.getResources(Pattern.compile(".*\\.ftl"));
  }

  public ReportingRepositoryFix(IASTNodeIdentHelper astNodeIdentHelper) {
    super(astNodeIdentHelper);
  }

  public Set<String> getAllTemplateNames() {
    return allTemplateNames;
  }

  /* This is the magic. Don't touch it ;-) */
  private class Helper implements Logger {

    @Override
    public boolean isTraceEnabled() {
      return false;
      // return Log.isTraceEnabled(ReportingRepository.class.getName());
    }

    @Override
    public void trace(String msg) {
      if (isTraceEnabled())
        System.out.println("[TRACE] " + msg);
    }

    @Override
    public void trace(String format, Object arg) {
      this.trace(String.format(format, new Object[] { arg }));
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
      this.trace(String.format(format, new Object[] { arg1, arg2 }));
    }

    @Override
    public void trace(String format, Object... arguments) {
      this.trace(String.format(format, arguments));
    }

    @Override
    public void trace(String msg, Throwable t) {
      this.trace(msg + "\n" + t.toString(), ReportingRepository.class.getName());
    }

    @Override
    public boolean isDebugEnabled() {
      return false;
      // return Log.isDebugEnabled(ReportingRepository.class.getName());
    }

    @Override
    public void debug(String msg) {
      if (isDebugEnabled())
        System.out.println("[DEBUG] " + msg);
    }

    @Override
    public void debug(String format, Object arg) {
      this.debug(String.format(format, new Object[] { arg }));
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
      this.debug(String.format(format, new Object[] { arg1, arg2 }));
    }

    @Override
    public void debug(String format, Object... arguments) {
      this.debug(String.format(format, arguments));
    }

    @Override
    public void debug(String msg, Throwable t) {
      this.debug(msg + "\n" + t.toString(), ReportingRepository.class.getName());
    }

    @Override
    public boolean isInfoEnabled() {
      return this.isDebugEnabled();
    }

    @Override
    public void info(String msg) {
      if (isInfoEnabled())
        System.out.println("[INFO] " + msg);
    }

    @Override
    public void info(String format, Object arg) {
      this.debug(format, arg);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
      this.debug(format, arg1, arg2);
    }

    @Override
    public void info(String format, Object... arguments) {
      this.debug(format, arguments);
    }

    @Override
    public void info(String msg, Throwable t) {
      this.debug(msg, t);
    }

    @Override
    public boolean isWarnEnabled() {
      return true;
    }

    @Override
    public void warn(String msg) {
      if (isWarnEnabled())
        System.err.println("[WARNING] " + msg);
    }

    @Override
    public void warn(String format, Object arg) {
      this.warn(String.format(format, new Object[] { arg }));
    }

    @Override
    public void warn(String format, Object... arguments) {
      this.warn(String.format(format, arguments));
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
      this.warn(String.format(format, new Object[] { arg1, arg2 }));
    }

    @Override
    public void warn(String msg, Throwable t) {
      this.warn(msg + "\n" + t.toString());
    }

    @Override
    public boolean isErrorEnabled() {
      return true;
    }

    @Override
    public void error(String msg) {
      if (isErrorEnabled())
        System.err.println("[ERROR] " + msg);
    }

    @Override
    public void error(String format, Object arg) {
      this.error(String.format(format, new Object[] { arg }));
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
      this.error(String.format(format, new Object[] { arg1, arg2 }));
    }

    @Override
    public void error(String format, Object... arguments) {
      this.error(String.format(format, arguments));
    }

    @Override
    public void error(String msg, Throwable t) {
      this.error(msg + "\n" + t.toString());
    }

    @Override
    public String getName() {
      return ReportingRepository.class.getName();
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
      return this.isTraceEnabled();
    }

    @Override
    public void trace(Marker marker, String msg) {
      this.trace(msg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
      this.trace(format, arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
      this.trace(format, arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... argArray) {
      this.trace(format, argArray);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
      this.trace(msg, t);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
      return this.isDebugEnabled();
    }

    @Override
    public void debug(Marker marker, String msg) {
      this.debug(msg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
      this.debug(format, arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
      this.debug(format, arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
      this.debug(format, arguments);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
      this.debug(msg, t);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
      return this.isInfoEnabled();
    }

    @Override
    public void info(Marker marker, String msg) {
      this.info(msg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
      this.info(format, arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
      this.info(format, arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
      this.info(format, arguments);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
      this.info(msg, t);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
      return this.isWarnEnabled();
    }

    @Override
    public void warn(Marker marker, String msg) {
      this.warn(msg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
      this.warn(format, arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
      this.warn(format, arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
      this.warn(format, arguments);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
      this.warn(msg, t);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
      return this.isErrorEnabled();
    }

    @Override
    public void error(Marker marker, String msg) {
      this.error(msg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
      this.error(format, arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
      this.error(format, arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
      this.error(format, arguments);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
      this.error(msg, t);
    }

  }

}
