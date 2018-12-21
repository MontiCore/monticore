package de.monticore.types.prettyprint;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasicgenericstypes._ast.*;
import de.monticore.types.mcbasicgenericstypes._visitor.MCBasicGenericsTypesVisitor;

public class MCBasicGenericsTypesPrettyPrinter extends MCBasicTypesPrettyPrinter implements MCBasicGenericsTypesVisitor {
  private MCBasicGenericsTypesVisitor realThis = this;

  public MCBasicGenericsTypesPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public MCBasicGenericsTypesVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void setRealThis(MCBasicGenericsTypesVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public void traverse(ASTMCListType a) {
    getPrinter().print("List<");
    a.getMCTypeArgument().accept(this);
    getPrinter().print(">");
  }

  @Override
  public void traverse(ASTMCOptionalType a) {
    getPrinter().print("Optional<");
    a.getMCTypeArgument().accept(this);
    getPrinter().print(">");
  }

  @Override
  public void traverse(ASTMCSetType a) {
    getPrinter().print("Set<");
    a.getMCTypeArgument().accept(this);
    getPrinter().print(">");
  }

  @Override
  public void traverse(ASTMCMapType a) {
    getPrinter().print("Map<");
    a.getKey().accept(this);
    getPrinter().print(",");
    a.getValue().accept(this);
    getPrinter().print(">");
  }

  public String prettyprint(ASTMCBasicTypeArgument a) {
    getPrinter().clearBuffer();
    a.accept(getRealThis());
    return getPrinter().getContent();
  }

}
