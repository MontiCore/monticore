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
import de.monticore.languages.grammar.MCExternalRuleSymbol;
import de.monticore.languages.grammar.MCLexRuleSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.monticore.languages.grammar.MCTypeSymbol;
import de.monticore.types.types._ast.ASTConstantsTypes;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.TypesNodeFactory;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

import java.util.Optional;
import java.util.function.UnaryOperator;

import static de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper.resolveRule;
import static de.monticore.codegen.mc2cd.TransformationHelper.createSimpleReference;
import static de.monticore.codegen.mc2cd.TransformationHelper.getAstPackageName;

/**
 * Infers the type that ASTCDAttributes should have according to what kind of rule the original
 * nonterminals were referring to.
 *
 * @author Sebastian Oberhoff
 */
public class ReferenceTypeTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTNonTerminal, ASTCDAttribute> link : rootLink.getLinks(ASTNonTerminal.class,
        ASTCDAttribute.class)) {
      link.target().setType(determineTypeToSet(link.source().getName(), rootLink.source()));
    }

    for (Link<ASTTerminal, ASTCDAttribute> link : rootLink.getLinks(ASTTerminal.class,
        ASTCDAttribute.class)) {
      link.target().setType(createSimpleReference("String"));
    }

    for (Link<ASTAttributeInAST, ASTCDAttribute> link : rootLink.getLinks(ASTAttributeInAST.class,
        ASTCDAttribute.class)) {
      ASTType type = determineTypeToSetForAttributeInAST(
          link.source().getGenericType(), rootLink.source());
      link.target().setType(type);
    }

    return rootLink;
  }

  private ASTType ruleSymbolToType(MCRuleSymbol ruleSymbol, String typeName) {
    if (ruleSymbol instanceof MCLexRuleSymbol) {
      return determineConstantsType(ruleSymbol.getType().getLexType())
          .map(lexType -> (ASTType) TypesNodeFactory.createASTPrimitiveType(lexType))
          .orElse(createSimpleReference("String"));
    }
    else if (ruleSymbol instanceof MCExternalRuleSymbol) {
      return createSimpleReference("AST" + typeName + "Ext");
    }
    else {
      String qualifiedASTNodeName = getAstPackageName(ruleSymbol.getType().getGrammarSymbol())
          + "AST" + ruleSymbol.getType().getName();
      return createSimpleReference(qualifiedASTNodeName);
    }
  }

  private ASTType determineTypeToSetForAttributeInAST(ASTGenericType astGenericType,
      ASTMCGrammar astMCGrammar) {
    Optional<MCRuleSymbol> ruleSymbol = TransformationHelper.resolveAstRuleType(astGenericType);
    if (!ruleSymbol.isPresent()) {
      return determineTypeToSet(astGenericType.getTypeName(), astMCGrammar);
    }
    else if (ruleSymbol.get() instanceof MCExternalRuleSymbol) {
      return createSimpleReference(astGenericType + "Ext");
    }
    else {
      MCTypeSymbol typeSymbol = ruleSymbol.get().getType();
      String qualifiedASTNodeName =
          getAstPackageName(typeSymbol.getGrammarSymbol()) + "AST" + typeSymbol.getName();
      return createSimpleReference(qualifiedASTNodeName);
    }
  }

  private ASTType determineTypeToSet(String typeName, ASTMCGrammar astMCGrammar) {
    Optional<ASTType> byReference = resolveRule(astMCGrammar, typeName)
        .map(ruleSymbol -> ruleSymbolToType(ruleSymbol, typeName));
    Optional<ASTType> byPrimitive = determineConstantsType(typeName)
        .map(TypesNodeFactory::createASTPrimitiveType);
    return byReference.orElse(byPrimitive.orElse(createSimpleReference(typeName)));
  }

  private Optional<Integer> determineConstantsType(String typeName) {
    switch (typeName) {
      case "int":
        return Optional.of(ASTConstantsTypes.INT);
      case "boolean":
        return Optional.of(ASTConstantsTypes.BOOLEAN);
      case "double":
        return Optional.of(ASTConstantsTypes.DOUBLE);
      case "float":
        return Optional.of(ASTConstantsTypes.FLOAT);
      case "char":
        return Optional.of(ASTConstantsTypes.CHAR);
      case "byte":
        return Optional.of(ASTConstantsTypes.BYTE);
      case "short":
        return Optional.of(ASTConstantsTypes.SHORT);
      case "long":
        return Optional.of(ASTConstantsTypes.LONG);
      default:
        return Optional.empty();
    }
  }
}
