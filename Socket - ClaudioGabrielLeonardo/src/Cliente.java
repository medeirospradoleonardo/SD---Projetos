
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Cliente {

    private static String SERVER_IP = "127.0.0.1";
    private static int SERVER_PORT = 9090;

    public static void main(String[] args) throws UnknownHostException, IOException, ClassNotFoundException {
        Socket socket = new Socket(SERVER_IP, SERVER_PORT);

        ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

        while (true) {
            // Recebe a parte da matriz para calcular e retorna a matriz resultante para o servidor
            output.writeObject(calcularMatriz((int[][]) input.readObject()));
            output.flush();
            output.reset();
        }
    }


    // Função para fazer o cálculo da parte vinda do servidor utilizando estêncil
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
