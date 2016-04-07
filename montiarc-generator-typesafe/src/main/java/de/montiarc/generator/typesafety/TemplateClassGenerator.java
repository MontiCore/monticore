/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.typesafety;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import com.google.common.io.Files;

import de.montiarc.generator.codegen.TemplateClassHelper;
import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTParameter;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTResult;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTSignature;
import de.monticore.lang.templatesignature.templatesignature._parser.TemplateSignatureParser;
import de.se_rwth.commons.Names;

/**
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class TemplateClassGenerator {
  
  private static final String signatureStart = "<#-- sig:";
  
  private static final String signatureEnd = "-->";
  
  public static void generateClassForTemplate(String targetName, Path modelPath,
      File fqnTemplateName, File targetFilepath) {
    Optional<ASTSignature> sig = getSignature(fqnTemplateName);
    if (sig.isPresent()) {
      ASTSignature signature = sig.get();
      List<ASTParameter> params = signature.getParameters();
      Optional<ASTResult> result = signature.getResult();
      final GeneratorSetup setup = new GeneratorSetup(targetFilepath);
      TemplateClassHelper helper = new TemplateClassHelper();
      final GeneratorEngine generator = new GeneratorEngine(setup);
      String qualifiedTargetTemplateName = fqnTemplateName.getPath();
      ASTNode node = new EmptyNode();
      qualifiedTargetTemplateName = qualifiedTargetTemplateName.substring(modelPath.toString()
          .length());
      String packageNameWithSeperators = Names.getPathFromFilename(qualifiedTargetTemplateName);
      String packageNameWithDots = Names.getPackageFromPath(packageNameWithSeperators.substring(1));
      
      generator.generate("templates.typesafety.TemplateClass",
          Paths.get(packageNameWithSeperators, targetName + ".java"),node ,
          packageNameWithDots, qualifiedTargetTemplateName, targetName,
          params, result, helper);
    }
    
  }
  
  private static Optional<ASTSignature> getSignature(File fqnTemplateName) {
    FileReader fr;
    Optional<ASTSignature> signature = Optional.empty();
    try {
      fr = new FileReader(fqnTemplateName);
      System.out.println(fqnTemplateName.getAbsolutePath());
      BufferedReader br = new BufferedReader(fr);
      String line = "";
      line = br.readLine();
      String signatureString = "";
      while (line != null) {
        // start of signature
        if (line.contains(signatureStart)) {
          int lastIndex = line.indexOf(signatureStart)+signatureStart.length();
          signatureString += line.substring(lastIndex);
        }
        // end of signature
        else if (line.contains(signatureEnd)) {
          int lastIndex = line.indexOf(signatureEnd);
          signatureString += line.substring(0,lastIndex);
          break;
        }
        else {
          signatureString += "\n" + line;
        }
        line = br.readLine();
      }
      br.close();
      
      
      TemplateSignatureParser parser = new TemplateSignatureParser();
      signature = parser.parseString_Signature(signatureString);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return signature;
  }
  
  
}
