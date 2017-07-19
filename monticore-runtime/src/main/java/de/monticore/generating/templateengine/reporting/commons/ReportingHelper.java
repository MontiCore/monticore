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

package de.monticore.generating.templateengine.reporting.commons;

import de.monticore.ast.ASTNode;
import de.se_rwth.commons.Names;

/**
 * Helper to write files
 * 
 * @author Timo Greifenberg
 */
public class ReportingHelper {
  
  /**
   * Removes all tabs and whitespaces. Transforms the string into a StringValue, beginning and
   * ending with quotes. If the length is lower than 7 and the formatted string does not fit in, the
   * output is "[...]".
   * 
   * @param toBeFormatted
   * @param length
   * @return
   */
  public static String formatStringToReportingString(String toBeFormatted,
      int length) {
    String replaced = toBeFormatted.replaceAll("\n", " ");
    replaced = replaced.replaceAll("\t", " ");
    replaced = replaced.replaceAll(" +", " ");
    if (length > 2 && replaced.length() < length - 2) {
      return "\"" + replaced + "\"";
    }
    else if (length > 7) {
      return "\"" + replaced.substring(0, length - 7) + "[...]\"";
    }
    else {
      return "\"[...]\"";
    }
  }
  
  /**
   * Removes all line breaks. Returns [...] if formatted string is longer than the parameter length.
   * 
   * @param toBeFormatted
   * @param length
   * @return
   */
  public static String formatLineToReportingLine(String toBeFormatted,
      int length) {
    String replaced = toBeFormatted.replaceAll("\n", " ");
    if (replaced.length() <= length) {
      return replaced;
    }
    else if (length > 5) {
      return replaced.substring(0, length - 5) + "[...]";
    }
    else {
      return "[...]";
    }
  }
  
  public static String getHookPointName(String hookName) {
    return "HP:\"" + hookName + "\"";
  }
  
  public static String getTemplateName(String hookName) {
    return Names.getSimpleName(hookName) + "."
        + ReportingConstants.TEMPLATE_FILE_EXTENSION;
  }
  
  public static int getASTDepth(ASTNode node) {
    return getASTDepthX(node);
  }
  
  private static int getASTDepthX(ASTNode node) {
    // node has no children
    if (node.get_Children() == null || node.get_Children().size() == 0) {
      return 0;
    }
    int maxDepth = -1; // default value
    for (ASTNode child : node.get_Children()) {
      int depthX = getASTDepthX(child) + 1;
      if (depthX > maxDepth) {
        maxDepth = depthX;
      }
    }
    return maxDepth;
  }
}
