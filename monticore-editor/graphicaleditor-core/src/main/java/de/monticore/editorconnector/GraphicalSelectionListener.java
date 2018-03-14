/* (c)  https://github.com/MontiCore/monticore */package de.monticore.editorconnector;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutline;

import de.monticore.ast.ASTNode;
import de.monticore.genericgraphics.GenericFormEditor;
import de.monticore.genericgraphics.GenericGraphicsEditor;
import de.monticore.genericgraphics.GenericGraphicsViewer;
import de.monticore.genericgraphics.controller.editparts.IMCGraphicalEditPart;
import de.monticore.genericgraphics.controller.editparts.connections.IMCConnectionEditPart;
import de.monticore.genericgraphics.controller.selection.IHasLineNumbers;
import de.monticore.genericgraphics.controller.selection.SelectionSyncException;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.langeditor.texteditor.TextEditorImpl;

/**
 * <p>
 * This class provides an {@link ISelectionListener} for {@link TextEditorImpl TextEditorImpls} and
 * {@link GenericGraphicsEditor GenericGraphicsEditors}. It synchronizes the selection of both, that
 * means, if something is selected in the text its also selected in the graphical editor and vice
 * versa.<br>
 * <br>
 * It assumes that only one {@link TextEditorImpl} and one {@link GenericGraphicsEditor} are opened
 * on the same file.
 * </p>
 * <p>
 * Therefore is listens for selection changes and synchronizes them as follows:
 * <ul>
 * <li>Rouhly, a mapping from text position to EditParts and a mapping from EditParts to text
 * positions is created. It is created on the basis of
 * <ul>
 * <li>{@link ASTNode ASTNodes} that save their linenumbers</li>
 * <li>models that implement the {@link IHasLineNumbers} interface</li>
 * </ul>
 * </li>
 * <li>Whenever one (or more) {@link GraphicalEditPart GraphicalEditPart(s)} are selected in a
 * {@link GenericGraphicsEditor}, an {@link TextEditorImpl TextEditorImpls} with the same file in
 * the workbench is searched. If found, the selection is set accordingly.</li> *
 * <li>Whenever one (or more) line(s) in a {@link TextEditorImpl} is (are) selected, it's checked if
 * this listener is registered to the {@link GenericGraphicsEditor} with the same file as input. If
 * so, the selection is set accordingly.</li>
 * <li>Whenever one (or more) objects in a {@link ContentOutline} is (are) selected, it's checked if
 * this listener is registered to the {@link GenericGraphicsEditor} with the same file as input. If
 * so, the selection is set accordingly.</li>
 * </ul>
 * </p>
 * TODO: Make it faster! Performance is low, perhaps some cached values?
 * 
 * @author Tim Enger
 */
public class GraphicalSelectionListener implements ISelectionListener {
  
  private GenericGraphicsViewer viewer;
  
  private IFile ownFile;
  
  private ISelection ownSelection;
  
  // mapping from line numbers to all editparts
  // that have an associated model object, that
  // has this line number in the textual representation
  // in addition to only saving the mapping of line -> editpart
  // also associate the exact position in the model
  private Map<Integer, List<IMCGraphicalEditPart>> linesToEP = new LinkedHashMap<Integer, List<IMCGraphicalEditPart>>();;
  
  // mapping from editparts to position in textual source of the model
  private Map<IMCGraphicalEditPart, TextPosition> epToPos = new LinkedHashMap<IMCGraphicalEditPart, TextPosition>();;
  
  /**
   * Constructor
   * 
   * @param ownFile The {@link IFile} the editor is opened on.
   * @param viewer The {@link GraphicalViewer} this listener is based on.
   * @throws SelectionSyncException
   */
  public GraphicalSelectionListener(IFile ownFile, GenericGraphicsViewer viewer) {
    this.viewer = viewer;
    this.ownFile = ownFile;
  }
  
