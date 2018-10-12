package com.neptunedreams.camera;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dialog;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Iterator;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * <br>Created by IntelliJ IDEA.
 * <br>Date: 6/17/13
 * <br>Time: 11:27 PM
 *
 * @author Miguel Mu\u00f1oz
 */
public class CameraFilter6 extends JPanel implements FilterDestination {
	private static final String LOAD_DIR = "loadDir";
	private static final String LOAD_DIR_NATIVE = "loadDirNative";
	private static final String PREVIOUS_FILE = "previousFile";
	private static final char DOT = '.';
	private Preferences preferences = Preferences.userNodeForPackage(CameraFilter6.class);
	private JPanel colorView;
	private JPanel grayView;
	
	private String formatName = "";
	private String fileExtension = "";
	
//	private Image grayImage = null;
	private static JFrame mainFrame=null;
	private BufferedImage loadedImage=null;
	private final ScalingImage scalingImage=new ScalingImage(null);

	public static void main(String[] args) {
		mainFrame = new JFrame("Camera Filter");
		mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		mainFrame.add(new CameraFilter6());
		int size = FilterChooser.DIAMETER + (FilterChooser.MARGIN * 2);
		mainFrame.setLocation(size + FilterChooser.MARGIN, 0);
		mainFrame.setSize(size*4, size*4);
		mainFrame.setVisible(true);
	}
	
	private CameraFilter6() {
		super(new BorderLayout());

		JToolBar toolBar = new JToolBar();
		add(toolBar, BorderLayout.PAGE_START);
		toolBar.setFloatable(false);

		JButton openButton = new JButton("Open");
		toolBar.add(openButton);
		openButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doOpen();
			}
		});
		
		JButton nativeOpenButton = new JButton("Native Open");
		toolBar.add(nativeOpenButton);
		nativeOpenButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				doNativeOpen();
			}
		});
		
		JButton saveButton = new JButton("Save");
		toolBar.add(saveButton);
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				doSave();
			}
		});

		JButton prevFileButton = new JButton("Previous File");
		toolBar.add(prevFileButton);
		prevFileButton.addActionListener(e->doOpenPrevious());
		
		preferences.addPreferenceChangeListener(evt -> {
			// I really should change the preference back to empty if the file fails to load. 
			// For now, I'm not going to bother.
			if (evt.getKey().equals(PREVIOUS_FILE)) {
				String newValue = evt.getNewValue();
				prevFileButton.setEnabled (newValue != null && !newValue.isEmpty());
			}
		});
		
		JButton traceButton = new JButton("Trace");
		toolBar.add(traceButton);
		traceButton.addActionListener(evt-> doTrace());

		JLabel processorLabel = new JLabel(" Processors: " + Runtime.getRuntime().availableProcessors());
		toolBar.add(processorLabel);
		
		String prevFile = preferences.get(PREVIOUS_FILE, "");
		prevFileButton.setEnabled(!prevFile.isEmpty());

		add(createPhotoView(), BorderLayout.CENTER);
		final FilterChooser filterChooser = new FilterChooser();
		filterChooser.addFilterListener(new FilterChooser.FilterListener() {
			@Override
			public void filter(final Color color) {
				filterChooser.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				doFilter(color);
				filterChooser.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			}
		});

		JDialog filterFrame = new JDialog(mainFrame, Dialog.ModalityType.MODELESS);

		filterFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		filterFrame.setResizable(false);
		filterFrame.add(filterChooser);
		filterFrame.setFocusableWindowState(false);
		
		filterFrame.pack();
		filterFrame.setVisible(true);

		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		if (bean.isThreadContentionMonitoringSupported()) {
			bean.setThreadContentionMonitoringEnabled(true);
			Runnable runner = new Runnable() {
				@Override
				public void run() {
					while (true) {
						long[] deadlocked = bean.findDeadlockedThreads();
						if (deadlocked != null) {
							logDeadLocked(bean, deadlocked);
						}
						try {
							Thread.sleep(30000);
						} catch (InterruptedException pE) {
							Thread.currentThread().interrupt();
							break;
						}
					}
				}
			};
			Thread monitorThread = new Thread(runner, "Deadlock Watch");
			monitorThread.setDaemon(true);
			monitorThread.start();
		}
	}
	
