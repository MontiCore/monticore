/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.interpreter;

import de.monticore.cd.facade.CDInterfaceUsageFacade;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.*;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.cd2java._visitor.VisitorConstants;
import de.monticore.codegen.cd2java._visitor.VisitorService;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.cd2java.methods.MutatorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;

public class InterpreterContextDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final VisitorService visitorService;

  protected final SymbolTableService symbolTableService;

  public InterpreterContextDecorator(final GlobalExtensionManagement glex,
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
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    return CD4CodeMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setCDInterfaceUsage(getInterfaceUsage())
        .setName(visitorService.getContextSimpleName())
        .addAllCDMembers(getAttributes())
        .build();
  }

  protected List<ASTCDMember> getAttributes() {
    List<ASTCDMember> attributes = new ArrayList<>();

    for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes()) {
      getCurrentAttributeForAST(attributes, typeSymbol, visitorService.getCDName());
    }

    for (String symbolName: symbolTableService.retrieveSymbolNamesFromCD()) {
      getCurrentAttributeForSymbol(attributes, symbolName, visitorService.getCDName());
    }

    for (DiagramSymbol superCD: visitorService.getSuperCDsTransitive()) {
      for (CDTypeSymbol typeSymbol : visitorService.getAllCDTypes(superCD)) {
        getCurrentAttributeForAST(attributes, typeSymbol, superCD.getName());
      }

      for (String symbolName: symbolTableService.retrieveSymbolNamesFromCD(superCD)) {
        getCurrentAttributeForSymbol(attributes, symbolName, superCD.getName());
      }
    }

    return attributes;
  }

  protected void getCurrentAttributeForAST(List<ASTCDMember> attributes, CDTypeSymbol typeSymbol, String name) {
    if (typeSymbol.isIsClass() || typeSymbol.isIsInterface()) {
      ASTCDAttribute attribute = cdAttributeFacade.createAttribute(
          PROTECTED.build(), visitorService.createASTFullName(typeSymbol), "current" + name + typeSymbol.getName());
      attributes.add(attribute);
      attributes.addAll(new MutatorDecorator(glex).decorate(attribute));
      attributes.addAll(new AccessorDecorator(glex, visitorService).decorate(attribute));
    }
  }

  protected void getCurrentAttributeForSymbol(List<ASTCDMember> attributes, String symbolName, String cdName) {
    ASTCDAttribute attribute = cdAttributeFacade.createAttribute(
        PROTECTED.build(), symbolName, "current" + cdName + symbolTableService.getSimpleNameFromSymbolName(symbolName));
    attributes.add(attribute);
    attributes.addAll(new MutatorDecorator(glex).decorate(attribute));
    attributes.addAll(new AccessorDecorator(glex, visitorService).decorate(attribute));
  }

  protected ASTCDInterfaceUsage getInterfaceUsage() {
    return CDInterfaceUsageFacade.getInstance()
        .createCDInterfaceUsage(visitorService.getContextInterfaceFullName());
  }

}
