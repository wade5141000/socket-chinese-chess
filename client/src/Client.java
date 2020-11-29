import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import static org.fusesource.jansi.Ansi.ansi;
public class Client {

  static String[][] game = new String[4][8];

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
//      String init = input.readUTF();
//      System.out.println(init);
      String init = initBoard();
      System.out.println(init);
      String move = "";
      String where = "";
      String target = "";
      while (true) {
        String result1 = input.readUTF();
        cleanConsole();
        System.out.println(ansi().render(result1));
        String token = input.readUTF(); // control
        System.out.println("請輸入要執行的動作(輸入數字) (1)移動棋子 (2)翻牌 (3)結束遊戲");
        move = sc.nextLine();
        if("1".equals(move)){
          System.out.println("請輸入要移動的棋子 座標(x,y)，例如：0,4");
          where = sc.nextLine();
          System.out.println("請輸入要前往的目標 座標(x,y)，例如：0,4");
          target = sc.nextLine();
        }else if("2".equals(move)){
          System.out.println("請輸入要翻牌的棋子 座標(x,y)，例如：0,4");
          where = sc.nextLine();
          target = "0,0";
        }else{
          break;
        }
        out.writeUTF(move +"," + where + "," + target);
        String result2 = input.readUTF();
        cleanConsole();
        System.out.println(ansi().render(result2));
      }
      System.out.println("遊戲結束");
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

  private static String initBoard(){
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
