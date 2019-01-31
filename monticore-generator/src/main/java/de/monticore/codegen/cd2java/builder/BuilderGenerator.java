package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.GeneratorHelper;
import de.monticore.codegen.cd2java.Generator;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.List;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java.builder.BuilderGeneratorConstants.*;

class BuilderGenerator implements Generator<ASTCDClass, ASTCDClass> {

  private final GlobalExtensionManagement glex;

  private final CDTypeFactory cdTypeFactory;

  private final CDAttributeFactory cdAttributeFactory;

  private final CDConstructorFactory cdConstructorFactory;

  private final CDMethodFactory cdMethodFactory;


  public BuilderGenerator(final GlobalExtensionManagement glex) {
    this.glex = glex;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdAttributeFactory = CDAttributeFactory.getInstance();
    this.cdConstructorFactory = CDConstructorFactory.getInstance();
    this.cdMethodFactory = CDMethodFactory.getInstance();
  }

  @Override
  public ASTCDClass generate(final ASTCDClass domainClass) {
    String builderClassName = domainClass.getName() + BUILDER_SUFFIX;
    ASTType domainType = this.cdTypeFactory.createTypeByDefinition(domainClass.getName());
    ASTType builderType = this.cdTypeFactory.createTypeByDefinition(builderClassName);


    ModifierBuilder modifierBuilder = ModifierBuilder.builder().Public();
    if (domainClass.isPresentModifier() && domainClass.getModifier().isAbstract()) {
      modifierBuilder.Abstract();
    }

    ASTCDAttribute realThisAttribute = this.cdAttributeFactory.createProtectedAttribute(builderType, REAL_THIS);
    List<ASTCDAttribute> builderAttributes = domainClass.getCDAttributeList().stream()
        .map(ASTCDAttribute::deepClone)
        .collect(Collectors.toList());

    ASTCDConstructor constructor = this.cdConstructorFactory.createProtectedDefaultConstructor(builderClassName);

    ASTCDMethod buildMethod = this.cdMethodFactory.createPublicMethod(domainType, BUILD_METHOD);
    List<ASTCDAttribute> mandatoryAttributes = builderAttributes.stream()
        .filter(a -> !GeneratorHelper.isListType(a.printType()))
        .filter(a -> !GeneratorHelper.isOptional(a))
        .collect(Collectors.toList());
    this.glex.replaceTemplate(EMPTY_BODY, buildMethod, new TemplateHookPoint("builder.BuildMethod", domainClass, mandatoryAttributes));

    ASTCDMethod isValidMethod = this.cdMethodFactory.createPublicMethod(this.cdTypeFactory.createBooleanType(), IS_VALID);


    BuilderMethodGenerator builderMethodGenerator = new BuilderMethodGenerator(this.glex, builderType);
    List<ASTCDMethod> attributeMethods = domainClass.getCDAttributeList().stream()
        .map(builderMethodGenerator::generate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    return  CD4AnalysisMill.cDClassBuilder()
        .setModifier(modifierBuilder.build())
        .setName(builderClassName)
        .addCDAttribute(realThisAttribute)
        .addAllCDAttributes(builderAttributes)
        .addCDConstructor(constructor)
        .addCDMethod(buildMethod)
        .addCDMethod(isValidMethod)
        .addAllCDMethods(attributeMethods)
        .build();
  }
}
