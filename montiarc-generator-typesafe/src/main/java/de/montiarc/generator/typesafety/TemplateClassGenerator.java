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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.antlr.v4.runtime.RecognitionException;

import de.montiarc.generator.codegen.TemplateClassHelper;
import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTComment;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTFTL;
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
    List<ASTParameter> params = new ArrayList<>();
    Optional<ASTResult> result = Optional.empty();
    if (sig.isPresent()) {
      ASTSignature signature = sig.get();
      params = signature.getParameters();
      result = signature.getResult();
    }
    final GeneratorSetup setup = new GeneratorSetup(targetFilepath);
    TemplateClassHelper helper = new TemplateClassHelper();
    final GeneratorEngine generator = new GeneratorEngine(setup);
    String qualifiedTargetTemplateName = fqnTemplateName.getPath();
    ASTNode node = new EmptyNode();
    qualifiedTargetTemplateName = qualifiedTargetTemplateName.substring(modelPath.toString()
        .length());
    String packageNameWithSeperators = Names.getPathFromFilename(qualifiedTargetTemplateName);
    String packageNameWithDots = "";
    if (packageNameWithSeperators.length() > 1) {
      packageNameWithDots = Names.getPackageFromPath(packageNameWithSeperators
          .substring(1));
    }
    generator.generate("templates.typesafety.TemplateClass",
        Paths.get(packageNameWithSeperators, targetName + ".java"), node,
        packageNameWithDots, qualifiedTargetTemplateName, targetName,
        params, result, helper);
    
  }
  
  private static Optional<ASTSignature> getSignature(File fqnTemplateName) {
    FileReader fr;
    try {
      fr = new FileReader(fqnTemplateName);
      System.out.println(fqnTemplateName.getAbsolutePath());
      BufferedReader br = new BufferedReader(fr);
      TemplateSignatureParser contentParser = new TemplateSignatureParser();
      Optional<ASTFTL> oFTL = Optional.empty();
      try {
        oFTL = contentParser.parse(br);
        contentParser.setError(false);
      }
      catch (RecognitionException re) {
      }
      
      if (oFTL.isPresent()) {
        ASTFTL ftl = oFTL.get();
        List<ASTComment> ftlComments = ftl.getComments();
        for (ASTComment ftlComment : ftlComments) {
          ASTSignature possibleSig = ftlComment.getSignatures().get(0);
          if (!possibleSig.getParameters().isEmpty()) {
            return Optional.of(possibleSig);
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
