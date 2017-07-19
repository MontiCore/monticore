/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
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
import java.nio.file.Path;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Maps;

import de.monticore.ast.ASTNode;
import de.monticore.generating.templateengine.reporting.commons.AReporter;
import de.monticore.generating.templateengine.reporting.commons.Layouter;
import de.monticore.generating.templateengine.reporting.commons.MapUtil;
import de.monticore.generating.templateengine.reporting.commons.ReportingConstants;
import de.monticore.generating.templateengine.reporting.commons.ReportingRepository;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @since TODO: add version number
 */
public class HandWrittenCodeReporter extends AReporter {
  
  final static String USED_HWC_FILES = "Used Handwritten Code Files";
  
  final static String UNUSED_HWC_FILES = "Unused Handwritten Code Files";
  
  final static String SIMPLE_FILE_NAME = "03_HandwrittenCodeFiles";
  
  private ReportingRepository repo;
  
  private Map<String, Integer> usedFileNames = Maps.newHashMap();
  
  public HandWrittenCodeReporter(String outputDir, String modelName, ReportingRepository repo) {
    super(outputDir + File.separator + ReportingConstants.REPORTING_DIR
        + File.separator + modelName, SIMPLE_FILE_NAME,
        ReportingConstants.REPORT_FILE_EXTENSION);
    this.repo = repo;
  }
  
  /**
   * @see mc.codegen.reporting.commons.DefaultReportEventHandler#reportUseHandwrittenCodeFile(java.lang.String)
   */
  @Override
  public void reportUseHandwrittenCodeFile(Path parentDir, Path fileName) {
    if (parentDir != null) {
      MapUtil.incMapValue(usedFileNames, parentDir.toString());
    }
  }
  
  private void resetVariables() {
    usedFileNames.clear();
  }
  
  private void writeContent() {
    Set<String> unusedFiles = repo.getAllHWJavaNames();
    unusedFiles.removeAll(usedFileNames.keySet());
    
    for (Entry<String, Integer> e : usedFileNames.entrySet()) {
      String count = e.getValue() + "x";
      writeLine(count + Layouter.getSpaceString(10 - count.length())
          + e.getKey());
    }
    
    writeLine("========================================================== "
        + UNUSED_HWC_FILES);
    for (String f : unusedFiles) {
      writeLine(Layouter.getSpaceString(10) + f);
    }
  }
  
  @Override
  protected void writeHeader() {
    writeLine("========================================================== "
        + USED_HWC_FILES);
    writeLine("#Counts" + Layouter.getSpaceString(3) + "File Name");
  }
  
  private void writeFooter() {
    writeLine("========================================================== Explanation");
    writeLine("   - Shows used and unused HWC files");
    writeLine("(EOF)");
  }
  
  @Override
  public void flush(ASTNode ast) {
    writeContent();
    writeFooter();
    resetVariables();
    super.flush(ast);
  }
  
}
