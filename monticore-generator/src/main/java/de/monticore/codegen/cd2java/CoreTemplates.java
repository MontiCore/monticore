/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import de.monticore.cd.cd4analysis._ast.ASTCDStereoValue;
import de.monticore.cd.cd4analysis._ast.ASTCDStereotype;
import de.monticore.cd.cd4analysis._ast.ASTModifier;
import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

  public static final String ANNOTATIONS = "core.Annotations";

  private CoreTemplates() {}

  public static HookPoint createPackageHookPoint(final String... packageName) {
    return createPackageHookPoint(Arrays.asList(packageName));
  }

  public static HookPoint createPackageHookPoint(final List<String> packageName) {
    return new StringHookPoint("package " + String.join(".", packageName) + ";");
  }

  public static HookPoint createAnnotationsHookPoint(final ASTModifier modifier) {
    String anno = "";
    if (modifier.isPresentStereotype()) {
      ASTCDStereotype stereo = modifier.getStereotype();
      for (ASTCDStereoValue stereoValue : stereo.getValueList()) {
        if (MC2CDStereotypes.DEPRECATED.toString().equals(stereoValue.getName())) {
          if (stereoValue.isPresentValue()) {
            // Append tag for java api
            anno = "/**\n * @deprecated " + stereoValue.getValue() + "\n **/\n";
          }
          anno += MC2CDStereotypes.DEPRECATED.toString();
        }
      }
    }
    return new StringHookPoint(anno);
  }
}
