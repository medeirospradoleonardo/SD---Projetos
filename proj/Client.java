import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JOptionPane;

public class Client {

    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);

        // BufferedReader input = new BufferedReader(new
        // InputStreamReader(socket.getInputStream()));
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

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

        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        PontoRGB[][] matriz = (PontoRGB[][]) input.readObject();
        
        // int serverResponse = matriz[0][1].getValorR();
        // System.out.println(serverResponse);

        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeObject(calcularMatriz(matriz));
        output.flush();

        socket.close();
        System.exit(0);
    }

    public static PontoRGB[][] calcularMatriz(PontoRGB[][] m) {
        int colunas = m[0].length;
        int linhas = m.length;
        PontoRGB r[][] = new PontoRGB[linhas - 2][colunas - 2];
        for (int i = 1; i < linhas - 1; i++) {
            for (int j = 1; j < colunas - 1; j++) {
                int R = (m[i][j].getValorR() + m[i - 1][j].getValorR() + m[i + 1][j].getValorR()
                        + m[i][j - 1].getValorR() + m[i][j + 1].getValorR()) / 5;
                int G = (m[i][j].getValorG() + m[i - 1][j].getValorG() + m[i + 1][j].getValorG()
                        + m[i][j - 1].getValorG() + m[i][j + 1].getValorG()) / 5;
                int B = (m[i][j].getValorB() + m[i - 1][j].getValorB() + m[i + 1][j].getValorB()
                        + m[i][j - 1].getValorB() + m[i][j + 1].getValorB()) / 5;
                r[i-1][j-1] = new PontoRGB(R, G, B);
            }

        }

        return r;

    }
}
