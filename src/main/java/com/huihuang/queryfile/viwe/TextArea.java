package com.huihuang.queryfile.viwe;

import com.huihuang.queryfile.controller.Controller;
import com.huihuang.queryfile.Utils.StringUtils;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class TextArea extends JFrame{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2568996309431093667L;
	
	private static final String PATH_TEXT = "请填写文件所在路径";
	private static final String END_FILE_NAME_TEXT = "请填写文件后缀";
	private static final String CONTENT_TEXT = "请填写要查询内容";
	private static final String END = "!";
	
	private JButton b1 = new JButton("Query");
	private JTextArea t = new JTextArea(20,40);
	private JTextArea pathText = new JTextArea(1,38);
	private JTextArea endFileNameText = new JTextArea(1,38);
	private JTextArea contentText = new JTextArea(1,38);
	private Controller controller = new Controller(t);


	public TextArea() {
		controller.start();
		pathText.setText(PATH_TEXT + END);
		endFileNameText.setText(END_FILE_NAME_TEXT + END);
		contentText.setText(CONTENT_TEXT + END);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				t.setText(StringUtils.EMPTY);
				controller.add(pathText.getText(), endFileNameText.getText(), contentText.getText());
			}
		});
		setLayout(new FlowLayout());
		add(new JScrollPane(t));
		add(new JScrollPane(pathText));
		add(new JScrollPane(endFileNameText));
		add(new JScrollPane(contentText));
		add(b1);
	}
}
