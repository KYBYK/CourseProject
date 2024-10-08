package page;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPanel;

import component.piechart.ModelPieChart;
import component.piechart.PieChart;

import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JComboBox;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import component.piechart.PieChart.PeiChartType;
import dao.YubinDAO;
import util.LoginManager;
import vo.ExamJoinVO;
import vo.ExamVO;
import vo.LoginVO;
import vo.StudentSubjectVO;
import vo.SubjectVO;

import javax.swing.DefaultComboBoxModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.SystemColor;

public class ProgressMonitoringManagementPage extends JPanel {
	private static final long serialVersionUID = 1L;

	private final String[] TABLE_COLUMN_ARRAY = {"학번", "학생 이름", "과목명", "시험명", "점수"};
	
	private final String SUBMIT = "제출";
	private final String NOT_SUBMIT = "미제출";
	private final String SCORE_RANGE1 = "0~39";
	private final String SCORE_RANGE2 = "40~59";
	private final String SCORE_RANGE3 = "60~79";
	private final String SCORE_RANGE4 = "80~100";
	private final String PASS = "합격";
	private final String FAIL = "불합격";
	
	// 컴포넌트 멤버변수
	JPanel panel;
	private JTable table;
	private PieChart examSubmit_pc;
	private PieChart point_pc;
	private PieChart pass_pc;
	private JComboBox subject_cb;
	private JComboBox exam_cb;

	// 추가 클래스 내부에서 사용하는 멤버변수
	YubinDAO dao = new YubinDAO();
	private List<SubjectVO> subjectList;
	private List<ExamVO> examList;
	private List<ExamJoinVO> examJoinList;
	private List<StudentSubjectVO> studentSubjectList;
	
	private String currSubjectIndex;
	private String currExamIndex;
	

