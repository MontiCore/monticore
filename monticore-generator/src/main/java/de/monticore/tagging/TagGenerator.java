/* (c) https://github.com/MontiCore/monticore */
package de.monticore.tagging;

import com.google.common.base.Joiner;
import de.monticore.codegen.cd2java._tagging.TaggingConstants;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.monticore.grammar.grammar.GrammarMill;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._visitor.GrammarTraverser;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._prettyprint.Grammar_WithConceptsFullPrettyPrinter;
import de.monticore.io.paths.MCPath;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class TagGenerator {


  /**
   * Generate the TagSchema and TagDefinition grammars and output them as mc4 files
   */
  public static void generateTaggingLanguages(ASTMCGrammar grammar, File outDirectory, MCPath modelPathHC) throws IOException {
    GrammarTraverser traverser = GrammarMill.traverser();
    TagDefinitionDerivVisitor definitionDerivVisitor = new TagDefinitionDerivVisitor();
    traverser.add4Grammar(definitionDerivVisitor);
    grammar.accept(traverser);
    ASTMCGrammar defDerivGrammar = definitionDerivVisitor.getGrammar();

    traverser = GrammarMill.traverser();
    TagSchemaDerivVisitor tagSchemaDerivVisitor = new TagSchemaDerivVisitor();
    traverser.add4Grammar(tagSchemaDerivVisitor);
    grammar.accept(traverser);
    ASTMCGrammar schemaDerivGrammar = tagSchemaDerivVisitor.getGrammar();


    Grammar_WithConceptsFullPrettyPrinter fpp = new Grammar_WithConceptsFullPrettyPrinter(new IndentPrinter());

    // TOP-ish / LanguageOverrideVisitor
    Optional<ASTMCGrammar> tagDefHC = parseGrammarHC(grammar, modelPathHC, TaggingConstants.TAGDEFINITION_SUFFIX);
    if (tagDefHC.isPresent()) {
      traverser = GrammarMill.traverser();
      TagLanguageOverrideVisitor overrideVisitor = new TagLanguageOverrideVisitor(defDerivGrammar);
      traverser.add4Grammar(overrideVisitor);
      tagDefHC.get().accept(traverser);
    }

    // TOP-ish / LanguageOverrideVisitor
    Optional<ASTMCGrammar> tagSchemaHC = parseGrammarHC(grammar, modelPathHC, TaggingConstants.TAGSCHEMA_SUFFIX);
    if (tagSchemaHC.isPresent()) {
      traverser = GrammarMill.traverser();
      TagLanguageOverrideVisitor overrideVisitor = new TagLanguageOverrideVisitor(schemaDerivGrammar);
      traverser.add4Grammar(overrideVisitor);
      tagSchemaHC.get().accept(traverser);
    }

    saveGrammar(defDerivGrammar, fpp, outDirectory);
    saveGrammar(schemaDerivGrammar, fpp, outDirectory);
  }

  static Path saveGrammar(ASTMCGrammar defDerivGrammar, Grammar_WithConceptsFullPrettyPrinter fpp, File outDirectory) throws IOException {
    // compute name of output file
    String directories = outDirectory.getPath().replace('\\', '/') + "/" + Joiner.on("/").join(defDerivGrammar.getPackageList());
    if (!Paths.get(directories).toFile().exists()) {
      Files.createDirectories(Paths.get(directories));
    }
    Path path = Paths.get(directories + "/" + defDerivGrammar.getName() + ".mc4");
    Reporting.reportFileCreation(path.toString());
    return Files.writeString(path, fpp.prettyprint(defDerivGrammar));
  }

  static  Optional<ASTMCGrammar> parseGrammarHC(ASTMCGrammar grammar, MCPath paths, String suffix) {
    List<String> hcGrammarNames = new ArrayList<>(grammar.getPackageList());
    hcGrammarNames.add(grammar.getName() + suffix + "HC.mc4");

    Path hcGrammarPath = Paths.get(hcGrammarNames.stream().collect(Collectors.joining(File.separator)));
    Optional<URL> hwGrammar = paths.find(hcGrammarPath.toString());
    if (hwGrammar.isPresent()) {
      try{
        return Grammar_WithConceptsMill.parser().parse(MCPath.toPath(hwGrammar.get()).get().toString());
      }catch (IOException ignored){

      }
    }
    return Optional.empty();
  }

  /**
   * Get the relative path of a Tag grammar from the model path and the original grammar location
   * @param modelPath the model path
   * @param grammar the original grammar file
   * @return a relative File (from the output dir)
   */
  public static File getTagGrammar(List<String> modelPath, File grammar, String tagtype) {
    Path grammarP = grammar.toPath();
    for (String mF : modelPath) {
      Path mP = new File(mF).toPath();
      if (!mP.getFileSystem().equals(grammarP.getFileSystem()))
        continue;
      if (!Objects.equals(mP.getRoot(), grammarP.getRoot()))
        continue;
      Path relP = mP.relativize(grammarP);
      if (relP.startsWith("../"))
        continue; // the grammar is not a file within the mP path
      File relFile = relP.toFile();
      return new File(relFile.getParent(), relFile.getName().replace(".mc4", tagtype +".mc4"));
    }
    Log.error("0xA5C11 Could not determine Tagging path for grammar " + grammar);
    return null;
  }
}