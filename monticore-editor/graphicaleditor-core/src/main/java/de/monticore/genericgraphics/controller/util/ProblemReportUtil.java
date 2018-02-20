/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.util;

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
