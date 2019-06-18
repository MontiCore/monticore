/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDAttribute;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.grammar._ast.*;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.types.FullGenericTypesPrinter;
import de.monticore.types.mcbasictypes._ast.ASTConstantsMCBasicTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesNodeFactory;
import de.monticore.utils.Link;
import de.se_rwth.commons.Names;

import java.util.Optional;
import java.util.function.UnaryOperator;

import static de.monticore.codegen.mc2cd.TransformationHelper.*;

/**
 * Infers the type that ASTCDAttributes should have according to what kind of rule the original
 * nonterminals were referring to.
 */
public class ReferenceTypeTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    for (Link<ASTNonTerminal, ASTCDAttribute> link : rootLink.getLinks(ASTNonTerminal.class,
        ASTCDAttribute.class)) {
      link.target().setMCType(determineTypeToSet(link.source().getName(), rootLink.source()));
      addStereotypeForASTTypes(link.source(), link.target(), rootLink.source());
    }

    for (Link<ASTTerminal, ASTCDAttribute> link : rootLink.getLinks(ASTTerminal.class,
        ASTCDAttribute.class)) {
      link.target().setMCType(createType("String"));
    }

    for (Link<ASTAdditionalAttribute, ASTCDAttribute> link : rootLink.getLinks(ASTAdditionalAttribute.class,
        ASTCDAttribute.class)) {
      ASTMCType type = determineTypeToSetForAttributeInAST(link.source().getMCType(), rootLink.source());
      link.target().setMCType(type);
        addStereotypeForASTTypes(link.source(), link.target(), rootLink.source());
    }

    return rootLink;
  }

  private ASTMCType ruleSymbolToType(MCProdSymbol ruleSymbol, String typeName) {
    if (ruleSymbol.isLexerProd()) {
      if (!ruleSymbol.getAstNode().isPresent() || !(ruleSymbol.getAstNode().get() instanceof ASTLexProd)) {
        return createType("String");
      }
      return determineConstantsType(HelperGrammar.createConvertType((ASTLexProd) ruleSymbol.getAstNode().get()))
          .map(lexType -> (ASTMCType) MCBasicTypesNodeFactory.createASTMCPrimitiveType(lexType))
          .orElse(createType("String"));
    } else if (ruleSymbol.isExternal()) {
      return createType("AST" + typeName + "Ext");
    } else {
      String qualifiedASTNodeName = getPackageName(ruleSymbol)
          + "AST" + ruleSymbol.getName();
      return createType(qualifiedASTNodeName);
    }
  }

  private ASTMCType determineTypeToSetForAttributeInAST(ASTMCType astGenericType,
                                                      ASTMCGrammar astMCGrammar) {
    Optional<MCProdSymbol> ruleSymbol = TransformationHelper
        .resolveAstRuleType(astMCGrammar, astGenericType);
    if (!ruleSymbol.isPresent()) {
      return determineTypeToSet(astGenericType, astMCGrammar);
    } else if (ruleSymbol.get().isExternal()) {
      return createType(astGenericType + "Ext");
    } else {
      String qualifiedASTNodeName = TransformationHelper
          .getPackageName(ruleSymbol.get()) + "AST" + ruleSymbol.get().getName();
      return createType(qualifiedASTNodeName);
    }
  }

  private ASTMCType determineTypeToSet(ASTMCType astGenericType, ASTMCGrammar astMCGrammar) {
    String typeName = Names.getQualifiedName(astGenericType.getNameList());
    Optional<ASTMCType> byReference = MCGrammarSymbolTableHelper
        .resolveRule(astMCGrammar, typeName)
        .map(ruleSymbol -> ruleSymbolToType(ruleSymbol, typeName));
    Optional<ASTMCType> byPrimitive = determineConstantsType(typeName)
        .map(MCBasicTypesNodeFactory::createASTMCPrimitiveType);
    return byReference.orElse(byPrimitive.orElse(createType(FullGenericTypesPrinter.printType(astGenericType))));
  }

  private ASTMCType determineTypeToSet(String typeName, ASTMCGrammar astMCGrammar) {
    Optional<ASTMCType> byReference = MCGrammarSymbolTableHelper
        .resolveRule(astMCGrammar, typeName)
        .map(ruleSymbol -> ruleSymbolToType(ruleSymbol, typeName));
    Optional<ASTMCType> byPrimitive = determineConstantsType(typeName)
        .map(MCBasicTypesNodeFactory::createASTMCPrimitiveType);
    return byReference.orElse(byPrimitive.orElse(createType(typeName)));
  }

  private Optional<Integer> determineConstantsType(String typeName) {
    switch (typeName) {
      case "int":
        return Optional.of(ASTConstantsMCBasicTypes.INT);
      case "boolean":
        return Optional.of(ASTConstantsMCBasicTypes.BOOLEAN);
      case "double":
        return Optional.of(ASTConstantsMCBasicTypes.DOUBLE);
      case "float":
        return Optional.of(ASTConstantsMCBasicTypes.FLOAT);
      case "char":
        return Optional.of(ASTConstantsMCBasicTypes.CHAR);
      case "byte":
        return Optional.of(ASTConstantsMCBasicTypes.BYTE);
      case "short":
        return Optional.of(ASTConstantsMCBasicTypes.SHORT);
      case "long":
        return Optional.of(ASTConstantsMCBasicTypes.LONG);
      default:
        return Optional.empty();
    }
  }

  private void addStereotypeForASTTypes(ASTNonTerminal nonTerminal, ASTCDAttribute attribute, ASTMCGrammar astmcGrammar) {
    Optional<MCProdSymbol> mcProdSymbol = MCGrammarSymbolTableHelper.resolveRule(astmcGrammar, nonTerminal.getName());
    if (mcProdSymbol.isPresent() && isASTType(mcProdSymbol.get())) {
      TransformationHelper.addStereoType(attribute, MC2CDStereotypes.AST_TYPE.toString(), "");
    }
  }

  private void addStereotypeForASTTypes(ASTAdditionalAttribute astAdditionalAttributes, ASTCDAttribute attribute, ASTMCGrammar astmcGrammar) {
    Optional<MCProdSymbol> mcProdSymbol = resolveAstRuleType(astmcGrammar, astAdditionalAttributes.getMCType());
    if (mcProdSymbol.isPresent() && isASTType(mcProdSymbol.get())) {
      TransformationHelper.addStereoType(attribute, MC2CDStereotypes.AST_TYPE.toString(), "");
    }
  }

  private boolean isASTType(MCProdSymbol mcProdSymbol) {
    return !mcProdSymbol.isLexerProd() && !mcProdSymbol.isEnum();
  }

}
