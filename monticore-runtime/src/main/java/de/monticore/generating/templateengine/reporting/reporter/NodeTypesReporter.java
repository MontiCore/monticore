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
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.SourcePosition;

import com.google.common.collect.Maps;

import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.MapUtil;
import de.monticore.generating.templateengine.reporting.commons.ObjectCountVisitor;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class NodeTypesReporter extends AReporter {
  
  final static String SIMPLE_FILE_NAME = "12_TypesOfNodes";
  
  private ReportingRepository repository;
  
  private Map<String, Integer> nodeTypeCount = Maps.newTreeMap();
  
  private Map<String, Integer> nodeTypeCountPos = Maps.newTreeMap();
  
  public NodeTypesReporter(String outputDir, String modelName,
      ReportingRepository repository) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);
    this.repository = repository;
  }
  
  @Override
  protected void writeHeader() {
    writeLine("======================================== Types of Nodes (all)");
    writeLine("#Objects  #Visits  Nonterminal-Name");
  }
  
  public void writeContent(ASTNode ast) {
    if (ast == null) {
      return;
    }
    
    ObjectCountVisitor ocv = new ObjectCountVisitor();
    ocv.handle(ast);
    Map<String, Integer> type2count = ocv.getObjectCountMap();
    
    writeMaps(nodeTypeCount, type2count);
    
    writeLine("======================================== Types of Nodes (With Source Pos)");
    writeLine("#Objects  #Visits  Nonterminal-Name");
    Map<String, Integer> type2countPos = ocv.getObjectCountMapPos();
    
    writeMaps(nodeTypeCountPos, type2countPos);
    
    writeLine("======================================== Types of Nodes (No Source Pos)");
    writeLine("#Objects  #Visits  Nonterminal-Name");
    
    Map<String, Integer> nodeTypeCountNoPos = getMapDiff(nodeTypeCount, nodeTypeCountPos);
    Map<String, Integer> type2countNoPos = getMapDiff(type2count, type2countPos);
    
    writeMaps(nodeTypeCountNoPos, type2countNoPos);
  }
  
  /**
   * @param nodeTypeCount2: Map contains all ASTNodes
   * @param nodetypeCountPos2: Map contains ASTNodes with a source position
   * @return Map containing ASTNodes without source position
   */
  private Map<String, Integer> getMapDiff(Map<String, Integer> nodeTypeCount2,
      Map<String, Integer> nodetypeCountPos2) {
    Map<String, Integer> dif = Maps.newTreeMap();
    // merging keys of objects and visits
    Set<String> allKeys = new TreeSet<String>();
    allKeys.addAll(nodetypeCountPos2.keySet());
    allKeys.addAll(nodeTypeCount2.keySet());
    for (String key : allKeys) {
      int val1, val2;
      if (nodeTypeCount2.containsKey(key)) {
        val1 = nodeTypeCount2.get(key);
      }
      else {
        val1 = 0;
      }
      if (nodetypeCountPos2.containsKey(key)) {
        val2 = nodetypeCountPos2.get(key);
      }
      else {
        val2 = 0;
      }
      dif.put(key, val1 - val2);
    }
    return dif;
  }
  
  /**
   * helper method: print these 2 maps the same way
   * 
   * @param nodeTypeCount2
   * @param type2count
   */
  private void writeMaps(Map<String, Integer> nodeTypeCount2, Map<String, Integer> type2count) {
    // merging keys of objects and visits
    Set<String> allKeys = new TreeSet<String>();
    allKeys.addAll(type2count.keySet());
    allKeys.addAll(nodeTypeCount2.keySet());
    
    for (String key : allKeys) {
      String objectCount, s;
      if (!type2count.containsKey(key)) {
        objectCount = "0x";
      }
      else {
        objectCount = type2count.get(key) + "x";
      }
      if (!nodeTypeCount2.containsKey(key)) {
        s = "0x";
      }
      else {
        s = nodeTypeCount2.get(key) + "x";
      }
      // evading 0x objects 0x visits line
      if (!(s.equals("0x") && objectCount.equals("0x"))) {
        writeLine(objectCount
            + Layouter.getSpaceString(10 - objectCount.length()) + s
            + Layouter.getSpaceString(9 - s.length()) + key);
      }
    }
    
  }
  
  private void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("Types of Nodes: Shows a List of all AST-Node-Types that occur in the final AST.");
    writeLine("Types of Nodes (with Source Position): Shows the subset of all AST Node Types");
    writeLine("which occurred in the initial AST after the parsing step.");
    writeLine("Types of Nodes (without Source Position): Shows the subset of all AST Node Types");
    writeLine("which have been added to the initial AST after the parsing step (e.g. by model");
    writeLine("transformation).");
    writeLine("For each AST type entry in the lists the following information is reported: ");
    writeLine(" - #Objects:  number of its instances of the corresponding AST type");
    writeLine(" - #Visits:   how often nodes of that type have been visited by a");
    writeLine(" call/includeTemplate");
    writeLine("(EOF)");
  }
  
  @Override
  public void reportTemplateStart(String templatename, ASTNode ast) {
    String key = Layouter.nodeName(ast);
    MapUtil.incMapValue(nodeTypeCount, key);
    if (!ast.get_SourcePositionStart().equals(SourcePosition.getDefaultSourcePosition())) {
      MapUtil.incMapValue(nodeTypeCountPos, key);
    }
  }
 
  @Override
  public void flush(ASTNode ast) {
    writeContent(ast);
    writeFooter();
    nodeTypeCount.clear();
    super.flush(ast);
  }
}
