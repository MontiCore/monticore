package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mccustomgenericstypes._ast.ASTMCBasicGenericsReferenceType;
import de.monticore.types.mcgenerictypes._ast.ASTMCComplexReferenceType;
import de.monticore.types.mcgenerictypes._ast.ASTMCWildcardType;
import de.monticore.types.mcgenerictypes._visitor.MCGenericTypesVisitor;

public class MCGenericTypesPrettyPrinter extends MCCustomGenericsTypesPrettyPrinter implements MCGenericTypesVisitor {
  private MCGenericTypesVisitor realThis = this;

  public MCGenericTypesPrettyPrinter(IndentPrinter printer){
    super(printer);
  }

  public MCGenericTypesPrettyPrinter(IndentPrinter printer, MCGenericTypesVisitor realThis) {
    super(printer);
    this.realThis = realThis;
  }

  @Override
  public MCGenericTypesVisitor getRealThis(){
    return realThis;
  }

  @Override
  public void setRealThis(MCGenericTypesVisitor realThis){
    this.realThis=realThis;
  }

  public void traverse(ASTMCWildcardType node) {
    getPrinter().print("?");
    if(node.isPresentUpperBound()) {
      getPrinter().print("extends");
      node.getUpperBound().accept(this);
    } else if(node.isPresentLowerBound()) {
      getPrinter().print("super");
      node.getLowerBound().accept(this);
    }
  }

  public void traverse(ASTMCComplexReferenceType node) {
    for(ASTMCBasicGenericsReferenceType q : node.getMCBasicGenericsReferenceTypeList())

    getPrinter().print(String.join(".",node.getNameList())+"<");

  }


}
