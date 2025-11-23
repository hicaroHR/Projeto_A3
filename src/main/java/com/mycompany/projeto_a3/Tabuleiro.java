/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projeto_a3;

public abstract class Tabuleiro {
    protected char[][] campo;
    private int tamanhoX = 10;
    private int tamanhoY = 10;

    public Tabuleiro(int tamanhoX, int tamanhoY) {
        campo = new char[tamanhoX][tamanhoY];
    }

    public int gettamanhoX() { return tamanhoX; }
    public int gettamanhoY() { return tamanhoY; }

    public char[][] getcampo() { return campo; }

    abstract void inicio();

    public boolean Status_do_navio() {
        for (int i = 0; i < tamanhoX; i++) {
            for (int j = 0; j < tamanhoY; j++) {
                if (campo[i][j] == 'N') {
                    return true;
                }
            }
        }
        return false;
    }
}

class MeuTabuleiro extends Tabuleiro {

    public MeuTabuleiro(int tamanhoX, int tamanhoY) {
        super(tamanhoX, tamanhoY);
    }

    @Override
    void inicio() {
        for (int i = 0; i < gettamanhoX(); i++) {
            for (int j = 0; j < gettamanhoY(); j++) {
                campo[i][j] = '~';
            }
        }
    }

    public boolean posicaoDisponivel(int linha, int coluna) {
        return campo[linha][coluna] == '~';
    }

    public boolean posicionarN(int linha, int coluna) {
        return campo[linha][coluna] == 'N';
    }

    public void mostrarTabuleiro(boolean mostrarNavios) {
        System.out.printf("«⛴..│ 1");

        for (int i = 2; i <= gettamanhoY(); i++) {
            System.out.printf("░%1d", i);
        }
        System.out.println(" │..⛴»");


        // Corpo do tabuleiro com letras à esquerda e à direita
        for (int i = 0; i < gettamanhoX(); i++) {
            char letra = (char)('A' + i);
            System.out.printf(" %2c ¦░", letra); // Letra à esquerda

            for (int j = 0; j < gettamanhoY(); j++) {
                char celula = campo[i][j];
                if (!mostrarNavios && celula == 'N') {
                    System.out.print(" ~");
                } else {
                    System.out.printf(" %c", celula);
                }
            }

            System.out.printf(" ░¦%2c%n", letra); // Letra à direita
        }
    }

class TabuleiroInimigo extends Tabuleiro {

    public TabuleiroInimigo(int tamanhoX, int tamanhoY) {
        super(tamanhoX, tamanhoY);
    }

    @Override
    void inicio() {
        for (int i = 0; i < gettamanhoX(); i++) {
            for (int j = 0; j < gettamanhoY(); j++) {
                campo[i][j] = '~';
            }
        }
    }
}
}
