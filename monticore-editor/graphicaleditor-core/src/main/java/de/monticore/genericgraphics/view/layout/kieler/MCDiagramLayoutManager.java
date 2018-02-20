/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.layout.kieler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.util.EList;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.google.common.collect.BiMap;

import de.cau.cs.kieler.core.kgraph.KEdge;
import de.cau.cs.kieler.core.kgraph.KGraphElement;
import de.cau.cs.kieler.core.kgraph.KLabel;
import de.cau.cs.kieler.core.kgraph.KLabeledGraphElement;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.core.kgraph.KPort;
import de.cau.cs.kieler.core.properties.IProperty;
import de.cau.cs.kieler.core.properties.Property;
import de.cau.cs.kieler.kiml.LayoutContext;
import de.cau.cs.kieler.kiml.config.ILayoutConfig;
import de.cau.cs.kieler.kiml.config.VolatileLayoutConfig;
import de.cau.cs.kieler.kiml.klayoutdata.KEdgeLayout;
import de.cau.cs.kieler.kiml.klayoutdata.KLayoutDataFactory;
import de.cau.cs.kieler.kiml.klayoutdata.KPoint;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;
import de.cau.cs.kieler.kiml.klayoutdata.impl.KEdgeLayoutImpl;
import de.cau.cs.kieler.kiml.klayoutdata.impl.KShapeLayoutImpl;
import de.cau.cs.kieler.kiml.options.EdgeLabelPlacement;
import de.cau.cs.kieler.kiml.options.EdgeType;
import de.cau.cs.kieler.kiml.options.LayoutOptions;
import de.cau.cs.kieler.kiml.ui.diagram.IDiagramLayoutManager;
import de.cau.cs.kieler.kiml.ui.diagram.LayoutMapping;
import de.cau.cs.kieler.kiml.util.KimlUtil;
import de.monticore.genericgraphics.GenericFormEditor;
import de.monticore.genericgraphics.GenericGraphicsEditor;
import de.monticore.genericgraphics.GenericGraphicsViewer;
import de.monticore.genericgraphics.controller.editparts.IMCConnectionEdgeEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCGraphicalEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCNodeEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCShapeEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCViewElementEditPart;
import de.monticore.genericgraphics.controller.editparts.connections.IMCConnectionEditPart;
import de.monticore.genericgraphics.controller.editparts.intern.TextConnectionLabelEditPart;
import de.monticore.genericgraphics.controller.views.outline.CombinedGraphicsOutlinePage;
import de.monticore.genericgraphics.model.ITextConnectionLabel;
import de.monticore.genericgraphics.model.graphics.IEdgeViewElement;
import de.monticore.genericgraphics.model.graphics.IShapeViewElement;
import de.monticore.genericgraphics.model.graphics.IViewElement;
import de.monticore.genericgraphics.model.impl.TextConnectionLabel;
import de.monticore.genericgraphics.view.figures.connections.MCBendpoint;
import de.monticore.genericgraphics.view.figures.connections.locators.ConnectionLocatorPosition;
import de.monticore.genericgraphics.view.layout.IConnectionType;
import de.se_rwth.commons.logging.Log;