  @Override
  public void selectionChanged(IWorkbenchPart part, ISelection selection) {
    if (selection.equals(ownSelection)) {
      return;
    }
    if (selection instanceof IStructuredSelection) {
      IStructuredSelection ss = (IStructuredSelection) selection;
      if (part instanceof GenericFormEditor) {
        IFile file = getFile(part);
        if (file == null || !file.equals(ownFile)) {
          return;
        }
        setSelection(ss, ((GenericFormEditor) part).getTextEditor());
      }
      else if (part instanceof ContentOutline) {
        // selection originates from agraphical OutlinePage
        ContentOutline co = (ContentOutline) part;
        IEditorPart activeE = co.getViewSite().getPage().getActiveEditor();
        if (activeE instanceof TextEditorImpl) {
          setSelection(ss, (TextEditorImpl) activeE);
        }
      }
    }
    else if (selection instanceof ITextSelection) {
      ITextSelection ts = (ITextSelection) selection;
      if (part instanceof GenericFormEditor) {
        IFile file = getFile(part);
        if (file == null || !file.equals(ownFile)) {
          return;
        }
        setSelection(ts, (GenericFormEditor) part);
      }
      else if (part instanceof ContentOutline) {
        // selection originates from a textual OutlinePage
        // Nothing to do: Handled in OutlinePage
      }
    }
  }
  
  /**
   * Create the mappings needed for computation of selections:
   * <ul>
   * <li><code>epToPos</code>: Map from {@link IMCGraphicalEditPart IMCGraphicalEditParts} to
   * {@link TextPosition TextPositions}. <br>
   * Thereby, {@link TextPosition TextPositions} are computed based on the model object which should
   * be either an {@link ASTNode} or an {@link IHasLineNumbers}.</li>
   * <li><code>linesToEPToPos</code>: A map from line numbers to all {@link IMCGraphicalEditPart
   * IMCGraphicalEditParts} whose underlying model objects have this line number</li>
   * </ul>
   * 
   * @throws SelectionSyncException
   */
  public void createMappings() throws SelectionSyncException {
    epToPos.clear();
    linesToEP.clear();
    
    for (Object ep : viewer.getEditPartRegistry().values()) {
      if (ep instanceof IMCGraphicalEditPart) {
        addMapping((IMCGraphicalEditPart) ep);
      }
    }
  }
  
  private void addMapping(IMCGraphicalEditPart ep) {
    Object model = ep.getModel();
    
    if (model instanceof ASTNode) {
      ASTNode node = (ASTNode) ep.getModel();
      addMapping(node, ep);
    }
    else if (model instanceof IHasLineNumbers) {
      IHasLineNumbers o = (IHasLineNumbers) ep.getModel();
      addMapping(o, ep);
    }
  }
  
  private void addMapping(ASTNode node, IMCGraphicalEditPart ep) {
    SourcePosition sps = node.get_SourcePositionStart();
    SourcePosition spe = node.get_SourcePositionEnd();
    
    if (sps == null || spe == null) {
      return;
    }
    
    TextPosition tp = new TextPosition(sps.getLine(), sps.getColumn(), spe.getLine(),
        spe.getColumn());
        
    epToPos.put(ep, tp);
    
    for (int line = sps.getLine(); line <= spe.getLine(); line++) {
      addToMap(line, ep, tp);
    }
  }
  
  private void addMapping(IHasLineNumbers o, IMCGraphicalEditPart ep) {
    TextPosition tp = new TextPosition(o.getStartLine(), o.getStartOffset(), o.getEndLine(),
        o.getEndOffset());
        
    epToPos.put(ep, tp);
    
    for (int line = o.getStartLine(); line <= o.getEndLine(); line++) {
      addToMap(line, ep, tp);
    }
  }
  
