package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTQualifiedReferenceType;
import de.monticore.types.mccustomgenericstypes._ast.ASTBasicGenericsReferenceType;
import de.monticore.types.mcgenerictypes._ast.ASTComplexReferenceType;
import de.monticore.types.mcgenerictypes._ast.ASTWildcardType;
import de.monticore.types.mcgenerictypes._visitor.MCGenericTypesVisitor;

public class MCGenericTypesPrettyPrinterConcreteVisitor extends MCCustomGenericsTypesPrettyPrinterConcreteVisitor implements MCGenericTypesVisitor {
  private MCGenericTypesVisitor realThis = this;

  public MCGenericTypesPrettyPrinterConcreteVisitor(IndentPrinter printer){
    super(printer);
  }

  public MCGenericTypesPrettyPrinterConcreteVisitor(IndentPrinter printer, MCGenericTypesVisitor realThis) {
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

  public void traverse(ASTWildcardType node) {
    getPrinter().print("?");
    if(node.isPresentUpperBound()) {
      getPrinter().print("extends");
      node.getUpperBound().accept(this);
    } else if(node.isPresentLowerBound()) {
      getPrinter().print("super");
      node.getLowerBound().accept(this);
    }
  }

  public void traverse(ASTComplexReferenceType node) {
    for(ASTBasicGenericsReferenceType q : node.getBasicGenericsReferenceTypeList())

    getPrinter().print(String.join(".",node.getNameList())+"<");

  }


}
