package com.gzw.kd.learn.code;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Toolkit;
import org.springframework.core.io.support.SpringFactoriesLoader;

/**
 * @author gzw
 * @description： hello
 * @since：2024/6/14 16:25
 */
public class HelloWorld {




        public static void main(String[] args) {
            // 创建一个新的JFrame窗口
            JFrame frame = new JFrame("Hello World");

            // 创建一个JLabel来显示文本
            JLabel label = new JLabel("Hello World");

            // 创建一个面板并添加标签
            JPanel panel = new JPanel();
            panel.add(label);

            // 设置窗口的属性
            frame.setContentPane(panel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack(); // 自动调整窗口大小以适合其内容

            // 居中显示窗口
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Dimension frameSize = frame.getSize();
            if (frameSize.height > screenSize.height) {
                frameSize.height = screenSize.height;
            }
            if (frameSize.width > screenSize.width) {
                frameSize.width = screenSize.width;
            }
            frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);

            // 显示窗口
            frame.setVisible(true);
        }

}
