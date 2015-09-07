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

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

import static de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper.resolveRule;
import static de.monticore.codegen.mc2cd.TransformationHelper.getAstPackageName;

/**
 * Checks if the source rules were extending other rules and sets the super
 * classes / extended interfaces of the target nodes accordingly.
 *
 * @author Sebastian Oberhoff
 */
public class ExtendsTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTClassProd, ASTCDClass> link : rootLink.getLinks(
        ASTClassProd.class, ASTCDClass.class)) {
      translateClassProd(link.source(), link.target(), rootLink.source());
    }

    for (Link<ASTAbstractProd, ASTCDClass> link : rootLink.getLinks(
        ASTAbstractProd.class, ASTCDClass.class)) {
      translateAbstractProd(link.source(), link.target(), rootLink.source());
    }

    for (Link<ASTInterfaceProd, ASTCDInterface> link : rootLink.getLinks(
        ASTInterfaceProd.class, ASTCDInterface.class)) {
      translateInterfaceProd(link.source(), link.target(), rootLink.source());
    }

    return rootLink;
  }

  private void translateClassProd(ASTClassProd classProd, ASTCDClass cdClass,
      ASTMCGrammar astGrammar) {
    // translates "extends"
    for (ASTRuleReference ruleReference : classProd.getSuperRule()) {
      MCRuleSymbol ruleSymbol = resolveRule(astGrammar, ruleReference.getName()).get();
      String astPackageName = getAstPackageName(ruleSymbol.getGrammarSymbol());
      cdClass.setSuperclass(TransformationHelper.createSimpleReference(
          astPackageName + "AST" + ruleReference.getName()));
    }

    // translates "astextends"
    for (ASTGenericType typeReference : classProd.getASTSuperClass()) {
      String qualifiedRuleName = TransformationHelper.getQualifiedTypeNameAndMarkIfExternal(
          typeReference, astGrammar, cdClass);
      cdClass.setSuperclass(TransformationHelper.createSimpleReference(qualifiedRuleName));
    }
  }

  private void translateAbstractProd(ASTAbstractProd abstractProd,
      ASTCDClass cdClass, ASTMCGrammar astGrammar) {
    // translates "extends"
    for (ASTRuleReference ruleReference : abstractProd.getSuperRule()) {
      MCRuleSymbol ruleSymbol = resolveRule(astGrammar, ruleReference.getName()).get();
      String astPackageName = getAstPackageName(ruleSymbol.getGrammarSymbol());
      cdClass.setSuperclass(TransformationHelper.createSimpleReference(
          astPackageName + "AST" + ruleReference.getName()));
    }

    // translates "astextends"
    String qualifiedRuleName;
    for (ASTGenericType typeReference : abstractProd.getASTSuperClass()) {
      qualifiedRuleName = TransformationHelper.getQualifiedTypeNameAndMarkIfExternal(
          typeReference, astGrammar, cdClass);
      cdClass.setSuperclass(TransformationHelper.createSimpleReference(qualifiedRuleName));
    }
  }

  private void translateInterfaceProd(ASTInterfaceProd interfaceProd,
      ASTCDInterface cdInterface, ASTMCGrammar astGrammar) {
    // translates "extends"
    for (ASTRuleReference ruleReference : interfaceProd.getSuperInterfaceRule()) {
      MCRuleSymbol ruleSymbol = resolveRule(astGrammar, ruleReference.getName()).get();
      String astPackageName = getAstPackageName(ruleSymbol.getGrammarSymbol());
      cdInterface.getInterfaces().add(TransformationHelper.createSimpleReference(
          astPackageName + "AST" + ruleReference.getName()));
    }

    // translates "astextends"
    String qualifiedRuleName;
    for (ASTGenericType typeReference : interfaceProd.getASTSuperInterface()) {
      qualifiedRuleName = TransformationHelper.getQualifiedTypeNameAndMarkIfExternal(
          typeReference, astGrammar, cdInterface);
      cdInterface.getInterfaces().add(
          TransformationHelper.createSimpleReference(qualifiedRuleName));
    }
  }
}
