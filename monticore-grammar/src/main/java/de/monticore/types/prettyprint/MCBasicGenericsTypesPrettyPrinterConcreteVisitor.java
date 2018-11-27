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
    MCBasicTypesPrettyPrinterConcreteVisitor prettyprinter = new MCBasicGenericsTypesPrettyPrinterConcreteVisitor(new IndentPrinter());
    prettyprinter.visit(a.getTypeArgument());
    String output = prettyprinter.getPrinter().getContent();
    getPrinter().print("List<"+output+">");
  }

  @Override
  public void visit(ASTOptionalType a){
    MCBasicTypesPrettyPrinterConcreteVisitor prettyprinter = new MCBasicGenericsTypesPrettyPrinterConcreteVisitor(new IndentPrinter());
    prettyprinter.visit(a.getTypeArgument());
    String output = prettyprinter.getPrinter().getContent();
    getPrinter().print("Optional<"+output+">");
  }

  @Override
  public void visit(ASTMapType a){
    MCBasicTypesPrettyPrinterConcreteVisitor prettyprinter = new MCBasicGenericsTypesPrettyPrinterConcreteVisitor(new IndentPrinter());
    prettyprinter.visit(a.getKey());
    prettyprinter.getPrinter().print(",");
    prettyprinter.visit(a.getValue());
    String output = prettyprinter.getPrinter().getContent();
    getPrinter().print("Map<"+output+">");
  }

  @Override
  public void visit(ASTSetType a){
    MCBasicTypesPrettyPrinterConcreteVisitor prettyprinter = new MCBasicGenericsTypesPrettyPrinterConcreteVisitor(new IndentPrinter());
    prettyprinter.visit(a.getTypeArgument());
    String output = prettyprinter.getPrinter().getContent();
    getPrinter().print("Set<"+output+">");
  }


}
