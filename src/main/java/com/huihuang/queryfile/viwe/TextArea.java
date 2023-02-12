package com.huihuang.queryfile.viwe;

import com.huihuang.queryfile.common.FileType;
import com.huihuang.queryfile.controller.Controller;
import com.huihuang.queryfile.utils.StringUtils;
import com.huihuang.queryfile.logs.LogHandler;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class TextArea extends JFrame{

	/**
	 * 序列化号
	 */
	private static final long serialVersionUID = 2568996309431093667L;

    /**
     * 查询按钮
     */
	private JButton query;
    /**
     *
     */
	private JButton obtain;
	private JTextArea context;
	private JTextArea pathText;
	private JComboBox<FileType> endFileNameText;
	private JTextArea contentText;
	private Controller controller;
	private LogHandler handler ;

	public TextArea() {
        pathText = new JTextArea(1,38);
        context = new JTextArea(20,45);
        // 设置JTextArea字体和颜色。
        context.setForeground(Color.blue);
        query = new JButton("搜索文件");
        obtain = new JButton("收集文件");
        contentText = new JTextArea(1,38);
        contentText = new JTextArea(1,38);
        controller = new Controller(context);
        handler = LogHandler.newInstance();
		controller.start();
		query.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				context.setText(StringUtils.EMPTY);
				final String content = contentText.getText();
				if (StringUtils.isBlank(content)) {
				    return;
                }
				controller.add(pathText.getText(), (FileType) endFileNameText.getSelectedItem(), content);
			}
		});
		obtain.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				controller.obtainFiles(pathText.getText(), contentText.getText());
			}
		});
		//禁止编辑
        context.setEnabled(false);
		setLayout(new FlowLayout(FlowLayout.LEFT));
        add(new JScrollPane(new JLabel("搜索到的文件名称:")));
		add(new JScrollPane(context));
		add(initPathText());
        add(initContentText());
        add(initJComboBox());
        add(initJButton());
		handler.start();
	}

	private JPanel initJComboBox() {
        JPanel panel = new JPanel();
        endFileNameText = new JComboBox(FileType.values());
        panel.add(new JScrollPane(new JLabel("请选择文件类型:")));
        panel.add(new JScrollPane(endFileNameText));
        panel.setSize(460, 20);
        return panel;
    }

    private JPanel initPathText() {
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(new JLabel("文件路径:")));
        panel.add(new JScrollPane(pathText));
        return panel;
    }

    private JPanel initContentText() {
        JPanel panel = new JPanel();
        panel.add(new JScrollPane(new JLabel("查询内容:")));
        panel.add(new JScrollPane(contentText));
        return panel;
    }

    private JPanel initJButton() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.add(query);
        panel.add(obtain);
        return panel;
    }
}
