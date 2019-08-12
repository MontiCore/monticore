/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;

import java.util.Arrays;
import java.util.List;

public final class CoreTemplates {

  public static final String PACKAGE = "core.Package";

  public static final String CLASS = "core.Class";

  public static final String INTERFACE = "core.Interface";

  public static final String ATTRIBUTE = "core.Attribute";

  public static final String VALUE = "core.Value";

  public static final String CONSTRUCTOR = "core.Constructor";

  public static final String METHOD = "core.Method";

  public static final String EMPTY_BODY = "core.EmptyBody";

  public static final String CONSTANT = "core.Constants";

  public static final String ENUM = "core.Enum";

  private CoreTemplates() {}

  public static HookPoint createPackageHookPoint(final String... packageName) {
    return createPackageHookPoint(Arrays.asList(packageName));
  }

  public static HookPoint createPackageHookPoint(final List<String> packageName) {
    return new StringHookPoint("package " + String.join(".", packageName) + ";");
  }
}
