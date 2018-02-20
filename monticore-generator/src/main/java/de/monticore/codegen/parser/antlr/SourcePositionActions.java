/* (c) https://github.com/MontiCore/monticore */

package de.monticore.codegen.parser.antlr;

import de.monticore.ast.ASTNode;
import de.monticore.codegen.parser.ParserGeneratorHelper;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;

/**
 * Adds source code positions building up code to the parsers
 */
public class SourcePositionActions {
  
  private ParserGeneratorHelper parserGenHelper;
  
  /**
   * Constructor for de.monticore.codegen.parser.antlr.SourcePositionActions
   * 
   * @param parserGenHelper
   */
  public SourcePositionActions(ParserGeneratorHelper parserGenHelper) {
    this.parserGenHelper = parserGenHelper;
  }
  
  /**
   * Create a mc.ast.SourcePosition at the beginning of a rule
   */
  public String startPosition(ASTNode a) {
    return "_aNode.set_SourcePositionStart( computeStartPosition(_input.LT(1)));\n";
  }
  
  public String endPosition(ASTNode a) {
    // Fetch last token to determine position
    return "_aNode.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));";
  }
  
  public String startPositionForLeftRecursiveRule(ASTNonTerminal a) {
    return "_aNode.set_SourcePositionStart(_localctx."
        + parserGenHelper.getTmpVarName(a)
        + ".ret.get_SourcePositionStart());\n";
  }
  
  public String endPositionForLeftRecursiveRule(ASTNonTerminal a) {
    return "_localctx."
        + parserGenHelper.getTmpVarName(a)
        + ".ret.set_SourcePositionEnd( computeStartPosition(_input.LT(1)));\n";
  }
}
