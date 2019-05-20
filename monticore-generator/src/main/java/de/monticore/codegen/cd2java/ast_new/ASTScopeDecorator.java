package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;

import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.symboltable.SymbolTableConstants.*;

public class ASTScopeDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {


  private final MethodDecorator methodDecorator;

  private final SymbolTableService symbolTableService;

  public ASTScopeDecorator(final GlobalExtensionManagement glex, final MethodDecorator methodDecorator,
                           final SymbolTableService symbolTableService) {
    super(glex);
    this.methodDecorator = methodDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass clazz) {
    ASTType scopeInterfaceType = symbolTableService.getScopeInterfaceType();
    if (symbolTableService.hasScopeStereotype(clazz)) {
      //create attributes
      ASTCDAttribute spannedScopeAttribute = createSpannedScopeAttribute();
      clazz.addCDAttribute(spannedScopeAttribute);
      clazz.addAllCDMethods(methodDecorator.decorate(spannedScopeAttribute));

      ASTType optScopeInterfaceType = this.getCDTypeFacade().createOptionalTypeOf(symbolTableService.getScopeInterfaceType());
      ASTCDAttribute spannedScope2Attribute = createSpannedScope2Attribute(optScopeInterfaceType);
      clazz.addCDAttribute(spannedScope2Attribute);
      clazz.addAllCDMethods(methodDecorator.decorate(spannedScope2Attribute));
    }

    //always add enclosingScope2 for attribute that has a scope
    ASTCDAttribute enclosingScope2Attribute = createEnclosingScope2Attribute(scopeInterfaceType);
    clazz.addCDAttribute(enclosingScope2Attribute);
    clazz.addAllCDMethods(methodDecorator.decorate(enclosingScope2Attribute));

    return clazz;
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