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
  JPanel centerDiv = new JPanel(null);
  JPanel leftDiv = new JPanel(new GridLayout(2, 1));
  JPanel rightDiv = new JPanel(new GridLayout(2, 1));
  JLabel nameLabel = new JLabel("No name");
  JLabel enemyNameLabel = new JLabel("Enemy name");
  JLabel wordLabel[] = new JLabel[100];
  JLabel enemyWordLabel[] = new JLabel[100];
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
  int speed = 600;
  String canStartFromServer;
  JLabel waitingEnemy = new JLabel("Waiting enemy...");

  ClienteFrame() {
    super("Fast Words");
    leftDiv.add(nameLabel);
    leftDiv.add(selfLifesLabel);
    rightDiv.add(enemyNameLabel);
    rightDiv.add(enemyLifesLabel);
    topDiv.setBackground(new Color(184, 242, 230));
    leftDiv.setBackground(new Color(184, 242, 230));
    rightDiv.setBackground(new Color(184, 242, 230));
    topDiv.add(leftDiv, BorderLayout.WEST);
    topDiv.add(rightDiv, BorderLayout.EAST);
    centerDiv.setBackground(new Color(207, 219, 213));

    waitingEnemy.setSize(200, 20);
    waitingEnemy.setFont(new Font("Arial", Font.PLAIN, 20));
    waitingEnemy.setLocation(400, 25);
    centerDiv.add(waitingEnemy);

    add(topDiv, BorderLayout.NORTH);
    add(centerDiv, BorderLayout.CENTER);
    setPreferredSize(new Dimension(1000, 800));
    setResizable(false);
    pack();
    setLocationRelativeTo(null); // alinhar o JFrame ao centro
    setVisible(true);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    // Setando o tamanho das labels do menu
    selfLifesLabel.setSize(200, 20);
    selfLifesLabel.setFont(new Font("Arial", Font.PLAIN, 20));

    enemyLifesLabel.setSize(200, 20);
    enemyLifesLabel.setFont(new Font("Arial", Font.PLAIN, 20));

    nameLabel.setSize(200, 20);
    nameLabel.setFont(new Font("Arial", Font.PLAIN, 20));

    enemyNameLabel.setSize(200, 20);
    enemyNameLabel.setFont(new Font("Arial", Font.PLAIN, 20));

    addKeyListener(new KeyAdapter() {
        public void keyPressed(KeyEvent e) {
          if(name.compareTo("-1") != 0 && enemyName.compareTo("-1") != 0) {
            // System.out.println("TECLA DIGITADA: " + e.getKeyChar() + "\n");
            // inputText = "Codigo: " + e.getKeyChar() + " ";
            teclaDigitada = e.getKeyChar();
            // os.println(cliente + " " + e.getKeyChar());
            while(wordLabel[wordCont] != null) {
              String word = wordLabel[wordCont].getText();
              // System.out.println("PREV WORD AND WORD in label -->>" + prevWords[wordCont] + "   " + word + "\n");
              if(word != "") { // está verificando todas as palavras, ou seja, se várias começarem com "a", vai apagar o "a" de todas
                  verifyWord(word, wordCont);
                }
              wordCont++;
            }
            wordCont = 0;
            // inputText = new String("Henrique" + " ");
            // os.println(inputText);
          }
        }
    });

    for(int i = 0; i < 100; i++)
      enemyWordLabel[i] = new JLabel();

    new Thread(new Runnable() {
      public void run() {
        try {
          do {
            // System.out.println("nome: " + name + " inimigo: " + enemyName + "\n" + (name.compareTo("-1") != 0) + (enemyName.compareTo("-1") != 0));
            if(name.compareTo("-1") != 0 && enemyName.compareTo("-1") != 0) {
              int wordsCount = 0;
              // int contTest = 0;
              int i = 0;
              List<String> list = rw.createList(100);
              // Collections.sort(list, Collator.getInstance());
              Iterator<String> words = list.iterator();
              // Iterator<String> wordsToSend = list.iterator();
              // while(wordsToSend.hasNext()) {
              //   String wordToSend = wordsToSend.next().toString();
              //   os.println(cliente + " " + wordToSend);
              //   contTest++;
              //   System.out.println("CONTADOR: " + contTest + "\n");
              // }
              // System.out.println("INICIOU getDownWords\n");
              waitingEnemy.setText("");
              while(words.hasNext()) {
                // System.out.println("ENTROU NO IF ->" + randomXAxis[i]);
                String word = words.next().toString();
                prevWords[i] = word;
                randomXAxis[i] = getRandomXAxis(0, 700);

                wordLabel[i] = new JLabel(word);
                wordLabel[i].setSize(200, 20);
                wordLabel[i].setFont(new Font("Arial", Font.PLAIN, 20));

                // wordLabel[i].setBackground(Color.red);
                centerDiv.add(wordLabel[i]);

                pos[i] = 0;
                wordLabel[i].setLocation(randomXAxis[i], pos[i]);
                // System.out.println("wordLabel[" + i + "]: " + wordLabel[i].getText() + "\n");
                //repaint();
                i++;
                wordsCount++;
                if(wordsCount % 10 == 0) {
                  speed -= 100;
                  if(speed <= 0) speed = 100;
                }
                Thread.sleep(5000); //35 segundos com 10 palavras
              }
              // Thread.sleep(12000); //12 segundos
            }
            Thread.sleep(1000);
          } while(true);
        } catch(InterruptedException ex) {
          System.err.println("Erro no run das palavras -> " + ex);
        }
      }
    }).start();

    new Thread(new Runnable() {
      int cont = 0;
      public void run() {
        try {
          do {
            while(wordLabel[cont] != null) {
              pos[cont] += 5;
              // cliente palavra x y
              os.println(cliente + " " + wordLabel[cont].getText() + " " + randomXAxis[cont] + " " + pos[cont] + " " + cont + " " + selfLifes);
              // System.out.println("LABEL[" + cont + "]: " + wordLabel[cont].getText() + "\n");
              wordLabel[cont].setLocation(randomXAxis[cont], pos[cont]);
              if(pos[cont] > 790) {
                selfLifes--;
                if(selfLifes == 0) System.out.println("PERDEU SUAS VIDAS!");
                selfLifesLabel.setText("Remain lifes: " + selfLifes);
              }
              cont++;
              // System.out.println("count: " + cont + "\n");
            }
            cont = 0;
            Thread.sleep(speed);
          } while(true);
        }catch(InterruptedException ex) {
          System.err.println("Erro no run do cont -> " + ex);
        }
      }
    }).start();
  }

  public void verifyWord(String word, int index) {
    // System.out.println("PALAVRA: " + word + "\n");
    int tamanho = word.length();
    // if(tamanho == 0) {
    //   System.out.println("COMPLETOU A PALAVRAAA\n");
    //   wordLabel[index].setText("");
    // }
    if(word.charAt(0) == teclaDigitada) {
      if(tamanho == 1) wordLabel[index].setText("");
      else wordLabel[index].setText(word.substring(1));
      // System.out.println("NOVA PALAVRA: " + word.substring(1) + "\n");
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
      socket = new Socket("192.168.0.8", 80);
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
      canStartFromServer = is.nextLine();

      if(canStartFromServer.compareTo(canStart) == 0) {    
        name = JOptionPane.showInputDialog(null, "Qual o seu nome?", "Digite seu nome", JOptionPane.QUESTION_MESSAGE);

        if (name == null || name.compareTo("-1") == 0 || name.equals("")) {
          int numero = cliente + 1;
          name = "Jogador" + numero;
        }

        if(name.compareTo("-1") != 0) os.println(cliente + " " + name);
        enemyName = is.nextLine();
        nameLabel.setText(name);
        enemyNameLabel.setText(enemyName);
      do {
        inputLine=is.nextLine();
        System.out.println("IS de numero " + i + ": " + (inputLine)+"\n");
        String enemyData[] = new String[5];

        enemyData = inputLine.split(" ");
        for(int i = 0; i < 5; i++)
          System.out.println("enemyData[" + i + "]: " + enemyData[i] + "\n");
        System.out.println("enemyWordLabel ->> " + enemyWordLabel[0]);
        int enemyCont = Integer.parseInt(enemyData[3]);
        enemyWordLabel[enemyCont].setText(enemyData[0]);
        enemyWordLabel[enemyCont].setLocation(Integer.parseInt(enemyData[1]), Integer.parseInt(enemyData[2]));
        enemyWordLabel[enemyCont].setSize(200, 20);
        enemyWordLabel[enemyCont].setForeground(new Color(158, 160, 158));
        enemyWordLabel[enemyCont].setFont(new Font("Arial", Font.PLAIN, 20));
        enemyLifesLabel.setText("Remain lifes: " + enemyData[4]);
        centerDiv.add(enemyWordLabel[enemyCont]);
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
