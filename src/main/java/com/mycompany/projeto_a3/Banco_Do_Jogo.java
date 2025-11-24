// =====================
// BANCO_DO_JOGO.JAVA
// =====================

package com.mycompany.projeto_a3;

import java.sql.*;                // Importa classes para acesso ao banco de dados
import java.util.ArrayList;       // Lista dinâmica
import java.util.List;            // Interface de listas

/**
 * Classe responsável por TODA a comunicação com o Banco de Dados.
 * Ela gerencia placares, estatísticas da IA e conexões SQLite.
 */
public class Banco_Do_Jogo {

    // Caminho do arquivo do banco SQLite
    private static final String URL = "jdbc:sqlite:C:/Users/hiica/MySql/batalha_naval.db";

    /**
     * Abre conexão com o banco SQLite.
     * @return Connection objeto de conexão.
     * @throws SQLException caso haja erro ao conectar.
     */
    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // ======================================================
    //  SEÇÃO 1 — TABELA DO PLACAR (JOGADORES)
    // ======================================================

    /**
     * Cria a tabela "placar" se ela ainda não existir.
     * Campos:
     * nome (PK), vitorias, derrotas
     */
    public void criarTabelaPlacar() {
        String sql = "CREATE TABLE IF NOT EXISTS placar ("
                + "nome TEXT PRIMARY KEY, "
                + "vitorias INTEGER DEFAULT 0, "
                + "derrotas INTEGER DEFAULT 0"
                + ");";

        try (Connection conn = conectar(); Statement st = conn.createStatement()) {
            st.execute(sql); // Executa o comando de criação
        } catch (SQLException e) {
            System.err.println("Erro criarTabelaPlacar: " + e.getMessage());
        }
    }

