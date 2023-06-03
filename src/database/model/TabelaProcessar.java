package database.model;

public class TabelaProcessar {
	Integer id, id_processo;
	String nome_tabela_origem, nome_tabela_dest;
	Integer ordem, condição;
	boolean habilitado;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getId_processo() {
		return id_processo;
	}
	public void setId_processo(Integer id_processo) {
		this.id_processo = id_processo;
	}
	public String getNome_tabela_origem() {
		return nome_tabela_origem;
	}
	public void setNome_tabela_origem(String nome_tabela_origem) {
		this.nome_tabela_origem = nome_tabela_origem;
	}
	public String getNome_tabela_dest() {
		return nome_tabela_dest;
	}
	public void setNome_tabela_dest(String nome_tabela_dest) {
		this.nome_tabela_dest = nome_tabela_dest;
	}
	public Integer getOrdem() {
		return ordem;
	}
	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}
	public Integer getCondição() {
		return condição;
	}
	public void setCondição(Integer condição) {
		this.condição = condição;
	}
	public boolean isHabilitado() {
		return habilitado;
	}
	public void setHabilitado(boolean habilitado) {
		this.habilitado = habilitado;
	}
}