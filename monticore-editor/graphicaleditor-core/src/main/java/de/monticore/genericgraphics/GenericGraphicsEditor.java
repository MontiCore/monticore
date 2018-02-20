/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics;

import java.util.EventObject;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.ui.parts.GraphicalEditor;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.part.FileEditorInput;

import de.monticore.editorconnector.util.ExtensionRegistryUtils;
import de.monticore.genericgraphics.controller.editparts.IMCEditPart;
import de.monticore.genericgraphics.controller.persistence.ErrorCollector;
import de.monticore.genericgraphics.controller.persistence.IGraphicsLoader;
import de.monticore.genericgraphics.controller.util.ASTNodeProblemReportHandler;
import de.monticore.genericgraphics.view.layout.ILayoutAlgorithm;
import de.se_rwth.langeditor.texteditor.TextEditorImpl;

/**
 * <p>
 * The Generic Graphical Editor for MontiCore Class Diagrams.
 * </p>
 * <p>
 * The Generic Graphical Editor makes use of the following utils:
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
 * Different functionality is provided by the Generic Graphical Editor without changing anything,
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
 */
public class GenericGraphicsEditor extends GraphicalEditor {
  
  private GenericGraphicsViewer viewer;
    
  private IFile inputFile;
  
  private Composite parentControl;
  
  private TextEditorImpl editor;
  
  
  /**
   * Constructor
   */
  public GenericGraphicsEditor(TextEditorImpl editor) {
    this.editor = editor;
    setEditDomain(new DefaultEditDomain(this));
  }
  
  /**
   * Override to assign, configure and initialize the Viewer by ourselves. Mostly equivalent to
   * GraphicalEditor.createPartControl(Composite). <br>
   * <br>
   * The model to be displayed is retrieved from the corresponding textual editor. If this editor is
   * opened already, there is no problem. However, if it isn't, the viewer will only be created and
   * configured and a part listener is added to the workbench to recognize when the textual editor
   * is opened. When the textual editor gets opened, the graphical viewer's contents will be
   * initialized by calling {@code createPartControl(Composite)} again. <br>
   * <b>It is necessary</b> to set the viewer as the selection provider the first time
   * {@code createPartControl(Composite)} is called, otherwise selection changes in the graphical
   * viewer will not be recognized by selection listeners that are added to a workbench window's
   * {@link ISelectionService}.
   */
  @Override
  public void createPartControl(Composite parent) {
    // only create viewer on first call
    if (parentControl == null) {
      try {
        viewer = ExtensionRegistryUtils.getViewer(inputFile);
        if (viewer != null) {
          viewer.init(getSite(), inputFile, editor);
          viewer.createControl(parent);
          viewer.configure();
          setGraphicalViewer(viewer);
          // hookGraphicalViewer()
          getSelectionSynchronizer().addViewer(getGraphicalViewer());
          getSite().setSelectionProvider(getGraphicalViewer());
        }
      }
      catch (CoreException e1) {
        e1.printStackTrace();
      }
    }   
    parentControl = parent;    
  }
  
  
  @Override
  public void initializeGraphicalViewer() {
    viewer.refreshContents();
  }
  
  @Override
  protected void setInput(IEditorInput input) {
    super.setInput(input);
    
    if (input instanceof IFileEditorInput) {
      inputFile = ((IFileEditorInput) input).getFile();
    }
    
    if (input != null && input instanceof IFileEditorInput) {
      IFile file = ((IFileEditorInput) input).getFile();
      
      if (viewer != null)
        viewer.setInput(file);
        
      setPartName(file.getName() + " (Graphical View)");
    }
  }
  
  public void setInput(IFile input) {
    setInput(new FileEditorInput(input));
  }
    
  @Override
  public void doSave(IProgressMonitor monitor) {
    viewer.doSave(monitor);
    getCommandStack().markSaveLocation();
  }
      
  
  // Mark the editor dirty when command stack changes
  // (layout is changed)
  @Override
  public void commandStackChanged(EventObject event) {
    firePropertyChange(IEditorPart.PROP_DIRTY);
    super.commandStackChanged(event);
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
    ASTNodeProblemReportHandler
        .showProblemReportsInGraphics(getGraphicalViewer().getEditPartRegistry().values());
        
    // TODO check for exception always occurring at the start of the editor
    // when this is active
    // ASTNodeProblemReportHandler.showProblemReportsInProblemsView(getEditorInputFile(),
    // ec);
  }
  
  /*******************************************/
  /************ GETTER & SETTERS *************/
  /*******************************************/
  
  /**
   * @return The editor input as {@link IFile}.
   */
  public IFile getEditorInputFile() {
    if (getEditorInput() != null) {
      return ((IFileEditorInput) getEditorInput()).getFile();
    }
    return null;
  }
  
  
  /**
   * Every GEF editor has a {@link RootEditPart}. This {@link RootEditPart} has a single child,
   * called <i>contents</i> editpart representing the model data of the editor.
   * 
   * @return The contents {@link IMCEditPart}.
   */
  public IMCEditPart getContentEditPart() {
    return (IMCEditPart) getGraphicalViewer().getRootEditPart().getContents();
  }
 
  @Override
  public GenericGraphicsViewer getGraphicalViewer() {
    return viewer;    
  }
}
