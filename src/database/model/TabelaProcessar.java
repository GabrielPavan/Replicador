package database.model;

public class TabelaProcessar {
	
	private String nome_tb_destino, descriçãoProcesso;
	private Integer id_processo, condicao;
	
	public Integer getId_processo() {
		return id_processo;
	}
	public void setId_processo(Integer id_processo) {
		this.id_processo = id_processo;
	}
	public String getNome_tb_destino() {
		return nome_tb_destino;
	}
	public void setNome_tb_destino(String nome_tb_destino) {
		this.nome_tb_destino = nome_tb_destino;
	}
	public Integer getCondicao() {
		return condicao;
	}
	public void setCondicao(Integer condicao) {
		this.condicao = condicao;
	}
	public String getDescriçãoProcesso() {
		return descriçãoProcesso;
	}
	public void setDescriçãoProcesso(String descriçãoProcesso) {
		this.descriçãoProcesso = descriçãoProcesso;
	}	
}