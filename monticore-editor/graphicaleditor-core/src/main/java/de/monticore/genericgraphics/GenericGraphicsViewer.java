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
package de.monticore.genericgraphics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.KeyStroke;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.Saveable;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import de.monticore.editorconnector.GraphicsTextSelectionListener;
import de.monticore.genericgraphics.controller.editparts.IMCEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCViewElementEditPart;
import de.monticore.genericgraphics.controller.editparts.MCEditPartFactory;
import de.monticore.genericgraphics.controller.persistence.DefaultGraphicsLoader;
import de.monticore.genericgraphics.controller.persistence.ErrorCollector;
import de.monticore.genericgraphics.controller.persistence.IGraphicsLoader;
import de.monticore.genericgraphics.controller.persistence.util.IPersistenceUtil;
import de.monticore.genericgraphics.controller.selection.SelectionSyncException;
import de.monticore.genericgraphics.controller.util.ASTNodeProblemReportHandler;
import de.monticore.genericgraphics.controller.views.outline.GraphicalOutlinePage;
import de.monticore.genericgraphics.view.layout.ILayoutAlgorithm;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.langeditor.injection.DIService;
import de.se_rwth.langeditor.modelstates.ModelState;
import de.se_rwth.langeditor.modelstates.ObservableModelStates;
import de.se_rwth.langeditor.texteditor.TextEditorImpl;

/**
 * <p>
 * The Generic Graphical Viewer for MontiCore Class Diagrams.
 * </p>
 * <p>
 * The Generic Graphical Viewer makes use of the following utils:
 * <ul>
 * <li>{@link IGraphicsLoader}: for loading and saving the model and view data</li>
 * <li>{@link SelectionSynchronizer}: for synchronizing selection between multiple editors</li>
 * <li>{@link ILayoutAlgorithm}: for layouting nodes of the diagram.</li>
 * <li>A ResourceTracker: for reacting on model file changes due to other editors
 * <ul>
 * <li>File contents changed and was saved => refresh content of this editor</li>
 * <li>File was renamed => reload the file. Note: the view file is not moved automatically.</li>
 * <li>File was deleted => close editor.</li>
 * </ul>
 * </li>
 * </ul>
 * </p>
 * <p>
 * Different functionality is provided by the Generic Graphical Viewer without changing anything,
 * and just implementing the abstract methods:
 * <ul>
 * <li>Loading of model data/view data and combination of both</li>
 * <li>Redo / Undo functionality for nearly all actions</li>
 * <li>Observing the underlying model file for changes and updating correctly</li>
 * <li>Providing a Overview View for the Editor</li>
 * <li>Showing Problems during parsing in Problems view</li>
 * <li>Show Problems in Figures</li>
 * <li>Providing printing functionality</li>
 * <li>Providing zoom functionality</li>
 * <li>Providing export to jpg, ico, bmp, gif, png -images</li>
 * </ul>
 * </p>
 * <p>
 * There are several methods that allow specific configuration, without the need to change any of
 * the existing methods.<br>
 * <br>
 * The following methods need to be implemented to provide functionality needed by the editor:
 * <ul>
 * <li>{@link #createEditPartFactory()}</li>
 * <li>{@link #createPersistenceUtil()}</li>
 * <li>{@link #createDSLTool(String[])}</li>
 * <li>{@link #createLayoutAlgorithm()}</li>
 * <li>{@link #getContents()}</li>
 * </ul>
 * Furthermore, the following methods allow to customize the process of loading model data
 * <ul>
 * <li>{@link #beforeModelLoad()}</li>
 * <li>{@link #afterModelLoadBeforeViewLoad()}</li>
 * <li>{@link #afterViewLoad()}</li>
 * </ul>
 * These methods are called as follows:
 * <ul>
 * <li>{@link #beforeModelLoad()}</li>
 * <li>loading of model data</li>
 * <li>{@link #afterModelLoadBeforeViewLoad()}</li>
 * <li>loading of view information</li>
 * <li>{@link #afterViewLoad()}</li>
 * <li>contents of GraphicalViewer is set with {@link #getContents()}</li>
 * <li>combination of model and view information with {@link #createLayoutAlgorithm()}</li>
 * </ul>
 * </p>
 * <p>
 * There are flags to set, for changing default behavior:
 * <ul>
 * <li>{link {@link #isAlwaysRefresh()}, {@link #setAlwaysRefresh(boolean)}: If <tt>true</tt> the
 * editor will reload the model after the model file changed. This means, e.g., that the model file
 * is reloaded when a single space is inserted. If <tt>false</tt> the editor is reloaded when the
 * model file changed and was saved.</li>
 * </ul>
 * </p>
 * <b>Note</b>: when overriding (non-abstract) methods (e.g. <code>foo()</code> ), you should always
 * call <code>super.foo()</code> first in your method.<br>
 * 
 * @author Tim Enger
 * @author Philipp Kehrbusch
 */
