/* (c) https://github.com/MontiCore/monticore */

package de.monticore.generating.templateengine.reporting.commons;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.Marker;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.monticore.ast.ASTNode;
import de.monticore.io.paths.IterablePath;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.references.SymbolReference;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

/**
 * ReportingRepository holds all used formatted ASTNode strings. All string
 * representations for a ASTNode should be retrieved from this repository
 * (getASTNodeNameFormatted method).
 *
 * @author BM
 */
public class ReportingRepository {
  
  private IASTNodeIdentHelper astNodeIdentHelper;
  
  // save objects that have no position
  private Map<Object, Integer> node2Ident = Maps.newHashMap();
  
  private Map<Object, String> node2Name = Maps.newHashMap();
  
  // save nodes that have a position
  private Map<String, Integer> name2maxidSourcePos = Maps.newHashMap();
  
  private Map<Object, Integer> nodeWithSource2Ident = Maps.newHashMap();
  
  private Map<Object, String> nodeWithSource2Name = Maps.newHashMap();
  
  // save current maxID for aSTNode string
  private Map<String, Integer> name2maxid = Maps.newHashMap();
  
  private Set<String> allTemplateNames = Sets.newLinkedHashSet();
  
  private Set<String> allHWJavaNames = Sets.newLinkedHashSet();
  
  private Set<String> allHWTemplateNames = Sets.newLinkedHashSet();
    
  public ReportingRepository(IASTNodeIdentHelper astNodeIdentHelper) {
    this.astNodeIdentHelper = astNodeIdentHelper;
  }
  
  /**
   * Populates this repository with all resolved paths from the given iterable
   * path.
   * 
   * @param hwcPath
   * @see IterablePath#getResolvedPaths()
   * @see IterablePath#from(List, String)
   */
  public void initAllHWJava(IterablePath hwcPath) {
    Iterator<Path> hwcFiles = hwcPath.getResolvedPaths();
    while (hwcFiles.hasNext()) {
      allHWJavaNames.add(hwcFiles.next().toAbsolutePath().toString());
    }
  }
  
  /**
   * Populates this repository with all resolved paths from the given iterable
   * path.
   * 
   * @param hwtPath
   * @see IterablePath#getResolvedPaths()
   * @see IterablePath#from(List, String)
   */
  public void initAllHWTemplates(IterablePath hwtPath) {
    Iterator<Path> hwtFiles = hwtPath.get();
    while (hwtFiles.hasNext()) {
      allHWTemplateNames.add(hwtFiles.next().toFile().toString().replaceAll("\\\\", "/"));
    }
  }
  
  /**
   * Scans the current class path for templates and stores them in this
   * repository.
   */
  public void initAllTemplates() {
    // it's a kind of magic
    Reflections.log = new Helper();
    Reflections helper = new Reflections(new ConfigurationBuilder()
        .addClassLoader(ClasspathHelper.contextClassLoader())
        .setUrls(ClasspathHelper.forClassLoader())
        .setScanners(new ResourcesScanner()));
    
    this.allTemplateNames = helper.getResources(Pattern.compile(".*\\.ftl"));
  }
  
  private String getNameFormatted(Object obj, String out, SourcePosition sourcePos) {
    String pos = Layouter.sourcePos(sourcePos);
    // node has a source position
    if (!sourcePos.equals(
        SourcePosition.getDefaultSourcePosition())) {
      if (!nodeWithSource2Ident.containsKey(obj)) {
        // set output name for node
        nodeWithSource2Name.put(obj, out + pos);
        // name map has no identifier
        if (!name2maxidSourcePos.containsKey(out + pos)) {
          // init map
          MapUtil.incMapValue(name2maxidSourcePos, out + pos); // value is 1
        }
        nodeWithSource2Ident.put(obj, name2maxidSourcePos.get(out + pos));
        MapUtil.incMapValue(name2maxidSourcePos, out + pos); // increase current
                                                             // value
      }
      // do not print <...>!1!
      if (nodeWithSource2Ident.get(obj) != 1) {
        return nodeWithSource2Name.get(obj).replace(Layouter.END_TAG, "!" + nodeWithSource2Ident.get(obj) + Layouter.END_TAG);
      }
      // instead <<...>> if identifier is '1'
      else {
        return nodeWithSource2Name.get(obj);
      }
    }
    // first time this node
    if (!node2Ident.containsKey(obj)) {
      // set output name for node
      node2Name.put(obj, out);
      // name map has no identifier
      if (!name2maxid.containsKey(out)) {
        // init map
        MapUtil.incMapValue(name2maxid, out);
      }
      node2Ident.put(obj, name2maxid.get(out));
      MapUtil.incMapValue(name2maxid, out);
    }
    
    // do not print <<...>>!1!
    if (node2Ident.get(obj) != 1) {
      return node2Name.get(obj) + Layouter.START_TAG + "!" + node2Ident.get(obj) + Layouter.END_TAG;
    }
    // instead <<...>> if identifier is '1'
    else {
      return node2Name.get(obj);
    }
  }
  
