/* (c) https://github.com/MontiCore/monticore */

package de.monticore.emf._ast;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;

/** A representation of the model object ASTENode **/
public interface ASTENodePackage extends ASTEPackage {
  // The package name.
  String eNAME = "ASTENode";
  
  // The package namespace URI.
  String eNS_URI = "http://ASTENode/1.0";
  
  // The package namespace name.
  String eNS_PREFIX = "ASTENode";
  
  // The singleton instance of the package.
  ASTENodePackage eINSTANCE = de.monticore.emf._ast.ASTENodePackageImpl.init();
  
  // The meta object id for the de.monticore.emf._ast.ConstantsASTENode
  int CONSTANTSASTENODE = 0;
  
  // The meta object id for the de.monticore.emf._ast.ENode
  int ENODE = 1;
  
  int ENODE_FEATURE_COUNT = 0;
  
  // Returns the meta object for enum ConstantsASTENode
  EEnum getConstantsASTENode();
  
  // Returns the meta object for class ENode
  EClass getENode();
  
  // Returns the factory that creates the instances of the model.
  ASTENodeFactory getASTENodeFactory();
  
  /**
   * Defines literals for the meta objects that represent each class, each
   * feature of each class, each enum, and each data type
   */
  interface Literals {
    EEnum CONSTANTSASTENODE = eINSTANCE.getConstantsASTENode();
    
    EClass ENODE = eINSTANCE.getENode();
    
  }
}
