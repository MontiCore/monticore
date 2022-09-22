package de.monticore.gradle;

import de.monticore.symboltable.serialization.json.JsonElement;

public interface GradleTaskStatistic {
  public JsonElement getGradleStatisticData();
}
