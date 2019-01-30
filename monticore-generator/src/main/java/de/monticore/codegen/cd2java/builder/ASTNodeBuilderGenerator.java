package de.monticore.codegen.cd2java.builder;

import de.monticore.ast.ASTCNode;
import de.monticore.codegen.cd2java.Generator;
import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.ArrayList;
import java.util.List;

import static de.monticore.codegen.cd2java.builder.BuilderGeneratorConstants.BUILDER_SUFFIX;

public class ASTNodeBuilderGenerator implements Generator<ASTCDClass, ASTCDClass> {

  private static final String DEFAULT_SUPER_CLASS = "de.monticore.ast.ASTNodeBuilder<%s>";

  private final GlobalExtensionManagement glex;

  private final BuilderGenerator builderGenerator;

  private final CDTypeFactory cdTypeFactory;

  private final CDMethodFactory cdMethodFactory;

  public ASTNodeBuilderGenerator(
      final GlobalExtensionManagement glex,
      final BuilderGenerator builderGenerator) {
    this.glex = glex;
    this.builderGenerator = builderGenerator;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdMethodFactory = CDMethodFactory.getInstance();
  }

  @Override
  public ASTCDClass generate(ASTCDClass domainClass) {
    ASTCDClass builderClass = this.builderGenerator.generate(domainClass);
    String builderClassName = builderClass.getName();

    ASTReferenceType superClass = createBuilderSuperClass(domainClass, builderClassName);
    builderClass.setSuperclass(superClass);

    List<ASTCDMethod> astCNodeMethods = new ArrayList<>();

    if (hasSuperClassOtherThanASTCNode(domainClass)) {
      ASTType builderType = this.cdTypeFactory.createTypeByDefinition(builderClassName);
      BuilderASTCNodeMethodGenerator builderASTCNodeMethodGenerator = createBuilderASTCNodeMethodGenerator(builderType);
      astCNodeMethods.addAll(builderASTCNodeMethodGenerator.generate());
    }

    builderClass.addAllCDMethods(astCNodeMethods);

    return builderClass;
  }


  private ASTReferenceType createBuilderSuperClass(final ASTCDClass domainClass, final String builderClassName) {
    String superClass = String.format(DEFAULT_SUPER_CLASS, builderClassName);
    if (hasSuperClassOtherThanASTCNode(domainClass)) {
      superClass = domainClass.printSuperClass() + BUILDER_SUFFIX;
    }
    return this.cdTypeFactory.createReferenceTypeByDefinition(superClass);
  }

  private boolean hasSuperClassOtherThanASTCNode(final ASTCDClass domainClass) {
    return domainClass.isPresentSuperclass() && !ASTCNode.class.getSimpleName().equals(domainClass.printSuperClass());
  }

  private BuilderASTCNodeMethodGenerator createBuilderASTCNodeMethodGenerator(final ASTType builderType) {
    return new BuilderASTCNodeMethodGenerator(this.glex, builderType);
  }
}
