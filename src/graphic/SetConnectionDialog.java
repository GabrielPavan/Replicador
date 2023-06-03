package graphic;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

@SuppressWarnings("serial")
public class SetConnectionDialog extends JDialog {
	private JTextField textFieldOrigem;
	private JTextField textFieldDestino;
	
	private String origem, destino;
	
	public String getOrigem() {
		return origem;
	}
	public String getDestino() {
		return destino;
	}

	public SetConnectionDialog(String origem, String destino, JFrame parent) {
		this.origem = origem;
		this.destino = destino;
		setResizable(false);
		setTitle("Defina as conexões");
		setBounds(100, 100, 430, 115);
		getContentPane().setLayout(null);
		setModal(true);
		setLocationRelativeTo(parent);
		
		JLabel lblNewLabel = new JLabel("Origem:");
		lblNewLabel.setBounds(10, 11, 70, 20);
		getContentPane().add(lblNewLabel);
		
		textFieldOrigem = new JTextField();
		textFieldOrigem.setBounds(60, 11, 257, 20);
		textFieldOrigem.setText(origem);
		getContentPane().add(textFieldOrigem);
		textFieldOrigem.setColumns(10);
		
		
		JLabel lblDestino = new JLabel("Destino:");
		lblDestino.setBounds(10, 42, 70, 20);
		getContentPane().add(lblDestino);
		
		textFieldDestino = new JTextField(destino);
		textFieldDestino.setColumns(10);
		textFieldDestino.setBounds(60, 42, 257, 20);
		textFieldDestino.setText(destino);
		getContentPane().add(textFieldDestino);
		
		JButton btnSalvar = new JButton("Salvar");
		btnSalvar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SetConnectionDialog.this.origem = textFieldOrigem.getText();
				SetConnectionDialog.this.destino = textFieldDestino.getText();
				dispose();
			}
		});
		btnSalvar.setBounds(327, 11, 77, 52);
		getContentPane().add(btnSalvar);
		setVisible(true);
	}
}