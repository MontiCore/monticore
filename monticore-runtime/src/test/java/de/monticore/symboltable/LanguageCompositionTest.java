/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable;

import de.monticore.ModelingLanguageFamily;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.mocks.languages.ParserMock;
import de.monticore.symboltable.mocks.languages.entity.ActionSymbol;
import de.monticore.symboltable.mocks.languages.entity.EntitySymbol;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTAction;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntity;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntityCompilationUnit;
import de.monticore.symboltable.mocks.languages.extendedstatechart.XStateChartSymbol;
import de.monticore.symboltable.mocks.languages.extendedstatechart.XStateSymbol;
import de.monticore.symboltable.mocks.languages.scandentity.EntityEmbeddingScLanguage;
import de.monticore.symboltable.mocks.languages.scandentity.Sc2EntityAdapter;
import de.monticore.symboltable.mocks.languages.scandentity.ScAndEntityLanguageFamily;
import de.monticore.symboltable.mocks.languages.statechart.StateChartSymbol;
import de.monticore.symboltable.mocks.languages.statechart.StateSymbol;
import de.monticore.symboltable.mocks.languages.statechart.asts.ASTState;
import de.monticore.symboltable.mocks.languages.statechart.asts.ASTStateChart;
import de.monticore.symboltable.resolving.CommonResolvingFilter;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author  Pedram Mir Seyed Nazari
 *
 */
public class LanguageCompositionTest {

//  @Test
  public void testLanguageFamily() {
    final ModelingLanguageFamily languageFamily = new ScAndEntityLanguageFamily();


    final ModelPath modelPath =
        new ModelPath(Paths.get("src/test/resources/de/monticore/symboltable/languagecomposition"));
    final MutableScope globalScope = new GlobalScope(modelPath, languageFamily);



    final EntitySymbol cla = globalScope.<EntitySymbol>resolve("family.Cla", EntitySymbol.KIND).orElse(null);
    assertNotNull(cla);
    assertEquals("Cla", cla.getName());
    assertEquals("family.Cla", cla.getFullName());

    final StateChartSymbol sc = globalScope.<StateChartSymbol>resolve("family.Sc", StateChartSymbol.KIND).orElse(null);
    assertNotNull(sc);
    assertEquals("Sc", sc.getName());
    assertEquals("family.Sc", sc.getFullName());

    // Starting from Cla the symbol Sc can be resolved if its kind is passed directly (without adaptors).
    assertTrue(cla.getSpannedScope().resolve("family.Sc", StateChartSymbol.KIND).isPresent());

    // ...if Sc is resolved through entity symbol kind, it is resolved as a Sc2Entity adaptor.
      final EntitySymbol adaptedEntitySymbol = cla.getSpannedScope().<EntitySymbol>resolve("family.Sc",

          EntitySymbol.KIND).orElse(null);
    assertNotNull(adaptedEntitySymbol);
    assertTrue(adaptedEntitySymbol instanceof Sc2EntityAdapter);

    assertSame(sc, ((Sc2EntityAdapter) adaptedEntitySymbol).getAdaptee());
  }

//  @Test
  public void testEmbeddedLanguage() {
    ASTEntityCompilationUnit astEntityCompilationUnit = new ASTEntityCompilationUnit();
    astEntityCompilationUnit.setPackageName("embedding");

    ASTEntity astEntity = new ASTEntity();
    astEntity.setName("Entity");
    astEntityCompilationUnit.addChild(astEntity);

    ASTAction astAction = new ASTAction();
    astAction.setName("action");
    astEntity.addChild(astAction);

    ASTStateChart astStateChart = new ASTStateChart();
    astStateChart.setName("Sc");
    ASTState astState = new ASTState();
    astState.setName("state");
    astStateChart.addChild(astState);

    // the embedding //
    astEntity.addChild(astStateChart);


    final EntityEmbeddingScLanguage language = new EntityEmbeddingScLanguage();
    language.setParser(new ParserMock(astEntityCompilationUnit));

    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(language.getResolvingFilters());


    final ModelPath modelPath = new ModelPath(Paths.get
        ("src/test/resources/de/monticore/symboltable/languagecomposition"));

    final Scope globalScope = new GlobalScope(modelPath, language, resolvingConfiguration);

    final EntitySymbol entity = globalScope.<EntitySymbol>resolve("embedding.Entity",
        EntitySymbol.KIND).orElse(null);
    assertNotNull(entity);



    // Resolve embedded statechart
    StateChartSymbol sc = entity.getSpannedScope().<StateChartSymbol>resolveLocally("Sc", StateChartSymbol.KIND).orElse(null);
    assertNotNull(sc);
    assertEquals("Sc", sc.getName());
    assertEquals("embedding.Entity.Sc", sc.getFullName());

  }

  @Test
  public void testLanguageInheritance() {
    StateChartSymbol sc = new StateChartSymbol("SC");
    StateSymbol state = new StateSymbol("state");
    sc.addState(state);

    EntitySymbol entity = new EntitySymbol("Entity");
    ActionSymbol action = new ActionSymbol("action");
    entity.addAction(action);

    MutableScope scope = new CommonScope(true);
    scope.addResolver(CommonResolvingFilter.create(StateChartSymbol.KIND));

    sc.getSpannedScope().getAsMutableScope().addResolver(CommonResolvingFilter.create(StateSymbol.KIND));

    XStateChartSymbol xSc = new XStateChartSymbol("xSc");
    xSc.getSpannedScope().getAsMutableScope().addResolver(CommonResolvingFilter.create(StateSymbol.KIND));
    // Note how symbols of the sub language can be used without any adapters

    scope.add(xSc);
    assertSame(xSc, scope.resolve("xSc", StateChartSymbol.KIND).get());
    // Super symbol cannot be used instead of sub. Resolver for sub needed.
    assertFalse(scope.resolve("xSc", XStateChartSymbol.KIND).isPresent());
    scope.addResolver(CommonResolvingFilter.create(XStateChartSymbol.KIND));
    assertSame(xSc, scope.resolve("xSc", XStateChartSymbol.KIND).get());

    XStateSymbol xState =  new XStateSymbol("xState");
    xSc.addState(xState);
    assertSame(xState, xSc.getSpannedScope().resolve("xState", StateSymbol.KIND).get());
    // Super symbol cannot be used instead of sub. Resolver for sub needed.
    assertFalse(xSc.getSpannedScope().resolve("xState", XStateSymbol.KIND).isPresent());
    xSc.getSpannedScope().getAsMutableScope().addResolver(CommonResolvingFilter.create(XStateSymbol.KIND));
    assertSame(xState, xSc.getSpannedScope().resolve("xState", XStateSymbol.KIND).get());

    XStateSymbol xState2 = new XStateSymbol("xState2");
    sc.addState(xState2);
    assertSame(xState2, sc.getState("xState2").get());
    assertSame(xState2, sc.getSpannedScope().resolve("xState2", StateSymbol.KIND).get());
  }


  
}
