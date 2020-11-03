/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.external.composite._symboltable;

import de.monticore.symboltable.resolving.ISymbolAdapter;
import mc.embedding.external.embedded._symboltable.TextSymbol;

public class Text2ContentAdapter extends ContentSymbol implements ISymbolAdapter<TextSymbol> {

  final TextSymbol adaptee;

  public Text2ContentAdapter(TextSymbol adaptee) {
    super(adaptee.getName());
    this.adaptee = adaptee;
  }

  @Override
  public TextSymbol getAdaptee() {
    return adaptee;
  }
}