  private void addToMap(int line, IMCGraphicalEditPart ep, TextPosition tp) {
    // Map<Integer, Map<IMCGraphicalEditPart, TextPosition>>
    List<IMCGraphicalEditPart> listForLine = linesToEP.get(line);
    if (listForLine == null) {
      listForLine = new ArrayList<IMCGraphicalEditPart>();
      linesToEP.put(line, listForLine);
    }
    listForLine.add(ep);
  }
  
  /**
   * <p>
   * Sets the text selection in a {@link TextEditorImpl} according to the selected
   * {@link GraphicalEditPart GraphicalEditParts} in the {@link IStructuredSelection}.
   * </p>
   * <p>
   * The selection is set according to the editpart {@link TextPosition TextPositions}. <br>
   * <br>
   * If only one is selected, the selection is precisely only the {@link TextPosition}.<br>
   * If more than one is selected, the selection goes from the lowest start position to the highest
   * end position in the text editor.
   * </p>
   * 
   * @param ss The {@link IStructuredSelection} containing the selected {@link GraphicalEditPart
   * GraphicalEditParts}.
   * @param editor The {@link TextEditorImpl} to set the text selection in.
   */
  @SuppressWarnings("unchecked")
  private void setSelection(IStructuredSelection ss, TextEditorImpl editor) {
    List<Object> selectionList = ss.toList();
    
    // if there is more than one element selected select all
    // from minimum line number with minimum offset
    // to maximum line number with maximum offset
    // works also for only one element selected
    
    int startLine = Integer.MAX_VALUE;
    int startOffset = Integer.MAX_VALUE;
    int endLine = Integer.MIN_VALUE;
    int endOffset = Integer.MIN_VALUE;
    
    boolean setOnce = false;
    
    for (Object o : selectionList) {
      if (o instanceof IMCGraphicalEditPart) {
        IMCGraphicalEditPart ep = (IMCGraphicalEditPart) o;
        
        if (!ep.isSelectable()) {
          continue;
        }
        if (ep.getModel() instanceof ASTNode) {
          ASTNode astNode = (ASTNode) ep.getModel();
          
          startLine = Math.min(startLine, astNode.get_SourcePositionStart().getLine());
          startOffset = Math.min(startOffset, astNode.get_SourcePositionStart().getColumn());
          endLine = Math.max(endLine, astNode.get_SourcePositionEnd().getLine());
          endOffset = Math.max(endOffset, astNode.get_SourcePositionEnd().getColumn());
          setOnce = true;
        }
      }
    }
    if (setOnce) {
      selectText(editor, new TextPosition(startLine, startOffset, endLine, endOffset));
    }
  }
  
  /**
   * Select text in text {@link TextEditorImpl} according to {@link TextPosition}.
   * 
   * @param editor The {@link TextEditorImpl}
   * @param tp The {@link TextPosition}
   */
  private void selectText(TextEditorImpl editor, TextPosition tp) {
    ISelectionProvider sp = editor.getSelectionProvider();
    
    // if Textpos is null, select nothing
    if (tp == null) {
      return;
    }
    
    // we have to find out which offset the lines have, we want to highlight
    IDocumentProvider dp = editor.getDocumentProvider();
    IDocument doc = dp.getDocument(editor.getEditorInput());
    
    int offset = -1;
    int length = -1;
    
    try {
      // offset of first character of the line
      // monticore starts counting from 1, eclipse from 0
      offset = doc.getLineOffset(tp.getStartLine() - 1);
      offset += tp.getStartOffset();
      
      // monticore starts counting from 1, eclipse from 0
      offset--;
      
      // the offset of the endline
      length = doc.getLineOffset(tp.getEndLine() - 1);
      length += tp.getEndOffset();
      
      // substract the offset from start
      length -= offset;
      
      // substract one more, to ensure that not the whole
      // line at the end is selected
      // it should be only selected till the last character
      length--;
    }
    catch (BadLocationException e) {
      Log.error("0xA1101 Text Selection failed due to the following exception: " + e);
      return;
    }
    
    // do the selection
    if (offset > -1 && length > -1) {
      ownSelection = new TextSelection(offset, length);
      sp.setSelection(ownSelection);
    }
  }
  