public abstract class GenericGraphicsViewer extends ScrollingGraphicalViewer {
    
  private IWorkbenchPartSite site;
  
  private IFile file;
  
  private GenericGraphicsSaveable saveable;
  
  private ActionRegistry actionRegistry;
  
  private IGraphicsLoader gLoader;
  
  private ILayoutAlgorithm layout;
  
  private GraphicsTextSelectionListener selectionListener;
  
  private ResourceTracker resourceListener = new ResourceTracker();
  
  TextEditorImpl txtEditor;
  
  /**
   * Initializes the viewer with an IWorkbenchPartSite and an input file. Must be called before
   * {@link #createControl(org.eclipse.swt.widgets.Composite)}.
   * 
   * @param site
   * @param file
   * @param txtEditor
   */
  public void init(IWorkbenchPartSite site, IFile file, TextEditorImpl txtEditor) {
    this.site = site;
    setInput(file);
    setEditPartFactory(createEditPartFactory());
    
    if (txtEditor != null) {
      setTextualEditor(txtEditor);
    }
  }
  
  /**
   * Like {@link #init(IWorkbenchPartSite, IFile, AbstractTextEditor)} without assigning a textual
   * editor. However, the viewer is not fully functional until
   * {@link #setTextualEditor(AbstractTextEditor)} is called.
   * 
   * @param site
   * @param file
   */
  public void init(IWorkbenchPartSite site, IFile file) {
    init(site, file, null);
  }
  
  public void setTextualEditor(TextEditorImpl txtEditor) {
    /* Editor can only be set once (graphical viewer is bound to one textual editor). This will also
     * avoid more than one ASTUpdateListener being created. If several listeners are registered for
     * one viewer, there will be errors as they are not removed properly. */
    if (this.txtEditor != null) {
      return;
    }
    
    selectionListener = new GraphicsTextSelectionListener(file, this);
    site.getWorkbenchWindow().getSelectionService().addPostSelectionListener(selectionListener);
    this.txtEditor = txtEditor;
    saveable = new GenericGraphicsSaveable();
    
  }
  
  public void configure() {
    // Equivalent to configureGraphicalViewer() from GraphicalEditor
    getControl().setBackground(ColorConstants.listBackground);
    
    // own configuration
    this.setRootEditPart(new ScalableFreeformRootEditPart());
    
    ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
    setRootEditPart(rootEditPart);
    
    gLoader = new DefaultGraphicsLoader(createPersistenceUtil(), file);
    layout = createLayoutAlgorithm();
    setupPrinting();
    setupZoom();
    
    // 
    ObservableModelStates observableModelStates = DIService.getInstance(ObservableModelStates.class);
    observableModelStates.getModelStates().stream()
        .filter(modelState -> modelState.getStorage().equals(getInputFile()))
        .forEach(this::acceptModelState);
    observableModelStates.addStorageObserver(getInputFile(), this::acceptModelState);
    
  }
  
  private void acceptModelState(ModelState modelState) {
    if (modelState.isLegal()) {
      Display.getDefault().asyncExec(() -> {
        setContents(modelState.getRootNode());
        refreshContents();
      });
    }
  }

  public void setInput(IFile file) {
    // The workspace never changes for an editor. So, removing and re-adding
    // the resourceListener is not necessary. But it is being done here for the
    // sake of proper implementation.
    // Plus, the resourceListener needs to be added to the workspace the first
    // time around.
    if (file != null) {
      this.file = file;
      file.getWorkspace().removeResourceChangeListener(resourceListener);
      file.getWorkspace().addResourceChangeListener(resourceListener);
    }
  }
  
