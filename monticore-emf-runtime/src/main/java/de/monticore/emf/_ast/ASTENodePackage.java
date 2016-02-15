package de.monticore.emf._ast;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;

public interface ASTENodePackage extends ASTEPackage {
  // The package name.
  String eNAME = "ASTENode";
  
  // The package namespace URI.
  String eNS_URI = "http://ASTENode/1.0";
  
  // The package namespace name.
  String eNS_PREFIX = "ASTENode";
  
  // The singleton instance of the package.
  ASTENodePackage eINSTANCE = de.monticore.emf._ast.ASTENodePackageImpl.init();
  
  /* generated by
   * mc.codegen.emf.epackages.epackagemethods.EPackageIDCalculationMain */
  // The meta object id for the de.monticore.emf._ast.ConstantsASTENode
  int CONSTANTSASTENODE = 0;
  
  // The meta object id for the de.monticore.emf._ast.ENode
  int ENODE = 1;
  
  /* generated by
   * mc.codegen.emf.epackages.epackagemethods.EPackageFeatureIDCalculationFile */
  int ENODE_FEATURE_COUNT = 0;
  
  /* generated by
   * mc.codegen.emf.epackages.epackagemethods.EPackageMetaObjectGetMethodsMain */
  // Returns the meta object for enum ConstantsASTENode
  EEnum getConstantsASTENode();
  
  /* generated by
   * mc.codegen.emf.epackages.epackagemethods.EPackageMetaObjectGetMethodsMain */
  // Returns the meta object for class ENode
  EClass getENode();
  
  // Returns the factory that creates the instances of the model.
  ASTENodeFactory getASTENodeFactory();
  
  /**
   * <!-- begin-user-doc --> Defines literals for the meta objects that
   * represent
   * <ul>
   * <li>each class,</li>
   * <li>each feature of each class,</li>
   * <li>each enum,</li>
   * <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
   */
  interface Literals {
    /* generated by mc.codegen.emf.epackages.epackagemethods.EPackageLiteralMain */
    EEnum CONSTANTSASTENODE = eINSTANCE.getConstantsASTENode();
    
    /* generated by mc.codegen.emf.epackages.epackagemethods.EPackageLiteralMain */
    EClass ENODE = eINSTANCE.getENode();
    
  }
}
