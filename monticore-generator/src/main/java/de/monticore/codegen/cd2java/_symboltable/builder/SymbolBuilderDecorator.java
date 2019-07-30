package de.monticore.codegen.cd2java._symboltable.builder;

import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractDecorator;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.modifiers.AccessModifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static de.monticore.codegen.cd2java.factories.CDModifier.PRIVATE;

public class SymbolBuilderDecorator extends AbstractDecorator<ASTCDClass, ASTCDClass> {

  private static final String SYMBOL_BUILD_INIT_TEMPLATE = "_symboltable.builder.SymbolInit";

  private final BuilderDecorator builderDecorator;

  public SymbolBuilderDecorator(final GlobalExtensionManagement glex, final BuilderDecorator builderDecorator) {
    super(glex);
    this.builderDecorator = builderDecorator;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass symbolClass) {
    ASTCDClass decoratedSymbolClass = symbolClass.deepClone();

    decoratedSymbolClass.addAllCDAttributes(createSymbolAttributes());
    decoratedSymbolClass.getCDMethodList().clear();

    ASTCDClass symbolBuilder = builderDecorator.decorate(decoratedSymbolClass);

    Optional<ASTCDMethod> buildMethod = symbolBuilder.getCDMethodList().stream().filter(m -> BuilderDecorator.BUILD_METHOD.equals(m.getName())).findFirst();
    buildMethod.ifPresent(b ->
        this.replaceTemplate(BuilderDecorator.BUILD_INIT_TEMPLATE, b, new TemplateHookPoint(SYMBOL_BUILD_INIT_TEMPLATE, symbolBuilder)));

    return symbolBuilder;
  }

  protected List<ASTCDAttribute> createSymbolAttributes() {
    ASTCDAttribute name = this.getCDAttributeFacade().createAttribute(PRIVATE, String.class, "name");
    ASTCDAttribute enclosingScope = this.getCDAttributeFacade().createAttribute(PRIVATE, Scope.class, "scope");
    ASTCDAttribute node = this.getCDAttributeFacade().createAttribute(PRIVATE, ASTNode.class, "astNode");
    ASTCDAttribute accessModifier = this.getCDAttributeFacade().createAttribute(PRIVATE, AccessModifier.class, "accessModifier");
    return new ArrayList<>(Arrays.asList(name, enclosingScope, node, accessModifier));
  }
}
