package de.monticore.codegen.cd2java.methods;

import de.monticore.codegen.cd2java.Generator;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.List;

public interface MethodGeneratorStrategy extends Generator<ASTCDAttribute, List<ASTCDMethod>> {

  @Override
  List<ASTCDMethod> generate(final ASTCDAttribute ast);
}
