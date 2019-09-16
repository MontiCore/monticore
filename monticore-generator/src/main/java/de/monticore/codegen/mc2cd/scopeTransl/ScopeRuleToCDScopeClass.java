package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cd.cd4analysis._ast.ASTCDClass;
import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTScopeRule;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.utils.ASTNodes;
import de.monticore.utils.Link;

import java.util.List;
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
    List<ASTScopeRule> scopeRuleList = ASTNodes.getSuccessors(rootLink.source(), ASTScopeRule.class);
    // only allowed to be one scope class
    if (linkList.size() == 1 ) {
      Link<ASTMCGrammar, ASTCDClass> link = linkList.stream().findFirst().get();
      //only one own scope rule for one cd compilationUnit
      if (scopeRuleList.size() == 1) {
        // add own scope rule
        ASTScopeRule scopeRule = scopeRuleList.get(0);
        new Link<>(scopeRule, link.target(), link.parent());
      }
      // super scope rules
      if (rootLink.source().getMCGrammarSymbolOpt().isPresent()) {
        for (MCGrammarSymbol grammarSymbol: rootLink.source().getMCGrammarSymbol().getAllSuperGrammars()) {
          grammarSymbol.getAstGrammar().get().getScopeRulesOpt().ifPresent(s -> new Link<>(s, link.target(), link.parent()));
        }
      }
    }
  }
}
