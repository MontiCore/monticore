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
package de.monticore.genericgraphics.controller.persistence.util;

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
