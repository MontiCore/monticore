/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.layout.kieler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.commands.Command;

import de.cau.cs.kieler.core.kgraph.KEdge;
import de.cau.cs.kieler.core.kgraph.KGraphElement;
import de.cau.cs.kieler.core.kgraph.KLabel;
import de.cau.cs.kieler.core.kgraph.KNode;
import de.cau.cs.kieler.kiml.klayoutdata.KEdgeLayout;
import de.cau.cs.kieler.kiml.klayoutdata.KPoint;
import de.cau.cs.kieler.kiml.klayoutdata.KShapeLayout;
import de.cau.cs.kieler.kiml.ui.diagram.LayoutMapping;
import de.monticore.genericgraphics.controller.editparts.IMCConnectionEdgeEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCGraphicalEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCShapeEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCViewElementEditPart;
import de.monticore.genericgraphics.model.graphics.IEdgeViewElement;
import de.monticore.genericgraphics.model.graphics.IShapeViewElement;
import de.monticore.genericgraphics.model.graphics.IViewElement;
import de.monticore.genericgraphics.view.figures.connections.MCBendpoint;

/**
 * <p>
 * A {@link Command} for application of a {@link LayoutMapping}.
 * </p>
 * <p>
 * This command applies the computed view data in the form of a
 * {@link LayoutMapping} from the KIELER project to the {@link IViewElement
 * IViewElements} of {@link IMCViewElementEditPart IMCViewElementEditParts}. <br>
 * see
 * http://rtsys.informatik.uni-kiel.de/confluence/pages/viewpage.action?pageId
 * =328078
 * </p>
 * 
 * @author Tim Enger
 */
public class ApplyLayoutCommand extends Command {
  
  private LayoutMapping<IMCEditPart> mapping;
  
  // store view element values for undo
  private Map<IMCEditPart, IViewElement> undoMap;
  
  // indicates if absolute or relative bendpoints should
  // be created
  private boolean absoluteBPs;
  
  // apparently the data of the figures it not updated immediately
  // so use the VE element data to compute relative bendpoints
  // it's a workaround until someone finds out how to
  // update the figures before creating bendpoints
  // so at the moment we need to store a mapping from ve to figures
  // to use it, when creating bendpoints
  private Map<IFigure, IShapeViewElement> figs;
  
  private double zoomFactor;
  
  /**
   * Constructor
   * 
   * @param mapping The {@link LayoutMapping} to apply.
   * @param absoluteBPs If <tt>true</tt> absolute bendpoints are created for the
   *          connections, otherwise relative ones.
   * @param zoomFactor The current zoom factor of the editor, important for
   *          relative bendpoint creation
   */
  public ApplyLayoutCommand(LayoutMapping<IMCEditPart> mapping, boolean absoluteBPs, double zoomFactor) {
    this.mapping = mapping;
    this.absoluteBPs = absoluteBPs;
    this.zoomFactor = zoomFactor;
    createUndoMap();
    
    figs = new HashMap<IFigure, IShapeViewElement>();
  }
  
