/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java.rete;

import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4codebasis._ast.ASTCDConstructor;
import de.monticore.cdbasis._ast.*;
import de.monticore.codegen.cd2java.AbstractCreator;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.symbols.basicsymbols._symboltable.DiagramSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
