/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import com.google.common.collect.Lists;
import de.monticore.ast.ASTNode;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cdbasis._ast.ASTCDAttribute;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.cd2java.DecorationHelper;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.utils.Link;
import de.se_rwth.commons.StringTransformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static de.monticore.codegen.mc2cd.TransformationHelper.getName;
import static de.monticore.codegen.mc2cd.TransformationHelper.getUsageName;

/**
 * The CDAttributes generated from AttributeInASTs completely hide any CDAttributes derived from
 * NonTerminals.
 */
public class RemoveOverriddenAttributesTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTClassProd, ASTCDClass> classLink : rootLink.getLinks(ASTClassProd.class,
        ASTCDClass.class)) {

      for (Link<ASTNode, ASTCDAttribute> link : classLink.getLinks(ASTNode.class, ASTCDAttribute.class)) {
        if (isOverridden(link, classLink) && isNotInherited(link.target())) {
          ASTCDAttribute target = link.target();
          classLink.target().removeCDMember(target);
        }
      }
    }
    return rootLink;
  }

  protected boolean isOverridden(Link<ASTNode, ASTCDAttribute> link, Link<?, ASTCDClass> classLink) {
    ASTNode source = link.source();
    Optional<String> usageName = getUsageName(classLink.source(), source);
    if (!usageName.isPresent()) {
      Optional<String> name = getName(source);
      if (name.isPresent()) {
        usageName = Optional.ofNullable(StringTransformations.uncapitalize(name.get()));
      }
    }
    Set<ASTAdditionalAttribute> attributesInASTLinkingToSameClass = attributesInASTLinkingToSameClass(
        classLink);
    attributesInASTLinkingToSameClass.remove(source);

    List<ASTAdditionalAttribute> attributes = new ArrayList<>();
    if (usageName.isPresent()) {
      String name = usageName.get();
      attributes = attributesInASTLinkingToSameClass.stream()
          .filter(ASTAdditionalAttribute::isPresentName)
          .filter(x -> name.equals(x.getName()))
          .collect(Collectors.toList());
    }

    boolean matchByUsageName = !attributes.isEmpty();
    boolean matchByTypeName = false;
    if (!matchByUsageName && !usageName.isPresent()) {
      for (ASTAdditionalAttribute attributeInAST : attributesInASTLinkingToSameClass) {
        if (!attributeInAST.isPresentName()) {
          String name = Grammar_WithConceptsMill.prettyPrint(attributeInAST.getMCType(), false);
          if (getName(source).orElse("").equals(name)) {
            attributes = Lists.newArrayList(attributeInAST);
            matchByTypeName = true;
            break;
          }
        }
      }
    }

    // add derived stereotype if needed
    if (hasDerivedStereotype(link.target().getModifier()) &&
        DecorationHelper.getInstance().isListType(CD4CodeMill.prettyPrint(link.target(), false))) {
      for (Link<ASTAdditionalAttribute, ASTCDAttribute> attributeLink :
          classLink.rootLink().getLinks(ASTAdditionalAttribute.class, ASTCDAttribute.class)) {
        if(attributes.contains(attributeLink.source())){
          addDerivedStereotypeToAttributes(attributeLink.target());
        }
      }
    }

    return matchByUsageName || matchByTypeName;
  }

  protected boolean hasDerivedStereotype(ASTModifier modifier) {
    if (modifier.isPresentStereotype()) {
      return modifier.getStereotype().getValuesList()
          .stream()
          .anyMatch(v -> v.getName().equals(MC2CDStereotypes.DERIVED_ATTRIBUTE_NAME.toString()));
    }
    return false;
  }

  protected void addDerivedStereotypeToAttributes(ASTCDAttribute attribute) {
    TransformationHelper.addStereoType(attribute,
        MC2CDStereotypes.DERIVED_ATTRIBUTE_NAME.toString(), "");
  }

  protected Set<ASTAdditionalAttribute> attributesInASTLinkingToSameClass(Link<?, ASTCDClass> link) {
    return link.rootLink().getLinks(ASTNode.class, ASTCDClass.class).stream()
        .filter(attributeLink -> attributeLink.target() == link.target())
        .flatMap(astRuleLink ->
            astRuleLink.getLinks(ASTAdditionalAttribute.class, ASTCDAttribute.class).stream())
        .map(Link::source).collect(Collectors.toSet());
  }

  protected boolean isNotInherited(ASTCDAttribute cdAttribute) {
    if (!cdAttribute.getModifier().isPresentStereotype()) {
      return true;
    }
    return cdAttribute.getModifier().getStereotype().getValuesList().stream()
        .map(ASTStereoValue::getName)
        .noneMatch(MC2CDStereotypes.INHERITED.toString()::equals);
  }
}
