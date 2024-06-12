/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.mc2cd;

import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.types.check.ISynthesize;

public class MC2CDBasisSymbolTableCompleter extends CDBasisSymbolTableCompleter {


  public MC2CDBasisSymbolTableCompleter(ISynthesize typeSynthesizer) {
    super(typeSynthesizer);
  }

  @Override
  public void visit(ASTCDCompilationUnit node) {
    // Do nothing
  }

}
