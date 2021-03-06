import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {

    private static int PORT = 9090;
    private static int n_clients = 2;
    private static int n_iteracoes = 100;
    private static int n_matriz = 256;
    private static int[][] m;
    private static ArrayList<String> pontos = new ArrayList<>();
    private static ArrayList<ClientHandler> clients = new ArrayList<>();
    private static ArrayList<int[][]> partes_matriz_client = new ArrayList<>();
    private static ArrayList<Boolean> partesBoolean = new ArrayList<>();
    private static ServerSocket listener;
    private static long tempoInicial;
    private static long tempoFinal;

    public static void main(String[] args) throws ClassNotFoundException, IOException {

        Scanner in = new Scanner(new FileReader("img01.dat"));
        n_matriz = 0;
        int n_ponto_fixos = 0;
        int qtd = 0;
        while (in.hasNextLine()) {
            String linha = in.nextLine();

            if (qtd == 0) {
                n_matriz = Integer.parseInt(linha.split(" ")[0]) + 2;
                n_ponto_fixos = Integer.parseInt(linha.split(" ")[1]);
            } else {
                if (qtd - 1 != n_ponto_fixos) {
                    pontos.add(linha);
                }
            }

            qtd += 1;
        }

        // Criando a matriz
        m = new int[n_matriz][n_matriz * 3];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < n_matriz; j++) {
                if ((i - 1 >= 0) && (i + 1 < m.length) && ((3 * (j - 1)) >= 0) && ((3 * (j + 1)) < m[0].length)) {
                    m[i][(3 * j)] = 0;
                    m[i][(3 * j) + 1] = 0;
                    m[i][(3 * j) + 2] = 0;
                } else {
                    m[i][(3 * j)] = 127;
                    m[i][(3 * j) + 1] = 127;
                    m[i][(3 * j) + 2] = 127;
                }
            }
        }

        colocarPontoFixo();

        for (int i = 0; i < n_clients; i++) {
            partes_matriz_client.add(new int[(n_matriz / n_clients) - 1][(n_matriz * 3) - 6]);
            partesBoolean.add(false);
        }

        ExecutorService pool = Executors.newFixedThreadPool(n_clients);
        listener = new ServerSocket(PORT);

        for (int i = 0; i < n_clients; i++) {
            System.out.println("Esperando Cliente " + (i + 1));
            Socket client = listener.accept();
            ClientHandler clientThread = new ClientHandler(client);

            clients.add(clientThread);
            System.out.println("Cliente " + (i + 1) + " adicionado!");

        }

        Collections.reverse(clients);
        for (ClientHandler c : clients) {
            pool.execute(c);
        }
        // pool.execute((Runnable) clients);
        tempoInicial = System.currentTimeMillis();
        System.out.println("Iniciando c??lculos...");

        while (getCiclosTotal() != n_clients * n_iteracoes) {
            if (getPartes()) {
                juntarMatrizes();
                colocarPontoFixo();
                for(int i=0; i<partesBoolean.size(); i++){
                    partesBoolean.set(i, false);
                }
                for (ClientHandler cc : clients) {
                    cc.setCiclos(cc.getCiclos() + 1);
                }
            }
        }

        colocarPontoFixo();

        tempoFinal = System.currentTimeMillis();

        Long tempoExecucao = tempoFinal - tempoInicial;

        System.out.println("O m??todo foi executado em " + (tempoExecucao) + " milisegundos");
        System.out.println("O m??todo foi executado em " + (tempoExecucao / 1000) + " segundos");

        listener.close();

        File arquivo = new File("final.dat");

        // cria um arquivo (vazio)
        arquivo.createNewFile();
        // cria um diret??rio
        arquivo.mkdir();

        // construtor que recebe o objeto do tipo arquivo
        FileWriter fw = new FileWriter(arquivo);
        // construtor que recebe tamb??m como argumento se o conte??do ser?? acrescentado

        // construtor recebe como argumento o objeto do tipo FileWriter
        BufferedWriter bw = new BufferedWriter(fw);

        // Colocando a matriz no arquivo n??o considerando as bordas
        for (int i = 1; i < m.length - 1; i++) {
            for (int j = 1; j < m.length - 1; j++) {
                bw.write("< " + m[i][(3 * j)] + ", " + m[i][(3 * j) + 1] + ", " + m[i][(3 * j) + 2] + " >");
                if (j != m.length - 1) {
                    bw.write(" ");
                }
            }
            bw.newLine();
        }

        // fecha os recursos
        bw.close();
        fw.close();

        System.out.println("Finalizado");

    }

    public static int getIndexClient(ClientHandler c) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).equals(c)) {
                return i;
            }
        }

        return -1;
    }

    public static ArrayList<Integer> getIntervalos(ClientHandler c) {
        ArrayList<Integer> intervalos = new ArrayList<>();
        int pos = getIndexClient(c);

        intervalos.add(((n_matriz - 2) / n_clients) * pos);
        intervalos.add((((n_matriz - 2) / n_clients) * (pos + 1)) + 1);

        return intervalos;
    }

    public static int[][] getParteMatriz(ClientHandler c) {
        ArrayList<Integer> intervalos = getIntervalos(c);
        int n_linhas = Math.abs(intervalos.get(0) - intervalos.get(1)) + 1;
        int p_m[][] = new int[n_linhas][n_matriz * 3];
        int x = 0;

        for (int i = intervalos.get(0); i <= intervalos.get(1); i++) {
            for (int j = 0; j < n_matriz; j++) {
                p_m[x][(3 * j)] = m[i][(3 * j)];
                p_m[x][(3 * j) + 1] = m[i][(3 * j) + 1];
                p_m[x][(3 * j) + 2] = m[i][(3 * j) + 2];
            }
            x++;
        }

        return p_m;
    }

    public static void setParteMatriz(ClientHandler c, int[][] p) throws IOException {
        int index_client = getIndexClient(c);

        partes_matriz_client.set(index_client, p);

        partesBoolean.set(index_client, true);

    }

    public static void colocarPontoFixo() {
        int x = 0;
        int y = 0;
        int r = 0;
        int b = 0;
        int g = 0;
        for (String p : pontos) {
            x = Integer.parseInt(p.split(" ")[0]);
            y = Integer.parseInt(p.split(" ")[1]);
            r = Integer.parseInt(p.split(" ")[2]);
            g = Integer.parseInt(p.split(" ")[3]);
            b = Integer.parseInt(p.split(" ")[4]);

            m[x][(3 * y)] = r;
            m[x][(3 * y) + 1] = g;
            m[x][(3 * y) + 2] = b;
        }
    }

    public static boolean getPartes() {
        for (Boolean p : partesBoolean) {
            if (!p) {
                return false;
            }
        }

        return true;
    }

    public static boolean getParteBoolean(ClientHandler c){
        int index_client = getIndexClient(c);
        return partesBoolean.get(index_client);
    }

    public static int getCiclosTotal() {
        int total = 0;
        for (ClientHandler c : clients) {
            total = total + c.getCiclos();
        }

        return total;
    }

    public static void juntarMatrizes() {
        // int r[][]= new int[n_matriz][n_matriz*3];

        int linha_r = 1;
        int coluna_r = 1;

        for (int i = 0; i < n_matriz; i++) {
            m[i][0] = 127;
            m[i][0 + 1] = 127;
            m[i][0 + 2] = 127;

            m[i][(3 * (n_matriz - 1))] = 127;
            m[i][(3 * (n_matriz - 1)) + 1] = 127;
            m[i][(3 * (n_matriz - 1)) + 2] = 127;

            m[0][(3 * i)] = 127;
            m[0][(3 * i) + 1] = 127;
            m[0][(3 * i) + 2] = 127;

            m[n_matriz - 1][(3 * i)] = 127;
            m[n_matriz - 1][(3 * i) + 1] = 127;
            m[n_matriz - 1][(3 * i) + 2] = 127;
        }

        for (int[][] p : partes_matriz_client) {
            for (int i = 0; i < p.length; i++) {
                for (int j = 0; j < p[0].length / 3; j++) {
                    m[linha_r][(3 * coluna_r)] = p[i][(3 * j)];
                    m[linha_r][(3 * coluna_r) + 1] = p[i][(3 * j) + 1];
                    m[linha_r][(3 * coluna_r) + 2] = p[i][(3 * j) + 2];
                    coluna_r++;
                }
                coluna_r = 1;
                linha_r++;
            }
        }
    }

    public static class ClientHandler implements Runnable {

        private Socket client;
        private ObjectOutputStream out;
        private ObjectInputStream in;
        private int ciclos = 0;

        public ClientHandler(Socket clientSocket) throws IOException {
            this.client = clientSocket;
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (!getParteBoolean(this)) {
                        out.writeObject(getParteMatriz(this));
                        out.flush();
                        setParteMatriz(this, (int[][]) in.readObject());
                        out.reset();
                    }
                }
            } catch (IOException e) {
                System.err.println("IO exception");
                System.err.println(e.getStackTrace());
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        public Socket getClient() {
            return this.client;
        }

        public int getCiclos() {
            return this.ciclos;
        }

        public void setCiclos(int ciclos) {
            this.ciclos = ciclos;
        }
    }
}
