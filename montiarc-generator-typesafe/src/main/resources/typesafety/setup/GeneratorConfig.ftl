${tc.params("String _package", "String outputDirectory")}


package ${_package};

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import de.monticore.generating.GeneratorSetup;
import de.monticore.generating.ExtendedGeneratorEngine;
import de.monticore.templateclassgenerator.codegen.TemplateClassGeneratorConstants;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.freemarker.TemplateAutoImport;

public class GeneratorConfig {
  
  private static ExtendedGeneratorEngine generator;
  
  private static final String DEFAULT_OUTPUT_FOLDER = "/target/generated-sources/gen";
  
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
    String workingDir = System.getProperty("user.dir");
    GeneratorSetup setup = setupOpt.orElse(new GeneratorSetup(new File(workingDir
        + DEFAULT_OUTPUT_FOLDER)));
    
    GlobalExtensionManagement glex = setup.getGlex().orElse(new GlobalExtensionManagement());
    glex.defineGlobalValue(TemplateClassGeneratorConstants.TEMPLATES_ALIAS, new TemplateStorage());
    setup.setGlex(glex);
    List<TemplateAutoImport> imports = new ArrayList<>();
    TemplateAutoImport ta = new TemplateAutoImport(Paths.get("Setup.ftl"), "${glex.getGlobalValue("TemplateClassPackage")}");
    imports.add(ta);
    setup.setAutoImports(imports);
    List<File> files = new ArrayList<>();
    
    String outDir = "${outputDirectory}";
    if(!outDir.endsWith(File.separator)){
    	outDir+=File.separator;
    }
    if(outDir.contains(workingDir)){
      outDir = workingDir + outDir;
    }
    outDir = outDir.replace("/",File.separator);
    outDir+="setup"+File.separator;
    File f = Paths.get(outDir).toFile();
    files.add(f);
    setup.setAdditionalTemplatePaths(files);
    GeneratorConfig.generator = new ExtendedGeneratorEngine(setup);
    
    return setup;
  }
  
  public static void init(GeneratorSetup setup) {
    GeneratorConfig.generator = new ExtendedGeneratorEngine(init(Optional.of(setup)));
  }
    
}
