/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.generating;

import com.google.common.collect.Maps;
import de.monticore.generating.templateengine.FreeMarkerTemplateEngineMock;
import de.monticore.generating.templateengine.GlobalExtensionManagement;
import de.monticore.generating.templateengine.ITemplateControllerFactory;
import de.monticore.generating.templateengine.TemplateControllerConfiguration;
import de.monticore.generating.templateengine.TemplateControllerConfigurationBuilder;
import de.monticore.generating.templateengine.TemplateControllerMockFactory;
import de.monticore.io.FileReaderWriter;
import de.monticore.io.FileReaderWriterMock;
import de.monticore.ast.ASTNode;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Mock for {@link GeneratorEngine}. Can always be used instead
 * of {@link GeneratorEngine}.
 *
 * @author  (last commit) $Author$
 * @version $Revision$,
 *          $Date$
 *
 */
public class GeneratorEngineMock extends GeneratorEngine {
  
  private FreeMarkerTemplateEngineMock freeMarkerTemplateEngine;
  private FileReaderWriterMock fileHandler;
  private Map<ASTNode, List<String>> handledNodesAndTemplates = Maps.newHashMap();


  public GeneratorEngineMock(GeneratorSetup generatorSetup, ITemplateControllerFactory
      templateControllerFactory) {
    super(generatorSetup, templateControllerFactory, new FileReaderWriterMock());
  }
  

  /**
   * @see de.monticore.generating.GeneratorEngine#createTemplateControllerConfiguration
   * (GeneratorSetup, de.monticore.generating.templateengine.ITemplateControllerFactory, de.monticore.io.FileReaderWriter)
   */
  @Override
  TemplateControllerConfiguration createTemplateControllerConfiguration(GeneratorSetup generatorSetup, ITemplateControllerFactory templateControllerFactory, FileReaderWriter fileHandler) {
    if (templateControllerFactory == null) {
      templateControllerFactory = new TemplateControllerMockFactory();
    }
    
    GlobalExtensionManagement glex = new GlobalExtensionManagement();

    freeMarkerTemplateEngine = new FreeMarkerTemplateEngineMock();
    
    TemplateControllerConfiguration config = new TemplateControllerConfigurationBuilder()
                                                .glex(glex)
                                                .templateControllerFactory(templateControllerFactory)
                                                .freeMarkerTemplateEngine(freeMarkerTemplateEngine)
                                                .fileHandler(fileHandler)
                                                .classLoader(getClass().getClassLoader())
                                                .externalTemplatePaths(new File[]{})
                                                .targetDir(generatorSetup.getOutputDirectory())
                                                .tracing(false)
                                                .build();
    
    this.fileHandler = (FileReaderWriterMock) fileHandler;
    
    return config;
  }
  
  public FreeMarkerTemplateEngineMock getFreeMarkerTemplateEngine() {
    return this.freeMarkerTemplateEngine;
  }
  
  public FileReaderWriterMock getFileHandler() {
    return this.fileHandler;
  }
  

  public Map<ASTNode, List<String>> getHandledNodesAndTemplates() {
    return this.handledNodesAndTemplates;
  }
  
}
