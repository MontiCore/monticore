package de.monticore.genericgraphics.controller.editparts;

import java.util.List;

import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import de.monticore.editorconnector.EditorConnector;
import de.monticore.genericgraphics.GenericGraphicsEditor;
import de.monticore.genericgraphics.GenericGraphicsViewer;
import de.monticore.genericgraphics.controller.util.ProblemReportUtil;
import de.se_rwth.commons.logging.Finding;


/**
 * <p>
 * This is an implementation of {@link IMCGraphicalEditPart} which extends
 * {@link AbstractGraphicalEditPart} and thus provides its functionality.
 * </p>
 * <p>
 * Furthermore the methods of the {@link IProblemReportHandler} interface are
 * implemented, by using the {@link ProblemReportUtil}.
 * </p>
 * 
 * @see ProblemReportUtil
 * @author Tim Enger
 */
public abstract class AbstractMCGraphicalEditPart extends AbstractGraphicalEditPart implements IMCGraphicalEditPart {
  
  /**
   * Constructor
   */
  public AbstractMCGraphicalEditPart() {
  }
  
  @Override
  protected void createEditPolicies() {
  }
  
  @Override
  public void setProblems(List<Finding> reports) {
    ProblemReportUtil.setProblems(reports, this);
  }
  
  @Override
  public void deleteAllProblems() {
    ProblemReportUtil.deleteAllProblems(this);
  }
  
  @Override
  public String getIdentifier() {
    return getModel().toString();
  }
  
  @Override
  public void performRequest(Request req) {
    IWorkbench workbench = PlatformUI.getWorkbench();
    IWorkbenchWindow window = (workbench != null) ? workbench.getActiveWorkbenchWindow() : null;
    IWorkbenchPage page = (window != null) ? window.getActivePage() : null;
    IWorkbenchPart activePart = (page != null) ? page.getActivePart() : null;
    IEditorPart activeE = (page != null) ? page.getActiveEditor() : null;
    
    if(activeE != null) {
      GenericGraphicsViewer viewer = EditorConnector.getInstance().getViewerForEditor(activeE);
      
      if(activePart instanceof GenericGraphicsEditor && req.getType() == RequestConstants.REQ_OPEN)
        viewer.getSelectionListener().editPartDoubleClick();
      if(req.getType() == RequestConstants.REQ_DIRECT_EDIT)
        viewer.getSelectionListener().editPartSelected(activePart, viewer.getSelection());
    }
      
    super.performRequest(req);
  }
}
