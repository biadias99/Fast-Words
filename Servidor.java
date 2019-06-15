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
      String data[] = new String[2];
      int cliente;

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
        do {
          inputLine = is.nextLine();
          System.out.println("\nDADOS RECEBIDOS: " + inputLine);
          for (int i=0; i<cont; i++) {
            System.out.println("CLIENTE " + i + " -> " + "Dados sendo enviados: " + inputLine);
            os[i].println(inputLine);
            os[i].flush();
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
