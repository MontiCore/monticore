/* (c) https://github.com/MontiCore/monticore */

package testinputs;

public class GrammarsForTestsProvider {
  
  public static void main(String[] args) {
    GrammarsForTestsProvider prov = new GrammarsForTestsProvider() {
    };
    System.out.println(prov.getContent());
  }
  
  public String getHeader() {
    String s = "package stationgrammar.ast;\n";
    s += "grammar stationgrammar {\n";
    
    s += "		options{";
    s += "parser \"k=6;defaultErrorHandler=false;\"	\n";
    s += "lexer \"k=2;charVocabulary='\\\\\\\\u0003'..'\\\\\\\\u7FFE';testLiterals=false;\"\n}\n";
    
    s += "\n";
    s += "	// standard identifier which may start with a $ or an underscore\n";
    s += "	ident IDENT\n";
    s += "		\"options {testLiterals=true;}\"\n";
    s += "		\"( 'a'..'z' | 'A'..'Z' | '_' | '$' )( 'a'..'z' | 'A'..'Z' | '_' | '0'..'9' | '$' )*\";\n";
    
    s += "	// standard definition of strings using escape sequence as a protected rule\n";
    s += "	ident / ESC\n";
    s += "		\"'\\\\\\\\'('n' { $setText(\\\"\\\\n\\\"); } |'r'{ $setText(\\\"\\\\r\\\"); }|'t'{ $setText(\\\"\\\\t\\\"); }|'\\\"' { $setText(\\\"\\\\\\\"\\\");  }|'\\\\\\\\'{ $setText(\\\"\\\\\\\\\\\");})\";\n";
    s += "	ident STRING\n";
    s += "		\"'\\\"'! (ESC|~('\\\"'|'\\\\\\\\'|'\\\\n'|'\\\\r'))* '\\\"'!\";\n";
    s += "	ident INT\n";
    s += "		\"( '0'..'9' ) ( '0' .. '9' )*\";\n";
    
    return s;
  }
  
  public String getFooter() {
    return "\n}\n";
  }
  
  public String getRules() {
    String s = getDiagramRule();
    s += getCountryRule();
    s += getTownRule();
    s += getStationRule();
    s += getSwitchRule();
    s += getStreetRule();
    s += getRailwayRule();
    return s;
  }
  
  public String getDiagramRule() {
    String s = "Diagram=";
    s += "!\"diagram\"\n";
    s += "Name:IDENT \"{\"\n";
    s += "(Countries:Country)*\n";
    s += "(Switches:Switch)*\n";
    s += "(Streets:Street)*\n";
    s += "(Railways:Railway)*\n";
    s += "\"}\"\n";
    s += ";\n";
    return s;
  }
  
  public String getCountryRule() {
    String s = "";
    s += "Country=\n";
    s += "	!\"country\"\n";
    s += "	Name:IDENT \"{\"\n";
    s += "	(Towns:Town)*\n";
    s += "	\"}\"\n";
    s += "	;\n";
    return s;
  }
  
  public String getTownRule() {
    String s = "";
    s += "Town=\n";
    s += "	!\"town\"\n";
    s += "	Thename:IDENT \"{\"\n";
    s += "	(Stations:Station)*\n";
    s += "	(Switches:Switch)*\n";
    s += "	(Railways:Railway)*\n";
    s += "	\"}\"\n";
    s += "	;\n";
    return s;
  }
  
  public String getStationRule() {
    String s = "";
    s += "Station=\n";
    s += "	!\"station\" \n";
    s += "	Name:IDENT\n";
    s += "	\";\"\n";
    s += "	;\n";
    return s;
  }
  
  public String getSwitchRule() {
    String s = "";
    s += "Switch=\n";
    s += "	!\"switch\"\n";
    s += "	Thename:IDENT\n";
    s += "	\";\"\n";
    s += "	;\n";
    return s;
  }
  
  public String getStreetRule() {
    String s = "";
    s += "Street=\n";
    s += "	!\"street\"\n";
    s += "	Name:IDENT\n";
    s += "	Firsttown:IDENT\n";
    s += "	Secondtown:IDENT\n";
    s += "	Length: INT !\"km\"\n";
    s += "	\";\"\n";
    s += "	;\n";
    return s;
  }
  
  public String getRailwayRule() {
    String s = "";
    s += "Railway=\n";
    s += "	!\"railway\"\n";
    s += "	Name: IDENT\n";
    s += "	Firstname:IDENT\n";
    s += "	Secondname:IDENT\n";
    s += "	Time: INT !\"min\"\n";
    s += "	\";\"\n";
    s += "	;\n";
    return s;
  }
  
  public String getGlobalNamings() {
    String s = "";
    s += "concept globalnaming Railway {\n";
    s += "  define Station.Name;\n";
    s += "  define Switch.Thename;\n";
    s += "  usage Railway.Firstname;\n";
    s += "  usage Railway.Secondname;\n";
    s += "}	\n";
    
    s += "concept globalnaming Street{\n";
    s += "  define Town.Thename;\n";
    s += "  usage Street.Firsttown;\n";
    s += "  usage Street.Secondtown;\n";
    s += "  }\n";
    
    return s;
  }
  
  public String getContent() {
    String content = getHeader();
    content += getRules();
    content += getGlobalNamings();
    content += getFooter();
    return content;
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    String content = getContent();
    String[] asArray = content.split("\n");
    String ret = "";
    for (int i = 0; i < asArray.length; i++) {
      ret += ("" + (i + 1) + ") " + asArray[i] + "\n");
    }
    return ret;
  }
  
}
