package de.se_rwth.langeditor.util.prefuse;

import java.util.Collection;
import java.util.function.Function;

import javax.swing.JFrame;

import prefuse.Display;
import prefuse.Visualization;
import prefuse.action.ActionList;
import prefuse.action.RepaintAction;
import prefuse.action.assignment.ColorAction;
import prefuse.action.layout.graph.NodeLinkTreeLayout;
import prefuse.activity.Activity;
import prefuse.controls.DragControl;
import prefuse.controls.PanControl;
import prefuse.controls.ZoomControl;
import prefuse.data.Graph;
import prefuse.data.Node;
import prefuse.data.Tree;
import prefuse.render.DefaultRendererFactory;
import prefuse.render.LabelRenderer;
import prefuse.util.ColorLib;
import prefuse.visual.VisualItem;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.se_rwth.langeditor.util.Misc;

public final class Prefuse {
  
  public static <T> Tree createTree(T root, Function<T, Collection<? extends T>> childGenerator) {
    return createTree(root, childGenerator, element -> element.getClass().getSimpleName());
  }
  
  public static <T> Tree createTree(T root, Function<T, Collection<? extends T>> childGenerator,
      Function<T, String> nameGenerator) {
    Tree tree = new Tree();
    tree.addColumn("name", String.class);
    BiMap<T, Node> elementToNode = HashBiMap.create();
    Node rootNode = tree.addRoot();
    rootNode.set("name", nameGenerator.apply(root));
    elementToNode.put(root, rootNode);
    
    Misc.preorder(root, childGenerator).forEach(element -> {
      Node parent = elementToNode.get(element);
      childGenerator.apply(element).forEach(childElement -> {
        Node child = tree.addChild(parent);
        child.set("name", nameGenerator.apply(childElement));
        elementToNode.put(childElement, child);
      });
    });
    return tree;
  }
  
  public static void show(Graph graph) {
    
    // -- 2. the visualization --------------------------------------------
    
    // add the graph to the visualization as the data group "graph"
    // nodes and edges are accessible as "graph.nodes" and "graph.edges"
    Visualization vis = new Visualization();
    vis.add("graph", graph);
    vis.setInteractive("graph.edges", null, false);
    
    // -- 3. the renderers and renderer factory ---------------------------
    
    // draw the "name" label for NodeItems
    LabelRenderer r = new LabelRenderer("name");
    r.setRoundedCorner(8, 8); // round the corners
    
    // create a new default renderer factory
    // return our name label renderer as the default for all non-EdgeItems
    // includes straight line edges for EdgeItems by default
    vis.setRendererFactory(new DefaultRendererFactory(r));
    
    // -- 4. the processing actions ---------------------------------------
    
    // create our nominal color palette
    // pink for females, baby blue for males
    @SuppressWarnings("unused")
    int[] palette = new int[] {
        ColorLib.rgb(255, 180, 180), ColorLib.rgb(190, 190, 255)
    };
    // use black for node text
    ColorAction text = new ColorAction("graph.nodes",
        VisualItem.TEXTCOLOR, ColorLib.gray(0));
    // use light grey for edges
    ColorAction edges = new ColorAction("graph.edges",
        VisualItem.STROKECOLOR, ColorLib.gray(200));
    
    // create an action list containing all color assignments
    ActionList color = new ActionList();
    color.add(text);
    color.add(edges);
    
    // create an action list with an animated layout
    ActionList layout = new ActionList(Activity.INFINITY);
    layout.add(new NodeLinkTreeLayout("graph"));
    layout.add(new RepaintAction());
    
    // add the actions to the visualization
    vis.putAction("color", color);
    vis.putAction("layout", layout);
    
    // -- 5. the display and interactive controls -------------------------
    
    Display d = new Display(vis);
    d.setSize(720, 500); // set display size
    // drag individual items around
    d.addControlListener(new DragControl());
    // pan with left-click drag on background
    d.addControlListener(new PanControl());
    // zoom with right-click drag
    d.addControlListener(new ZoomControl());
    
    // -- 6. launch the visualization -------------------------------------
    
    // create a new window to hold the visualization
    JFrame frame = new JFrame("prefuse example");
    // ensure application exits when window is closed
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.add(d);
    frame.pack(); // layout components in window
    frame.setVisible(true); // show the window
    
    // assign the colors
    vis.run("color");
    // start up the animated layout
    vis.run("layout");
  }
  
}
