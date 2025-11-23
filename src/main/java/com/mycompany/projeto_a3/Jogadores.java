package com.mycompany.projeto_a3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public abstract class Jogadores {
    public String nome;
    protected int frotas[];
    MeuTabuleiro inimigo;
    MeuTabuleiro tabuleiro;

    public Jogadores(String nome, MeuTabuleiro inimigo, MeuTabuleiro tabuleiro) {
        this.nome = nome;
        this.frotas = new int[]{2, 3, 4};
        this.tabuleiro = tabuleiro;
        this.inimigo = inimigo;
    }

    abstract void ataque();
    abstract void posicao();
}

// ============================ JOGADOR HUMANO ============================

class Jogador1 extends Jogadores {

    public Jogador1(String nome, MeuTabuleiro inimigo, MeuTabuleiro tabuleiro) {
        super(nome, inimigo, tabuleiro);
    }

    @Override
    void posicao() {
        Scanner tecla = new Scanner(System.in);
        System.out.println("Vamos posicionar as frotas!");
        System.out.println("Essas sao as suas frotas: " + frotas.length + " navios para posicionar.");

        for (int i = 0; i < frotas.length; i++) {
            int tamanhoN = frotas[i];
            System.out.println("\nPosicione o navio de tamanho " + tamanhoN);

            for (int j = 0; j < tamanhoN; j++) {
                boolean posicaoValida = false;
                String entrada;

                do {
                    System.out.println("Digite a coordenada " + (j + 1) + " (Ex: A1, B5):");
                    entrada = tecla.nextLine().toUpperCase().trim();

                    // valida formato Letra+Número
                    if (!entrada.matches("^[A-J](10|[1-9])$")) {
                        System.out.println("Formato invalido! Use formato Letra+Numero (ex: A1, B10).");
                        continue;
                    }

                    int linha = entrada.charAt(0) - 'A';
                    int coluna = Integer.parseInt(entrada.substring(1)) - 1;

                    if (linha < 0 || linha >= tabuleiro.gettamanhoX() || coluna < 0 || coluna >= tabuleiro.gettamanhoY()) {
                        System.out.println("Posicao fora do limite!");
                        continue;
                    }

                    if (tabuleiro.posicaoDisponivel(linha, coluna)) {
                        tabuleiro.getcampo()[linha][coluna] = 'N';
                        posicaoValida = true;
                    } else {
                        System.out.println("Posicao ja ocupada. Digite novamente.");
                    }

                } while (!posicaoValida);
            }
        }
    }

    @Override
    void ataque() {
        Scanner tecla = new Scanner(System.in);
        boolean tiroValido = false;

        while (!tiroValido) {
            System.out.println("Digite a coordenada do ataque (Ex: A1, B5):");
            String entrada = tecla.nextLine().toUpperCase().trim();

            if (!entrada.matches("^[A-J](10|[1-9])$")) {
                System.out.println("Formato invalido! Use formato Letra+Número (ex: A1, B10).");
                continue;
            }

            int linha = entrada.charAt(0) - 'A';
            int coluna = Integer.parseInt(entrada.substring(1)) - 1;

            if (linha < 0 || linha >= inimigo.gettamanhoX() || coluna < 0 || coluna >= inimigo.gettamanhoY()) {
                System.out.println("Posicao fora do limite!");
                continue;
            }

            char valorAtual = inimigo.getcampo()[linha][coluna];

            if (valorAtual == 'X' || valorAtual == 'O') {
                System.out.println("Posicao ja atacada!");
                continue;
            }

            if (valorAtual == 'N') {
                System.out.println(" Acertou um navio!");
                inimigo.getcampo()[linha][coluna] = 'X';
            } else {
                System.out.println(" Acertou a agua!");
                inimigo.getcampo()[linha][coluna] = 'O';
            }

            tiroValido = true;
        }
    }
}

class Maquina extends Jogadores {

    private int dificuldade;
    private Banco_Do_Jogo banco = new Banco_Do_Jogo();

    public Maquina(String nome, MeuTabuleiro inimigo, MeuTabuleiro tabuleiro) {
        super(nome, inimigo, tabuleiro);
    }

    public void setDificuldade(int dificuldade) {
        this.dificuldade = dificuldade;
    }

