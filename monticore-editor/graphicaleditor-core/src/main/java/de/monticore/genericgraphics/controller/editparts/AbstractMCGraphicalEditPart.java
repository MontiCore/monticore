/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editparts;

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
