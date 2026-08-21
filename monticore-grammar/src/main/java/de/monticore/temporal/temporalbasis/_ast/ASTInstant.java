/* (c) https://github.com/MontiCore/monticore */
package de.monticore.temporal.temporalbasis._ast;

import java.time.temporal.Temporal;
import java.time.temporal.TemporalAccessor;

@SuppressWarnings("unused") // This interface is part of the public-facing API
public interface ASTInstant extends ASTInstantTOP, TemporalAccessor {

    Temporal toTemporal();

}
