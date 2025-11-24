package com.mycompany.projeto_a3;

import java.util.Scanner; // Importa a classe Scanner para leitura pelo teclado

public class Projeto {
    public static void main(String[] args) {
        Scanner tecla = new Scanner(System.in); // new → cria um novo objeto Scanner na memória
        Banco_Do_Jogo banco = new Banco_Do_Jogo(); // new → cria novo objeto para gerenciar placar

        banco.criarTabelaPlacar();

        MeuTabuleiro tabuleiroJogador = new MeuTabuleiro(10, 10); // new → cria tabuleiro do jogador
        MeuTabuleiro tabuleiroInimigo = new MeuTabuleiro(10, 10); // new → cria tabuleiro da máquina

        tabuleiroJogador.inicio();
        tabuleiroInimigo.inicio();

        System.out.println("Ola, digite o seu nome:");
        String nomeJogador = tecla.nextLine(); // String → Lê o nome digitado

        if (!banco.jogadorExiste(nomeJogador)) { // if → decide se o jogador já existe
            banco.registrarJogador(nomeJogador);
            System.out.println("Novo jogador registrado: " + nomeJogador);
        } else {   // else → executado quando o if acima for falso
            System.out.println("Bem-vindo de volta, " + nomeJogador);
            banco.mostrarPlacarIndividual(nomeJogador);
        }

        Jogador1 jogador = new Jogador1(nomeJogador, tabuleiroInimigo, tabuleiroJogador); // new → cria jogador humano
        Maquina maquina = new Maquina("Maquina", tabuleiroJogador, tabuleiroInimigo); // new → cria máquina

        int dificuldade = 0; // int → usado porque dificuldade é um número inteiro (1, 2 ou 3)

        while (dificuldade < 1 || dificuldade > 3) { // while → repete até a dificuldade ser válida
            System.out.println("\nEscolha a dificuldade:");
            System.out.println("1 - Facil");
            System.out.println("2 - Medio");
            System.out.println("3 - Dificil");
            System.out.print("Opcao: ");

            if (tecla.hasNextInt()) { // if → verifica se a entrada é número
                dificuldade = tecla.nextInt();
            } else { // else → caso a entrada não seja número
                tecla.next();
                dificuldade = 0;
            }

            switch (dificuldade) { // switch → estrutura de decisão para vários valores possíveis
                case 1: // case → executa quando dificuldade == 1
                    System.out.println("Dificuldade selecionada: Facil");
                    break; // break → encerra o switch para não executar os outros cases
                case 2:
                    System.out.println("Dificuldade selecionada: Medio");
                    break; // break evita continuar para outros cases
                case 3:
                    System.out.println("Dificuldade selecionada: Dificil");
                    break;
                default:  // default → executa quando nenhum case corresponde ao valor
                    System.out.println("Opcao invalida! Tente novamente.");
            }
        }

        maquina.setDificuldade(dificuldade);

        jogador.posicao();
        maquina.posicao();

        boolean startGame = false; // boolean → tipo que guarda true ou false
        // false → significa que o jogo ainda NÃO começou/terminou

        while (!startGame) { // while → repete enquanto for true
            System.out.println("==== Tabuleiro do jogador ====");
            tabuleiroJogador.mostrarTabuleiro(true); // true → mostra navios do jogador
            System.out.println("==== Tabuleiro da maquina ====");
            tabuleiroInimigo.mostrarTabuleiro(false); // false → não mostra os navios da máquina

            jogador.ataque();

            if (!tabuleiroInimigo.Status_do_navio()) { // if → verifica condição
                // ! → negação (não tem mais navios)
                System.out.println("\nVoce venceu!");
                banco.adicionarVitoria(nomeJogador);
                banco.adicionarDerrota("Maquina");
                startGame = true; // true → indica que o jogo acabou
                break; // break → sai imediatamente do while
            }

            maquina.ataque();

            if (!tabuleiroJogador.Status_do_navio()) { // if → jogador perdeu todos navios
                System.out.println("\nA maquina venceu!");
                banco.adicionarDerrota(nomeJogador);
                banco.adicionarVitoria("Maquina");
                startGame = true; // termina o jogo
                break; // sai do while
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
