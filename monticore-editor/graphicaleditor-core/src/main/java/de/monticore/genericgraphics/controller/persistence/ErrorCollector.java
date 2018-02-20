/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.persistence;

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
