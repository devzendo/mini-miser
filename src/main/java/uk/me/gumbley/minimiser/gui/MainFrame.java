package uk.me.gumbley.minimiser.gui;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.apache.log4j.Logger;

import uk.me.gumbley.commoncode.exception.AppException;
import uk.me.gumbley.commoncode.gui.SwingWorker;
import uk.me.gumbley.commoncode.gui.ThreadCheckingRepaintManager;
import uk.me.gumbley.commoncode.string.StringUtils;
import uk.me.gumbley.minimiser.common.AppName;
import uk.me.gumbley.minimiser.version.Version;

public class MainFrame {
	private static final Logger myLogger = Logger.getLogger(MainFrame.class);

	private JFrame mainFrame;
	private ActionListener exitAL;

	public MainFrame(ArrayList<String> argList) throws AppException {
		super();
		// Process command line
		for (int i = 0; i < argList.size(); i++) {
			myLogger.debug("arg " + i + " = '" + argList.get(i) + "'");
		}
		ThreadCheckingRepaintManager.initialise();

		// Create new Window and exit handler
		createMainFrame();

		// Menu
        mainFrame.add(createMenu(), BorderLayout.NORTH);

        mainFrame.pack();
        mainFrame.setVisible(true);
	}

	private void createMainFrame() {
		mainFrame = new JFrame(AppName.getAppName() + " v"
				+ Version.getVersion());
		mainFrame.setLayout(new BorderLayout());
		exitAL = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int opt = JOptionPane.showConfirmDialog(mainFrame,
						"Are you sure you want to exit?", "Confirm exit",
						JOptionPane.YES_NO_OPTION);
				// myLogger.info("option returned is " + opt);
				if (opt == 0) {
					SwingWorker worker = new SwingWorker() {
						public Object construct() {
							myLogger.info("Shutting down...");
							try {
								SwingUtilities.invokeAndWait(new Runnable() {
									public void run() {
										mainFrame.setCursor(new Cursor(
												Cursor.WAIT_CURSOR));
										enableDisableControls(false);
									}
								});
							} catch (InterruptedException e) {
							} catch (InvocationTargetException e) {
							}
							shutdown();
							try {
								SwingUtilities.invokeAndWait(new Runnable() {
									public void run() {
										mainFrame.setCursor(new Cursor(
												Cursor.DEFAULT_CURSOR));
									}
								});
							} catch (InterruptedException e) {
							} catch (InvocationTargetException e) {
							}
							return null;
						}
					};
					worker.start();
				}
			}
		};
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mainFrame.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
				exitAL.actionPerformed(null);
			}

			public void windowClosed(WindowEvent e) {
			}

			public void windowIconified(WindowEvent e) {
			}

			public void windowDeiconified(WindowEvent e) {
			}

			public void windowActivated(WindowEvent e) {
			}

			public void windowDeactivated(WindowEvent e) {
			}
		});
	}

	private JMenuBar createMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        //
        
        //
        JMenuItem fileExit = new JMenuItem("Exit");
        fileExit.setMnemonic('x');
        fileExit.addActionListener(exitAL);
        fileMenu.add(fileExit);
        //
        // ---
        //
        JMenu helpMenu = new JMenu("Help");
        //
        JMenuItem helpManual = new JMenuItem("Help contents");
        helpMenu.add(helpManual);
        //
        
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }


	private void shutdown() {
		long start = System.currentTimeMillis();
		long stop = System.currentTimeMillis();
		long dur = stop - start;
		myLogger.info("Shutdown took " + StringUtils.translateTimeDuration(dur));
		// give time for the above to be logged
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
		}
		if (mainFrame != null) {
			mainFrame.dispose();
		}
		System.exit(0);
	}

	private void enableDisableControls(boolean enable) {
	}
}
