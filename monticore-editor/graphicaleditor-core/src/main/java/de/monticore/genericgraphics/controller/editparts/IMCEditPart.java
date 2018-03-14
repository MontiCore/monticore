/* (c)  https://github.com/MontiCore/monticore */package de.monticore.genericgraphics.controller.editparts;

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
