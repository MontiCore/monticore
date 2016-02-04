/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.emf._ast;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public interface ASTEPackage extends EPackage {
  
  public List<ASTEPackage> getASTESuperPackages();
  
  public String getName();
  
  public String getPackageName();

}
