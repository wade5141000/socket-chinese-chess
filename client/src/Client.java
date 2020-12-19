import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

import static org.fusesource.jansi.Ansi.ansi;

public class Client {

  static String[][] game = new String[4][8];
  static String board = "";

  public static void main(String args[]) throws IOException {

    Scanner sc = new Scanner(System.in);
    Socket socket = null;
    DataInputStream input = null;
    DataOutputStream out = null;

    try {
      socket = new Socket("127.0.0.1", 5000);
      cleanConsole();
      System.out.println("連線成功!");
      input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
      out = new DataOutputStream(socket.getOutputStream());

      board = initBoard();
      System.out.println(board);
      String move = "";
      String where = "";
      String target = "";
      while (true) {
        String result1 = input.readUTF();
        cleanConsole();
        board = ansi().render(result1).toString();
        System.out.println(board);
        readGame(input);
//        String token = input.readUTF(); // control
        boolean valid = false;
        while (!valid) {
          boolean flag = false;
          System.out.println("請輸入要執行的動作(輸入數字) (1)移動/吃 棋子 (2)翻牌");
          move = sc.nextLine();
          if ("1".equals(move)) {
            System.out.println("請輸入要操作的棋子座標(上至下，左至右)，例如：0,6");
            where = sc.nextLine();
            System.out.println("請輸入要前往/攻擊的目標位置座標(上至下，左至右)，例如：0,6");
            target = sc.nextLine();
            flag = true;
          } else if ("2".equals(move)) {
            System.out.println("請輸入要翻牌的棋子座標(上至下，左至右)，例如：0,6");
            where = sc.nextLine();
            target = "0,0";
            flag = true;
          }
          if (flag && where.matches("[0-3],[0-7]") && target.matches("[0-3],[0-7]")) {
            out.writeUTF(move + "," + where + "," + target);
            valid = input.readBoolean();
            flag = valid;
          } else {
            flag = false;
          }
          if (!flag) {
            cleanConsole();
            System.out.println(board);
            System.out.println(ansi().render("\n@|red ***輸入錯誤，請重新輸入***|@").toString());
            System.out.println();
          }
        }

        String result2 = input.readUTF();
        cleanConsole();
        board = ansi().render(result2).toString();
        System.out.println(board);
//        String token2 = input.readUTF();
        readGame(input);
      }
//      System.out.println("遊戲結束");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      sc.close();
      input.close();
      out.close();
      socket.close();
    }

  }

  private static void cleanConsole() throws IOException, InterruptedException {
    ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "cls");
    pb.inheritIO().start().waitFor();
  }

  private static void readGame(DataInputStream input) throws IOException {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 8; j++) {
        game[i][j] = parseString(input.readUTF());
      }
    }
  }

  private static String parseString(String input) {
    if (input.length() == 1) return input;
    return input.split(" ")[1].substring(0, 1);
  }

  private static String initBoard() {
    StringBuilder sb = new StringBuilder();
    String space = "          ";
    sb.append("\n\n\n");
    sb.append(space);
    sb.append(" ┌──┬──┬──┬──┬──┬──┬──┬──┐\n");
    sb.append(space);
    sb.append("0│●│●│●│●│●│●│●│●│\n");
    sb.append(space);
    sb.append(" ├──┼──┼──┼──┼──┼──┼──┼──┤\n");
    sb.append(space);
    sb.append("1│●│●│●│●│●│●│●│●│\n");
    sb.append(space);
    sb.append(" ├──┼──┼──┼──┼──┼──┼──┼──┤\n");
    sb.append(space);
    sb.append("2│●│●│●│●│●│●│●│●│\n");
    sb.append(space);
    sb.append(" ├──┼──┼──┼──┼──┼──┼──┼──┤\n");
    sb.append(space);
    sb.append("3│●│●│●│●│●│●│●│●│\n");
    sb.append(space);
    sb.append(" └──┴──┴──┴──┴──┴──┴──┴──┘\n");
    sb.append(space);
    sb.append("   0  1  2  3  4  5  6  7");
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 8; j++) {
        game[i][j] = "●";
      }
    }

    return sb.toString();
  }

}
