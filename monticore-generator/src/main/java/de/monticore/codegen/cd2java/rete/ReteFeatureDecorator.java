/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.rete;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cd4codebasis._ast.ASTCDMethod;
import de.monticore.cdbasis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static de.monticore.cd.codegen.CD2JavaTemplates.EMPTY_BODY;
import static de.monticore.cd.codegen.CD2JavaTemplates.JAVADOC;
import static de.monticore.cd.codegen.CD2JavaTemplates.VALUE;
import static de.monticore.cd.facade.CDModifier.*;
import static de.monticore.codegen.cd2java.rete.ReteConstants.*;

/**
 * creates the class containing one de.monticore.rete.Feature constant per
 * attribute of the AST classes and interfaces of a grammar, e.g.
 * {@code public static final Feature<ASTState, String> STATE_NAME = ...}
 * <p>
 * Attributes that repeat an attribute of a super type (e.g. attributes of an
 * interface production in the implementing classes) get no feature of their
 * own: the feature of the super type is used for all its subtypes.
 */
public class ReteFeatureDecorator extends AbstractCreator<ASTCDCompilationUnit, ASTCDClass> {

  protected final ReteService reteService;

  public ReteFeatureDecorator(final GlobalExtensionManagement glex,
                              final ReteService reteService) {
    super(glex);
    this.reteService = reteService;
  }

  public void decorate(ASTCDCompilationUnit input, ASTCDCompilationUnit decoratedCD) {
    ASTCDPackage retePackage = getPackage(input, decoratedCD, RETE_PACKAGE);
    retePackage.addCDElement(decorate(input));
  }

  @Override
  public ASTCDClass decorate(ASTCDCompilationUnit input) {
    String className = reteService.getFeaturesSimpleName();
    List<ASTCDAttribute> featureAttributes = createFeatureAttributes(input.getCDDefinition());

    ASTCDClass featureClass = CD4CodeMill.cDClassBuilder()
        .setModifier(PUBLIC.build())
        .setName(className)
        .addAllCDMembers(featureAttributes)
        .addCDMember(createAllFeaturesAttribute(featureAttributes))
        .addCDMember(createAllTransitiveFeaturesAttribute())
        .addCDMember(createResolveConstantMethod(input.getCDDefinition()))
        .addCDMember(createResolveConstantInSuperLanguagesMethod())
        .addCDMember(createConstructor(className))
        .build();

    replaceTemplate(JAVADOC, featureClass, new StringHookPoint("""
        /**
         * The features of the language {@code %s}: one typed key per attribute of its
         * AST classes and interfaces. Attribute values of AST nodes are reported to a
         * {@link %s} under these keys.
         * <p>
         * Attributes inherited from super languages are reported under the features
         * of the super language. {@code %s} contains the features of this language
         * only, {@code %s} additionally those of all super languages.
         */
        """.formatted(reteService.getCDName(), RETE_NETWORK_TYPE, ALL_FEATURES, ALL_TRANSITIVE_FEATURES)));

    return featureClass;
  }

  /**
   * creates the feature constants of all attributes of the AST interfaces and
   * classes of the class diagram, interfaces first
   */
  protected List<ASTCDAttribute> createFeatureAttributes(ASTCDDefinition cdDefinition) {
    Map<String, ASTCDAttribute> featureAttributes = new LinkedHashMap<>();
    for (ASTCDType type : reteService.getFeatureTypes(cdDefinition)) {
      for (ASTCDAttribute attribute : reteService.getFeatureAttributes(type)) {
        String constantName = reteService.getFeatureConstantName(type, attribute);
        if (featureAttributes.containsKey(constantName)) {
          Log.error("0xA5C10 The feature constant " + constantName + " of attribute "
              + attribute.getName() + " of " + type.getName() + " clashes with another feature constant.",
              attribute.get_SourcePositionStart());
          continue;
        }
        featureAttributes.put(constantName, createFeatureAttribute(constantName, type, attribute,
            reteService.getFeatureValueType(attribute).get()));
      }
    }
    return new ArrayList<>(featureAttributes.values());
  }

  /**
   * creates e.g. {@code public static final Feature<ASTState, String> STATE_NAME =
   * Feature.of("name", ASTState.class, String.class);}
   */
  protected ASTCDAttribute createFeatureAttribute(String constantName, ASTCDType type,
                                                 ASTCDAttribute attribute, String valueType) {
    String ownerType = reteService.createASTFullName(type.getSymbol());
    String featureName = getDecorationHelper().getNativeAttributeName(attribute.getName());
    ASTCDAttribute featureAttribute = getCDAttributeFacade().createAttribute(PUBLIC_STATIC_FINAL.build(),
        getMCTypeFacade().createBasicGenericTypeOf(FEATURE_TYPE, ownerType, valueType), constantName);
    this.replaceTemplate(VALUE, featureAttribute, new StringHookPoint("= " + FEATURE_TYPE + ".of(\""
        + featureName + "\", " + ownerType + ".class, " + valueType + ".class)"));
    return featureAttribute;
  }

