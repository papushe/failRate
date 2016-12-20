package failrate.papushe;
import java.awt.*;
import java.awt.datatransfer.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.event.*;
import javax.swing.border.LineBorder;

public class FailRate extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	double te1 =0;
	double te2 =0;
	private JButton btnClearFields;
	private JLabel label;
	private JLabel lblNewLabel;
	private JLabel lblPayAttentionTo;
	private JLabel lblVerifyYouHave;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FailRate frame = new FailRate();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public FailRate() {
		
		setTitle("API FailRate");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 533, 362);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblAddErrNumbers = new JLabel("500 Errors by API by DC:");
		lblAddErrNumbers.setToolTipText("");
		lblAddErrNumbers.setBounds(20, 133, 162, 16);
		contentPane.add(lblAddErrNumbers);
		
		JLabel lblAllApiCalls = new JLabel("All API Calls by DC:");
		lblAllApiCalls.setBounds(20, 169, 168, 16);
		contentPane.add(lblAllApiCalls);
		
		JButton btnCopyResult = new JButton("Copy Result");
		btnCopyResult.setEnabled(false);
		btnCopyResult.setBounds(370, 243, 120, 29);
		btnCopyResult.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringSelection stringSelection = new StringSelection (textField_2.getText());
				Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
				clpbrd.setContents (stringSelection, null);
				}
		});
		contentPane.add(btnCopyResult);
		
		JButton btnNewButton = new JButton("Calculate");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try{
					te1 = Double.parseDouble(textField.getText());
					te1*=100;
					te2 = Double.parseDouble(textField_1.getText());
					if (te2 == 0) {
						textField_2.setText("Can't divide by 0");
						textField_2.setBackground(Color.red);
						textField_1.setText("");
					}
					else{
						String numberAsString = Double.toString(te1/te2);
						textField_2.setText(numberAsString);
						btnCopyResult.setEnabled(true);
					}
				}
				catch(NumberFormatException x){
					textField_2.setBackground(Color.red);
					textField_2.setText("Error : "+ x.getMessage());
				}
			}
		});
		btnNewButton.setBounds(196, 203, 120, 29);
		contentPane.add(btnNewButton);
		
		JLabel lblTheApiFailrate = new JLabel("The API FailRate is:");
		lblTheApiFailrate.setBounds(20, 249, 135, 16);
		contentPane.add(lblTheApiFailrate);
		
		textField = new JTextField();
		textField.setBounds(186, 128, 140, 26);
		contentPane.add(textField);
		textField.setColumns(20);
		
		textField_1 = new JTextField();
		textField_1.setBounds(186, 161, 140, 26);
		contentPane.add(textField_1);
		textField_1.setColumns(20);
		
		textField_2 = new JTextField();
		textField_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		textField_2.setBounds(155, 244, 200, 26);
		contentPane.add(textField_2);
		textField_2.setColumns(20);
		textField_2.setEditable(false);
		
		btnClearFields = new JButton("Clear Fields");
		btnClearFields.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				textField.setText("");
				textField_1.setText("");
				textField_2.setText("");
				btnCopyResult.setEnabled(false);
				textField_2.setBackground(Color.white);
			}
		});
		btnClearFields.setBounds(196, 282, 120, 29);
		contentPane.add(btnClearFields);
		
		label = new JLabel("%");
		label.setBounds(358, 249, 61, 16);
		contentPane.add(label);
		
		lblNewLabel = new JLabel("Amit Papushe Shely | Gigya 2016");
		lblNewLabel.setBounds(325, 318, 192, 16);
		contentPane.add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("Make sure you are looking and counting only at the time frame of the incident.");
		lblNewLabel_1.setBounds(20, 23, 497, 16);
		contentPane.add(lblNewLabel_1);
		
		lblPayAttentionTo = new JLabel("Pay attention to the @type to avoid double counting.");
		lblPayAttentionTo.setBounds(20, 51, 458, 16);
		contentPane.add(lblPayAttentionTo);
		
		lblVerifyYouHave = new JLabel("Verify you have filtered the correct DC in the query.");
		lblVerifyYouHave.setBounds(20, 79, 375, 16);
		contentPane.add(lblVerifyYouHave);
	}
}