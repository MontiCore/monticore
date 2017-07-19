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
package de.monticore.genericgraphics.controller.editparts;

import java.util.List;

import org.eclipse.gef.EditPart;

import de.monticore.genericgraphics.controller.persistence.IIdentifiable;


/**
 * This interface provides additional methods for {@link EditPart EditParts}
 * that are needed in order to provide the following functionality:
 * <ul>
 * <li>Identification functionality (by implementing {@link IIdentifiable})</li>
 * <ul>
 * <li>{@link IMCEditPart#getIdentifier()}</li>
 * </ul>
 * </li> <li>Error Handling (by implementing {@link IProblemReportHandler})</li>
 * <ul>
 * <li>{@link IMCEditPart#setProblems(List)}</li>
 * <li>{@link IMCEditPart#deleteAllProblems()}</li>
 * </ul>
 * </li> </ul>
 * 
 * @author Tim Enger
 */
public interface IMCEditPart extends EditPart, IProblemReportHandler, IIdentifiable {
  
}
