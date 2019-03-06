package de.monticore.codegen.cd2java.ast_new;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.codegen.cd2java.methods.AccessorDecorator;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.symboltable.SymbolTableGeneratorHelper;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.CD4AnalysisHelper;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.se_rwth.commons.Names;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;


import static de.se_rwth.commons.Names.getQualifier;
import static de.se_rwth.commons.Names.getSimpleName;

public class ASTReferencedSymbolDecorator implements Decorator<ASTCDClass, ASTCDClass> {

  private final GlobalExtensionManagement glex;

  private final CDTypeFactory cdTypeFactory;

  private final CDAttributeFactory cdAttributeFactory;

  private static final String DEFINITION = "Definition";

  private static final String SYMBOL = "Symbol";

  //TODO test
  public ASTReferencedSymbolDecorator(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdAttributeFactory = CDAttributeFactory.getInstance();
  }

  @Override
  public ASTCDClass decorate(ASTCDClass astcdClass) {
    List<ASTCDAttribute> attributeList = new ArrayList<>();
    List<ASTCDMethod> methodList = new ArrayList<>();
    for (ASTCDAttribute astcdAttribute : astcdClass.getCDAttributeList()) {
      if (isReferencedSymbolAttribute(astcdAttribute)) {
        String referencedSymbolType = getReferencedSymbolType(astcdAttribute);
        //create referenced symbol attribute and methods
        ASTCDAttribute refSymbolAttribute = getRefSymbolAttribute(astcdAttribute, referencedSymbolType);
        attributeList.add(refSymbolAttribute);
        methodList.addAll(getRefSymbolMethods(refSymbolAttribute, referencedSymbolType));
        //create referenced definition Methods
        methodList.addAll(getRefDefinitionMethods(astcdAttribute, referencedSymbolType));

      }
    }
    astcdClass.addAllCDMethods(methodList);
    astcdClass.addAllCDAttributes(attributeList);
    return astcdClass;
  }

  private ASTCDAttribute getRefSymbolAttribute(ASTCDAttribute attribute, String referencedSymbol) {
    ASTType attributeType;
    if (GeneratorHelper.isListType(attribute.printType())) {
      //if the attribute is a list
      attributeType = cdTypeFactory.createMapTypeOf(String.class.getSimpleName(),  "Optional<" + referencedSymbol + ">>");
    } else {
      //if the attribute is mandatory or optional
      attributeType = cdTypeFactory.createOptionalTypeOf(referencedSymbol);
    }
    return cdAttributeFactory.createAttribute(PRIVATE, attributeType, attribute.getName() + SYMBOL);
  }

  private List<ASTCDMethod> getRefSymbolMethods(ASTCDAttribute refSymbolAttribute, String referencedSymbol) {
    if (GeneratorHelper.isMapType(refSymbolAttribute.printType())) {
      //have to change type of attribute list instead of map
      //because the inner representation is a map but for users the List methods are only shown
      ASTType optionalType = cdTypeFactory.createOptionalTypeOf(referencedSymbol);
      ASTType listType = cdTypeFactory.createListTypeOf(optionalType);
      refSymbolAttribute = cdAttributeFactory.createAttribute(refSymbolAttribute.getModifier(), listType, refSymbolAttribute.getName());
    }
    AccessorDecorator accessorDecorator = new AccessorDecorator(glex);
    List<ASTCDMethod> methods = accessorDecorator.decorate(refSymbolAttribute);
    return methods;
  }

  private List<ASTCDMethod> getRefDefinitionMethods(ASTCDAttribute astcdAttribute, String referencedSymbol) {
    ASTType symbolType;
    String referencedNode = referencedSymbol.substring(0, referencedSymbol.lastIndexOf("_symboltable")) + GeneratorHelper.AST_PACKAGE_SUFFIX_DOT + GeneratorHelper.AST_PREFIX + getSimpleSymbolName(referencedSymbol);
    if (GeneratorHelper.isListType(astcdAttribute.printType())) {
      //if the attribute is a list
      symbolType = cdTypeFactory.createListTypeOf(referencedNode);
    } else {
      //if the attribute is mandatory or optional
      symbolType = cdTypeFactory.createOptionalTypeOf(referencedNode);
    }
    ASTCDAttribute refSymbolAttribute = cdAttributeFactory.createAttribute(PRIVATE, symbolType, astcdAttribute.getName() + DEFINITION);
    AccessorDecorator accessorDecorator = new AccessorDecorator(glex);
    List<ASTCDMethod> methods = accessorDecorator.decorate(refSymbolAttribute);
    return methods;
  }


  private boolean isReferencedSymbolAttribute(ASTCDAttribute attribute) {
    return CD4AnalysisHelper.hasStereotype(attribute, MC2CDStereotypes.REFERENCED_SYMBOL.toString());
  }

  private String getSimpleSymbolName(String referencedSymbol) {
    return getSimpleName(referencedSymbol).substring(0, getSimpleName(referencedSymbol).indexOf(SYMBOL));
  }

  private String getReferencedSymbolType(ASTCDAttribute attribute) {
    String referencedSymbol = CD4AnalysisHelper.getStereotypeValues(attribute,
        MC2CDStereotypes.REFERENCED_SYMBOL.toString()).get(0);

    if (!getQualifier(referencedSymbol).isEmpty()) {
      referencedSymbol = SymbolTableGeneratorHelper
          .getQualifiedSymbolType(getQualifier(referencedSymbol)
              .toLowerCase(), Names.getSimpleName(referencedSymbol));
    }
    return referencedSymbol;
  }
}
