package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.I_SYMBOL_REFERENCE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

public class SymbolReferenceDecorator extends AbstractCreator<ASTCDType, ASTCDClass> {

  protected SymbolTableService symbolTableService;

  public SymbolReferenceDecorator(final GlobalExtensionManagement glex,
                                  final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDType input) {
    String symbolReferenceClassSimpleName = symbolTableService.getSymbolReferenceClassSimpleName(input);
    String symbolFullName = symbolTableService.getSymbolFullName(input);
    ASTMCQualifiedType scopeInterfaceType = symbolTableService.getScopeInterfaceType();
    return CD4AnalysisMill.cDClassBuilder()
        .setName(symbolReferenceClassSimpleName)
        .setModifier(PUBLIC.build())
        .setSuperclass(getCDTypeFacade().createQualifiedType(symbolFullName))
        .addInterface(getCDTypeFacade().createQualifiedType(I_SYMBOL_REFERENCE))
        .addCDConstructor(createConstructor(symbolReferenceClassSimpleName, scopeInterfaceType))
        .build();
  }

  protected ASTCDConstructor createConstructor(String symbolReferenceClass, ASTMCType scopeInterfaceType){
    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, "name");
    ASTCDParameter enclosingScopeParameter = getCDParameterFacade().createParameter(scopeInterfaceType, "enclosingScope");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), symbolReferenceClass, nameParameter, enclosingScopeParameter);
    this.replaceTemplate(EMPTY_BODY, constructor, new TemplateHookPoint("_symboltable.symbolreferece.Constructor"));
    return constructor;
  }
}
