package de.monticore.codegen.cd2java.builder;

import de.monticore.ast.ASTCNode;
import de.monticore.codegen.cd2java.Generator;
import de.monticore.codegen.cd2java.factories.*;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BuilderGenerator implements Generator<ASTCDClass, ASTCDClass> {

  private static final String BUILDER_SUFFIX = "Builder";

  private static final String BUILD_METHOD = "build";

  private static final String DEFAULT_SUPER_CLASS = "de.monticore.ast.ASTNodeBuilder<%s>";

  private final CDTypeFactory cdTypeFactory;

  private final CDConstructorFactory cdConstructorFactory;

  private final CDMethodFactory cdMethodFactory;

  private final CDParameterFactory cdParameterFactory;

  public BuilderGenerator(final CDTypeFactory cdTypeFactory, final CDConstructorFactory cdConstructorFactory, final CDMethodFactory cdMethodFactory,
      final CDParameterFactory cdParameterFactory) {
    this.cdTypeFactory = cdTypeFactory;
    this.cdConstructorFactory = cdConstructorFactory;
    this.cdMethodFactory = cdMethodFactory;
    this.cdParameterFactory = cdParameterFactory;
  }

  @Override
  public ASTCDClass generate(final ASTCDClass domainClass) {
    String builderClassName = domainClass.getName() + BUILDER_SUFFIX;
    ASTModifier modifier = ModifierBuilder.builder().Public().build();

    ASTReferenceType superClass = createBuilderSuperClass(domainClass);

    List<ASTCDAttribute> builderAttributes = domainClass.getCDAttributeList().stream()
        .map(ASTCDAttribute::deepClone)
        .collect(Collectors.toList());

    ASTCDConstructor constructor = this.cdConstructorFactory.createProtectedDefaultConstructor(builderClassName);

    ASTType domainType = this.cdTypeFactory.createTypeByDefinition(domainClass.getName());
    ASTCDMethod buildMethod = this.cdMethodFactory.createPublicMethod(domainType, BUILD_METHOD);

    ASTType builderType = this.cdTypeFactory.createTypeByDefinition(builderClassName);
    BuilderMethodGenerator builderMethodGenerator = createBuilderMethodGenerator(builderType);

    List<ASTCDMethod> attributeMethods = domainClass.getCDAttributeList().stream()
        .map(builderMethodGenerator::generate)
        .flatMap(List::stream)
        .collect(Collectors.toList());

    List<ASTCDMethod> astCNodeMethods = new ArrayList<>();
    if (hasSuperClassOtherThanASTCNode(domainClass)) {
      BuilderASTCNodeMethodGenerator builderASTCNodeMethodGenerator = createBuilderASTCNodeMethodGenerator(builderType);
      astCNodeMethods.addAll(builderASTCNodeMethodGenerator.generate());
    }

    return  CD4AnalysisMill.cDClassBuilder()
        .setModifier(modifier)
        .setName(builderClassName)
        .setSuperclass(superClass)
        .addAllCDAttributes(builderAttributes)
        .addCDConstructor(constructor)
        .addCDMethod(buildMethod)
        .addAllCDMethods(attributeMethods)
        .addAllCDMethods(astCNodeMethods)
        .build();
  }

  private ASTReferenceType createBuilderSuperClass(final ASTCDClass domainClass) {
    String superClass = String.format(DEFAULT_SUPER_CLASS, domainClass.getName());
    if (hasSuperClassOtherThanASTCNode(domainClass)) {
      superClass = domainClass.printSuperClass() + BUILDER_SUFFIX;
    }
    return this.cdTypeFactory.createReferenceTypeByDefinition(superClass);
  }

  private boolean hasSuperClassOtherThanASTCNode(final ASTCDClass domainClass) {
    return domainClass.isPresentSuperclass() && !ASTCNode.class.getSimpleName().equals(domainClass.printSuperClass());
  }

  private BuilderMethodGenerator createBuilderMethodGenerator(final ASTType builderType) {
    return new BuilderMethodGenerator(this.cdTypeFactory, this.cdMethodFactory, this.cdParameterFactory, builderType);
  }

  private BuilderASTCNodeMethodGenerator createBuilderASTCNodeMethodGenerator(final ASTType builderType) {
    return new BuilderASTCNodeMethodGenerator(this.cdMethodFactory, builderType);
  }
}
