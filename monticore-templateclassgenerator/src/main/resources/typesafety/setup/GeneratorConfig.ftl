<#--
****************************************************************************
MontiCore Language Workbench, www.monticore.de
Copyright (c) 2017, MontiCore, All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
****************************************************************************
-->
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
