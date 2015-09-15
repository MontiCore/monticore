/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.generating.templateengine.reporting.commons;

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
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
  
  // save nodes that have no position
  private Map<ASTNode, Integer> node2Ident = Maps.newHashMap();
  
  private Map<ASTNode, String> node2Name = Maps.newHashMap();
  
  // save nodes that have a position
  private Map<String, Integer> name2maxidSourcePos = Maps.newHashMap();
  
  private Map<ASTNode, Integer> nodeWithSource2Ident = Maps.newHashMap();
  
  private Map<ASTNode, String> nodeWithSource2Name = Maps.newHashMap();
  
  // save current maxID for aSTNode string
  private Map<String, Integer> name2maxid = Maps.newHashMap();
  
  private Set<String> allTemplateNames = Sets.newHashSet();
  
  private Set<String> allHWJavaNames = Sets.newHashSet();
  
  private Set<String> allHWTemplateNames = Sets.newHashSet();
  
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
   * @deprecated use {@link #initAllHWTemplates(IterablePath)} instead
   */
  @Deprecated
  public void initAllHWTemplates(String... path) {
    Collection<String> dirs = Arrays.asList(path);
    for (String d : dirs) {
      File dir = new File(d);
      if (dir.isDirectory()) {
        resolveAllHWTemplateNames(dir);
      }
    }
  }
  
  /* Use IterablePath API as provided by MontiCore API */
  @Deprecated
  private void resolveAllHWTemplateNames(File dir) {
    if (dir.isDirectory()) {
      List<File> files = Arrays.asList(dir.listFiles());
      for (File f : files) {
        if (f.isFile()) {
          if (isTemplate(f)) {
            allHWTemplateNames.add(f.getName());
          }
        }
        if (f.isDirectory()) {
          resolveAllHWTemplateNames(f);
        }
      }
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
  
  /**
   * @deprecated use {@link #initAllTemplates()} instead; Freemarker loads
   * templates via class loader; as such it's rather senseless to search
   * directories for templates manually; {@link #initAllTemplates()} does what
   * is needed using the class loader thus mimicking what Freemarker sees
   */
  @Deprecated
  public void initAllTemplates(String... path) {
    Collection<String> dirs = Arrays.asList(path);
    for (String d : dirs) {
      File dir = new File(d);
      if (dir.isDirectory()) {
        resolveAllTemplateNames(dir);
      }
    }
  }
  
  /* see above */
  @Deprecated
  private void resolveAllTemplateNames(File dir) {
    if (dir.isDirectory()) {
      List<File> files = Arrays.asList(dir.listFiles());
      for (File f : files) {
        if (f.isFile()) {
          if (isTemplate(f)) {
            allTemplateNames.add(f.getName());
          }
        }
        if (f.isDirectory()) {
          resolveAllTemplateNames(f);
        }
      }
    }
  }
  
  /* no longer needed (see IterablePath) */
  @Deprecated
  private boolean isTemplate(File file) {
    if (file.getName().endsWith(".ftl")) {
      return true;
    }
    return false;
  }
  
  /**
   * Method that converts the ASTNode into a formatted string with a source
   * position if this is possible. The structure of the string is
   * <<nodeName:nodeType>><x,y> or <<nodeName:nodeType>> !ID!.
   * 
   * @param ASTNode that should be converted into unique String
   * @return representation of the ASTNode that contains either the position or
   * a unique identification number for the object
   */
  public String getASTNodeNameFormatted(ASTNode a) {
    String out = astNodeIdentHelper.getIdent(a);
    String pos = Layouter.sourcePos(a);
    // node has a source position
    if (!a.get_SourcePositionStart().equals(
        SourcePosition.getDefaultSourcePosition())) {
      if (!nodeWithSource2Ident.containsKey(a)) {
        // set output name for node
        nodeWithSource2Name.put(a, out + pos);
        // name map has no identifier
        if (!name2maxidSourcePos.containsKey(out + pos)) {
          // init map
          MapUtil.incMapValue(name2maxidSourcePos, out + pos); // value is 1
        }
        nodeWithSource2Ident.put(a, name2maxidSourcePos.get(out + pos));
        MapUtil.incMapValue(name2maxidSourcePos, out + pos); // increase current
                                                             // value
      }
      // do not print <<...>>!1!
      if (nodeWithSource2Ident.get(a) != 1) {
        return nodeWithSource2Name.get(a) + "!" + nodeWithSource2Ident.get(a) + "!";
      }
      // instead <<...>> if identifier is '1'
      else {
        return nodeWithSource2Name.get(a);
      }
    }
    // first time this node
    if (!node2Ident.containsKey(a)) {
      // set output name for node
      node2Name.put(a, out);
      // name map has no identifier
      if (!name2maxid.containsKey(out)) {
        // init map
        MapUtil.incMapValue(name2maxid, out);
      }
      node2Ident.put(a, name2maxid.get(out));
      MapUtil.incMapValue(name2maxid, out);
    }
    
    // do not print <<...>>!1!
    if (node2Ident.get(a) != 1) {
      return node2Name.get(a) + "!" + node2Ident.get(a) + "!";
    }
    // instead <<...>> if identifier is '1'
    else {
      return node2Name.get(a);
    }
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
