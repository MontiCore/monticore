/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java._symboltable.symbol;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.codegen.cd2java._ast.builder.BuilderConstants;
import de.monticore.codegen.cd2java._ast.builder.BuilderDecorator;
import de.monticore.codegen.cd2java._ast.builder.buildermethods.BuilderMutatorMethodDecorator;
import de.monticore.codegen.cd2java._symboltable.SymbolTableService;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.generating.templateengine.TemplateHookPoint;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.VALUE;
import static de.monticore.cd.facade.CDModifier.PROTECTED;
import static de.monticore.cd.facade.CDModifier.PUBLIC;
import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.codegen.cd2java._ast.ast_class.ASTConstants.AST_PREFIX;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.BUILD_METHOD;
import static de.monticore.codegen.cd2java._ast.builder.BuilderConstants.REAL_BUILDER;
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

    addStereoinfoDefaultValue(symbolBuilder);

    if (hasInheritedSymbol) {
      // set superclass
      Map<ASTCDClass, String> values = symbolTableService.getInheritedSymbolPropertyClasses(Lists.newArrayList(symbolClass));
      String value = values.getOrDefault(symbolClass, "");
      if (!value.isEmpty()) {
        symbolBuilder.setCDExtendUsage(CD4CodeMill.cDExtendUsageBuilder().addSuperclass(getMCTypeFacade().createQualifiedType(value + BuilderConstants.BUILDER_SUFFIX)).build());
      }
      // add delegate-Method
      // overwrite setters since they need to return the correct Builder type
      ASTMCType builderType = this.getMCTypeFacade().createQualifiedType(symbolBuilder.getName());
      BuilderMutatorMethodDecorator builderMutatorMethodDecorator =  new BuilderMutatorMethodDecorator(glex, symbolTableService, builderType);
      symbolBuilder.addAllCDMembers(
              getMethodsForDefaultAttrs(defaultAttrs, builderMutatorMethodDecorator));
      // Override getScope-Methods
      symbolBuilder.addAllCDMembers(createScopeMethods(hasInheritedScope));
    }

    ASTMCType builderType = getMCTypeFacade().createQualifiedType(symbolBuilder.getName());
    symbolBuilder.addAllCDMembers(createStereoinfoConvenienceMethods(builderType));

    Set<ASTCDAttribute> buildAttributes = Sets.newLinkedHashSet(decoratedSymbolClass.getCDAttributeList());

    // builder has all attributes
    buildAttributes.addAll(defaultAttrs);

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

    ASTCDAttribute accessModifier = this.getCDAttributeFacade().createAttribute(PROTECTED.build(), ACCESS_MODIFIER, "accessModifier");
    this.replaceTemplate(VALUE, accessModifier, new StringHookPoint("= " + ACCESS_MODIFIER_ALL_INCLUSION));
    attrs.add(accessModifier);

    ASTMCType symbolicStereotype = getMCTypeFacade().createQualifiedType(I_STEREOTYPE_REFERENCE);
    ASTMCType valueOptional = getMCTypeFacade().createOptionalTypeOf(INTERPRETER_VALUE);
    ASTMCType stereotypeMap = getMCTypeFacade().createMapTypeOf(symbolicStereotype, valueOptional);
    ASTCDAttribute stereotypes =
      this.getCDAttributeFacade().createAttribute(PROTECTED.build(), stereotypeMap, STEREOINFO_VAR);
    attrs.add(stereotypes);

    attrs.add(this.getCDAttributeFacade().createAttribute(PROTECTED.build(),
            symbolTableService.getScopeInterfaceFullName(), ENCLOSING_SCOPE_VAR));

    return attrs;
  }

  protected void addStereoinfoDefaultValue(ASTCDClass builder) {
    HookPoint defaultVal = new StringHookPoint("= new java.util.HashMap<>()");

    builder.getCDAttributeList().stream()
      .filter(a -> STEREOINFO_VAR.equals(a.getName()))
      .forEach(s -> this.replaceTemplate(VALUE, s, defaultVal));
  }

  protected List<ASTCDMethod> getMethodsForDefaultAttrs(List<ASTCDAttribute> defaultAttrs,
                                                        BuilderMutatorMethodDecorator builderMutatorMethodDecorator) {
    return defaultAttrs.stream()
        .map(builderMutatorMethodDecorator::decorate)
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  /**
   * Creates methods {@code Builder addStereoinfo(IStereotypeSymbol)} and
   * {@code Builder addStereoinfo(IStereotypeSymbol, Value)}.<p>
   * Stereoinfo is a map and by default only a getter and setter for it would be generated. These
   * alone can be uncomfortable to use in a builder setting. Thus this method provides the named,
   * more convenient, accessors.
   */
  protected List<ASTCDMethod> createStereoinfoConvenienceMethods(ASTMCType builderType) {
    ASTMCType stereotypeMCType = getMCTypeFacade().createQualifiedType(I_STEREOTYPE_REFERENCE);
    ASTMCType valueMCType = getMCTypeFacade().createQualifiedType(INTERPRETER_VALUE);

    ASTCDMethod addWithoutValue = getCDMethodFacade().createMethod(
      PUBLIC.build(),
      builderType,
      "addStereoinfo",
      getCDParameterFacade().createParameter(stereotypeMCType, "stereotype")
    );
    String withoutValueCode =
      "this." + STEREOINFO_VAR + ".put(stereotype, java.util.Optional.empty());\n" +
      "return this." + REAL_BUILDER + ";";
    this.replaceTemplate(EMPTY_BODY, addWithoutValue, new StringHookPoint(withoutValueCode));

    ASTCDMethod addWithValue = getCDMethodFacade().createMethod(
      PUBLIC.build(),
      builderType,
      "addStereoinfo",
      getCDParameterFacade().createParameter(stereotypeMCType, "stereotype"),
      getCDParameterFacade().createParameter(valueMCType, "value")
    );
    String withValueCode =
      "this." + STEREOINFO_VAR + ".put(stereotype, java.util.Optional.of(value));\n" +
      "return this." + REAL_BUILDER + ";";
    this.replaceTemplate(EMPTY_BODY, addWithValue, new StringHookPoint(withValueCode));

    return Arrays.asList(addWithoutValue, addWithValue);
  }

}
