package com.mycompany.projeto_a3;

public abstract class Tabuleiro {
    protected char[][] campo; // protected → visível para classes filhas // char[][] → matriz de caracteres
    private int tamanhoX = 10; // private → só essa classe acesso // int → número inteiro
    private int tamanhoY = 10;

    public Tabuleiro(int tamanhoX, int tamanhoY) { // construtor com dois ints (tamanhos)
        campo = new char[tamanhoX][tamanhoY]; // new char[][] → cria matriz nova na memória
    }

    public int gettamanhoX() { return tamanhoX; } // return → devolve o valor
    public int gettamanhoY() { return tamanhoY; }

    public char[][] getcampo() { return campo; } // retorna a matriz inteira

    abstract void inicio(); // abstract void → método sem corpo, classe filha deve implementar

    public boolean Status_do_navio() { // boolean → retorna true ou false
        for (int i = 0; i < tamanhoX; i++) { // for → laço que repete; i = 0 → início; i < tamanhoX → condição; i++ → incrementa
            for (int j = 0; j < tamanhoY; j++) { // segundo for → percorre colunas
                if (campo[i][j] == 'N') { // if → verifica condição
                    return true; // return true → navio encontrado
                }
            }
        }
        return false; // return false → nenhum navio encontrado
    }
}

class MeuTabuleiro extends Tabuleiro {

    public MeuTabuleiro(int tamanhoX, int tamanhoY) {
        super(tamanhoX, tamanhoY); // super → chama o construtor da classe mãe
    }

    @Override // @Override → indica que o método está sobrescrevendo o da classe mãe
    void inicio() { // void → não retorna nada
        for (int i = 0; i < gettamanhoX(); i++) { // for → percorre cada linha
            for (int j = 0; j < gettamanhoY(); j++) { // segundo for → percorre colunas
                campo[i][j] = '~'; // char → armazena um único caractere
            }
        }
    }

    public boolean posicaoDisponivel(int linha, int coluna) {
        return campo[linha][coluna] == '~'; // return → devolve comparação (true/false)
    }

    public boolean posicionarN(int linha, int coluna) {
        return campo[linha][coluna] == 'N'; // verifica se já tem navio
    }

    public void mostrarTabuleiro(boolean mostrarNavios) { // boolean → controla visibilidade

        // Cabeçalho numérico do tabuleiro
        System.out.printf("«⛴..│ 1");
        for (int i = 2; i <= gettamanhoY(); i++) {
            System.out.printf("░%1d", i); // %1d → formatação para números
        }
        System.out.println(" │..⛴»");

        // Corpo do tabuleiro com letras à esquerda e à direita
        for (int i = 0; i < gettamanhoX(); i++) {
            char letra = (char)('A' + i); // (char) → converte número para letra
            System.out.printf(" %2c ¦░", letra); // Letra à esquerda

            for (int j = 0; j < gettamanhoY(); j++) { // for → laço que percorre as colunas
                // i → contador de linhas
                // j → contador de colunas
                char celula = campo[i][j];
                if (!mostrarNavios && celula == 'N') { // ! → negação (“não”)
                    System.out.print(" ~");
                } else {
                    System.out.printf(" %c", celula);
                }
            }

            System.out.printf(" ░¦%2c%n", letra); // Letra à direita
        }
    }
}

class TabuleiroInimigo extends Tabuleiro { // extends → herança

    public TabuleiroInimigo(int tamanhoX, int tamanhoY) {
        super(tamanhoX, tamanhoY); // super → chama construtor da classe mãe
    }

    @Override
    void inicio() { // mesmo comportamento da classe MeuTabuleiro
        for (int i = 0; i < gettamanhoX(); i++) {
            for (int j = 0; j < gettamanhoY(); j++) {
                campo[i][j] = '~';
            }
        }
    }
}