  /**
   * Method that converts the ASTNode into a formatted string with a source
   * position if this is possible. The structure of the string is
   * @nodeName!nodeType(x,y) or @nodeName!nodeType(!ID).
   * 
   * @param ASTNode that should be converted into unique String
   * @return representation of the ASTNode that contains either the position or
   * a unique identification number for the object
   */
  public String getASTNodeNameFormatted(ASTNode a) {
    String out = astNodeIdentHelper.getIdent(a);
    return getNameFormatted(a, out, a.get_SourcePositionStart());
  }
  
  /**
   * Method that converts the Symbol into a formatted string with a source
   * position if this is possible. The structure of the string is
   * @symbolName!symbolType(x,y) or @symbolName!symbolType(!ID).
   * 
   * @param symbol The symbol that should be converted into unique String
   * @return representation of the ASTNode that contains either the position or
   * a unique identification number for the object
   */
  public String getSymbolNameFormatted(Symbol symbol) {
    String name = astNodeIdentHelper.getIdent(symbol);
    return getNameFormatted(symbol, name, symbol.getSourcePosition());
  }
  
  /**
   * Method that converts the SymbolReference into a formatted string with a source
   * position if this is possible. The structure of the string is
   * @symbolName!symbolType(x,y) or @symbolName!symbolType(!ID).
   * 
   * @param symbol The symbol that should be converted into unique String
   * @return representation of the ASTNode that contains either the position or
   * a unique identification number for the object
   */
  public String getSymbolNameFormatted(SymbolReference<?> symbolReference) {
    String name = astNodeIdentHelper.getIdent(symbolReference);
    return getNameFormatted(symbolReference, name, SourcePosition.getDefaultSourcePosition());
  }
  
  /**
   * Method that converts the Symbol into a formatted string with a source
   * position if this is possible. The structure of the string is
   * @symbolName!symbolType(x,y) or @symbolName!symbolType(!ID).
   * 
   * @param symbol The symbol that should be converted into unique String
   * @return representation of the ASTNode that contains either the position or
   * a unique identification number for the object
   */
  public String getScopeNameFormatted(Scope scope) {
    String name = astNodeIdentHelper.getIdent(scope);
    return getNameFormatted(scope, name, SourcePosition.getDefaultSourcePosition());
  }

  public Set<String> getAllTemplateNames() {
    return allTemplateNames;
  }
  
  public Set<String> getAllHWJavaNames() {
    return allHWJavaNames;
  }
  
  public Set<String> getAllHWTemplateNames() {
    return allHWTemplateNames;
  }
  
  /* This is the magic. Don't touch it ;-) */
  private class Helper implements Logger {
    
    @Override
    public boolean isTraceEnabled() {
      return Log.isTraceEnabled(ReportingRepository.class.getName());
    }
    
    @Override
    public void trace(String msg) {
      Log.trace(msg, ReportingRepository.class.getName());
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
      Log.trace(msg, t, ReportingRepository.class.getName());
    }
    
    @Override
    public boolean isDebugEnabled() {
      return Log.isDebugEnabled(ReportingRepository.class.getName());
    }
    
    @Override
    public void debug(String msg) {
      Log.debug(msg, ReportingRepository.class.getName());
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
      Log.debug(msg, t, ReportingRepository.class.getName());
    }
    
    @Override
    public boolean isInfoEnabled() {
      return this.isDebugEnabled();
    }
    
    @Override
    public void info(String msg) {
      this.debug(msg);
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
      Log.warn(msg);
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
      Log.warn(msg, t);
    }
    
    @Override
    public boolean isErrorEnabled() {
      return true;
    }
    
    @Override
    public void error(String msg) {
      Log.error(msg);
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
      Log.error(msg, t);
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
