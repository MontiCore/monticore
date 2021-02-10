/* (c) https://github.com/MontiCore/monticore */
package coloredgraph._symboltable;

import coloredgraph._ast.ASTGraph;
import coloredgraph._ast.ASTNameColor;
import coloredgraph._ast.ASTRGBColor;
import coloredgraph._ast.ASTVertex;
import coloredgraph._visitor.ColoredGraphVisitor2;

import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ColoredGraphSTCompleteTypes implements ColoredGraphVisitor2 {


    // buffers current color to enable traversal of different Color AST elements
    // without additional visitor
    protected Color currentColor;


    //collect all used colors for scoperule attribute
    protected Set<Color> allColors;

    public ColoredGraphSTCompleteTypes(){
        allColors = new HashSet<>();
    }

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

    public Set<Color> getAllColors(){
        return allColors;
    }
}
