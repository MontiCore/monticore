/*
 * Copyright (c) 2016 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.montiarc.generator.typesafety;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import de.montiarc.generator.codegen.TemplateClassHelper;
import de.monticore.ast.ASTNode;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.se_rwth.commons.Names;
import freemarker.cache.FileTemplateLoader;
import freemarker.core.Argument;
import freemarker.core.FMHelper;
import freemarker.template.Configuration;
import freemarker.template.Template;

/**
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class TemplateClassGenerator {
  
  private static final String PARAM_METHOD = "tc.params";
  private static final String RESULT_METHOD = "tc.result";
  
  
  
  
  public static void generateClassForTemplate(String targetName, Path modelPath,
      String fqnTemplateName, File targetFilepath) {
//    Optional<ASTSignature> sig = getSignature(fqnTemplateName);
//    List<ASTParameter> params = new ArrayList<>();
//    Optional<ASTResultDecl> result = Optional.empty();
//    if (sig.isPresent()) {
//      ASTSignature signature = sig.get();
//      params = cleanParams(signature.getParamDecl().getParameters());
//      
//      result = cleanResult(signature.getResultDecl());
//    }
    List<Argument> params = new ArrayList<>();
    Optional<String> result = Optional.empty();
    Configuration config = new Configuration();
    Template t = null;
    try {
      config.setTemplateLoader(new FileTemplateLoader(modelPath.toFile()));
      t = config.getTemplate(fqnTemplateName); 
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    Map<String, List<List<String>>> methodCalls = FMHelper.getMethodCalls(t);
    if(methodCalls.containsKey(PARAM_METHOD)){
      // we just recognize the first entry as there 
      // must not be multiple params definitions
      params = FMHelper.getParams(methodCalls.get(PARAM_METHOD).get(0));
    }
    if(methodCalls.containsKey(RESULT_METHOD)){
      // A template can only have one result type.
      String dirtyResult = methodCalls.get(RESULT_METHOD).get(0).get(0);
      String cleanResult = dirtyResult.replace("\"", "");
      result = Optional.of(cleanResult);
    }
    
    final GeneratorSetup setup = new GeneratorSetup(targetFilepath);
    TemplateClassHelper helper = new TemplateClassHelper();
    final GeneratorEngine generator = new GeneratorEngine(setup);
    String qualifiedTargetTemplateName = fqnTemplateName;
    ASTNode node = new EmptyNode();
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
  
//  /**
//   * TODO: Write me!
//   * @param resultDecl
//   * @return
//   */
//  private static Optional<ASTResultDecl> cleanResult(Optional<ASTResultDecl> resultDecl) {
//    if(!resultDecl.isPresent()){
//      return resultDecl;
//    }
//    ASTResultDecl decl = resultDecl.get();
//    decl.setResult(clean(resultDecl.get().getResult()));
//    return Optional.of(decl);
//  }
//
//  private static Optional<ASTSignature> getSignature(File fqnTemplateName) {
//    FileReader fr;
//    try {
//      fr = new FileReader(fqnTemplateName);
//      System.out.println(fqnTemplateName.getAbsolutePath());
//      BufferedReader br = new BufferedReader(fr);
//      TemplateSignatureParser contentParser = new TemplateSignatureParser();
//      Optional<ASTFTL> oFTL = Optional.empty();
//      try {
//        oFTL = contentParser.parse(br);
//        contentParser.setError(false);
//      }
//      catch (RecognitionException re) {
//      }
//      
//      if (oFTL.isPresent()) {
//        ASTFTL ftl = oFTL.get();
//        List<ASTSignature> signatures = ftl.getSignatures();
//        for(ASTSignature s : signatures){
//          if(!s.getParamDecl().getParameters().isEmpty()){
//            return Optional.of(s);
//          }
//        }
//      }
//    }
//    catch (IOException e) {
//      e.printStackTrace();
//    }
//    return Optional.empty();
//  }
//  
//  private static List<ASTParameter> cleanParams(List<ASTParameter> dirtyParams) {
//    List<ASTParameter> cleanOnes = new ArrayList<>();
//    for(ASTParameter p : dirtyParams){
//      cleanOnes.add(cleanParam(p));
//    }
//    return cleanOnes;
//  }
//  
//
//  /**
//   * TODO: Write me!
//   * @param p
//   * @return
//   */
//  private static ASTParameter cleanParam(ASTParameter p) {
//    ASTParameter cleanOne = p;
//    String type = clean(p.getType());
//    String name = clean(p.getName());
//    cleanOne.setType(type);
//    cleanOne.setName(name);
//    return cleanOne;
//  }
//
//  /**
//   * TODO: Write me!
//   * @param type
//   * @return
//   */
//  private static String clean(String s) {
//    String clean = s;
//    for(String charToReplace : CHARS_TO_REPLACE){
//      if(clean.contains(charToReplace)){
//        clean = clean.replace(charToReplace, "");
//      }
//    }
//    return clean;
//  }
}
