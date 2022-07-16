import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);

        // BufferedReader input = new BufferedReader(new
        // InputStreamReader(socket.getInputStream()));
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

        // while(true){
        // System.out.println("> ");
        // String command = keyboard.readLine();

        // if(command.equals("quit")) break;

        // out.println(command);

        // String serverResponse = input.readLine();
        // String serverResponse2 = input.readLine();
        // System.out.println("Server says: " + serverResponse);
        // System.out.println("Server says: " + serverResponse2);
        // }

        while (true) {
            int[][] matriz = (int[][]) input.readObject();

            // int serverResponse = matriz[0][1].getValorR();
            // System.out.println(serverResponse);

            output.writeObject(calcularMatriz(matriz));
            output.flush();

        }

        // socket.close();
        // System.exit(0);
    }

    public static int[][] calcularMatriz(int[][] m) {
        int linhas = m.length;
        int colunas = m[0].length;
        int m_r[][] = new int[linhas - 2][colunas - 6];
        for (int i = 1; i < linhas - 1; i++) {
            for (int j = 1; j < colunas / 3 - 1; j++) {
                int r = (m[i][(3 * j)] + m[i - 1][(3 * j)] + m[i + 1][(3 * j)]
                        + m[i][(3 * (j - 1))] + m[i][(3 * (j + 1))]) / 5;
                int g = (m[i][(3 * j) + 1] + m[i - 1][(3 * j) + 1] + m[i + 1][(3 * j) + 1]
                        + m[i][(3 * (j - 1)) + 1] + m[i][(3 * (j + 1)) + 1]) / 5;
                int b = (m[i][(3 * j) + 2] + m[i - 1][(3 * j) + 2] + m[i + 1][(3 * j) + 2]
                        + m[i][(3 * (j - 1)) + 2] + m[i][(3 * (j + 1)) + 2]) / 5;
                m_r[i - 1][(3 * (j - 1))] = r;
                m_r[i - 1][(3 * (j - 1)) + 1] = g;
                m_r[i - 1][(3 * (j - 1)) + 2] = b;
            }

        }

        return m_r;

    }
}
