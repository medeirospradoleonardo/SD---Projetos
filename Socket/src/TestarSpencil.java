import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class TestarSpencil {

    public static PontoRGB[][] calcularMatriz(PontoRGB[][] m) {
        int colunas = m[0].length;
        int linhas = m.length;
        PontoRGB r[][] = new PontoRGB[linhas][colunas];
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                if ((i - 1 >= 0) && (i + 1 < linhas) && (j - 1 >= 0) && (j + 1 < colunas)) {
                    int R = (m[i][j].getValorR() + m[i - 1][j].getValorR() + m[i + 1][j].getValorR()
                            + m[i][j - 1].getValorR() + m[i][j + 1].getValorR()) / 5;
                    int G = (m[i][j].getValorG() + m[i - 1][j].getValorG() + m[i + 1][j].getValorG()
                            + m[i][j - 1].getValorG() + m[i][j + 1].getValorG()) / 5;
                    int B = (m[i][j].getValorB() + m[i - 1][j].getValorB() + m[i + 1][j].getValorB()
                            + m[i][j - 1].getValorB() + m[i][j + 1].getValorB()) / 5;
                    r[i][j] = new PontoRGB(R, G, B);
                } else {
                    r[i][j] = new PontoRGB(m[i][j].getValorR(), m[i][j].getValorG(), m[i][j].getValorB());
                }
            }
        }

        return r;

    }

    public static void main(String[] args) throws IOException {

        Scanner in = new Scanner(new FileReader("dados.txt"));
        int n_matriz = 0;
        int n_ponto_fixos = 0;
        int qtd = 0;
        ArrayList<String> pontos = new ArrayList<>();
        while (in.hasNextLine()) {
            String linha = in.nextLine();

            if(qtd == 0){
                n_matriz = Integer.parseInt(linha.split(" ")[0]);
                n_ponto_fixos = Integer.parseInt(linha.split(" ")[1]);
            }else{
                if(qtd-1 != n_ponto_fixos){
                    pontos.add(linha);
                }
            }

            qtd += 1;
        }

        System.out.println(pontos.size());

        PontoRGB m[][] = new PontoRGB[n_matriz][n_matriz];
        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                if ((i - 1 >= 0) && (i + 1 < m.length) && (j - 1 >= 0) && (j + 1 < m[0].length)) {
                    m[i][j] = new PontoRGB(0, 0, 0);
                } else {
                    m[i][j] = new PontoRGB(127, 127, 127);
                }
            }
        }

        File arquivo = new File("teste.txt");

        // cria um arquivo (vazio)
        arquivo.createNewFile();
        // cria um diretório
        arquivo.mkdir();

        // construtor que recebe o objeto do tipo arquivo
        FileWriter fw = new FileWriter(arquivo);
        // construtor que recebe também como argumento se o conteúdo será acrescentado

        // construtor recebe como argumento o objeto do tipo FileWriter
        BufferedWriter bw = new BufferedWriter(fw);

        for (int i = 0; i < m.length; i++) {
            for (int j = 0; j < m[0].length; j++) {
                bw.write("< " + m[i][j].getValorR() + ", " + m[i][j].getValorG() + ", " + m[i][j].getValorB() + " >");
                if (j != m[0].length - 1) {
                    bw.write(" ");
                }
            }
            bw.newLine();
        }

        // fecha os recursos
        bw.close();
        fw.close();

    }
}
