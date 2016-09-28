<#--
***************************************************************************************
Copyright (c) 2016, MontiCore
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
***************************************************************************************
-->
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
    GeneratorSetup setup = setupOpt.orElse(new GeneratorSetup(new File(TemplateClassGeneratorConstants.DEFAULT_OUTPUT_FOLDER)));
    
    GlobalExtensionManagement glex = setup.getGlex().orElse(new GlobalExtensionManagement());
    glex.defineGlobalVar(TemplateClassGeneratorConstants.TEMPLATES_ALIAS, new TemplateAccessor());
    setup.setGlex(glex);
    List<TemplateAutoImport> imports = new ArrayList<>();
    TemplateAutoImport ta = new TemplateAutoImport(Paths.get("Setup.ftl"), TemplateClassGeneratorConstants.TEMPLATE_CLASSES_PACKAGE);
    imports.add(ta);
    setup.setAutoImports(imports);
    List<File> files = new ArrayList<>();
    
    File f = Paths.get(getRelativeTargetPath()+"/"+TemplateClassGeneratorConstants.TEMPLATE_CLASSES_PACKAGE+"/"+TemplateClassGeneratorConstants.TEMPLATE_CLASSES_SETUP_PACKAGE+"/").toFile();
    files.add(f);
    setup.setAdditionalTemplatePaths(files);
    GeneratorConfig.generator = new ExtendedGeneratorEngine(setup);
    
    return setup;
  }
  
  public static void init(GeneratorSetup setup) {
    GeneratorConfig.generator = new ExtendedGeneratorEngine(init(Optional.of(setup)));
  }
  
  private static String getRelativeTargetPath() {
    String workingDir = GeneratorConfig.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    if (workingDir.contains(File.separator)){
      workingDir = workingDir.replace(File.separator, "/");
    }
    workingDir = workingDir.substring(0,workingDir.lastIndexOf("/target"));
    workingDir = workingDir.substring(workingDir.lastIndexOf("/"));    
    String relPath = "${outputDirectory}";
    if(relPath.contains(workingDir)){
      relPath = relPath.substring(relPath.lastIndexOf(workingDir));
    }
    return relPath;    
  }
    
}
