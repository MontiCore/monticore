/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTScopeRule;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.utils.Link;

import java.util.Set;
import java.util.function.UnaryOperator;

public class ScopeRuleToCDScopeClass implements UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {

  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    createLinksForMatchedASTRules(rootLink);
    return rootLink;
  }

  private void createLinksForMatchedASTRules(
          Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    // creates Links from the ScopeRule for the Scope class
    Set<Link<ASTMCGrammar, ASTCDClass>> linkList = rootLink.getLinks(ASTMCGrammar.class, ASTCDClass.class);
    if (linkList.size() == 1) {
      Link<ASTMCGrammar, ASTCDClass> link = linkList.stream().findFirst().get();   // only allowed to be one scope class
      if (rootLink.source().isPresentScopeRule()) {
        ASTScopeRule scopeRule = rootLink.source().getScopeRule();
        new Link<>(scopeRule, link.target(), link.parent());
      }

      // super scope rules
      if (rootLink.source().isPresentSymbol()) {
        for (MCGrammarSymbol grammarSymbol : rootLink.source().getSymbol().getAllSuperGrammars()) {
          if (grammarSymbol.getAstGrammar().get().isPresentScopeRule()) {
            new Link<>(grammarSymbol.getAstGrammar().get().getScopeRule(), link.target(), link.parent());
          }
        }
      }
    }
  }

}
