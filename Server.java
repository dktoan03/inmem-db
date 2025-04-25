import java.util.Scanner;

public class Server {
  public static void main(String[] args) {
    DataStore dataStore = new DataStore();
    CommandProcessor processor = new CommandProcessor(dataStore);
    Scanner scanner = new Scanner(System.in);

    System.out.println("MiniRedis Java Server Started. Type commands:");

    while (true) {
      System.out.print("> ");
      String line = scanner.nextLine();
      if (line.equalsIgnoreCase("exit"))
        break;
      String response = processor.process(line);
      System.out.println(response);
    }

    scanner.close();
    System.out.println("MiniRedis Server Shutdown.");
  }
}
