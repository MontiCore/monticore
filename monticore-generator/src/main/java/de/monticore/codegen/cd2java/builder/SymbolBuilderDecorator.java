package de.monticore.codegen.cd2java.builder;

import de.monticore.codegen.cd2java.Decorator;
import de.monticore.codegen.cd2java.exception.DecorateException;
import de.monticore.codegen.cd2java.factories.CDAttributeFactory;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
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

    return this.builderDecorator.decorate(decoratedSymbolClass);
  }

  private List<ASTCDAttribute> createSymbolAttributes() {
    ASTCDAttribute name = this.cdAttributeFactory.createAttributeByDefinition("private String name;");
    ASTCDAttribute enclosingScope = this.cdAttributeFactory.createAttributeByDefinition("private Scope enclosingScope;");
    ASTCDAttribute node = this.cdAttributeFactory.createAttributeByDefinition("private ASTNode node;");
    ASTCDAttribute kind = this.cdAttributeFactory.createAttributeByDefinition("private SymbolKind kind;");
    ASTCDAttribute accessModifier = this.cdAttributeFactory.createAttributeByDefinition("private AccessModifier accessModifier;");
    return new ArrayList<>(Arrays.asList(name, enclosingScope, node, kind, accessModifier));
  }
}
