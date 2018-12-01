package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasicgenericstypes._ast.*;
import de.monticore.types.mcbasicgenericstypes._visitor.MCBasicGenericsTypesVisitor;
import de.monticore.types.mccustomgenericstypes._ast.ASTBasicGenericsReferenceType;
import de.monticore.types.mccustomgenericstypes._ast.ASTCustomTypeArgument;
import de.monticore.types.mccustomgenericstypes._visitor.MCCustomGenericsTypesVisitor;

public class MCCustomGenericsTypesPrettyPrinterConcreteVisitor extends MCBasicGenericsTypesPrettyPrinterConcreteVisitor implements MCCustomGenericsTypesVisitor {
  private MCCustomGenericsTypesVisitor realThis = this;

  public MCCustomGenericsTypesPrettyPrinterConcreteVisitor(IndentPrinter printer){
    super(printer);
  }

  public MCCustomGenericsTypesPrettyPrinterConcreteVisitor(IndentPrinter printer, MCCustomGenericsTypesVisitor realThis) {
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

  public void traverse(ASTBasicGenericsReferenceType node) {
   getPrinter().print(String.join(".",node.getNameList())+"<");

   for(ASTTypeArgument t:node.getTypeArgumentList()) {
     t.accept(this);
   }
    getPrinter().print(">");
  }

  public void traverse(ASTCustomTypeArgument node) {
    node.getReferenceType().accept(this);
  }

}
