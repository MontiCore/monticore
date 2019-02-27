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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RefDefinitionOptionalMethodDecoratorStrategy extends OptionalMethodDecoratorStrategy {

  private String refSymbolType;
  private String refSymbolSimpleName;

  protected RefDefinitionOptionalMethodDecoratorStrategy(GlobalExtensionManagement glex, String refSymbolType, String refSymbolSimpleName) {
    super(glex);
    this.refSymbolType = refSymbolType;
    this.refSymbolSimpleName = refSymbolSimpleName;
  }

  @Override
  public List<ASTCDMethod> decorate(final ASTCDAttribute ast) {
    return new ArrayList<>(getGetter(ast));
  }

  @Override
  protected HookPoint createGetImplementation(final ASTCDAttribute ast) {
    return new TemplateHookPoint("ast_new.refSymbolMethods.Get", ast, refSymbolType);
  }

  @Override
  protected HookPoint createGetOptImplementation(final ASTCDAttribute ast) {
    String simpleAttributeName = ast.getName().endsWith("Definition") ? ast.getName().substring(0, ast.getName().lastIndexOf("Definition")) : ast.getName();
    return new TemplateHookPoint("ast_new.refSymbolMethods.GetDefinitionOpt", simpleAttributeName, refSymbolType, refSymbolSimpleName);
  }

  @Override
  protected HookPoint createIsPresentImplementation(final ASTCDAttribute ast) {
    return new TemplateHookPoint("ast_new.refSymbolMethods.IsPresent", ast.getName());
  }
}
