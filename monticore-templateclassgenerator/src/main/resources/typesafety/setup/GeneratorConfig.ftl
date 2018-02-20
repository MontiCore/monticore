<#-- (c) https://github.com/MontiCore/monticore -->
${tc.params("String _package", "String outputDirectory")}


package ${_package};

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.ExtendedGeneratorEngine;
import de.monticore.templateclassgenerator.codegen.TemplateClassGeneratorConstants;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.freemarker.TemplateAutoImport;
import de.monticore.io.paths.IterablePath;

public class GeneratorConfig {
  
  private static ExtendedGeneratorEngine generator;
  
  
  public static ExtendedGeneratorEngine getGeneratorEngine() {
    if (null == generator) {
      init();
    }
    return generator;
  }
  
  static private GeneratorSetup init() {
    return init(Optional.empty());
  }
  
  private static GeneratorSetup init(Optional<GeneratorSetup> setupOpt) {
    GeneratorSetup setup = setupOpt.orElse(
    	new GeneratorSetup().setup.setOutputDirectory(
	   Paths.get(TemplateClassGeneratorConstants.DEFAULT_OUTPUT_FOLDER).toAbsolutePath().toFile()));
    
    GlobalExtensionManagement glex = setup.getGlex();
    glex.defineGlobalVar(TemplateClassGeneratorConstants.TEMPLATES_ALIAS, new TemplateAccessor());
    setup.setGlex(glex);
    List<TemplateAutoImport> imports = new ArrayList<>();
    TemplateAutoImport ta = new TemplateAutoImport(TemplateClassGeneratorConstants.TEMPLATE_CLASSES_PACKAGE+"/"+TemplateClassGeneratorConstants.TEMPLATE_CLASSES_SETUP_PACKAGE+"/Setup.ftl", TemplateClassGeneratorConstants.TEMPLATE_CLASSES_PACKAGE);
    imports.add(ta);
    setup.setAutoImports(imports);
   
    File f = new File("${outputDirectory}");
    if(f.exists()){   
      IterablePath ip = IterablePath.from(f, "ftl");
      setup.setAdditionalTemplatePaths(ip.getPaths().stream().map(Path::toFile).collect(Collectors.toList()));
    }
    
    
    GeneratorConfig.generator = new ExtendedGeneratorEngine(setup);
    
    return setup;
  }
  
  public static void init(GeneratorSetup setup) {
    GeneratorConfig.generator = new ExtendedGeneratorEngine(init(Optional.of(setup)));
  }
  
    
}
