/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editparts;

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
