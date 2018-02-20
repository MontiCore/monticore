/* (c) https://github.com/MontiCore/monticore */

package de.monticore.emf._ast;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;

/**
 * An interface for all MontiCore model packages.
 *
 */
public interface ASTEPackage extends EPackage {
  
  /**
   *  Returns the list of packages for all extended models 
   */
  public List<ASTEPackage> getASTESuperPackages();
  
  public String getName();
  
  /**
   * Returns the package's name of the referenced model 
   */
  public String getPackageName();

}
