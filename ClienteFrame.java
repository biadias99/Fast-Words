import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;

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
  String name = "-1";
  String enemyName = "-1";
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
    String palavra = "bianca";
    JLabel wordLabel = new JLabel(palavra);
    int tamanho = palavra.length();
    centerDiv.add(wordLabel);
    add(topDiv, BorderLayout.NORTH);
    add(centerDiv, BorderLayout.CENTER);
    pack();
    setSize(1000, 800);
    setLocationRelativeTo(null); // alinhar o JFrame ao centro
    setVisible(true);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    addKeyListener(new KeyAdapter() {
      int i = 0;
        public void keyPressed(KeyEvent e) {
          System.out.println("TECLA DIGITADA: " + e.getKeyChar() + "\n");
          inputText = "Codigo: " + e.getKeyChar() + " ";

          System.out.println("PALAVRA: " + palavra + "\n");
          if(palavra.charAt(i) == e.getKeyChar()) {
            wordLabel.setText(palavra.substring(i+1));
            repaint();
            System.out.println("NOVA PALAVRA: " + palavra.substring(i+1) + "\n");
            i++;
            if(i == tamanho) {
              System.out.println("COMPLETOU A PALAVRAAA\n");
              i = 0;
            }
          } else {
            wordLabel.setText(palavra);
            i = 0;
          }
          os.println(cliente + " " + e.getKeyChar());
          // inputText = new String("Henrique" + " ");
          // os.println(inputText);
        }
    });

    new Thread(new Runnable() {
      public void run() {
        try {
          do {
            System.out.println("nome: " + name + " inimigo: " + enemyName + "\n" + (name.compareTo("-1") != 0) + (enemyName.compareTo("-1") != 0));
            if(name.compareTo("-1") != 0 && enemyName.compareTo("-1") != 0) {
              System.out.println("ENTROU NO IF");
              Thread.sleep(1000);
            }
          } while(true);
        } catch(InterruptedException ex) {

        }
      }
    }).start();
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

      if(cliente < 0) {
        String clienteNumber = is.nextLine();
        System.out.println("primeiro os -> Cliente: " + clienteNumber);
        cliente = Integer.parseInt(clienteNumber);
        if(cliente > 0)
          enemy = 0;
        else enemy = 1;
        System.out.println("CLIENTE: " + cliente + "ENEMY: " + enemy);
      }
      String canStartFromServer = is.nextLine();

      if(canStartFromServer.compareTo(canStart) == 0) {
        System.out.println("PODE COMECARRRR");
        name = JOptionPane.showInputDialog(null, "Qual o seu nome?", "Digite seu nome", JOptionPane.QUESTION_MESSAGE);
        if(name.compareTo("-1") != 0) os.println(cliente + " " + name);
        enemyName = is.nextLine();
        nameLabel.setText(name);
        enemyNameLabel.setText(enemyName);
      do {
        System.out.println("OS de numero " + i + (inputLine=is.nextLine())+"\n");
        i++;
      } while (!inputLine.equals(""));
    }

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
