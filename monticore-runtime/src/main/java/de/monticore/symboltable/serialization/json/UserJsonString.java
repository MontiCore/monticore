/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

import de.monticore.prettyprint.IndentPrinter;
import org.apache.commons.text.StringEscapeUtils;

public class UserJsonString extends JsonString {

  public UserJsonString(String value) {
    super(value);
  }

  @Override
  public String print(IndentPrinter p) {
    p.print('"' + StringEscapeUtils.escapeJson(value) + '"');
    return p.getContent();
  }

}
