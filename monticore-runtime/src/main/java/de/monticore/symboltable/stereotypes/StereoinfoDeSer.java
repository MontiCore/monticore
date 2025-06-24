/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.stereotypes;

import de.monticore.interpreter.MIValue;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.se_rwth.commons.logging.Log;

import java.util.Map;
import java.util.Optional;

/**
 * Facade to serialize and deserialize stereoinfos of symbols. <p>
 * I.e., the de-/serialization is based on / results in a json object with the
 * info about a stereotype that a symbol has. If the symbol has. If there is a
 * corresponding stereovalue for the stereotype, it is also de-/serialized.
 * <p>
 * The facade consists of
 * {@link StereoinfoDeSer#printAsJson(IStereotypeReference, Optional)},
 * {@link StereoinfoDeSer#printAsJson(Map.Entry)}, and
 * {@link StereoinfoDeSer#deserialize(JsonElement, IScope)}.
 * Custom de-/serialization behavior can be achieved by initializing the
 * singleton within this class with a sub class that overwrites
 * {@link StereoinfoDeSer#doPrintAsJson(IStereotypeReference, Optional)} and
 * {@link StereoinfoDeSer#doDeserialize(JsonElement, IScope)}.
 * <p>
 * Note that deserialization is not supported out of the box. Initialize this
 * facade with a {@code StereofinoDeSer} with such support. Languages that
 * provide stereotype symbols (implementations of {@link IStereotypeSymbol})
 * should also provide such a DeSerializer.
 */
public class StereoinfoDeSer {

  public static final String STEREO_TYPE = "stereotype";
  public static final String STEREO_VALUE = "stereovalue";

  protected static final String LOG_NAME = "StereoinfoDeSer";

  /**
   * Singleton instance with implementations of
   * {@link StereoinfoDeSer#doPrintAsJson(IStereotypeReference, Optional)} and
   * {@link StereoinfoDeSer#doDeserialize(JsonElement, IScope)}
   * to which the  facade calls
   * {@link StereoinfoDeSer#printAsJson(IStereotypeReference, Optional)},
   * {@link StereoinfoDeSer#printAsJson(Map.Entry)}, and
   * {@link StereoinfoDeSer#deserialize(JsonElement, IScope)}
   * are delegated to. <p>
   * Set this field from a sub class to configure the behavior.
   */
  protected static StereoinfoDeSer instance;

  protected static StereoinfoDeSer getInstance() {
    if (instance == null) {
      instance = new StereoinfoDeSer();
    }
    return instance;
  }

  /**
   * Facade to convert the information that a symbol has stereotype into a json
   * object that also holds the associated stereovalue if present.
   * <p>
   * See {@link StereoinfoDeSer} on how to configure how this facade behaves.
   */
  public static String printAsJson(
    Map.Entry<? extends IStereotypeReference, Optional<MIValue>> stereoinfo) {
    return printAsJson(stereoinfo.getKey(), stereoinfo.getValue());
  }

  /**
   * Facade to convert the information that a symbol has stereotype into a json
   * object that also holds the associated stereovalue if present.
   * <p>
   * See {@link StereoinfoDeSer} on how to configure how this facade behaves.
   */
  public static String printAsJson(IStereotypeReference stereotype, Optional<MIValue> value) {

    return getInstance().doPrintAsJson(stereotype, value);
  }

  protected String doPrintAsJson(IStereotypeReference stereotype, Optional<MIValue> value) {
    if (value.isPresent()) {
      Log.errorInternal(
        "0x82401 Internal error: The serialization of values for symbolic stereotypes is not yet " +
          "supported."
      );
    }

    if (stereotype.getResolved().isEmpty()) {
      Log.errorInternal(
        "0x82405 Internal error: The serialization of stereotype annotations was called with an " +
          "invalid stereotype symbol reference."
      );
      return "";
    }

    JsonPrinter p = new JsonPrinter();

    p.beginObject();
    p.member(STEREO_TYPE, stereotype.getResolved().get().getFullName());
    p.endObject();

    return p.getContent();
  }

  /**
   * Facade to deserialize the information that a symbol has stereotype from a json
   * object that also holds the associated stereovalue if present.
   * <p>
   * See {@link StereoinfoDeSer} on how to configure how this facade behaves.
   */
  public static Map.Entry<IStereotypeReference, Optional<MIValue>> deserialize(
    JsonElement json, IScope enclosingScope) {

    return getInstance().doDeserialize(json, enclosingScope);
  }

  @SuppressWarnings("unused")
  protected Map.Entry<IStereotypeReference, Optional<MIValue>> doDeserialize(JsonElement json,
                                                                           IScope enclosingScope) {
    Log.errorInternal(
      "0x82402 Internal error: The deserialization of stereotype annotations is not supported by " +
        "default. Provide an adequate deserializer that can handle this."
    );
    return null;
  }

}
