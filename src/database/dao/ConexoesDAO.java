package database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import database.model.Conexoes;

public class ConexoesDAO {
	
	private String selectById = "select * from tb_conexoes where id = ?;";
	private String selectAll = "select * from tb_conexoes order by id asc;";
	private PreparedStatement pstSelect;
	private PreparedStatement pstSelectAll;
	
	public ConexoesDAO(Connection conn) throws SQLException {
		pstSelect = conn.prepareStatement(selectById);
		pstSelectAll = conn.prepareStatement(selectAll);
	}
	
	public Conexoes SelectAllById(int id) throws SQLException {
		
		Conexoes conexoes = null;
		
		pstSelect.setInt(1, id);
		ResultSet resultado = pstSelect.executeQuery();
		
		if (resultado.next()) {
			conexoes = new Conexoes();
			conexoes.setId(resultado.getInt("id"));
			conexoes.setEndereco_ip(resultado.getString("ip"));
			conexoes.setEndereco_porta(resultado.getString("porta"));
			conexoes.setUsuario(resultado.getString("usuario"));
			conexoes.setSenha(resultado.getString("senha"));
			conexoes.setNome_banco(resultado.getString("nome_banco"));
			conexoes.setTipo_banco(resultado.getString("tipo_banco"));
		}
		
		return conexoes;
	}
public ArrayList<Conexoes> SelectAll() throws SQLException {
		
		
		
		ResultSet resultado = pstSelectAll.executeQuery();
		
		ArrayList<Conexoes> conexoes = new ArrayList<>();
		Conexoes conexao = null;
		while (resultado.next()) {
			conexao = new Conexoes();
			conexao.setId(resultado.getInt("id"));
			conexao.setEndereco_ip(resultado.getString("ip"));
			conexao.setEndereco_porta(resultado.getString("porta"));
			conexao.setUsuario(resultado.getString("usuario"));
			conexao.setSenha(resultado.getString("senha"));
			conexao.setNome_banco(resultado.getString("nome_banco"));
			conexao.setTipo_banco(resultado.getString("tipo_banco"));
			
			conexoes.add(conexao);
		}
		
		return conexoes;
	}

}











