import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;

class Servidor {
  public static void main(String[] args) {
    ServerSocket serverSocket=null;

    try {
      serverSocket = new ServerSocket(80);
    } catch (IOException e) {
      System.out.println("Could not listen on port: " + 80 + ", " + e);
      System.exit(1);
    }

    for (int i=0; i<3; i++) {
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
  static int cont=0;
  static int gettingNames = 0;
  static int gettingLists = 0;
  static String names[] = new String[2];

  Servindo(Socket clientSocket) {
    this.clientSocket = clientSocket;
    try {
        os[cont] = new PrintStream(clientSocket.getOutputStream());
        os[cont].println(cont);
        os[cont].flush();
        if(cont == 1) {
          os[cont - 1].println("PODE COMECAR");
          os[cont].println("PODE COMECAR");
        }
    } catch (IOException erro) {}
  }

  public void run() {
    try {
      Scanner is = new Scanner(clientSocket.getInputStream());
      os[cont++] = new PrintStream(clientSocket.getOutputStream());
      String inputLine, outputLine;
      String  gettingWords[] = new String[2];
      String data[] = new String[2];
      String dados[] = new String[6];
      // List<String> listReceived0 = new ArrayList<String>();
      // List<String> listReceived1 = new ArrayList<String>();
      int cliente;
      // int clienteList;

      if(gettingNames < 2) {
        System.out.println("ENTROU GETTING NAMES\n");
        data = is.nextLine().split(" ");
        cliente = Integer.parseInt(data[0]);

        names[cliente] = data[1];
        gettingNames++;
      }

      System.out.println("NOMES:   " + names[0] + " --- " + names[1] + " GETTING NAMES: " + gettingNames + "\n");
      if(gettingNames == 2) {
        os[0].println(names[1]);
        os[1].println(names[0]);
        gettingNames++;
      }
      // if(gettingLists < 2) {
      //   int i = 0;
      //   while(is.hasNext()) {
      //     gettingWords = is.nextLine().split(" ");
      //     clienteList = Integer.parseInt(gettingWords[0]);
      //     if(clienteList == 0) listReceived0.add(gettingWords[1]);
      //     else if(clienteList == 1) listReceived1.add(gettingWords[1]);
      //     System.out.println(" CLIENTE[" + gettingWords[0] + "]: " + gettingWords[1] + "contador" + i++ +"\n");
      //   }
      //   gettingLists++;
      // }

        do {
          inputLine = is.nextLine();
          System.out.println("Servidor -> " + inputLine);
          dados = inputLine.split(" ");
          int clienteReceived = Integer.parseInt(dados[0]);
          String word = dados[1];
          int posX = Integer.parseInt(dados[2]);
          int posY = Integer.parseInt(dados[3]);
          int index = Integer.parseInt(dados[4]);
          int lifes = Integer.parseInt(dados[5]);
          if(posY > 790 && lifes >= 0) lifes--;

          try {
            Thread.sleep(50);
          } catch(InterruptedException ie) { }
          
          if(clienteReceived == 0) {
            os[1].println(word + " " + posX + " " + posY + " " + index + " " + lifes);
            os[1].flush();
          } else if (clienteReceived == 1) {
            os[0].println(word + " " + posX + " " + posY + " " + index + " " + lifes);
            os[0].flush();
          }
        } while (!inputLine.equals(""));

      for (int i=0; i<cont; i++)
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
