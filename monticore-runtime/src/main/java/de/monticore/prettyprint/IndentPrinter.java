/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

/**
 * This class can be used as a printer to files or StringBuilders. It adds
 * indentation by using the special methods indent(), unindent(). The method
 * calls indent() and reindent() affect the current line, even if the first part
 * was already printed. Note: Call flushBuffer() at the end in order to flush
 * the internal buffer to retrieve the correct output.
 * STATE Ok; Useful class for indent strings
 */
public class IndentPrinter {
  
  // Current level opf indentation
  protected int indent = 0;
  
  // Currrent identation as String
  protected String spacer = "";
  
  // length of one indent:
  // default length is 2
  protected String sp = "  ";
  
  // line length
  protected int maxlinelength = -1;
  
  // optional break (makes additional line breal only after
  // optionalBreak()-invocation
  protected boolean optionalBreak = false;
  
  private int optionalBreakPosition = -1;
  
  // StringBuilder for for content that can still be moved by indent/unindent
  protected StringBuilder linebuffer = new StringBuilder();
  
  // StringBuilder for already correctly indented content
  protected StringBuilder writtenbuffer;
  
  /**
   * Uses a new interal buffer for writing
   */
  public IndentPrinter() {
    this(new StringBuilder());
  }
  
  /**
   * Uses this StringBuilder for appending
   * 
   * @param writtenbuffer Buffer to use
   */
  public IndentPrinter(StringBuilder writtenbuffer) {
    this.writtenbuffer = writtenbuffer;
  }
  
  /**
   * Uses startcontent as start of buffer an sets the indention
   * 
   * @param startContent first line of content
   * @param indention first indention (e.g. 0 for classes, 1 for methods and
   *          attributes)
   */
  public IndentPrinter(String startContent, int indention) {
    this();
    indent(indention);
    addLine(startContent);
  }
  
  /**
   * Returns the content of the internal buffer Note: This method isn't side
   * effect free: It flushes the internal buffer. After calling this method, new
   * text added by print/println is automaticly starting in a new line
   * 
   * @return Content of buffer as String
   */
  public String getContent() {
    
    flushBuffer();
    return writtenbuffer.toString();
  }
  
  /**
   * Set length of intendation: number of spaces per level
   * 
   * @param l number of spaces per level (default is 2)
   */
  public void setIndentLength(int l) {
    sp = "";
    for (int i = 0; i < l; i++) {
      sp += " ";
    }
    
    spacer = "";
    for (int i = 0; i < indent; i++) {
      spacer += sp;
    }
  }
  
  /**
   * Returns length of intendation: number of spaces per level
   * 
   * @return number of spaces per level
   */
  public int getIndentLength() {
    return sp.length();
  }
  
  /**
   * This method actually does the print. It deals with "\r""\n","\r","\n" in
   * the string. This method is not meant for external use.
   * 
   * @param String String to be
   */
  protected void doPrint(String s) {
    
    // get position of "\n"
    int pos = s.indexOf("\n");
    while (pos >= 0) {
      String substring = s.substring(0, pos);
      
      // Start new line if string exceeds maxlinelength
      if (maxlinelength != -1) {
        if (pos + linebuffer.length() > maxlinelength) {
          
          handleOptionalBreak();
        }
      }
      // Print up to newline, then a newline and new spacer
      linebuffer.append(substring);
      
      flushBuffer();
      writtenbuffer.append("\n");
      
      s = s.substring(pos + 1);
      pos = s.indexOf("\n");
    }
    
    // Start new line if string exceeds maxlinelength
    if (maxlinelength != -1) {
      if (s.length() + linebuffer.length() > maxlinelength) {
        handleOptionalBreak();
      }
    }
    
    linebuffer.append(s);
  }
  
  private void handleOptionalBreak() {
    if (optionalBreak) {
      
      if (optionalBreakPosition > 0) {
        String sub2 = linebuffer.substring(optionalBreakPosition);
        linebuffer = linebuffer.delete(optionalBreakPosition, linebuffer.length());
        
        flushBuffer();
        writtenbuffer.append("\n");
        
        linebuffer.append(sub2);
      }
      
    }
    else {
      flushBuffer();
      writtenbuffer.append("\n");
    }
  }
  
