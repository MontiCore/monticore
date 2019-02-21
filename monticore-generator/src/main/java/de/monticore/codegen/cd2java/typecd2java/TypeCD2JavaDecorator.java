package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

public class TypeCD2JavaDecorator implements Decorator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  private static final String AST_PACKAGE_SUFFIX = "._ast.";

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit compilationUnit) {
    //uses as default the ast package suffix
    return decorate(AST_PACKAGE_SUFFIX, compilationUnit);
  }

  private ASTCDCompilationUnit decorate(String packageSuffix, final ASTCDCompilationUnit compilationUnit) {
    //make copy from compilationunit so that the actual one is not changed
    ASTCDCompilationUnit copy = compilationUnit.deepClone();
    copy.setEnclosingScope(compilationUnit.getEnclosingScope());
    TypeCD2JavaVisitor visitor = new TypeCD2JavaVisitor(packageSuffix, copy);
    visitor.handle(copy);
    return copy;
  }
}
