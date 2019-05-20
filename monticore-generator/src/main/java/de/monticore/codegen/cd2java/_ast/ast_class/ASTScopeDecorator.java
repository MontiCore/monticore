package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.ENCLOSING_SCOPE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SPANNED_SCOPE;
import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;

public class ASTScopeDecorator extends AbstractDecorator<ASTCDType, List<ASTCDAttribute>> {

  private final SymbolTableService symbolTableService;

  public ASTScopeDecorator(final GlobalExtensionManagement glex,
                           final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public List<ASTCDAttribute> decorate(final ASTCDType clazz) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    ASTType scopeInterfaceType = symbolTableService.getScopeInterfaceType();
    if (clazz.getModifierOpt().isPresent() && symbolTableService.hasScopeStereotype(clazz.getModifierOpt().get())) {
      //create attributes
      ASTCDAttribute spannedScopeAttribute = createSpannedScopeAttribute();
      attributeList.add(spannedScopeAttribute);

      ASTType optScopeInterfaceType = this.getCDTypeFacade().createOptionalTypeOf(symbolTableService.getScopeInterfaceType());
      ASTCDAttribute spannedScope2Attribute = createSpannedScope2Attribute(optScopeInterfaceType);
      attributeList.add(spannedScope2Attribute);
    }
    //always add enclosingScope2 for attribute that has a scope
    ASTCDAttribute enclosingScope2Attribute = createEnclosingScope2Attribute(scopeInterfaceType);
    attributeList.add(enclosingScope2Attribute);

    return attributeList;
  }

  protected ASTCDAttribute createSpannedScopeAttribute() {
    //todo replace with spannedScope2 some day
    ASTType scopeType = this.getCDTypeFacade().createOptionalTypeOf(symbolTableService.getScopeType());
    String attributeName = String.format(SPANNED_SCOPE, symbolTableService.getCDName());
    return this.getCDAttributeFacade().createAttribute(PROTECTED, scopeType, attributeName);
  }

  protected ASTCDAttribute createSpannedScope2Attribute(ASTType scopeType) {
    //todo better name with the grammar name in the attributeName, like it was before
//    String attributeName = String.format(SPANNED_SCOPE, symbolTableService.getCDName()) + "2";
    String attributeName = String.format(SPANNED_SCOPE, "") + "2";
    return this.getCDAttributeFacade().createAttribute(PROTECTED, scopeType, attributeName);
  }

  protected ASTCDAttribute createEnclosingScope2Attribute(ASTType scopeType) {
    String attributeName = ENCLOSING_SCOPE + "2";
    return this.getCDAttributeFacade().createAttribute(PROTECTED, scopeType, attributeName);
  }
}