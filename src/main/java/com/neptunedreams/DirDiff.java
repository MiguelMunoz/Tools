package com.neptunedreams;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.*;
import com.ObservableReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class identifies files that have changed when comparing two directory trees. It include support to identify
 * changes to ignore.
 * <p>
 * This is a very narrow focused utility. It was written to examine the effects that swagger's code-gen options have
 * in the generated code. This can only be done by generating the code with the option on and off, and comparing the
 * results. This utility quickly identifies which generated files have changed between the two generations.
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 11/21/18
 * <p>Time: 1:34 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings({"HardCodedStringLiteral", "UseOfSystemOutOrSystemErr"})
public final class DirDiff extends JPanel {

	private static final char COMMA = ',';
	private static final FileSystem FILE_SYSTEM = FileSystems.getDefault();
	private static final char NEW_LINE = '\n';
	private static JFrame frame;
	private final JTextArea textArea = new JTextArea(20, 25);

	public static void main(String[] args) {
		frame = new JFrame("Directory Difference");
		DirDiff dirDiff = new DirDiff();
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setLocationByPlatform(true);
		frame.add(dirDiff);
		frame.pack();
		frame.setVisible(true);
	}
	
	private static final String SEARCH_DIR = "searchDir";
	private static final String IGNORE_LIST = "ignoreList";
	private Preferences preferences = Preferences.userNodeForPackage(getClass());
	
//	private @Nullable Path dir1Path;
//	private @Nullable Path dir2Path;
	private final ObservableReference<Path> path1Ref = new ObservableReference<>(null);
	private final ObservableReference<Path> path2Ref = new ObservableReference<>(null);
	
	private JList<String> ignoreJList = new JList<>(new DefaultListModel<>());
	
	private DirDiff() {
		super(new GridBagLayout());
		addRow("Directory 1", this::setPath1);
		addRow("Directory 2", this::setPath2);
		
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		constraints.insets = new Insets(0, 4, 0, 4);
		add(new JLabel("Ignore:"), constraints);
		constraints.gridx = 1;
		add(wrap(ignoreJList), constraints);
		JButton addButton = new JButton("Add");
		addButton.addActionListener((e) -> doAddIgnoreString());
		add(addButton, constraints);
		
		JButton removeButton = new JButton("Remove");
		removeButton.setEnabled(false);
		removeButton.addActionListener((e) -> doRemove());
		add(removeButton, constraints);

		List<String> ignoreList = cdsToList(preferences.get(IGNORE_LIST, ""));
		for (String s: ignoreList) {
			getIgnoreModel().addElement(s);
		}
		
		ignoreJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ignoreJList.getSelectionModel().addListSelectionListener((e) 
				-> removeButton.setEnabled(ignoreJList.getSelectedIndex() >= 0));
		
		JButton showDiff = new JButton("Show Differences");
		showDiff.setEnabled(false);
		constraints.gridx = 3;
		constraints.gridy = 0;
		add(showDiff, constraints);
		showDiff.addActionListener((e) -> showDifferences());

		Consumer<Path> diffStatus = (p) -> showDiff.setEnabled(!path1Ref.isNull() && !path2Ref.isNull());
		path1Ref.addListener(diffStatus);
		path2Ref.addListener(diffStatus);

		constraints.gridheight = 4;
		constraints.gridx = 3;
		constraints.gridy = 1;
		JScrollPane scrollPane = new JScrollPane(
				textArea, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS
		);
		add(scrollPane, constraints);
	}

	private void showDifferences() {
		StringBuilder builder = new StringBuilder();
		Path dir1 = path1Ref.getValidObject();
		Path dir2 = path2Ref.getValidObject();

		try {
			findNonMatches(dir1, dir2, builder);
		} catch (IOException e) {
			e.printStackTrace();
		}
		textArea.setText(builder.toString());
	}
	
	private void findNonMatches(Path dir1, Path dir2, StringBuilder builder) throws IOException {
		File[] files1 = dir1.toFile().listFiles();
		
		// the listFiles() method should really throw an exception for an invalid file, rather than returning null!
		if (files1 == null) {
			throw new IllegalStateException(dir1.toString());
		}
		List<String> ignoredStringList = extractList(ignoreJList);
		for (File f1 : files1) {
			Path path1 = f1.toPath();
			Path path2 = dir2.resolve(dir1.relativize(path1));
			if (f1.isDirectory()) {
				findNonMatches(path1, path2, builder);
			} else if (!path1.getFileName().toString().startsWith(".")) {
				if (path2.toFile().exists()) {
					if (!match(Files.newBufferedReader(path1), Files.newBufferedReader(path2), ignoredStringList)) {
						builder.append(dir1.relativize(path1)).append(NEW_LINE);
					}
				}
			}
		}
	}
	
