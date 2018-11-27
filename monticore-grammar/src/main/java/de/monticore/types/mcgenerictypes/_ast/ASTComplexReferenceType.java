package de.monticore.types.mcgenerictypes._ast;

import de.monticore.types.mcbasicgenericstypes._ast.ASTTypeArgument;
import de.monticore.types.mccustomgenericstypes._ast.ASTSimpleReferenceType;

import java.util.List;

public class ASTComplexReferenceType extends ASTComplexReferenceTypeTOP {
  
  public ASTComplexReferenceType() {
  }
  
  public ASTComplexReferenceType(
      List<ASTSimpleReferenceType> simpleReferenceTypes, List<String> names,
      List<ASTTypeArgument> typeArguments) {
    super(simpleReferenceTypes, names, typeArguments);
  }
  
  
  @Override public List<ASTTypeArgument> getArgumentList() {
    return getTypeArgumentList();
  }
  
  @Override public void setArgumentList(List<ASTTypeArgument> arguments) {
    setTypeArgumentList(arguments);
  }
}
