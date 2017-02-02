
package com.livescore;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LiveScoreUpdates {

	private List<String> liveScore = new ArrayList<String>();
	private String header;
	private String message;
	private int hours;
	private int minutes;

	@SuppressWarnings("deprecation")
	private void createUI() {

		Calendar cal = Calendar.getInstance();
		hours = cal.getTime().getHours();
		minutes = cal.getTime().getMinutes();

		String timeStamp;
		if (minutes < 10)
			timeStamp = hours + ":" + "0" + minutes;
		else
			timeStamp = hours + ":" + minutes;

		header = "Live score updates from CricInfo";

		final JFrame frame = new JFrame("Live score updates");
		frame.setSize(420, 120);
		frame.setUndecorated(true);

		Image img = Toolkit.getDefaultToolkit().getImage("E:\\Logo.png");
		frame.setIconImage(img);

		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();// size
																		// of
																		// the
																		// screen
		Insets toolHeight = Toolkit.getDefaultToolkit().getScreenInsets(frame.getGraphicsConfiguration());// height
																											// of
																											// the
																											// task
																											// bar
		frame.setLocation(scrSize.width - frame.getWidth(), scrSize.height - toolHeight.bottom - frame.getHeight());

		final JLabel headingLabel = new JLabel(header);
		headingLabel.setOpaque(false);
		headingLabel.setSize(300, 30);
		headingLabel.setLocation(5, 5);

		final JLabel timeLabel = new JLabel("Time : " + timeStamp);
		timeLabel.setOpaque(false);
		timeLabel.setSize(300, 30);
		timeLabel.setLocation(5, 25);

		final JLabel messageLabel = new JLabel(message);
		messageLabel.setOpaque(false);
		messageLabel.setSize(400, 10);
		messageLabel.setLocation(10, 50);

		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new CloseListener());
		closeButton.setSize(70, 30);
		closeButton.setLocation(320, 10);
		closeButton.setForeground(Color.BLACK);

		final JTextArea textAreal = new JTextArea(message);
		textAreal.setPreferredSize(new Dimension(340, 120));
		textAreal.setLineWrap(true);
		textAreal.setLocation(5, 50);
		textAreal.setOpaque(false);

		frame.add(closeButton);
		frame.add(headingLabel);
		frame.add(timeLabel);
		frame.add(textAreal);

		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);

		Timer timer = new Timer();
		TimerTask repeatUpdate = new TimerTask() {
			@Override
			public void run() {
				try {
					updateScore();
					Calendar cal = Calendar.getInstance();
					hours = cal.getTime().getHours();
					minutes = cal.getTime().getMinutes();

					String timeStamp;
					if (minutes < 10)
						timeStamp = hours + ":" + "0" + minutes;
					else
						timeStamp = hours + ":" + minutes;

					timeLabel.setText("Time : " + timeStamp);
					headingLabel.setText(header);
					textAreal.setText(message);

				} catch (IOException e) {
				} catch (Exception e) {
				}
			}
		};
		timer.schedule(repeatUpdate, 0l, 5000 * 2);

	}

	private void updateScore() throws IOException, Exception {

		URL url = new URL("http://static.cricinfo.com/rss/livescores.xml");
		HttpURLConnection request1 = (HttpURLConnection) url.openConnection();
		request1.setRequestMethod("GET");
		request1.connect();
		InputStream is = request1.getInputStream();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder;
		dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(is);
		doc.getDocumentElement().normalize();

		XPath xPath = XPathFactory.newInstance().newXPath();
		String expression = "//channel/item";
		NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(doc, XPathConstants.NODESET);

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node nNode = nodeList.item(i);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) nNode;
				liveScore.add(eElement.getElementsByTagName("title").item(0).getTextContent());
			}
		}

		message = "\n\n\n";

		if (liveScore.size() <= 3) {
			for (int n = 0; n < liveScore.size(); n++) {
				message = message + "\n  " + liveScore.get(n);
			}
		} else {
			for (int n = 0; n < 3; n++) {
				message = message + "\n  " + liveScore.get(n);
			}
		}
	}

	public static void main(String[] args) throws Exception {

		LiveScoreUpdates app = new LiveScoreUpdates();
		app.updateScore();
		app.createUI();
	}
}

class CloseListener implements ActionListener {
	@Override
	public void actionPerformed(ActionEvent e) {
		System.exit(0);
	}
}
