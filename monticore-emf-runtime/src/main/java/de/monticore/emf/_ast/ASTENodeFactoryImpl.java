package de.monticore.emf._ast;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

public class ASTENodeFactoryImpl extends EFactoryImpl implements ASTENodeFactory {
  
  // Creates the default factory implementation.
  public static ASTENodeFactory init() {
    try {
      ASTENodeFactory theASTENodeFactory = (ASTENodeFactory) EPackage.Registry.INSTANCE
          .getEFactory(ASTENodePackage.eNS_URI);
      if (theASTENodeFactory != null) {
        return theASTENodeFactory;
      }
    }
    catch (Exception exception) {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new ASTENodeFactoryImpl();
  }
  
  @Override
  public EObject create(EClass eClass) {
    switch (eClass.getClassifierID()) {
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName()
            + "' is not a valid classifier");
    }
  }
  
  @Override
  public Object createFromString(EDataType eDataType, String initialValue) {
    switch (eDataType.getClassifierID()) {

      case ASTENodePackage.CONSTANTSASTENODE:
        return createConstantsASTENodeFromString(eDataType, initialValue);
        
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName()
            + "' is not a valid classifier");
    }
  }
  
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue) {
    switch (eDataType.getClassifierID()) {

      case ASTENodePackage.CONSTANTSASTENODE:
        return convertConstantsASTENodeToString(eDataType, instanceValue);
        
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName()
            + "' is not a valid classifier");
    }
  }
  
  public ASTENodePackage getASTENodePackage() {
    return (ASTENodePackage) getEPackage();
  }
  
  public ConstantsASTENode createConstantsASTENodeFromString(EDataType eDataType,
      String initialValue) {
    ConstantsASTENode result = ConstantsASTENode.get(initialValue);
    if (result == null)
      throw new IllegalArgumentException("The value '" + initialValue
          + "' is not a valid enumerator of '" + eDataType.getName() + "'");
    return result;
  }
  
  public String convertConstantsASTENodeToString(EDataType eDataType, Object instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }
  
  // TODO create methods for eDatatypes createEtypeFromString and
  // convertEtypeToString
}
