/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

/**
 * creates a SymbolReference class from a grammar
 */
public class SymbolTableCreatorForSuperTypes extends AbstractCreator<ASTCDCompilationUnit, List<ASTCDClass>> {

  protected final SymbolTableService symbolTableService;

  public SymbolTableCreatorForSuperTypes(final GlobalExtensionManagement glex,
                                         final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public List<ASTCDClass> decorate(ASTCDCompilationUnit input) {
    List<ASTCDClass> superForSubSTC = new ArrayList<>();
    List<CDDefinitionSymbol> superCDsTransitive = symbolTableService.getSuperCDsTransitive();
    String ownScopeInterface = symbolTableService.getScopeInterfaceFullName();
    for (CDDefinitionSymbol cdDefinitionSymbol : superCDsTransitive) {
      // only super classes, that have a start prod
      if (cdDefinitionSymbol.isPresentAstNode() && symbolTableService.hasStartProd(cdDefinitionSymbol.getAstNode())) {
        String superSTCForSubSTCName = symbolTableService.getSuperSTCForSubSTCSimpleName(cdDefinitionSymbol);
        String superSTC = symbolTableService.getSymbolTableCreatorFullName(cdDefinitionSymbol);
        String superScopeInterface = symbolTableService.getScopeInterfaceFullName(cdDefinitionSymbol);
        ASTMCWildcardTypeArgument wildCardTypeArgument = getMCTypeFacade().createWildCardWithUpperBoundType(superScopeInterface);
        ASTMCBasicGenericType dequeWildcardType = getMCTypeFacade().createBasicGenericTypeOf(DEQUE_TYPE, wildCardTypeArgument);

        ASTCDClass superSTCForSubClass = CD4CodeMill.cDClassBuilder()
            .setName(superSTCForSubSTCName)
            .setModifier(PUBLIC.build())
            .setSuperclass(getMCTypeFacade().createQualifiedType(superSTC))
            .addCDConstructor(createConstructor(superSTCForSubSTCName, dequeWildcardType))
            .addCDMethod(createCreateScopeMethod(ownScopeInterface, symbolTableService.getCDName()))
            .build();
        superForSubSTC.add(superSTCForSubClass);
      }
    }
    return superForSubSTC;
  }

  protected ASTCDConstructor createConstructor(String className, ASTMCType dequeWildcardType) {
    ASTCDParameter enclosingScope = getCDParameterFacade().createParameter(dequeWildcardType, SCOPE_STACK_VAR);
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), className, enclosingScope);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("super(" + SCOPE_STACK_VAR + ");"));
    return constructor;
  }

  protected ASTCDMethod createCreateScopeMethod(String scopeInterfaceName, String definitionName) {
    String symTabMill = symbolTableService.getMillFullName();
    ASTCDParameter boolParam = getCDParameterFacade().createParameter(getMCTypeFacade().createBooleanType(), SHADOWING_VAR);
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(scopeInterfaceName),
        "createScope", boolParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(
        "_symboltable.symboltablecreator.CreateScope", scopeInterfaceName, symTabMill, definitionName));
    return createFromAST;
  }
}