  /**
   * creates {@code public static final List<Feature<?, ?>> ALL = List.of(...);}
   */
  protected ASTCDAttribute createAllFeaturesAttribute(List<ASTCDAttribute> featureAttributes) {
    ASTCDAttribute allAttribute = getCDAttributeFacade().createAttribute(PUBLIC_STATIC_FINAL.build(),
        createFeatureListType(), ALL_FEATURES);
    String features = featureAttributes.stream()
        .map(ASTCDAttribute::getName)
        .collect(Collectors.joining(", "));
    this.replaceTemplate(VALUE, allAttribute, new StringHookPoint("= java.util.List.of(" + features + ")"));
    return allAttribute;
  }

  /**
   * creates {@code public static final List<Feature<?, ?>> ALL_TRANSITIVE}
   * containing the features of this language and of all its (transitive)
   * super languages; as every features class lists only its own features in
   * ALL, each feature is contained exactly once, even for diamond inheritance
   */
  protected ASTCDAttribute createAllTransitiveFeaturesAttribute() {
    ASTCDAttribute allTransitiveAttribute = getCDAttributeFacade().createAttribute(PUBLIC_STATIC_FINAL.build(),
        createFeatureListType(), ALL_TRANSITIVE_FEATURES);
    List<String> featureLists = new ArrayList<>();
    featureLists.add(ALL_FEATURES);
    for (DiagramSymbol superCD : reteService.getSuperCDsTransitive()) {
      featureLists.add(reteService.getFeaturesFullName(superCD) + "." + ALL_FEATURES);
    }
    this.replaceTemplate(VALUE, allTransitiveAttribute, new StringHookPoint("= java.util.stream.Stream.of("
        + String.join(", ", featureLists) + ").flatMap(java.util.List::stream).toList()"));
    return allTransitiveAttribute;
  }

  /**
   * creates {@code public static Optional<String> resolveConstant(String astType, String attributeName)}:
   * the qualified name of the feature constant of the attribute (named as in the grammar, i.e.
   * without list suffix) of an AST type of this language, incl. inherited attributes, e.g.
   * ("automata._ast.ASTState", "initial") -> "automata._rete.AutomataReteFeatures.STATE_INITIAL".
   * Meant for generators that generate code using the feature constants.
   */
  protected ASTCDMethod createResolveConstantMethod(ASTCDDefinition cdDefinition) {
    ASTCDMethod method = getCDMethodFacade().createMethod(PUBLIC_STATIC.build(),
        createOptionalStringType(), RESOLVE_CONSTANT,
        getCDParameterFacade().createParameter("String", "astType"),
        getCDParameterFacade().createParameter("String", "attributeName"));

    // types of this language list all their visible attributes, so an unknown attribute has no
    // feature; types of other languages may be known by the super languages
    StringBuilder body = new StringBuilder("return switch (astType) {\n");
    createResolveConstantCases(cdDefinition).forEach((astType, attributes) -> {
      body.append("  case \"").append(astType).append("\" -> switch (attributeName) {\n");
      attributes.forEach((attributeName, result) -> body.append("    case \"").append(attributeName)
          .append("\" -> ").append(result).append(";\n"));
      body.append("    default -> java.util.Optional.empty();\n  };\n");
    });
    body.append("  default -> ").append(RESOLVE_CONSTANT_IN_SUPER_LANGUAGES)
        .append("(astType, attributeName);\n};");
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint(body.toString()));