	private boolean match(BufferedReader br1, BufferedReader br2, final List<String> ignoredText) throws IOException {
		String line1 = br1.readLine();
		String line2 = br2.readLine();

		while ((line1 != null) && (line2 != null)) {
			if (!linesMatch(line1, line2)) {
				return false;
			}
			
			// Now that we've compared the two lines, 
			line1 = br1.readLine();
			while ((line1 != null) && ignoredText.stream().anyMatch(line1::contains)) {
				line1 = br1.readLine();
			}

			line2 = br2.readLine();
			while (((line2 != null) && ignoredText.stream().anyMatch(line2::contains))) {
				line2 = br2.readLine();
			}
		}
		return true;
	}
	
	private boolean linesMatch(@NotNull String line1, @NotNull String line2) {
		//noinspection EqualsReplaceableByObjectsCall
		return line1.equals(line2);
	}

	private JScrollPane wrap(JList<?> list) {
		list.setVisibleRowCount(10);
		return new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
	
//	private GridBagConstraints 

	private void doAddIgnoreString() {
		String ignoreData = JOptionPane.showInputDialog(this, "Ignore String:");
		if (!ignoreData.isEmpty()) {
			getIgnoreModel().addElement(ignoreData);
			List<String> ignoreList = extractList(ignoreJList);
			savePreference(IGNORE_LIST, listToCds(ignoreList));
		}
	}
	
	private void savePreference(String key, String value) {
		preferences.put(key, value);
		try {
			preferences.flush();
		} catch (BackingStoreException e) {
			e.printStackTrace();
		}
	}

	private void doRemove() {
		getIgnoreModel().remove(ignoreJList.getSelectedIndex());
		List<String> ignoreList = extractList(ignoreJList);
		savePreference(IGNORE_LIST, listToCds(ignoreList));
	}

	private DefaultListModel<String> getIgnoreModel() {
		return (DefaultListModel<String>) ignoreJList.getModel();
	}
	
	private List<String> cdsToList(String listString) {
		List<String> list = new LinkedList<>();
		StringTokenizer tokenizer = new StringTokenizer(listString, ",");
		while (tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken());
		}
		return list;
	}
	
	private String listToCds(List<String> list) {
		if (list.isEmpty()) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		Iterator<String> iterator = list.iterator();
		builder.append(iterator.next());
		while (iterator.hasNext()) {
			builder.append(COMMA).append(iterator.next());
		}
		return builder.toString();
	}
	
	private List<String> extractList(JList<String> list) {
		List<String> strings = new LinkedList<>();
		ListModel<String> model = list.getModel();
		for (int ii = 0; ii<model.getSize(); ++ii) {
			strings.add(model.getElementAt(ii));
		}
		return strings;
	}
	
	private void addRow(String labelText, Consumer<Path> setter) {
		final JLabel label = new JLabel(labelText);

		GridBagConstraints constraints = new GridBagConstraints();
		constraints.gridx = 0;
		constraints.anchor = GridBagConstraints.FIRST_LINE_START;
		JButton button = new JButton(labelText);
		add(button, constraints);
		button.addActionListener((e) -> setPath(label, setter));

		constraints.gridx = 1;
		add(label, constraints);
	}

	private void setPath(JLabel label, Consumer<Path> setter) {
		Path path = applyPath(label);
		setter.accept(path);
	}

	private @Nullable Path applyPath(JLabel label) {
		@Nullable Path path = getPath();
		if (path != null) {
			label.setText(path.getFileName().toString());
			frame.pack();
		}
		return path;
	} 
	
	@Nullable
	private Path getPath() {
		JFileChooser fileChooser = new JFileChooser(preferences.get(SEARCH_DIR, System.getProperty("user.home")));
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int result = fileChooser.showOpenDialog(this);
		if (result == JFileChooser.APPROVE_OPTION) {
			File chosenFile = fileChooser.getSelectedFile();
			final String filePath = chosenFile.getAbsolutePath();
			final Path path = FILE_SYSTEM.getPath(filePath);
			savePreference(SEARCH_DIR, path.getParent().toString());
			return path;
		}
		return null;
	}

	private void setPath1(Path path) {
		path1Ref.setObject(path);
	}

	private void setPath2(Path path) {
		path2Ref.setObject(path);
	}
}
