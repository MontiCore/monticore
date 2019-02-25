package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.methods.OptionalMethodDecoratorStrategy;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.Arrays;
import java.util.List;

public class RefSymbolOptionalMethodDecoratorStrategy extends OptionalMethodDecoratorStrategy {

  private ASTType refSymbolType;

  protected RefSymbolOptionalMethodDecoratorStrategy(GlobalExtensionManagement glex, ASTType refSymbolType) {
    super(glex);
    this.refSymbolType = refSymbolType;
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    ASTCDMethod get = createGetMethod(ast);
    ASTCDMethod getOpt = createGetOptMethod(ast);
    ASTCDMethod isPresent = createIsPresentMethod(ast);
    return Arrays.asList(get, getOpt, isPresent);
  }

  @Override
  protected HookPoint createGetImplementation(final ASTCDAttribute ast) {
    return new TemplateHookPoint("ast_new.refSymbolMethods.Get", ast, TypesPrinter.printType(refSymbolType));
  }

  @Override
  protected HookPoint createGetOptImplementation(final ASTCDAttribute ast) {
    return new TemplateHookPoint("ast_new.refSymbolMethods.GetSymbolOpt", ast.getName(), TypesPrinter.printType(refSymbolType), GeneratorHelper.isOptional(ast));
  }

  @Override
  protected HookPoint createIsPresentImplementation(final ASTCDAttribute ast) {
    return new TemplateHookPoint("ast_new.refSymbolMethods.IsPresent", ast.getName());
  }
}
