/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.types.references;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import de.monticore.symboltable.SymbolKind;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.references.CommonSymbolReference;
import de.monticore.symboltable.types.TypeSymbol;
import de.monticore.symboltable.types.TypeSymbolKind;

/**
 * Default implementation of {@link TypeReference}.
 *
 * @author Pedram Mir Seyed Nazari
 *
 */
public class CommonTypeReference<T extends TypeSymbol> extends CommonSymbolReference<T> implements TypeReference<T> {

  private List<ActualTypeArgument> actualTypeArguments = new ArrayList<>();

  private int dimension = 0;

  public CommonTypeReference(String referencedSymbolName, SymbolKind referencedSymbolKind, Scope definingScopeOfReference) {
    super(referencedSymbolName, referencedSymbolKind, definingScopeOfReference);
  }

  @Override
  public List<ActualTypeArgument> getActualTypeArguments() {
    return ImmutableList.copyOf(actualTypeArguments);
  }

  @Override
  public void setActualTypeArguments(List<ActualTypeArgument> actualTypeArguments) {
    this.actualTypeArguments = new ArrayList<>(actualTypeArguments);
  }

  @Override
  public int getDimension() {
    return dimension;
  }

  @Override
  public void setDimension(int arrayDimension) {
    this.dimension = arrayDimension;
  }
}
