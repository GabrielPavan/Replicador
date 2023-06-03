package database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import database.model.TabelaProcessar;

public class TabelaProcessarDAO {
	
	private String select = "SELECT * FROM tb_processo_tabela WHERE id_processo = ? and habilitado ORDER BY id ASC";
	private String UpdateCodicao = "UPDATE tb_processo_tabela SET condicao = ? WHERE id = ?";
	private PreparedStatement pstSelect;
	private PreparedStatement pstUpdateCondicao;
	
	public TabelaProcessarDAO(Connection conn) throws SQLException {
		pstSelect = conn.prepareStatement(select);
		pstUpdateCondicao = conn.prepareStatement(UpdateCodicao);
	}
	
	public ArrayList<TabelaProcessar> selectAll(int id_processo) throws SQLException {
		
		ArrayList<TabelaProcessar> arlTabelaProcessar = new ArrayList<TabelaProcessar>();
		
		pstSelect.setInt(1, id_processo);
		ResultSet resultado = pstSelect.executeQuery();
		while (resultado.next()) {
			TabelaProcessar tp = new TabelaProcessar();
			tp.setId(resultado.getInt("id"));
			tp.setId_processo(resultado.getInt("id_processo"));
			tp.setNome_tabela_origem(resultado.getString("nome_tabela_origem"));
			tp.setNome_tabela_dest(resultado.getString("nome_tabela_dest"));
			tp.setOrdem(resultado.getInt("ordem"));
			tp.setCondição(resultado.getInt("condicao"));
			tp.setHabilitado(resultado.getBoolean("habilitado"));
			arlTabelaProcessar.add(tp);
		}
		return arlTabelaProcessar;	
	}
	
	public boolean updateCondicao(int id, int condicao) throws SQLException {
		pstUpdateCondicao.setInt(1, condicao);
		pstUpdateCondicao.setInt(2, id);
		return (pstUpdateCondicao.executeUpdate() == 1 ? true : false);
	}
}