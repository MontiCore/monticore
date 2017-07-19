/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, 2016, MontiCore, All rights reserved.
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
 *******************************************************************************/
package de.monticore.genericgraphics.controller.persistence;

import java.util.ArrayList;
import java.util.List;

import de.se_rwth.commons.logging.Finding;

/**
 * Simple Error Collector for collection of {@link ProblemReport ProblemReports}
 * during parsing of a model file by MontiCore
 * 
 * @author Tim Enger
 */
public class ErrorCollector {
  
  private List<Finding> reports;
  
  /**
   * Constructor
   */
  public ErrorCollector() {
    reports = new ArrayList<Finding>();
  }


  public List<Finding> getReports() {
    return reports;
  }
  
  public void add(Finding r) {
    reports.add(r);
  }

  
}
