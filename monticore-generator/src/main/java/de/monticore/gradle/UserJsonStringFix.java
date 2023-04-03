/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symboltable.serialization.json.JsonString;
import org.apache.commons.text.StringEscapeUtils;

@Deprecated
// Remove on Release of 7.5.0
public class UserJsonStringFix extends JsonString {

  public UserJsonStringFix(String value) {
    super(value);
  }

  @Override
  public String print(IndentPrinter p) {
    p.print('"' + StringEscapeUtils.escapeJson(value) + '"');
    return p.getContent();
  }

}
