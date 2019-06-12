import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClienteFrame extends JFrame implements Runnable {
  static PrintStream os = null;
  JPanel div = new JPanel();
  String inputText;
  JLabel labelName = new JLabel("Sem nome");

  ClienteFrame() {
    super("Fast Words");
    div.add(labelName);
    add(div, BorderLayout.NORTH);
    pack();
    setSize(1000, 800);
    setLocationRelativeTo(null); // alinhar o JFrame ao centro
    setVisible(true);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          System.out.println(e.getKeyCode());
          inputText = "Codigo: " + e.getKeyCode() + " ";
          os.println(inputText);
          // inputText = new String("Henrique" + " ");
          // os.println(inputText);
        }
    });
  }

  public static void main(String[] args) {
    new Thread(new ClienteFrame()).start();
  }

  public void run() {
    Socket socket = null;
    Scanner is = null;

    try {
      socket = new Socket("200.145.219.215", 80);
      os = new PrintStream(socket.getOutputStream(), true);
      is = new Scanner(socket.getInputStream());
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host.");
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to host");
    }

    try {
      String inputLine;
      String name;
      name = JOptionPane.showInputDialog(null, "Qual o seu nome?", "Digite seu nome", JOptionPane.QUESTION_MESSAGE);
      os.println(name);
      labelName.setText(name);

      do {
        System.out.println((inputLine=is.nextLine())+"\n");
      } while (!inputLine.equals(""));

      os.close();
      is.close();
      socket.close();
    } catch (UnknownHostException e) {
      System.err.println("Trying to connect to unknown host: " + e);
    } catch (IOException e) {
      System.err.println("IOException:  " + e);
    }
  }
}