/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.selection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.SelectionManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;

/**
 * <p>
 * A selection manager that only selects parent figures and not its children.
 * </p>
 * Example:<br>
 * User selects a class of a class diagram which has several child figures.
 * Independent of which child figure the user clicked on only the parent, the
 * outermost figure, is selected only.
 * 
 * @author Tim Enger
 */
public class OnlyParentSelectionManager extends SelectionManager {
  
  private final GraphicalViewer viewer;
  
  /**
   * Constructor
   * 
   * @param viewer
   */
  public OnlyParentSelectionManager(GraphicalViewer viewer) {
    this.viewer = viewer;
  }
  
  @Override
  public void setSelection(ISelection selection) {
    // build a collection of originally selection parts
    List<?> oldSelection = ((IStructuredSelection) selection).toList();
    // and a collection from which nested parts are removed
    List<Object> newSelection = new ArrayList<Object>(oldSelection);
    
    // cycle through all selected parts and remove nested parts
    // which have a parent or grandparent part that is selected
    Iterator<Object> iter = newSelection.iterator();
    while (iter.hasNext()) {
      if (containsAncestor(newSelection, (EditPart) iter.next())) {
        iter.remove();
      }
    }
    
    // pass the new selection to the superclass implementation
    // to perform the actual selection
    super.setSelection(new StructuredSelection(newSelection));
  }
  
  // don't select inner children
  // e.g. don't select every attribute, when the class is already selected
  @Override
  public void appendSelection(EditPart editpart) {
    List<?> selection = ((IStructuredSelection) getSelection()).toList();
    
    // if "nothing" is selected then getSelection() returns
    // the viewer's primary edit part in which case the
    // specified part should be selected
    if (selection.size() == 1 && selection.get(0) == viewer.getContents()) {
      super.appendSelection(editpart);
      return;
    }
    
    // if the selection already contains an ancestor
    // of the specified part then don't selection the part
    if (containsAncestor(selection, editpart)) {
      return;
    }
    
    // deselect any currently selection parts
    // which have the new part as an ancestor
    Iterator<?> iter = new ArrayList<Object>(selection).iterator();
    while (iter.hasNext()) {
      EditPart each = (EditPart) iter.next();
      if (isAncestor(editpart, each)) {
        deselect(each);
      }
    }
    
    // call the supercalls implementation to select the part
    super.appendSelection(editpart);
  }
  
  private boolean isAncestor(EditPart ancestor, EditPart part) {
    while (part != null) {
      part = part.getParent();
      if (part == ancestor) {
        return true;
      }
    }
    return false;
  }
  
  private boolean containsAncestor(List<?> list, EditPart part) {
    while (part != null) {
      part = part.getParent();
      if (list.contains(part)) {
        return true;
      }
    }
    return false;
  }
  
}
