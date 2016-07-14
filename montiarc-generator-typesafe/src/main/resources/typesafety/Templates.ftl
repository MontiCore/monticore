${tc.params("String package", "java.util.List<File> nodes", "String modelPath", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper")}

package ${package};

public class Templates {

  ${tc.includeArgs("typesafety.TemplatesInnerClasses", [nodes,0, modelPath, helper])}

}
