import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    public static void main(String[] args) throws Exception {
        ArrayList<String[]> configInfo = readConfig(args[1]);
        Socket socket = new Socket(configInfo.get(1)[0], Integer.parseInt(configInfo.get(1)[1]));

        System.out.println("Connected to server:" + socket.getInetAddress() + " " + socket.getPort());


        BufferedInputStream inFromServer = new BufferedInputStream(socket.getInputStream());
        BufferedWriter outToServer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));


        String outputString = "REQS " + args[0] + "\n";
        byte[] inputBuffer = new byte[1024 * 10];

        Thread.sleep(1000);

        System.out.println("Send to server:" + socket.getInetAddress() + " " + socket.getPort() + ">");
        System.out.println(outputString);

        outToServer.write(outputString);
        outToServer.flush();

        inFromServer.read(inputBuffer, 0, inputBuffer.length);

        String inputString = new String(inputBuffer);
        String[] inputSplit = inputString.split(" ");

        System.out.println("Receive from server:" + socket.getInetAddress() + " " + socket.getPort() + ">");
        System.out.println(inputString);

        if (inputSplit[0].equals("OK")) {
            int numOfChunk = Integer.parseInt(inputSplit[1].trim());

            FileOutputStream fileOut = new FileOutputStream(args[0]);

            for (int i = 0; i < numOfChunk; ++i) {
                inFromServer.read(inputBuffer, 0, inputBuffer.length);
                fileOut.write(inputBuffer);

                System.out.println("Receive from server:" + socket.getInetAddress() + " " + socket.getPort() + ">");
                System.out.println("chunk index " + i);
            }

            fileOut.close();
        }

        Thread.sleep(1000);

        outputString = "QUIT\n";
        outToServer.write(outputString);
        outToServer.flush();

        System.out.println("Send to server:" + socket.getInetAddress() + " " + socket.getPort() + ">");
        System.out.println(outputString);


        socket.close();
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
