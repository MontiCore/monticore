/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.interpreter;

import de.monticore.cd.facade.CDExtendUsageFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDExtendUsage;
import de.monticore.cdbasis._ast.ASTCDPackage;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterface;
import de.monticore.cdinterfaceandenum._ast.ASTCDInterfaceBuilder;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorConstants;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.*;

public class InterpreterContextInterfaceDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDInterface> {

  protected final VisitorService visitorService;

  protected final SymbolTableService symbolTableService;

  public InterpreterContextInterfaceDecorator(final GlobalExtensionManagement glex,
                                              final VisitorService visitorService,
                                              final SymbolTableService symbolTableService) {
    super(glex);
    this.visitorService = visitorService;
    this.symbolTableService = symbolTableService;
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage visitorPackage = getPackage(input, decoratedCD, VisitorConstants.VISITOR_PACKAGE);
    visitorPackage.addCDElement(decorate(input));
  }

  @Override
  public ASTCDInterface decorate(ASTCDCompilationUnit input) {
    ASTCDInterfaceBuilder builder = CD4CodeMill.cDInterfaceBuilder()
        .setModifier(PUBLIC.build())
        .setName(visitorService.getContextInterfaceSimpleName())
        .addAllCDMembers(getMethods());

    if (!visitorService.getSuperCDsDirect().isEmpty()) {
      builder.setCDExtendUsage(getExtendUsage());
    }

    return builder.build();
  }

  protected List<ASTCDMethod> getMethods() {
    List<ASTCDMethod> methods = new ArrayList<>();

    for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes()) {
      if (typeSymbol.isIsClass() || typeSymbol.isIsInterface()) {
        ASTCDAttribute attribute = cdAttributeFacade.createAttribute(
            PROTECTED.build(), visitorService.createASTFullName(typeSymbol), "current" + visitorService.getCDName() + typeSymbol.getName());
        decorateAttribute(methods, attribute);
      }
    }

    for (String symbolName: symbolTableService.retrieveSymbolNamesFromCD()) {
      ASTCDAttribute attribute = cdAttributeFacade.createAttribute(
          PROTECTED.build(),
          symbolName,
          "current" + visitorService.getCDName() + symbolTableService.getSimpleNameFromSymbolName(symbolName));

      decorateAttribute(methods, attribute);
    }

    return methods;
  }

  protected void decorateAttribute(List<ASTCDMethod> methods, ASTCDAttribute attribute) {
    List<ASTCDMethod> mutatorMethods = new MutatorDecorator(glex).decorate(attribute);
    mutatorMethods.forEach(m -> m.setModifier(PUBLIC_ABSTRACT.build()));
    methods.addAll(mutatorMethods);

    List<ASTCDMethod> accessorMethods = new AccessorDecorator(glex, visitorService).decorate(attribute);
    accessorMethods.forEach(m -> m.setModifier(PUBLIC_ABSTRACT.build()));
    methods.addAll(accessorMethods);
  }

  protected ASTCDExtendUsage getExtendUsage() {
    return CDExtendUsageFacade.getInstance()
        .createCDExtendUsage(
            visitorService.getSuperCDsDirect().stream()
                .map(visitorService::getContextInterfaceFullName)
                .toArray(String[]::new));
  }
}
