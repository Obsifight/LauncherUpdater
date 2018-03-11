package fr.thisismac.downloader;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

@SuppressWarnings("serial")
public class Core extends JFrame implements ActionListener {

	public static void main(String[] args) {
		new Core();
	}

	private String ONLINE_PATH = "https://obsifight.fr/downloads/ObsiFight" + getPlatform().getExt();

	private JLabel text = new JLabel("ObsiFight utilise un nouveau launcher", SwingConstants.CENTER);
	private JLabel text2 = new JLabel("Cliquez sur le bouton pour lancer l'installation", SwingConstants.CENTER);
	private JButton validate = new JButton("Installer");

	public Core() {
		setSize(250, 170);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setLocationRelativeTo(null);
		setTitle("ObsiFight");

		validate.addActionListener(this);
		validate.setBorder(new EmptyBorder(5, 5, 5, 5));

		text.setBorder(new EmptyBorder(5, 5, 5, 5));
		text2.setBorder(new EmptyBorder(5, 5, 5, 5));

		add("North", text);
		add("Center", text2);
		add("South", validate);

		pack();
		setVisible(true);
		
		System.out.println(ONLINE_PATH);
	}

	/**
	 * Start installation and handle some problem
	 * @param toDir
	 */
	public void install(File toDir) {
		File toFile = new File(toDir, "/ObsiFight" + getPlatform().getExt());
		if (toFile.exists()) {
			int callback = JOptionPane.showConfirmDialog(new Frame(),
					"Le launcher est déjà installé dans ce dossier. Voulez-vous quand même forcer l'installation ?",
					"ObsiFight", 1);

			if (callback == JOptionPane.NO_OPTION) {
				JOptionPane.showMessageDialog(new Frame(),
						"Je ObsiFight donc lancer le launcher présent, si vous retombez sur celui-ci, c'est qu'il vous faut forcer l'installation",
						"Obsifight", 1);
				try {
					Desktop.getDesktop().open(toFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			} else if (callback == JOptionPane.YES_OPTION) {
				toFile.delete();

				download(ONLINE_PATH, toFile);

				JOptionPane.showMessageDialog(new Frame(),
						"Je vais donc lancer le launcher présent, si cela ne marche pas, essayer de le lancer par vous même.",
						"ObsiFight", 1);
				try {
					Desktop.getDesktop().open(toFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		}
		else {
			download(ONLINE_PATH, toFile);

			JOptionPane.showMessageDialog(new Frame(),
					"Je vais donc lancer le launcher présent, si cela ne marche pas, essayer de le lancer par vous même.",
					"ObsiFight", 1);
			try {
				Desktop.getDesktop().open(toFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.exit(0);
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == validate) {
			JFileChooser fileChooser = new JFileChooser();

			fileChooser.setDialogTitle("Choissisez où le nouveau launcher sera télécharger.");
			fileChooser.setCurrentDirectory(new File(System.getProperty("user.home") + "/Desktop"));
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int callback = fileChooser.showOpenDialog(this);

			if (callback == JFileChooser.APPROVE_OPTION) {
				install(fileChooser.getSelectedFile());
			}
		}
	}

	/**
	 * Downloads a file from a online URL
	 * 
	 * @param site
	 *            The URL of the file you want to download.
	 * @param pathTo
	 *            The path where you want the file to be downloaded.
	 * 
	 * @return <b>true</b>If the download was a success. </b>false</b>If there
	 *         is an error during the download.
	 * 
	 * @throws IOException
	 *             InputOutputException.
	 * 
	 * @author Skyost (https://github.com/Skyost/Skyupdater)
	 */

	private final boolean download(final String site, final File pathTo) {
		try {
			final HttpURLConnection connection = (HttpURLConnection) new URL(site).openConnection();
			connection.setConnectTimeout(5000);
			String response = connection.getResponseCode() + " " + connection.getResponseMessage();
			System.out.println(response);
			if (!response.startsWith("2")) {
				JOptionPane.showMessageDialog(new Frame(),
						"Nous avons eu un petit de notre coté pour récupérer le nouveau launcher, essayez via obsifight.fr ou re-essayez dans quelques minutes.",
						"ObsiFight", 1);
				System.exit(0);
				return false;
			}
			final long size = connection.getContentLength();
			final long koSize = size / 1000;
			long lastPercent = 0;
			long percent = 0;
			float totalDataRead = 0;
			final InputStream inputStream = connection.getInputStream();
			final FileOutputStream fileOutputStream = new FileOutputStream(pathTo);
			final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 1024);
			final byte[] data = new byte[1024];
			int i = 0;
			while ((i = inputStream.read(data, 0, 1024)) >= 0) {
				totalDataRead += i;
				bufferedOutputStream.write(data, 0, i);
				percent = ((long) (totalDataRead * 100) / size);
				if (lastPercent != percent) {
					lastPercent = percent;
					System.out.println("[ObsiFight] " + percent + "% of " + koSize + "ko...");
				}

			}
			bufferedOutputStream.close();
			fileOutputStream.close();
			inputStream.close();
			return true;
		} catch (final Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(new Frame(),
					"Nous avons eu un petit de notre coté pour installer le nouveau launcher, essayez via hardfight.fr ou re-essayez dans quelques minutes.",
					"ObsiFight", 1);
			System.exit(0);
		}
		return false;
	}

	/**
	 * Get user's platform via {@link System}
	 * 
	 * @return {@link OS}
	 */
	public OS getPlatform() {
		final String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win"))
			return OS.WINDOWS;
		if (osName.contains("mac"))
			return OS.MACOS;
		if (osName.contains("linux"))
			return OS.LINUX;
		if (osName.contains("unix"))
			return OS.LINUX;
		return OS.UNKNOWN;
	}

	/**
	 * Enum platform and extention for it
	 * 
	 * @author ThisIsMac
	 */
	public enum OS {
		WINDOWS(".exe"), MACOS(".dmg"), SOLARIS(".jar"), LINUX(".jar"), UNKNOWN(".jar");

		private String ext;

		OS(String ext) {
			this.ext = ext;
		}

		public String getExt() {
			return ext;
		}
	}
}
