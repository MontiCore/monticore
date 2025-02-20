// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.streams;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.streams.util.StreamTypeRelations;
import de.se_rwth.commons.logging.Log;

/**
 * relations for built-in Collection SymTypes of MCCollectionTypes
 * these are List, Set, Optional, Map
 * Per default, this does NOT include types that inherit from collection types
 */
public class StreamSymTypeRelations extends SymTypeRelations {

  protected static StreamTypeRelations streamTypeRelations;

  public static final String STREAM = "Stream";
  public static final String EVENT_STREAM = "EventStream";
  public static final String SYNC_STREAM = "SyncStream";
  public static final String TOPT_STREAM = "ToptStream";
  public static final String UNTIMED_STREAM = "UntimedStream";

  public static void init() {
    Log.trace("init StreamTypeRelations", "TypeCheck setup");
    SymTypeRelations.init();
    streamTypeRelations = new StreamTypeRelations();
  }

  public static boolean isStream(SymTypeExpression type) {
    return getStreamTypeRelations().isStream(type);
  }

  public static boolean isEventStream(SymTypeExpression type) {
    return getStreamTypeRelations().isEventStream(type);
  }

  public static boolean isSyncStream(SymTypeExpression type) {
    return getStreamTypeRelations().isSyncStream(type);
  }

  public static boolean isToptStream(SymTypeExpression type) {
    return getStreamTypeRelations().isToptStream(type);
  }

  public static boolean isUntimedStream(SymTypeExpression type) {
    return getStreamTypeRelations().isUntimedStream(type);
  }

  public static boolean isStreamOfUnknownSubType(SymTypeExpression type) {
    return getStreamTypeRelations().isStreamOfUnknownSubType(type);
  }

  /**
   * @return the Element type of a Stream.
   */
  public static SymTypeExpression getStreamElementType(SymTypeExpression type) {
    return getStreamTypeRelations().getStreamElementType(type);
  }

  // Helper

  protected static StreamTypeRelations getStreamTypeRelations() {
    if (streamTypeRelations == null) {
      Log.error("0xFD9CB internal error: "
          + "StreamSymTypeRelations was not init()-ialized."
      );
    }
    return streamTypeRelations;
  }
}
