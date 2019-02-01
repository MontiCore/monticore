package de.monticore.codegen.cd2java.builder;

import de.monticore.ast.ASTCNode;
import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.factories.CDMethodFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.builder.BuilderDecoratorConstants.BUILDER_SUFFIX;

public class ASTNodeBuilderDecorator implements Decorator<ASTCDClass, ASTCDClass> {

  private static final String DEFAULT_SUPER_CLASS = "de.monticore.ast.ASTNodeBuilder<%s>";

  private final GlobalExtensionManagement glex;

  private final BuilderDecorator builderDecorator;

  private final CDTypeFactory cdTypeFactory;

  public ASTNodeBuilderDecorator(
      final GlobalExtensionManagement glex,
      final BuilderDecorator builderDecorator) {
    this.glex = glex;
    this.builderDecorator = builderDecorator;
    this.cdTypeFactory = CDTypeFactory.getInstance();
  }

  @Override
  public ASTCDClass decorate(ASTCDClass domainClass) {
    ASTCDClass builderClass = this.builderDecorator.decorate(domainClass);
    String builderClassName = builderClass.getName();

    ASTReferenceType superClass = createBuilderSuperClass(domainClass, builderClassName);
    builderClass.setSuperclass(superClass);

    List<ASTCDMethod> astCNodeMethods = new ArrayList<>();

    if (!hasSuperClassOtherThanASTCNode(domainClass)) {
      ASTType builderType = this.cdTypeFactory.createSimpleReferenceType(builderClassName);
      BuilderASTCNodeMethodDecorator builderASTCNodeMethodGenerator = new BuilderASTCNodeMethodDecorator(this.glex, builderType);
      astCNodeMethods.addAll(builderASTCNodeMethodGenerator.decorate());
    }

    builderClass.addAllCDMethods(astCNodeMethods);

    this.glex.bindHookPoint("<JavaBlock>:BuildMethod:init", new TemplateHookPoint("builder.ASTCNodeInit", domainClass));

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
}