//	private JPanel makeExtraFilterPanel() {
//		JPanel extraFilterPanel = new JPanel(new GridLayout(0, 1));
//		JButton button = new JButton("Desaturate");
//		button.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(final ActionEvent e) {
//				
//			}
//		});
//	}

	private static void logDeadLocked(ThreadMXBean bean, long[] ids) {
		for (long id: ids) {
			ThreadInfo info = bean.getThreadInfo(id);
			System.err.printf("%nBlocked Thread id %d name = %s%n", info.getThreadId(), info.getThreadName());
			LockInfo lockInfo = info.getLockInfo();
			System.err.printf("Lock of %s%n", lockInfo.getClassName());
			MonitorInfo[] monitorInfos = info.getLockedMonitors();
			for (MonitorInfo monitorInfo : monitorInfos) {
				System.err.printf("Locked Monitor of class %s, hash=%d%n", monitorInfo.getClassName(), monitorInfo.getIdentityHashCode());
			}
			StackTraceElement[] trace = info.getStackTrace();
			for (StackTraceElement element : trace) {
				System.err.printf("\t%s%n", element);
			}
		}
		// todo: write me:
	}

	private void doFilter(final Color color) {
//		SwingWorker<>
			ImageFilter filter = new RgbToGrayImageFilter(color);
			applyFilter(filter);

	}

	public void applyFilter(final ImageFilter filter) {
		if (loadedImage != null) {
			// For single-threaded filtering, use this line:
//  		FilteredImageSource imageSource = new FilteredImageSource(loadedImage.getSource(), filter);

		// For parallel filtering, use these two lines:
		FilteredImageSource imageSource = new FilteredImageSource(new ParallelProducer(loadedImage), filter);
		imageSource.addConsumer(filter);

		Image image = grayView.createImage(imageSource);
		scalingImage.setImage(image);
		Toolkit.getDefaultToolkit().beep();
		} else {
			JOptionPane.showMessageDialog(this, "No image open.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	void doNonColorFilter(AbstractFilter filter) {
		if (loadedImage != null) {
			applyFilter(filter);
		}
	}

	private void doSave() {
		String loadDirectory = preferences.get(LOAD_DIR, System.getProperty("user.home"));
		String saveDirectory = preferences.get("saveDir", loadDirectory);
		JFileChooser fileChooser = new JFileChooser(saveDirectory);
		int success = fileChooser.showSaveDialog(this);
		if (success == JFileChooser.APPROVE_OPTION) {
			File saveFile = fileChooser.getSelectedFile();
			String name = saveFile.getName();
			String extension = getExtension(name);
			if (extension.isEmpty()) {
				name = name + fileExtension;
				fileChooser.setName(name);
			}
			try {
				Image image = scalingImage.getImage();
//				ToolkitImage toolkitImage = (ToolkitImage) scalingImage.getImage();
				BufferedImage grayImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
				Graphics g2 = grayImage.getGraphics();
				g2.drawImage(image, 0, 0, null);
				g2.dispose();
//				BufferedImage writableImage = new BufferedImage(grayImage.getWidth(this), grayImage.getHeight(this), BufferedImage.TYPE_BYTE_GRAY);
				Graphics g = grayImage.getGraphics();
				g.drawImage(grayImage, 0, 0, this);
				ImageIO.write(grayImage, formatName, saveFile);
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, e.getMessage(), "Error: " + e.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
			} catch (Throwable t) {
				t.printStackTrace();
				JOptionPane.showMessageDialog(this, t.getMessage(), "Error: " + t.getClass().getSimpleName(), JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private Component createPhotoView() {
		colorView = new JPanel(new BorderLayout());
		JPanel photoView = new JPanel(new GridLayout(0, 2));
		photoView.add(colorView);
		grayView = new JPanel(new BorderLayout());
		grayView.add(scalingImage);
		photoView.add(grayView);
		return photoView;
	}

	private void doOpen() {
		String lastDirectory = preferences.get(LOAD_DIR, System.getProperty("user.home"));
		JFileChooser fileChooser = new JFileChooser(lastDirectory);
		fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", ImageIO.getReaderFileSuffixes()));
		int success = fileChooser.showOpenDialog(this);
		if (success == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			preferences.put(LOAD_DIR, selectedFile.getParent());
			
			openFile(selectedFile);
		}
	}
	
	private void doNativeOpen() {
		String lastDirectory = preferences.get(LOAD_DIR, System.getProperty("user.home"));
		lastDirectory = preferences.get(LOAD_DIR_NATIVE, lastDirectory);
		FileDialog fileChooser = new FileDialog(mainFrame, "Choose Image");
		fileChooser.setDirectory(lastDirectory);
		String[] rawExtensions = ImageIO.getReaderFileSuffixes();
		final String[] extensions = new String[rawExtensions.length];
		int index = 0;
		for (String ext: rawExtensions) {
			//noinspection StringConcatenationInLoop
			extensions[index++] = DOT + ext;
		}
		fileChooser.setFilenameFilter(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				String lowName = name.toLowerCase();
				for (String end: extensions) {
					if (lowName.endsWith(end)) {
						return true;
					}
				}
				return false;
			}
		});
		fileChooser.setVisible(true);
		String selectedFileName = fileChooser.getFile();
		if (selectedFileName != null) {
			String directory = fileChooser.getDirectory();
			File selectedFile = new File(directory, selectedFileName);
			openFile(selectedFile);
			preferences.put(LOAD_DIR_NATIVE, directory);
		}
	}
	
	private void doOpenPrevious() {
		String path = preferences.get(PREVIOUS_FILE, null);
		if (path != null) {
			fileExtension = getExtension(path);
			openFile(new File(path));
		}
	}
	
	private String getExtension(String path) {
		return path.substring(path.lastIndexOf('.'));
	}

	private void openFile(final File selectedFile) {
		try {
			formatName = getFormatName(selectedFile);
			BufferedImage image = ImageIO.read(selectedFile);
			loadedImage = image;
			colorView.removeAll();
			ScalingImage canvas = new ScalingImage(image);
			colorView.add(canvas);
			colorView.revalidate();
			preferences.put(PREVIOUS_FILE, selectedFile.getAbsolutePath());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, e.getMessage() + ": " + selectedFile.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void doTrace() {
		int count = Thread.activeCount();
		Thread[] all = new Thread[count * 2];
		int aCount = Thread.enumerate(all);
		for (int ii=0; ii<aCount; ++ii) {
			System.out.printf("%nThread id %d - %s%n", all[ii].getId(), all[ii].getName());
			StackTraceElement[] elements = all[ii].getStackTrace();
			for (StackTraceElement ste: elements) {
				System.out.printf("  %s%n", ste);
			}
		}
	}
	
	private String getFormatName(File imageFile) throws IOException {
		Iterator<ImageReader> itr;
		try (ImageInputStream iis = ImageIO.createImageInputStream(imageFile)) {
			// get all currently registered readers that recognize the image format
			itr = ImageIO.getImageReaders(iis);
		}
		if (!itr.hasNext()) {
			throw new IOException("No readers found!");
		}
		// get the first reader
		ImageReader reader = itr.next();
		return reader.getFormatName();

	}
	
}
