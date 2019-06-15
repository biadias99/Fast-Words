import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.lang.*;
import java.text.Collator;

public class ClienteFrame extends JFrame implements Runnable {
  static PrintStream os = null;
  JPanel topDiv = new JPanel(new BorderLayout());
  JPanel centerDiv = new JPanel();
  JPanel leftDiv = new JPanel(new GridLayout(2, 1));
  JPanel rightDiv = new JPanel(new GridLayout(2, 1));
  JLabel nameLabel = new JLabel("No name");
  JLabel enemyNameLabel = new JLabel("Enemy name");
  JLabel wordLabel[] = new JLabel[100];
  int randomXAxis[] = new int[100];
  String prevWords[] = new String[100];
  RandomWords rw = new RandomWords();
  int selfLifes = 3;
  int enemyLifes = 3;
  JLabel selfLifesLabel = new JLabel("Remain lifes: " + selfLifes);
  JLabel enemyLifesLabel = new JLabel("Remain lifes: " + enemyLifes);
  int cliente = -1;
  int enemy = -1;
  String name = "-1";
  String enemyName = "-1";
  int i = 2;
  int pos[] = new int[100];
  int wordCont = 0;
  String inputText;
  String canStart = "PODE COMECAR";
  char teclaDigitada;

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
    setPreferredSize(new Dimension(1500, 900));
    setResizable(false);
    pack();
    setLocationRelativeTo(null); // alinhar o JFrame ao centro
    setVisible(true);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    addKeyListener(new KeyAdapter() {
      int i = 0;
        public void keyPressed(KeyEvent e) {
          // System.out.println("TECLA DIGITADA: " + e.getKeyChar() + "\n");
          // inputText = "Codigo: " + e.getKeyChar() + " ";
          teclaDigitada = e.getKeyChar();
          os.println(cliente + " " + e.getKeyChar());
          while(wordLabel[wordCont] != null) {
            String word = wordLabel[wordCont].getText();
            System.out.println("PREV WORD AND WORD in label -->>" + prevWords[wordCont] + "   " + word + "\n");
            if(word != "") { // está verificando todas as palavras, ou seja, se várias começarem com "a", vai apagar o "a" de todas
                verifyWord(word, wordCont);
              }
            wordCont++;
          }
          wordCont = 0;
          // inputText = new String("Henrique" + " ");
          // os.println(inputText);
        }
    });

    new Thread(new Runnable() {
      public void run() {
        try {
          do {
            // System.out.println("nome: " + name + " inimigo: " + enemyName + "\n" + (name.compareTo("-1") != 0) + (enemyName.compareTo("-1") != 0));
            if(name.compareTo("-1") != 0 && enemyName.compareTo("-1") != 0) {
              List<String> list = rw.createList(10);
              Collections.sort(list, Collator.getInstance());
              Iterator<String> words = list.iterator();
              getDownWords.start();
              // System.out.println("INICIOU getDownWords\n");
              int i = 0;
                while(words.hasNext()) {
                  randomXAxis[i] = getRandomXAxis(0, 1450);
                  // System.out.println("ENTROU NO IF ->" + randomXAxis[i]);
                  String word = words.next().toString();
                  prevWords[i] = word;
                  int tamanho = word.length();
                  // System.out.println("RandomWord string: " + word + " tamanho: " + tamanho + "\n");
                  wordLabel[i] = new JLabel(word);
                  wordLabel[i].setSize(100, 10);
                  wordLabel[i].setBackground(Color.red);
                  centerDiv.add(wordLabel[i]);
                  pos[i] = 0;
                  wordLabel[i].setLocation(randomXAxis[i], pos[i]);
                  // System.out.println("wordLabel[" + i + "]: " + wordLabel[i].getText() + "\n");
                  //repaint();
                  i++;
                  Thread.sleep(3500);
                }
              Thread.sleep(200000);
            }
            Thread.sleep(1000);
          } while(true);
        } catch(InterruptedException ex) {}
      }
    }).start();
  }

  Thread getDownWords = new Thread(new Runnable() {
    int cont = 0;
    public void run() {
      try {
        do {
          while(wordLabel[cont] != null) {
            pos[cont] += 5;
            // System.out.println("LABEL[" + cont + "]: " + wordLabel[cont].getText() + "\n");
            wordLabel[cont].setLocation(randomXAxis[cont], pos[cont]);
            cont++;
            // System.out.println("count: " + cont + "\n");
          }
          cont = 0;
          Thread.sleep(200);
        } while(true);
      }catch(InterruptedException ex) {}
    }
  });

  public void verifyWord(String word, int index) {
    System.out.println("PALAVRA: " + word + "\n");
    int tamanho = word.length();
    if(tamanho == 0) {
      System.out.println("COMPLETOU A PALAVRAAA\n");
      wordLabel[index].setText("");
    }
    if(word.charAt(0) == teclaDigitada) {
      if(tamanho == 1) wordLabel[index].setText("");
      else wordLabel[index].setText(word.substring(1));
      System.out.println("NOVA PALAVRA: " + word.substring(1) + "\n");
    } else {
      wordLabel[index].setText(prevWords[index]);
      i = 0;
    }
  }

  public int getRandomXAxis(int min, int max) {
    return (int)(Math.random() * ((max - min) + 1)) + min;
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
        System.out.println("OS de numero " + i + ": " + (inputLine=is.nextLine())+"\n");
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
