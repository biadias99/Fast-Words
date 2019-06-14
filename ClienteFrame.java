import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class ClienteFrame extends JFrame implements Runnable {
  static PrintStream os = null;
  JPanel topDiv = new JPanel(new BorderLayout());
  JPanel centerDiv = new JPanel();
  JPanel leftDiv = new JPanel(new GridLayout(2, 1));
  JPanel rightDiv = new JPanel(new GridLayout(2, 1));
  JLabel nameLabel = new JLabel("No name");
  JLabel enemyNameLabel = new JLabel("Enemy name");
  int selfLifes = 3;
  int enemyLifes = 3;
  JLabel selfLifesLabel = new JLabel("Remain lifes: " + selfLifes);
  JLabel enemyLifesLabel = new JLabel("Remain lifes: " + enemyLifes);
  int cliente = -1;
  int enemy = -1;
  int i = 2;
  String inputText;
  String canStart = "PODE COMECAR";

  ClienteFrame() {
    super("Fast Words");
    leftDiv.add(nameLabel);
    leftDiv.add(selfLifesLabel);
    rightDiv.add(enemyNameLabel);
    rightDiv.add(enemyLifesLabel);
    topDiv.setBackground(Color.green);
    leftDiv.setBackground(Color.green);
    rightDiv.setBackground(Color.green);
    topDiv.add(leftDiv, BorderLayout.WEST);
    topDiv.add(rightDiv, BorderLayout.EAST);
    centerDiv.setBackground(Color.blue);
    add(topDiv, BorderLayout.NORTH);
    add(centerDiv, BorderLayout.CENTER);
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
      socket = new Socket("192.168.42.81", 80);
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
      String enemyName;

      if(cliente < 0) {
        String clienteNumber = is.nextLine();
        System.out.println("primeiro os -> Cliente: " + clienteNumber);
        cliente = Integer.parseInt(clienteNumber);
        if(cliente > 0)
          enemy = 0;
        else enemy = 1;
        System.out.println("CLIENTE: " + cliente + "ENEMY: " + enemy);
      }
      // name = JOptionPane.showInputDialog(null, "Qual o seu nome?", "Digite seu nome", JOptionPane.QUESTION_MESSAGE);
      // os.println(name);
      // nameLabel.setText(name);
      String canStartFromServer = is.nextLine();
      System.out.println("segundo os -> " + canStartFromServer); 

      do {
        System.out.println("os numero " + i + (inputLine=is.nextLine())+"\n");
        i++;
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
