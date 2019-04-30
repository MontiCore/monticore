<#-- (c) https://github.com/MontiCore/monticore -->
${tc.params("String _package", "String outputDirectory")}


package ${_package};

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import de.monticore.generating.GeneratorEngine;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import de.monticore.generating.templateengine.ExtendedTemplateController;
import java.util.Optional;
import java.util.stream.Collectors;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.ExtendedGeneratorSetup;
import de.monticore.templateclassgenerator.codegen.TemplateClassGeneratorConstants;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.io.paths.IterablePath;

public class GeneratorConfig {
  
  private static ExtendedGeneratorSetup setup;
  
  
  public static ExtendedTemplateController getTemplateController(String templateName) {
    if (null == setup) {
      init(Optional.empty());
    }
    return (ExtendedTemplateController) setup.getNewTemplateController(templateName);
  }
    
  private static void init(Optional<GeneratorSetup> setupOpt) {
    setup = new ExtendedGeneratorSetup();
    if (setupOpt.isPresent()) {
      setup = new ExtendedGeneratorSetup(setupOpt.get());
    }
    else {
      setup.setOutputDirectory(Paths.get(TemplateClassGeneratorConstants.DEFAULT_OUTPUT_FOLDER)
          .toAbsolutePath().toFile());
    }
    
    GlobalExtensionManagement glex = setup.getGlex();
    glex.defineGlobalVar(TemplateClassGeneratorConstants.TEMPLATES_ALIAS, new TemplateAccessor());
    setup.setGlex(glex);
    Map<String, String> imports = new HashMap<>();
    imports.put(TemplateClassGeneratorConstants.TEMPLATE_CLASSES_PACKAGE,
        TemplateClassGeneratorConstants.TEMPLATE_CLASSES_PACKAGE + "/"
            + TemplateClassGeneratorConstants.TEMPLATE_CLASSES_SETUP_PACKAGE + "/Setup.ftl");
    setup.setAutoImports(imports);
   
    File f = new File("${outputDirectory}");
    if(f.exists()){   
      IterablePath ip = IterablePath.from(f, "ftl");
      setup.setAdditionalTemplatePaths(ip.getPaths().stream().map(Path::toFile).collect(Collectors.toList()));
    }
  }
  
  public static void init(GeneratorSetup setup) {
    init(Optional.of(setup));
  }
  
    
}
