package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.factories.CDAttributeFactory;
import de.monticore.codegen.cd2java.factories.CDTypeFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.types._ast.ASTType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SymbolBuilderDecorator implements Decorator<ASTCDClass, ASTCDClass> {

  private final GlobalExtensionManagement glex;

  private final BuilderDecorator builderDecorator;

  private final CDTypeFactory cdTypeFactory;

  private final CDAttributeFactory cdAttributeFactory;

  public SymbolBuilderDecorator(
      final GlobalExtensionManagement glex,
      final BuilderDecorator builderDecorator) {
    this.glex = glex;
    this.builderDecorator = builderDecorator;
    this.cdTypeFactory = CDTypeFactory.getInstance();
    this.cdAttributeFactory = CDAttributeFactory.getInstance();
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass symbolClass) {
    ASTCDClass decoratedSymbolClass = symbolClass.deepClone();

    decoratedSymbolClass.addAllCDAttributes(createSymbolAttributes());
    decoratedSymbolClass.getCDMethodList().clear();

    this.glex.bindHookPoint("<JavaBlock>:BuildMethod:init", new TemplateHookPoint("builder.SymbolInit", decoratedSymbolClass));

    return this.builderDecorator.decorate(decoratedSymbolClass);
  }

  private List<ASTCDAttribute> createSymbolAttributes() {
    ASTType stringType = this.cdTypeFactory.createSimpleReferenceType("String");
    ASTCDAttribute name = this.cdAttributeFactory.createPrivateAttribute(stringType, "name");

    ASTType scopeType = this.cdTypeFactory.createSimpleReferenceType("Scope");
    ASTCDAttribute enclosingScope = this.cdAttributeFactory.createPrivateAttribute(scopeType, "scope");

    ASTType nodeType = this.cdTypeFactory.createSimpleReferenceType("ASTNode");
    ASTCDAttribute node = this.cdAttributeFactory.createPrivateAttribute(nodeType, "astNode");

    ASTType accessModifierType = this.cdTypeFactory.createSimpleReferenceType("AccessModifier");
    ASTCDAttribute accessModifier = this.cdAttributeFactory.createPrivateAttribute(accessModifierType, "accessModifier");

    return new ArrayList<>(Arrays.asList(name, enclosingScope, node, accessModifier));
  }
}
