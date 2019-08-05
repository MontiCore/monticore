package de.monticore.typepersistence;

import com.google.common.collect.Lists;
import de.monticore.aggregation.blah._parser.BlahParser;
import de.monticore.aggregation.blah._symboltable.BlahScope;
import de.monticore.aggregation.blah._symboltable.BlahSymbolTableCreator;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.typepersistence.variable._ast.ASTVar;
import de.monticore.typepersistence.variable._parser.VariableParser;
import de.monticore.typepersistence.variable._symboltable.VariableLanguage;
import de.monticore.typepersistence.variable._symboltable.VariableSymbolTableCreator;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

public class TypePersistenceTest {

  @Test
  public void test() throws IOException {

    // infrastruktur aufbauen, modelle zum resolven einlesen, SymTab aufbauen, adapter schreiben, globalscope foo und blah verbinden
    // TransitiveAdapterResolvingFilter implementieren und im globscope registrieren,
    //
   /* ***************************************************************************************************************
   ******************************************************************************************************************
                                       Blah/Blub Infrastruktur
    ******************************************************************************************************************
    */

    //Create global scope for our language combination
    VariableLanguage varLang = new VariableLanguage("VariableLangName", "var") {
    };
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();

    resolvingConfiguration.addDefaultFilters(varLang.getResolvingFilters());
    GlobalScope globalScope = new GlobalScope(new ModelPath(), varLang, resolvingConfiguration);

    //Parse blah model
    VariableParser blahParser = new VariableParser();
    Optional<ASTVar> varModel = blahParser.parse_String("var String a");
    VariableSymbolTableCreator varSymbolTableCreator = new VariableSymbolTableCreator(resolvingConfiguration, globalScope);
    Scope blahSymbolTable = varSymbolTableCreator.createFromAST(varModel.get());
ASTMCType a;
    assertTrue(varModel.isPresent());
    System.out.println(varModel);
  }
}
