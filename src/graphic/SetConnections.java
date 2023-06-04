package graphic;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import database.ConnectionFactory;
import database.dao.ConexoesDAO;
import database.model.Conexoes;

import javax.swing.JComboBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class SetConnections extends JDialog {
	private JTextField textFieldOrigem;
	private JTextField textFieldDestino;
	private JComboBox<String> comboBoxDestino;
	private JComboBox<String> comboBoxOrigem;
	
	public SetConnections() {
		setTitle("Definir conexões");
		setBounds(100, 100, 440, 145);
		getContentPane().setLayout(null);
		
		JLabel lblOrigem = new JLabel("Origem:");
		lblOrigem.setBounds(10, 11, 60, 20);
		getContentPane().add(lblOrigem);
		
		JLabel lblDestino = new JLabel("Destino:");
		lblDestino.setBounds(10, 42, 60, 20);
		getContentPane().add(lblDestino);
		
		textFieldOrigem = new JTextField();
		textFieldOrigem.setBounds(65, 11, 245, 20);
		getContentPane().add(textFieldOrigem);
		textFieldOrigem.setColumns(10);
		
		textFieldDestino = new JTextField();
		textFieldDestino.setColumns(10);
		textFieldDestino.setBounds(65, 42, 245, 20);
		getContentPane().add(textFieldDestino);
		
		comboBoxOrigem = new JComboBox<String>();
		comboBoxOrigem.setBounds(320, 10, 100, 22);
		getContentPane().add(comboBoxOrigem);
		
		comboBoxDestino = new JComboBox<String>();
		comboBoxDestino.setBounds(320, 41, 100, 22);
		getContentPane().add(comboBoxDestino);
		
		JButton btnSalvar = new JButton("Salvar");
		btnSalvar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnSalvar.setBounds(320, 72, 100, 23);
		getContentPane().add(btnSalvar);
		
		findConnections();
		setVisible(true);
	}

	private void findConnections() {
		try {
			Connection conn = ConnectionFactory.getConnection("localhost", "5432", "Controle", "postgres",
					"1KUkd2HXpelZ7TkV6zU2", ConnectionFactory.TIPO_BANCO_POSTGRES);
			
			ConexoesDAO dao = new ConexoesDAO(conn);
			ArrayList<Conexoes> conexoes = dao.SelectAll();
				
			Conexoes conexaoOrigem = conexoes.get(0);
			Conexoes conexaoDestino = conexoes.get(1);
			textFieldOrigem.setText(conexaoOrigem.getEndereco_ip() + ":" + conexaoOrigem.getEndereco_porta() + "/" + conexaoOrigem.getNome_banco());
			textFieldDestino.setText(conexaoDestino.getEndereco_ip() + ":" + conexaoDestino.getEndereco_porta() + "/" + conexaoDestino.getNome_banco());
			
			comboBoxDestino.addItem(conexaoOrigem.getTipo_banco());
			comboBoxDestino.addItem(conexaoDestino.getTipo_banco());
			comboBoxDestino.setSelectedItem(conexaoDestino.getTipo_banco());
			
			comboBoxOrigem.addItem(conexaoOrigem.getTipo_banco());
			comboBoxOrigem.addItem(conexaoDestino.getTipo_banco());
			comboBoxOrigem.setSelectedItem(conexaoOrigem.getTipo_banco());
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}