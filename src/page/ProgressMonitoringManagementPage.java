package page;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class ProgressMonitoringManagementPage extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Create the panel.
	 */
	public ProgressMonitoringManagementPage() {
		setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
	}

}
