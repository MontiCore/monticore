package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.methods.ListMethodDecoratorStrategy;
import de.monticore.codegen.cd2java.methods.MandatoryMethodDecoratorStrategy;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.ArrayList;
import java.util.List;

public class RefSymbolListMethodDecoratorStrategy extends ListMethodDecoratorStrategy {

  private String refSymbolType;
  private String refSymbolSimpleName;

  protected RefSymbolListMethodDecoratorStrategy(GlobalExtensionManagement glex, MandatoryMethodDecoratorStrategy mandatoryMethodDecoratorStrategy, String refSymbolType, String refSymbolSimpleName) {
    super(glex, mandatoryMethodDecoratorStrategy);
    this.refSymbolType = refSymbolType;
    this.refSymbolSimpleName = refSymbolSimpleName;
  }

  @Override
  protected List<ASTCDMethod> getMethodList() {
    ArrayList<ASTCDMethod> methods = new ArrayList<>(createGetter());
    methods.forEach(this::addImplementation);

    methods.add(createGetListMethod());
    return methods;
  }

  @Override
  protected HookPoint createImplementation(String attributeName, String methodName, String parameterCall, String returnType) {
    return new TemplateHookPoint("ast_new.refSymbolMethods.MethodDelegate", attributeName, methodName, parameterCall, returnType);
  }

}