    replaceTemplate(JAVADOC, method, new StringHookPoint("""
        /**
         * Resolves the qualified name of the feature constant of an attribute, e.g.
         * ("automata._ast.ASTState", "initial") -> "automata._rete.AutomataReteFeatures.STATE_INITIAL".
         * Inherited attributes are resolved to the feature of the (super) language declaring them.
         *
         * @param astType the full name of the AST type, e.g. automata._ast.ASTState
         * @param attributeName the name of the attribute as in the grammar, i.e. without list suffix
         * @return the qualified name of the feature constant, empty if there is no feature
         */
        """));
    return method;
  }

  /**
   * @return the cases of resolveConstant: astType -> attributeName -> result, for all AST types of
   * this language and all their (also inherited) attributes. The result is the qualified name of a
   * feature constant of this language, or the delegation to the super languages for attributes
   * whose feature is declared by a super language. Types without such attributes are omitted.
   */
  protected Map<String, Map<String, String>> createResolveConstantCases(ASTCDDefinition cdDefinition) {
    // the feature constants of this language: AST type -> attribute name -> constant
    Map<String, Map<String, String>> ownConstants = new LinkedHashMap<>();
    for (ASTCDType type : reteService.getFeatureTypes(cdDefinition)) {
      Map<String, String> constants = new LinkedHashMap<>();
      for (ASTCDAttribute attribute : reteService.getFeatureAttributes(type)) {
        constants.put(getNativeName(reteService.getSymbolName(attribute)),
            reteService.getFeaturesFullName() + "." + reteService.getFeatureConstantName(type, attribute));
      }
      ownConstants.put(reteService.createASTFullName(type.getSymbol()), constants);
    }

    Map<String, Map<String, String>> cases = new LinkedHashMap<>();
    for (ASTCDType type : reteService.getFeatureTypes(cdDefinition)) {
      Map<String, String> typeCases = new LinkedHashMap<>();
      List<TypeSymbol> typeAndSuperTypes = new ArrayList<>();
      typeAndSuperTypes.add(type.getSymbol());
      typeAndSuperTypes.addAll(reteService.getSuperTypesTransitive(type.getSymbol()));

      Set<String> attributeNames = new TreeSet<>();
      typeAndSuperTypes.forEach(t -> attributeNames.addAll(reteService.getAttributeNames(t)));

      for (String attributeName : attributeNames) {
        Optional<TypeSymbol> declaringType = getDeclaringType(typeAndSuperTypes, attributeName);
        if (declaringType.isEmpty()) {
          continue;
        }
        String declaringASTType = reteService.createASTFullName(declaringType.get());
        String nativeName = getNativeName(attributeName);
        if (ownConstants.containsKey(declaringASTType)) {
          String constant = ownConstants.get(declaringASTType).get(nativeName);
          if (constant != null) {
            typeCases.put(nativeName, "java.util.Optional.of(\"" + constant + "\")");
          }
        }
        else {
          typeCases.put(nativeName, RESOLVE_CONSTANT_IN_SUPER_LANGUAGES + "(\"" + declaringASTType
              + "\", \"" + nativeName + "\")");
        }
      }
      if (!typeCases.isEmpty()) {
        cases.put(reteService.createASTFullName(type.getSymbol()), typeCases);
      }
    }
    return cases;
  }

  /**
   * @return the type whose feature applies to the attribute: the type (or super type) declaring
   * the attribute without a super type declaring it as well
   */
  protected Optional<TypeSymbol> getDeclaringType(List<TypeSymbol> typeAndSuperTypes,
                                                  String attributeName) {
    return typeAndSuperTypes.stream()
        .filter(type -> reteService.getAttributeNames(type).contains(attributeName))
        .filter(type -> reteService.getSuperTypesTransitive(type).stream()
            .noneMatch(superType -> reteService.getAttributeNames(superType).contains(attributeName)))
        .findFirst();
  }

  /** e.g. r__final -> final */
  protected String getNativeName(String attributeName) {
    return getDecorationHelper().getNativeAttributeName(attributeName);
  }

  /**
   * creates {@code protected static Optional<String> resolveConstantInSuperLanguages(String astType,
   * String attributeName)}, which asks the direct super languages one after another (these
   * delegate to their super languages)
   */
  protected ASTCDMethod createResolveConstantInSuperLanguagesMethod() {
    ASTCDMethod method = getCDMethodFacade().createMethod(PROTECTED_STATIC.build(),
        createOptionalStringType(), RESOLVE_CONSTANT_IN_SUPER_LANGUAGES,
        getCDParameterFacade().createParameter("String", "astType"),
        getCDParameterFacade().createParameter("String", "attributeName"));

    StringBuilder body = new StringBuilder();
    if (!reteService.getSuperCDsDirect().isEmpty()) {
      body.append("java.util.Optional<String> constant;\n");
      for (DiagramSymbol superCD : reteService.getSuperCDsDirect()) {
        body.append("constant = ").append(reteService.getFeaturesFullName(superCD)).append(".")
            .append(RESOLVE_CONSTANT).append("(astType, attributeName);\n")
            .append("if (constant.isPresent()) {\n  return constant;\n}\n");
      }
    }
    body.append("return java.util.Optional.empty();");
    replaceTemplate(EMPTY_BODY, method, new StringHookPoint(body.toString()));
    return method;
  }

  /**
   * @return {@code java.util.Optional<String>}
   */
  protected ASTMCType createOptionalStringType() {
    return getMCTypeFacade().createBasicGenericTypeOf("java.util.Optional", "String");
  }

  /**
   * @return {@code java.util.List<de.monticore.rete.Feature<?, ?>>}
   */
  protected ASTMCType createFeatureListType() {
    ASTMCType anyFeatureType = getMCTypeFacade().createBasicGenericTypeOf(FEATURE_TYPE,
        getMCTypeFacade().createWildCardWithNoBounds(), getMCTypeFacade().createWildCardWithNoBounds());
    return getMCTypeFacade().createBasicGenericTypeOf("java.util.List",
        CD4CodeMill.mCCustomTypeArgumentBuilder().setMCType(anyFeatureType).build());
  }

  protected ASTCDConstructor createConstructor(String className) {
    return getCDConstructorFacade().createConstructor(PROTECTED.build(), className);
  }
}
