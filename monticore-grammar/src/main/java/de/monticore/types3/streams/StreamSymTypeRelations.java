// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.streams;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.logging.Log;

/**
 * relations for built-in Stream SymTypes
 * these are Stream, EventStream, SyncStream, ToptStream, and UntimedStream.
 * This does NOT include types deriving from these.
 */
public class StreamSymTypeRelations {

  public static final String STREAM = "Stream";
  public static final String EVENT_STREAM = "EventStream";
  public static final String SYNC_STREAM = "SyncStream";
  public static final String TOPT_STREAM = "ToptStream";
  public static final String UNTIMED_STREAM = "UntimedStream";

  protected static StreamSymTypeRelations delegate;

  // methods

  public static boolean isStream(SymTypeExpression type) {
    return getDelegate()._isStream(type);
  }

  protected boolean _isStream(SymTypeExpression type) {
    return isStreamOfUnknownSubType(type) ||
        isEventStream(type) ||
        isSyncStream(type) ||
        isToptStream(type) ||
        isUntimedStream(type);
  }

  public static boolean isEventStream(SymTypeExpression type) {
    return getDelegate()._isEventStream(type);
  }

  protected boolean _isEventStream(SymTypeExpression type) {
    return isSpecificStream(type, StreamSymTypeRelations.EVENT_STREAM);
  }

  public static boolean isSyncStream(SymTypeExpression type) {
    return getDelegate()._isSyncStream(type);
  }

  protected boolean _isSyncStream(SymTypeExpression type) {
    return isSpecificStream(type, StreamSymTypeRelations.SYNC_STREAM);
  }

  public static boolean isToptStream(SymTypeExpression type) {
    return getDelegate()._isToptStream(type);
  }

  protected boolean _isToptStream(SymTypeExpression type) {
    return isSpecificStream(type, StreamSymTypeRelations.TOPT_STREAM);
  }

  public static boolean isUntimedStream(SymTypeExpression type) {
    return getDelegate()._isUntimedStream(type);
  }

  protected boolean _isUntimedStream(SymTypeExpression type) {
    return isSpecificStream(type, StreamSymTypeRelations.UNTIMED_STREAM);
  }

  public static boolean isStreamOfUnknownSubType(SymTypeExpression type) {
    return getDelegate()._isStreamOfUnknownSubType(type);
  }

  protected boolean _isStreamOfUnknownSubType(SymTypeExpression type) {
    return isSpecificStream(type, StreamSymTypeRelations.STREAM);
  }

  /**
   * @return the Element type of a Stream.
   */
  public static SymTypeExpression getStreamElementType(SymTypeExpression type) {
    return getDelegate()._getStreamElementType(type);
  }

  protected SymTypeExpression _getStreamElementType(SymTypeExpression type) {
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

  // static delegate

  public static void init() {
    Log.trace("init default StreamSymTypeRelations", "TypeCheck setup");
    setDelegate(new StreamSymTypeRelations());
  }

  public static void reset() {
    StreamSymTypeRelations.delegate = null;
  }

  protected static void setDelegate(StreamSymTypeRelations newDelegate) {
    StreamSymTypeRelations.delegate = Log.errorIfNull(newDelegate);
  }

  protected static StreamSymTypeRelations getDelegate() {
    if (StreamSymTypeRelations.delegate == null) {
      init();
    }
    return StreamSymTypeRelations.delegate;
  }

}
