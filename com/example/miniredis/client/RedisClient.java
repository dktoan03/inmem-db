package com.example.miniredis.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class RedisClient {
    private static final String HOST = "localhost";
    private static final int PORT = 6379;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
             Scanner scanner = new Scanner(System.in)) {

            System.out.println(in.readLine());

            while (true) {
                System.out.print("> ");
                String command = scanner.nextLine();
                if (command.equalsIgnoreCase("exit")) break;

                out.write(command + "\n");
                out.flush();

                String response = in.readLine();
                System.out.println(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}