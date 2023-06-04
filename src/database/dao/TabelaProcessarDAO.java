package database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import database.model.TabelaProcessar;

public class TabelaProcessarDAO {
	
	private String select = "SELECT * FROM tb_processo_tabela WHERE id_processo = ? and habilitado ORDER BY ordem ASC";
	private String selectAll = "SELECT pt.id, p.nome_processo, pt.nome_tabela_origem, pt.nome_tabela_dest, pt.ordem, condicao, pt.habilitado FROM tb_processo_tabela as pt left join tb_processo as p on pt.id_processo = p.id ORDER By ordem ASC;";
	private String UpdateCodicao = "UPDATE tb_processo_tabela SET condicao = ? WHERE id = ?";
	private PreparedStatement pstSelect;
	private PreparedStatement pstSelectAll;
	private PreparedStatement pstUpdateCondicao;
	
	public TabelaProcessarDAO(Connection conn) throws SQLException {
		pstSelect = conn.prepareStatement(select);
		pstSelectAll = conn.prepareStatement(selectAll);
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
public ArrayList<TabelaProcessar> selectAll() throws SQLException {
		
		ArrayList<TabelaProcessar> arlTabelaProcessar = new ArrayList<TabelaProcessar>();
	
		ResultSet resultado = pstSelectAll.executeQuery();
		while (resultado.next()) {
			TabelaProcessar tp = new TabelaProcessar();
			tp.setId(resultado.getInt("id"));
			tp.setDescricaoProcesso(resultado.getString("nome_processo"));
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