package com.huihuang.queryfile;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class SwingConsole {

	public static void run(final JFrame f,final int width,final int height) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				f.setTitle("Query");
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				f.setSize(width, height);
				f.setVisible(true);
				f.setResizable(false);
			}
		});
	}
}
