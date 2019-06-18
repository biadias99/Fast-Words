import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

// conexão padrão
class Servidor {
  public static void main(String[] args) {
    ServerSocket serverSocket = null;

    try {
      serverSocket = new ServerSocket(8383);
    } catch (IOException e) {
      System.out.println("Could not listen on port: " + 80 + ", " + e);
      System.exit(1);
    }

    for (int i = 0; i < 3; i++) {
      Socket clientSocket = null;
      try {
        clientSocket = serverSocket.accept();
      } catch (IOException e) {
        System.out.println("Accept failed: " + 80 + ", " + e);
        System.exit(1);
      }
      System.out.println("Accept Funcionou!");

      new Servindo(clientSocket).start();

    }

    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}

class Servindo extends Thread {
  Socket clientSocket;
  static PrintStream os[] = new PrintStream[3];
  static int cont = 0;
  static int gettingNames = 0;
  static int gettingLists = 0;
  static String names[] = new String[2];
  static int lifeThread = 1;

  Servindo(Socket clientSocket) {
    this.clientSocket = clientSocket;
    try {
      os[cont] = new PrintStream(clientSocket.getOutputStream());
      os[cont].println(cont); // enviando num do client -> 0 ou 1
      os[cont].flush();
      if (cont == 1) {
        os[cont - 1].println("PODE COMECAR");
        os[cont].println("PODE COMECAR");
      }
    } catch (IOException erro) {
    }
  }

  // verifica se a palavra está nas palavras q ele perdeu
  public boolean wordExists(String lostWords[], String word) {
    for (int i = 0; i < 3; i++) {
      if (lostWords[i].equals(word)) {
        return true;
      }
    }
    return false;
  }

  public void run() {
    try {
      Scanner is = new Scanner(clientSocket.getInputStream());
      os[cont++] = new PrintStream(clientSocket.getOutputStream());
      String inputLine;
      String data[] = new String[2];
      String dados[] = new String[6];
      String lostWords0[] = new String[3];
      int contLostWords0 = 0;
      String lostWords1[] = new String[3];
      int contLostWords1 = 0;
      
      for (int i = 0; i < 3; i++) {
        lostWords0[i] = "";
        lostWords1[i] = "";
      }

      int cliente;

      // se for < 2, é pq n tem os 2 nomes ainda
      if (gettingNames < 2) {
        data = is.nextLine().split(" ");
        cliente = Integer.parseInt(data[0]);

        names[cliente] = data[1]; // salva os nomes do jogador com o indice do cliente
        gettingNames++;
      }

      if (gettingNames == 2) { // manda o nome do inimigo p o cliente
        os[0].println(names[1]);
        os[1].println(names[0]);
        gettingNames++;
      }

      do {
        inputLine = is.nextLine();
        dados = inputLine.split(" ");

        // recebe todos os dados e distribui p os clientes adequados
        int clienteReceived = Integer.parseInt(dados[0]);
        String word = dados[1]; // aqui tem a palavra já sem o caracter q as pessoas digitaram
        // se a pessoa erra, manda a string inteira
        int posX = Integer.parseInt(dados[2]);
        int posY = Integer.parseInt(dados[3]);
        int index = Integer.parseInt(dados[4]);
        int lifes = Integer.parseInt(dados[5]);

        // distribui p clientes adequados
        if (clienteReceived == 0) {
          // verifica se a palavra saiu da tela
          if (posY > 710 && lifes >= 0 && !word.equals("") && !wordExists(lostWords0, word)) {
            lostWords0[contLostWords0] = word;
            contLostWords0++;
            lifes--;
            if (lifes == 0) {
              lifeThread = 0;
            }
          }
          os[1].println(word + " " + posX + " " + posY + " " + index + " " + lifes);
          os[1].flush();
        } else if (clienteReceived == 1) {
          if (posY > 710 && lifes >= 0 && !word.equals("") && !wordExists(lostWords1, word)) {
            lostWords1[contLostWords1] = word;
            contLostWords1++;
            lifes--;
            if (lifes == 0) {
              lifeThread = 0;
            }
          }
          os[0].println(word + " " + posX + " " + posY + " " + index + " " + lifes);
          os[0].flush();
        }
      } while (!inputLine.equals("") && lifeThread == 1);

      for (int i = 0; i < cont; i++)
        os[i].close();
      is.close();
      clientSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    } catch (NoSuchElementException e) {
      System.out.println("Conexacao terminada pelo cliente");
    }
  }
};
