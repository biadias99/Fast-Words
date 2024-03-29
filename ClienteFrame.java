import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;
import java.lang.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;

class ImagePanel extends JPanel {

  private Image img;

  public ImagePanel(Image img) {
    this.img = img;
    Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
    setPreferredSize(size);
    setMinimumSize(size);
    setMaximumSize(size);
    setSize(size);
    setLayout(null);
  }

  public void paintComponent(Graphics g) {
    g.drawImage(img, 0, 0, null);
  }

}

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
  String lostWords[] = new String[3];
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
  int speed = 100;
  String canStartFromServer;
  JLabel waitingEnemy = new JLabel("Waiting enemy...");
  static int lifeThread = 1;

  ClienteFrame() {
    super("Fast Words");
    centerDiv = new ImagePanel(new ImageIcon("fundo.jpeg").getImage());
    leftDiv.add(nameLabel);
    leftDiv.add(selfLifesLabel);
    rightDiv.add(enemyNameLabel);
    rightDiv.add(enemyLifesLabel);
    topDiv.setBackground(new Color(0, 0, 0));
    leftDiv.setBackground(new Color(0, 0, 0));
    rightDiv.setBackground(new Color(0, 0, 0));
    topDiv.add(leftDiv, BorderLayout.WEST);
    topDiv.add(rightDiv, BorderLayout.EAST);
    centerDiv.setBackground(new Color(207, 219, 213));

    waitingEnemy.setSize(200, 80);
    waitingEnemy.setFont(new Font("Arial", Font.BOLD, 20));
    waitingEnemy.setLocation(400, 25);
    waitingEnemy.setForeground(new Color(255, 255, 255));
    
    centerDiv.add(waitingEnemy);

    add(topDiv, BorderLayout.NORTH);
    add(centerDiv, BorderLayout.CENTER);
    setPreferredSize(new Dimension(1000, 800));
    setResizable(false);
    pack();
    setLocationRelativeTo(null); // alinhar o JFrame ao centro
    setVisible(true);
    setDefaultCloseOperation(EXIT_ON_CLOSE);

    // Inicializa as duas variáveis porque não pode ser null
    for (int i = 0; i < 3; i++)
      lostWords[i] = "";

    for (int i = 0; i < 100; i++)
      enemyWordLabel[i] = new JLabel();

    // Setando o tamanho das labels do menu
    selfLifesLabel.setSize(200, 20);
    selfLifesLabel.setFont(new Font("Arial", Font.PLAIN, 20));
    selfLifesLabel.setForeground(new Color(255, 255, 255));

    enemyLifesLabel.setSize(200, 20);
    enemyLifesLabel.setFont(new Font("Arial", Font.PLAIN, 20));
    enemyLifesLabel.setForeground(new Color(255, 255, 255));

    nameLabel.setSize(200, 20);
    nameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
    nameLabel.setForeground(new Color(255, 255, 255));

    enemyNameLabel.setSize(200, 20);
    enemyNameLabel.setFont(new Font("Arial", Font.PLAIN, 20));
    enemyNameLabel.setForeground(new Color(255, 255, 255));

    addKeyListener(new KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        if (name.compareTo("-1") != 0 && enemyName.compareTo("-1") != 0) {
          teclaDigitada = e.getKeyChar();
          while (wordLabel[wordCont] != null) {
            String word = wordLabel[wordCont].getText();
            if (word != "") { // está verificando todas as palavras, ou seja, se várias começarem com "a", vai
                              // apagar o "a" de todas
              verifyWord(word, wordCont);
            }
            wordCont++;
          }
          wordCont = 0;
        }
      }
    });

    new Thread(new Runnable() {
      public void run() {
        try {
          do {
            if (name.compareTo("-1") != 0 && enemyName.compareTo("-1") != 0) { // usuários chegaram?
              int wordsCount = 0;
              int i = 0;
              List<String> list = rw.createList(100); // cria uma lista com palavras randômicas
              Iterator<String> words = list.iterator();
              waitingEnemy.setText("");
              while (words.hasNext() && lifeThread == 1) {
                String word = words.next().toString(); // pega a próxima palavra que vem da lista
                prevWords[i] = word;
                randomXAxis[i] = getRandomXAxis(0, 700);
                
                // Seta a posição da palavra do usuário
                wordLabel[i] = new JLabel(word);
                wordLabel[i].setSize(200, 50);
                wordLabel[i].setFont(new Font("Arial", Font.BOLD, 25));
                wordLabel[i].setForeground(new Color(241, 196, 15));

                centerDiv.add(wordLabel[i]);

                pos[i] = 0; // Para iniciar lá em cima
                wordLabel[i].setLocation(randomXAxis[i], pos[i]);
                
                i++;
                wordsCount++;
                if (wordsCount % 10 == 0) { // aumenta a velocidade a cada 10 palavras
                  speed -= 20;
                  if (speed <= 0) // entretanto, se for 0, ele deixa padrão 100 pq muito rápido dificulta
                    speed = 20;
                }
                Thread.sleep(3500); // gera palavra nova na tela a cada 3,5s
              }
            }
            Thread.sleep(100); // seta um tempo para procurar se os usuários entraram
          } while (lifeThread == 1);
        } catch (InterruptedException ex) {
          System.err.println("Erro no run das palavras -> " + ex);
        }
      }
    }).start();

    new Thread(new Runnable() {
      int cont = 0;
      int contLostWords = -1;

      public void run() {
        try {
          do {
            while (wordLabel[cont] != null) {
              pos[cont] += 1;
              wordLabel[cont].setLocation(randomXAxis[cont], pos[cont]); // seta a nova posicao +5

              // envia para o servidor os dados
              os.println(cliente + " " + wordLabel[cont].getText() + " " + randomXAxis[cont] + " " + pos[cont] + " "
                  + cont + " " + selfLifes);

              // verifica quem ganhou e perdeu e para as threads
              if (selfLifes == 0 && enemyLifes != 0) {
                lifeThread = 0; // para threads
                centerDiv.removeAll();

                waitingEnemy.setText("YOU LOSE!");
                waitingEnemy.setSize(300, 80);
                waitingEnemy.setFont(new Font("Arial", Font.BOLD, 50));
                waitingEnemy.setLocation(400, 25);
                waitingEnemy.setForeground(new Color(255, 255, 255));

                centerDiv.add(waitingEnemy);

                revalidate();
                repaint();
              } else if (selfLifes != 0 && enemyLifes == 0) {
                lifeThread = 0;
                centerDiv.removeAll();

                waitingEnemy.setText("YOU WIN!");
                waitingEnemy.setSize(300, 80);
                waitingEnemy.setFont(new Font("Arial", Font.BOLD, 50));
                waitingEnemy.setLocation(400, 25);
                waitingEnemy.setForeground(new Color(255, 255, 255));

                centerDiv.add(waitingEnemy);

                revalidate();
                repaint();
              } else if (selfLifes == 0 && enemyLifes == 0) {
                lifeThread = 0;
                centerDiv.removeAll();

                waitingEnemy.setText("DRAW!");
                waitingEnemy.setSize(300, 80);
                waitingEnemy.setFont(new Font("Arial", Font.BOLD, 50));
                waitingEnemy.setLocation(400, 25);
                waitingEnemy.setForeground(new Color(255, 255, 255));

                centerDiv.add(waitingEnemy);

                revalidate();
                repaint();
              }

              if (pos[cont] > 710 && !wordLabel[cont].getText().equals("") && !wordExists(wordLabel[cont].getText())) {
                // se for maior q 710 o pos, ele perdeu a palavra e perde vida
                contLostWords++;
                lostWords[contLostWords] = wordLabel[cont].getText();
                selfLifes--;
                selfLifesLabel.setText("Remain lifes: " + selfLifes);
              }
              cont++;
            }
            cont = 0; // começa a contar palavras de novo
            Thread.sleep(speed); // velocidade
          } while (lifeThread == 1);
        } catch (InterruptedException ex) {
          System.err.println("Erro no run do cont -> " + ex);
        }
      }
    }).start();
  }

  public boolean wordExists(String word) { // verifica se palavra existe na lista de palavras perdidas
    for (int i = 0; i < 3; i++) {
      if (lostWords[i].equals(word)) {
        return true;
      }
    }
    return false;
  }

  public void verifyWord(String word, int index) {
    // verifica se a tecla digitada correspondente a palavra
    int tamanho = word.length();
    if (word.charAt(0) == teclaDigitada) {
      if (tamanho == 1)
        wordLabel[index].setText("");
      else
        wordLabel[index].setText(word.substring(1));
    } else {
      wordLabel[index].setText(prevWords[index]);
      i = 0;
    }
  }

  public int getRandomXAxis(int min, int max) { // gera um num aleatorio
    return (int) (Math.random() * ((max - min) + 1)) + min;
  }

  public static void main(String[] args) {
    Thread threadMain = new Thread(new ClienteFrame());
    threadMain.start();
  }

  public void run() {
    Socket socket = null;
    Scanner is = null;

    try {
      socket = new Socket("186.217.119.110", 8383); // conexao com servidor
      os = new PrintStream(socket.getOutputStream(), true);
      is = new Scanner(socket.getInputStream());
    } catch (UnknownHostException e) {
      System.err.println("Don't know about host.");
    } catch (IOException e) {
      System.err.println("Couldn't get I/O for the connection to host");
    }

    try {
      String inputLine;

      if (cliente < 0) {
        String clienteNumber = is.nextLine(); // pega linha do servidor
        cliente = Integer.parseInt(clienteNumber); // pega cliente
        if (cliente > 0)
          enemy = 0;
        else
          enemy = 1;
      }
      canStartFromServer = is.nextLine(); // verifica se pode começar (2 clientes)

      if (canStartFromServer.compareTo(canStart) == 0) {
        name = JOptionPane.showInputDialog(null, "Qual o seu nome?", "Digite seu nome", JOptionPane.QUESTION_MESSAGE);

        // se o nome nao existir (fechou a janela, cancelou ou enviou vazio), seta um padrão
        if (name == null || name.compareTo("-1") == 0 || name.equals("")) {
          int numero = cliente + 1;
          name = "Jogador" + numero;
        }

        // manda pro servidor o nome do jogador
        if (name.compareTo("-1") != 0)
          os.println(cliente + " " + name);

        enemyName = is.nextLine();
        nameLabel.setText(name);
        enemyNameLabel.setText(enemyName);

        do {
          inputLine = is.hasNext() ? is.nextLine() : ""; // evita q dê erro caso n tiver mais linhas
          String enemyData[] = new String[5];
          enemyData = inputLine.split(" ");

          // pega dados do inimigo
          int enemyCont = Integer.parseInt(enemyData[3]);
          enemyLifes = Integer.parseInt(enemyData[4]);
          enemyWordLabel[enemyCont].setText(enemyData[0]);
          enemyWordLabel[enemyCont].setLocation(Integer.parseInt(enemyData[1]), Integer.parseInt(enemyData[2]));
          enemyWordLabel[enemyCont].setSize(200, 50);
          enemyWordLabel[enemyCont].setForeground(new Color(236, 240, 241));
          enemyWordLabel[enemyCont].setFont(new Font("Arial", Font.BOLD, 25));
          enemyLifesLabel.setText("Remain lifes: " + enemyData[4]);
          centerDiv.add(enemyWordLabel[enemyCont]); // adiciona palavra do inimigo na tela
          i++;
        } while (!inputLine.equals("") && lifeThread == 1);
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
