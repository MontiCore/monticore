/*******************************************************************************
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
 *******************************************************************************/
package de.monticore.genericgraphics.controller.editparts;

import java.util.List;

import org.eclipse.gef.EditPart;

import de.se_rwth.commons.logging.Finding;

/**
 * * Interface providing methods to handle errors in an {@link EditPart}.
 * 
 * @author Tim Enger
 */
public interface IProblemReportHandler {
  
  /**
   * Sets errors.
   * 
   * @param reports The list of {@link ProblemReport ProblemReports}.
   */
  public void setProblems(List<Finding> reports);
  
  /**
   * Deletes all set errors.
   */
  public void deleteAllProblems();
  
}
