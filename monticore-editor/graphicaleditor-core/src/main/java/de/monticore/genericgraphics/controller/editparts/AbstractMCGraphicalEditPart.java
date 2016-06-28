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
package de.monticore.genericgraphics.controller.editparts;

import java.util.List;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

import de.monticore.genericgraphics.controller.util.ProblemReportUtil;
import de.se_rwth.commons.logging.Finding;


/**
 * <p>
 * This is an implementation of {@link IMCGraphicalEditPart} which extends
 * {@link AbstractGraphicalEditPart} and thus provides its functionality.
 * </p>
 * <p>
 * Furthermore the methods of the {@link IProblemReportHandler} interface are
 * implemented, by using the {@link ProblemReportUtil}.
 * </p>
 * 
 * @see ProblemReportUtil
 * @author Tim Enger
 */
public abstract class AbstractMCGraphicalEditPart extends AbstractGraphicalEditPart implements IMCGraphicalEditPart {
  
  /**
   * Constructor
   */
  public AbstractMCGraphicalEditPart() {
  }
  
  @Override
  protected void createEditPolicies() {
  }
  
  @Override
  public void setProblems(List<Finding> reports) {
    ProblemReportUtil.setProblems(reports, this);
  }
  
  @Override
  public void deleteAllProblems() {
    ProblemReportUtil.deleteAllProblems(this);
  }
  
  @Override
  public String getIdentifier() {
    return getModel().toString();
  }
  
}
