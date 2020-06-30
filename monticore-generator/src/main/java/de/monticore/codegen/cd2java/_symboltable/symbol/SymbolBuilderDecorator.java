/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import com.google.common.collect.Lists;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDMethod;
import de.monticore.cd.cd4analysis._ast.ASTCDParameter;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.BuilderConstants;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCGenericType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mcfullgenerictypes.MCFullGenericTypesMill;
import de.se_rwth.commons.StringTransformations;

import java.util.*;
import java.util.stream.Collectors;

import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
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
    boolean isInherited = symbolTableService.hasInheritedSymbolStereotype(symbolClass.getModifier());
    List<ASTCDAttribute> defaultAttrs = createSymbolAttributes(symbolClass);
    if (!isInherited) {
      decoratedSymbolClass.addAllCDAttributes(defaultAttrs);
    }

    builderDecorator.setPrintBuildMethodTemplate(false);
    ASTCDClass symbolBuilder = builderDecorator.decorate(decoratedSymbolClass);
    builderDecorator.setPrintBuildMethodTemplate(true);

    if (isInherited) {
      // set superclass
      Map<ASTCDClass, String> values = symbolTableService.getInheritedSymbolPropertyClasses(Lists.newArrayList(symbolClass));
      String value = values.getOrDefault(symbolClass, "");
      if (!value.isEmpty()) {
        symbolBuilder.setSuperclass(getMCTypeFacade().createQualifiedType(value + BuilderConstants.BUILDER_SUFFIX));
      }
      // add delegate-Method
      symbolBuilder.addAllCDMethods(
              getMethodsForDefaultAttrs(defaultAttrs, this.getMCTypeFacade().createQualifiedType(symbolBuilder.getName())));
      // Override getScope-Methods
      boolean hasInheritedSpannedScope = symbolClass.isPresentModifier() &&
              (symbolTableService.hasScopeStereotype(symbolClass.getModifier())
                      || symbolTableService.hasInheritedScopeStereotype(symbolClass.getModifier()));
      symbolBuilder.addAllCDMethods(createScopeMethods(hasInheritedSpannedScope));
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
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(scopeInterface), "getEnclosingScope");
    String errorCode = symbolTableService.getGeneratedErrorCode(scopeInterface + "getEnclosingScope");
    this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "GetScope",
            ENCLOSING_SCOPE_VAR, scopeInterface, errorCode));
    methods.add(method);

    // getSpannedScope
    if (hasInheritedSpannedScope) {
      method = getCDMethodFacade().createMethod(PUBLIC, getMCTypeFacade().createQualifiedType(scopeInterface), "getSpannedScope");
      errorCode = symbolTableService.getGeneratedErrorCode(scopeInterface + "getSpannedScope");
      this.replaceTemplate(EMPTY_BODY, method, new TemplateHookPoint(TEMPLATE_PATH + "GetScope",
              SPANNED_SCOPE_VAR, scopeInterface, errorCode));
      methods.add(method);
    }
    return methods;

  }

  protected HookPoint createImplementation(final ASTCDMethod method) {
    String methodName = method.getName();
    String parameterCall = method.getCDParameterList().stream()
            .map(ASTCDParameter::getName)
            .collect(Collectors.joining(", "));
    return new TemplateHookPoint(TEMPLATE_PATH + "DefaultDelegate", methodName, parameterCall);
  }

  protected List<ASTCDAttribute> createSymbolAttributes(ASTCDClass symbolClass) {
    List<ASTCDAttribute> attrs = Lists.newArrayList();
    attrs.add(this.getCDAttributeFacade().createAttribute(PROTECTED, String.class, NAME_VAR));

    attrs.add(this.getCDAttributeFacade().createAttribute(PROTECTED, String.class, FULL_NAME_VAR));

    attrs.add(this.getCDAttributeFacade().createAttribute(PROTECTED, String.class, PACKAGE_NAME_VAR));

    ASTMCOptionalType optionalTypeOfASTNode = getMCTypeFacade().createOptionalTypeOf(
            symbolTableService.getASTPackage() + "." + AST_PREFIX + symbolClass.getName());
    attrs.add(this.getCDAttributeFacade().createAttribute(PROTECTED, optionalTypeOfASTNode, AST_NODE_VAR));

    attrs.add(this.getCDAttributeFacade().createAttribute(PROTECTED, ACCESS_MODIFIER, "accessModifier"));

    attrs.add(this.getCDAttributeFacade().createAttribute(PROTECTED,
            symbolTableService.getScopeInterfaceFullName(), ENCLOSING_SCOPE_VAR));

    // add only for scope spanning symbols
    if (symbolClass.isPresentModifier() && (symbolTableService.hasScopeStereotype(symbolClass.getModifier())
            || symbolTableService.hasInheritedScopeStereotype(symbolClass.getModifier()))) {
      attrs.add(this.getCDAttributeFacade().createAttribute(PROTECTED,
              symbolTableService.getScopeInterfaceFullName(), SPANNED_SCOPE_VAR));
    }
    return attrs;
  }

  protected Collection<? extends ASTCDMethod> getMethodsForDefaultAttrs(List<ASTCDAttribute> defaultAttrs,
                                                                        ASTMCType builderType) {
    String ret = builderType.printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
    List<ASTCDMethod> result = new ArrayList<>();
    for (ASTCDAttribute defaultAttr: defaultAttrs) {
      String type;
      if (defaultAttr.getMCType() instanceof ASTMCGenericType) {
        type = ((ASTMCGenericType) defaultAttr.getMCType()).
                getMCTypeArgument(0).printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
      } else {
        type = defaultAttr.getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
      }
      String signature = "public " + ret + " set" + StringTransformations.capitalize(defaultAttr.getName()) + "(" +
              type + " " + defaultAttr.getName() + ");";
      ASTCDMethod method = this.getCDMethodFacade().createMethodByDefinition(signature);
      this.replaceTemplate(EMPTY_BODY, method, createImplementation(method));
      result.add(method);
    }
    return result;
  }

}