  /**
   * Flushes the internal buffer. After calling this method, new text added by
   * print/println is automaticly starting in a new line
   */
  public void flushBuffer() {
    
    optionalBreakPosition = 0;
    
    // HK: Live with trailing spaces, as comments keep on changing otherwise
    // prune trailing spaces
    // int i = linebuffer.length() - 1;
    // while (i >= 0 && linebuffer.charAt(i) == ' ')
    // i--;
    // linebuffer.setLength(i + 1);
    
    // indent nonempty buffers
    if (linebuffer.length() != 0) {
      writtenbuffer.append(spacer);
      writtenbuffer.append(linebuffer);
    }
    linebuffer.setLength(0);
  }
  
  /**
   * For positive values of i: indent i levels For negative values of i:
   * unindent -i levels This method call affects the current line, even if the
   * first part was already printed.
   * 
   * @param i Number of levels to indent/unindent
   */
  public void indent(int i) {
    if (i > 0) {
      indent += i;
      for (int j = 0; j < i; j++)
        spacer += sp;
    }
    else if (i < 0) {
      while (i < 0 && indent > 0) {
        this.indent--;
        spacer = spacer.substring(sp.length());
        i++;
      }
    }
  }
  
  /**
   * Indent one level This method call affects the current line, even if the
   * first part was already printed.
   */
  public void indent() {
    this.indent++;
    spacer += sp;
  }
  
  /**
   * Unindent one level This method call affects the current line, even if the
   * first part was already printed.
   */
  public void unindent() {
    if (this.indent > 0) {
      this.indent--;
      spacer = spacer.substring(sp.length());
    }
  }
  
  /**
   * Prints the result of the toString() method of Object o or the string "null"
   * if o has the null value
   * 
   * @param o Object to be printed
   */
  public void print(Object o) {
    doPrint((o == null) ? "null" : o.toString());
  }
  
  public void printWithoutProcessing(Object o) {
    
    linebuffer.append(o.toString());
  }
  
  /**
   * Prints the result of the toString() method of Object o or the string "null"
   * if o has the null value followed by a newline
   * 
   * @param o Object to be printed
   */
  public void println(Object o) {
    print(o);
    println();
  }
  
  /**
   * Prints a newline
   */
  public void println() {
    doPrint("\n");
  }
  
  /**
   * Prints n newlines
   * 
   * @param n Number of newlines
   */
  public void println(int n) {
    for (int i = 0; i < n; i++) {
      doPrint("\n");
    }
    
  }
  
  /**
   * Returns the WrittenBuffer without flushing the buffer. This methdo call is
   * side effect free, but might not contain the whole buffer. To retrieve the
   * complete buffer call flushBuffer() before getWrtttenBuffer()
   * 
   * @return Buffer with already written lines
   */
  public StringBuilder getWrittenbuffer() {
    return writtenbuffer;
  }
  
  /**
   * Returns true if the current position is the beginning of a new line
   * 
   * @return true if current line is empty
   */
  public boolean isStartOfLine() {
    return (linebuffer.length() == 0);
  }
  
  /**
   * adds a line and sets the indention automatically as following: if more "}"
   * than "{" in newContent, the next line will be unindented by the difference,
   * otherwise the current line will be indented by the difference. If the
   * difference is 0, no indention will be changed
   * 
   * @param newContent content to add
   */
  public void addLine(Object newContent) {
    String nc = newContent.toString().trim();
    int counter = 0;
    for (int i = 0; i < nc.length(); i++) {
      if (nc.charAt(i) == '{') {
        counter++;
      }
      else if (nc.charAt(i) == '}') {
        counter--;
      }
    }
    if (counter < 0) {
      indent(counter);
    }
    print(newContent);
    println();
    if (counter > 0) {
      indent(counter);
    }
  }
  
  /**
   * Returns the maximum used line length
   * 
   * @return
   */
  public int getMaxlinelength() {
    return maxlinelength;
  }
  
  /**
   * Sets the maximum used line length
   * 
   * @param maxlinelength
   */
  public void setMaxlinelength(int maxlinelength) {
    this.maxlinelength = maxlinelength;
  }
  
  /**
   * optional break (makes additional line breal only after
   * optionalBreak()-invocation
   * 
   * @return
   */
  public boolean isOptionalBreak() {
    return optionalBreak;
  }
  
  /**
   * optional break (makes additional line breal only after
   * optionalBreak()-invocation
   * 
   * @param optionBreak
   */
  public void setOptionalBreak(boolean optionBreak) {
    this.optionalBreak = optionBreak;
  }
  
  public void optionalBreak() {
    this.optionalBreakPosition = linebuffer.length();
  }
  
  public void clearBuffer() {
    flushBuffer();
    writtenbuffer.setLength(0);
  }
}
