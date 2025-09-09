/* (c) https://github.com/MontiCore/monticore */

package de.monticore.cardinality._ast;


import de.monticore.cardinality.CardinalityMill;

public class ASTCardinality extends ASTCardinalityTOP {

  public int getLowerBound() {
    if (this.isMany()) {
      return 0;
    } else {
      return this.getLowerBoundLit().getValue();
    }
  }

  public void setLowerBound(int i) {
    setLowerBoundLit(CardinalityMill.natLiteralBuilder().setDigits(Integer.toString(i)).build());
  }

  public int getUpperBound() {
    if (this.isMany() || this.isNoUpperLimit()) {
      return 0;
    } else if (this.isPresentUpperBoundLit()) {
      return this.getUpperBoundLit().getValue();
    }
    return this.getLowerBoundLit().getValue();
  }

  public void setUpperBound(int i) {
    setUpperBoundLit(CardinalityMill.natLiteralBuilder().setDigits(Integer.toString(i)).build());
  }
}


