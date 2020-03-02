/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._ast.ast_class.reference.symbol;

import de.monticore.cd.cd4analysis._ast.*;
import de.monticore.codegen.cd2java.AbstractTransformer;
import de.monticore.codegen.cd2java._ast.ast_class.reference.symbol.methoddecorator.ReferencedSymbolAccessorDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;

/**
 * is a transforming class for the ast generation
 * adds the symbol reference attributes and the corresponding getters -> uses the symbol reference attribute created in ASTReferencedSymbolDecorator
 */

public class ASTReferencedSymbolDecorator<T extends ASTCDType> extends AbstractTransformer<T> {

  protected static final String SYMBOL = "Symbol";

  public static final String IS_OPTIONAL = "isOptional";

  protected final ReferencedSymbolAccessorDecorator accessorDecorator;

  protected final SymbolTableService symbolTableService;

  public ASTReferencedSymbolDecorator(final GlobalExtensionManagement glex,
                                      final ReferencedSymbolAccessorDecorator accessorDecorator,
                                      final SymbolTableService symbolTableService) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public T decorate(final T originalClass, T changedClass) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : originalClass.getCDAttributeList()) {
      if (symbolTableService.isReferencedSymbol(astcdAttribute)) {
        String referencedSymbolType = symbolTableService.getReferencedSymbolTypeName(astcdAttribute);
        //create referenced symbol attribute and methods
        ASTCDAttribute refSymbolAttribute = getRefSymbolAttribute(astcdAttribute, referencedSymbolType);
        attributeList.add(refSymbolAttribute);

        boolean wasAttributeOptional = wasAttributeOptional(astcdAttribute);
        methodList.addAll(getRefSymbolMethods(refSymbolAttribute, referencedSymbolType, wasAttributeOptional));
      }
    }
    changedClass.getCDMethodList().addAll(methodList);
    changedClass.getCDAttributeList().addAll(attributeList);
    return changedClass;
  }

  /**
   * creates optional attribute for mandatory and optional symbol references
   * creates a map for a list of symbol references
   */
  protected ASTCDAttribute getRefSymbolAttribute(ASTCDAttribute attribute, String referencedSymbol) {
    ASTModifier modifier = PROTECTED.build();
    //add referenced Symbol modifier that it can later be distinguished
    TransformationHelper.addStereotypeValue(modifier, MC2CDStereotypes.REFERENCED_SYMBOL_ATTRIBUTE.toString());

    ASTMCOptionalType optionalTypeOfReferencedSymbol = getMCTypeFacade().createOptionalTypeOf(referencedSymbol);
    if (getDecorationHelper().isListType(attribute.printType())) {
      //if the attribute is a list
      ASTMCType attributeType = getMCTypeFacade().createMapTypeOf(getMCTypeFacade().createStringType(), optionalTypeOfReferencedSymbol);
      ASTCDAttribute symbolAttribute = this.getCDAttributeFacade().createAttribute(modifier, attributeType, attribute.getName() + SYMBOL);
      replaceTemplate(VALUE, symbolAttribute, new StringHookPoint("= new HashMap<>()"));
      return symbolAttribute;
    } else {
      //if the attribute is mandatory or optional
      ASTCDAttribute symbolAttribute = this.getCDAttributeFacade().createAttribute(modifier, optionalTypeOfReferencedSymbol, attribute.getName() + SYMBOL);
      replaceTemplate(VALUE, symbolAttribute, new StringHookPoint("= Optional.empty()"));
      return symbolAttribute;
    }
  }

  /**
   * generated the correct getters for the reference symbol attributes
   */
  protected List<ASTCDMethod> getRefSymbolMethods(ASTCDAttribute refSymbolAttribute, String referencedSymbol, boolean wasAttributeOptional) {
    ASTCDAttribute methodDecorationAttribute = refSymbolAttribute.deepClone();
    if (getDecorationHelper().isMapType(refSymbolAttribute.printType())) {
      //have to change type of attribute list instead of map
      //because the inner representation is a map but for users the List methods are only shown
      ASTMCType optionalType = getMCTypeFacade().createOptionalTypeOf(referencedSymbol);
      ASTMCType listType = getMCTypeFacade().createListTypeOf(optionalType);
      methodDecorationAttribute = getCDAttributeFacade().createAttribute(refSymbolAttribute.getModifier().deepClone(), listType, refSymbolAttribute.getName());
    } else if (wasAttributeOptional) {
      //add stereotype to attribute to later in the method generation know if the original attribute was optional or mandatory
      TransformationHelper.addStereotypeValue(methodDecorationAttribute.getModifier(), IS_OPTIONAL);
    }
    //to later easy symbol type
    TransformationHelper.addStereotypeValue(methodDecorationAttribute.getModifier(), MC2CDStereotypes.REFERENCED_SYMBOL.toString(), referencedSymbol);
    return accessorDecorator.decorate(methodDecorationAttribute);
  }

  protected boolean wasAttributeOptional(ASTCDAttribute originalAttribute) {
    return getDecorationHelper().isOptional(originalAttribute.getMCType());
  }

}
