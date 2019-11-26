/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.ast.ASTNode;
import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.cd.cd4analysis._ast.ASTCDStereoValue;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.types.mcfullgenerictypes._ast.MCFullGenericTypesMill;
import de.monticore.utils.Link;

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
    for (Link<ASTNode, ASTCDClass> classLink : rootLink.getLinks(ASTNode.class,
        ASTCDClass.class)) {

      for (Link<ASTNode, ASTCDAttribute> link : classLink.getLinks(ASTNode.class, ASTCDAttribute.class)) {
        if (isOverridden(link.source(), classLink) && isNotInherited(link.target())) {
          ASTCDAttribute target = link.target();
          classLink.target().getCDAttributeList().remove(target);
        }
      }
    }
    return rootLink;
  }

  private boolean isOverridden(ASTNode source, Link<?, ASTCDClass> classLink) {
    Optional<String> usageName = getUsageName(classLink.source(), source);
    if (!usageName.isPresent()) {
      usageName = getName(source);
    }
    Set<ASTAdditionalAttribute> attributesInASTLinkingToSameClass = attributesInASTLinkingToSameClass(
        classLink);
    attributesInASTLinkingToSameClass.remove(source);

    boolean matchByUsageName = usageName.isPresent() && attributesInASTLinkingToSameClass.stream()
            .filter(ASTAdditionalAttribute::isPresentName)
        .map(ASTAdditionalAttribute::getName)
        .anyMatch(usageName.get()::equals);

    boolean matchByTypeName = false;
    if (!usageName.isPresent()) {
      for (ASTAdditionalAttribute attributeInAST : attributesInASTLinkingToSameClass) {
        if (!attributeInAST.isPresentName()) {
          String name = attributeInAST.getMCType().printType(MCFullGenericTypesMill.mcFullGenericTypesPrettyPrinter());
          if(getName(source).orElse("").equals(name)){
            matchByTypeName= true;
            break;
          }
        }
      }
    }

    return matchByUsageName || matchByTypeName;
  }

  private Set<ASTAdditionalAttribute> attributesInASTLinkingToSameClass(Link<?, ASTCDClass> link) {
    return link.rootLink().getLinks(ASTNode.class, ASTCDClass.class).stream()
        .filter(attributeLink -> attributeLink.target() == link.target())
        .flatMap(astRuleLink ->
            astRuleLink.getLinks(ASTAdditionalAttribute.class, ASTCDAttribute.class).stream())
        .map(Link::source).collect(Collectors.toSet());
  }

  private boolean isNotInherited(ASTCDAttribute cdAttribute) {
    if (!cdAttribute.isPresentModifier()) {
      return true;
    }
    if (!cdAttribute.getModifier().isPresentStereotype()) {
      return true;
    }
    return cdAttribute.getModifier().getStereotype().getValueList().stream()
        .map(ASTCDStereoValue::getName)
        .noneMatch(MC2CDStereotypes.INHERITED.toString()::equals);
  }
}
