/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDType;
import de.monticore.cd.cd4analysis._symboltable.CDDefinitionSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.ENCLOSING_SCOPE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SPANNED_SCOPE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;

public class ASTScopeDecorator extends AbstractCreator<ASTCDType, List<ASTCDAttribute>> {

  private final SymbolTableService symbolTableService;

  public ASTScopeDecorator(final GlobalExtensionManagement glex,
                           final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public List<ASTCDAttribute> decorate(final ASTCDType clazz) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    ASTMCType scopeInterfaceType = symbolTableService.getScopeInterfaceType();
    Optional<ASTCDType> superScope = symbolTableService.getTypeWithScopeInfo(clazz);
    if (superScope.isPresent()) {
      //create attributes
      attributeList.add(createSpannedScopeAttribute());

      ASTMCType optScopeInterfaceType = this.getCDTypeFacade().createOptionalTypeOf(symbolTableService.getScopeInterfaceType());
      attributeList.add(createSpannedScopeAttribute(optScopeInterfaceType));
    }
    //always add enclosingScope for attribute that has a scope
    attributeList.add(createEnclosingScopeAttribute(scopeInterfaceType));

    //add methods for super interfaces because otherwise the class will not compile
    //todo only add methods for scopes that are needed from the interfaces the class extends
    //mechanism: search interfaces, get grammar from interface, add scope from grammar
    for (CDDefinitionSymbol superCD : symbolTableService.getSuperCDsTransitive()) {
      ASTMCType superScopeInterfaceType = symbolTableService.getScopeInterfaceType(superCD);
      ASTCDAttribute enclosingScopeAttribute = createEnclosingScopeAttribute(superScopeInterfaceType);
      TransformationHelper.addStereotypeValue(enclosingScopeAttribute.getModifier(), MC2CDStereotypes.INHERITED.toString());
      attributeList.add(enclosingScopeAttribute);
    }
    return attributeList;
  }

  protected ASTCDAttribute createSpannedScopeAttribute() {
    //todo replace with spannedScope2 some day
    ASTMCType scopeType = this.getCDTypeFacade().createOptionalTypeOf(symbolTableService.getScopeType());
    String attributeName = String.format(SPANNED_SCOPE, symbolTableService.getCDName());
    ASTCDAttribute attribute = this.getCDAttributeFacade().createAttribute(PROTECTED, scopeType, attributeName);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    return attribute;
  }

  protected ASTCDAttribute createSpannedScopeAttribute(ASTMCType scopeType) {
    //todo better name with the grammar name in the attributeName, like it was before
    String attributeName = String.format(SPANNED_SCOPE, "");
    ASTCDAttribute attribute = this.getCDAttributeFacade().createAttribute(PROTECTED, scopeType, attributeName);
    this.replaceTemplate(VALUE, attribute, new StringHookPoint("= Optional.empty()"));
    return attribute;
  }

  protected ASTCDAttribute createEnclosingScopeAttribute(ASTMCType scopeType) {
    String attributeName = ENCLOSING_SCOPE;
    return this.getCDAttributeFacade().createAttribute(PROTECTED, scopeType, attributeName);
  }
}
