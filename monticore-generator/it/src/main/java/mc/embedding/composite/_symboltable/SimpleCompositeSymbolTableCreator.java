/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.composite._symboltable;

import java.util.Deque;

import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.embedding.composite._visitor.CompositeVisitor;

public class SimpleCompositeSymbolTableCreator extends CommonSymbolTableCreator implements CompositeVisitor {


  private CompositeVisitor realThis = this;

  public SimpleCompositeSymbolTableCreator(final ResolvingConfiguration resolverConfig,
      final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }

  public SimpleCompositeSymbolTableCreator(final ResolvingConfiguration resolverConfig,
      final Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);

  }

  @Override public void setRealThis(CompositeVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
    }
  }

  @Override public CompositeVisitor getRealThis() {
    return realThis;
  }


}
