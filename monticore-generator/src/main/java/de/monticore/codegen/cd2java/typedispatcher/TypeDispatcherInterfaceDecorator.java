/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.typedispatcher;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd.facade.CDMethodFacade;
import de.monticore.cd.facade.CDParameterFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterfaceBuilder;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.MCTypeFacade;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.facade.CDModifier.PUBLIC_ABSTRACT;
import static de.monticore.codegen.cd2java.typedispatcher.TypeDispatcherConstants.*;

public class TypeDispatcherInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final VisitorService visitorService;

  protected final SymbolTableService symbolTableService;

  public TypeDispatcherInterfaceDecorator(final GlobalExtensionManagement glex,
                                          final VisitorService visitorService,
                                          final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage visitorPackage = getPackage(input, decoratedCD, UTILS_PACKAGE);
    visitorPackage.addCDElement(decorate(input));
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    String interfaceName = getTypeDispatcherInterfaceName(visitorService.getCDName());
    ASTCDExtendUsage extendUsage = getInterfaceExtendUsage();

    List<ASTCDMethod> methods = new ArrayList<>();
    methods.addAll(methodsForAST());
    methods.addAll(methodsForSymbols());
    methods.addAll(methodsForScopes());

    ASTCDInterfaceBuilder builder = CD4CodeMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(interfaceName)
        .addAllCDMembers(methods);

    if (!extendUsage.isEmptySuperclass()) {
      builder.setCDExtendUsage(extendUsage);
    }

    return builder.build();
  }

  public List<ASTCDMethod> methodsForAST() {
    List<ASTCDMethod> methods = new ArrayList<>();

    for (CDTypeSymbol symbol: visitorService.getAllCDTypes()) {
      createMethods(
          methods, symbol.getName(), visitorService.createASTFullName(symbol),
          NODE_TYPE, NODE_PARAMETER);
    }

    return methods;
  }


  public List<ASTCDMethod> methodsForSymbols() {
    List<ASTCDMethod> methods = new ArrayList<>();

    for (String symbol: symbolTableService.retrieveSymbolNamesFromCD()) {
      createMethods(
          methods, symbolTableService.getSimpleNameFromSymbolName(symbol), symbol,
          SYMBOL_TYPE, SYMBOL_PARAMETER);
    }

    return methods;
  }

  public List<ASTCDMethod> methodsForScopes() {
    List<ASTCDMethod> methods = new ArrayList<>();

      createMethods(
          methods,
          symbolTableService.getScopeInterfaceSimpleName(),
          symbolTableService.getScopeInterfaceFullName(),
          SCOPE_TYPE, SCOPE_PARAMETER);

    createMethods(
        methods,
        symbolTableService.getGlobalScopeInterfaceSimpleName(),
        symbolTableService.getGlobalScopeInterfaceFullName(),
        SCOPE_TYPE, SCOPE_PARAMETER);

    createMethods(
        methods,
        symbolTableService.getArtifactScopeInterfaceSimpleName(),
        symbolTableService.getArtifactScopeInterfaceFullName(),
        SCOPE_TYPE, SCOPE_PARAMETER);

    return methods;
  }

  public void createMethods(List<ASTCDMethod> methods,
                            String name,
                            String fullName,
                            String parameterType,
                            String parameterName) {
    methods.add(CDMethodFacade.getInstance().createMethod(
        PUBLIC_ABSTRACT.build(),
        MCTypeFacade.getInstance().createBooleanType(),
        String.format("is%s", name),
        CDParameterFacade.getInstance().createParameter(parameterType, parameterName)));

    methods.add(CDMethodFacade.getInstance().createMethod(
        PUBLIC_ABSTRACT.build(),
        MCTypeFacade.getInstance().createQualifiedType(fullName),
        String.format("as%s", name),
        CDParameterFacade.getInstance().createParameter(parameterType, parameterName)));
  }

  public ASTCDExtendUsage getInterfaceExtendUsage() {
    MCTypeFacade typeFacade = MCTypeFacade.getInstance();
    List<ASTMCObjectType> superTypes = new ArrayList<>();

    for (DiagramSymbol symbol : visitorService.getSuperCDsDirect()) {
      superTypes.add(
          typeFacade.createQualifiedType(
              String.format("%s.%s",
                  symbol.getFullName().toLowerCase() + "." + UTILS_PACKAGE,
                  getTypeDispatcherInterfaceName(symbol.getName()))));
    }

    return CDExtendUsageFacade.getInstance().createCDExtendUsage(superTypes);
  }

  public String getTypeDispatcherInterfaceName(String name) {
    return String.format("I%s%s", name, TYPE_DISPATCHER_SUFFIX);
  }

}
