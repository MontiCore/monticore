/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.codegen.mc2cd.manipul.CDManipulation;
import de.monticore.codegen.mc2cd.transl.MC2CDTranslation;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.utils.Link;

import java.util.function.Function;

/**
 * This is the top-level function accepting a MC AST and taking it all the way
 * through to the finished CD AST.
 *
 */
public class MC2CDTransformation implements Function<ASTMCGrammar, ASTCDCompilationUnit> {
  
  protected GlobalExtensionManagement glex;

  public MC2CDTransformation(GlobalExtensionManagement glex) {
    this.glex = glex;
  }
  
  @Override
  public ASTCDCompilationUnit apply(ASTMCGrammar grammar) {
    Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink = new Link<>(grammar,
            CD4AnalysisMill.cDCompilationUnitBuilder().uncheckedBuild(), null);
    
    return new MC2CDTranslation(glex)
        .andThen(Link::target)
        .andThen(new CDManipulation())
        .apply(rootLink);
  }
  
}
