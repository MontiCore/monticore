package de.monticore.codegen.cd2java._ast.ast_class;

import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDType;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;
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
      attributeList.add(createSpannedScopeAttribute());

      ASTType optScopeInterfaceType = this.getCDTypeFacade().createOptionalTypeOf(symbolTableService.getScopeInterfaceType());
      attributeList.add(createSpannedScope2Attribute(optScopeInterfaceType));
    }

    //add methods for super intrefaces because otherwise the class will not compile
    for (ASTReferenceType referenceType : clazz.getInterfaceList()) {
      if(referenceType instanceof ASTSimpleReferenceType){
        ASTSimpleReferenceType simpleReferenceType = (ASTSimpleReferenceType) referenceType;
        simpleReferenceType.getNameList().set(simpleReferenceType.lastIndexOfName("_ast"), SYMBOL_TABLE_PACKGE);
        simpleReferenceType.getName(simpleReferenceType.sizeNames()-1).replaceFirst("AST", "I");
      }
      ASTCDAttribute enclosingScope2Attribute = createEnclosingScope2Attribute(referenceType);
      TransformationHelper.addStereotypeValue(enclosingScope2Attribute.getModifier(), MC2CDStereotypes.INHERITED.toString());
      attributeList.add(enclosingScope2Attribute);
    }
    //always add enclosingScope2 for attribute that has a scope
    attributeList.add(createEnclosingScope2Attribute(scopeInterfaceType));

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