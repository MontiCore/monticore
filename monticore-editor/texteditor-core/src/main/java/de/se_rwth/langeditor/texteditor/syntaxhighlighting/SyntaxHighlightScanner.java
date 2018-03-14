/* (c)  https://github.com/MontiCore/monticore */package de.se_rwth.langeditor.texteditor.syntaxhighlighting;

import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

import com.google.inject.Inject;

import de.se_rwth.langeditor.global.ColorManager;
import de.se_rwth.langeditor.injection.TextEditorScoped;
import de.se_rwth.langeditor.language.Language;

@TextEditorScoped
public class SyntaxHighlightScanner extends RuleBasedScanner {
  
  private final ColorManager colorManager;
  
  @Inject
  public SyntaxHighlightScanner(ColorManager colorManager, Language description) {
    this.colorManager = colorManager;
    
    setRules(new IRule[] { keywordRule(description.getKeywords()),
        singleLineCommentRule(),
        multiLineCommentRule() });
  }
  
  private WordRule keywordRule(List<String> keywords) {
    WordRule wordRule = new WordRule(new AnyWordDetector());
    
    TextAttribute keywordStyle = new TextAttribute(colorManager.getColor(new RGB(130, 0, 120)),
        null, SWT.BOLD);
    Token keywordToken = new Token(keywordStyle);
    for (String keyword : keywords) {
      wordRule.addWord(keyword, keywordToken);
    }
    
    return wordRule;
  }
  
  private IRule singleLineCommentRule() {
    TextAttribute singleLineCommentStyle = new TextAttribute(
        colorManager.getColor(new RGB(95, 127, 63)));
    Token singleLineCommentToken = new Token(singleLineCommentStyle);
    return new SingleLineRule("//", "", singleLineCommentToken);
  }
  
  private IRule multiLineCommentRule() {
    TextAttribute multiLineCommentStyle = new TextAttribute(
        colorManager.getColor(new RGB(95, 127, 63)));
    Token multiLineCommentToken = new Token(multiLineCommentStyle);
    return new MultiLineRule("/*", "*/", multiLineCommentToken);
  }
}
