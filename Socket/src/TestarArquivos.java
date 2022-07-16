import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;

public class TestarArquivos {

    public static Boolean getIgualdadeArquivos(ArrayList<PontoRGB> pontos_arq1, ArrayList<PontoRGB> pontos_arq2){
        for(int i=0; i<pontos_arq1.size(); i++){
            if(!(pontos_arq1.get(i).getValorR() == pontos_arq2.get(i).getValorR()) || !(pontos_arq1.get(i).getValorG() == pontos_arq2.get(i).getValorG()) || !(pontos_arq1.get(i).getValorB() == pontos_arq2.get(i).getValorB())){
                return false;
            }
        }

        return true;
    }
    public static void main(String[] args) throws FileNotFoundException {
        int n = 256;

        Scanner in1 = new Scanner(new FileReader("saida.dat"));
        Scanner in2 = new Scanner(new FileReader("saidaNossa.dat"));

        ArrayList<PontoRGB> pontos_arq1 = new ArrayList<>();
        ArrayList<PontoRGB> pontos_arq2 = new ArrayList<>();
        String linha;
        String[] ponto;
        int r;
        int g;
        int b;

        // Alimentando o array referente ao arquivo 1
        while (in1.hasNextLine()) {
            linha = in1.nextLine();

            for(int i=0; i<n; i++){
                linha = linha.replaceAll(" ", "");
                ponto = linha.split(">")[i].replaceAll("[\\D]", " ").split(" ");

                r = Integer.parseInt(ponto[1]);
                g = Integer.parseInt(ponto[2]);
                b = Integer.parseInt(ponto[3]);

                pontos_arq1.add(new PontoRGB(r, g, b));
            }
        }

        // Alimentando o array referente ao arquivo 2
        while (in2.hasNextLine()) {
            linha = in2.nextLine();

            for(int i=0; i<n; i++){
                linha = linha.replaceAll(" ", "");
                ponto = linha.split(">")[i].replaceAll("[\\D]", " ").split(" ");

                r = Integer.parseInt(ponto[1]);
                g = Integer.parseInt(ponto[2]);
                b = Integer.parseInt(ponto[3]);

                pontos_arq2.add(new PontoRGB(r, g, b));
            }
        }

        if(getIgualdadeArquivos(pontos_arq1, pontos_arq2)){
            System.out.println("Arquivos iguais");
        }else{
            System.out.println("Arquivos diferentes");
        }
    }
}
