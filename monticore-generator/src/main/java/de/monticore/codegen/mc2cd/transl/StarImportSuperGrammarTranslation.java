/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.MCBasicTypesMill;
import de.monticore.utils.Link;

import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Adds the fully qualified names of supergrammars as star-imports to the ASTCDCompilationUnit of
 * the CD AST.
 * 
 */
public class StarImportSuperGrammarTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {
    ASTMCGrammar grammar = rootLink.source();
    if (grammar.getSymbolOpt().isPresent()) {
      MCGrammarSymbol symbol = (MCGrammarSymbol) grammar.getSymbol();
      for (MCGrammarSymbol superSymbol : symbol.getSuperGrammarSymbols()) {
        List<String> names = Arrays.asList(superSymbol.getFullName().split("\\."));
        ASTMCImportStatement importStatement = MCBasicTypesMill.mCImportStatementBuilder().setMCQualifiedName(MCBasicTypesMill.mCQualifiedNameBuilder().setPartList(names).build())
            .setStar(true).build();
        ;
        rootLink.target().getMCImportStatementList().add(importStatement);
      }
    }  
    return rootLink;
  }
  
}
