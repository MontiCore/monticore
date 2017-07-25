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
package de.monticore.genericgraphics.controller.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.swt.graphics.Image;

import de.monticore.genericgraphics.controller.editparts.IMCGraphicalEditPart;
import de.monticore.genericgraphics.view.decorations.IDecoratorDisplay;
import de.monticore.genericgraphics.view.icons.ProblemReportIconProvider;
import de.se_rwth.commons.logging.Finding;


/**
 * A utility class that can be used by {@link IMCGraphicalEditPart
 * IMCGraphicalEditParts} to set {@link ProblemReport ProblemReports}.
 * 
 * @author Tim Enger
 */
public class ProblemReportUtil {
  
  /**
   * <p>
   * Sets all {@link ProblemReport ProblemReports} in a
   * {@link IMCGraphicalEditPart}.
   * </p>
   * <p>
   * Therefore, the method checks if the {@link IFigure} of the EditPart is an
   * implementation of the {@link IDecoratorDisplay}. If so, the
   * {@link IDecoratorDisplay#setDecorator(Image, List)} method is used to set
   * the {@link ProblemReport ProblemReports}.<br>
   * <br>
   * The {@link Image icon} used to indicate the error is provided by the
   * {@link ProblemReportIconProvider} class.
   * </p>
   * 
   * @param reports The list of {@link ProblemReport ProblemReports} to set.
   * @param ep The {@link IMCGraphicalEditPart}.
   */
  public static void setProblems(List<Finding> reports, IMCGraphicalEditPart ep) {
    ep.deleteAllProblems();
    if (reports == null || reports.isEmpty()) {
      return;
    }
    
    // if the figure of this editpart is able to display decorators
    // use this functionality to display errors
    if (ep.getFigure() instanceof IDecoratorDisplay) {
      IDecoratorDisplay fig = (IDecoratorDisplay) ep.getFigure();
      
      List<String> messages = new ArrayList<String>();
      for (Finding report : reports) {
        messages.add(report.getMsg());
      }
      
      Image icon = ProblemReportIconProvider.getIconForProblemReportType(reports.get(0).getType());
      fig.setDecorator(icon, messages);
    }
  }
  
  /**
   * <p>
   * Deletes all set {@link ProblemReport ProblemReports} in a
   * {@link IMCGraphicalEditPart}.
   * </p>
   * <p>
   * Therefore, the method checks if the {@link IFigure} of the EditPart is an
   * implementation of the {@link IDecoratorDisplay}. If so, the
   * {@link IDecoratorDisplay#deleteDecorator()} method is used to delete the
   * set {@link ProblemReport ProblemReports}.<br>
   * 
   * @param ep The {@link IMCGraphicalEditPart}.
   */
  public static void deleteAllProblems(IMCGraphicalEditPart ep) {
    if (ep.getFigure() instanceof IDecoratorDisplay) {
      IDecoratorDisplay fig = (IDecoratorDisplay) ep.getFigure();
      fig.deleteDecorator();
    }
  }
  
}
