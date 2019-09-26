/* (c) https://github.com/MontiCore/monticore */
package prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import stringliterals._ast.ASTCharLiteral;
import stringliterals._ast.ASTStringLiteral;
import stringliterals._ast.ASTStringLiteralsNode;
import stringliterals._visitor.StringLiteralsVisitor;

public class StringLiteralsPrettyPrinter implements StringLiteralsVisitor {
  
  private IndentPrinter printer = null;
  
  public StringLiteralsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }
  
  @Override
  public void handle(ASTCharLiteral node) {
    getPrinter().print("'" + node.getSource() + "'");
  }
  
  @Override
  public void handle(ASTStringLiteral node) {
    getPrinter().print("\"" + node.getSource() + "\"");
  }
  
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  public String prettyprint(ASTStringLiteralsNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
  
}
