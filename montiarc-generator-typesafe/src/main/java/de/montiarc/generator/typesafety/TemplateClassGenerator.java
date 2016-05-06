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
import de.monticore.lang.templatesignature.templatesignature._ast.ASTFTL;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTParameter;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTResultDecl;
import de.monticore.lang.templatesignature.templatesignature._ast.ASTSignature;
import de.monticore.lang.templatesignature.templatesignature._parser.TemplateSignatureParser;
import de.se_rwth.commons.Names;

/**
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class TemplateClassGenerator {
  
  private static final String[] CHARS_TO_REPLACE = {",", " ", "\"","(", ")"}; 
  
  public static void generateClassForTemplate(String targetName, Path modelPath,
      File fqnTemplateName, File targetFilepath) {
    Optional<ASTSignature> sig = getSignature(fqnTemplateName);
    List<ASTParameter> params = new ArrayList<>();
    Optional<ASTResultDecl> result = Optional.empty();
    if (sig.isPresent()) {
      ASTSignature signature = sig.get();
      params = cleanParams(signature.getParamDecl().getParameters());
      
      result = cleanResult(signature.getResultDecl());
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
  
  /**
   * TODO: Write me!
   * @param resultDecl
   * @return
   */
  private static Optional<ASTResultDecl> cleanResult(Optional<ASTResultDecl> resultDecl) {
    if(!resultDecl.isPresent()){
      return resultDecl;
    }
    ASTResultDecl decl = resultDecl.get();
    decl.setResult(clean(resultDecl.get().getResult()));
    return Optional.of(decl);
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
        List<ASTSignature> signatures = ftl.getSignatures();
        for(ASTSignature s : signatures){
          if(!s.getParamDecl().getParameters().isEmpty()){
            return Optional.of(s);
          }
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }
  
  private static List<ASTParameter> cleanParams(List<ASTParameter> dirtyParams) {
    List<ASTParameter> cleanOnes = new ArrayList<>();
    for(ASTParameter p : dirtyParams){
      cleanOnes.add(cleanParam(p));
    }
    return cleanOnes;
  }
  

  /**
   * TODO: Write me!
   * @param p
   * @return
   */
  private static ASTParameter cleanParam(ASTParameter p) {
    ASTParameter cleanOne = p;
    String type = clean(p.getType());
    String name = clean(p.getName());
    cleanOne.setType(type);
    cleanOne.setName(name);
    return cleanOne;
  }

  /**
   * TODO: Write me!
   * @param type
   * @return
   */
  private static String clean(String s) {
    String clean = s;
    for(String charToReplace : CHARS_TO_REPLACE){
      if(clean.contains(charToReplace)){
        clean = clean.replace(charToReplace, "");
      }
    }
    return clean;
  }
}
