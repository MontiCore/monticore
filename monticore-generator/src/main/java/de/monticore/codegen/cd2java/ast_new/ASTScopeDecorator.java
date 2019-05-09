package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.methods.MethodDecorator;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.factories.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.symboltable.SymbolTableConstants.ENCLOSING_SCOPE;
import static de.monticore.codegen.cd2java.symboltable.SymbolTableConstants.SPANNED_SCOPE;

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
    if (symbolTableService.isScopeClass(clazz)) {
      //create attributes
      List<ASTCDAttribute> scopeAttributes = createScopeAttributes();
      clazz.addAllCDAttributes(scopeAttributes);
      //create getter and setter for all attributes
      List<ASTCDMethod> scopeMethods = new ArrayList<>();
      for (ASTCDAttribute x : scopeAttributes) {
        scopeMethods.addAll(methodDecorator.decorate(x));
      }
      clazz.addAllCDMethods(scopeMethods);
    }
    return clazz;
  }

  protected List<ASTCDAttribute> createScopeAttributes() {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    attributeList.add(createSpannedScopeAttribute());
    // new attributes for new Symtab branch
    ASTType scopeType = this.getCDTypeFacade().createOptionalTypeOf(symbolTableService.getScopeIntefaceType());
    attributeList.add(createSpannedScope2Attribute(scopeType));
    attributeList.add(createEnclosingScope2Attribute(scopeType));
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