  /**
   * Sets the graphical selection in the {@link GenericGraphicsEditor} according to the selected
   * text in the {@link ITextSelection}.
   * 
   * @param ts The {@link ITextSelection} containing the selected text
   * @param editor The {@link TextEditorImpl} needed to compute offset.
   */
  private void setSelection(ITextSelection ts, GenericFormEditor editor) {
    
    // we have to find out which offset the lines have, we want to highlight
    IDocumentProvider dp = editor.getTextEditor().getDocumentProvider();
    IDocument doc = dp.getDocument(editor.getEditorInput());
    
    // determine the current text selection
    int startLine = ts.getStartLine();
    int endLine = ts.getEndLine();
    
    int startOffset = -1;
    int endOffset = -1;
    
    try {
      startOffset = ts.getOffset() - doc.getLineOffset(startLine);
      endOffset = ts.getOffset() + ts.getLength() - doc.getLineOffset(endLine);
    }
    catch (BadLocationException e) {
      Log.error("0xA1102 Text Selection failed due to the following exception: " + e);
      return;
    }
    
    // monticore starts counting at 1, eclipse at 0
    startLine++;
    endLine++;
    startOffset++;
    endOffset++;
    
    selectEPFromPosition(startLine, startOffset, endLine, endOffset);
  }
  
  private void selectEPFromPosition(int startLine, int startOffset, int endLine, int endOffset) {
    // collect all potential editparts
    List<IMCGraphicalEditPart> potEPs = new ArrayList<>();
    List<IMCGraphicalEditPart> epsToSelect = new ArrayList<>();
    for (int line = startLine; line <= endLine; line++) {
      List<IMCGraphicalEditPart> epsList = linesToEP.get(line);
      
      if (epsList != null) {
        potEPs.addAll(epsList);
      }
    }
    
    // check if complete text position of EP
    // is included in the selection, and EP is selectable
    // if so select it
    for (IMCGraphicalEditPart ep : potEPs) {
      TextPosition tp = epToPos.get(ep);
      if (tp.isIncluded(startLine, startOffset, endLine, endOffset)) {
        if (ep.isSelectable()) {
          epsToSelect.add(ep);
        }
      }
    }
    
    // check if something will be selected
    if (epsToSelect.isEmpty()) {
      // so nothing will be selected,
      // try to find the smallest encompassing editpart
      IMCGraphicalEditPart smallestEP = null;
      TextPosition smallestTP = null;
      
      for (IMCGraphicalEditPart ep : potEPs) {
        TextPosition tp = epToPos.get(ep);
        
        if (tp.isEncompassing(startLine, startOffset, endLine, endOffset)) {
          if (smallestEP == null) {
            smallestEP = ep;
            smallestTP = tp;
          }
          else {
            // if this text position is encompassed by the current
            // smallest one then this one is the new smallest one
            if (smallestTP.isEncompassing(tp.getStartLine(), tp.getStartOffset(), tp.getEndLine(),
                tp.getEndOffset())) {
                
              // except the new one is a IMCConnectionEP (which is not part of
              // an association)
              // this avoids the selection of "extends"/"implements"
              // connections instead of the corresponding classes
              if (!(ep instanceof IMCConnectionEditPart)
                  || ep.getIdentifier().startsWith("association")) {
                smallestEP = ep;
                smallestTP = tp;
              }
            }
          }
        }
      }
      if (smallestEP != null) {
        if (smallestEP.isSelectable()) {
          epsToSelect.add(smallestEP);
        }
      }
    }
    selectEditParts(epsToSelect);
  }
  
