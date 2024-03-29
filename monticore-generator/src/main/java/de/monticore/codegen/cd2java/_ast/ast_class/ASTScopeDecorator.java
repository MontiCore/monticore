/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDType;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.ENCLOSING_SCOPE_VAR;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.SPANNED_SCOPE_VAR;
import static de.monticore.cd.facade.CDModifier.*;

/**
 * creates a list of scope attributes that are used for the AST class
 */
public class ASTScopeDecorator extends AbstractCreator<ASTCDType, List<ASTCDAttribute>> {

  protected final SymbolTableService symbolTableService;

  public ASTScopeDecorator(final GlobalExtensionManagement glex,
                           final SymbolTableService symbolTableService) {
    super(glex);
    this.symbolTableService = symbolTableService;
  }

  @Override
  public List<ASTCDAttribute> decorate(final ASTCDType clazz) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    boolean isInheritedScope = symbolTableService.hasInheritedScopeStereotype(clazz.getModifier());
    ASTMCType scopeInterfaceType = symbolTableService.getScopeInterfaceType();

    // IsScopeSpanning?
    if (symbolTableService.hasScopeStereotype(clazz.getModifier()) || isInheritedScope) {
      ASTCDAttribute spannedScopeAttribute = createSpannedScopeAttribute(scopeInterfaceType);
      attributeList.add(spannedScopeAttribute);
    }

    //always add enclosingScope for attribute that has a scope
    attributeList.add(createEnclosingScopeAttribute(scopeInterfaceType));

    //add methods for super interfaces because otherwise the class will not compile
    //mechanism: search interfaces, get grammar from interface, add scope from grammar
    for (DiagramSymbol superCD : symbolTableService.getSuperCDsTransitive()) {
      ASTMCType superScopeInterfaceType = symbolTableService.getScopeInterfaceType(superCD);
      ASTCDAttribute enclosingScopeAttribute = createEnclosingScopeAttribute(superScopeInterfaceType);
      TransformationHelper.addStereotypeValue(enclosingScopeAttribute.getModifier(), MC2CDStereotypes.INHERITED.toString());
      attributeList.add(enclosingScopeAttribute);
      if (isInheritedScope) {
        ASTCDAttribute spannedScopeAttribute = createSpannedScopeAttribute(superScopeInterfaceType);
        TransformationHelper.addStereotypeValue(spannedScopeAttribute.getModifier(), MC2CDStereotypes.INHERITED.toString());
        attributeList.add(spannedScopeAttribute);
      }
    }
    return attributeList;
  }

  protected ASTCDAttribute createSpannedScopeAttribute(ASTMCType scopeType) {
    String attributeName = String.format(SPANNED_SCOPE_VAR, "");
    return this.getCDAttributeFacade().createAttribute(PROTECTED.build(), scopeType, attributeName);
  }

  protected ASTCDAttribute createEnclosingScopeAttribute(ASTMCType scopeType) {
    return this.getCDAttributeFacade().createAttribute(PROTECTED.build(), scopeType, ENCLOSING_SCOPE_VAR);
  }
}
