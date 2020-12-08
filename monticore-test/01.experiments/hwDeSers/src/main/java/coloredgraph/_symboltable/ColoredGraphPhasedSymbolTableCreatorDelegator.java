package coloredgraph._symboltable;

import coloredgraph.ColoredGraphMill;
import coloredgraph._ast.ASTGraph;
import coloredgraph._visitor.ColoredGraphTraverser;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ColoredGraphPhasedSymbolTableCreatorDelegator {
    protected IColoredGraphGlobalScope globalScope;
    protected ColoredGraphScopesGenitorDelegator scopesGenitorDelegator;
    protected List<ColoredGraphTraverser> priorityList;
    protected ColoredGraphSTCompleteTypes completeTypes;

    public ColoredGraphPhasedSymbolTableCreatorDelegator(){
        this(ColoredGraphMill.globalScope());
    }

    public ColoredGraphPhasedSymbolTableCreatorDelegator(IColoredGraphGlobalScope globalScope){
        this.globalScope  = globalScope;
        this.scopesGenitorDelegator = new ColoredGraphScopesGenitorDelegator(globalScope);
        this.priorityList = new ArrayList<>();
        this.completeTypes = new ColoredGraphSTCompleteTypes();
        ColoredGraphTraverser traverser = ColoredGraphMill.traverser();
        traverser.add4ColoredGraph(completeTypes);
        priorityList.add(traverser);
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
    public IColoredGraphArtifactScope createFromAST(ASTGraph rootNode) {

        IColoredGraphArtifactScope as = scopesGenitorDelegator.createFromAST(rootNode);
        this.priorityList.forEach(rootNode::accept);
        Set<Color> allColors = completeTypes.getAllColors();
        as.setNumberOfColors(allColors.size());
        return as;
    }
}
