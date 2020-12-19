import org.fusesource.jansi.AnsiConsole;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

  static String blueTeam = "將士象車馬砲卒";
  static String redTeam = "帥仕相硨傌炮兵";
  static List<String> chess = new ArrayList<>(Arrays.asList(
      "將", "士", "士", "象", "象", "車", "車", "馬", "馬", "砲", "砲", "卒", "卒", "卒", "卒", "卒",
      "帥", "仕", "仕", "相", "相", "硨", "硨", "傌", "傌", "炮", "炮", "兵", "兵", "兵", "兵", "兵"
  ));
  static String[][] game = new String[4][8];
  static Random random = new Random();

  static Map<String, Team> teamMap = new HashMap<>();

  public static void main(String args[]) throws IOException {
    AnsiConsole.systemInstall();
    ServerSocket server = new ServerSocket(5000);
    initBoard();

    Socket c1 = null;
    DataOutputStream c1_out = null;
    DataInputStream c1_input = null;

    Socket c2 = null;
    DataOutputStream c2_out = null;
    DataInputStream c2_input = null;

    try {
      System.out.println("等待玩家 1...");
      c1 = server.accept();
      c1_out = new DataOutputStream(c1.getOutputStream());
      c1_input = new DataInputStream(new BufferedInputStream(c1.getInputStream()));
      c1_out.writeUTF(drawGameBoard());
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
      String[] moves;
      while (!c1_say.equalsIgnoreCase("quit") && !c2_say.equalsIgnoreCase("quit")) {
        writeGame(c1_out);
//        c1_out.writeUTF("AA"); // token
        boolean valid1 = false;
        boolean valid2 = false;
        while (!valid1) {
          c1_say = c1_input.readUTF();
          moves = c1_say.split(",");
          move = moves[0];
          where[0] = moves[1];
          where[1] = moves[2];
          target[0] = moves[3];
          target[1] = moves[4];
          if ("1".equals(move)) {
            valid1 = moveOrAttack("c1", Integer.parseInt(where[0]), Integer.parseInt(where[1]), Integer.parseInt(target[0]), Integer.parseInt(target[1]));
          } else if ("2".equals(move)) {
            valid1 = openChess(Integer.parseInt(where[0]), Integer.parseInt(where[1]));
          }
          c1_out.writeBoolean(valid1);
        }
        c1_out.writeUTF(drawGameBoard());
        writeGame(c1_out);
        c2_out.writeUTF(drawGameBoard());
        // ----------------------------------
//        c2_out.writeUTF("BB"); // token
        writeGame(c2_out);
        while (!valid2) {
          c2_say = c2_input.readUTF();
          moves = c2_say.split(",");
          move = moves[0];
          where[0] = moves[1];
          where[1] = moves[2];
          target[0] = moves[3];
          target[1] = moves[4];
          if ("1".equals(move)) {
            valid2 = moveOrAttack("c2", Integer.parseInt(where[0]), Integer.parseInt(where[1]), Integer.parseInt(target[0]), Integer.parseInt(target[1]));
          } else if ("2".equals(move)) {
            valid2 = openChess(Integer.parseInt(where[0]), Integer.parseInt(where[1]));
          }
          c2_out.writeBoolean(valid2);
        }
        c1_out.writeUTF(drawGameBoard());
        c2_out.writeUTF(drawGameBoard());
        writeGame(c2_out);
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

  private static void initBoard() {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 8; j++) {
        game[i][j] = "●";
      }
    }
  }

  private static void writeGame(DataOutputStream out) throws IOException {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 8; j++) {
        out.writeUTF(game[i][j]);
      }
    }
  }

  private static String drawGameBoard() {
    StringBuilder sb = new StringBuilder();
    String space = "          ";
    sb.append("\n\n\n");
    sb.append(space);
    sb.append(" ┌──┬──┬──┬──┬──┬──┬──┬──┐\n");
    for (int i = 0; i < 4; i++) {
      sb.append(space).append(i).append("│");
      for (int j = 0; j < 8; j++) {
        sb.append(game[i][j]).append("│");
      }
      if (i < 3) {
        sb.append("\n").append(space).append(" ├──┼──┼──┼──┼──┼──┼──┼──┤\n");
      } else {
        sb.append("\n").append(space).append(" └──┴──┴──┴──┴──┴──┴──┴──┘\n").append(space).append("   0  1  2  3  4  5  6  7");
      }
    }
    return sb.toString();
  }


  private static boolean openChess(int x, int y) {
    String target = game[x][y];
    if ("●".equals(target)) {
      String piece = chess.remove(random.nextInt(chess.size()));

      boolean isBlue = blueTeam.contains(piece);
      piece = isBlue ? "@|blue " + piece + "|@" : "@|red " + piece + "|@";
      game[x][y] = piece;

      if (teamMap.size() == 0) {
        if (isBlue) {
          teamMap.put("c1", Team.BLUE);
          teamMap.put("c2", Team.RED);
        } else {
          teamMap.put("c1", Team.RED);
          teamMap.put("c2", Team.BLUE);
        }
      }
      return true;
    }
    System.out.println("不可翻牌");
    return false;
  }

  private static boolean moveOrAttack(String who, int x, int y, int target_x, int target_y) {
    Team team = teamMap.get(who);
    String origin_piece = game[x][y];
    String piece = parseString(origin_piece);
    String origin_target = game[target_x][target_y];
    String target = parseString(origin_target);

    if ((blueTeam.contains(piece) && team == Team.BLUE) || (redTeam.contains(piece) && team == Team.RED)) { /** 不是自己的棋子 */
      if ("●".equals(target) || (team == Team.BLUE && blueTeam.contains(target))
          || (team == Team.RED && redTeam.contains(target))) { /** 目標是沒翻開的棋或是自己的棋 */
        System.out.println("不可以移動到這");
        return false;
      } else {
        if ("　".equals(target)) {
          game[x][y] = "　";
          game[target_x][target_y] = origin_piece;
          return true;
        } else {
          if (team == Team.BLUE) {
            switch (piece) {
              case "將":
                if ("帥仕相硨傌炮".contains(target)) {
                  game[x][y] = "　";
                  game[target_x][target_y] = origin_piece;
                  return true;
                } else {
                  System.out.println("牌太小，吃不了");
                  return false;
                }
              case "士":
                if ("仕相硨傌炮兵".contains(target)) {
                  game[x][y] = "　";
                  game[target_x][target_y] = origin_piece;
                  return true;
                } else {
                  System.out.println("牌太小，吃不了");
                  return false;
                }
              case "象":
                if ("相硨傌炮兵".contains(target)) {
                  game[x][y] = "　";
                  game[target_x][target_y] = origin_piece;
                  return true;
                } else {
                  System.out.println("牌太小，吃不了");
                  return false;
                }
              case "車":
                if ("硨傌炮兵".contains(target)) {
                  game[x][y] = "　";
                  game[target_x][target_y] = origin_piece;
                  return true;
                } else {
                  System.out.println("牌太小，吃不了");
                  return false;
                }
              case "馬":
                if ("傌炮兵".contains(target)) {
                  game[x][y] = "　";
                  game[target_x][target_y] = origin_piece;
                  return true;
                } else {
                  System.out.println("牌太小，吃不了");
                  return false;
                }
              case "卒":
                if ("帥兵".contains(target)) {
                  game[x][y] = "　";
                  game[target_x][target_y] = origin_piece;
                  return true;
                } else {
                  System.out.println("牌太小，吃不了");
                  return false;
                }
            }
            return false;
          } else {
            switch (piece) {
              case "帥":
                if ("將士象車馬砲".contains(target)) {
                  game[x][y] = "　";
                  game[target_x][target_y] = origin_piece;
                  return true;
                } else {
                  System.out.println("牌太小，吃不了");
                  return false;
                }
              case "仕":
                if ("士象車馬砲卒".contains(target)) {
                  game[x][y] = "　";
                  game[target_x][target_y] = origin_piece;
                  return true;
                } else {
                  System.out.println("牌太小，吃不了");
                  return false;
                }
              case "相":
                if ("象車馬砲卒".contains(target)) {
                  game[x][y] = "　";
                  game[target_x][target_y] = origin_piece;
                  return true;
                } else {
                  System.out.println("牌太小，吃不了");
                  return false;
                }
              case "硨":
                if ("車馬砲卒".contains(target)) {
                  game[x][y] = "　";
                  game[target_x][target_y] = origin_piece;
                  return true;
                } else {
                  System.out.println("牌太小，吃不了");
                  return false;
                }
              case "傌":
                if ("馬砲卒".contains(target)) {
                  game[x][y] = "　";
                  game[target_x][target_y] = origin_piece;
                  return true;
                } else {
                  System.out.println("牌太小，吃不了");
                  return false;
                }
              case "兵":
                if ("將卒".contains(target)) {
                  game[x][y] = "　";
                  game[target_x][target_y] = origin_piece;
                  return true;
                } else {
                  System.out.println("牌太小，吃不了");
                  return false;
                }
            }
            return false;
          }
        }
      }
    }
    System.out.println("這不是你的棋子");
    return false;

  }


  private static String parseString(String input) {
    if (input.length() == 1) return input;
    return input.split(" ")[1].substring(0, 1);
  }

  enum Team {

    BLUE, RED
  }


}
