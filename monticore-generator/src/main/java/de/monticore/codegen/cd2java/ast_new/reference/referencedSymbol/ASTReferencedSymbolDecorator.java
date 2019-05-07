package de.monticore.codegen.cd2java.ast_new.reference.referencedSymbol;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.ast_new.reference.referencedSymbol.referenedSymbolMethodDecorator.ReferencedSymbolAccessorDecorator;
import de.monticore.codegen.cd2java.factories.DecorationHelper;
import de.monticore.codegen.cd2java.symboltable.SymbolTableService;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;
import de.monticore.umlcd4a.cd4analysis._ast.ASTModifier;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;

public class ASTReferencedSymbolDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  private static final String SYMBOL = "Symbol";

  public static final String IS_OPTIONAL = "isOptional";

  private final ReferencedSymbolAccessorDecorator accessorDecorator;

  private final SymbolTableService symbolTableService;

  public ASTReferencedSymbolDecorator(final GlobalExtensionManagement glex, final ReferencedSymbolAccessorDecorator accessorDecorator,
                                      final SymbolTableService symbolTableService) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
    this.symbolTableService = symbolTableService;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass clazz) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : clazz.getCDAttributeList()) {
      if (symbolTableService.isReferencedSymbol(astcdAttribute)) {
        String referencedSymbolType = symbolTableService.getReferencedSymbolTypeName(astcdAttribute);
        //create referenced symbol attribute and methods
        ASTCDAttribute refSymbolAttribute = getRefSymbolAttribute(astcdAttribute, referencedSymbolType);
        attributeList.add(refSymbolAttribute);

        boolean wasAttributeOptional = wasAttributeOptional(astcdAttribute);
        methodList.addAll(getRefSymbolMethods(refSymbolAttribute, referencedSymbolType, wasAttributeOptional));
      }
    }
    clazz.addAllCDMethods(methodList);
    clazz.addAllCDAttributes(attributeList);
    return clazz;
  }

  protected ASTCDAttribute getRefSymbolAttribute(ASTCDAttribute attribute, String referencedSymbol) {
    ASTType attributeType;
    if (GeneratorHelper.isListType(attribute.printType())) {
      //if the attribute is a list
      attributeType = getCDTypeFacade().createTypeByDefinition("Map< String, Optional<" + referencedSymbol + ">>");
    } else {
      //if the attribute is mandatory or optional
      attributeType = getCDTypeFacade().createOptionalTypeOf(referencedSymbol);
    }
    ASTModifier modifier = PRIVATE.build();
    //add referenced Symbol modifier that it can later be distinguished
    TransformationHelper.addStereotypeValue(modifier, MC2CDStereotypes.REFERENCED_SYMBOL_ATTRIBUTE.toString());
    return this.getCDAttributeFacade().createAttribute(PRIVATE, attributeType, attribute.getName() + SYMBOL);
  }

  private List<ASTCDMethod> getRefSymbolMethods(ASTCDAttribute refSymbolAttribute, String referencedSymbol, boolean wasAttributeOptional) {
    ASTCDAttribute methodDecorationAttribute = refSymbolAttribute.deepClone();
    if (GeneratorHelper.isMapType(refSymbolAttribute.printType())) {
      //have to change type of attribute list instead of map
      //because the inner representation is a map but for users the List methods are only shown
      ASTType optionalType = getCDTypeFacade().createOptionalTypeOf(referencedSymbol);
      ASTType listType = getCDTypeFacade().createListTypeOf(optionalType);
      methodDecorationAttribute = getCDAttributeFacade().createAttribute(refSymbolAttribute.getModifier().deepClone(), listType, refSymbolAttribute.getName());
    } else if (wasAttributeOptional) {
      //add stereotye to attribute to later in the method generation know if the original attribute was optional or mandatory
      TransformationHelper.addStereotypeValue(methodDecorationAttribute.getModifier(), IS_OPTIONAL);
    }

    return accessorDecorator.decorate(methodDecorationAttribute);
  }

  private boolean wasAttributeOptional(ASTCDAttribute originalAttribute) {
    return DecorationHelper.isOptional(originalAttribute.getType());
  }

}
