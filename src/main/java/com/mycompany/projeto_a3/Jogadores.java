package com.mycompany.projeto_a3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

// Versão comentada completa da v2 — Jogadores, Jogador1 e Maquina
// Comentários explicativos adicionados em todas as partes importantes

public abstract class Jogadores {
    public String nome; // nome do jogador (público)
    protected int frotas[]; // vetor com os tamanhos dos navios (visível para subclasses)
    MeuTabuleiro inimigo; // tabuleiro do adversário (onde este jogador ataca)
    MeuTabuleiro tabuleiro; // tabuleiro próprio (onde posiciona suas frotas)

    // Construtor: recebe nome, referência ao tabuleiro inimigo e ao próprio tabuleiro
    public Jogadores(String nome, MeuTabuleiro inimigo, MeuTabuleiro tabuleiro) {
        this.nome = nome; // atribui o nome recebido ao campo da instância
        this.frotas = new int[]{2, 3, 4}; // define os tamanhos padrão das frotas
        this.tabuleiro = tabuleiro; // referência ao tabuleiro próprio
        this.inimigo = inimigo; // referência ao tabuleiro inimigo
    }

    // Métodos abstratos: implementados por Jogador1 (humano) e Maquina (IA)
    abstract void ataque();
    abstract void posicao();
}

// ============================ JOGADOR HUMANO ============================

class Jogador1 extends Jogadores {

    public Jogador1(String nome, MeuTabuleiro inimigo, MeuTabuleiro tabuleiro) {
        super(nome, inimigo, tabuleiro); // chama construtor da classe mãe
    }

    @Override
    void posicao() {
        // Lê do teclado e posiciona cada célula dos navios (modelo simples, não contíguo obrigatório)
        Scanner tecla = new Scanner(System.in);
        System.out.println("Vamos posicionar as frotas!");
        System.out.println("Essas sao as suas frotas: " + frotas.length + " navios para posicionar.");

        // Para cada navio (por tamanho) solicita posição célula-a-célula
        for (int i = 0; i < frotas.length; i++) {
            int tamanhoN = frotas[i];
            System.out.println("\nPosicione o navio de tamanho " + tamanhoN);

            for (int j = 0; j < tamanhoN; j++) {
                boolean posicaoValida = false;
                String entrada;

                do {
                    System.out.println("Digite a coordenada " + (j + 1) + " (Ex: A1, B5):");
                    entrada = tecla.nextLine().toUpperCase().trim();

                    // Valida formato: letra A-J seguida de 1-10
                    if (!entrada.matches("^[A-J](10|[1-9])$")) {
                        System.out.println("Formato invalido! Use formato Letra+Numero (ex: A1, B10).");
                        continue; // volta a pedir
                    }

                    int linha = entrada.charAt(0) - 'A'; // converte letra A-J em índice 0-9
                    int coluna = Integer.parseInt(entrada.substring(1)) - 1; // converte número para índice 0-9

                    // Verifica limites do tabuleiro
                    if (linha < 0 || linha >= tabuleiro.gettamanhoX() || coluna < 0 || coluna >= tabuleiro.gettamanhoY()) {
                        System.out.println("Posicao fora do limite!");
                        continue;
                    }

                    // Verifica se a posição está disponível
                    if (tabuleiro.posicaoDisponivel(linha, coluna)) {
                        tabuleiro.getcampo()[linha][coluna] = 'N'; // marca navio
                        posicaoValida = true; // sai do loop
                    } else {
                        System.out.println("Posicao ja ocupada. Digite novamente.");
                    }

                } while (!posicaoValida);
            }
        }
    }

    @Override
    void ataque() {
        // Recebe coordenada de ataque do jogador e atualiza tabuleiro inimigo
        Scanner tecla = new Scanner(System.in);
        boolean tiroValido = false;

        while (!tiroValido) {
            System.out.println("Digite a coordenada do ataque (Ex: A1, B5):");
            String entrada = tecla.nextLine().toUpperCase().trim();

            // Validação de formato
            if (!entrada.matches("^[A-J](10|[1-9])$")) {
                System.out.println("Formato invalido! Use formato Letra+Número (ex: A1, B10).");
                continue;
            }

            int linha = entrada.charAt(0) - 'A';
            int coluna = Integer.parseInt(entrada.substring(1)) - 1;

            // Verifica limites
            if (linha < 0 || linha >= inimigo.gettamanhoX() || coluna < 0 || coluna >= inimigo.gettamanhoY()) {
                System.out.println("Posicao fora do limite!");
                continue;
            }

            char valorAtual = inimigo.getcampo()[linha][coluna];

            // Verifica se já foi atacada
            if (valorAtual == 'X' || valorAtual == 'O') {
                System.out.println("Posicao ja atacada!");
                continue;
            }

            // Marca acerto ('X') ou água ('O')
            if (valorAtual == 'N') {
                System.out.println(" Acertou um navio!");
                inimigo.getcampo()[linha][coluna] = 'X';
            } else {
                System.out.println(" Acertou a agua!");
                inimigo.getcampo()[linha][coluna] = 'O';
            }

            tiroValido = true; // finaliza o ataque
        }
    }
}

// ============================ MÁQUINA (IA) ============================

class Maquina extends Jogadores {

    private int dificuldade; // nível da IA (1,2,3)
    private Banco_Do_Jogo banco = new Banco_Do_Jogo(); // persistência/estatísticas (opcional)