  /**
   * Select the {@link GraphicalEditPart GraphicalEditParts} in the list. And sets the focus (
   * {@link GraphicalViewer#reveal(org.eclipse.gef.EditPart)} to the first one.
   * 
   * @param selection List of {@link GraphicalEditPart GraphicalEditParts} to select.
   */
  private void selectEditParts(List<IMCGraphicalEditPart> selection) {
    ownSelection = new StructuredSelection(selection);
    viewer.setSelection(ownSelection);
    // set focus to the first one
    if (!selection.isEmpty()) {
      if (viewer.getControl() != null)
        viewer.reveal(selection.get(0));
    }
  }
  
  /**
   * @param part {@link IWorkbenchPart}
   * @return The input {@link IFile} of the {@link IWorkbenchPart}.
   */
  private IFile getFile(IWorkbenchPart part) {
    if (part instanceof IEditorPart) {
      return getFileForIEditorPart((IEditorPart) part);
    }
    return null;
  }
  
  private IFile getFileForIEditorPart(IEditorPart part) {
    if (part.getEditorInput() instanceof IFileEditorInput) {
      IFile file = ((IFileEditorInput) part.getEditorInput()).getFile();
      return file;
    }
    return null;
  }
  
  private class TextPosition {
    
    private int startLine;
    
    private int endLine;
    
    private int startOffset;
    
    private int endOffset;
    
    /**
     * @param startLine
     * @param startOffset
     * @param endLine
     * @param endOffset
     */
    public TextPosition(int startLine, int startOffset, int endLine, int endOffset) {
      this.startLine = startLine;
      this.endLine = endLine;
      this.startOffset = startOffset;
      this.endOffset = endOffset;
    }
    
    /**
     * @return The startLine
     */
    public int getStartLine() {
      return startLine;
    }
    
    /**
     * @return The endLine
     */
    public int getEndLine() {
      return endLine;
    }
    
    /**
     * @return The startOffset
     */
    public int getStartOffset() {
      return startOffset;
    }
    
    /**
     * @return The endOffset
     */
    public int getEndOffset() {
      return endOffset;
    }
    
    /**
     * Determines if {@link TextPosition this} is included in the given values.
     * 
     * @param startLine The start line
     * @param startOffset The start offset
     * @param endLine The end line
     * @param endOffset The end offset
     * @return <tt>True</tt> {@link TextPosition this} is included in the given values.
     */
    public boolean isIncluded(int startLine, int startOffset, int endLine, int endOffset) {
      int tpStartLine = getStartLine();
      int tpEndLine = getEndLine();
      if (tpStartLine >= startLine && tpEndLine <= endLine) {
        
        boolean start;
        
        // pay attention to offset
        if (tpStartLine == startLine) {
          start = getStartOffset() >= startOffset;
        }
        else {
          start = true;
        }
        
        if (start && tpEndLine == endLine) {
          return getEndOffset() <= endOffset;
        }
        
        if (start) {
          return true;
        }
      }
      return false;
    }
    
    /**
     * Determines if {@link TextPosition this} encompasses the given values.
     * 
     * @param startLine The start line
     * @param startOffset The start offset
     * @param endLine The end line
     * @param endOffset The end offset
     * @return <tt>True</tt> {@link TextPosition this} encompassed the given values.
     */
    public boolean isEncompassing(int startLine, int startOffset, int endLine, int endOffset) {
      // check the lines
      if (getStartLine() <= startLine && getEndLine() >= endLine) {
        boolean startOffsetGood = true;
        boolean endOffsetGood = true;
        
        // if on the same start line, the offsets need to be checked
        if (getStartLine() == startLine) {
          startOffsetGood = getStartOffset() <= startOffset;
        }
        
        // if on the same end line, the offsets need to be checked
        if (getEndLine() == endLine) {
          endOffsetGood = getEndOffset() >= endOffset;
        }
        if (startOffsetGood && endOffsetGood) {
          return true;
        }
      }
      return false;
    }
    
    @Override
    public String toString() {
      return "start: " + startLine + ":" + startOffset + " -> end: " + endLine + ":" + endOffset;
    }
  }
}
