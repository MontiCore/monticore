/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import static de.monticore.codegen.mc2cd.TransformationHelper.createSimpleReference;
import static de.monticore.codegen.mc2cd.TransformationHelper.getPackageName;

import java.util.Optional;
import java.util.function.UnaryOperator;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.grammar._ast.ASTAttributeInAST;
import de.monticore.grammar.grammar._ast.ASTGenericType;
import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.types.types._ast.ASTConstantsTypes;
import de.monticore.types.types._ast.ASTType;
import de.monticore.types.types._ast.TypesNodeFactory;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.utils.Link;

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
  
  private ASTType ruleSymbolToType(MCProdSymbol ruleSymbol, String typeName) {
    if (ruleSymbol.isLexerProd()) {
      if (!ruleSymbol.getAstNode().isPresent() || !(ruleSymbol.getAstNode().get() instanceof ASTLexProd)) {
        return createSimpleReference("String");
      }
      return determineConstantsType(HelperGrammar.createConvertType((ASTLexProd)ruleSymbol.getAstNode().get()))
          .map(lexType -> (ASTType) TypesNodeFactory.createASTPrimitiveType(lexType))
          .orElse(createSimpleReference("String"));
    }
    else if (ruleSymbol.isExternal()) {
      return createSimpleReference("AST" + typeName + "Ext");
    }
    else {
      String qualifiedASTNodeName = getPackageName(ruleSymbol)
          + "AST" + ruleSymbol.getName();
      return createSimpleReference(qualifiedASTNodeName);
    }
  }

  private ASTType determineTypeToSetForAttributeInAST(ASTGenericType astGenericType,
      ASTMCGrammar astMCGrammar) {
    Optional<MCProdSymbol> ruleSymbol = TransformationHelper
        .resolveAstRuleType(astMCGrammar, astGenericType);
    if (!ruleSymbol.isPresent()) {
      return determineTypeToSet(astGenericType.getTypeName(), astMCGrammar);
    }
    else if (ruleSymbol.get().isExternal()) {
      return createSimpleReference(astGenericType + "Ext");
    }
    else {
      String qualifiedASTNodeName = TransformationHelper
          .getPackageName(ruleSymbol.get()) + "AST" + ruleSymbol.get().getName();
      return createSimpleReference(qualifiedASTNodeName);
    }
  }

  private ASTType determineTypeToSet(String typeName, ASTMCGrammar astMCGrammar) {
    Optional<ASTType> byReference = MCGrammarSymbolTableHelper
        .resolveRule(astMCGrammar, typeName)
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