	@SuppressWarnings("unlikely-arg-type")
	public ProgressMonitoringManagementPage() {
		setSize(new Dimension(800, 600));
		setLayout(new BorderLayout(0, 0));

		panel = new JPanel();
		panel.setBackground(SystemColor.inactiveCaptionBorder);
		panel.setSize(new Dimension(800, 600));
		add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		subject_cb = new JComboBox();
		exam_cb = new JComboBox();
		
		// 권한에 맞는 과목이 나오는 ComboBox
		subject_cb.setBounds(12, 26, 155, 30);
		setCombobox();
		subject_cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setExamCombobox();
			}
		});
		panel.add(subject_cb);
		
		// 과목별 시험이 나오면 ComboBox
		exam_cb.setBounds(179, 26, 235, 30);
		exam_cb.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setPiechart();
			}
		});
		panel.add(exam_cb);
		
		// 시험 응시 현황 타이틀과 파이차트
		JLabel lblNewLabel = new JLabel("시험 응시 현황");
		lblNewLabel.setFont(new Font("나눔고딕", Font.BOLD, 20));
		lblNewLabel.setBounds(12, 86, 140, 19);
		panel.add(lblNewLabel);
		examSubmit_pc = new PieChart(this);
		examSubmit_pc.setLocation(0, 86);
		examSubmit_pc.setSize(new Dimension(258, 303));
		examSubmit_pc.setFont(new Font("프리젠테이션 4 Regular", Font.PLAIN, 19));
		examSubmit_pc.setChartType(PieChart.PeiChartType.DONUT_CHART);
		panel.add(examSubmit_pc);

		// 점수 분포 타이틀과 파이차트
		JLabel lblNewLabel_1 = new JLabel("점수 분포");
		lblNewLabel_1.setFont(new Font("나눔고딕", Font.BOLD, 20));
		lblNewLabel_1.setBounds(278, 86, 140, 19);
		panel.add(lblNewLabel_1);
		point_pc = new PieChart(this);
		point_pc.setSize(new Dimension(262, 242));
		point_pc.setFont(new Font("Dialog", Font.PLAIN, 19));
		point_pc.setChartType(PeiChartType.DONUT_CHART);
		point_pc.setBounds(259, 86, 269, 303);
		panel.add(point_pc);
		
		// 합격 현황 타이틀과 파이차트
		JLabel lblNewLabel_1_1 = new JLabel("합격 현황");
		lblNewLabel_1_1.setFont(new Font("나눔고딕", Font.BOLD, 20));
		lblNewLabel_1_1.setBounds(545, 86, 140, 19);
		panel.add(lblNewLabel_1_1);
		pass_pc = new PieChart(this);
		pass_pc.setSize(new Dimension(262, 242));
		pass_pc.setFont(new Font("Dialog", Font.PLAIN, 19));
		pass_pc.setChartType(PeiChartType.DONUT_CHART);
		pass_pc.setBounds(530, 86, 269, 303);
		panel.add(pass_pc);


		// 눌린 파이차트 요소에 속하는 학생 목록 테이블
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBackground(SystemColor.inactiveCaptionBorder);
		scrollPane.setBounds(12, 390, 776, 200);
		panel.add(scrollPane);
		table = new JTable();
		table.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		table.setShowGrid(true);
		table.setGridColor(Color.LIGHT_GRAY);
		scrollPane.setViewportView(table);
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			TABLE_COLUMN_ARRAY
		) {
			boolean[] columnEditables = new boolean[] {
					false, false, false, false, false
				};
				public boolean isCellEditable(int row, int column) {
					return columnEditables[column];
				}
			});
		table.getColumnModel().getColumn(0).setResizable(false);
		table.getColumnModel().getColumn(0).setPreferredWidth(112);
		table.getColumnModel().getColumn(1).setResizable(false);
		table.getColumnModel().getColumn(1).setPreferredWidth(87);
		table.getColumnModel().getColumn(2).setResizable(false);
		table.getColumnModel().getColumn(2).setPreferredWidth(99);
		table.getColumnModel().getColumn(3).setResizable(false);
		table.getColumnModel().getColumn(3).setPreferredWidth(143);
		table.getColumnModel().getColumn(4).setResizable(false);
		table.getColumnModel().getColumn(4).setPreferredWidth(141);
	}

	private void setCombobox() {
		LoginVO loginMember = LoginManager.getInstance().getLoginMember();
		String professorIdx = null;
		boolean isProfessor = loginMember.getChk_role().equals(LoginManager.PROFESSOR);
		if (isProfessor) {
			professorIdx = LoginManager.getInstance().getProfessorInfo().getP_idx();
		}
		subjectList = dao.getSubjectList(professorIdx);
		if (subjectList != null) {
			ArrayList<String> subjectNameList = new ArrayList<>();
			subjectNameList.add("--- 과목 선택 ---");
			for (SubjectVO vo : subjectList) {
				subjectNameList.add(vo.getSb_name());
			}
			subject_cb.setModel(new DefaultComboBoxModel(subjectNameList.toArray()));
		} else {
			subject_cb.setModel(new DefaultComboBoxModel(new String[] {}));
		}
		setExamCombobox();
	}

	private void setExamCombobox() {
		if(subject_cb.getSelectedIndex() == 0) {
			exam_cb.setModel(new DefaultComboBoxModel(new String[] {"--- 과목을 선택해주세요. ---"}));
			return;
		}
		currSubjectIndex = subjectList.get(subject_cb.getSelectedIndex()-1).getSb_idx();
		examList = dao.getExamList(currSubjectIndex);
		if (examList != null) {
			ArrayList<String> examNameList = new ArrayList<>();
			examNameList.add("--- 시험 선택 ---");
			for (ExamVO vo : examList) {
				examNameList.add(vo.getE_name());
			}
			exam_cb.setModel(new DefaultComboBoxModel(examNameList.toArray()));
		} else {
			exam_cb.setModel(new DefaultComboBoxModel(new String[] {"--- 과목을 선택해주세요. ---"}));
		}
	}

	private void setPiechart() {
		if(exam_cb.getSelectedIndex() == 0) return;
		currExamIndex = examList.get(exam_cb.getSelectedIndex()-1).getE_idx();
		// 1. 해당 과목을 수강하는 수강생 정보를 StudentSubjectVO에서 가져온다.
		studentSubjectList = dao.getStudentSubjectList(currSubjectIndex);
		// 2. 해당 과목 해당 시험 제출 정보와 학생 정보를 examJoinVO에서 가져온다.
		examJoinList = dao.getExamJoinList(currExamIndex);
		
		// piechart1 : 제출 현황 => 해당 과목의 수강생 - 응시학생 = 미응시 학생
		examSubmit_pc.clearData();
		examSubmit_pc.setSelectedIndex(-1);
		examSubmit_pc.addData(new ModelPieChart("제출", examJoinList.size(), makeColor()));
		examSubmit_pc.addData(new ModelPieChart("미제출", studentSubjectList.size() - examJoinList.size(), makeColor()));
		// piechart2 : 점수 분포
		int sec1 = 0; // 0 ~ 39
		int sec2 = 0; // 40 ~ 59
		int sec3 = 0; // 60 ~ 79
		int sec4 = 0; // 80 ~ 100
		for (ExamJoinVO vo : examJoinList) {
			if (Integer.parseInt(vo.getEj_score()) >= 80) {
				sec4++;
			} else if (Integer.parseInt(vo.getEj_score()) >= 60) {
				sec3++;
			} else if (Integer.parseInt(vo.getEj_score()) >= 40) {
				sec2++;
			} else if (Integer.parseInt(vo.getEj_score()) >= 0) {
				sec1++;
			}
		}
		point_pc.clearData();
		point_pc.setSelectedIndex(-1);
		point_pc.addData(new ModelPieChart("0~39", sec1, makeColor()));
		point_pc.addData(new ModelPieChart("40~59", sec2, makeColor()));
		point_pc.addData(new ModelPieChart("60~79", sec3, makeColor()));
		point_pc.addData(new ModelPieChart("80~100", sec4, makeColor()));

		// piechart3 : 합격 현황 제출한 학생 중 60점 이상인 학생의 수
		int passCnt = 0;
		for (ExamJoinVO vo : examJoinList) {
			if (Integer.parseInt(vo.getEj_score()) >= 60) {
				passCnt++;
			}
		}
		pass_pc.clearData();
		pass_pc.setSelectedIndex(-1);
		pass_pc.addData(new ModelPieChart("합격", passCnt, new Color(0, 204, 0)));
		pass_pc.addData(new ModelPieChart("불합격", examJoinList.size() - passCnt, new Color(160, 160, 160)));
	}

	private Color makeColor() {
		int r = (int) (Math.random() * 255);
		int g = (int) (Math.random() * 255);
		int b = (int) (Math.random() * 255);
		return new Color(r, g, b);
	}
	
	public void makeTable(String chartItem) {
		List<ExamJoinVO> list = new ArrayList<>();
		
		if(chartItem.trim().equals(NOT_SUBMIT)) {
			setNotSubmitTable();
			return;
		}
		switch (chartItem.trim()) {
		case SUBMIT:
			// examJoinList에 있는 학생 정보
			list = examJoinList;
			break;
		case SCORE_RANGE1:
			// examJoinList에 점수가 해당 범위에 속하는 학생 정보
			list = getListByScoreInRange(0, 39);
			break;
		case SCORE_RANGE2 :
			list = getListByScoreInRange(40, 59);
			break;
		case SCORE_RANGE3:
			list = getListByScoreInRange(60, 79);
			break;
		case SCORE_RANGE4:
			list = getListByScoreInRange(80, 100);
			break;
		case PASS:
			// examJoinList에 점수가 60점 이상인 학생 정보
			list = getListByScoreInRange(60, 100);
			break;
		case FAIL:
			// examJoinList에 점수가 60점 미만인 학생 정보
			list = getListByScoreInRange(0, 59);
			break;
		}
		
		ArrayList<Map<String, String>> mapList = new ArrayList<>();
		for(ExamJoinVO vo : list) {			
			Map<String, String> map = new HashMap<>();
			map.put(TABLE_COLUMN_ARRAY[0], vo.getStvo().getSt_num());
			map.put(TABLE_COLUMN_ARRAY[1], vo.getStvo().getSt_name());
			map.put(TABLE_COLUMN_ARRAY[2], (String)subject_cb.getSelectedItem());
			map.put(TABLE_COLUMN_ARRAY[3], (String)exam_cb.getSelectedItem());
			map.put(TABLE_COLUMN_ARRAY[4], vo.getEj_score());
			mapList.add(map);
		}
		setTable(mapList);
	}
	
	private void setNotSubmitTable() {
		// studentSubjectList에는 있지만 examJoinList에 없는 학생 정보
		List<ExamJoinVO> examJoinList_copy = examJoinList;
		List<StudentSubjectVO> studentSubjectList_copy = studentSubjectList;
		for (ExamJoinVO sb_idx : examJoinList_copy) {
			for (int i = 0; i < studentSubjectList_copy.size(); i++) {
				StudentSubjectVO a_idx = studentSubjectList_copy.get(i);
				if (a_idx.getStvo().getSt_idx().contains(sb_idx.getStvo().getSt_idx())) {
					studentSubjectList_copy.remove(i);
					break;
				}
			}
		}
		String[] columnName = { TABLE_COLUMN_ARRAY[0], TABLE_COLUMN_ARRAY[1], TABLE_COLUMN_ARRAY[2], TABLE_COLUMN_ARRAY[3] };
		String[][] data = new String[studentSubjectList_copy.size()][columnName.length];
		for (int i = 0; i < studentSubjectList_copy.size(); i++) {
			data[i][0] = studentSubjectList_copy.get(i).getStvo().getSt_num();
			data[i][1] = studentSubjectList_copy.get(i).getStvo().getSt_name();
			data[i][2] = (String) subject_cb.getSelectedItem();
			data[i][3] = (String) exam_cb.getSelectedItem();
		}
		table.setModel(new DefaultTableModel(data, columnName) {
			boolean[] columnEditables = new boolean[] {
					false, false, false, false, false
				};
				public boolean isCellEditable(int row, int column) {
					return columnEditables[column];
				}
			});
	}
	
	private void setTable(ArrayList<Map<String, String>> list) {
		String[][] data = new String[list.size()][TABLE_COLUMN_ARRAY.length];
		for(int i=0; i<list.size(); i++) {
			for(int j=0; j<TABLE_COLUMN_ARRAY.length; j++) {
				data[i][j] = list.get(i).get(TABLE_COLUMN_ARRAY[j]);
			}
		}
		table.setModel(new DefaultTableModel(data, TABLE_COLUMN_ARRAY) {
			boolean[] columnEditables = new boolean[] {
					false, false, false, false, false
				};
				public boolean isCellEditable(int row, int column) {
					return columnEditables[column];
				}
			});
	}
	
	private List<ExamJoinVO> getListByScoreInRange(int minRange, int maxRange) {
		List<ExamJoinVO> list = new ArrayList<>();
		for(ExamJoinVO vo : examJoinList) {
			int score = Integer.parseInt(vo.getEj_score());
			if(score >= minRange && score <= maxRange) {
				list.add(vo);
			}
		}
		return list;
	}
	
	public void resetAllPieChartSelectedIndex(PieChart pie) {
		if(pie == examSubmit_pc) {
			point_pc.setSelectedIndex(-1);
			pass_pc.setSelectedIndex(-1);
		} else if(pie == point_pc) {
			examSubmit_pc.setSelectedIndex(-1);
			pass_pc.setSelectedIndex(-1);
		} else if(pie == pass_pc) {
			examSubmit_pc.setSelectedIndex(-1);
			point_pc.setSelectedIndex(-1);
		} 
	}
	
	private void setTableEditable() {
		
	}
}
