package de.monticore.dstlgen.util;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;

public class DSTLService {

  private final ASTMCGrammar grammar;

  public DSTLService(ASTMCGrammar grammar) {
    this.grammar = grammar;
  }

  protected int count = 0;
  public String getGeneratedErrorCode(String name) {
    // Use the string representation
    // also use a count to make sure no double codes can appear
    // because sometimes there is not enough information for a unique string
    String codeString = grammar.getPackageList() + grammar.getName() + name + count;
    count++;
    //calculate hashCode, but limit the values to have at most 5 digits
    int hashCode = Math.abs(codeString.hashCode() % 100000);
    //use String formatting to add leading zeros to always have 5 digits
    String errorCodeSuffix = String.format("%05d", hashCode);
    return "x" + errorCodeSuffix;
  }

}
