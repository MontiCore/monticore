package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasicgenericstypes._ast.ASTListType;
import de.monticore.types.mcbasicgenericstypes._ast.ASTMapType;
import de.monticore.types.mcbasicgenericstypes._ast.ASTOptionalType;
import de.monticore.types.mcbasicgenericstypes._ast.ASTSetType;
import de.monticore.types.mcbasicgenericstypes._cocos.MCBasicGenericsTypesASTListTypeCoCo;
import de.monticore.types.mcbasicgenericstypes._visitor.MCBasicGenericsTypesVisitor;
import de.monticore.types.mcbasictypes._ast.ASTMCBasicTypesNode;

import java.util.Iterator;

public class MCBasicGenericsTypesPrettyPrinterConcreteVisitor extends MCBasicTypesPrettyPrinterConcreteVisitor implements MCBasicGenericsTypesVisitor {
  private MCBasicGenericsTypesVisitor realThis = this;

  public MCBasicGenericsTypesPrettyPrinterConcreteVisitor(IndentPrinter printer){
    super(printer);
  }

  @Override
  public MCBasicGenericsTypesVisitor getRealThis(){
    return realThis;
  }

  @Override
  public void setRealThis(MCBasicGenericsTypesVisitor realThis){
    this.realThis=realThis;
  }

  @Override
  public void visit(ASTListType a){
    getPrinter().print("List<"+a.getSimpleReferenceType().getQName()+">");
  }

  @Override
  public void visit(ASTOptionalType a){
    getPrinter().print("Optional<"+a.getSimpleReferenceType().getQName()+">");
  }

  @Override
  public void visit(ASTMapType a){
    getPrinter().print("Map<"+a.getSimpleReferenceType(0).getQName()+","+a.getSimpleReferenceType(1).getQName() +">");
  }

  @Override
  public void visit(ASTSetType a){
    getPrinter().print("Set<"+a.getSimpleReferenceType().getQName()+">");
  }


}
