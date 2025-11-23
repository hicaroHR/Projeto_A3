package com.mycompany.projeto_a3;

import java.util.Scanner;

public class Projeto {
    public static void main(String[] args) {
        Scanner tecla = new Scanner(System.in);
        Banco_Do_Jogo banco = new Banco_Do_Jogo();

        
        banco.criarTabelaPlacar();

        MeuTabuleiro tabuleiroJogador = new MeuTabuleiro(10, 10);
        MeuTabuleiro tabuleiroInimigo = new MeuTabuleiro(10, 10);

        tabuleiroJogador.inicio();
        tabuleiroInimigo.inicio();

        System.out.println("Ola, digite o seu nome:");
        String nomeJogador = tecla.nextLine();

        
        if (!banco.jogadorExiste(nomeJogador)) {
            banco.registrarJogador(nomeJogador);
            System.out.println("Novo jogador registrado: " + nomeJogador);
        } else {
            System.out.println("Bem-vindo de volta, " + nomeJogador);
            banco.mostrarPlacarIndividual(nomeJogador);
        }

        Jogador1 jogador = new Jogador1(nomeJogador, tabuleiroInimigo, tabuleiroJogador);
        Maquina maquina = new Maquina("Maquina", tabuleiroJogador, tabuleiroInimigo);

        int dificuldade = 0;

        while (dificuldade < 1 || dificuldade > 3) {
            System.out.println("\nEscolha a dificuldade:");
            System.out.println("1 - Facil");
            System.out.println("2 - Medio");
            System.out.println("3 - Dificil");
            System.out.print("Opcao: ");

            if (tecla.hasNextInt()) {
                dificuldade = tecla.nextInt();
            } else {
                tecla.next(); 
                dificuldade = 0;
            }

            switch (dificuldade) {
                case 1:
                    System.out.println("Dificuldade selecionada: Facil");
                    break;
                case 2:
                    System.out.println("Dificuldade selecionada: Medio");
                    break;
                case 3:
                    System.out.println("Dificuldade selecionada: Dificil");
                    break;
                default:
                    System.out.println("Opcao invalida! Tente novamente.");
            }
        }

        
        maquina.setDificuldade(dificuldade);

       
        jogador.posicao();
        maquina.posicao();

        boolean startGame = false;

        while (!startGame) {
            System.out.println("==== Tabuleiro do jogador ====");
            tabuleiroJogador.mostrarTabuleiro(true);
            System.out.println("==== Tabuleiro da maquina ====");
            tabuleiroInimigo.mostrarTabuleiro(false);

            jogador.ataque();

            if (!tabuleiroInimigo.Status_do_navio()) {
                System.out.println("\nVoce venceu!");
                banco.adicionarVitoria(nomeJogador);
                banco.adicionarDerrota("Maquina");
                startGame = true;
                break;
            }

            maquina.ataque();

            if (!tabuleiroJogador.Status_do_navio()) {
                System.out.println("\nA maquina venceu!");
                banco.adicionarDerrota(nomeJogador);
                banco.adicionarVitoria("Maquina");
                startGame = true;
                break;
            }

            System.out.println("Tabuleiro do jogador");
            tabuleiroJogador.mostrarTabuleiro(true);
            System.out.println("Tabuleiro da máquina");
            tabuleiroInimigo.mostrarTabuleiro(false);
        }

        System.out.println("Fim de jogo");

        System.out.println("\nSeu placar atualizado:");
        banco.mostrarPlacarIndividual(nomeJogador);

        System.out.println("\nPlacar geral:");
        banco.mostrarPlacarGeral();

        tecla.close();
    }
}