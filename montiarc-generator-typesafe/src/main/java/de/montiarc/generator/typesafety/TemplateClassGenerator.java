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
import de.monticore.lang.templatesignature.templatecontent._ast.ASTFTL;
import de.monticore.lang.templatesignature.templatecontent._parser.TemplateContentParser;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTComment;
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
    try {
      fr = new FileReader(fqnTemplateName);
      System.out.println(fqnTemplateName.getAbsolutePath());
      BufferedReader br = new BufferedReader(fr);
      TemplateContentParser contentParser = new TemplateContentParser();
      TemplateSignatureParser signatureParser = new TemplateSignatureParser();
      Optional<ASTFTL> oFTL = contentParser.parse(br);
      if (oFTL.isPresent()) {
        ASTFTL ftl = oFTL.get();
        List<String> ftlComments = ftl.getComments();
        for(String ftlComment : ftlComments){
          Optional<ASTComment> oComment = signatureParser.parse_String(ftlComment);
          if(oComment.isPresent()){
            ASTComment comment = oComment.get();
            ASTSignature signature = comment.getSignature();
            if(!signature.getParameters().isEmpty()){
              return Optional.of(signature);
            }
          }
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }
  
  
}
