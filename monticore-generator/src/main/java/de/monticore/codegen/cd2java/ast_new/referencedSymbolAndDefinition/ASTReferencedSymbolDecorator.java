package de.monticore.codegen.cd2java.ast_new.referencedSymbolAndDefinition;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.ast_new.referencedSymbolAndDefinition.referenedSymbolMethodDecorator.ReferencedSymbolAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;

public class ASTReferencedSymbolDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  private static final String SYMBOL = "Symbol";

  private final ReferencedSymbolAccessorDecorator accessorDecorator;

  public ASTReferencedSymbolDecorator(final GlobalExtensionManagement glex, final ReferencedSymbolAccessorDecorator accessorDecorator) {
    super(glex);
    this.accessorDecorator = accessorDecorator;
  }

  @Override
  public ASTCDClass decorate(ASTCDClass clazz) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : clazz.getCDAttributeList()) {
      if (ReferencedSymbolUtil.isReferencedSymbolAttribute(astcdAttribute)) {
        String referencedSymbolType = ReferencedSymbolUtil.getReferencedSymbolTypeName(astcdAttribute);
        //create referenced symbol attribute and methods
        ASTCDAttribute refSymbolAttribute = getRefSymbolAttribute(astcdAttribute, referencedSymbolType);
        attributeList.add(refSymbolAttribute);
        methodList.addAll(getRefSymbolMethods(refSymbolAttribute, referencedSymbolType));
      }
    }
    clazz.addAllCDMethods(methodList);
    clazz.addAllCDAttributes(attributeList);
    return clazz;
  }

  private ASTCDAttribute getRefSymbolAttribute(ASTCDAttribute attribute, String referencedSymbol) {
    ASTType attributeType;
    if (GeneratorHelper.isListType(attribute.printType())) {
      //if the attribute is a list
      attributeType = getCDTypeFactory().createTypeByDefinition("Map< String, Optional<" + referencedSymbol + ">>");
    } else {
      //if the attribute is mandatory or optional
      attributeType = getCDTypeFactory().createOptionalTypeOf(referencedSymbol);
    }
    ASTCDAttribute astcdAttribute = this.getCDAttributeFactory().createAttribute(PRIVATE, attributeType, attribute.getName() + SYMBOL);
    return astcdAttribute;
  }

  private List<ASTCDMethod> getRefSymbolMethods(ASTCDAttribute refSymbolAttribute, String referencedSymbol) {
    if (GeneratorHelper.isMapType(refSymbolAttribute.printType())) {
      //have to change type of attribute list instead of map
      //because the inner representation is a map but for users the List methods are only shown
      ASTType optionalType = getCDTypeFactory().createOptionalTypeOf(referencedSymbol);
      ASTType listType = getCDTypeFactory().createListTypeOf(optionalType);
      refSymbolAttribute = getCDAttributeFactory().createAttribute(refSymbolAttribute.getModifier().deepClone(), listType, refSymbolAttribute.getName());
    }
    return accessorDecorator.decorate(refSymbolAttribute);
  }

}
