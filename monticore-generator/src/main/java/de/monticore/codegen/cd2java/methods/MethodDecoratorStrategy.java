package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.List;

public interface MethodDecoratorStrategy extends Decorator<ASTCDAttribute, List<ASTCDMethod>> {

  @Override
  List<ASTCDMethod> decorate(final ASTCDAttribute ast);
}
