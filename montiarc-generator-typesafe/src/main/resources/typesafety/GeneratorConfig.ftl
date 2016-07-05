${tc.params("String _package")}


package ${_package};

import java.io.File;
import java.util.Optional;
import de.monticore.generating.MyGeneratorEngine;
import de.monticore.generating.GeneratorSetup;



public class GeneratorConfig {
  
  private static MyGeneratorEngine generator;
  
  private static final String DEFAULT_OUTPUT_FOLDER = "/target/generated-sources/templateClasses";
  
  public static MyGeneratorEngine getGeneratorEngine() {
    if (null == generator) {
      String workingDir = System.getProperty("user.dir");
      GeneratorSetup setup = new GeneratorSetup(new File(workingDir+DEFAULT_OUTPUT_FOLDER));
      GeneratorConfig.generator = new MyGeneratorEngine(setup);
      GeneratorConfig.generator.setTemplates(Optional.of(new Templates()));
    }
    return generator;
  }
  
  public static void setGeneratorEngine(MyGeneratorEngine generator) {
    GeneratorConfig.generator = generator;
    GeneratorConfig.generator.setTemplates(Optional.of(new Templates()));
  }
  
}