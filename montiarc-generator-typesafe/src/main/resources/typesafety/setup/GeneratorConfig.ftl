${tc.params("String _package")}


package ${_package};

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.MyGeneratorEngine;
import de.monticore.templateclassgenerator.codegen.TemplateClassGeneratorConstants;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.freemarker.TempalateAutoImport;

public class GeneratorConfig {
  
  private static MyGeneratorEngine generator;
  
  private static final String DEFAULT_OUTPUT_FOLDER = "/target/generated-sources/templateClasses";
  
  public static MyGeneratorEngine getGeneratorEngine() {
    if (null == generator) {
      init();
    }
    return generator;
  }
  
  static private GeneratorSetup init() {
    return init(Optional.empty());
  }
  
  private static GeneratorSetup init(Optional<GeneratorSetup> setupOpt) {
    String workingDir = System.getProperty("user.dir");
    GeneratorSetup setup = setupOpt.orElse(new GeneratorSetup(new File(workingDir
        + DEFAULT_OUTPUT_FOLDER)));
    
    GlobalExtensionManagement glex = setup.getGlex().orElse(new GlobalExtensionManagement());
    glex.defineGlobalValue(TemplateClassGeneratorConstants.TEMPLATES_ALIAS, new Templates());
    setup.setGlex(glex);
    List<TempalateAutoImport> imports = new ArrayList<>();
    TempalateAutoImport ta = new TempalateAutoImport(Paths.get("Setup.ftl"), "${glex.getGlobalValue("TemplateClassPackage")}");
    imports.add(ta);
    setup.setAutoImports(imports);
    List<File> files = new ArrayList<>();
    File f = Paths.get(workingDir + DEFAULT_OUTPUT_FOLDER + ""
        + "/setup/").toFile();
    files.add(f);
    setup.setAdditionalTemplatePaths(files);
    GeneratorConfig.generator = new MyGeneratorEngine(setup);
    
    return setup;
  }
  
  public static void init(GeneratorSetup setup) {
    GeneratorConfig.generator = new MyGeneratorEngine(init(Optional.of(setup)));
  }
    
}
