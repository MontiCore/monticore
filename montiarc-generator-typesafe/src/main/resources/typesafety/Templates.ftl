${tc.params("String package", "java.util.List<File> nodes", "String modelPath", "de.monticore.templateclassgenerator.codegen.TemplateClassHelper helper")}

package ${package};

public class Templates implements de.monticore.templateclassgenerator.ITemplates {

  ${tc.includeArgs("typesafety.TemplatesInnerClasses", [nodes,0, modelPath, helper])}

}
