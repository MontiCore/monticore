/* (c) https://github.com/MontiCore/monticore */

package coloredgraph._symboltable;

import coloredgraph._ast.ASTGraph;
import coloredgraph._ast.ASTNameColor;
import coloredgraph._ast.ASTRGBColor;
import coloredgraph._ast.ASTVertex;

import java.awt.*;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;

/**
 * This handwritten class extends the generated symbol table cretion to set the values of
 * symbolrule and scoperule attributes.
 * For the scoperule, all colors of the vertices in this model have to be counted.
 * For setting the "color" symbolrule attribute of the VertexSymbol, the source in the model
 * (and thus, also in the AST) can be either a symbolic color name or an RGB value.
 * The "isInitial" symbolrule attribute can be directly translated from AST to symbol.
  */
public class ColoredGraphSymbolTableCreator extends ColoredGraphSymbolTableCreatorTOP {

  /**
   * When using the TOP mechanism, all constructors of the TOP class should be overridden to
   * be available everywhere, where they were used for the TOP class.
   */
  public ColoredGraphSymbolTableCreator() {
    super();
  }

  /**
   * When using the TOP mechanism, all constructors of the TOP class should be overridden to
   * be available everywhere, where they were used for the TOP class.
   * @param enclosingScope
   */
  public ColoredGraphSymbolTableCreator(IColoredGraphScope enclosingScope) {
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
  public ColoredGraphSymbolTableCreator(
      Deque<? extends IColoredGraphScope> scopeStack) {
    super(scopeStack);
  }

  // buffers current color to enable traversal of different Color AST elements
  // without additional visitor
  protected Color currentColor;

  //collect all used colors for scoperule attribute
  protected Set<Color> allColors;

  /****************************************************
   * Section: visitors
   ****************************************************/

  /**
   * ASTRGBColor creates an awt.Color object
   * from grammar production:
   * RGBColor implements Color = NatLiteral "," NatLiteral "," NatLiteral ;
   */
  @Override public void visit(ASTRGBColor node) {
    int r = node.getNatLiteral(0).getValue();
    int g = node.getNatLiteral(1).getValue();
    int b = node.getNatLiteral(2).getValue();
    Color c = new Color(r, g, b);
    currentColor = c;     // stored for the endVisit(ASTVertex)
    allColors.add(c);
  }

  /**
   * ASTNameColor creates an awt.Color object
   * from grammar production:
   * NameColor implements Color  = Name;
   * assuming that only a fixed set of color names is allowed.
   * For robustness all unrecognized color names are mapped to black.
   */
  @Override public void visit(ASTNameColor node) {
    Color c;
    switch (node.getName()) {
      case "black":
        c = Color.BLACK;
      case "white":
        c = Color.WHITE;
      case "red":
        c = Color.RED;
      case "green":
        c = Color.GREEN;
      case "blue":
        c = Color.BLUE;
      case "yellow":
        c = Color.YELLOW;
      default:
        c = Color.BLACK;
    }
    currentColor = c;       // stored for the endVisit(ASTVertex)
    allColors.add(c);
  }

  /**
   * At the end of ASTVertex: the Symbol can be created
   * (currentColor was set by the visit's earlier)
   */
  @Override public void endVisit(ASTVertex node) {
    VertexSymbol symbol = node.getSymbol();
    symbol.setColor(currentColor);
    symbol.setInitial(node.isInitial());
  }

  /****************************************************
   * Section: createFromAST
   ****************************************************/

  /**
   * This method created the symbol table for a passed AST node, which is the result of the parse
   * method of the Parser.
   * This method is overridden to set the scoperule attribute: Before the symbol table creator
   * traverses the AST, the set of all colors of the model is initialized with as empty set.
   * During traversal, the colors of the vertices are added to this set.
   * After the traversal, the value of the symbolrule of the artifact scope is set to the size
   * of the set.
   * @param rootNode
   * @return
   */
  @Override public IColoredGraphArtifactScope createFromAST(ASTGraph rootNode) {
    allColors = new HashSet<>();
    IColoredGraphArtifactScope as = super.createFromAST(rootNode);
    as.setNumberOfColors(allColors.size());
    return as;
  }


}
