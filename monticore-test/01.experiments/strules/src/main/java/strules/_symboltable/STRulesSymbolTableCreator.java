/* (c) https://github.com/MontiCore/monticore */

package strules._symboltable;

import java.util.Deque;

/**
 * This handwritten class extends the generated symbol table cretion to set the values of
 * symbolrule and scoperule attributes.
 * For the scoperule, all colors of the vertices in this model have to be counted.
 * For setting the "color" symbolrule attribute of the VertexSymbol, the source in the model
 * (and thus, also in the AST) can be either a symbolic color name or an RGB value.
 * The "isInitial" symbolrule attribute can be directly translated from AST to symbol.
  */
public class STRulesSymbolTableCreator extends STRulesSymbolTableCreatorTOP {

  /**
   * When using the TOP mechanism, all constructors of the TOP class should be overridden to
   * be available everywhere, where they were used for the TOP class.
   */
  public STRulesSymbolTableCreator() {
    super();
  }

  /**
   * When using the TOP mechanism, all constructors of the TOP class should be overridden to
   * be available everywhere, where they were used for the TOP class.
   * @param enclosingScope
   */
  public STRulesSymbolTableCreator(ISTRulesScope enclosingScope) {
    super(enclosingScope);
  }

  /**
   * When using the TOP mechanism, all constructors of the TOP class should be overridden to
   * be available everywhere, where they were used for the TOP class.
   *
   * For instance, this constructor enables reusing this symbol table creator as part of a
   * (generated) symbol table creator delegator of a sub language, in which the scope stacks are
   * shared between the delegated symbol table creators.
   * @param scopeStack
   */
  public STRulesSymbolTableCreator(Deque<? extends ISTRulesScope> scopeStack) {
    super(scopeStack);
  }


  /****************************************************
   * Section: visitors
   ****************************************************/


}