  @Override
  public void execute() {
    
    Map<KShapeLayout, IMCShapeEditPart> shapes = new HashMap<KShapeLayout, IMCShapeEditPart>();
    
    Map<KEdgeLayout, IMCConnectionEdgeEditPart> edges = new HashMap<KEdgeLayout, IMCConnectionEdgeEditPart>();
    
    Map<KShapeLayout, IMCShapeEditPart> labels = new HashMap<KShapeLayout, IMCShapeEditPart>();
    
    // first collect all data
    for (Entry<KGraphElement, IMCEditPart> e : mapping.getGraphMap().entrySet()) {
      KGraphElement ge = e.getKey();
      IMCEditPart ep = e.getValue();
      
      if (ep instanceof IMCShapeEditPart) {
        IShapeViewElement sve = ((IMCShapeEditPart) ep).getViewElement();
        if (ge instanceof KNode) {
          KShapeLayout layout = ((KNode) ge).getData(KShapeLayout.class);
          shapes.put(layout, (IMCShapeEditPart) ep);
          figs.put(((IMCGraphicalEditPart) ep).getFigure(), sve);
        }
        else if (ge instanceof KLabel) {
          KShapeLayout layout = ((KLabel) ge).getData(KShapeLayout.class);
          labels.put(layout, (IMCShapeEditPart) ep);
        }
      }
      else if (ep instanceof IMCConnectionEdgeEditPart && ge instanceof KEdge) {
        KEdgeLayout layout = ((KEdge) ge).getData(KEdgeLayout.class);
        edges.put(layout, (IMCConnectionEdgeEditPart) ep);
      }
    }
    
    // process shapes
    for (Entry<KShapeLayout, IMCShapeEditPart> entry : shapes.entrySet()) {
      setShapeViewElement(entry.getValue(), entry.getKey());
    }
    
    // process edges
    for (Entry<KEdgeLayout, IMCConnectionEdgeEditPart> entry : edges.entrySet()) {
      IMCConnectionEdgeEditPart ep = entry.getValue();
      KEdgeLayout layout = entry.getKey();
      
      setEdgeViewElement(ep, layout);
      
      // we have to manually set the points of the connection in order to
      // use relative locators
      // if we would not do this here, the connection would not be updated
      // before the Label locators ask for start & end point
      // took me only one night to find that out and try
      // a lot of different other ways in order to avoid the following
      // which cannot be avoided, or I missed something :)
      PolylineConnection con = (PolylineConnection) ep.getFigure();
      PointList points = new PointList();
      
      points.addPoint(new Point(Math.round(layout.getSourcePoint().getX()), Math.round(layout.getSourcePoint().getY())));
      for (KPoint point : layout.getBendPoints()) {
        points.addPoint(new Point(Math.round(point.getX()), Math.round(point.getY())));
      }
      points.addPoint(new Point(Math.round(layout.getTargetPoint().getX()), Math.round(layout.getTargetPoint().getY())));
      
      con.setPoints(points);
      con.getParent().revalidate();
      con.getParent().repaint();
    }
    
    // process the labels
    for (Entry<KShapeLayout, IMCShapeEditPart> entry : labels.entrySet()) {
      setShapeViewElement(entry.getValue(), entry.getKey());
    }
  }
  
  @Override
  public boolean canUndo() {
    return true;
  }
  
  @Override
  public void undo() {
    for (Entry<IMCEditPart, IViewElement> entry : undoMap.entrySet()) {
      IMCEditPart ep = entry.getKey();
      
      if (!(ep instanceof IMCViewElementEditPart)) {
        continue;
      }
      
      IViewElement ve = ((IMCViewElementEditPart) ep).getViewElement();
      IViewElement oldVE = entry.getValue();
      
      if (ve instanceof IShapeViewElement && oldVE instanceof IShapeViewElement) {
        IShapeViewElement sve = (IShapeViewElement) ve;
        IShapeViewElement oldSVE = (IShapeViewElement) oldVE;
        
        sve.setX(oldSVE.getX());
        sve.setY(oldSVE.getY());
        sve.setWidth(oldSVE.getWidth());
        sve.setHeight(oldSVE.getHeight());
        sve.notifyObservers();
      }
      else if (ve instanceof IEdgeViewElement && oldVE instanceof IEdgeViewElement) {
        IEdgeViewElement eve = (IEdgeViewElement) ve;
        IEdgeViewElement oldCVE = (IEdgeViewElement) oldVE;
        
        eve.setConstraints(oldCVE.getConstraints());
        eve.notifyObservers();
      }
    }
  }
  
  /**
   * creates the undo map, that stores the "old" {@link IViewElement} values for
   * every {@link IMCEditPart} that is member of the mapping.
   */
  private void createUndoMap() {
    undoMap = new HashMap<IMCEditPart, IViewElement>();
    
    for (IMCEditPart ep : mapping.getGraphMap().values()) {
      if (ep instanceof IMCViewElementEditPart) {
        IMCViewElementEditPart vep = (IMCViewElementEditPart) ep;
        undoMap.put(vep, (IViewElement) vep.getViewElement().clone());
      }
    }
  }
  