    @Override
    void posicao() {
        System.out.println("Posicionando frotas da maquina...");

        for (int i = 0; i < frotas.length; i++) {
            int tamanhoNavio = frotas[i];
            boolean posicionado = false;

            while (!posicionado) {
                int linha = (int) (Math.random() * tabuleiro.gettamanhoX());
                int coluna = (int) (Math.random() * tabuleiro.gettamanhoY());
                boolean horizontal = Math.random() < 0.5;

                if (veriEspa(tabuleiro, linha, coluna, tamanhoNavio, horizontal)) {
                    if (horizontal) {
                        for (int j = 0; j < tamanhoNavio; j++)
                            tabuleiro.getcampo()[linha][coluna + j] = 'N';
                    } else {
                        for (int j = 0; j < tamanhoNavio; j++)
                            tabuleiro.getcampo()[linha + j][coluna] = 'N';
                    }
                    posicionado = true;
                }
            }
        }
    }

    @Override
    void ataque() {

        int linha = -1, coluna = -1;

     

        switch (dificuldade) {

          
            case 1: {
                Random rnd = new Random();
                int tent = 0;
                do {
                    linha = rnd.nextInt(inimigo.gettamanhoX());
                    coluna = rnd.nextInt(inimigo.gettamanhoY());
                    tent++;
                    if (tent > 1000) break; // segurança
                } while (inimigo.getcampo()[linha][coluna] == 'X' || inimigo.getcampo()[linha][coluna] == 'O');
                break;
            }

          
            case 2: {
                boolean achou = false;

                
                for (int i = 0; i < inimigo.gettamanhoX() && !achou; i++) {
                    for (int j = 0; j < inimigo.gettamanhoY() && !achou; j++) {
                        if (inimigo.getcampo()[i][j] == 'X') {
                            int[][] adj = {{i - 1, j}, {i, j + 1}, {i + 1, j}, {i, j - 1}};
                            for (int[] p : adj) {
                                int li = p[0], co = p[1];
                                if (li >= 0 && li < inimigo.gettamanhoX() && co >= 0 && co < inimigo.gettamanhoY()) {
                                    char c = inimigo.getcampo()[li][co];
                                    if (c != 'X' && c != 'O') {
                                        linha = li;
                                        coluna = co;
                                        achou = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (!achou) {
                    
                    for (int i = 0; i < inimigo.gettamanhoX() && !achou; i++) {
                        for (int j = 0; j < inimigo.gettamanhoY() && !achou; j++) {
                            if ((i + j) % 2 == 0) {
                                char c = inimigo.getcampo()[i][j];
                                if (c != 'X' && c != 'O') {
                                    linha = i;
                                    coluna = j;
                                    achou = true;
                                }
                            }
                        }
                    }
                }

               
                if (!achou) {
                    outer:
                    for (int i = 0; i < inimigo.gettamanhoX(); i++) {
                        for (int j = 0; j < inimigo.gettamanhoY(); j++) {
                            char c = inimigo.getcampo()[i][j];
                            if (c != 'X' && c != 'O') {
                                linha = i;
                                coluna = j;
                                break outer;
                            }
                        }
                    }
                }
                break;
            }

  
            case 3: {
                boolean jogadaDefinida = false;

               
                outerTarget:
                for (int i = 0; i < inimigo.gettamanhoX() && !jogadaDefinida; i++) {
                    for (int j = 0; j < inimigo.gettamanhoY() && !jogadaDefinida; j++) {
                        if (inimigo.getcampo()[i][j] == 'X') {

                           
              try {
                 List<int[]> vizinhosBD = banco.buscarVizinhosMaisComuns(dificuldade, i, j);
                  for (int[] p : vizinhosBD) {
                   int li = p[0], co = p[1];
                     if (li >= 0 && li < inimigo.gettamanhoX() && co >= 0 && co < inimigo.gettamanhoY()) {
                       char cel = inimigo.getcampo()[li][co];
                         if (cel != 'X' && cel != 'O') {
                           linha = li;
                              coluna = co;
                                jogadaDefinida = true;
                                  break outerTarget;
                                        }
                                    }
                                }
                            } catch (Throwable t) {
                               
                            }

                          
           int[][] adj = {{i, j+1}, {i, j-1}, {i+1, j}, {i-1, j}}; 
            for (int[] p : adj) {
                int li = p[0], co = p[1];
                   if (li >= 0 && li < inimigo.gettamanhoX() && co >= 0 && co < inimigo.gettamanhoY()) {
                     char cel = inimigo.getcampo()[li][co];
                       if (cel != 'X' && cel != 'O') {
                         linha = li;
                           coluna = co;
                              jogadaDefinida = true;
                                   break outerTarget;
                                    }
                                }
                            }
                        }
                    }
                }

                
                if (!jogadaDefinida) {
                    try {
                        int[] jog = banco.melhorJogada(dificuldade); 
                        if (jog != null && jog.length >= 2) {
                            int l = jog[0], c = jog[1];
                            if (l >= 0 && l < inimigo.gettamanhoX() && c >= 0 && c < inimigo.gettamanhoY()) {
                                char cel = inimigo.getcampo()[l][c];
                                if (cel != 'X' && cel != 'O') {
                                    linha = l;
                                    coluna = c;
                                    jogadaDefinida = true;
                                }
                            }
                        }
                    } catch (Throwable t) {
                       
                    }
                }

               
                if (!jogadaDefinida) {
                    int n = inimigo.gettamanhoX();
                    int m = inimigo.gettamanhoY();
                    int centroL = n / 2;
                    int centroC = m / 2;

                    List<int[]> coords = new ArrayList<>(n * m);
                    for (int i = 0; i < n; i++) {
                        for (int j = 0; j < m; j++) {
                            coords.add(new int[]{i, j});
                        }
                    }

                    coords.sort((a, b) -> {
                        int da = Math.abs(a[0] - centroL) + Math.abs(a[1] - centroC);
                        int db = Math.abs(b[0] - centroL) + Math.abs(b[1] - centroC);
                        if (da != db) return Integer.compare(da, db);
                        if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
                        return Integer.compare(a[1], b[1]);
                    });

                    for (int[] p : coords) {
                        int li = p[0], co = p[1];
                        char c = inimigo.getcampo()[li][co];
                        if (c != 'X' && c != 'O') {
                            linha = li;
                            coluna = co;
                            break;
                        }
                    }
                }

                break;
            }

       
            default: {
                Random rnd = new Random();
                int tent = 0;
                do {
                    linha = rnd.nextInt(inimigo.gettamanhoX());
                    coluna = rnd.nextInt(inimigo.gettamanhoY());
                    tent++;
                    if (tent > 1000) break;
                } while (inimigo.getcampo()[linha][coluna] == 'X' || inimigo.getcampo()[linha][coluna] == 'O');
                break;
            }
        } 

       
        if (linha < 0 || coluna < 0) {
            outer2:
            for (int i = 0; i < inimigo.gettamanhoX(); i++) {
                for (int j = 0; j < inimigo.gettamanhoY(); j++) {
                    char c = inimigo.getcampo()[i][j];
                    if (c != 'X' && c != 'O') {
                        linha = i;
                        coluna = j;
                        break outer2;
                    }
                }
            }
        }

        char valorAtual = inimigo.getcampo()[linha][coluna];
        boolean acertou = (valorAtual == 'N');

        System.out.println("Maquina atacou: " + (char)('A' + linha) + "" + (coluna + 1));

        if (acertou) {
            System.out.println("A mquina ACERTOU um navio!");
            inimigo.getcampo()[linha][coluna] = 'X';
        } else {
            System.out.println("A maquina acertou a agua.");
            inimigo.getcampo()[linha][coluna] = 'O';
        }

        try {
            banco.registrarJogadaIA(dificuldade, linha, coluna, acertou);
        } catch (Throwable t) {
           
        }
    }

    boolean veriEspa(MeuTabuleiro tab, int linha, int coluna, int tamanhoNavio, boolean horizontal) {
        if (horizontal) {
            if (coluna + tamanhoNavio > tab.gettamanhoY()) return false;
            for (int j = 0; j < tamanhoNavio; j++)
                if (!tab.posicaoDisponivel(linha, coluna + j)) return false;
        } else {
            if (linha + tamanhoNavio > tab.gettamanhoX()) return false;
            for (int j = 0; j < tamanhoNavio; j++)
                if (!tab.posicaoDisponivel(linha + j, coluna)) return false;
        }
        return true;
    }
}