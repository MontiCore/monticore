/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCSimpleGenericTypesNode;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor;

import java.util.Iterator;

public class MCSimpleGenericTypesPrettyPrinter extends MCCollectionTypesPrettyPrinter implements MCSimpleGenericTypesVisitor {
  private MCSimpleGenericTypesVisitor realThis = this;

  public MCSimpleGenericTypesPrettyPrinter(IndentPrinter printer){
    super(printer);
  }

  public MCSimpleGenericTypesPrettyPrinter(IndentPrinter printer, MCSimpleGenericTypesVisitor realThis) {
    super(printer);
    this.realThis = realThis;
  }

  @Override
  public MCSimpleGenericTypesVisitor getRealThis(){
    return realThis;
  }

  @Override
  public void setRealThis(MCSimpleGenericTypesVisitor realThis){
    this.realThis=realThis;
  }

  public void handle(ASTMCBasicGenericType node) {
   getPrinter().print(String.join(".",node.getNamesList())+"<");
    Iterator<ASTMCTypeArgument> a = node.getMCTypeArgumentsList().iterator();
    // printListSimpleGenericTypes(a,",");
    String seperator = ",";
    String sepTemp = "";

   for(ASTMCTypeArgument t:node.getMCTypeArgumentsList()) {
     getPrinter().print(sepTemp);
     t.accept(getRealThis());
     sepTemp = seperator;
   }
    getPrinter().print(">");
  }

  public void handle(ASTMCCustomTypeArgument node) {
    node.getMCType().accept(getRealThis());
  }


  public String prettyprint(ASTMCCustomTypeArgument a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
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
      iter.next().accept(getRealThis());
      sep = seperator;
    }
  }

}
