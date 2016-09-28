/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.grammar.grammar._ast.*;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.utils.Link;
import de.monticore.ast.ASTNode;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;

import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static de.monticore.codegen.mc2cd.EssentialTransformationHelper.getName;
import static de.monticore.codegen.mc2cd.EssentialTransformationHelper.getUsageName;

/**
 * The CDAttributes generated from AttributeInASTs completely hide any CDAttributes derived from
 * NonTerminals.
 *
 * @author Sebastian Oberhoff
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
          .forEach(classLink.target().getCDAttributes()::remove);
    }
    return rootLink;
  }

  private boolean isOverridden(ASTNode source, Link<?, ASTCDClass> classLink) {
    Optional<String> usageName = getUsageName(classLink.source(), source);
    Set<ASTAttributeInAST> attributesInASTLinkingToSameClass = attributesInASTLinkingToSameClass(
        classLink);
    attributesInASTLinkingToSameClass.remove(source);

    boolean matchByUsageName = usageName.isPresent() && attributesInASTLinkingToSameClass.stream()
        .map(ASTAttributeInAST::getName)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .anyMatch(usageName.get()::equals);

    boolean matchByTypeName = !usageName.isPresent() && attributesInASTLinkingToSameClass.stream()
        .filter(attributeInAST -> !attributeInAST.getName().isPresent())
        .map(ASTAttributeInAST::getGenericType)
        .map(ASTGenericType::getTypeName)
        .anyMatch(getName(source).orElse("")::equals);

    return matchByUsageName || matchByTypeName;
  }

  private Set<ASTAttributeInAST> attributesInASTLinkingToSameClass(Link<?, ASTCDClass> link) {
    return link.rootLink().getLinks(ASTNode.class, ASTCDClass.class).stream()
        .filter(attributeLink -> attributeLink.target() == link.target())
        .flatMap(astRuleLink ->
            astRuleLink.getLinks(ASTAttributeInAST.class, ASTCDAttribute.class).stream())
        .map(Link::source).collect(Collectors.toSet());
  }

  private boolean isNotInherited(ASTCDAttribute cdAttribute) {
    Optional<ASTModifier> modifier = cdAttribute.getModifier();
    if (!modifier.isPresent()) {
      return true;
    }
    Optional<ASTStereotype> stereotype = modifier.get().getStereotype();
    if (!stereotype.isPresent()) {
      return true;
    }
    return stereotype.get().getValues().stream()
        .map(ASTStereoValue::getName)
        .noneMatch(MC2CDStereotypes.INHERITED.toString()::equals);
  }
}