/**
 * <p>
 * An implementation of {@link IDiagramLayoutManager}.
 * </p>
 * <p>
 * Explanation: <br>
 * The following methods need to be implemented (with javadoc explanation,
 * ordered by call sequence):
 * <ol>
 * <li>{@link #supports(Object)}: Determine whether this layout manager is able
 * to perform layout for the given object.</li>
 * <li>{@link #getAdapterList()}: Returns the collection of adapter types
 * handled by this factory.</li>
 * <li>{@link #getAdapter(Object, Class)}: Returns an object which is an
 * instance of the given class associated with the given object. Returns null if
 * no such object can be found.</li>
 * <li>{@link #buildLayoutGraph(IWorkbenchPart, Object)}: Build a KGraph
 * instance for the given diagram. The resulting layout graph should reflect the
 * structure of the original diagram. All graph elements must have KShapeLayouts
 * or KEdgeLayouts attached, and their modification flags must be set to false.</li>
 * <li>{@link #applyLayout(LayoutMapping, boolean, int)}: Apply the computed
 * layout back to the diagram. Graph elements whose modification flag was not
 * set during layout should be ignored.</li>
 * </ol>
 * </p>
 * <p>
 * This class works for {@link IMCEditPart IMCEditParts}. It iterates through
 * all {@link IMCEditPart IMCEditParts} of the diagram and checks if they are
 * {@link IMCViewElementEditPart IMCViewElementEditParts}. If it is a
 * {@link IMCViewElementEditPart} the following elements are added to the
 * KGraph:
 * <ul>
 * <li>{@link IMCShapeEditPart}: A {@link KNode} with {@link KShapeLayout} for
 * {@link IShapeViewElement IShapeViewElements}</li>
 * <li>{@link IMCConnectionEdgeEditPart}: A {@link KEdge} with
 * {@link KEdgeLayout} for {@link IEdgeViewElement IEdgeViewElements}</li>
 * <li>{@link ITextConnectionLabel IConnectionLabels}A {@link KLabel} with
 * {@link KShapeLayout} for {@link IShapeViewElement IShapeViewElements}</li>
 * </ul>
 * Note: when the computed layout is applied, all bendpoints of a connections
 * are relative Bendpoints, ignoring if they were absolute bendpoints before. If
 * you want to change this behavior, look at the command creation in the
 * {@link MCDiagramLayoutManager#applyLayout(LayoutMapping, boolean, int)}
 * method. THe {@link ApplyLayoutCommand} has a flag to decide whether to use
 * absolute or relative bendpoints.
 * </p>
 * <p>
 * This class should serve as an example for future implementations.
 * </p>
 * <p>
 * Not supported at the moment:
 * <ul>
 * <li>move anchors according to algorithm computation</li>
 * <li>hierarchical nested view elements</li>
 * </ul>
 * 
 * @author Tim Enger
 */
public class MCDiagramLayoutManager implements IDiagramLayoutManager<IMCEditPart> {
  
  /** editor part of the currently layouted diagram. */
  private IProperty<GenericGraphicsViewer> GENERIC_VIEWER = new Property<GenericGraphicsViewer>("de.monticore.genericgraphics.GenericGraphicsViewer");
  
  /**
   * the volatile layout config for static properties such as minimal node
   * sizes.
   */
  private IProperty<VolatileLayoutConfig> STATIC_CONFIG = new Property<VolatileLayoutConfig>("de.monticore.genericgraphics.staticLayoutConfig");
  
  /**
   * stores a list of editparts that possibly have outgoing connections
   */
  private IProperty<List<IMCGraphicalEditPart>> CONNECTIONS = new Property<List<IMCGraphicalEditPart>>("de.monticore.genericgraphics.CONNECTIONS");
  
  private ILayoutConfig layoutConfig = new MCLayoutConfig();
  
  /**
   * Constructor
   */
  public MCDiagramLayoutManager() {
    
  }
  
