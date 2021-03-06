package com.neptunedreams.gui.link;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.prefs.Preferences;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import org.jetbrains.annotations.NotNull;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 3/27/17
 * <p>Time: 1:29 AM
 * <p>
 * Done: 1) Show weight of headline field
 * Done: 2) Change button name on selection
 * Done: 3) Copy text on click
 * Done: 4) Add support for changing headline size
 * skip: 5) Preserve text for next use (What was I thinking?)
 * Done: 6) Add an accelerator for the "Create Link" button
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"HardCodedStringLiteral", "MagicNumber", "MagicCharacter"})
public final class LinkBuilder extends JPanel {

  private static final String CREATE_LINK_AND_COPY = "Create Link and Copy";
  private static final String SELECT_TEXT_TO_LINK = "Select Text to Link";
  private final int r = 10;
  private final int c = 45;
  @NotNull
  private JTextArea textView = new JTextArea(r, c);
  @NotNull
  private JTextArea linkView = new JTextArea(r, c);
  @NotNull
  private JTextArea htmlView = new JTextArea(r, c);
  @NotNull
  private JLabel instructions = new JLabel(SELECT_TEXT_TO_LINK);
  
  @NotNull
  private JTextArea cleanUpInputView = new JTextArea(r, c);
  @NotNull
  private JTextArea cleanUpOutputView = new JTextArea(r, c);

  private JButton copyButton;
  @NotNull
  private final SpinnerNumberModel model = new SpinnerNumberModel(1, 0, 4, 1);
  @NotNull
  private final JSpinner spinner = new JSpinner(model);
  @NotNull
  private final JTextField headlineField = new JTextField();
  private Action linkAction;
  private JButton addLinkButton;

  public static void main(String[] args) {
    JFrame frame = new JFrame("Link Builder");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.add(new LinkBuilder(frame));
    frame.pack();
    frame.setLocationByPlatform(true);
    frame.setVisible(true);
  }

  private LinkBuilder(@NotNull JFrame frame) {
    super(new BorderLayout());
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Link", makeMainTab());
    Font plainFont = this.getFont(Font.PLAIN, 12, "Verdana", "Arial", "Helvetica", "sans-serif");
    tabbedPane.addTab("Clean Up", makeCleanupTab());
    tabbedPane.addTab("Tags", new Tags());
    add(tabbedPane, BorderLayout.CENTER);
    textView.setFont(plainFont);
    linkView.setFont(plainFont);
    htmlView.setFont(plainFont);
    cleanUpInputView.setFont(plainFont);
    cleanUpOutputView.setFont(plainFont);
    JMenuBar menuBar = new JMenuBar();
    JMenu menu = new JMenu("Edit");
    menu.setMnemonic('e');
    JMenuItem item = new JMenuItem(linkAction);
    menu.add(item);
    menuBar.add(menu);
    frame.setJMenuBar(menuBar);
  }
  
  private JPanel makeMainTab() {
    JPanel mainTab = new JPanel(new BorderLayout());
	  mainTab.add(makeHeadlineView(), BorderLayout.PAGE_START);
	  mainTab.add(makeCenterView(), BorderLayout.CENTER);
	  mainTab.add(makeButtonPanel(), BorderLayout.PAGE_END);
	  return mainTab;
  }

  /**
   * The Cleanup tab replaces blank lines with break tags.
   * @return The inner cleanup tab, with input view and output view
   */
  private JPanel makeInnerCleanupTab() {
    JPanel cleanUpTab = new JPanel(new GridLayout(0, 1));
    cleanUpTab.add(scroll(cleanUpInputView));
    cleanUpTab.add(scroll(cleanUpOutputView));
    return cleanUpTab;
  }
  
  private JPanel makeCleanupTab() {
  	JPanel cleanUpTab = new JPanel(new BorderLayout());
  	cleanUpTab.add(makeInnerCleanupTab(), BorderLayout.CENTER);
  	cleanUpTab.add(makeCleanUpButtonPanel(), BorderLayout.PAGE_END);
  	return cleanUpTab;
  }

