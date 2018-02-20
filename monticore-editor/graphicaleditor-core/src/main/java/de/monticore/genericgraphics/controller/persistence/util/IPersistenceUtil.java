/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.persistence.util;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;

import de.monticore.genericgraphics.model.graphics.IViewElement;


/**
 * Interface for all export utilities. They need to be able to
 * <ul>
 * <li>export a {@link java.util.List List} of {@link IViewElement
 * IViewElements} to a {@link IFile}</li>
 * <li>import a {@link java.util.List List} of {@link IViewElement
 * IViewElements} from a {@link IFile}</li>
 * </ul>
 * 
 * @author Tim Enger
 */
public interface IPersistenceUtil {
  
  /**
   * Export a {@link java.util.List List} of {@link IViewElement IViewElements}
   * to a {@link IFile}.
   * 
   * @param ves The {@link java.util.List List} of {@link IViewElement
   *          IViewElements} to export.
   * @param file The {@link IFile} to export to.
   * @param progressMonitor The {@link IProgressMonitor} to use for showing
   *          progress
   * @return <tt>True</tt> if the export was successful, otherwise
   *         <tt>false</tt>.
   */
  public boolean exportViewElements(List<IViewElement> ves, IFile file, IProgressMonitor progressMonitor);
  
  /**
   * Import a {@link java.util.List List} of {@link IViewElement IViewElements}
   * from a {@link IFile}.
   * 
   * @param file The {@link IFile} to import from.
   * @return The imported {@link java.util.List List} of {@link IViewElement
   *         IViewElements}.
   */
  public List<IViewElement> importViewElements(IFile file);
  
}