    public Maquina(String nome, MeuTabuleiro inimigo, MeuTabuleiro tabuleiro) {
        super(nome, inimigo, tabuleiro);
    }

    public void setDificuldade(int dificuldade) {
        this.dificuldade = dificuldade; // atribui nível
    }

    @Override
    void posicao() {
        // Posiciona navios aleatoriamente, respeitando limites e ocupação
        System.out.println("Posicionando frotas da maquina...");

        for (int i = 0; i < frotas.length; i++) {
            int tamanhoNavio = frotas[i];
            boolean posicionado = false;

            while (!posicionado) {
                int linha = (int) (Math.random() * tabuleiro.gettamanhoX());
                int coluna = (int) (Math.random() * tabuleiro.gettamanhoY());
                boolean horizontal = Math.random() < 0.5; // orientação aleatória

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
        // Decide uma jogada com base na dificuldade e atualiza o tabuleiro inimigo
        int linha = -1, coluna = -1; // coordenadas escolhidas

        switch (dificuldade) {
            // ----------------- DIFICULDADE 1: aleatório -----------------
            case 1: {
                Random rnd = new Random();
                int tent = 0;
                do {
                    linha = rnd.nextInt(inimigo.gettamanhoX());
                    coluna = rnd.nextInt(inimigo.gettamanhoY());
                    tent++;
                    if (tent > 1000) break; // segurança para evitar loop infinito
                } while (inimigo.getcampo()[linha][coluna] == 'X' || inimigo.getcampo()[linha][coluna] == 'O');
                break;
            }

            // ----------------- DIFICULDADE 2: padrão "caça" ---------------
            case 2: {
                boolean achou = false;

                // 1) procurar acertos ('X') e tentar atacar adjacentes
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

                // 2) se não encontrou, usa padrão xadrez (economiza tentativas)
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

                // 3) fallback: percorre todas as células e pega a primeira disponível
                if (!achou) {
                    outer:
                    for (int i = 0; i < inimigo.gettamanhoX(); i++) {
                        for (int j = 0; j < inimigo.gettamanhoY(); j++) {
                            char c = inimigo.getcampo()[i][j];
                            if (c != 'X' && c != 'O') {
                                linha = i;
                                coluna = j;
                                break outer; // sai dos dois loops
                            }
                        }
                    }
                }
                break;
            }

            // --------------- DIFICULDADE 3: avançada (estatística + alvo) ---------------
            case 3: {
                boolean jogadaDefinida = false;

                // Primeiro tenta usar informações de acertos anteriores e estatísticas do banco
                outerTarget:
                for (int i = 0; i < inimigo.gettamanhoX() && !jogadaDefinida; i++) {
                    for (int j = 0; j < inimigo.gettamanhoY() && !jogadaDefinida; j++) {
                        if (inimigo.getcampo()[i][j] == 'X') {
                            // tenta obter vizinhos prováveis do banco de dados (método opcional)
                            try {
                                List<int[]> vizinhosBD = banco.buscarVizinhosMaisComuns(dificuldade, i, j);
                                if (vizinhosBD != null) {
                                    for (int[] p : vizinhosBD) {
                                        int li = p[0], co = p[1];
                                        if (li >= 0 && li < inimigo.gettamanhoX() && co >= 0 && co < inimigo.gettamanhoY()) {
                                            char cel = inimigo.getcampo()[li][co];
                                            if (cel != 'X' && cel != 'O') {
                                                linha = li;
                                                coluna = co;
                                                jogadaDefinida = true;
                                                break outerTarget; // saiu com jogada
                                            }
                                        }
                                    }
                                }
                            } catch (Throwable t) {
                                // ignora erros de BD e continua com heurísticas locais
                            }

                            // heurística local: checar adjacentes em volta de um X
                            int[][] adj = {{i, j + 1}, {i, j - 1}, {i + 1, j}, {i - 1, j}};
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

                // Se BD e adjacentes não definiram jogada, tenta obter melhor jogada estatística
                if (!jogadaDefinida) {
                    try {
                        int[] jog = banco.melhorJogada(dificuldade); // pode retornar null
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
                        // ignora erros de BD
                    }
                }

                // Estratégia final: escolher coordenadas próximas do centro ordenadas por distância (preferir centro)
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

                break; // fim case 3
            }

            // ----------------- DEFAULT: fallback aleatório -----------------
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

        // Se ainda não definiu coordenadas (linha/coluna negativas), pega a primeira célula livre
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

        // Executa o ataque: marca 'X' para acerto e 'O' para água
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

        // Tenta registrar estatística da jogada (opcional)
        try {
            banco.registrarJogadaIA(dificuldade, linha, coluna, acertou);
        } catch (Throwable t) {
            // ignora falhas de persistência
        }
    }

    // Verifica se há espaço para posicionar um navio (horizontal ou vertical)
    boolean veriEspa(MeuTabuleiro tab, int linha, int coluna, int tamanhoNavio, boolean horizontal) {
        if (horizontal) {
            if (coluna + tamanhoNavio > tab.gettamanhoY()) return false; // ultrapassa borda
            for (int j = 0; j < tamanhoNavio; j++)
                if (!tab.posicaoDisponivel(linha, coluna + j)) return false; // posição ocupada
        } else {
            if (linha + tamanhoNavio > tab.gettamanhoX()) return false; // ultrapassa borda
            for (int j = 0; j < tamanhoNavio; j++)
                if (!tab.posicaoDisponivel(linha + j, coluna)) return false; // posição ocupada
        }
        return true; // tudo ok
    }
}
