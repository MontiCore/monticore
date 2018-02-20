/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.texteditor.syntaxhighlighting;

import org.eclipse.jface.text.rules.IWordDetector;

public class AnyWordDetector implements IWordDetector {
  
  @Override
  public boolean isWordStart(char character) {
    return character != ' ';
  }
  
  @Override
  public boolean isWordPart(char character) {
    return character != ' ';
  }
}
