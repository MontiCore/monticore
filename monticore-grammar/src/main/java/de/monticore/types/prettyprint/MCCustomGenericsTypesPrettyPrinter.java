package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasicgenericstypes._ast.ASTMCTypeArgument;
import de.monticore.types.mccustomgenericstypes._ast.ASTMCBasicGenericsReferenceType;
import de.monticore.types.mccustomgenericstypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mccustomgenericstypes._visitor.MCCustomGenericsTypesVisitor;

public class MCCustomGenericsTypesPrettyPrinter extends MCBasicGenericsTypesPrettyPrinter implements MCCustomGenericsTypesVisitor {
  private MCCustomGenericsTypesVisitor realThis = this;

  public MCCustomGenericsTypesPrettyPrinter(IndentPrinter printer){
    super(printer);
  }

  public MCCustomGenericsTypesPrettyPrinter(IndentPrinter printer, MCCustomGenericsTypesVisitor realThis) {
    super(printer);
    this.realThis = realThis;
  }

  @Override
  public MCCustomGenericsTypesVisitor getRealThis(){
    return realThis;
  }

  @Override
  public void setRealThis(MCCustomGenericsTypesVisitor realThis){
    this.realThis=realThis;
  }

  public void traverse(ASTMCBasicGenericsReferenceType node) {
   getPrinter().print(String.join(".",node.getNameList())+"<");

   for(ASTMCTypeArgument t:node.getMCTypeArgumentList()) {
     t.accept(this);
   }
    getPrinter().print(">");
  }

  public void traverse(ASTMCCustomTypeArgument node) {
    node.getMCReferenceType().accept(this);
  }

}
