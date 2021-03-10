/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import com.google.common.collect.Lists;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.BuilderConstants;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._ast.builder.buildermethods.BuilderMutatorMethodDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.codegen.cd2java.CDModifier.PROTECTED;
import static de.monticore.codegen.cd2java.CDModifier.PUBLIC;
import static de.monticore.codegen.cd2java.CoreTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_METHOD;
import static de.monticore.codegen.cd2java._symboltable.SymbolTableConstants.*;

public class SymbolBuilderDecorator extends AbstractCreator<ASTCDClass, ASTCDClass> {

  protected final SymbolTableService symbolTableService;

  protected final BuilderDecorator builderDecorator;

  protected static final String TEMPLATE_PATH = "_symboltable.symbol.";

  public SymbolBuilderDecorator(final GlobalExtensionManagement glex,
                                final SymbolTableService symbolTableService,
                                final BuilderDecorator builderDecorator) {
    super(glex);
    this.symbolTableService = symbolTableService;
    this.builderDecorator = builderDecorator;
  }

  @Override
  public ASTCDClass decorate(final ASTCDClass symbolClass) {
    ASTCDClass decoratedSymbolClass = symbolClass.deepClone();
    decoratedSymbolClass.setName(symbolTableService.getNameWithSymbolSuffix(symbolClass));
    decoratedSymbolClass.getCDMethodList().clear();
    boolean hasInheritedSymbol = symbolTableService.hasInheritedSymbolStereotype(symbolClass.getModifier());
    boolean hasInheritedScope = symbolTableService.hasInheritedScopeStereotype(symbolClass.getModifier());
    boolean hasScope = symbolTableService.hasScopeStereotype(symbolClass.getModifier());
    List<ASTCDAttribute> defaultAttrs = createSymbolAttributes(symbolClass);
    if (!hasInheritedSymbol) {
      decoratedSymbolClass.addAllCDMembers(defaultAttrs);
    }
    if (hasScope || hasInheritedScope) {
      ASTCDAttribute spannedScopeAttr = getCDAttributeFacade()
              .createAttribute(PROTECTED.build(), symbolTableService.getScopeInterfaceType(), SPANNED_SCOPE_VAR);
      if (!hasInheritedSymbol ||
              (!hasInheritedScope && hasScope)) {
        decoratedSymbolClass.addCDMember(spannedScopeAttr);
      } else {
        defaultAttrs.add(spannedScopeAttr);
      }
    }

    builderDecorator.setPrintBuildMethodTemplate(false);
    ASTCDClass symbolBuilder = builderDecorator.decorate(decoratedSymbolClass);
    builderDecorator.setPrintBuildMethodTemplate(true);

    if (hasInheritedSymbol) {
      // set superclass
      Map<ASTCDClass, String> values = symbolTableService.getInheritedSymbolPropertyClasses(Lists.newArrayList(symbolClass));
      String value = values.getOrDefault(symbolClass, "");
      if (!value.isEmpty()) {
        symbolBuilder.setSuperclass(getMCTypeFacade().createQualifiedType(value + BuilderConstants.BUILDER_SUFFIX));
      }
      // add delegate-Method
      // overwrite setters since they need to return the correct Builder type
      ASTMCType builderType = this.getMCTypeFacade().createQualifiedType(symbolBuilder.getName());
      BuilderMutatorMethodDecorator builderMutatorMethodDecorator =  new BuilderMutatorMethodDecorator(glex, builderType);
      symbolBuilder.addAllCDMembers(
              getMethodsForDefaultAttrs(defaultAttrs, builderMutatorMethodDecorator));
      // Override getScope-Methods
      symbolBuilder.addAllCDMembers(createScopeMethods(hasInheritedScope));
    }

    List<ASTCDAttribute> buildAttributes = Lists.newArrayList(symbolClass.getCDAttributeList());
    // builder has all attributes but not the spannedScope attribute from the symbol Class
    defaultAttrs.stream().filter(a -> !SPANNED_SCOPE_VAR.equals(a.getName())).forEach(a -> buildAttributes.add(a));
    // new build method template
    Optional<ASTCDMethod> buildMethod = symbolBuilder.getCDMethodList()
        .stream()
        .filter(m -> BUILD_METHOD.equals(m.getName()))
        .findFirst();
    buildMethod.ifPresent(b -> this.replaceTemplate(EMPTY_BODY, b,
        new TemplateHookPoint(TEMPLATE_PATH + "BuildSymbol", decoratedSymbolClass.getName(), buildAttributes)));

    return symbolBuilder;
  }

  protected List<ASTCDMethod> createScopeMethods(boolean hasInheritedSpannedScope) {
    String scopeInterface = symbolTableService.getScopeInterfaceFullName();
    List<ASTCDMethod> methods = Lists.newArrayList();

    // getEnclosingScope
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createQualifiedType(scopeInterface), "getEnclosingScope");
    String errorCode = symbolTableService.getGeneratedErrorCode(scopeInterface + "getEnclosingScope");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "GetScope",
            ENCLOSING_SCOPE_VAR, scopeInterface, errorCode));
    methods.add(method);

    // getSpannedScope
    if (hasInheritedSpannedScope) {
      method = getCDMethodFacade().createMethod(PUBLIC.build(), getMCTypeFacade().createQualifiedType(scopeInterface), "getSpannedScope");
      errorCode = symbolTableService.getGeneratedErrorCode(scopeInterface + "getSpannedScope");
      this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "GetScope",
              SPANNED_SCOPE_VAR, scopeInterface, errorCode));
      methods.add(method);
    }
    return methods;
  }

  protected List<ASTCDAttribute> createSymbolAttributes(ASTCDClass symbolClass) {
    List<ASTCDAttribute> attrs = Lists.newArrayList();
    attrs.add(this.getCDAttributeFacade().createAttribute(PROTECTED.build(), String.class, NAME_VAR));

    attrs.add(this.getCDAttributeFacade().createAttribute(PROTECTED.build(), String.class, FULL_NAME_VAR));

    attrs.add(this.getCDAttributeFacade().createAttribute(PROTECTED.build(), String.class, PACKAGE_NAME_VAR));

    ASTMCOptionalType optionalTypeOfASTNode = getMCTypeFacade().createOptionalTypeOf(
            symbolTableService.getASTPackage() + "." + AST_PREFIX + symbolClass.getName());
    attrs.add(this.getCDAttributeFacade().createAttribute(PROTECTED.build(), optionalTypeOfASTNode, AST_NODE_VAR));

    attrs.add(this.getCDAttributeFacade().createAttribute(PROTECTED.build(), ACCESS_MODIFIER, "accessModifier"));

    attrs.add(this.getCDAttributeFacade().createAttribute(PROTECTED.build(),
            symbolTableService.getScopeInterfaceFullName(), ENCLOSING_SCOPE_VAR));

    return attrs;
  }

  protected List<ASTCDMethod> getMethodsForDefaultAttrs(List<ASTCDAttribute> defaultAttrs,
                                                        BuilderMutatorMethodDecorator builderMutatorMethodDecorator) {
    return defaultAttrs.stream()
        .map(builderMutatorMethodDecorator::decorate)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

}
