package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.builder.BuilderDecoratorConstants.*;
import static de.monticore.codegen.cd2java.factories.CDModifier.*;
import static de.monticore.codegen.cd2java.factories.CDTypeFactory.BOOLEAN_TYPE;

class BuilderDecorator implements Decorator<ASTCDClass, ASTCDClass> {

  private final GlobalExtensionManagement glex;

  private final CDTypeFactory cdTypeFactory;

  private final CDAttributeFactory cdAttributeFactory;

  private final CDConstructorFactory cdConstructorFactory;

  private final CDMethodFactory cdMethodFactory;


  BuilderDecorator(final GlobalExtensionManagement glex) throws DecorateException {
    this.glex = glex;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdAttributeFactory = CDAttributeFactory.getInstance();
    this.cdConstructorFactory = CDConstructorFactory.getInstance();
    this.cdMethodFactory = CDMethodFactory.getInstance();
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass domainClass) throws DecorateException {
    String builderClassName = domainClass.getName() + BUILDER_SUFFIX;
    ASTType domainType = this.cdTypeFactory.createSimpleReferenceType(domainClass.getName());
    ASTType builderType = this.cdTypeFactory.createSimpleReferenceType(builderClassName);


    ASTModifier modifier = PUBLIC;
    if (this.isAbstract(domainClass)) {
      modifier = PUBLIC_ABSTRACT;
    }

    ASTCDAttribute realThisAttribute = this.cdAttributeFactory.createAttribute(PROTECTED, builderType, REAL_THIS);
    List<ASTCDAttribute> builderAttributes = domainClass.getCDAttributeList().stream()
        .map(ASTCDAttribute::deepClone)
        .collect(Collectors.toList());
    List<ASTCDAttribute> mandatoryAttributes = builderAttributes.stream()
        .filter(a -> !GeneratorHelper.isListType(a.printType()))
        .filter(a -> !GeneratorHelper.isOptional(a))
        .filter(a -> !GeneratorHelper.isPrimitive(a.getType()))
        .collect(Collectors.toList());


    ASTCDConstructor constructor = this.cdConstructorFactory.createConstructor(PROTECTED, builderClassName);
    this.glex.replaceTemplate(EMPTY_BODY, constructor, new StringHookPoint("this.realBuilder = (" + builderClassName + ") this;"));

    ASTCDMethod buildMethod = this.cdMethodFactory.createMethod(modifier, domainType, BUILD_METHOD);
    this.glex.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint("builder.BuildMethod", domainClass, mandatoryAttributes));

    ASTCDMethod isValidMethod = this.cdMethodFactory.createMethod(PUBLIC, BOOLEAN_TYPE, IS_VALID);
    this.glex.replaceTemplate(EMPTY_BODY, isValidMethod, new TemplateHookPoint("builder.IsValidMethod", mandatoryAttributes));


    BuilderMethodDecorator builderMethodGenerator = new BuilderMethodDecorator(this.glex, builderType);
    List<ASTCDMethod> attributeMethods = domainClass.getCDAttributeList().stream()
        .map(builderMethodGenerator::decorate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    return  CD4AnalysisMill.cDClassBuilder()
        .setModifier(modifier)
        .setName(builderClassName)
        .addCDAttribute(realThisAttribute)
        .addAllCDAttributes(builderAttributes)
        .addCDConstructor(constructor)
        .addCDMethod(buildMethod)
        .addCDMethod(isValidMethod)
        .addAllCDMethods(attributeMethods)
        .build();
  }

  private boolean isAbstract(final ASTCDClass domainClass) {
    return domainClass.isPresentModifier() && domainClass.getModifier().isAbstract();
  }
}