  /**
   * Sets the values of the {@link IEdgeViewElement} according to the given
   * {@link KEdgeLayout}.
   * 
   * @param cve The {@link IShapeViewElement}
   * @param e The {@link KEdgeLayout}
   */
  private void setEdgeViewElement(IMCConnectionEdgeEditPart ep, KEdgeLayout e) {
    
    IEdgeViewElement eve = ep.getViewElement();
    PolylineConnection con = (PolylineConnection) ep.getFigure();
    
    List<MCBendpoint> points = new ArrayList<MCBendpoint>();
    // set the view element according to layout data
    for (KPoint p : e.getBendPoints()) {
      MCBendpoint bp;
      if (absoluteBPs) {
        bp = new MCBendpoint(new Point((int) p.getX(), (int) p.getY()));
      }
      else {
        bp = createRelativeBendpoint(p, con);
      }
      points.add(bp);
    }
    
    eve.setConstraints(points);
    eve.notifyObservers();
  }
  
  /**
   * Sets the values of the {@link IShapeViewElement} according to the given
   * {@link KShapeLayout}.
   * 
   * @param sve The {@link IShapeViewElement}
   * @param s The {@link KShapeLayout}
   */
  private void setShapeViewElement(IMCShapeEditPart ep, KShapeLayout s) {
    IShapeViewElement sve = ep.getViewElement();
    
    // set the view element according to layout data
    sve.setX(Math.round(s.getXpos()));
    sve.setY(Math.round(s.getYpos()));
    
    // preferred size it used in VE, then leave it as it is
    // if (sve.getWidth() > 0) {
    // sve.setWidth(Math.round(s.getWidth()));
    // }
    // if (sve.getHeight() > 0) {
    // sve.setHeight(Math.round(s.getHeight()));
    // }
    
    // TODO
    // the size is not computes correctly and I don't know why,
    // so leave the preferred size for the moment
    
    sve.setWidth(-1);
    sve.setHeight(-1);
    
    sve.notifyObservers();
  }
  
  private MCBendpoint createRelativeBendpoint(KPoint point, PolylineConnection con) {
    Point newL = new Point((int) point.getX(), (int) point.getY());
    
    con.translateToRelative(newL);
    
    // apparently the data of the figures it not updated immediately
    // so use the VE element data to compute relative bendpoints
    // it's a workaround until someone finds out how to
    // update the figures before creating bendpoints
    
    IFigure startFig = con.getSourceAnchor().getOwner();
    IShapeViewElement startVE = figs.get(startFig);
    Point ref1;
    if (startVE != null) {
      Dimension size = startFig.getPreferredSize();
      ref1 = new Point((int) (startVE.getX() + 0.5 * size.width), (int) (startVE.getY() + 0.5 * size.height));
    }
    else {
      ref1 = con.getSourceAnchor().getReferencePoint();
    }
    
    IFigure targetFig = con.getTargetAnchor().getOwner();
    IShapeViewElement targetVE = figs.get(targetFig);
    Point ref2;
    if (targetVE != null) {
      Dimension size = targetFig.getPreferredSize();
      ref2 = new Point((int) (targetVE.getX() + 0.5 * size.width), (int) (targetVE.getY() + 0.5 * size.height));
    }
    else {
      ref2 = con.getTargetAnchor().getReferencePoint();
    }
    
    con.translateToRelative(ref1);
    con.translateToRelative(ref2);
    
    Dimension relStart = newL.getDifference(ref1);
    Dimension relTarget = newL.getDifference(ref2);
    
    // bendpoints seems to dependent on the zoom level
    // so scale them
    relStart.scale(zoomFactor);
    relTarget.scale(zoomFactor);
    
    return new MCBendpoint(relStart, relTarget);
  }
}
