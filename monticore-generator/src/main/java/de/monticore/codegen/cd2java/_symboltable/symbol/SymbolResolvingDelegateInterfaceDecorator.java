package de.monticore.codegen.cd2java._symboltable.symbol;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC_ABSTRACT;

public class SymbolResolvingDelegateInterfaceDecorator extends AbstractCreator<ASTCDType, ASTCDInterface> {

  protected final SymbolTableService symbolTableService;

  public SymbolResolvingDelegateInterfaceDecorator(final GlobalExtensionManagement glex,
                                                   final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDInterface decorate(ASTCDType input) {
    String symbolResolvingDelegateInterface = symbolTableService.getSymbolResolvingDelegateInterfaceSimpleName(input);
    String symbolFullName = symbolTableService.getSymbolFullName(input);
    String symbolSimpleName = symbolTableService.getSymbolSimpleName(input);


    return CD4AnalysisMill.cDInterfaceBuilder()
        .setName(symbolResolvingDelegateInterface)
        .setModifier(PUBLIC.build())
        .addCDMethod(createResolveAdaptedStateSymbol(symbolFullName, symbolSimpleName))
        .build();
  }

  protected ASTCDMethod createResolveAdaptedStateSymbol(String fullSymbolName, String simpleSymbolName) {
    ASTMCType collectionTypeOfSymbol = getCDTypeFacade().createCollectionTypeOf(fullSymbolName);

    ASTCDParameter nameParameter = getCDParameterFacade().createParameter(String.class, "name");
    ASTCDParameter accessModifierParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createQualifiedType(ACCESS_MODIFIER), "modifier");
    ASTCDParameter foundSymbolsParameter = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), "foundSymbols");
    ASTCDParameter predicateParameter = getCDParameterFacade().createParameter(getCDTypeFacade()
        .createTypeByDefinition(String.format(PREDICATE, fullSymbolName)), "predicate");

    return getCDMethodFacade().createMethod(PUBLIC_ABSTRACT, collectionTypeOfSymbol, String.format(RESOLVE_ADAPTED, simpleSymbolName),
        foundSymbolsParameter, nameParameter, accessModifierParameter, predicateParameter);
  }
}
