/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd.scopeTransl;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.Function;

public class MC2CDScopeTranslation implements Function<ASTMCGrammar, ASTCDCompilationUnit> {

  @Override
  public ASTCDCompilationUnit apply(ASTMCGrammar grammar) {
    Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink = new Link<>(grammar,
        CD4AnalysisMill.cDCompilationUnitBuilder().uncheckedBuild(), null);

    return new CDScopeTranslation()
        .andThen(Link::target)
        .apply(rootLink);
  }

}
