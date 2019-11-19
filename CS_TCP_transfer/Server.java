import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    public static void main(String[] args) throws Exception {
        File targetFile = new File(args[0]);
        FileInputStream targetFileInput = new FileInputStream(targetFile);
        ArrayList<String[]> configInfo = readConfig(args[1]);


        boolean done = false;

        ServerSocket welcomeSocket = new ServerSocket(Integer.parseInt(configInfo.get(0)[1]));
        while (!done) {
            Socket connectionSocket = welcomeSocket.accept();

            System.out.println("Connected to client:" + connectionSocket.getRemoteSocketAddress() + " " + connectionSocket.getPort());

            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            BufferedOutputStream outToClient = new BufferedOutputStream(connectionSocket.getOutputStream());


            String inputString;
            String outputString;
            byte[] chunk = new byte[1024 * 10];
            int readSize;
            while ((inputString = inFromClient.readLine()) != null) {

                System.out.println("Receive from client:" + connectionSocket.getRemoteSocketAddress() + " " + connectionSocket.getPort() + ">");
                System.out.println(inputString);

                String[] inputSplit = inputString.split(" ");
                if (inputSplit[0].equals("REQS") && inputSplit[1].equals(args[0])) {
                    outputString = "OK " + ((int) Math.ceil(targetFile.length() / (1024 * 10.0))) + "\n";

                    Thread.sleep(1000);

                    System.out.println("Send to client:" + connectionSocket.getRemoteSocketAddress() + " " + connectionSocket.getPort() + ">");
                    System.out.println(outputString);

                    outToClient.write(outputString.getBytes());
                    outToClient.flush();

                    int chunkIndex = 0;
                    while ((readSize = targetFileInput.read(chunk)) != -1) {
                        Thread.sleep(1000);

                        System.out.println("Send to client:" + connectionSocket.getRemoteSocketAddress() + " " + connectionSocket.getPort() + ">");
                        System.out.println("chunk index " + chunkIndex);

                        outToClient.write(chunk, 0, readSize);
                        outToClient.flush();
                        ++chunkIndex;
                    }
                } else if (inputSplit[0].equals("QUIT")) {
                    System.out.println("Connection is terminated.");
                    connectionSocket.close();
                    done = true;
                    break;
                } else {
                    outputString = "FAIL";
                    Thread.sleep(1000);

                    System.out.println("Send to client:" + connectionSocket.getRemoteSocketAddress() + " " + connectionSocket.getPort() + ">");
                    System.out.println(outputString);
                }
            }
        }

        welcomeSocket.close();
        targetFileInput.close();
    }

    private static ArrayList<String[]> readConfig(String pathname) throws IOException {
        BufferedReader configFile = new BufferedReader(new FileReader(pathname));

        ArrayList<String[]> configInfo = new ArrayList<>(5);

        String line = configFile.readLine();
        while (line != null) {
            configInfo.add(line.split(" ", 2));
            line = configFile.readLine();
        }

        return configInfo;
    }
}
