package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

public class TypeCD2JavaDecorator implements Decorator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  private static final String AST_PACKAGE_SUFFIX = "._ast.";

  private static final String VISITOR_PACKAGE_SUFFIX = "._visitor.";

  private static final String SYMBOLTABLE_PACKAGE_SUFFIX = "._symboltable.";

  private static final String COCOS_PACKAGE_SUFFIX = "._cocos.";

  private static final String OD_PACKAGE_SUFFIX = "._od.";

  @Override
  public ASTCDCompilationUnit decorate(final ASTCDCompilationUnit compilationUnit) {
    //uses as default the ast package suffix
    return decorateAST(compilationUnit);
  }

  public ASTCDCompilationUnit decorateAST(final ASTCDCompilationUnit compilationUnit) {
    return decorate(AST_PACKAGE_SUFFIX, compilationUnit);
  }

  public ASTCDCompilationUnit decorateVisitor(final ASTCDCompilationUnit compilationUnit) {
    return decorate(VISITOR_PACKAGE_SUFFIX, compilationUnit);
  }

  public ASTCDCompilationUnit decorateSymboltable(final ASTCDCompilationUnit compilationUnit) {
    return decorate(SYMBOLTABLE_PACKAGE_SUFFIX, compilationUnit);
  }

  public ASTCDCompilationUnit decorateCocos(final ASTCDCompilationUnit compilationUnit) {
    return decorate(COCOS_PACKAGE_SUFFIX, compilationUnit);
  }

  public ASTCDCompilationUnit decorateOd(final ASTCDCompilationUnit compilationUnit) {
    return decorate(OD_PACKAGE_SUFFIX, compilationUnit);
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
