// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.streams.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.logging.Log;

/**
 * relations for built-in Stream SymTypes
 * these are Stream, EventStream, SyncStream, ToptStream.
 * This does NOT include types deriving from these.
 */
public class StreamTypeRelations {

  /**
   * whether the type is a Stream
   */
  public boolean isStream(SymTypeExpression type) {
    return isSpecificStream(type, "Stream") ||
        isEventStream(type) ||
        isSyncStream(type) ||
        isToptStream(type) ||
        isUntimedStream(type);
  }

  public boolean isEventStream(SymTypeExpression type) {
    return isSpecificStream(type, "EventStream");
  }

  public boolean isSyncStream(SymTypeExpression type) {
    return isSpecificStream(type, "SyncStream");
  }

  public boolean isToptStream(SymTypeExpression type) {
    return isSpecificStream(type, "ToptStream");
  }

  public boolean isUntimedStream(SymTypeExpression type) {
    return isSpecificStream(type, "UntimedStream");
  }

  /**
   * @return the Element type of a Stream.
   */
  public SymTypeExpression getStreamElementType(SymTypeExpression type) {
    if (!isStream(type)) {
      Log.error("0xFD1C9 internal error: tried to get the type "
          + "of an stream's element of a non stream type");
      return SymTypeExpressionFactory.createObscureType();
    }
    return type.asGenericType().getArgument(0);
  }

  // Helper

  protected boolean isSpecificStream(SymTypeExpression type, String streamName) {
    if (!type.isGenericType()) {
      return false;
    }
    SymTypeOfGenerics generic = type.asGenericType();
    String name = generic.getTypeConstructorFullName();
    if (!name.equals(streamName)) {
      return false;
    }
    if (generic.sizeArguments() != 1) {
      Log.warn("0xFD1C3 encountered generic called "
          + name + " with "
          + generic.sizeArguments() + " type arguments, "
          + "but expected 1");
      return false;
    }
    return true;
  }

}
