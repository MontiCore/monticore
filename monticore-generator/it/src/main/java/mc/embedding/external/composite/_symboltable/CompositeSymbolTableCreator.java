/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.external.composite._symboltable;

import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import mc.embedding.external.composite._visitor.CompositeDelegatorVisitor;
import mc.embedding.external.composite._visitor.CompositeVisitor;
import mc.embedding.external.embedded._symboltable.EmbeddedSymbolTableCreator;
import mc.embedding.external.host._ast.ASTHost;
import mc.embedding.external.host._symboltable.HostSymbolTableCreator;

import java.util.Deque;

public class CompositeSymbolTableCreator extends CommonSymbolTableCreator implements CompositeVisitor {

  private final HostSymbolTableCreator hostSymbolTableCreator;

  public final CompositeDelegatorVisitor visitor;

  private CompositeVisitor realThis = this;

  public CompositeSymbolTableCreator(final ResolvingConfiguration resolverConfig,
      final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);

    this.hostSymbolTableCreator = new HostSymbolTableCreator(resolverConfig, scopeStack);

    visitor = new CompositeDelegatorVisitor();
    visitor.setCompositeVisitor(this);
    visitor.setHostVisitor(this.hostSymbolTableCreator);
    visitor.setEmbeddedVisitor(
        new EmbeddedSymbolTableCreator(resolverConfig, scopeStack));
  }

  public CompositeSymbolTableCreator(final ResolvingConfiguration resolverConfig,
      final Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);

    this.hostSymbolTableCreator = new HostSymbolTableCreator(resolverConfig, scopeStack);

    visitor = new CompositeDelegatorVisitor();
    visitor.setCompositeVisitor(this);
    visitor.setHostVisitor(this.hostSymbolTableCreator);
    visitor.setEmbeddedVisitor(
        new EmbeddedSymbolTableCreator(resolverConfig, scopeStack));
  }

  @Override public void setRealThis(CompositeVisitor realThis) {
    this.realThis = realThis;
  }

  @Override public CompositeVisitor getRealThis() {
    return realThis;
  }

  @Override
  public MutableScope getFirstCreatedScope() {
    return hostSymbolTableCreator.getFirstCreatedScope();
  }

  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public Scope createFromAST(ASTHost rootNode) {
    Log.errorIfNull(rootNode);
    rootNode.accept(visitor);
    return getFirstCreatedScope();
  }



}