  @Override
  public boolean supports(Object object) {
    //  TODO: check what should be supported
    // return object instanceof GenericFormEditor || object instanceof IMCEditPart || object instanceof CombinedGraphicsOutlinePage;
    return true;
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Class[] getAdapterList() {
    return new Class<?>[] { IMCEditPart.class };
  }
  
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public Object getAdapter(Object object, Class adapterType) {
    try {
      if (adapterType.isAssignableFrom(ILayoutConfig.class)) {
        return layoutConfig;
      }
      else if (adapterType.isAssignableFrom(IMCEditPart.class)) {
        if (object != null) {
        }
        if (object instanceof IMCEditPart) {
          return object;
        }
        else if (object instanceof GenericGraphicsEditor) {
          return ((GenericGraphicsEditor) object).getContentEditPart();
        }
        else if (object instanceof GenericFormEditor) {
          return ((GenericFormEditor) object).getGraphicalEditor().getContentEditPart();
        }
        else if (object instanceof CombinedGraphicsOutlinePage) {
          return ((CombinedGraphicsOutlinePage) object).getGraphicalOutline().getViewer().getContentEditPart();
        }
        else if (object instanceof RootEditPart) {
          return ((RootEditPart) object).getContents();
        }
      }
      else if (object instanceof IAdaptable) {
        return ((IAdaptable) object).getAdapter(adapterType);
      }
      
    }
    catch (RuntimeException exception) {
      // when the editor part has been closed NPEs can occur
    }
    return null;
  }
  
  @Override
  public LayoutMapping<IMCEditPart> buildLayoutGraph(IWorkbenchPart workbenchPart, Object diagramPart) {
    // System.err.println("MCDLM> buildLayout");
    // System.err.println("-----> workbenchPart: " + workbenchPart);
    // System.err.println("-----> diagramPart: " + diagramPart);
    
    LayoutMapping<IMCEditPart> mapping = new LayoutMapping<IMCEditPart>(this);
    
    mapping.setProperty(STATIC_CONFIG, new VolatileLayoutConfig(MCLayoutConfig.PRIORITY - 1));
    mapping.setProperty(CONNECTIONS, new ArrayList<IMCNodeEditPart>());
    
    GenericGraphicsViewer viewer = null;
    // get the generic graphics viewer part
    if (workbenchPart instanceof GenericGraphicsEditor) {
      viewer = ((GenericGraphicsEditor) workbenchPart).getGraphicalViewer();
    } else if (workbenchPart instanceof GenericFormEditor) {
      viewer = ((GenericFormEditor) workbenchPart).getGraphicalEditor().getGraphicalViewer();
    } else {
      IContentOutlinePage outline = workbenchPart.getAdapter(IContentOutlinePage.class);
      if (outline instanceof CombinedGraphicsOutlinePage) {
        viewer = ((CombinedGraphicsOutlinePage) outline).getGraphicalOutline().getViewer();
      }
    }
    
    // choose the layout root edit part
    IMCEditPart layoutRootPart = null;
    if (diagramPart instanceof IMCEditPart) {
      layoutRootPart = (IMCEditPart) diagramPart;
    }
    
    if (layoutRootPart == null && viewer != null) {
      layoutRootPart = viewer.getContentEditPart();
    }
     
    if (layoutRootPart == null) {
      throw new UnsupportedOperationException("Not supported by this layout manager: Workbench part " + workbenchPart + ", Edit part " + diagramPart);
    }
    
    // set optional diagram editor
    if (viewer != null) {
      mapping.setProperty(GENERIC_VIEWER, viewer);
    }
    
    // set top level element
    KNode topNode = KimlUtil.createInitializedNode();
    KShapeLayout topLayout = topNode.getData(KShapeLayout.class);
    
    GraphicalEditPart top = (GraphicalEditPart) layoutRootPart;
    Rectangle topBounds = top.getFigure().getBounds();
    topLayout.setPos(topBounds.x, topBounds.y);
    topLayout.setSize(topBounds.width, topBounds.height);
    
    mapping.getGraphMap().put(topNode, layoutRootPart);
    mapping.setLayoutGraph(topNode);
    mapping.setParentElement(layoutRootPart);
    
    buildRecursively(mapping, layoutRootPart, topNode);
    
    // after all editparts have been added, add connections
    // and their labels
    // all the connections need to have:
    // -- IMCEditParts that are already added as parents
    // -- PolylineConnections as figures
    addAllConnections(mapping);
    
    // create layout configurators for Generic Graphics Editors
    mapping.getLayoutConfigs().add(layoutConfig);
    mapping.getLayoutConfigs().add(mapping.getProperty(STATIC_CONFIG));
    
    return mapping;
  }
  
  private void buildRecursively(LayoutMapping<IMCEditPart> mapping, IMCEditPart current, KNode parent) {
    
    for (Object child : current.getChildren()) {
      
      // add to the list of possible editparts with connections.
      if (child instanceof IMCGraphicalEditPart) {
        mapping.getProperty(CONNECTIONS).add((IMCGraphicalEditPart) child);
      }
      
      // we only want to layout view elements
      // if its not an IMCViewElementEditPart:
      // skip this level and add the next VEEditPart you find
      // the parent KNode in KGraph will be the current parent node
      if (!(child instanceof IMCShapeEditPart)) {
        // as long as it's an IMCEditPart
        if (child instanceof IMCEditPart) {
          buildRecursively(mapping, (IMCEditPart) child, parent);
        }
        continue;
      }
      
      IMCShapeEditPart ep = (IMCShapeEditPart) child;
      
      KNode node = KimlUtil.createInitializedNode();
      KShapeLayout shapeLayout = node.getData(KShapeLayout.class);
      node.setParent(parent);
      
      setKShapeLayout(shapeLayout, ep);
      
      mapping.getGraphMap().put(node, ep);
      
      // the modification flag must initially be false
      ((KShapeLayoutImpl) shapeLayout).resetModificationFlag();
      
      // add all its children
      buildRecursively(mapping, ep, node);
      
    }
  }
  
  @Override
  public void applyLayout(LayoutMapping<IMCEditPart> mapping, boolean zoomToFit, int animationTime) {
    Object layoutGraphObj = mapping.getParentElement();
    if (layoutGraphObj instanceof IMCEditPart) {
      ScalableRootEditPart rep = (ScalableRootEditPart) ((IMCEditPart) layoutGraphObj).getRoot();
      
      ZoomManager zoomManager = rep.getZoomManager();
      KNode parentNode = mapping.getLayoutGraph();
      KShapeLayout parentLayout = parentNode.getData(KShapeLayout.class);
      Dimension available = zoomManager.getViewport().getClientArea().getSize();
      
      float desiredWidth = parentLayout.getWidth();
      double scaleX = Math.min(available.width / desiredWidth, zoomManager.getMaxZoom());
      float desiredHeight = parentLayout.getHeight();
      double scaleY = Math.min(available.height / desiredHeight, zoomManager.getMaxZoom());
      
      final double scale = Math.min(scaleX, scaleY);
      
      applyLayout(mapping, zoomManager.getZoom());
      if (zoomToFit) {
        zoomManager.setViewLocation(new Point(0, 0));
        zoomManager.setZoom(scale);
        zoomManager.setViewLocation(new Point(0, 0));
      }
    }
    else {
      // assume 100% zoom level since we don't have any information
      applyLayout(mapping, 1.0);
    }
  }
  
  /**
   * Applies the computed layout to the original diagram.
   * 
   * @param mapping a layout mapping that was created by this layout manager
   */
  private void applyLayout(LayoutMapping<IMCEditPart> mapping, double scale) {
    // create a new command and execute it
    // use relative bendpoints
    Command applyCommand = new ApplyLayoutCommand(mapping, false, scale);
    
    GenericGraphicsViewer viewer = mapping.getProperty(GENERIC_VIEWER);
    
    if (viewer != null) {
      CommandStack cStack = viewer.getEditDomain().getCommandStack();
      cStack.execute(applyCommand);
    } else {
      Log.error("0xA1113 MCDiagramLayoutManager: no Editor was found! Cannot execute apply layout command!");
    }
  }
  
  @Override
  public void undoLayout(LayoutMapping<IMCEditPart> mapping) {
    
  }
  
  /**
   * <p>
   * Add all connections to the mapping.
   * </p>
   * <p>
   * Therefore iterate through all added IMCEditParts and check for outgoing
   * connections.
   * </p>
   * 
   * @param mapping The {@link LayoutMapping} to add all connections to.
   */
  private void addAllConnections(LayoutMapping<IMCEditPart> mapping) {
    BiMap<KGraphElement, IMCEditPart> graphMap = mapping.getGraphMap();
    Map<KEdge, IMCEditPart> newCons = new HashMap<KEdge, IMCEditPart>();
    
    for (IMCGraphicalEditPart ep : mapping.getProperty(CONNECTIONS)) {
      // add all source connections
      for (Object c : ep.getSourceConnections()) {
        
        // add connections only, if they have an associated ViewElement
        // check for IMCConnectionEditPart and IMCViewElementEditPart
        // is necessary to ensure that computed bendpoints can be applied
        if (!(c instanceof IMCConnectionEdgeEditPart)) {
          continue;
        }
        
        IMCConnectionEdgeEditPart cep = (IMCConnectionEdgeEditPart) c;
        
        // find a proper source node and source port
        KGraphElement sourceElem = null;
        EditPart sourceObj = cep.getSource();
        if (sourceObj instanceof IMCEditPart) {
          sourceElem = graphMap.inverse().get(sourceObj);
        }
        
        KNode sourceNode = null;
        KPort sourcePort = null;
        if (sourceElem instanceof KNode) {
          sourceNode = (KNode) sourceElem;
        }
        else if (sourceElem instanceof KPort) {
          sourcePort = (KPort) sourceElem;
          sourceNode = sourcePort.getNode();
        }
        else {
          continue;
        }
        
        // find a proper target node and target port
        KGraphElement targetElem = null;
        EditPart targetObj = cep.getTarget();
        if (targetObj instanceof IMCEditPart) {
          targetElem = graphMap.inverse().get(targetObj);
        }
        
        KNode targetNode = null;
        KPort targetPort = null;
        if (targetElem instanceof KNode) {
          targetNode = (KNode) targetElem;
        }
        else if (targetElem instanceof KPort) {
          targetPort = (KPort) targetElem;
          targetNode = targetPort.getNode();
        }
        else {
          continue;
        }
        
        KEdge edge = KimlUtil.createInitializedEdge();
        
        if (sourceNode == null || targetNode == null) {
          System.err.println("MCDLM> source: " + sourceNode + " target: " + targetNode + "! Skip this connection!");
          continue;
        }
        
        // set source and target
        edge.setSource(sourceNode);
        if (sourcePort != null) {
          edge.setSourcePort(sourcePort);
        }
        edge.setTarget(targetNode);
        if (targetPort != null) {
          edge.setTargetPort(targetPort);
        }
        
        newCons.put(edge, cep);
        
        // store the current coordinates of the edge
        KEdgeLayout edgeLayout = edge.getData(KEdgeLayout.class);
        setKEdgeLayout(edgeLayout, cep.getViewElement(), ((PolylineConnection) cep.getFigure()));
        
        if (cep instanceof IConnectionType) {
          IConnectionType.Connection_Type type = ((IConnectionType) cep).getConnectionType();
          if (type != null) {
            switch (type) {
              case ASSOCIATION:
                edgeLayout.setProperty(LayoutOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
                break;
              case DEPENDENCY:
                edgeLayout.setProperty(LayoutOptions.EDGE_TYPE, EdgeType.DEPENDENCY);
                break;
              case GENERALIZATION:
                edgeLayout.setProperty(LayoutOptions.EDGE_TYPE, EdgeType.GENERALIZATION);
                break;
              default:
                edgeLayout.setProperty(LayoutOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
                break;
            }
          }
          else {
            edgeLayout.setProperty(LayoutOptions.EDGE_TYPE, EdgeType.ASSOCIATION);
          }
        }
        // the modification flag must initially be false
        ((KEdgeLayoutImpl) edgeLayout).resetModificationFlag();
        
        // check for labels to add
        addAllLabels(mapping, cep, edge);
      } // end-for souceConnections
    } // end-for editparts
    
    // add the connections to the graphMap
    graphMap.putAll(newCons);
  }
  
  /**
   * <p>
   * Add all labels to the mapping.
   * </p>
   * <p>
   * Therefore iterates through all children of the
   * {@link IMCConnectionViewElementEditPart} and checks if instance of
   * {@link TextConnectionLabelEditPart}. If so, add it as {@link KLabel} to the
   * KGraph.
   * </p>
   * 
   * @param mapping The {@link LayoutMapping} to add all connections to
   * @param cep The {@link IMCConnectionViewElementEditPart} to take the labels
   *          from.
   * @param parent The parant {@link KLabeledGraphElement parent} in the KGraph
   */
  private void addAllLabels(LayoutMapping<IMCEditPart> mapping, IMCConnectionEditPart cep, KLabeledGraphElement parent) {
    
    for (Object child : cep.getChildren()) {
      
      // if the child is not a connection label editpart then continue
      if (!(child instanceof TextConnectionLabelEditPart)) {
        continue;
      }
      
      TextConnectionLabelEditPart clep = (TextConnectionLabelEditPart) child;
      
      TextConnectionLabel cLabel = (TextConnectionLabel) clep.getModel();
      // IShapeViewElement sve = (IShapeViewElement) clep.getViewElement();
      
      // check if label has text to display
      if (cLabel.getTexts().isEmpty()) {
        continue;
      }
      
      String text = cLabel.getString();
      ConnectionLocatorPosition position = cLabel.getPosition();
      Font font = cLabel.getFont();
      
      if (position == null) {
        continue;
      }
      
      KLabel kLabel = KimlUtil.createInitializedLabel(parent);
      kLabel.setText(text);
      VolatileLayoutConfig staticConfig = mapping.getProperty(STATIC_CONFIG);
      
      // set position of label in config
      switch (position) {
        case BEFORE_SOURCE_UP:
          staticConfig.setValue(LayoutOptions.EDGE_LABEL_PLACEMENT, kLabel, LayoutContext.GRAPH_ELEM, EdgeLabelPlacement.TAIL);
          break;
        case BEFORE_SOURCE_DOWN:
          staticConfig.setValue(LayoutOptions.EDGE_LABEL_PLACEMENT, kLabel, LayoutContext.GRAPH_ELEM, EdgeLabelPlacement.TAIL);
          break;
        case SOURCE:
          staticConfig.setValue(LayoutOptions.EDGE_LABEL_PLACEMENT, kLabel, LayoutContext.GRAPH_ELEM, EdgeLabelPlacement.TAIL);
          break;
        case MIDDLE_DOWN:
          staticConfig.setValue(LayoutOptions.EDGE_LABEL_PLACEMENT, kLabel, LayoutContext.GRAPH_ELEM, EdgeLabelPlacement.CENTER);
          break;
        case MIDDLE_UP:
          staticConfig.setValue(LayoutOptions.EDGE_LABEL_PLACEMENT, kLabel, LayoutContext.GRAPH_ELEM, EdgeLabelPlacement.CENTER);
          break;
        case BEFORE_TARGET_UP:
          staticConfig.setValue(LayoutOptions.EDGE_LABEL_PLACEMENT, kLabel, LayoutContext.GRAPH_ELEM, EdgeLabelPlacement.HEAD);
          break;
        case BEFORE_TARGET_DOWN:
          staticConfig.setValue(LayoutOptions.EDGE_LABEL_PLACEMENT, kLabel, LayoutContext.GRAPH_ELEM, EdgeLabelPlacement.HEAD);
          break;
        case TARGET:
          staticConfig.setValue(LayoutOptions.EDGE_LABEL_PLACEMENT, kLabel, LayoutContext.GRAPH_ELEM, EdgeLabelPlacement.HEAD);
          break;
        default:
          break;
      }
      
      // set font of label in config
      if (font != null && !font.isDisposed()) {
        staticConfig.setValue(LayoutOptions.FONT_NAME, kLabel, LayoutContext.GRAPH_ELEM, font.getFontData()[0].getName());
        staticConfig.setValue(LayoutOptions.FONT_SIZE, kLabel, LayoutContext.GRAPH_ELEM, font.getFontData()[0].getHeight());
      }
      
      KShapeLayout layout = kLabel.getData(KShapeLayout.class);
      setKShapeLayout(layout, clep);
      
      // reset modification flag
      ((KShapeLayoutImpl) layout).resetModificationFlag();
      
      mapping.getGraphMap().put(kLabel, clep);
    }
  }
  
  /**
   * <p>
   * Sets the values of the {@link KShapeLayout} according to the given
   * {@link IMCShapeEditPart} with a {@link IShapeViewElement}.
   * </p>
   * <p>
   * If the width & height of the {@link IShapeViewElement} are
   * <code>-1 (or below)</code>, then the preferred size of the {@link IFigure}
   * of the {@link IMCShapeEditPart} is applied.
   * </p>
   * 
   * @param s The {@link KShapeLayout}
   * @param ep The {@link IMCShapeEditPart} that stores a
   *          {@link IShapeViewElement} and a {@link IFigure} for setting the
   *          preferred size.
   */
  private void setKShapeLayout(KShapeLayout s, IMCShapeEditPart ep) {
    
    IViewElement ve = ep.getViewElement();
    if (ve instanceof IShapeViewElement) {
      IShapeViewElement sve = (IShapeViewElement) ve;
      s.setPos(sve.getX(), sve.getY());
      
      Dimension size = ep.getFigure().getPreferredSize();
      
      // if width < 0, set preferred width
      if (sve.getWidth() < 0) {
        s.setWidth(size.width);
      }
      else {
        s.setWidth(sve.getWidth());
      }
      
      // if height < 0, set preferred height
      if (sve.getHeight() < 0) {
        s.setHeight(size.height);
      }
      else {
        s.setHeight(sve.getHeight());
      }
    }
  }
  
  /**
   * Sets the values of the {@link KEdgeLayout} according to the given
   * {@link IEdgeViewElement}.
   * 
   * @param e The {@link KEdgeLayout}
   * @param eve The {@link IEdgeViewElement}
   */
  private void setKEdgeLayout(KEdgeLayout e, IEdgeViewElement eve, PolylineConnection con) {
    List<MCBendpoint> bpList = eve.getConstraints();
    
    Point start = con.getStart();
    Point end = con.getEnd();
    
    KPoint sourcePoint = e.getSourcePoint();
    sourcePoint.setX(start.x);
    sourcePoint.setY(start.y);
    
    EList<KPoint> kBPList = e.getBendPoints();
    
    for (int i = 1; i < bpList.size() - 1; i++) {
      MCBendpoint bp = bpList.get(i);
      KPoint kpoint = KLayoutDataFactory.eINSTANCE.createKPoint();
      
      if (bp.isAbsolute()) {
        // absolute bendpoint, nothing special todo here
        Point a = bp.getAbsolutePoint();
        kpoint.setX(a.x);
        kpoint.setY(a.y);
      }
      else {
        // we need to translate the relative point into an absolute one
        Point refS = con.getSourceAnchor().getReferencePoint();
        Dimension relStart = bp.getRelativeStart();
        
        kpoint.setX(refS.x + relStart.width);
        kpoint.setY(refS.y + relStart.height);
      }
      kBPList.add(kpoint);
    }
    KPoint targetPoint = e.getTargetPoint();
    targetPoint.setX(end.x);
    targetPoint.setY(end.y);
  }
}