	private JPanel makeCleanUpButtonPanel() {
  	JPanel buttonPanel = new JPanel(new BorderLayout());
  	JButton doCleanUp = new JButton("Clean-up and Copy");
  	buttonPanel.add(BorderLayout.LINE_END, doCleanUp);
  	doCleanUp.addActionListener(this::cleanUpText);
  	return buttonPanel;
	}
	
	private void cleanUpText(ActionEvent ignored) {
		String text = cleanUpInputView.getText();
		final String finalText = cleanText(text);
		cleanUpOutputView.setText(finalText);

		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection stringSelection = new StringSelection(finalText);
		clipboard.setContents(stringSelection, stringSelection);
	}

	@SuppressWarnings({"HardcodedFileSeparator", "HardcodedLineSeparator"})
	private String cleanText(String text) {
		text = text.replace("\t", " ");
		text = replaceAll(text, "\n ", "\n");
		text = replaceAll(text, " \n", "\n");
		text = replaceAll(text, "\n\n", "\n");
		text = replaceAll(text, "<br>\n", "<br>");
		text = replaceAll(text, "\n<br>", "<br>");
		text = replaceAll(text, "\n", "<br><br>");
		return text;
	}

	/**
	 * I tried saying text = text.replace("\\s$", "$"); to replace space-eol with eol, but
	 * it didn't work. So I wrote this inefficient, non-regex version, which works fine.
	 * <p>
	 * So calling {@code replaceAll("banana", "na", "l")} will return {@code "ball"}
	 * @param s The string to search
	 * @param search The text to replace
	 * @param replacement The replacement text.
	 * @return A String with the replacements made.
	 */
	private String replaceAll(String s, String search, String replacement) {
  	var index = s.indexOf(search);
  	if (index >= 0) {
  		StringBuilder builder = new StringBuilder(s);
		  final int length = search.length();
		  final int sLen = search.length();
		  while (index >= 0) {
			  int end = index + length;
			  builder.delete(index, end);
			  builder.insert(index, replacement);
			  
			  // Here, we subtract sLen to handle the case of replaceAll("xaaaabc", "ab", "b");
			  index = builder.indexOf(search, Math.max(0, (index - sLen) + length));
		  }
		  return builder.toString();
	  }
	  return s;
	}

	@NotNull
  private JComponent makeHeadlineView() {
    JPanel view = new JPanel(new BorderLayout());
    view.add(headlineField, BorderLayout.CENTER);
    view.add(spinner, BorderLayout.LINE_END);
    model.addChangeListener(e -> {
      final Number value = (Number) model.getValue();
      assert value != null;
      adjustHeadlineFont(value.intValue());
    });
    adjustHeadlineFont(1);
    return view;
  }

  private void adjustHeadlineFont(final int i) {
    int size = new int[]{12, 15, 17, 22, 26}[i];
    Font font = getFont(Font.BOLD, size, "Verdana", "Arial", "Helvetica", "sans-serif");
    headlineField.setFont(font);
  }

  private Font getFont(int style, int size, @NotNull String... names) {
    for (String name : names) {
      Font f = new Font(name, style, size);
//			System.out.printf("Comparing %s with %s...%n", f.getFontName(), name);
      final String fontName = f.getFontName();
      assert fontName != null;
      if (fontName.contains(name)) {
        return f;
      }
    }
    return new Font(null, style, size);
  }

  @NotNull
  private JComponent makeCenterView() {
    JPanel panel = new JPanel(new GridLayout(0, 1));
//		textView.setText("This is a noteworthy article, from a respectable journal.");
    panel.add(wrap(scroll(textView), "Body Text:"));
//		linkView.setText("http://www.newyorker.com/magazine/noteworthy-article");
    panel.add(wrap(scroll(linkView), "Link to:"));
    panel.add(wrap(scroll(htmlView), "Result"));

    textView.getCaret().addChangeListener(e -> {
      String selection = textView.getSelectedText();
      assert copyButton != null;
      final boolean enabled = selection != null;
      assert linkAction != null;
      linkAction.setEnabled(enabled);
      instructions.setVisible(!enabled);
//			linkAction.putValue(Action.NAME, enabled? CREATE_LINK_AND_COPY : SELECT_TEXT_TO_LINK);
//			button.setDisplayedMnemonicIndex(enabled? 'L' : -1);
    });
    
    htmlView.getCaret().addChangeListener(e -> {
      String selection = htmlView.getSelectedText();
      addLinkButton.setEnabled((selection != null) && !selection.isEmpty());
    });
    return panel;
  }

