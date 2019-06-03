package de.monticore.types.mcbasictypes._ast;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.prettyprint.MCFullGenericTypesPrettyPrinter;

import java.util.Optional;

public class ASTMCReturnType extends ASTMCReturnTypeTOP {
  public ASTMCReturnType() {
  }

  public ASTMCReturnType(Optional<ASTMCVoidType> mCVoidType, Optional<ASTMCType> mCType) {
    super(mCVoidType, mCType);
  }


  public String printType() {
    IndentPrinter printer = new IndentPrinter();

    MCFullGenericTypesPrettyPrinter vi = new MCFullGenericTypesPrettyPrinter(printer);
    this.accept(vi);
    return vi.getPrinter().getContent();
  }

}
