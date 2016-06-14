/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, 2016, MontiCore, All rights reserved.
 *  
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package de.monticore.genericgraphics.controller.views.outline;

import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LayoutListener;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.geometry.PrecisionRectangle;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

import de.monticore.genericgraphics.GenericGraphicsViewer;

/**
 * Depending on the {@link GenericGraphicsViewer} assigned in the constructor, 
 * this ContentOutlinePage either displays a graphical viewer or an overview of that 
 * viewer. If the viewer's control has been created already (which means it has already
 * been added to an editor for example), a thumbnail of the viewer is displayed as an
 * overview. Otherwise, the viewer's control is created in
 * {@link #createControl(Composite)} and the viewer gets configured, initialized and
 * displayed.
 * <br>
 * 
 * @author Philipp Kehrbusch
 *
 */
public class GraphicalOutlinePage extends ContentOutlinePage {

  private GenericGraphicsViewer viewer;
  private DefaultEditDomain editDomain;
  
  private ScrollableThumbnail thumbnail;
  private DisposeListener disposeListener;
  
  private LightweightSystem lws;
  private SashForm sash;
  private Canvas canvas;
  
  private LayoutListener layoutListener;
  private IFigure rootFigure;
  private double zoom;
  private boolean hasBeenLayouted = false;
  private boolean outlineHasBeenShown = false;
  
  private boolean externalViewer = false;
  
  public GraphicalOutlinePage(GenericGraphicsViewer viewer, IFile input) {
    this.viewer = viewer;
    
    editDomain = new DefaultEditDomain(null);
  }
  
  @Override
  public void createControl(Composite parent) {    
    // manage own graphical viewer and display it
    if(viewer != null && viewer.getControl() ==  null) {
      externalViewer = false;
      viewer.createControl(parent);
      editDomain.addViewer(viewer);
      viewer.configure();
      viewer.refreshContents();

      // Add LayoutListener to root figure to calculate the zoom factor needed to
      // fit the diagram to the view.
      if(viewer.getRootEditPart().getContents() instanceof AbstractGraphicalEditPart) {
        final ContentOutline outlineView = (ContentOutline)GraphicalOutlinePage.this.getSite().getPage().findView("org.eclipse.ui.views.ContentOutline"); 
        
        if(outlineView != null) {
          rootFigure = ((AbstractGraphicalEditPart)viewer.getRootEditPart().getContents()).getFigure();
          
          layoutListener = new LayoutListener() {
            public void postLayout(IFigure container) {
              Rectangle diagramBounds = new PrecisionRectangle(container.getBounds());
              container.translateToAbsolute(diagramBounds);
              org.eclipse.swt.graphics.Rectangle outlineBounds = outlineView.getCurrentPage().getControl().getBounds();
              zoom = Math.min((double)outlineBounds.width/(double)diagramBounds.width, (double)outlineBounds.height/(double)diagramBounds.height);
              hasBeenLayouted = true;
              
              // if the outline has been shown before the layout was processed, apply zoom now
              if(outlineHasBeenShown) {
                rootFigure.removeLayoutListener(this);
                ((ScalableRootEditPart)viewer.getRootEditPart()).getZoomManager().setZoom(zoom);
              }
            }

            public void invalidate(IFigure figure) {}
            public boolean layout(IFigure figure) {return false;}
            public void remove(IFigure figure) {}
            public void setConstraint(IFigure figure, Object obj) {}
          };
          
          rootFigure.addLayoutListener(layoutListener);
        }
      }
           
      // create popup menu
      createPopupMenu(viewer.getControl());
    }
    else if (viewer != null) {
      externalViewer = true;
      createOverview(parent);
      createPopupMenu(canvas);
    }
  }
  
  /**
   * Assigns a model file to this OutlinePage's viewer
   * @param file  File containing the model data
   */
  public void setInputFile(IFile file) {
    if(!externalViewer && viewer != null)
      viewer.setInput(file);
  }
  
  private void createOverview(Composite parent) {
    sash = new SashForm(parent, SWT.VERTICAL);
    canvas = new Canvas(sash, SWT.NONE);
    lws = new LightweightSystem(canvas);

    refreshContents();
  }
  
  private void refreshContents() {
    if (viewer != null) {
      // miniature view
      if(viewer.getRootEditPart() instanceof ScalableRootEditPart) {
        thumbnail = new ScrollableThumbnail((Viewport) ((ScalableRootEditPart) viewer.getRootEditPart()).getFigure());
        thumbnail.setSource(((ScalableRootEditPart) viewer.getRootEditPart()).getLayer(LayerConstants.PRINTABLE_LAYERS));
      }
      else if(viewer.getRootEditPart() instanceof ScalableFreeformRootEditPart) {
        thumbnail = new ScrollableThumbnail((Viewport) ((ScalableFreeformRootEditPart) viewer.getRootEditPart()).getFigure());
        thumbnail.setSource(((ScalableFreeformRootEditPart) viewer.getRootEditPart()).getLayer(LayerConstants.PRINTABLE_LAYERS));
      }
      
      lws.setContents(thumbnail);
      disposeListener = new DisposeListener() {
        @Override
        public void widgetDisposed(DisposeEvent e) {
          if (thumbnail != null) {
            thumbnail.deactivate();
            thumbnail = null;
          }
        }
      };
      viewer.getControl().addDisposeListener(disposeListener);
    }
  }
  
  /** Creates the context menu, attaches it to a specified control
   * and registers it to the {@link IPageSite}.
   * 
   * @param control The {@link Control}
   */
  private void createPopupMenu(Control control) {
    MenuManager manager = new MenuManager();
    
    if(control != null) {
      Menu context = manager.createContextMenu(control);
      control.setMenu(context);
      getSite().registerContextMenu("outlineContext", manager, this);
    }
  }
  
  @Override
  public Control getControl() {
    if(externalViewer)
      return sash;
    else if(viewer != null)
      return viewer.getControl();
    else
    	return null;
  }
  
  @Override
  public void dispose() {
    if (viewer != null) {
      if (viewer.getControl() != null && !viewer.getControl().isDisposed()) {
        if (disposeListener != null) {
          viewer.getControl().removeDisposeListener(disposeListener);
        }
      }
      
      if(!externalViewer)
        viewer.dispose();
    }
    super.dispose();
  }
  
  @Override
  public void setFocus() {
    if(externalViewer && sash != null)
      sash.setFocus();
  }
  
  public GenericGraphicsViewer getViewer() {
    return viewer;
  }
  
  public void setViewer(GenericGraphicsViewer newViewer) {
    if (viewer != null) {
      viewer.dispose();
    }
    this.viewer = newViewer;
  }
  
  /**
   * Informs this graphical outline page that it has been displayed
   */
  public void shown() {
    outlineHasBeenShown = true;
    
    /* Remove layout listener only after the viewer has actually been displayed the
     * the first time. This ensures that the zoom on the viewer is updated until the
     * viewer is actually displayed.
     */
    if(layoutListener != null && rootFigure != null && hasBeenLayouted) {
      rootFigure.removeLayoutListener(layoutListener);
      layoutListener = null;
      ZoomManager zoomManager = ((ScalableRootEditPart)viewer.getRootEditPart()).getZoomManager();
      zoomManager.setZoom(zoom);
    }
  }

  public CommandStack getCommandStack() {
    if(editDomain != null)
      return editDomain.getCommandStack();
    else
      return null;
  }
}
