/* (c) https://github.com/MontiCore/monticore */
package de.monticore.codegen.cd2java;

import de.monticore.codegen.mc2cd.MC2CDStereotypes;
import de.monticore.generating.templateengine.HookPoint;
import de.monticore.generating.templateengine.StringHookPoint;
import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;

import java.util.Arrays;
import java.util.List;

public final class CoreTemplates {

  private CoreTemplates() {
  }

  public static HookPoint createPackageHookPoint(final String... packageName) {
    return createPackageHookPoint(Arrays.asList(packageName));
  }

  public static HookPoint createPackageHookPoint(final List<String> packageName) {
    return new StringHookPoint("package " + String.join(".", packageName) + ";");
  }

  public static HookPoint createAnnotationsHookPoint(final ASTModifier modifier) {
    String anno = "";
    if (modifier.isPresentStereotype()) {
      ASTStereotype stereo = modifier.getStereotype();
      for (ASTStereoValue stereoValue : stereo.getValuesList()) {
        if (MC2CDStereotypes.DEPRECATED.toString().equals(stereoValue.getName())) {
          if (!stereoValue.getValue().isEmpty()) {
            // Append tag for java api
            anno = "/**\n * @deprecated " + stereoValue.getValue() + "\n **/\n";
          }
          anno += "@Deprecated";
        }
      }
    }
    return new StringHookPoint(anno);
  }
}
