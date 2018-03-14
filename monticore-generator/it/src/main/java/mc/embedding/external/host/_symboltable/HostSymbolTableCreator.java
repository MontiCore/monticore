/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.external.host._symboltable;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.embedding.external.host._ast.ASTHost;
import mc.embedding.external.host._visitor.HostVisitor;

import java.util.Deque;

public class HostSymbolTableCreator extends HostSymbolTableCreatorTOP {

  private HostVisitor realThis = this;

  public HostSymbolTableCreator(ResolvingConfiguration resolverConfig,
      MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }

  public HostSymbolTableCreator (ResolvingConfiguration resolvingConfiguration,
      Deque<MutableScope> scopeStack) {
    super(resolvingConfiguration, scopeStack);
  }

  @Override public void visit(ASTHost node) {
    final HostSymbol hostSymbol = new HostSymbol(node.getName());

    addToScopeAndLinkWithNode(hostSymbol, node);
  }

  @Override public void endVisit(ASTHost node) {
    removeCurrentScope();
  }

  @Override public void setRealThis(HostVisitor realThis) {
    this.realThis = realThis;
  }

  @Override public HostVisitor getRealThis() {
    return realThis;
  }
}
