package de.monticore.codegen.cd2java._symboltable.symboltablecreator;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.cd.cd4code._ast.CD4CodeMill;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.DEQUE_WILDCARD_TYPE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PUBLIC;

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
    for (CDDefinitionSymbol cdDefinitionSymbol : superCDsTransitive) {
      // only super classes that have a start prod
      if (cdDefinitionSymbol.getAstNode().isPresent() && symbolTableService.hasStartProd(cdDefinitionSymbol.getAstNode().get())) {
        String superSTCForSubSTCName = symbolTableService.getSuperSTCForSubSTCSimpleName(cdDefinitionSymbol);
        String superSTC = symbolTableService.getSymbolTableCreatorFullName(cdDefinitionSymbol);
        String superScopeInterface = symbolTableService.getScopeInterfaceFullName(cdDefinitionSymbol);
        String ownScopeInterface = symbolTableService.getScopeInterfaceFullName();
        String dequeWildcardType = String.format(DEQUE_WILDCARD_TYPE, superScopeInterface);

        ASTCDClass superSTCForSubClass = CD4CodeMill.cDClassBuilder()
            .setName(superSTCForSubSTCName)
            .setModifier(PUBLIC.build())
            .setSuperclass(getCDTypeFacade().createQualifiedType(superSTC))
            .addCDConstructor(createConstructor(superSTCForSubSTCName, dequeWildcardType))
            .addCDMethod(createCreateScopeMethod(ownScopeInterface, symbolTableService.getCDName()))
            .build();
        superForSubSTC.add(superSTCForSubClass);
      }
    }
    return superForSubSTC;
  }

  protected ASTCDConstructor createConstructor(String className, String dequeWildcardType) {
    ASTCDParameter enclosingScope = getCDParameterFacade().createParameter(getCDTypeFacade().createTypeByDefinition(dequeWildcardType),
        "scopeStack");
    ASTCDConstructor constructor = getCDConstructorFacade().createConstructor(PUBLIC.build(), className, enclosingScope);
    this.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("super(scopeStack);"));
    return constructor;
  }

  protected ASTCDMethod createCreateScopeMethod(String scopeInterfaceName, String definitionName) {
    String symTabMill = symbolTableService.getSymTabMillFullName();
    ASTCDParameter boolParam = getCDParameterFacade().createParameter(getCDTypeFacade().createBooleanType(), "shadowing");
    ASTCDMethod createFromAST = getCDMethodFacade().createMethod(PUBLIC, getCDTypeFacade().createQualifiedType(scopeInterfaceName),
        "createScope", boolParam);
    this.replaceTemplate(EMPTY_BODY, createFromAST, new TemplateHookPoint(
        "_symboltable.symboltablecreator.CreateScope", scopeInterfaceName, symTabMill, definitionName));
    return createFromAST;
  }
}
