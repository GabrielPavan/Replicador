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
import javax.swing.SwingConstants;
import javax.swing.JPasswordField;
import javax.swing.JSeparator;

@SuppressWarnings("serial")
public class SetConnections extends JDialog {
	private JTextField textFieldOrigem;
	private JTextField textFieldDestino;
	private JComboBox<String> comboBoxDestino;
	private JComboBox<String> comboBoxOrigem;
	private JTextField textFieldUserOrigin;
	private JPasswordField passwordFieldOrigin;
	private JTextField textFieldUserDest;
	private JPasswordField passwordFieldDest;
	
	public SetConnections() {
		setResizable(false);
		setTitle("Definir conexões");
		setBounds(100, 100, 400, 290);
		getContentPane().setLayout(null);
		
		JLabel lblOrigem = new JLabel("Origem:");
		lblOrigem.setHorizontalAlignment(SwingConstants.RIGHT);
		lblOrigem.setBounds(10, 11, 53, 20);
		getContentPane().add(lblOrigem);
		
		JLabel lblDestino = new JLabel("Destino:");
		lblDestino.setHorizontalAlignment(SwingConstants.RIGHT);
		lblDestino.setBounds(10, 114, 53, 20);
		getContentPane().add(lblDestino);
		
		textFieldOrigem = new JTextField();
		textFieldOrigem.setBounds(65, 11, 290, 20);
		getContentPane().add(textFieldOrigem);
		textFieldOrigem.setColumns(10);
		
		textFieldDestino = new JTextField();
		textFieldDestino.setColumns(10);
		textFieldDestino.setBounds(65, 114, 290, 20);
		getContentPane().add(textFieldDestino);
		
		comboBoxOrigem = new JComboBox<String>();
		comboBoxOrigem.setBounds(255, 42, 100, 20);
		getContentPane().add(comboBoxOrigem);
		
		comboBoxDestino = new JComboBox<String>();
		comboBoxDestino.setBounds(255, 145, 100, 20);
		getContentPane().add(comboBoxDestino);
		
		JButton btnSalvar = new JButton("Salvar");
		btnSalvar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnSalvar.setBounds(255, 217, 100, 23);
		getContentPane().add(btnSalvar);
		
		JLabel lblUserOrigin = new JLabel("Usuário:");
		lblUserOrigin.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUserOrigin.setBounds(10, 42, 53, 20);
		getContentPane().add(lblUserOrigin);
		
		textFieldUserOrigin = new JTextField();
		textFieldUserOrigin.setColumns(10);
		textFieldUserOrigin.setBounds(65, 42, 180, 20);
		getContentPane().add(textFieldUserOrigin);
		
		passwordFieldOrigin = new JPasswordField();
		passwordFieldOrigin.setBounds(65, 67, 180, 20);
		getContentPane().add(passwordFieldOrigin);
		
		JLabel lblPasswordOrigin = new JLabel("Senha:");
		lblPasswordOrigin.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPasswordOrigin.setBounds(10, 67, 53, 20);
		getContentPane().add(lblPasswordOrigin);
		
		JLabel lblUserDest = new JLabel("Usuário:");
		lblUserDest.setHorizontalAlignment(SwingConstants.RIGHT);
		lblUserDest.setBounds(10, 145, 53, 20);
		getContentPane().add(lblUserDest);
		
		JLabel lblPasswordDest = new JLabel("Senha:");
		lblPasswordDest.setHorizontalAlignment(SwingConstants.RIGHT);
		lblPasswordDest.setBounds(10, 176, 53, 20);
		getContentPane().add(lblPasswordDest);
		
		textFieldUserDest = new JTextField();
		textFieldUserDest.setColumns(10);
		textFieldUserDest.setBounds(65, 145, 180, 20);
		getContentPane().add(textFieldUserDest);
		
		passwordFieldDest = new JPasswordField();
		passwordFieldDest.setBounds(65, 176, 180, 20);
		getContentPane().add(passwordFieldDest);
		
		JSeparator separator01 = new JSeparator();
		separator01.setBounds(10, 98, 360, 5);
		getContentPane().add(separator01);
		
		JSeparator separator02 = new JSeparator();
		separator02.setBounds(10, 207, 360, 2);
		getContentPane().add(separator02);
		
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
			textFieldUserOrigin.setText(conexaoOrigem.getUsuario());
			passwordFieldOrigin.setText(conexaoOrigem.getSenha());
			textFieldUserDest.setText(conexaoDestino.getUsuario());
			passwordFieldDest.setText(conexaoDestino.getSenha());
			
			comboBoxDestino.addItem(conexaoOrigem.getTipo_banco());
			comboBoxDestino.addItem(conexaoDestino.getTipo_banco());
			comboBoxDestino.setSelectedItem(conexaoDestino.getTipo_banco());
			
			comboBoxOrigem.addItem(conexaoOrigem.getTipo_banco());
			comboBoxOrigem.addItem(conexaoDestino.getTipo_banco());
			comboBoxOrigem.setSelectedItem(conexaoOrigem.getTipo_banco());
			
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}