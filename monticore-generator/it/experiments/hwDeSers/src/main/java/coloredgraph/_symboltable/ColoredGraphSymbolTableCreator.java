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

public class ColoredGraphSymbolTableCreator extends ColoredGraphSymbolTableCreatorTOP {

  public ColoredGraphSymbolTableCreator(IColoredGraphScope enclosingScope) {
    super(enclosingScope);
  }

  public ColoredGraphSymbolTableCreator(
      Deque<? extends IColoredGraphScope> scopeStack) {
    super(scopeStack);
  }

  // buffer current color to enable traversal of different Color AST elements
  // without additional visitor
  protected Color currentColor;

  //collect colors for scoperule attribute
  protected Set<Color> allColors;

  @Override public void endVisit(ASTVertex node) {
    VertexSymbol symbol = node.getSymbol();
    symbol.setColor(currentColor);
    symbol.setInitial(node.isInitial());
  }

  @Override public IColoredGraphArtifactScope createFromAST(ASTGraph rootNode) {
    allColors = new HashSet<>();
    IColoredGraphArtifactScope as = super.createFromAST(rootNode);
    as.setNumberOfColors(allColors.size());
    return as;
  }

  @Override public void visit(ASTRGBColor node) {
    int r = node.getNatLiteral(0).getValue();
    int g = node.getNatLiteral(1).getValue();
    int b = node.getNatLiteral(2).getValue();
    Color c = new Color(r, g, b);
    currentColor = c;
    allColors.add(c);
  }

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
    currentColor = c;
    allColors.add(c);
  }
}
