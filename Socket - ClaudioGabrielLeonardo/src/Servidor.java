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

    // Parâmetros para testes
    private static int PORT = 9090;
    private static int n_clients = 2;
    private static int n_iteracoes = 10000;



    private static int n_matriz;
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

        // Lendo o arquivo
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

        // Colocando os pontos fixos na matriz inicial
        colocarPontoFixo();

        for (int i = 0; i < n_clients; i++) {
            partes_matriz_client.add(new int[(n_matriz / n_clients) - 1][(n_matriz * 3) - 6]);
            partesBoolean.add(false);
        }

        ExecutorService pool = Executors.newFixedThreadPool(n_clients);
        listener = new ServerSocket(PORT);

        // Fazendo a conexão com os clientes
        for (int i = 0; i < n_clients; i++) {
            System.out.println("Esperando Cliente " + (i + 1));
            Socket client = listener.accept();
            ClientHandler clientThread = new ClientHandler(client);

            clients.add(clientThread);
            System.out.println("Cliente " + (i + 1) + " adicionado!");

        }

        Collections.reverse(clients);

        // Iniciando a contagem de tempo
        tempoInicial = System.currentTimeMillis();

        for (ClientHandler c : clients) {
            pool.execute(c);
        }
        System.out.println("Iniciando cálculos...");

        // Looping até se acabar as iterações
        while (getCiclosTotal() != n_clients * n_iteracoes) {

            // Identificando se todas as partes já vieram, se sim, é feita a junção e o processo é refeito
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

        System.out.println("O método foi executado em " + (tempoExecucao) + " milisegundos");
        System.out.println("O método foi executado em " + (tempoExecucao / 1000) + " segundos");

        listener.close();

        // Salva a matriz final no arquivo
        File arquivo = new File("saidaSocket.dat");

        // Cria um arquivo (vazio)
        arquivo.createNewFile();
        // Cria um diretório
        arquivo.mkdir();

        // Construtor que recebe o objeto do tipo arquivo
        FileWriter fw = new FileWriter(arquivo);
        // Construtor que recebe também como argumento se o conteúdo será acrescentado

        // Construtor recebe como argumento o objeto do tipo FileWriter
        BufferedWriter bw = new BufferedWriter(fw);

        // Colocando a matriz no arquivo não considerando as bordas
        for (int i = 1; i < m.length - 1; i++) {
            for (int j = 1; j < m.length - 1; j++) {
                bw.write("< " + m[i][(3 * j)] + ", " + m[i][(3 * j) + 1] + ", " + m[i][(3 * j) + 2] + " >");
                if (j != m.length - 1) {
                    bw.write(" ");
                }
            }
            bw.newLine();
        }

        // Fecha os recursos
        bw.close();
        fw.close();

        System.out.println("Finalizado");

    }

    // Função para pegar o id (posição) de um cliente específico
    public static int getIndexClient(ClientHandler c) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).equals(c)) {
                return i;
            }
        }

        return -1;
    }

    // Função para ver o intervalo da matriz de um determinado cliente
    public static ArrayList<Integer> getIntervalos(ClientHandler c) {
        ArrayList<Integer> intervalos = new ArrayList<>();
        int pos = getIndexClient(c);

        intervalos.add(((n_matriz - 2) / n_clients) * pos);
        intervalos.add((((n_matriz - 2) / n_clients) * (pos + 1)) + 1);

        return intervalos;
    }

    // Função para obter a parte da matriz relativa a um determinado cliente
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

    // Função para colocar a parte da matriz gerada do cliente no arraylist das matrizes geradas
    public static void setParteMatriz(ClientHandler c, int[][] p) throws IOException {
        int index_client = getIndexClient(c);

        partes_matriz_client.set(index_client, p);

        partesBoolean.set(index_client, true);

    }

    // Função para colocar os pontos fixos na matriz principal
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

    // Função que vê se todas as partes ja foram calculadas
    public static boolean getPartes() {
        for (Boolean p : partesBoolean) {
            if (!p) {
                return false;
            }
        }

        return true;
    }

    // Função para ver se a parte de um determinado cliente ja foi calculada
    public static boolean getParteBoolean(ClientHandler c){
        int index_client = getIndexClient(c);
        return partesBoolean.get(index_client);
    }

    // Função para ver o total de ciclos (junta cada ciclo feito de cada cliente)
    public static int getCiclosTotal() {
        int total = 0;
        for (ClientHandler c : clients) {
            total = total + c.getCiclos();
        }

        return total;
    }

    // Função para pegar todas as partes geradas e criar a matriz principal
    public static void juntarMatrizes() {

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

    // Classe para fazer a comunicação entre cliente e servidor
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
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                } catch (IOException e1) {
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
