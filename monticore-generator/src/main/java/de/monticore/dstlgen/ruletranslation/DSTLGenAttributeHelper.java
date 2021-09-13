/* (c) https://github.com/MontiCore/monticore */
package de.monticore.dstlgen.ruletranslation;

import de.monticore.dstlgen.util.DSTLUtil;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.Joiners;

import java.util.List;
import java.util.Optional;

import static de.se_rwth.commons.StringTransformations.capitalize;
import static de.se_rwth.commons.StringTransformations.uncapitalize;

/**
 * Helper to handle names of attributes.
 *
 */
public class DSTLGenAttributeHelper {

  /**
   * Constructor for de.monticore.tf.ruletranslation.AttributeHelper
   */
  public DSTLGenAttributeHelper() {
  }

  public String getNameUpperCase(ASTNonTerminal ast) {
    if (ast.isPresentUsageName()) {
      return capitalize(ast.getUsageName());
    } else {
      return capitalize(ast.getName());
    }
  }

  public String getNameLowerCase(ASTNonTerminal ast) {
    if (ast.isPresentUsageName()) {
      return uncapitalize(ast.getUsageName());
    } else {
      return uncapitalize(ast.getName());
    }
  }

  public String getNameUpperCase(ASTConstantGroup ast) {
    return DSTLUtil.getNameForConstant(ast);
  }

  public String getNameLowerCase(ASTConstantGroup ast) {
    return  uncapitalize(DSTLUtil.getNameForConstant(ast));
  }

  public String getType(ASTNonTerminal ast){
    return ast.isPresentSymbol() && ast.getSymbol().isPresentReferencedType() ?
           ast.getSymbol().getReferencedType() :
           capitalize(ast.getName());
  }

  public String getTypePackage(ASTNonTerminal ast){
    Optional<ProdSymbol> prodSymbol = ast.getEnclosingScope().resolveInSuperGrammars(ast.getName(), AccessModifier.ALL_INCLUSION);
    if (prodSymbol.isPresent()) {
      if(prodSymbol.get().getPackageName().isEmpty()){
        return "tr";
      }
      return prodSymbol.get().getPackageName() +".tr";
    }

    MCGrammarSymbol astGrammarSymbol = (MCGrammarSymbol) ast.getEnclosingScope().getEnclosingScope().getSpanningSymbol();
    String result =  Joiners.DOT.join(astGrammarSymbol.getAstNode().getPackageList());
    if(result.isEmpty()){
      return "tr";
    }
    return result + ".tr";
  }

  public String getTypeGrammarName(ASTNonTerminal ast){
      String prodName = getType(ast);
      ProdSymbol prod = ast.getSymbol().getEnclosingScope().resolveProd(prodName).get();
      return  getSymbolGrammar((IGrammar_WithConceptsScope) prod.getEnclosingScope(), prodName).toLowerCase();
  }

  private String getSymbolGrammar(IGrammar_WithConceptsScope scope, String prodName) {
    List<MCGrammarSymbol> grammars = scope.getLocalMCGrammarSymbols();
    if(grammars.isEmpty()) {
        return getSymbolGrammar(scope.getEnclosingScope(), prodName);
    }
    return grammars.get(0).getName();
  }

  public boolean isAttributeOptional(ASTNonTerminal ast){
    RuleComponentSymbol componentSymbol = ast.getSymbol();
    return componentSymbol.isIsOptional();
  }

  public boolean isAttributeExternal(ASTNonTerminal ast){
    String prodName = getType(ast);
    ProdSymbol prod = ast.getSymbol().getEnclosingScope().resolveProd(prodName).get();
    return prod.isIsExternal();
  }

}