  private JComponent wrap(@NotNull JComponent component, String title) {
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(new JLabel(title), BorderLayout.PAGE_START);
    panel.add(component, BorderLayout.CENTER);
    return panel;
  }

  @NotNull
  private JPanel makeButtonPanel() {
    copyButton = new JButton();
    //noinspection CloneableClassWithoutClone
    linkAction = new AbstractAction() {
      @Override
      public void actionPerformed(final ActionEvent e) {
        createLink();
      }
    };

	  // Use this for pre java-10
//	  final KeyStroke ks = KeyStroke.getKeyStroke('L', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());

	  // Use this one for java 10+
	  //noinspection MagicConstant
	  final KeyStroke ks = KeyStroke.getKeyStroke('L', Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx());
	  linkAction.putValue(Action.ACCELERATOR_KEY, ks);
    linkAction.putValue(Action.NAME, CREATE_LINK_AND_COPY);
    linkAction.setEnabled(false);
//		button.setDisplayedMnemonicIndex(7);
//		button.addActionListener(e -> createLink());
    copyButton.setAction(linkAction);
//		button.setEnabled(false);

    addLinkButton = new JButton("Add Link");
    addLinkButton.setEnabled(false);
    addLinkButton.addActionListener(e -> insertExtraLink());
    
    JPanel buttonPanel = new JPanel(new BorderLayout());
    JPanel innerPanel = makeInnerButtonPanel(copyButton, addLinkButton);
    buttonPanel.add(innerPanel, BorderLayout.LINE_END);

    JButton clearButton = new JButton("Clear");
    buttonPanel.add(clearButton, BorderLayout.LINE_START);
    clearButton.addActionListener(e -> doClear());
    return buttonPanel;
  }

  @NotNull
  private JPanel makeInnerButtonPanel(@NotNull JButton copyLink, @NotNull JButton addLink) {
    JPanel innerPanel = new JPanel(new BorderLayout());
    innerPanel.add(instructions, BorderLayout.CENTER);
    innerPanel.add(copyLink, BorderLayout.LINE_END);
    innerPanel.add(addLink, BorderLayout.LINE_START);
    
    return innerPanel;
  }
  
  private void insertExtraLink() {
    String link = JOptionPane.showInputDialog(this, "Enter link");
    if (link != null) {
      int start = htmlView.getSelectionStart();
      int end = htmlView.getSelectionEnd();
      final String selectedText = htmlView.getSelectedText();
      final String currentText = htmlView.getText();
      String prefix = currentText.substring(0, start);
      String suffix = currentText.substring(end);
      final String htmlText = String.format("%s<a href=\"%s\">%s</a>%s", prefix, link, selectedText, suffix);
      htmlView.setText(htmlText);
      htmlView.setSelectionStart(start);
      htmlView.setSelectionEnd(htmlView.getText().length() - suffix.length());
      copyHtmlText(htmlText);
    }
  }

  private void doClear() {
    textView.setText("");
    linkView.setText("");
    htmlView.setText("");
    headlineField.setText("");
  }

  private void createLink() {
    int start = textView.getSelectionStart();
    int end = textView.getSelectionEnd();
    String cText = textView.getText();
    String text = String.format(
        "%s%s<a href=\"%s\">%s</a>%s",
        getHeadlineString(),
        cText.substring(0, start),
        linkView.getText(),
        cText.substring(start, end),
        cText.substring(end)
    );
    htmlView.setText(text);

    copyHtmlText(text);
  }

  private void copyHtmlText(final String text) {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringSelection stringSelection = new StringSelection(text);
    clipboard.setContents(stringSelection, stringSelection);
  }