    /**
     * Verifica se um jogador já está registrado no banco.
     * @param nome nome do jogador
     * @return true se existir, false caso contrário
     */
    public boolean jogadorExiste(String nome) {
        String sql = "SELECT 1 FROM placar WHERE nome = ?";

        try (Connection conn = conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);   // Substitui o "?" pelo nome
            ResultSet rs = ps.executeQuery(); // Executa consulta

            return rs.next(); // Se encontrou resultado, jogador existe

        } catch (SQLException e) {
            System.err.println("Erro jogadorExiste: " + e.getMessage());
            return false;
        }
    }

    /**
     * Registra um novo jogador no placar.
     * @param nome nome do jogador
     */
    public void registrarJogador(String nome) {
        String sql = "INSERT INTO placar (nome) VALUES (?)";

        try (Connection conn = conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);  // Coloca o nome no comando SQL
            ps.executeUpdate();     // Executa INSERT

        } catch (SQLException e) {
            System.err.println("Erro registrarJogador: " + e.getMessage());
        }
    }

    /**
     * Incrementa 1 vitória ao jogador informado.
     */
    public void adicionarVitoria(String nome) {
        String sql = "UPDATE placar SET vitorias = vitorias + 1 WHERE nome = ?";

        try (Connection conn = conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro adicionarVitoria: " + e.getMessage());
        }
    }

    /**
     * Incrementa 1 derrota ao jogador informado.
     */
    public void adicionarDerrota(String nome) {
        String sql = "UPDATE placar SET derrotas = derrotas + 1 WHERE nome = ?";

        try (Connection conn = conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro adicionarDerrota: " + e.getMessage());
        }
    }

    // ======================================================
    //  SEÇÃO 2 — EXIBIÇÃO DE PLACAR
    // ======================================================

    /**
     * Mostra os dados de um jogador específico (vitórias e derrotas).
     */
    public void mostrarPlacarIndividual(String nome) {
        String sql = "SELECT nome, vitorias, derrotas FROM placar WHERE nome = ?";

        try (Connection conn = conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.printf(" Jogador: %s | Vitórias: %d | Derrotas: %d ",
                rs.getString("nome"), rs.getInt("vitorias"), rs.getInt("derrotas"));
            } else {
                System.out.println("Nenhum registro encontrado para: " + nome);
            }

        } catch (SQLException e) {
            System.err.println("Erro mostrarPlacarIndividual: " + e.getMessage());
        }
    }

    /**
     * Mostra o ranking completo ordenado por número de vitórias.
     */
    public void mostrarPlacarGeral() {
        String sql = "SELECT nome, vitorias, derrotas "
                + "FROM placar ORDER BY vitorias DESC, nome ASC";

        try (Connection conn = conectar(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            System.out.println("===== PLACAR GERAL =====");
            while (rs.next()) {
                System.out.printf("%-12s | Vitórias: %-3d | Derrotas: %-3d ",
                        rs.getString("nome"), rs.getInt("vitorias"), rs.getInt("derrotas"));
            }

        } catch (SQLException e) {
            System.err.println("Erro mostrarPlacarGeral: " + e.getMessage());
        }
    }

    // ======================================================
    // SEÇÃO 3 — TABELA DE INTELIGÊNCIA DA IA
    // ======================================================

    /**
     * Cria tabela que armazena acertos da IA para que ela aprenda.
     *
     * PRIMARY KEY(dificuldade, linha, coluna)
     * significa: uma mesma posição, na mesma dificuldade, só aparece 1 vez.
     */
    public void criarTabelaMaquinaInteligencia() {
        String sql = "CREATE TABLE IF NOT EXISTS maquina_inteligencia ("
                + "dificuldade INTEGER NOT NULL, "
                + "linha INTEGER NOT NULL, "
                + "coluna INTEGER NOT NULL, "
                + "resultado TEXT NOT NULL, "
                + "quantidade INTEGER DEFAULT 0, "
                + "PRIMARY KEY (dificuldade, linha, coluna)"
                + ");";

        try (Connection conn = conectar(); Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erro criarTabelaMaquinaInteligencia: " + e.getMessage());
        }
    }

    /**
     * Registra uma jogada da IA, mas SOMENTE se ela acertou.
     * Cada acerto aumenta a contagem naquela posição.
     */
    public void registrarJogadaIA(int dificuldade, int linha, int coluna, boolean acertou) {

        if (!acertou) return; // Não registra erros

        String sql =
                "INSERT INTO maquina_inteligencia (dificuldade, linha, coluna, resultado, quantidade) "
                        + "VALUES (?, ?, ?, 'HIT', 1) "
                        + "ON CONFLICT(dificuldade, linha, coluna) "
                        + "DO UPDATE SET quantidade = quantidade + 1";

        try (Connection conn = conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dificuldade);
            ps.setInt(2, linha);
            ps.setInt(3, coluna);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro registrarJogadaIA: " + e.getMessage());
        }
    }

    // ======================================================
    // SEÇÃO 4 — MELHOR JOGADA
    // ======================================================

    /**
     * Retorna a posição mais forte já usada pela IA, baseada no banco.
     * @return int[]{linha, coluna} ou null se não existir
     */
    public int[] melhorJogada(int dificuldade) {

        String sql = "SELECT linha, coluna FROM maquina_inteligencia "
                + "WHERE dificuldade = ? AND resultado = 'HIT' "
                + "ORDER BY quantidade DESC LIMIT 1";

        try (Connection conn = conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dificuldade);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new int[]{ rs.getInt("linha"), rs.getInt("coluna") };
            }

        } catch (SQLException e) {
            System.err.println("Erro melhorJogada: " + e.getMessage());
        }

        return null;
    }

    // ======================================================
    // SEÇÃO 5 — BUSCAR VIZINHOS MAIS PROVÁVEIS
    // ======================================================

    /**
     * Procura posições vizinhas que também costumam ser acertos.
     */
    public List<int[]> buscarVizinhosMaisComuns(int dificuldade, int linha, int coluna) {

        String sql =
                "SELECT linha, coluna FROM maquina_inteligencia "
                        + "WHERE dificuldade = ? AND resultado = 'HIT' AND ( "
                        + " (linha = ? AND ABS(coluna - ?) = 1) "
                        + " OR (coluna = ? AND ABS(linha - ?) = 1) "
                        + " ) ORDER BY quantidade DESC";

        List<int[]> lista = new ArrayList<>();

        try (Connection conn = conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dificuldade);
            ps.setInt(2, linha);
            ps.setInt(3, coluna);
            ps.setInt(4, coluna);
            ps.setInt(5, linha);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(new int[]{ rs.getInt("linha"), rs.getInt("coluna") });
            }

        } catch (SQLException e) {
            System.err.println("Erro buscarVizinhosMaisComuns: " + e.getMessage());
        }

        return lista;
    }
}
