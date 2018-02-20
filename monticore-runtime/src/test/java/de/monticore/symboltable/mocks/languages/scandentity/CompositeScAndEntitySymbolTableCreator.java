/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.mocks.languages.entity.EntityLanguageSymbolTableCreator;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntity;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntityBase;
import de.monticore.symboltable.mocks.languages.statechart.StateChartLanguageSymbolTableCreator;
import de.monticore.symboltable.mocks.languages.statechart.asts.ASTStateChartBase;

// TODO PN implement generic CompositeSymbolTableCreator?
public class CompositeScAndEntitySymbolTableCreator extends CommonSymbolTableCreator implements
    EntityLanguageSymbolTableCreator, StateChartLanguageSymbolTableCreator {

  public CompositeScAndEntitySymbolTableCreator(ResolvingConfiguration resolverConfig, MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }

  @Override
  public void traverse(ASTEntity node) {
    visit(node);

    for (ASTNode child : node.get_Children()) {
      if (child instanceof ASTEntityBase) {
        ((ASTEntityBase) child).accept(this);
      }
      // TODO PN adjust to current visitor concept
      // EMBEDDING!! //
      else if (child instanceof ASTStateChartBase) {
        ((ASTStateChartBase) child).accept(this);
      }
    }

    endVisit(node);
  }


}
