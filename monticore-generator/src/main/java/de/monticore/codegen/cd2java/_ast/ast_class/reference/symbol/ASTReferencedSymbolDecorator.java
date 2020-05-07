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
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.CoreTemplates.VALUE;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.LOADER_SUFFIX;

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

        methodList.addAll(getRefSymbolMethods(astcdAttribute, referencedSymbolType));
        if (!getDecorationHelper().isListType(astcdAttribute.printType())) {
          methodList.add(getUpdateLoaderAttribute(refSymbolAttribute, astcdAttribute.getName(),
              getDecorationHelper().isOptional(astcdAttribute.printType())));
        } else {
          methodList.add(getUpdateLoaderListAttribute(refSymbolAttribute.getName(), referencedSymbolType, astcdAttribute.getName()));
        }
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

    ASTMCQualifiedType symbolLoaderType = getMCTypeFacade().createQualifiedType(referencedSymbol + LOADER_SUFFIX);
    if (getDecorationHelper().isListType(attribute.printType())) {
      //if the attribute is a list
      ASTMCType attributeType = getMCTypeFacade().createMapTypeOf(getMCTypeFacade().createStringType(), symbolLoaderType);
      ASTCDAttribute symbolAttribute = this.getCDAttributeFacade().createAttribute(modifier, attributeType, attribute.getName() + SYMBOL + LOADER_SUFFIX);
      replaceTemplate(VALUE, symbolAttribute, new StringHookPoint("= new HashMap<>()"));
      return symbolAttribute;
    } else {
      //if the attribute is mandatory or optional
      return this.getCDAttributeFacade().createAttribute(modifier, symbolLoaderType, attribute.getName() + SYMBOL + LOADER_SUFFIX);
    }
  }

  /**
   * generated the correct getters for the reference symbol attributes
   */
  protected List<ASTCDMethod> getRefSymbolMethods(ASTCDAttribute astcdAttribute, String referencedSymbol) {
    ASTCDAttribute symbolAttribute = getCDAttributeFacade().createAttribute(PUBLIC, referencedSymbol, astcdAttribute.getName() + SYMBOL);
    if (getDecorationHelper().isListType(astcdAttribute.printType())) {
      //have to change type of attribute list instead of map
      //because the inner representation is a map but for users the List methods are only shown
      ASTMCType optionalType = getMCTypeFacade().createOptionalTypeOf(referencedSymbol);
      ASTMCType listType = getMCTypeFacade().createListTypeOf(optionalType);
      symbolAttribute = getCDAttributeFacade().createAttribute(astcdAttribute.getModifier().deepClone(), listType, astcdAttribute.getName() + SYMBOL);
    } else if (getDecorationHelper().isOptional(astcdAttribute.printType())) {
      //add stereotype to attribute to later in the method generation know if the original attribute was optional or mandatory
      TransformationHelper.addStereotypeValue(symbolAttribute.getModifier(), IS_OPTIONAL);
    }
    //to later easy symbol type
    TransformationHelper.addStereotypeValue(symbolAttribute.getModifier(), MC2CDStereotypes.REFERENCED_SYMBOL.toString(), referencedSymbol);
    return accessorDecorator.decorate(symbolAttribute);
  }


  protected ASTCDMethod getUpdateLoaderAttribute(ASTCDAttribute loaderAttribute, String nameAttributeName, boolean wasOptional) {
    ASTCDMethod updateLoaderMethod = getCDMethodFacade().createMethod(PROTECTED, "update" +
        StringTransformations.capitalize(loaderAttribute.getName()));
    replaceTemplate(EMPTY_BODY, updateLoaderMethod, new TemplateHookPoint("_ast.ast_class.refSymbolMethods.UpdateLoader",
        loaderAttribute.getName(), getDecorationHelper().getNativeAttributeName(nameAttributeName), loaderAttribute.printType(), wasOptional));
    return updateLoaderMethod;
  }

  protected ASTCDMethod getUpdateLoaderListAttribute(String referencedAttributeName, String referencedAttributeType, String nameAttributeName) {
    ASTCDMethod updateLoaderMethod = getCDMethodFacade().createMethod(PROTECTED, "update" +
        StringTransformations.capitalize(referencedAttributeName));
    replaceTemplate(EMPTY_BODY, updateLoaderMethod, new TemplateHookPoint("_ast.ast_class.refSymbolMethods.UpdateLoaderList",
        referencedAttributeName, nameAttributeName, referencedAttributeType + LOADER_SUFFIX));
    return updateLoaderMethod;
  }


}
