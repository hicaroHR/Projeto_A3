package com.mycompany.projeto_a3;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Banco_Do_Jogo {

    private static final String URL = "jdbc:sqlite:C:/Users/hiica/MySql/batalha_naval.db";

    public Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // ==============================
    // TABELA PLACAR
    // ==============================
    public void criarTabelaPlacar() {
        String sql = "CREATE TABLE IF NOT EXISTS placar ("
                + "nome TEXT PRIMARY KEY, "
                + "vitorias INTEGER DEFAULT 0, "
                + "derrotas INTEGER DEFAULT 0"
                + ");";
        try (Connection conn = conectar(); Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.err.println("Erro criarTabelaPlacar: " + e.getMessage());
        }
    }

    public boolean jogadorExiste(String nome) {
        String sql = "SELECT 1 FROM placar WHERE nome = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            System.err.println("Erro jogadorExiste: " + e.getMessage());
            return false;
        }
    }

    public void registrarJogador(String nome) {
        String sql = "INSERT INTO placar (nome) VALUES (?)";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro registrarJogador: " + e.getMessage());
        }
    }

    public void adicionarVitoria(String nome) {
        String sql = "UPDATE placar SET vitorias = vitorias + 1 WHERE nome = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro adicionarVitoria: " + e.getMessage());
        }
    }

    public void adicionarDerrota(String nome) {
        String sql = "UPDATE placar SET derrotas = derrotas + 1 WHERE nome = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nome);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erro adicionarDerrota: " + e.getMessage());
        }
    }

    // ==============================
    // EXIBIÇÃO DO PLACAR
    // ==============================
    public void mostrarPlacarIndividual(String nome) {
        String sql = "SELECT nome, vitorias, derrotas FROM placar WHERE nome = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nome);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.printf("\nJogador: %s | Vitórias: %d | Derrotas: %d\n",
                        rs.getString("nome"),
                        rs.getInt("vitorias"),
                        rs.getInt("derrotas"));
            } else {
                System.out.println("Nenhum registro encontrado para: " + nome);
            }

        } catch (SQLException e) {
            System.err.println("Erro mostrarPlacarIndividual: " + e.getMessage());
        }
    }

    public void mostrarPlacarGeral() {
        String sql = "SELECT nome, vitorias, derrotas "
                + "FROM placar ORDER BY vitorias DESC, nome ASC";

        try (Connection conn = conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            System.out.println("\n===== PLACAR GERAL =====");
            while (rs.next()) {
                System.out.printf("%-12s | Vitórias: %-3d | Derrotas: %-3d\n",
                        rs.getString("nome"),
                        rs.getInt("vitorias"),
                        rs.getInt("derrotas"));
            }

        } catch (SQLException e) {
            System.err.println("Erro mostrarPlacarGeral: " + e.getMessage());
        }
    }

    // ==============================
    // IA - TABELA
    // ==============================
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

    public void registrarJogadaIA(int dificuldade, int linha, int coluna, boolean acertou) {
        if (!acertou) return;

        String sql =
                "INSERT INTO maquina_inteligencia (dificuldade, linha, coluna, resultado, quantidade) "
                        + "VALUES (?, ?, ?, 'HIT', 1) "
                        + "ON CONFLICT(dificuldade, linha, coluna) "
                        + "DO UPDATE SET quantidade = quantidade + 1";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, dificuldade);
            ps.setInt(2, linha);
            ps.setInt(3, coluna);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Erro registrarJogadaIA: " + e.getMessage());
        }
    }

    // ==============================
    // MELHOR JOGADA
    // ==============================
    public int[] melhorJogada(int dificuldade) {
        String sql =
                "SELECT linha, coluna FROM maquina_inteligencia "
                        + "WHERE dificuldade = ? AND resultado = 'HIT' "
                        + "ORDER BY quantidade DESC LIMIT 1";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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

    public List<int[]> buscarVizinhosMaisComuns(int dificuldade, int linha, int coluna) {

        String sql =
                "SELECT linha, coluna FROM maquina_inteligencia "
                        + "WHERE dificuldade = ? AND resultado = 'HIT' AND ( "
                        + " (linha = ? AND ABS(coluna - ?) = 1) "
                        + " OR (coluna = ? AND ABS(linha - ?) = 1) "
                        + ") ORDER BY quantidade DESC";

        List<int[]> lista = new ArrayList<>();

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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
