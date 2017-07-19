/*******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
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
package de.monticore.genericgraphics.controller.persistence;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.EditPart;

import de.monticore.genericgraphics.controller.editparts.IMCEditPart;
import de.monticore.genericgraphics.controller.editparts.IMCViewElementEditPart;
import de.monticore.genericgraphics.controller.persistence.util.IPersistenceUtil;
import de.monticore.genericgraphics.model.graphics.IViewElement;
import de.monticore.genericgraphics.view.layout.ILayoutAlgorithm;


/**
 * <p>
 * Interface for handling persistence providing methods for
 * <ul>
 * <li>loading model data</li>
 * <li>saving view Data</li>
 * <li>loading view Data</li>
 * <li>combination View and Model data</li>
 * </ul>
 * </p>
 * <p>
 * This class makes use of the following classes/tools:
 * <ul>
 * <li>{@link IPersistenceUtil}: to import and export {@link IViewElement
 * IViewElements}</li>
 * <li>{@link DSLTool}: to parse an domain model file</li>
 * </ul>
 * </P
 * 
 * @author Tim Enger
 */
public interface IGraphicsLoader {
  
  /**
   * Save the view information of the given list of {@link EditPart EditParts}.
   * Only the \code{IViewElement IViewElements} of
   * {@link IMCViewElementEditPart IMCViewElementEditParts} in the list will be
   * saved.
   * 
   * @param editparts The {@link EditPart EditParts} providing the view
   *          information.
   * @param monitor The {@link IProgressMonitor} to monitor the progress
   */
  public void saveViewData(List<EditPart> editparts, IProgressMonitor monitor);
  
  /**
   * Load the view data.
   * 
   * @return The loaded view data as list of {@link IViewElement IViewElements}
   */
  public List<IViewElement> loadViewData();
  
  /**
   * <p>
   * Assign the loaded {@link IViewElement IViewElements} to the
   * {@link IMCEditPart IMCEditParts}.
   * </p>
   * <p>
   * TODO: write how this is exactly done.
   * </p>
   * 
   * @param editparts The {@link EditPart EditParts} the loaded
   *          {@link IViewElement IViewElements} should be assigned to
   * @param layout The {@link ILayoutAlgorithm} to use for layouting.
   *        If null, the layout will not be generated automatically if
   *        none exists.
   * @return True a new layout was generated, false if an existing layout
   *         was used.
   */
  public boolean combineModelViewData(List<EditPart> editparts, ILayoutAlgorithm layout);
  
  /**
   * @return The loaded view data as list of {@link IViewElement IViewElements}
   */
  public List<IViewElement> getLoadedViewData();
  
  /**
   * @param file The <b>model</b> {@IFile} to operate on
   */
  public void setModelFile(IFile file);
  
  /**
   * @return The <b>model</b> {@link IFile} operating on
   */
  public IFile getModelFile();
  
  /**
   * @param file The <b>view</b> {@IFile} to operate on
   */
  public void setViewFile(IFile file);
  
  /**
   * Sets the view file according to the model file. That means the path is
   * copied and only the extension is changed.
   */
  public void setViewFileAccordingToModelFile();
  
  /**
   * @return The <b>view</b> {@link IFile} operating on
   */
  public IFile getViewFile();
  
  /**
   * @return The {@link IPersistenceUtil} this loader uses.
   */
  public IPersistenceUtil getPersistenceUtil();
  
  /**
   * Sets the {@link IPersistenceUtil} this loader uses.
   * 
   * @param util The {@link IPersistenceUtil} this loader will use.
   */
  public void setPersistenceUtil(IPersistenceUtil util);
}
