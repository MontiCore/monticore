package de.monticore.types.mccustomgenericstypes._ast;

import com.google.common.collect.Lists;
import de.monticore.types.mcbasicgenericstypes._ast.ASTTypeArgument;

import java.util.List;

public class ASTSimpleReferenceType extends ASTSimpleReferenceTypeTOP {
  
  public ASTSimpleReferenceType() {
  }
  
  public ASTSimpleReferenceType(List<String> names,
      List<ASTTypeArgument> typeArguments) {
    super(names, typeArguments);
  }
  
  @Override
  public List<ASTTypeArgument> getArgumentList() {
    return Lists.newArrayList(getTypeArgumentList());
  }
  
  @Override
  public void setArgumentList(List<ASTTypeArgument> arguments) {
    setTypeArgumentList(arguments);
  }
}
