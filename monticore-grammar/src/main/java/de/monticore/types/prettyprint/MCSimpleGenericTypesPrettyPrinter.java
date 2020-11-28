/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCSimpleGenericTypesNode;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesHandler;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesTraverser;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor2;

import java.util.Iterator;

public class MCSimpleGenericTypesPrettyPrinter implements MCSimpleGenericTypesVisitor2, MCSimpleGenericTypesHandler {

  protected MCSimpleGenericTypesTraverser traverser;

  protected IndentPrinter printer;

  public MCSimpleGenericTypesPrettyPrinter(IndentPrinter printer){
    this.printer = printer;
  }

  public IndentPrinter getPrinter() {
    return printer;
  }

  public void setPrinter(IndentPrinter printer) {
    this.printer = printer;
  }

  @Override
  public MCSimpleGenericTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCSimpleGenericTypesTraverser traverser) {
    this.traverser = traverser;
  }

  public void handle(ASTMCBasicGenericType node) {
   getPrinter().print(String.join(".",node.getNameList())+"<");
    Iterator<ASTMCTypeArgument> a = node.getMCTypeArgumentList().iterator();
    // printListSimpleGenericTypes(a,",");
    String seperator = ",";
    String sepTemp = "";

   for(ASTMCTypeArgument t:node.getMCTypeArgumentList()) {
     getPrinter().print(sepTemp);
     t.accept(getTraverser());
     sepTemp = seperator;
   }
    getPrinter().print(">");
  }

  public void handle(ASTMCCustomTypeArgument node) {
    node.getMCType().accept(getTraverser());
  }


  /**
   * Prints a list
   *
   * @param iter iterator for the list
   * @param seperator string for seperating list
   */
  protected void printListSimpleGenericTypes(Iterator<? extends ASTMCSimpleGenericTypesNode> iter, String seperator) {
    // print by iterate through all items
    String sep = "";
    while (iter.hasNext()) {
      getPrinter().print(sep);
      iter.next().accept(getTraverser());
      sep = seperator;
    }
  }

}
