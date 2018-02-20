/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.scandentity;

import de.monticore.ModelingLanguageFamily;
import de.monticore.symboltable.mocks.languages.entity.EntityLanguage;
import de.monticore.symboltable.mocks.languages.statechart.StateChartLanguage;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class ScAndEntityLanguageFamily extends ModelingLanguageFamily {

  public ScAndEntityLanguageFamily() {
    addModelingLanguage(new StateChartLanguage());
    addModelingLanguage(new EntityLanguage());

    addResolver(new Sc2EntityTransitiveResolvingFilter());
  }
}
