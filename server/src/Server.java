import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.fusesource.jansi.Ansi.ansi;

public class Server {

  static String blue = "將士象車馬砲卒";
  static String red = "帥仕相硨傌炮兵";
  static List<String> chess = new ArrayList<>(Arrays.asList(
      "將", "士", "士", "象", "象", "車", "車", "馬", "馬", "砲", "砲", "卒", "卒", "卒", "卒", "卒",
      "帥", "仕", "仕", "相", "相", "硨", "硨", "傌", "傌", "炮", "炮", "兵", "兵", "兵", "兵", "兵"
  ));
  static String[][] game = new String[4][8];
  static Random random = new Random();
  public static void main(String args[]) throws IOException {
    AnsiConsole.systemInstall();
    ServerSocket server = new ServerSocket(5000);

    Socket c1 = null;
    DataOutputStream c1_out = null;
    DataInputStream c1_input = null;

    Socket c2 = null;
    DataOutputStream c2_out = null;
    DataInputStream c2_input = null;

    try {
      String board = initBoard();
      System.out.println("等待玩家 1...");
      c1 = server.accept();
      c1_out = new DataOutputStream(c1.getOutputStream());
      c1_input = new DataInputStream(new BufferedInputStream(c1.getInputStream()));
      c1_out.writeUTF(board);

      System.out.println("等待玩家 2...");
      c2 = server.accept();
      c2_out = new DataOutputStream(c2.getOutputStream());
      c2_input = new DataInputStream(new BufferedInputStream(c2.getInputStream()));
      // c2_out.writeUTF(board);
      cleanConsole();
      System.out.println("連線成功，遊戲開始");
      String c1_say = "";
      String c2_say = "";
      String move = "";
      String[] where = new String[2];
      String[] target = new String[2];
      String game = "";
      String[] moves;
      while (!c1_say.equalsIgnoreCase("quit") && !c2_say.equalsIgnoreCase("quit")) {
        c1_out.writeUTF("OK"); // token
        c1_say = c1_input.readUTF();
        moves = c1_say.split(",");
        move = moves[0];
        where[0] = moves[1];
        where[1] = moves[2];
        target[0] = moves[3];
        target[1] = moves[4];
        if("1".equals(move)){

        }else if("2".equals(move)){
          game = openChess(Integer.parseInt(where[0]), Integer.parseInt(where[1]));
        }
        c1_out.writeUTF(game);
        c2_out.writeUTF(game);
        // ----------------------------------
        c2_out.writeUTF("OK"); // token
        c2_say = c2_input.readUTF();
        moves = c2_say.split(",");
        move = moves[0];
        where[0] = moves[1];
        where[1] = moves[2];
        target[0] = moves[3];
        target[1] = moves[4];
        if("1".equals(move)){

        }else if("2".equals(move)){
          game = openChess(Integer.parseInt(where[0]), Integer.parseInt(where[1]));
        }
        c1_out.writeUTF(game);
        c2_out.writeUTF(game);
      }
      System.out.println("Closing connection");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      c1_out.close();
      c1_input.close();
      c1.close();
      c2_out.close();
      c2_input.close();
      c2.close();
      server.close();
    }
  }

  private static void cleanConsole() throws IOException, InterruptedException {
    new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
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

  private static String openChess(int x, int y) {

    String target = game[x][y];
    if("●".equals(target)){
      String piece = chess.remove(random.nextInt(chess.size()));
      piece = blue.contains(piece) ? "@|blue " + piece + "|@" : "@|red " + piece + "|@";
      game[x][y] = piece;
    }else{
      System.out.println("不可翻牌");
    }
    StringBuilder sb = new StringBuilder();
    String space = "          ";
    sb.append("\n\n\n");
    sb.append(space);
    sb.append(" ┌──┬──┬──┬──┬──┬──┬──┬──┐\n");
//    for (int i = 0; i < 4; i++) {
//      for (int j = 0; j < 8; j++) {
//        String piece = chess.remove(random.nextInt(chess.size()));
//        piece = blue.contains(piece) ? "@|blue " + piece + "|@" : "@|red " + piece + "|@";
//        game[i][j] = piece;
//      }
//    }
    for (int i = 0; i < 4; i++) {
      sb.append(space).append(i).append("│");
      for (int j = 0; j < 8; j++) {
        sb.append(game[i][j]).append("│");
      }
      if(i<3){
        sb.append("\n").append(space).append(" ├──┼──┼──┼──┼──┼──┼──┼──┤\n");
      }
      else{
        sb.append("\n").append(space).append(" └──┴──┴──┴──┴──┴──┴──┴──┘\n").append(space).append("   0  1  2  3  4  5  6  7");
      }

    }
    return sb.toString();
    // System.out.println(ansi().render(sb.toString()));

//    while (chess.size() > 0) {
//      System.out.println(ansi().render(piece));
//    }

//    StringBuilder sb = new StringBuilder();
//    String space = "          ";
//    sb.append("\n\n\n");
//    sb.append(space);
//    sb.append(" ┌──┬──┬──┬──┬──┬──┬──┬──┐\n");
//    sb.append(space);
//    sb.append("4│將│士│士│象│象│車│車│馬│\n");
//    sb.append(space);
//    sb.append(" ├──┼──┼──┼──┼──┼──┼──┼──┤\n");
//    sb.append(space);
//    sb.append("3│馬│砲│砲│卒│卒│卒│卒│卒│\n");
//    sb.append(space);
//    sb.append(" ├──┼──┼──┼──┼──┼──┼──┼──┤\n");
//    sb.append(space);
//    sb.append("2│●│●│●│●│●│●│●│●│\n");
//    sb.append(space);
//    sb.append(" ├──┼──┼──┼──┼──┼──┼──┼──┤\n");
//    sb.append(space);
//    sb.append("1│●│●│●│●│●│●│●│●│\n");
//    sb.append(space);
//    sb.append(" └──┴──┴──┴──┴──┴──┴──┴──┘\n");
//    sb.append(space);
//    sb.append("   1  2  3  4  5  6  7  8");
//    System.out.println(sb);


  }

//  private static void drawBoard() {
//
//    String[][] game = new String[4][8];
//    Random random = new Random();
//    StringBuilder sb = new StringBuilder();
//    String space = "          ";
//    sb.append("\n\n\n");
//    sb.append(space);
//    sb.append(" ┌──┬──┬──┬──┬──┬──┬──┬──┐\n");
//    for (int i = 0; i < 4; i++) {
//      for (int j = 0; j < 8; j++) {
//        String piece = chess.remove(random.nextInt(chess.size()));
//        piece = blue.contains(piece) ? "@|blue " + piece + "|@" : "@|red " + piece + "|@";
//        game[i][j] = piece;
//      }
//    }
//    for (int i = 0; i < 4; i++) {
//      sb.append(space).append(i).append("│");
//      for (int j = 0; j < 8; j++) {
//        sb.append(game[i][j]).append("│");
//      }
//      if(i<3){
//        sb.append("\n").append(space).append(" ├──┼──┼──┼──┼──┼──┼──┼──┤\n");
//      }
//      else{
//        sb.append("\n").append(space).append(" └──┴──┴──┴──┴──┴──┴──┴──┘\n").append(space).append("   0  1  2  3  4  5  6  7");
//      }
//    }
//    System.out.println(ansi().render(sb.toString()));
//  }

}
