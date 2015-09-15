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

package de.monticore.generating.templateengine.reporting.reporter;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.monticore.ast.ASTNode;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.MapUtil;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class VariablesReporter extends AReporter {
  
  final static String SIMPLE_FILE_NAME = "07_Variables";
  
  private Map<String, Integer> var2asmt;
  
  private Map<String, Integer> var2adds;
  
  private List<String> templateCount;
  
  public VariablesReporter(String outputDir, String modelName) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR + File.separator
        + modelName,
        SIMPLE_FILE_NAME, ReportingConstants.REPORT_FILE_EXTENSION);
    templateCount = Lists.newArrayList();
    var2adds = Maps.newHashMap();
    var2asmt = Maps.newHashMap();
  }
  
  @Override
  protected void writeHeader() {
    writeLine("========================================================== Variables assigned");
    writeLine("#Asmt  #Adds  Variable name");
  }
  
  private void writeContent() {
    Set<String> allKeys = Sets.newHashSet();
    allKeys.addAll(var2adds.keySet());
    allKeys.addAll(var2asmt.keySet());
    for (String key : allKeys) {
      String adds = getNumber(var2adds, key);
      String asmts = getNumber(var2asmt, key);
      
      writeLine(asmts + Layouter.getSpaceString(7 - asmts.length()) + adds
          + Layouter.getSpaceString(7 - adds.length()) + key);
    }
  }
  
  private String getNumber(Map<String, Integer> map, String key) {
    if (map.get(key) != null) {
      return map.get(key) + "x";
    }
    else {
      return "0x";
    }
  }
  
  private void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("Variables assigned: list all variable names that got a value during the");
    writeLine("process.");
    writeLine("Each entry knows:");
    writeLine(" - #Asmt        how often has a value been assigned (setValue)");
    writeLine(" - #Adds        how often has the value been extended (addValue)");
    writeLine("(EOF)");
  }
  
  @Override
  public void reportSetValue(String name, Object value) {
    if (name != null) {
      // templateCount.add("NAME:   " + name + Layouter.getSpaceString(10 -
      // name.length())
      // + "VALUE:   " + value);
      MapUtil.incMapValue(var2asmt, name);
    }
  }
  
  @Override
  public void reportAddValue(String name, Object value, int size) {
    if (name != null) {
      MapUtil.incMapValue(var2adds, name);
    }
  }
  
  private void resetVariables() {
    templateCount.clear();
    var2adds.clear();
    var2asmt.clear();
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeContent();
    writeFooter();
    resetVariables();
    super.flush(ast);
  }
}
