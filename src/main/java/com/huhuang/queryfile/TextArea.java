package com.huhuang.queryfile;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;

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
	
	private JButton b1 = new JButton("Query");
	private JTextArea t = new JTextArea(20,40);
	private JTextArea pathText = new JTextArea(1,38);
	private JTextArea endFileNameText = new JTextArea(1,38);
	private JTextArea contentText = new JTextArea(1,38);
	public TextArea() {
		pathText.setText(PATH_TEXT);
		endFileNameText.setText(END_FILE_NAME_TEXT);
		contentText.setText(CONTENT_TEXT);
		b1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				List<String> fileNames = getFileNames(pathText.getText(), endFileNameText.getText(), contentText.getText());
				for (String fileName : fileNames) {
					t.append(fileName + "\n");
				}
			}
		});
		setLayout(new FlowLayout());
		add(new JScrollPane(t));
		add(new JScrollPane(pathText));
		add(new JScrollPane(endFileNameText));
		add(new JScrollPane(contentText));
		add(b1);
	}
	
	private List<String> getFileNames(String path,String fileType,String content){
		if (END_FILE_NAME_TEXT.equals(fileType) && CONTENT_TEXT.equals(content)) {
			return Collections.emptyList();
		}
		QueryFileProcessor processor = new QueryFileProcessor();
		if (PATH_TEXT.equals(path)) {
			path = null;
		}
		return processor.queryFile(path,"." + fileType, content);
	}
}
