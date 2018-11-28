package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasicgenericstypes._ast.ASTListType;
import de.monticore.types.mcbasicgenericstypes._ast.ASTMapType;
import de.monticore.types.mcbasicgenericstypes._ast.ASTOptionalType;
import de.monticore.types.mcbasicgenericstypes._ast.ASTSetType;
import de.monticore.types.mcbasicgenericstypes._visitor.MCBasicGenericsTypesVisitor;

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
  public void traverse(ASTListType a){
    getPrinter().print("List<");
    a.getTypeArgument().accept(this);
    getPrinter().print(">");
  }

  @Override
  public void traverse(ASTOptionalType a){
    getPrinter().print("Optional<");
    a.getTypeArgument().accept(this);
    getPrinter().print(">");
  }

  @Override
  public void traverse(ASTSetType a){
    getPrinter().print("Set<");
    a.getTypeArgument().accept(this);
    getPrinter().print(">");
  }

  @Override
  public void traverse(ASTMapType a){
    getPrinter().print("Map<");
    a.getKey().accept(this);
    getPrinter().print(",");
    a.getValue().accept(this);
    getPrinter().print(">");
  }

}
