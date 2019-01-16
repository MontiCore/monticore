package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor;
import de.monticore.types.mcsimplegenerictypes._visitor.MCSimpleGenericTypesVisitor;

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
   getPrinter().print(String.join(".",node.getNameList())+"<");

   for(ASTMCTypeArgument t:node.getMCTypeArgumentList()) {
     t.accept(getRealThis());
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
}
