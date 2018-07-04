package de.monticore.symboltable.serializing;

import com.google.gson.JsonDeserializer;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;

/**
 * This interface realizes deserialize Strings to {@link Symbol} instances.
 * Implementations of this interface are generated and registered to an {@link IArtifactScopeSerializer}.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public interface IScopeDeserializer<S extends Scope> extends JsonDeserializer<S>{
  
}
