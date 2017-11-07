package test;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import com.sap.conn.jco.JCoException;
import com.sap.conn.jco.JCoTable;

import util.StepByStepClient;

public class Main_Test {
	private static String ENTRIESName = "ENTRIES";
	private static String FunctionName = "RFC_GET_TABLE_ENTRIES";

	public static void main(String[] args) throws JCoException {
		StepByStepClient.step2ConnectUsingPool();
	}

	public static  void CallRFCTable() {
		JFrame frame = new JFrame();
		JDialog dialog = new JDialog(frame);
		dialog.setSize(300, 400);
		JLabel label = new JLabel();
		label.setText("ssss");
		dialog.add(label);
		dialog.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JOptionPane.showMessageDialog(dialog, "1");
		try {
			JOptionPane.showMessageDialog(dialog, "2");

			JCoTable table = StepByStepClient.CallRFCTable(FunctionName, dialog);
			JOptionPane.showMessageDialog(dialog, "3");

			// JFrame frame=new JFrame();
			// JDialog dialog=new JDialog(frame);
			// dialog.setSize(300,400);
			// JLabel label=new JLabel();
			StringBuilder builder = new StringBuilder();
			JOptionPane.showMessageDialog(dialog, "4");

			for (int i = 0; i < table.getNumRows(); i++) {
				JOptionPane.showMessageDialog(dialog, "5");

				table.setRow(i);
				builder.append(table.getString("WA"));
				JOptionPane.showMessageDialog(dialog, "6");

			}
			label.setText(builder.toString());
			dialog.add(label);
			dialog.setVisible(true);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} catch (JCoException e) {
			e.printStackTrace();
		}
	}

}
