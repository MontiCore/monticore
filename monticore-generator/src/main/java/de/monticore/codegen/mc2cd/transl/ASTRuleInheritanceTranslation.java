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

import java.util.function.UnaryOperator;

import de.monticore.codegen.mc2cd.EssentialTransformationHelper;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTGenericType;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.utils.Link;

/**
 * Checks if the source astrules were extending or implementing other rules
 * and sets the super classes / extended interfaces of the target nodes
 * accordingly. This Translation differs from {@link ExtendsTranslation} and
 * {@link ImplementsTranslation} since it applies to ASTRules rather than
 * ordinary rules. E.g: 'ast A astextends B' instead of 'A astextends B'.
 *
 * @author Sebastian Oberhoff
 */
public class ASTRuleInheritanceTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTASTRule, ASTCDClass> link : rootLink.getLinks(
        ASTASTRule.class, ASTCDClass.class)) {
      translateClassProd(link.source(), link.target(), rootLink.source());
    }

    for (Link<ASTASTRule, ASTCDInterface> link : rootLink.getLinks(
        ASTASTRule.class, ASTCDInterface.class)) {
      translateInterfaceProd(link.source(), link.target(), rootLink.source());
    }

    return rootLink;
  }

  private void translateInterfaceProd(ASTASTRule rule, ASTCDInterface cdInterface,
      ASTMCGrammar astGrammar) {
    // translates "astextends"
    for (ASTGenericType superInterface : rule.getASTSuperClass()) {
      String qualifiedSuperInterface = EssentialTransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superInterface, astGrammar, cdInterface);

      cdInterface.getInterfaces().add(
          EssentialTransformationHelper.createSimpleReference(qualifiedSuperInterface));
    }
  }

  private void translateClassProd(ASTASTRule rule, ASTCDClass cdClass, ASTMCGrammar astGrammar) {
    // translates "astextends"
    for (ASTGenericType superClass : rule.getASTSuperClass()) {
      String qualifiedSuperClass = EssentialTransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superClass, astGrammar, cdClass);
      cdClass.setSuperclass(EssentialTransformationHelper.createSimpleReference(qualifiedSuperClass));
    }

    // translates "astimplements"
    for (ASTGenericType superInterface : rule.getASTSuperInterface()) {
      String qualifiedSuperInterface = EssentialTransformationHelper
          .getQualifiedTypeNameAndMarkIfExternal(superInterface, astGrammar, cdClass);
      cdClass.getInterfaces()
          .add(EssentialTransformationHelper.createSimpleReference(qualifiedSuperInterface));
    }
  }
}
