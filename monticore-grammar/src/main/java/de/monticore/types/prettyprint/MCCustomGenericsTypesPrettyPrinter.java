package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mccustomgenericstypes._ast.ASTMCCollectionTypesReferenceType;
import de.monticore.types.mccustomgenericstypes._ast.ASTMCCustomTypeArgument;
import de.monticore.types.mccustomgenericstypes._visitor.MCCustomGenericsTypesVisitor;

public class MCCustomGenericsTypesPrettyPrinter extends MCCollectionTypesPrettyPrinter implements MCCustomGenericsTypesVisitor {
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

  public void handle(ASTMCCollectionTypesReferenceType node) {
   getPrinter().print(String.join(".",node.getNameList())+"<");

   for(ASTMCTypeArgument t:node.getMCTypeArgumentList()) {
     t.accept(getRealThis());
   }
    getPrinter().print(">");
  }

  public void handle(ASTMCCustomTypeArgument node) {
    node.getMCReferenceType().accept(getRealThis());
  }


  public String prettyprint(ASTMCCustomTypeArgument a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }
}
