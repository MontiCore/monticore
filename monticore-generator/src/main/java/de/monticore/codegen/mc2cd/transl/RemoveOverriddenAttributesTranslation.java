/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.grammar.grammar._ast.ASTAdditionalAttribute;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.types.BasicGenericsTypesPrinter;
import de.monticore.umlcd4a.cd4analysis._ast.*;
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
 *
 */
public class RemoveOverriddenAttributesTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    for (Link<ASTNode, ASTCDClass> classLink : rootLink.getLinks(ASTNode.class,
        ASTCDClass.class)) {

      classLink.getLinks(ASTNode.class, ASTCDAttribute.class).stream()
          .filter(attributeLink -> isOverridden(attributeLink.source(), classLink))
          .filter(attributeLink -> isNotInherited(attributeLink.target()))
          .map(Link::target)
          .forEach(classLink.target().getCDAttributeList()::remove);
    }
    return rootLink;
  }

  private boolean isOverridden(ASTNode source, Link<?, ASTCDClass> classLink) {
    Optional<String> usageName = getUsageName(classLink.source(), source);
    Set<ASTAdditionalAttribute> attributesInASTLinkingToSameClass = attributesInASTLinkingToSameClass(
        classLink);
    attributesInASTLinkingToSameClass.remove(source);

    boolean matchByUsageName = usageName.isPresent() && attributesInASTLinkingToSameClass.stream()
        .map(ASTAdditionalAttribute::getNameOpt)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .anyMatch(usageName.get()::equals);

    boolean matchByTypeName = !usageName.isPresent() && attributesInASTLinkingToSameClass.stream()
        .filter(attributeInAST -> !attributeInAST.getNameOpt().isPresent())
        .map(ASTAdditionalAttribute::getMCType)
        .map(BasicGenericsTypesPrinter::printType)
        .anyMatch(getName(source).orElse("")::equals);

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
    Optional<ASTModifier> modifier = cdAttribute.getModifierOpt();
    if (!modifier.isPresent()) {
      return true;
    }
    Optional<ASTCDStereotype> stereotype = modifier.get().getStereotypeOpt();
    if (!stereotype.isPresent()) {
      return true;
    }
    return stereotype.get().getValueList().stream()
        .map(ASTCDStereoValue::getName)
        .noneMatch(MC2CDStereotypes.INHERITED.toString()::equals);
  }
}
