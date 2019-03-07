package de.monticore.codegen.cd2java.symboltable;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.builder.BuilderDecorator;
import de.monticore.codegen.cd2java.factories.CDAttributeFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.builder.BuilderDecorator.BUILD_INIT_TEMPLATE;
import static de.monticore.codegen.cd2java.builder.BuilderDecorator.BUILD_METHOD;
import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;

public class SymbolBuilderDecorator implements Decorator<ASTCDClass, ASTCDClass> {

  private static final String SYMBOL_BUILD_INIT_TEMPLATE = "symboltable_new.builder.SymbolInit";

  private final GlobalExtensionManagement glex;

  private final BuilderDecorator builderDecorator;

  private final CDAttributeFactory cdAttributeFactory;

  public SymbolBuilderDecorator(
      final GlobalExtensionManagement glex,
      final BuilderDecorator builderDecorator) {
    this.glex = glex;
    this.builderDecorator = builderDecorator;
    this.cdAttributeFactory = CDAttributeFactory.getInstance();
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass symbolClass) {
    ASTCDClass decoratedSymbolClass = symbolClass.deepClone();

    decoratedSymbolClass.addAllCDAttributes(createSymbolAttributes());
    decoratedSymbolClass.getCDMethodList().clear();

    ASTCDClass symbolBuilder = this.builderDecorator.decorate(decoratedSymbolClass);

    Optional<ASTCDMethod> buildMethod = symbolBuilder.getCDMethodList().stream().filter(m -> BUILD_METHOD.equals(m.getName())).findFirst();
    buildMethod.ifPresent(b ->
        this.glex.replaceTemplate(BUILD_INIT_TEMPLATE, b, new TemplateHookPoint(SYMBOL_BUILD_INIT_TEMPLATE, symbolBuilder)));

    return symbolBuilder;
  }

  private List<ASTCDAttribute> createSymbolAttributes() {
    ASTCDAttribute name = this.cdAttributeFactory.createAttribute(PRIVATE, String.class, "name");
    ASTCDAttribute enclosingScope = this.cdAttributeFactory.createAttribute(PRIVATE, Scope.class, "scope");
    ASTCDAttribute node = this.cdAttributeFactory.createAttribute(PRIVATE, ASTNode.class, "astNode");
    ASTCDAttribute accessModifier = this.cdAttributeFactory.createAttribute(PRIVATE, AccessModifier.class, "accessModifier");
    return new ArrayList<>(Arrays.asList(name, enclosingScope, node, accessModifier));
  }
}
