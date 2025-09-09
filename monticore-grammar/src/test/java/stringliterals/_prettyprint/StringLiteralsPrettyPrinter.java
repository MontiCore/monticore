/* (c) https://github.com/MontiCore/monticore */
package stringliterals._prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import stringliterals._ast.ASTCharLiteral;
import stringliterals._ast.ASTStringLiteral;

public class StringLiteralsPrettyPrinter extends StringLiteralsPrettyPrinterTOP{
  public StringLiteralsPrettyPrinter(IndentPrinter printer, boolean printComments) {
    super(printer, printComments);
  }

  // We have to TOP-override these printing methods,
  // as the default generator only supports MCCommonLiterals Chars/Strings

  @Override
  public void handle(ASTCharLiteral node) {
    getPrinter().print("'" + node.getSource() + "'");
  }

  @Override
  public void handle(ASTStringLiteral node) {
    getPrinter().print("\"" + node.getSource() + "\"");
  }
}