  @SuppressWarnings("unchecked")
  public void refreshContents() {
    
    // Update mapping
    try {
      selectionListener.createMappings();
    }
    catch (SelectionSyncException e) {
      Log.error("Refreshing of content failed due to the following exception: " + e);
    } 
    
    beforeModelLoad();
    
    afterModelLoadBeforeViewLoad();
    
    // view data
    gLoader.loadViewData();
    
    ILayoutAlgorithm layoutAlgo = layout;
    
    if (!(getDisplayingPart() instanceof GenericGraphicsEditor)) {
      for (EditPart ep : new ArrayList<EditPart>(this.getEditPartRegistry().values())) {
        ep.removeEditPolicy(EditPolicy.DIRECT_EDIT_ROLE);
        ep.removeEditPolicy(EditPolicy.LAYOUT_ROLE);
        ep.removeEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE);
      }
    }
    
    boolean newLayout = gLoader.combineModelViewData(
        new ArrayList<EditPart>(this.getEditPartRegistry().values()), layoutAlgo);
        
    // save automatically generated layout
    if (newLayout) {
      this.doSave(new NullProgressMonitor());
    }
    
 /*   selectionListener = new GraphicsTextSelectionListener(file, this);
    // Only add a selection listener for viewer's displayed in a graphical editor. Only add
    // selection listener to other viewer if there is no graphical editor containing the viewer.
    // This ensures that only one viewer is used per input file.
    // (as the viewer can be displayed in outlines etc as well)
    if ((selectionListener != null && getDisplayingPart() instanceof GenericGraphicsEditor)) {
      site.getWorkbenchWindow().getSelectionService().addPostSelectionListener(selectionListener);
    }
*/      
    showProblemReports();
    
  }
  
  /**
   * <p>
   * Sets up a zooming functionality.
   * </p>
   * <p>
   * Provides the following functionality
   * <ul>
   * <li>'Numpad +' & 'Numpad -' for zooming in & out</li>
   * <li>'CTRL + Mousewheel' for zooming in & out</li>
   * </ul>
   * </p>
   */
  private void setupZoom() {
    ActionRegistry aRegistry = getActionRegistry();
    
    // zooming!
    ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
    setRootEditPart(rootEditPart);
    List<String> zoomContributions = Arrays.asList(
        new String[] { ZoomManager.FIT_ALL, ZoomManager.FIT_HEIGHT, ZoomManager.FIT_WIDTH });
    rootEditPart.getZoomManager().setZoomLevelContributions(zoomContributions);
    rootEditPart.getZoomManager()
        .setZoomLevels(new double[] { .25, .5, .75, 1.0, 1.5, 2.0, 2.5, 3, 4 });
        
    IAction zoomIn = new ZoomInAction(rootEditPart.getZoomManager());
    IAction zoomOut = new ZoomOutAction(rootEditPart.getZoomManager());
    
    aRegistry.registerAction(zoomIn);
    aRegistry.registerAction(zoomOut);
    
    KeyHandler keyHandler = new KeyHandler();
    keyHandler.setParent(getKeyHandler());
    
    keyHandler.put(KeyStroke.getPressed('+', SWT.KEYPAD_ADD, 0),
        aRegistry.getAction(GEFActionConstants.ZOOM_IN));
    keyHandler.put(KeyStroke.getPressed('-', SWT.KEYPAD_SUBTRACT, 0),
        aRegistry.getAction(GEFActionConstants.ZOOM_OUT));
        
    // Mousewheel Zooming
    // SWT.MOD1 => CTRL
    setProperty(MouseWheelHandler.KeyGenerator.getKey(SWT.MOD1), MouseWheelZoomHandler.SINGLETON);
    setKeyHandler(keyHandler);
  }
  
  /**
   * Sets up the possibility to print the visualization.
   */
  private void setupPrinting() {
    if (getDisplayingPart() != null) {
      IAction printAction = new PrintAction(getDisplayingPart());
      getActionRegistry().registerAction(printAction);
    }
  }
  
  private ActionRegistry getActionRegistry() {
    if (actionRegistry == null)
      actionRegistry = new ActionRegistry();
    return actionRegistry;
  }
      
  /**
   * <p>
   * Specifies the {@link EditPartFactory} which should be used.
   * </p>
   * <p>
   * This {@link EditPartFactory} should be an extension of the {@link MCEditPartFactory} to provide
   * full support for all features of the framework.
   * </p>
   * 
   * @return The {@link EditPartFactory} which should be used.
   */
  public abstract EditPartFactory createEditPartFactory();
  
  /**
   * <p>
   * Specifies the {@link IPersistenceUtil} to be used for loading and saving the view files
   * (graphical information).
   * </p>
   * 
   * @return The {@link IPersistenceUtil} to be used for loading and saving the view files
   * (graphical information).
   */
  public abstract IPersistenceUtil createPersistenceUtil();
  
  /**
   * <p>
   * Specifies the {@link ILayoutAlgorithm} to be used for initially layouting the diagram.
   * </p>
   * 
   * @return {@link ILayoutAlgorithm} to be used for initially layouting the diagram.
   */
  public abstract ILayoutAlgorithm createLayoutAlgorithm();
  
  /**
   * Put any functionality in this method, that needs to be executed before the model data is
   * loaded. <br>
   * See class description for more details.
   */
  public void beforeModelLoad() {
    // customization possible
  }
  
  /**
   * Put any functionality in this method, that needs to be executed after the model data was
   * loaded, but before the view data is loaded. <br>
   * See class description for more details.
   */
  public void afterModelLoadBeforeViewLoad() {
    // customization possible
  }
  
  /**
   * Put any functionality in this method, that needs to be executed after the model and the view
   * data were loaded.<br>
   * See class description for more details.
   */
  public void afterViewLoad() {
    // customization possible
  }
  
  public void doSave(IProgressMonitor monitor) {
    try {
      saveable.doSave(monitor);
    }
    catch (CoreException e) {
      e.printStackTrace();
    }
  }
    
  public void dispose() {
    // dispose resourcetracker
    if (resourceListener != null) {
      file.getWorkspace().removeResourceChangeListener(resourceListener);
    }
    // dispose selection listener
    if (selectionListener != null) {
      site.getWorkbenchWindow().getSelectionService()
          .removePostSelectionListener(selectionListener);
    }
  }
  
  /*******************************************/
  /************** FILE CHANGED ***************/
  /*******************************************/
  
  /**
   * <p>
   * This class is responsible for tracking resource (file) changes.
   * </p>
   * The following functionality is provided:
   * <ul>
   * <li>File contents changed and was saved => refresh content of this editor</li>
   * <li>File was renamed => reload the file.<br>
   * Note: the view file is not moved automatically.</li>
   * <li>File was deleted => close editor.</li>
   * </ul>
   * <b>Note</b>: Set the <code>alwaysRefresh</code> flag: if <tt>true</tt> the editor will reload
   * the model after the model file changed. This means, e.g., that the model file is reloaded when
   * a single space is inserted. If <tt>false</tt> the editor is reloaded when the model file
   * changed and was saved. <br>
   * <br>
   * 
   * @author Tim Enger
   */
  class ResourceTracker implements IResourceChangeListener, IResourceDeltaVisitor {
    
    @Override
    public void resourceChanged(IResourceChangeEvent event) {
      IResourceDelta delta = event.getDelta();
      try {
        if (delta != null) {
          delta.accept(this);
        }
      }
      catch (CoreException e) {
        // What should be done here?
        e.printStackTrace();
      }
    }
    
    @Override
    public boolean visit(IResourceDelta delta) {
      if (delta == null || !delta.getResource().equals(file)) {
        return true;
      }
      if (delta.getKind() == IResourceDelta.REMOVED) {
        Display display = site.getShell().getDisplay();
        if ((IResourceDelta.MOVED_TO & delta.getFlags()) == 0) {
          // CASE: the file was deleted => close editor
          // NOTE:
          // The case where an open, unsaved file is deleted is
          // not handled here.
          // If it should be handled use a PartListener added
          // to the Workbench in the initialize() method.
          display.asyncExec(new Runnable() {
            
            @Override
            public void run() {
              if (getDisplayingPart() instanceof GenericGraphicsEditor
                  && !((GenericGraphicsEditor) getDisplayingPart()).isDirty()) {
                site.getPage().closeEditor(((GenericGraphicsEditor) getDisplayingPart()), false);
              }
            }
          });
        }
        else { // CASE: the file was moved or renamed => reload and change file
          final IFile newFile = ResourcesPlugin.getWorkspace().getRoot()
              .getFile(delta.getMovedToPath());
          display.asyncExec(new Runnable() {
            
            @Override
            public void run() {
              IFile oldViewFile = gLoader.getViewFile();
              
              // superSetInput(new FileEditorInput(newFile));
              if (getDisplayingPart() instanceof GenericGraphicsEditor) {
                ((GenericGraphicsEditor) getDisplayingPart()).setInput(newFile); // will call
                                                                                 // #GenericGraphicsViewer.setInput(IFile)
              }
              else
                setInput(newFile);
                
              // update files of graphics loader
              gLoader.setModelFile(newFile);
              gLoader = new DefaultGraphicsLoader(createPersistenceUtil(), file);
              
              // this code means, that the view file, will always be in the same
              // folder as the model file with the same name but different
              // extension whenever the model file was moved in eclipse.
              try {
                if (oldViewFile != null) {
                  // copy old view file to new location
                  String ext = oldViewFile.getFileExtension();
                  IPath newPath = (IPath) newFile.getFullPath().clone();
                  newPath = newPath.removeFileExtension().addFileExtension(ext);
                  
                  oldViewFile.copy(newPath, true, new NullProgressMonitor());
                  // remove old file
                  oldViewFile.delete(true, new NullProgressMonitor());
                }
              }
              catch (CoreException e) {
                e.printStackTrace();
              }
              gLoader.setViewFileAccordingToModelFile();
              
              refreshContents();
            }
          });
        }
      }
      return false;
    }
  }
  
  /**
   * <p>
   * Show problem reports in:
   * <ul>
   * <li>Eclipse Problem View</li>
   * <li>Graphic Representation</li>
   * </ul>
   * </p>
   * <p>
   * This method is called during the initialization/refresh of the editor and uses the methods of
   * the {@link ASTNodeProblemReportHandler} util.
   * </p>
   * <p>
   * <b>Note</b>: This method is intended to be overwritten by subclasses if they want to change the
   * problem report handling.
   * </p>
   * 
   * @param ec The {@link ErrorCollector} used as input for the {@link ProblemReport ProblemReports}
   */
  @SuppressWarnings("unchecked")
  public void showProblemReports() {
    ASTNodeProblemReportHandler.showProblemReportsInGraphics(this.getEditPartRegistry().values());
        
    // TODO check for exception always occurring at the start of the editor
    // when this is active
    // ASTNodeProblemReportHandler.showProblemReportsInProblemsView(file,
    // ec);
  }
  
  /*******************************************/
  /************ GETTER & SETTERS *************/
  /*******************************************/
  
  /**
   * @return The viewer's input as {@link IFile}.
   */
  public IFile getInputFile() {
    return file;
  }
  
  /**
   * @return The absolute path of the editor file.
   */
  public String getAbsoluteFilePath() {
    return file.getRawLocation().toOSString();
  }
  
  /**
   * @return The project folder.
   */
  public String getProjectFolder() {
    return file.getProject().getLocation().toString();
  }
  
  /**
   * @return The {@link IGraphicsLoader}
   */
  public IGraphicsLoader getGraphicsLoader() {
    return gLoader;
  }
  
  /**
   * @param gLoader The {@link IGraphicsLoader} to set
   */
  public void setGraphicsLoader(IGraphicsLoader gLoader) {
    this.gLoader = gLoader;
  }
  
  /**
   * Every GEF editor has a {@link RootEditPart}. This {@link RootEditPart} has a single child,
   * called <i>contents</i> editpart representing the model data of the editor.
   * 
   * @return The contents {@link IMCEditPart}.
   */
  public IMCEditPart getContentEditPart() {
    return (IMCEditPart) this.getRootEditPart().getContents();
  }  
  
  public GraphicsTextSelectionListener getSelectionListener() {
    return selectionListener;
  }
  
  public GraphicalOutlinePage getGraphicalOutline() {
    return null;
  }
  
  /**
   * Applies a layout to the diagram that was generated by the layout algorithm returned in
   * {@link #createLayoutAlgorithm()}.
   */
  public void applyGeneratedLayout() {
    if (layout != null) {
      List<EditPart> eps = new ArrayList<EditPart>(this.getEditPartRegistry().values());
      List<IMCViewElementEditPart> veEps = new ArrayList<IMCViewElementEditPart>();
      
      for (EditPart ep : eps) {
        if (ep instanceof IMCViewElementEditPart)
          veEps.add((IMCViewElementEditPart) ep);
      }
      
      layout.layout(veEps);
      
      doSave(new NullProgressMonitor());
      refreshContents();
    }
  }
  
  /**
   * Returns the {@code IWorkbenchPart} this Viewer is attached to.
   */
  public IWorkbenchPart getDisplayingPart() {
    return site.getPart();
  }
  
  /**
   * Returns the {@link IEditorPart} that is associated with this viewer. If the viewer is displayed
   * in an editor, the {@link IEditorPart} is returned. If the viewer is displayed in the Outline
   * View, the editor that belongs to the viewer's ContentOutlinePage is returned.
   * 
   * @return The {@link IEditorPart} that belongs to this viewer. Returns null if there is no such
   * editor.
   */
  public IEditorPart getEditor() {
    if (getDisplayingPart() instanceof ContentOutline) {
      return txtEditor;
    }
    else if (getDisplayingPart() instanceof IEditorPart)
      return (IEditorPart) getDisplayingPart();
    else
      return null;
  }
  
  public void setDirty() {
    if (saveable != null)
      saveable.setDirty();
  }
  
  class GenericGraphicsSaveable extends Saveable {
    
    private boolean isDirty = false;
    
    public GenericGraphicsSaveable() {
      if (txtEditor != null
          && !(GenericGraphicsViewer.this.getDisplayingPart() instanceof ContentOutline))
        // TODO MB
        // txtEditor.registerExternalSaveable(this);
        System.out.println();
    }
    
    @Override
    public void doSave(IProgressMonitor monitor) throws CoreException {
      // use persistenceHandler
      Collection epsSet = getEditPartRegistry().values();
      List<EditPart> eps = new ArrayList<EditPart>(epsSet);
      gLoader.saveViewData(eps, monitor);
      
      if (getDisplayingPart() instanceof GenericGraphicsEditor) {
        ((GenericGraphicsEditor) getDisplayingPart()).doSaveEditorOnly(monitor);
      }
      
      isDirty = false;
      refreshContents();
    }
    
    public void doSaveAs() {
      // update viewer's input file
      if (txtEditor.getEditorInput() instanceof FileEditorInput) {
        IFile newFile = ((FileEditorInput) txtEditor.getEditorInput()).getFile();
        GenericGraphicsViewer.this.setInput(newFile);
        
        IFile oldViewFile = gLoader.getViewFile();
        
        try {
          if (oldViewFile != null) {
            // copy old view file to new location
            String ext = oldViewFile.getFileExtension();
            IPath newPath = (IPath) newFile.getFullPath().clone();
            newPath = newPath.removeFileExtension().addFileExtension(ext);
            
            oldViewFile.copy(newPath, true, new NullProgressMonitor());
            // remove old file
            oldViewFile.delete(true, new NullProgressMonitor());
          }
        }
        catch (CoreException e) {
          e.printStackTrace();
        }
        gLoader.setViewFileAccordingToModelFile();
        
        try {
          doSave(new NullProgressMonitor());
        }
        catch (CoreException e) {
          e.printStackTrace();
        }
      }
    }
    
    @Override
    public boolean equals(Object obj) {
      return (obj instanceof GenericGraphicsSaveable && obj.hashCode() == this.hashCode());
    }
    
    @Override
    public ImageDescriptor getImageDescriptor() {
      return null;
    }
    
    @Override
    public String getName() {
      return GenericGraphicsViewer.this.getInputFile().getName() + " (Graphical View)";
    }
    
    @Override
    public String getToolTipText() {
      return null;
    }
    
    @Override
    public int hashCode() {
      return GenericGraphicsViewer.this.hashCode();
    }
    
    @Override
    public boolean isDirty() {
      return isDirty;
    }
    
    public void setDirty() {
      isDirty = true;
    }
  }
}
