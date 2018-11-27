package de.monticore.types.mcgenerictypes._ast;

import de.monticore.types.mcbasicgenericstypes._ast.ASTTypeArgument;
import de.monticore.types.mccustomgenericstypes._ast.ASTSimpleReferenceType;

import java.util.List;

public class ASTComplexReferenceType extends ASTComplexReferenceTypeTOP {
  
  public ASTComplexReferenceType() {
  }
  
  public ASTComplexReferenceType(
      List<ASTSimpleReferenceType> simpleReferenceTypes) {
    super(simpleReferenceTypes);
  }
  
  @Override public List<String> getNameList() {
    return null;
  }
  
  @Override public void setNameList(List<String> names) {
  
  }
  
  @Override public List<ASTTypeArgument> getArgumentList() {
    return null;
  }
  
  @Override public void setArgumentList(List<ASTTypeArgument> arguments) {
//    setTypeArgumentList(arguments);
  }
}
