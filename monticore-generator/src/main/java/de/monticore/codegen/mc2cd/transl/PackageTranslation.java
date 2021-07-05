/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.mc2cd.transl;

import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.types.mcbasictypes._ast.ASTMCPackageDeclaration;
import de.monticore.utils.Link;

import java.util.function.UnaryOperator;

/**
 * This function copies over the package statement from the source root to the target root.
 * 
 */
public class PackageTranslation implements
    UnaryOperator<Link<ASTMCGrammar, ASTCDCompilationUnit>> {
  
  @Override
  public Link<ASTMCGrammar, ASTCDCompilationUnit> apply(
      Link<ASTMCGrammar, ASTCDCompilationUnit> rootLink) {

    ASTMCPackageDeclaration mCPackageDeclaration = CD4AnalysisMill.mCPackageDeclarationBuilder().setMCQualifiedName(
            CD4AnalysisMill.mCQualifiedNameBuilder()
                    .setPartsList(rootLink.source().getPackageList())
                    .build())
            .build();
    rootLink.target().setMCPackageDeclaration(mCPackageDeclaration);
    
    return rootLink;
  }
  
}
