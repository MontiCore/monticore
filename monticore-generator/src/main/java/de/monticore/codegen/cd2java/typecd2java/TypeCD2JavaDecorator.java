package de.monticore.codegen.cd2java.typecd2java;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

public class TypeCD2JavaDecorator implements Decorator<ASTCDCompilationUnit, ASTCDCompilationUnit> {

  private static final String AST_PACKAGE_SUFFIX = "_ast";

  private static final String VISITOR_PACKAGE_SUFFIX= "_visitor";

  private static final String SYMBOLTABLE_PACKAGE_SUFFIX= "_symboltable";

  private static final String COCOS_PACKAGE_SUFFIX= "_cocos";

  private static final String OD_PACKAGE_SUFFIX= "_od";


  @Override
  public ASTCDCompilationUnit decorate(ASTCDCompilationUnit compilationUnit) {
    return decorateAST(compilationUnit);
  }

  public ASTCDCompilationUnit decorateAST(ASTCDCompilationUnit compilationUnit) {
    TypeCD2JavaVisitor visitor = new TypeCD2JavaVisitor(AST_PACKAGE_SUFFIX, compilationUnit);
    visitor.handle(compilationUnit);
    return compilationUnit;
  }

  public ASTCDCompilationUnit decorateVisitor(ASTCDCompilationUnit compilationUnit) {
    TypeCD2JavaVisitor visitor = new TypeCD2JavaVisitor(VISITOR_PACKAGE_SUFFIX, compilationUnit);
    visitor.handle(compilationUnit);
    return compilationUnit;
  }

  public ASTCDCompilationUnit decorateSymboltable(ASTCDCompilationUnit compilationUnit) {
    TypeCD2JavaVisitor visitor = new TypeCD2JavaVisitor(SYMBOLTABLE_PACKAGE_SUFFIX, compilationUnit);
    visitor.handle(compilationUnit);
    return compilationUnit;
  }

  public ASTCDCompilationUnit decorateCocos(ASTCDCompilationUnit compilationUnit) {
    TypeCD2JavaVisitor visitor = new TypeCD2JavaVisitor(COCOS_PACKAGE_SUFFIX, compilationUnit);
    visitor.handle(compilationUnit);
    return compilationUnit;
  }

  public ASTCDCompilationUnit decorateOd(ASTCDCompilationUnit compilationUnit) {
    TypeCD2JavaVisitor visitor = new TypeCD2JavaVisitor(OD_PACKAGE_SUFFIX, compilationUnit);
    visitor.handle(compilationUnit);
    return compilationUnit;
  }


}
