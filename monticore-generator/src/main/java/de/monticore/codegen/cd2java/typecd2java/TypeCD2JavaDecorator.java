package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

public class TypeCD2JavaDecorator extends AbstractDecorator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  private static final String AST_PACKAGE_SUFFIX = "._ast.";

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit compilationUnit) {
    //uses as default the ast package suffix
    return decorate(AST_PACKAGE_SUFFIX, compilationUnit);
  }

  private ASTCDCompilationUnit decorate(String packageSuffix, final ASTCDCompilationUnit compilationUnit) {
    TypeCD2JavaVisitor visitor = new TypeCD2JavaVisitor(packageSuffix, compilationUnit);
    visitor.handle(compilationUnit);
    return compilationUnit;
  }
}
