package database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import database.model.TabelaProcessar;

public class TabelaProcessarDAO {
	private String selectById = "select tp.*, p.nome_processo from tb_tabela_processo as tp left join tb_processo as p on tp.id_processo = p.id WHERE tp.id_processo = ?;";
	private String selectAll = "select tp.*, p.nome_processo from tb_tabela_processo as tp left join tb_processo as p on tp.id_processo = p.id;";
	private String insert = "INSERT INTO tb_tabela_processo VALUES (?, ?, ?);";
	private String UpdateCodicao = "UPDATE tb_tabela_processo SET condicao = ? WHERE nome_tb_destino = ? and id_processo = ?";
	
	private PreparedStatement pstSelect;
	private PreparedStatement pstSelectAll;
	private PreparedStatement pstInsert;
	private PreparedStatement pstUpdateCondicao;
	
	public TabelaProcessarDAO(Connection conn) throws SQLException {
		pstSelect = conn.prepareStatement(selectById);
		pstSelectAll = conn.prepareStatement(selectAll);
		pstInsert = conn.prepareStatement(insert);
		pstUpdateCondicao = conn.prepareStatement(UpdateCodicao);
	}
public ArrayList<TabelaProcessar> selectAll(int id_processo) throws SQLException {
		
		ArrayList<TabelaProcessar> arlTabelaProcessar = new ArrayList<TabelaProcessar>();
		
		pstSelect.setInt(1, id_processo);
		ResultSet resultado = pstSelect.executeQuery();
		while (resultado.next()) {
			TabelaProcessar tp = new TabelaProcessar();
			tp.setNome_tb_destino(resultado.getString("nome_tb_destino"));
			tp.setId_processo(resultado.getInt("id_processo"));
			tp.setCondicao(resultado.getInt("condicao"));
			tp.setDescriçãoProcesso(resultado.getString("nome_processo"));
			arlTabelaProcessar.add(tp);
		}
		return arlTabelaProcessar;	
	}
	
	public ArrayList<TabelaProcessar> selectAll() throws SQLException {
		
		ArrayList<TabelaProcessar> arlTabelaProcessar = new ArrayList<TabelaProcessar>();
	
		ResultSet resultado = pstSelectAll.executeQuery();
		while (resultado.next()) {
			TabelaProcessar tp = new TabelaProcessar();
			tp.setNome_tb_destino(resultado.getString("nome_tb_destino"));
			tp.setId_processo(resultado.getInt("id_processo"));
			tp.setCondicao(resultado.getInt("condicao"));
			tp.setDescriçãoProcesso(resultado.getString("nome_processo"));
			arlTabelaProcessar.add(tp);
		}
		return arlTabelaProcessar;	
	}
	public boolean insert(String nome_tb_destino, int id_processo) throws SQLException {
		pstInsert.setString(1, nome_tb_destino);
		pstInsert.setInt(2, id_processo);
		pstInsert.setInt(3, 0);
		
		try {
			return !pstInsert.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	public boolean updateCondicao(int condicao, String nome_tb_destino, int id_processo) throws SQLException {
		pstUpdateCondicao.setInt(1, condicao);
		pstUpdateCondicao.setString(2, nome_tb_destino);
		pstUpdateCondicao.setInt(3, id_processo);
		
		return (pstUpdateCondicao.executeUpdate() == 1 ? true : false);
	}
}