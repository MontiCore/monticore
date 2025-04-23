/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.stereotypes;

import de.monticore.interpreter.Value;
import de.monticore.symboltable.IScope;
import de.monticore.symboltable.serialization.JsonPrinter;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.se_rwth.commons.logging.Log;

import java.util.Map;
import java.util.Optional;

public class StereoinfoDeSer {

  public static final String STEREO_TYPE = "stereotype";
  public static final String STEREO_VALUE = "stereovalue";

  protected static final String LOG_NAME = "StereoinfoDeSer";

  protected static StereoinfoDeSer instance;

  protected static StereoinfoDeSer getInstance() {
    if (instance == null) {
      instance = new StereoinfoDeSer();
    }
    return instance;
  }

  public static String printAsJson(
    Map.Entry<? extends ISymbolicStereotype, Optional<Value>> stereoinfo) {
    return printAsJson(stereoinfo.getKey(), stereoinfo.getValue());
  }

  public static String printAsJson(ISymbolicStereotype stereotype, Optional<Value> value) {

    return getInstance().doPrintAsJson(stereotype, value);
  }

  protected String doPrintAsJson(ISymbolicStereotype stereotype, Optional<Value> value) {
    if (value.isPresent()) {
      Log.errorInternal(
        "0x82401 Internal error: The serialization of values for symbolic stereotypes is not yet " +
          "supported."
      );
    }

    JsonPrinter p = new JsonPrinter();

    p.beginObject();
    p.member(STEREO_TYPE, stereotype.getFullName());
    p.endObject();

    return p.getContent();
  }

  public static Map.Entry<ISymbolicStereotype, Optional<Value>> deserialize(JsonElement json,
                                                                            IScope enclosingScope) {
    return getInstance().doDeserialize(json, enclosingScope);
  }

  @SuppressWarnings("unused")
  protected Map.Entry<ISymbolicStereotype, Optional<Value>> doDeserialize(JsonElement json,
                                                                          IScope enclosingScope) {
    Log.errorInternal(
      "0x82402 Internal error: The deserialization of symbolic stereotype annotations is not " +
        "supported by default. Provide an adequate deserializer that can handle this."
    );
    return null;
  }

}
