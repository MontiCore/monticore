package de.monticore.codegen.cd2java.ast_new.reference.referencedSymbol;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java.ast_new.reference.ReferencedSymbolUtil;
import de.monticore.codegen.cd2java.ast_new.reference.referencedSymbol.referenedSymbolMethodDecorator.ReferencedSymbolAccessorDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;

public class ASTReferencedSymbolDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  private static final String SYMBOL = "Symbol";

  public static final String IS_OPTIONAL = "isOptional";

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

  protected ASTCDAttribute getRefSymbolAttribute(ASTCDAttribute attribute, String referencedSymbol) {
    ASTType attributeType;
    if (GeneratorHelper.isListType(attribute.printType())) {
      //if the attribute is a list
      attributeType = getCDTypeFactory().createTypeByDefinition("Map< String, Optional<" + referencedSymbol + ">>");
    } else {
      //if the attribute is mandatory or optional
      attributeType = getCDTypeFactory().createOptionalTypeOf(referencedSymbol);
    }
    return this.getCDAttributeFactory().createAttribute(PRIVATE, attributeType, attribute.getName() + SYMBOL);
  }

  private List<ASTCDMethod> getRefSymbolMethods(ASTCDAttribute refSymbolAttribute, String referencedSymbol) {
    ASTCDAttribute methodDecorationAttribute = refSymbolAttribute.deepClone();
    if (GeneratorHelper.isMapType(refSymbolAttribute.printType())) {
      //have to change type of attribute list instead of map
      //because the inner representation is a map but for users the List methods are only shown
      ASTType optionalType = getCDTypeFactory().createOptionalTypeOf(referencedSymbol);
      ASTType listType = getCDTypeFactory().createListTypeOf(optionalType);
      methodDecorationAttribute = getCDAttributeFactory().createAttribute(refSymbolAttribute.getModifier().deepClone(), listType, refSymbolAttribute.getName());
    } else if (GeneratorHelper.isOptional(refSymbolAttribute)) {
      //add stereotye to attribute to later in the method generation know if the original attribute was optional or mandatory
      ASTCDStereoValue stereoValue = CD4AnalysisMill.cDStereoValueBuilder().setName("isOptional").build();
      methodDecorationAttribute.getModifier().setStereotype(CD4AnalysisMill.cDStereotypeBuilder().addValue(stereoValue).build());
    }

    return accessorDecorator.decorate(methodDecorationAttribute);
  }

}
