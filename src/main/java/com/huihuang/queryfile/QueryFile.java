package com.huihuang.queryfile;

import com.huihuang.queryfile.viwe.SwingConsole;
import com.huihuang.queryfile.viwe.TextArea;

/**
 * *主函数入口，启动类
 * @author Raiden
 *
 */
public class QueryFile {

	public static void main(String[] args) {
		SwingConsole.run(new TextArea(), 520, 600);
	}
}