  private String getHeadlineString() {
    final String text = headlineField.getText().trim();
    if (text.isEmpty()) {
      return "";
    }
    return String.format(
        "%s<b>%s</b>%s<br>",
        bigStart(),
        text,
        bigEnd()
    );
  }

  private String bigStart() {
    //noinspection ConstantConditions
    return repeat("<big>", ((Integer) model.getValue()));
  }

  private String bigEnd() {
    //noinspection ConstantConditions
    return repeat("</big>", ((Integer) model.getValue()));
  }

  @NotNull
  private String repeat(String txt, int multiple) {
    StringBuilder builder = new StringBuilder();
    for (int ii = 0; ii < multiple; ++ii) {
      builder.append(txt);
    }
    return builder.toString();
  }

  @NotNull
  private JScrollPane scroll(@NotNull JTextArea wrapped) {
    wrapped.setLineWrap(true);
    wrapped.setWrapStyleWord(true);
    return new JScrollPane(wrapped, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
  }
  
  private static class Tags extends JPanel {
    private static final String TAGS = "Tags";
    private final Preferences prefs = Preferences.userNodeForPackage(LinkBuilder.class);
    private final JTextField textField = new JTextField();
    private JPanel tagPanel = new JPanel(new GridLayout(0, 1));
    @SuppressWarnings("CloneableClassWithoutClone")
    private final Set<String> tags = new TreeSet<>() {
      @Override
      public boolean add(final String s) {
        final boolean added = super.add(s);
        prefs.put(TAGS, writeTags());
        return added;
      }

      @Override
      public boolean remove(final Object o) {
        final boolean removed = super.remove(o);
        prefs.put("Tags", writeTags());
        return removed;
      }
    };

    Tags() {
	    super(new BorderLayout());
	    readTags(prefs.get(TAGS, ""));
	    add(makeToolBar(), BorderLayout.PAGE_END);
	    add(textField, BorderLayout.PAGE_START);
	    JPanel spacingPanel = new JPanel(new BorderLayout());
	    spacingPanel.add(tagPanel, BorderLayout.PAGE_START);
	    add(spacingPanel, BorderLayout.CENTER);
	    for (String s: tags) {
	      tagPanel.add(new TagButton(s));
      }
    }
    
    private JToolBar makeToolBar() {
	    JToolBar toolBar = new JToolBar();
	    JButton addButton = new JButton("+");
	    addButton.addActionListener(e -> doAdd());
	    toolBar.add(addButton);
	    return toolBar;
    }
    
    private void doAdd() {
      String newTag = JOptionPane.showInputDialog(tagPanel, "Tag:", "").trim();
      if (!newTag.isEmpty()) {
        tags.add(newTag);
        tagPanel.add(new TagButton(newTag));
      }
    }
    
    private String writeTags() {
      StringBuilder builder = new StringBuilder();
      Iterator<String> itr = tags.iterator();
      if (itr.hasNext()) {
        builder.append(itr.next());
        while (itr.hasNext()) {
          builder.append('|').append(itr.next());
        }
      }
      return builder.toString();
    }
    
    private void readTags(String tagString) {
      tags.clear();
      StringTokenizer tk = new StringTokenizer(tagString, "|");
      while (tk.hasMoreTokens()) {
        tags.add(tk.nextToken());
      }
    }
    
    private void doTag(String tag) {
      int spaceSpot = tag.indexOf(' ');
      String result;
      final String content = textField.getText().trim();
      if (spaceSpot < 0) {
        result = String.format("<%s>%s</%s>", tag, content, tag);
      } else {
        String name = tag.substring(0, spaceSpot);
        String attributes = tag.substring(++spaceSpot);
        result = String.format("<%s %s>%s</%s>", name, attributes, content, name);
      }
      StringSelection stringSelection = new StringSelection(result);
      Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
      clipboard.setContents(stringSelection, stringSelection);
    }

    private class TagButton extends JButton {
      private final String tagValue;
      TagButton(String value) {
        super(value);
        tagValue = value.trim();
        addActionListener(e -> doTag(tagValue));
      }
    }
  }
}
