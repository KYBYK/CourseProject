package page;

import java.util.List;

import javax.swing.JPanel;

import dao.JongDAO;
import util.PageManager;
import vo.ExamJoinVO;
import vo.ExamSubmitVO;
import vo.StudentVO;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class ExamScoreListManagementPage extends JPanel {

	private static final long serialVersionUID = 1L;
	private JTextField textField;
	List<ExamSubmitVO> e_list;
	List<ExamJoinVO> ej_list;
	private JTable table_1;
	JongDAO jdao;
	String idx;

	String[] st_header = {"학번", "학생명", "점수", "답변확인", "채점" };
	Object[][] st_data = new Object[5][3];
	JTableButtonRenderer buttonRenderer;

	/**
	 * Create the frame.
	 */
	public ExamScoreListManagementPage(String idx, String sb_code) {
		this.idx = idx;
		jdao = new JongDAO();
		e_list = jdao.examJoin(idx);
		
		setLayout(null);
		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 800, 600);
		add(panel);
		panel.setLayout(null);
		JLabel lblNewLabel = new JLabel("\uACFC\uBAA9\uBA85");
		lblNewLabel.setFont(new Font("굴림", Font.PLAIN, 26));
		lblNewLabel.setBounds(10, 10, 450, 46);
		panel.add(lblNewLabel);

		JPanel panel_1 = new JPanel();
		panel_1.setBounds(10, 59, 790, 35);
		FlowLayout flowLayout = (FlowLayout) panel_1.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		panel.add(panel_1);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"이름", "학번", "전화번호"}));
		panel_1.add(comboBox);

		textField = new JTextField();
		panel_1.add(textField);
		textField.setColumns(10);
		JButton btnNewButton = new JButton("\uAC80\uC0C9");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//검색할 단어 가져오기
				String searchValue = textField.getText().trim();
				if(searchValue.length() < 1) {
					JOptionPane.showMessageDialog(ExamScoreListManagementPage.this, "검색어가 필요합니다.");
					textField.requestFocus();
					return;
				}
				StudentVO[] ar = jdao.searchStudent(
						comboBox.getSelectedIndex()+"", searchValue,sb_code,idx);
				
				if(ar != null) {
					//ar을 2차원 배열로 변환
					st_data = new Object[ar.length][st_header.length];
					for (int i = 0; i < ar.length; i++) {
						StudentVO stvo = ar[i];
						List<ExamJoinVO> ej_list = jdao.examScore(e_list.get(i).getE_idx());

						st_data[i][0] = stvo.getSt_num();
						st_data[i][1] = stvo.getSt_name();
						if(ej_list.get(i).getEj_score() == null) {
							st_data[i][2] = "";
						}else {
							st_data[i][2] = ej_list.get(i).getEj_score();
						}
						st_data[i][3] = new JButton("답변확인");
						st_data[i][4] = new JButton("채점");
					}
					table_1.setModel(new DefaultTableModel(st_data, st_header));
					table_1.getColumn("답변확인").setCellRenderer(buttonRenderer);
					table_1.getColumn("채점").setCellRenderer(buttonRenderer);
					
				}
			}
		});
		panel_1.add(btnNewButton);
		
		table_1 = new JTable(new ClientTableModel());
		table_1.setShowGrid(true);
		table_1.setGridColor(Color.LIGHT_GRAY);
		setTable();
		buttonRenderer = new JTableButtonRenderer();
		table_1.getColumn("답변확인").setCellRenderer(buttonRenderer);
		table_1.getColumn("채점").setCellRenderer(buttonRenderer);
		table_1.setBounds(0, 0, 1, 1);
		table_1.getTableHeader().setReorderingAllowed(false);

		JScrollPane scrollPane_1 = new JScrollPane(table_1);
		scrollPane_1.setBounds(10, 104, 790, 447);
		panel.add(scrollPane_1);
		
		
		table_1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int column = table_1.getSelectedColumn();
				String num = table_1.getValueAt(table_1.getSelectedRow(),0).toString();
				String code = jdao.studentNumIdx(num);
				// 답변확인 버튼 눌렀을 때
				if(column == 3) {
					ExamAnswerPage eap = new ExamAnswerPage(code);
					PageManager.getInstance().changePage(eap);
				}
				// 답변확인 버튼 눌렀을 때
				if(column == 4) {
					ExamScoreManagemenPage esmp = new ExamScoreManagemenPage(idx, code);// 시험인덱스, 학생인덱스
					PageManager.getInstance().changePage(esmp);
				}
				
			}

		});
	}
	private void setTable() {
		st_data = new Object[e_list.size()][st_header.length];
		
		for (int i = 0; i < e_list.size(); i++) {
			ExamSubmitVO esvo = e_list.get(i);
			StudentVO stvo = esvo.getStvo();

			List<ExamJoinVO> ej_list = jdao.examScore(e_list.get(i).getE_idx());
			st_data[i][0] = esvo.getStvo().getSt_num();
			st_data[i][1] = esvo.getStvo().getSt_name();
			try {				
				if(ej_list.get(i).getEj_score() == null) {
					st_data[i][2] = "";
				}else {
					st_data[i][2] = ej_list.get(i).getEj_score();
				}
			} catch(Exception e) {
				
			}
			st_data[i][3] = new JButton("답변확인");
			st_data[i][4] = new JButton("채점");
		}
		table_1.setModel(new DefaultTableModel(st_data, st_header));
	}

	class ClientTableModel extends DefaultTableModel {

		private final Class<?>[] columnTypes = new Class<?>[] { String.class, String.class, JButton.class,
				JButton.class };

		@Override
		public int getColumnCount() {
			return st_header.length;
		}

		@Override
		public int getRowCount() {
			return st_data.length;
		}

		@Override
		public String getColumnName(int columnIndex) {
			return st_header[columnIndex];
		}

		@Override
		public Class<?> getColumnClass(int columnIndex) {
			return columnTypes[columnIndex];
		}

		@Override
		public Object getValueAt(int rowIndex, int columnIndex) {
			return st_data[rowIndex][columnIndex];
		}
	}

	class JTableButtonRenderer implements TableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			JButton button = (JButton) value;
			return button;
		}
	}
}
