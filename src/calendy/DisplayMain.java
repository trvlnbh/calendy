package calendy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class DisplayMain extends JFrame implements ActionListener, ListSelectionListener {
	private long userKey;
	private int dateKey, selectedIndex;
	private SelectUser select;
	private Calendar cal, cal2, cal3;
	private JMenuBar menuBar;
	private JMenu menuSet;
	private JMenuItem sChange, sExit;
	private JPanel centerPan, yearPan, monthPan, memoCoverPan, todayPan, memoPan, memosetPan;
	private JPanel[] dayPan;
	private JLabel[] weekLabel, dayLabel, memoLabel;
	private JButton[] dayButton;
	private JButton leftArrow, rightArrow;
	private JTextField year, month;
	private JTextField addMemo;
	private JLabel memoTitle, todayTitle;
	private JButton addMemoBtn, delMemoBtn;
	private Vector<String> memoVec, todayVec;
	private JList<String> memoList, todayList;
	private int NowYear, NowMonth, NowFirstDayofWeek, BeforeFirstDayofWeek;
	private int tmpYear, tmpMonth, tmpDay;
	
	private URL iconurl = getClass().getClassLoader().getResource("icon_note.png");
	private ImageIcon note = new ImageIcon(iconurl);
	private URL fIconurl = getClass().getClassLoader().getResource("icon_cal.png");
	
	public DisplayMain(SelectUser s) {
		select = s;
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(select.getSelectedName() + "'s Day");
		setSize(951, 780);
		
		Toolkit toolkt = Toolkit.getDefaultToolkit();
		Image iconimg = toolkt.getImage(fIconurl);
		setIconImage(iconimg);
		
		Container contentPane = getContentPane();
		contentPane.setBackground(Color.WHITE);
		
		userKey = select.getSelectedKey();
		
		//--------------------------------------------------------------------
		//								Menu
		//--------------------------------------------------------------------
		
		menuBar = new JMenuBar();
		menuBar.setBackground(new Color(0xF5F5F5));
		
		menuSet = new JMenu("¸Þ´º");
		menuSet.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 15));
		sChange = new JMenuItem("»ç¿ëÀÚ º¯°æ");
		sChange.addActionListener(this);
		sChange.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 13));
		sChange.setBackground(new Color(0xF5F5F5));
		sExit = new JMenuItem("Á¾·á");
		sExit.addActionListener(this);
		sExit.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 13));
		sExit.setBackground(new Color(0xF5F5F5));
		
		menuSet.add(sChange);
		menuSet.addSeparator();
		menuSet.add(sExit);
		menuBar.add(menuSet);
		
		setJMenuBar(menuBar);
		
		//--------------------------------------------------------------------
		//								Calendar
		//--------------------------------------------------------------------
		
		cal = Calendar.getInstance();
		cal2 = Calendar.getInstance();
		cal3 = Calendar.getInstance();
		
		NowYear = cal.get(Calendar.YEAR);
		NowMonth = cal.get(Calendar.MONTH);
		tmpYear = cal.get(Calendar.YEAR);
		tmpMonth = cal.get(Calendar.MONTH);
		tmpDay = cal.get(Calendar.DATE) - 1;
		
		cal2.set(NowYear, NowMonth, 1);
		NowFirstDayofWeek = cal2.get(Calendar.DAY_OF_WEEK);
		BeforeFirstDayofWeek = -1;
		
		dayLabel = new JLabel[37];
		dayButton = new JButton[37];
		memoLabel = new JLabel[37];
		
		memoVec = new Vector<>();
		todayVec = new Vector<>();
		todayList = new JList<>(todayVec);
		today_memo_load();
		
		centerPan = new JPanel();
		centerPan.setLayout(new BorderLayout());
		centerPan.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		centerPan.setBackground(Color.WHITE);
		contentPane.add(centerPan, BorderLayout.CENTER);

		yearPan = new JPanel();
		yearPan.setBackground(Color.WHITE);
		
		monthPan = new JPanel();
		monthPan.setLayout(new GridLayout(7, 7, 1, 1));
		monthPan.setBackground(new Color(0x193441));
		
		memoCoverPan = new JPanel();
		memoCoverPan.setLayout(new BorderLayout());
		memoCoverPan.setBackground(Color.WHITE);
		
		todayPan = new JPanel();
		todayPan.setLayout(new BorderLayout());
		todayPan.setBackground(Color.WHITE);
		
		memoPan = new JPanel();
		memoPan.setLayout(new BorderLayout());
		memoPan.setBackground(Color.WHITE);
		
		memosetPan = new JPanel();
		memosetPan.setBackground(Color.WHITE);
		
		//		SELECT
		
		leftArrow = new JButton("<");
		leftArrow.addActionListener(this);
		rightArrow = new JButton(">");
		rightArrow.addActionListener(this);
		
		String PrintNowYear = Integer.toString(NowYear);
		String PrintNowMonth = Integer.toString(NowMonth + 1);
		
		year = new JTextField(PrintNowYear, 5);
		year.setFont(new Font("¸¼Àº °íµñ", Font.ITALIC, 17));
		year.setHorizontalAlignment(JTextField.CENTER);
		year.addActionListener(this);
		month = new JTextField(PrintNowMonth, 3);
		month.setFont(new Font("¸¼Àº °íµñ", Font.ITALIC, 17));
		month.setHorizontalAlignment(JTextField.CENTER);
		month.addActionListener(this);
		
		yearPan.add(leftArrow);
		yearPan.add(year);
		yearPan.add(month);
		yearPan.add(rightArrow);
		
		//		CALENDAR
		
		weekLabel = new JLabel[7];
		weekLabel[0] = new JLabel("      SUN     ");
		weekLabel[0].setForeground(new Color(0xEA2E49));
		weekLabel[1] = new JLabel("      MON     ");
		weekLabel[2] = new JLabel("      TUE     ");
		weekLabel[3] = new JLabel("      WED     ");
		weekLabel[4] = new JLabel("      THU     ");
		weekLabel[5] = new JLabel("      FRI     ");
		weekLabel[6] = new JLabel("      SAT     ");
		weekLabel[6].setForeground(new Color(0x77C4D3));
		for (int i = 0; i < 7; i++) {
			weekLabel[i].setOpaque(true);
			weekLabel[i].setBackground(Color.WHITE);
			weekLabel[i].setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 20));
			monthPan.add(weekLabel[i]);
		}
		
		dayPan = new JPanel[42];
		for (int i = 0; i < 42; i++) {
			dayPan[i] = new JPanel();
			dayPan[i].setBackground(Color.WHITE);
			dayPan[i].setLayout(new BorderLayout());
			monthPan.add(dayPan[i]);
		}
		
		set_calendar();
		
		//		MEMO
		
		memoList = new JList<>(memoVec);
		memoList.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 11));
		memoList.addListSelectionListener(this);
		JScrollPane scrl = new JScrollPane(memoList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		memoTitle = new JLabel("¢º Memo (ÇÑ±Û ÃÖ´ë 70ÀÚ)");
		memoTitle.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 12));
		addMemo = new JTextField(40);
		addMemo.addActionListener(this);
		addMemo.setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 14));
		addMemoBtn = new JButton("Ãß°¡");
		addMemoBtn.addActionListener(this);
		addMemoBtn.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 12));
		delMemoBtn = new JButton("»èÁ¦");
		delMemoBtn.addActionListener(this);
		delMemoBtn.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 12));
		
		memosetPan.add(addMemo);
		memosetPan.add(addMemoBtn);
		memosetPan.add(delMemoBtn);
		memoPan.add(memoTitle, BorderLayout.NORTH);
		memoPan.add(scrl, BorderLayout.CENTER);
		memoPan.add(memosetPan, BorderLayout.SOUTH);
		
		todayTitle = new JLabel("¢º Today's Memo                                               ");
		todayTitle.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 12));
		todayList.setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 14));
		JScrollPane scrl2 = new JScrollPane(todayList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		todayPan.add(todayTitle, BorderLayout.NORTH);
		todayPan.add(scrl2, BorderLayout.CENTER);
		
		memoCoverPan.add(memoPan, BorderLayout.WEST);
		memoCoverPan.add(todayPan, BorderLayout.EAST);
		
		
		//--------------------------------------------------------------------
		
		centerPan.add(yearPan, BorderLayout.NORTH);
		centerPan.add(monthPan, BorderLayout.CENTER);
		centerPan.add(memoCoverPan, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == year) {
			store_beforedata();
			NowYear = Integer.parseInt(year.getText());
			set_yearmonth();
		}
		else if (obj == month) {
			store_beforedata();
			NowMonth = Integer.parseInt(month.getText()) - 1;
			set_yearmonth();
		}
		else if (obj == leftArrow) {
			store_beforedata();
			if (NowYear >= 2000) {
				if (NowMonth == 0) {
					if (NowYear == 2000) {
						return;
					}
					NowYear -= 1;
					NowMonth = 11;
				}
				else {
					NowMonth -= 1;
				}
				set_yearmonth();
			}
		}
		else if (obj == rightArrow) {
			store_beforedata();
			if (NowYear <= 2099) {
				if (NowMonth == 11) {
					if (NowYear == 2099) {
						return;
					}
					NowYear += 1;
					NowMonth = 0;
				}
				else {
					NowMonth += 1;
				}
				set_yearmonth();
			}
		}
		else if (obj == addMemo) {
			memo_add();
		}
		else if (obj == addMemoBtn) {
			memo_add();
		}
		else if (obj == delMemoBtn) {
			memo_del();
		}
		else if (obj == sChange) {
			new SelectUser();
			this.setVisible(false);
		}
		else if (obj == sExit) {
			System.exit(0);
		}
		else {
			find_button(obj);
			memo_load();
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			selectedIndex = memoList.getSelectedIndex();
		}
	}
	
	public void store_beforedata() {
		cal3.set(NowYear, NowMonth, 1);
		BeforeFirstDayofWeek = cal3.get(Calendar.DAY_OF_WEEK);
	}
	
	public void set_yearmonth() {
		year.setText(Integer.toString(NowYear));
		month.setText(Integer.toString(NowMonth + 1));
		
		cal2.set(NowYear, NowMonth, 1);
		NowFirstDayofWeek = cal2.get(Calendar.DAY_OF_WEEK);
		
		set_calendar();
	}
	
	public void set_calendar() {
		int StartFlagOld = get_start_flag_of_month(BeforeFirstDayofWeek);
		int StartFlagNew = get_start_flag_of_month(NowFirstDayofWeek);
		
		if (BeforeFirstDayofWeek != -1) {
			for (int i = StartFlagOld; i < cal3.getActualMaximum(Calendar.DAY_OF_MONTH) + StartFlagOld; i++) {
				dayPan[i].remove(dayLabel[i]);
				dayPan[i].remove(dayButton[i]);
				dayPan[i].remove(memoLabel[i]);
				dayPan[i].validate();
				dayPan[i].repaint();
			}
		}
		
		int exteriorDay = 1;
		for (int i = StartFlagNew; i < cal2.getActualMaximum(Calendar.DAY_OF_MONTH) + StartFlagNew; i++) {
			String dayNum = Integer.toString(exteriorDay);
			exteriorDay++;
			dayLabel[i] = new JLabel(dayNum);
			dayLabel[i].setOpaque(true);
			if ((i % 7) == 6) {
				dayLabel[i].setBackground(new Color(0x77C4D3));
			}
			else if ((i % 7) == 0) {
				dayLabel[i].setBackground(new Color(0xEA2E49));
			}
			else {
				dayLabel[i].setBackground(new Color(0xDAEDE2));
			}
			dayLabel[i].setFont(new Font("¸¼Àº °íµñ", Font.BOLD, 14));
			dayPan[i].add(dayLabel[i], BorderLayout.NORTH);
			
			dayButton[i] = new JButton("click");
			dayButton[i].setFont(new Font("¸¼Àº °íµñ", Font.PLAIN, 10));
			dayButton[i].setBackground(Color.WHITE);
			dayButton[i].setForeground(Color.LIGHT_GRAY);
			dayButton[i].setBorderPainted(false);
			dayButton[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			dayButton[i].addActionListener(this);
			dayPan[i].add(dayButton[i],BorderLayout.CENTER);

			memoLabel[i] = new JLabel();
		}

		setup_memo_icon();
	}
	
	public void setup_memo_icon() {
		int StartFlagNew = get_start_flag_of_month(NowFirstDayofWeek);
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE", "hr", "hr");
			Statement stmt = conn.createStatement();
			
			int exteriorDay = 1;
			for (int i = StartFlagNew; i < cal2.getActualMaximum(Calendar.DAY_OF_MONTH) + StartFlagNew; i++) {
				String dateKeyStr = Integer.toString(NowYear) + Integer.toString(NowMonth) + Integer.toString(exteriorDay - 1);
				
				String sqlSlct = "SELECT * FROM MEMODATA WHERE USERKEY=" + userKey + " AND DATEKEY=" + dateKeyStr;
				ResultSet memoRS = stmt.executeQuery(sqlSlct);
				
				if (memoRS.next()) {
					memoLabel[i].setIcon(note);
					dayPan[i].add(memoLabel[i], BorderLayout.SOUTH);
				}
				else {
					dayPan[i].remove(memoLabel[i]);
				}

				exteriorDay++;
				dayPan[i].updateUI();
			}
			
		} catch (ClassNotFoundException exc) {
			exc.printStackTrace();
		} catch (SQLException exc) {
			exc.printStackTrace();
		}
	}
	
	public void today_memo_load() {
		todayVec.clear();
		
		String dateKeyStr = Integer.toString(tmpYear) + Integer.toString(tmpMonth) + Integer.toString(tmpDay);
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE", "hr", "hr");
			Statement stmt = conn.createStatement();
			
			String sqlSlct = "SELECT * FROM MEMODATA WHERE USERKEY=" + userKey + " AND DATEKEY=" + dateKeyStr;
			ResultSet memoRS = stmt.executeQuery(sqlSlct);
			
			while (memoRS.next()) {
				if (memoRS.getString(3).length() >= 18) {
					todayVec.add(memoRS.getString(3).substring(0, 18) + "...");
				}
				else {
					todayVec.add(memoRS.getString(3));
				}
			}
			
		} catch (ClassNotFoundException exc) {
			exc.printStackTrace();
		} catch (SQLException exc) {
			exc.printStackTrace();
		}
		
		todayList.updateUI();
	}
	
	public void memo_load() {
		memoVec.clear();

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE", "hr", "hr");
			Statement stmt = conn.createStatement();
			
			String sqlSlct = "SELECT * FROM MEMODATA WHERE USERKEY=" + userKey + " AND DATEKEY=" + dateKey;
			ResultSet memoRS = stmt.executeQuery(sqlSlct);
			
			while (memoRS.next()) {
				memoVec.add(memoRS.getString(3));
			}
			
		} catch (ClassNotFoundException exc) {
			exc.printStackTrace();
		} catch (SQLException exc) {
			exc.printStackTrace();
		}

		memoList.updateUI();
	}
	
	public void memo_add() {
		String getMemo = addMemo.getText();
		if (dateKey == 0) {
			JOptionPane.showMessageDialog(null, "³¯Â¥¸¦ ¼±ÅÃÇÏ¼¼¿ä.", "Àú±â¿ä", JOptionPane.OK_OPTION);
			return;
		}
		if (getMemo.length() > 70) {
			JOptionPane.showMessageDialog(null, "70ÀÚ ÀÌ³»·Î ÀÔ·ÂÇÏ¼¼¿ä.", "Àú±â¿ä", JOptionPane.OK_OPTION);
			addMemo.setText("");
			return;
		}
		else if (getMemo.length() == 0) {
			JOptionPane.showMessageDialog(null, "³»¿ëÀ» ÀÔ·ÂÇÏ¼¼¿ä.", "Àú±â¿ä", JOptionPane.OK_OPTION);
			return;
		}
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE", "hr", "hr");
			Statement stmt = conn.createStatement();
			
			String sqlInsrt = "INSERT INTO MEMODATA VALUES (" + userKey + ", " + dateKey + ", '" + getMemo + "')";
			stmt.executeUpdate(sqlInsrt);
			
		} catch (ClassNotFoundException exc) {
			exc.printStackTrace();
		} catch (SQLException exc) {
			exc.printStackTrace();
		}
		
		addMemo.setText("");
		setup_memo_icon();
		today_memo_load();
		memo_load();
	}
	
	public void memo_del() {
		int option = JOptionPane.showConfirmDialog(null, "¸Þ¸ð¸¦ »èÁ¦ÇÒ±î¿ä?", "¸Þ¸ð »èÁ¦", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.NO_OPTION) {
			return;
		}
		String getDelMemo = memoVec.elementAt(selectedIndex);
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE", "hr", "hr");
			Statement stmt = conn.createStatement();
			
			String sqlDel = "DELETE FROM MEMODATA WHERE USERKEY=" + userKey + " AND DATEKEY=" + dateKey + " AND MEMO='" + getDelMemo + "'";
			stmt.executeUpdate(sqlDel);
			
		} catch (ClassNotFoundException exc) {
			exc.printStackTrace();
		} catch (SQLException exc) {
			exc.printStackTrace();
		}
		
		setup_memo_icon();
		today_memo_load();
		memo_load();
	}
	
	public int get_start_flag_of_month(int loc_week) {
		switch (loc_week) {
		case 1:
			return 0;
		case 2:
			return 1;
		case 3:
			return 2;
		case 4:
			return 3;
		case 5:
			return 4;
		case 6:
			return 5;
		case 7:
			return 6;
		}
		return -1;
	}
	
	public void find_button(Object o) {
		String dateKeyStr = Integer.toString(NowYear) + Integer.toString(NowMonth);
		int StartFlag = get_start_flag_of_month(NowFirstDayofWeek);
		int plus;
		
		if (o == dayButton[0]) {
			if (0 >= StartFlag) {
				plus = 0 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[1]) {
			if (1 >= StartFlag) {
				plus = 1 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[2]) {
			if (2 >= StartFlag) {
				plus = 2 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[3]) {
			if (3 >= StartFlag) {
				plus = 3 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[4]) {
			if (4 >= StartFlag) {
				plus = 4 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[5]) {
			if (5 >= StartFlag) {
				plus = 5 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[6]) {
			if (6 >= StartFlag) {
				plus = 6 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[7]) {
			if (7 >= StartFlag) {
				plus = 7 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[8]) {
			if (8 >= StartFlag) {
				plus = 8 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[9]) {
			if (9 >= StartFlag) {
				plus = 9 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[10]) {
			if (10 >= StartFlag) {
				plus = 10 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[11]) {
			if (11 >= StartFlag) {
				plus = 11 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[12]) {
			if (12 >= StartFlag) {
				plus = 12 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[13]) {
			if (13 >= StartFlag) {
				plus = 13 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[14]) {
			if (14 >= StartFlag) {
				plus = 14 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[15]) {
			if (15 >= StartFlag) {
				plus = 15 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[16]) {
			if (16 >= StartFlag) {
				plus = 16 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[17]) {
			if (17 >= StartFlag) {
				plus = 17 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[18]) {
			if (18 >= StartFlag) {
				plus = 18 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[19]) {
			if (19 >= StartFlag) {
				plus = 19 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[20]) {
			if (20 >= StartFlag) {
				plus = 20 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[21]) {
			if (21 >= StartFlag) {
				plus = 21 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[22]) {
			if (22 >= StartFlag) {
				plus = 22 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[23]) {
			if (23 >= StartFlag) {
				plus = 23 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[24]) {
			if (24 >= StartFlag) {
				plus = 24 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[25]) {
			if (25 >= StartFlag) {
				plus = 25 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[26]) {
			if (26 >= StartFlag) {
				plus = 26 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[27]) {
			if (27 >= StartFlag) {
				plus = 27 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[28]) {
			if (28 >= StartFlag) {
				plus = 28 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[29]) {
			if (29 >= StartFlag) {
				plus = 29 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[30]) {
			if (30 >= StartFlag) {
				plus = 30 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[31]) {
			if (31 >= StartFlag) {
				plus = 31 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[32]) {
			if (32 >= StartFlag) {
				plus = 32 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[33]) {
			if (33 >= StartFlag) {
				plus = 33 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[34]) {
			if (34 >= StartFlag) {
				plus = 34 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[35]) {
			if (35 >= StartFlag) {
				plus = 35 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[36]) {
			if (36 >= StartFlag) {
				plus = 36 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
		else if (o == dayButton[37]) {
			if (37 >= StartFlag) {
				plus = 37 - StartFlag;
				dateKeyStr = dateKeyStr + Integer.toString(plus);
				dateKey = Integer.parseInt(dateKeyStr);
			}
		}
	}

}
