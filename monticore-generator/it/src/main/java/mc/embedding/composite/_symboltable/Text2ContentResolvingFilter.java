/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.composite._symboltable;

import de.monticore.symboltable.Symbol;
import de.monticore.symboltable.resolving.CommonAdaptedResolvingFilter;
import mc.embedding.embedded._symboltable.TextSymbol;
import mc.embedding.host._symboltable.ContentSymbol;

import static com.google.common.base.Preconditions.checkArgument;

public class Text2ContentResolvingFilter extends CommonAdaptedResolvingFilter<ContentSymbol>{

  public Text2ContentResolvingFilter() {
    super(TextSymbol.KIND, ContentSymbol.class, ContentSymbol.KIND);
  }

  @Override
  public ContentSymbol translate(Symbol textSymbol) {
    checkArgument(textSymbol instanceof TextSymbol);

    return new Text2ContentAdapter((TextSymbol) textSymbol);
  }
}
