/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
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

package de.monticore.generating.templateengine.reporting.artifacts.formatter;

import java.util.List;

import de.monticore.generating.templateengine.reporting.artifacts.model.RootPkg;

public abstract class AFormatter {
  
  private String indentString = "";
  
  protected void indent() {
    indentString += "  ";
  }
  
  protected void unindent() {
    indentString = indentString.substring(0, indentString.length() - 2);
  }
  
  protected void addLine(List<String> lines, String line) {
    lines.add(indentString + line);
  }
  
  public abstract List<String> getContent(RootPkg rootPkg);
  
}
