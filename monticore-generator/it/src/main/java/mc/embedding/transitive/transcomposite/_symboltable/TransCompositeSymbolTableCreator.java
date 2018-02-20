/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.transitive.transcomposite._symboltable;

import java.util.Deque;

import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import mc.embedding.composite._symboltable.SimpleCompositeSymbolTableCreator;
import mc.embedding.embedded._symboltable.EmbeddedSymbolTableCreator;
import mc.embedding.host._symboltable.HostSymbolTableCreator;
import mc.embedding.transitive.transcomposite._visitor.TransCompositeDelegatorVisitor;
import mc.embedding.transitive.transcomposite._visitor.TransCompositeVisitor;
import mc.embedding.transitive.transhost._ast.ASTTransStart;
import mc.embedding.transitive.transhost._symboltable.TransHostSymbolTableCreator;

public class TransCompositeSymbolTableCreator extends CommonSymbolTableCreator implements
    TransCompositeVisitor {
  
  private final TransHostSymbolTableCreator hostSymbolTableCreator;
  
  public final TransCompositeDelegatorVisitor visitor;
  
  private TransCompositeVisitor realThis = this;
  
  public TransCompositeSymbolTableCreator(final ResolvingConfiguration resolverConfig,
      final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
    
    this.hostSymbolTableCreator = new TransHostSymbolTableCreator(resolverConfig, scopeStack);
    
    visitor = new TransCompositeDelegatorVisitor();
    
    visitor.setHostVisitor( new HostSymbolTableCreator(resolverConfig, scopeStack));
    visitor.setEmbeddedVisitor(
        new EmbeddedSymbolTableCreator(resolverConfig, scopeStack));
    visitor.setTransCompositeVisitor(this);
    visitor
        .setTransHostVisitor(this.hostSymbolTableCreator);
    visitor.setCompositeVisitor(new SimpleCompositeSymbolTableCreator(resolverConfig, scopeStack));
  }
  
  public TransCompositeSymbolTableCreator(final ResolvingConfiguration resolverConfig,
      final Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
    
    this.hostSymbolTableCreator = new TransHostSymbolTableCreator(resolverConfig, scopeStack);
    
    visitor = new TransCompositeDelegatorVisitor();
    
    visitor.setHostVisitor( new HostSymbolTableCreator(resolverConfig, scopeStack));
    visitor.setEmbeddedVisitor(
        new EmbeddedSymbolTableCreator(resolverConfig, scopeStack));
    visitor.setTransCompositeVisitor(this);
    visitor
        .setTransHostVisitor(this.hostSymbolTableCreator);
    visitor.setCompositeVisitor(new SimpleCompositeSymbolTableCreator(resolverConfig, scopeStack));
   
  }
  
  @Override
  public void setRealThis(TransCompositeVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }
  
  @Override
  public TransCompositeVisitor getRealThis() {
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
  public Scope createFromAST(ASTTransStart rootNode) {
    Log.errorIfNull(rootNode);
    rootNode.accept(visitor);
    return getFirstCreatedScope();
  }
  
}
