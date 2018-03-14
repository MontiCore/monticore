/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomComboContributionItem;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;

import de.monticore.genericgraphics.controller.actions.ExportAsImageAction;


/**
 * Generic {@link ActionBarContributor} providing the following functionality:
 * <ul>
 * <li>Toolbar:
 * <ul>
 * <li>Adds a {@link ZoomComboContributionItem}</li>
 * <li>Adds a undo/redo</li>
 * </ul>
 * </li>
 * <li>Global Action Keys
 * <ul>
 * <li>adds key for printing</li>
 * <li>adds key for "select all"</li>
 * </ul>
 * </li>
 * </ul>
 * 
 * @author Tim Enger
 */
public class GenericActionBarContributor extends ActionBarContributor {
  
  private ExportAsImageAction exportAction;
  
  @Override
  protected void buildActions() {
    addRetargetAction(new UndoRetargetAction());
    addRetargetAction(new RedoRetargetAction());
    
    addRetargetAction(new ZoomInRetargetAction());
    addRetargetAction(new ZoomOutRetargetAction());
    
    exportAction = new ExportAsImageAction(null);
    addAction(exportAction);
  }
  
  @Override
  public void contributeToToolBar(IToolBarManager toolBarManager) {
    // redo & undo
    toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
    toolBarManager.add(getAction(ActionFactory.REDO.getId()));
    
    toolBarManager.add(new Separator());
    
    // zoom
    toolBarManager.add(getAction(GEFActionConstants.ZOOM_IN));
    toolBarManager.add(getAction(GEFActionConstants.ZOOM_OUT));
    toolBarManager.add(new ZoomComboContributionItem(getPage()));
    
    toolBarManager.add(new Separator());
    
    // export as image
    toolBarManager.add(getAction(ExportAsImageAction.EXPORT_AS_IMAGE_ID));
  }
  
  @Override
  protected void declareGlobalActionKeys() {
    addGlobalActionKey(ActionFactory.PRINT.getId());
    addGlobalActionKey(ActionFactory.SELECT_ALL.getId());
  }
  
  @Override
  public void setActiveEditor(IEditorPart editor) {
    // override this so that the exportAction instance can track the
    // current editor
    super.setActiveEditor(editor);
    exportAction.setEditorPart(editor);
  }
}
