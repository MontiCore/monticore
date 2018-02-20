/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.embedded._symboltable;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.embedding.embedded._ast.ASTText;
import mc.embedding.embedded._visitor.EmbeddedVisitor;

import java.util.ArrayList;
import java.util.Deque;

import static java.util.Optional.empty;

public class EmbeddedSymbolTableCreator extends EmbeddedSymbolTableCreatorTOP {
  private EmbeddedVisitor realThis = this;

  public EmbeddedSymbolTableCreator(ResolvingConfiguration resolverConfig, MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);

    final ArtifactScope artifactScope = new ArtifactScope(empty(), "", new ArrayList<>());

    putOnStack(artifactScope);
  }

  public EmbeddedSymbolTableCreator(ResolvingConfiguration resolverConfig,
      Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
  }

  @Override public void visit(ASTText node) {
    final TextSymbol textSymbol = new TextSymbol(node.getName());

    addToScopeAndLinkWithNode(textSymbol, node);
  }

  @Override public void setRealThis(EmbeddedVisitor realThis) {
    this.realThis = realThis;
  }

  @Override public EmbeddedVisitor getRealThis() {
    return realThis;
  }
}
