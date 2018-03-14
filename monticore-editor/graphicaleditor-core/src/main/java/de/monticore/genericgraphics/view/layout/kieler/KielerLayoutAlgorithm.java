/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.view.layout.kieler;

import java.util.List;

import org.eclipse.gef.EditPart;
import org.eclipse.ui.IWorkbenchPart;

import de.cau.cs.kieler.kiml.ui.diagram.DiagramLayoutEngine;
import de.monticore.genericgraphics.GenericGraphicsEditor;
import de.monticore.genericgraphics.controller.editparts.IMCViewElementEditPart;
import de.monticore.genericgraphics.view.layout.ILayoutAlgorithm;

/**
 * <p>
 * An implementation of {@link ILayoutAlgorithm} that allows to use Kieler
 * layout algorithms easily.
 * </p>
 * <p>
 * <b>Note:</b> This layout algorithm ignores the given list of
 * {@link IMCViewElementEditPart IMCViewElementEditParts} in
 * {@link #layout(List)} and layouts the given {@link IWorkbenchPart} and the
 * content editpart. The content {@link EditPart} can be accessed through
 * {@link GenericGraphicsEditor#getContentEditPart()}.
 * </p>
 * 
 * @author Tim Enger
 */
public class KielerLayoutAlgorithm implements ILayoutAlgorithm {
  
  private IWorkbenchPart workbenchPart;
  private Object diagramPart;
  
  /**
   * @param workbenchPart The {@link IWorkbenchPart} of the editor
   * @param diagramPart The content {@link EditPart} which can be accessed
   *          through {@link GenericGraphicsEditor#getContentEditPart()}.
   */
  public KielerLayoutAlgorithm(final IWorkbenchPart workbenchPart, final Object diagramPart) {
    this.workbenchPart = workbenchPart;
    this.diagramPart = diagramPart;
  }
  
  /**
   * This layout algorithm ignores the given list of
   * {@link IMCViewElementEditPart IMCViewElementEditParts} and layouts the
   * given {@link IWorkbenchPart} and the content editpart.
   */
  @Override
  public void layout(List<IMCViewElementEditPart> ves) {
    DiagramLayoutEngine.INSTANCE.layout(workbenchPart, diagramPart, false, false, true, false, null);
  }
}
