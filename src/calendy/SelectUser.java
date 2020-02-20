package calendy;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

@SuppressWarnings("serial")
public class SelectUser extends JFrame implements ActionListener, ListSelectionListener, MouseListener {
	private JPanel btnPan;
	private Vector<String> userVec;
	private Vector<Long> keyVec;
	private JList<String> userList;
	private JButton Ent, Add, Del, Bye;
	private int selectedIndex;
	private String selectedName;
	private long selectedKey;
	private Calendar dateCal;
	private String year, month, day;
	
	private URL fIconurl = getClass().getClassLoader().getResource("icon_cal.png");
	
	public SelectUser() {
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Calendy");
		setSize(325, 500);
		
		Toolkit toolkt = Toolkit.getDefaultToolkit();
		Image iconimg = toolkt.getImage(fIconurl);
		setIconImage(iconimg);
		
		Container contentPane = getContentPane();
		
		dateCal = Calendar.getInstance();
		year = Integer.toString(dateCal.get(Calendar.YEAR));
		month = Integer.toString(dateCal.get(Calendar.MONTH) + 1);
		day = Integer.toString(dateCal.get(Calendar.DATE));
		
		userVec = new Vector<>();
		keyVec = new Vector<>();
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE", "hr", "hr");
			Statement stmt = conn.createStatement();
			
			String sqlSlct = "SELECT * FROM USERDATA";
			ResultSet userRS = stmt.executeQuery(sqlSlct);
			
			while (userRS.next()) {
				keyVec.add(userRS.getLong(1));
				userVec.add(userRS.getString(2));
			}
			
		} catch (ClassNotFoundException exc) {
			exc.printStackTrace();
		} catch (SQLException exc) {
			exc.printStackTrace();
		}

		userList = new JList<>(userVec);
		userList.addListSelectionListener(this);
		userList.addMouseListener(this);
		userList.setFont(new Font("맑은 고딕", Font.BOLD, 14));
		JScrollPane scrl = new JScrollPane(userList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		btnPan = new JPanel();
		btnPan.setLayout(new GridLayout(4, 1, 0, 3));
		btnPan.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		
		Ent = new JButton("선택");
		Ent.addActionListener(this);
		Ent.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		Ent.setBackground(new Color(0xDAEDE2));
		Add = new JButton("사용자 추가");
		Add.addActionListener(this);
		Add.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		Add.setBackground(new Color(0xDAEDE2));
		Del = new JButton("사용자 삭제");
		Del.addActionListener(this);
		Del.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		Del.setBackground(new Color(0xDAEDE2));
		Bye = new JButton("종료");
		Bye.addActionListener(this);
		Bye.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		Bye.setBackground(new Color(0xDAEDE2));
		
		btnPan.add(Ent);
		btnPan.add(Add);
		btnPan.add(Del);
		btnPan.add(Bye);
		
		contentPane.add(scrl, BorderLayout.CENTER);
		contentPane.add(btnPan, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new SelectUser();
	}
	
	public String getSelectedName() {
		return selectedName;
	}
	
	public long getSelectedKey() {
		return selectedKey;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == Ent) {
			if (!userVec.isEmpty()) {
				if (selectedName != null) {
					new DisplayMain(this);
					this.setVisible(false);
				}
			}
		}
		else if (obj == Add) {
			String H = Integer.toString(dateCal.get(Calendar.HOUR));
			String M = Integer.toString(dateCal.get(Calendar.MINUTE));
			String S = Integer.toString(dateCal.get(Calendar.SECOND));
			String UserKeyStr = year + month + day + H + M + S;
			long addUserKey = Long.parseLong(UserKeyStr);
			keyVec.add(addUserKey);
			
			String userName = JOptionPane.showInputDialog(null, "사용자 이름을 입력하세요. (20자 이내)", "사용자 추가", JOptionPane.OK_CANCEL_OPTION);
			if (userName.equals("")) {
				userName = userName.replace("", "chicken");
			}
			String addName = userName + "[" + year + "-" + month + "-" + day + "]";
			userVec.add(addName);

			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE", "hr", "hr");
				Statement stmt = conn.createStatement();
				
				String sqlInsrt = "INSERT INTO USERDATA VALUES (" + addUserKey + ", '" + addName + "')";
				stmt.executeUpdate(sqlInsrt);
				
			} catch (ClassNotFoundException exc) {
				exc.printStackTrace();
			} catch (SQLException exc) {
				exc.printStackTrace();
			}
			
			dateCal = Calendar.getInstance();
			userList.updateUI();
		}
		else if (obj == Del) {
			int option = JOptionPane.showConfirmDialog(null, "정말 삭제할까요?", "사용자 삭제", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				if (!userVec.isEmpty()) {
					userVec.remove(selectedIndex);
					keyVec.remove(selectedKey);
					
					try {
						Class.forName("oracle.jdbc.driver.OracleDriver");
						Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:XE", "hr", "hr");
						Statement stmt = conn.createStatement();
						
						String sqlDel1 = "DELETE FROM USERDATA WHERE USERKEY=" + selectedKey;
						stmt.executeUpdate(sqlDel1);
						String sqlDel2 = "DELETE FROM MEMODATA WHERE USERKEY=" + selectedKey;
						stmt.executeUpdate(sqlDel2);
						
					} catch (ClassNotFoundException exc) {
						exc.printStackTrace();
					} catch (SQLException exc) {
						exc.printStackTrace();
					}
					
					userList.updateUI();
				}
			}
		}
		else if (obj == Bye) {
			System.exit(0);
		}
	}
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (!e.getValueIsAdjusting()) {
			selectedIndex = userList.getSelectedIndex();
			String extractName = userVec.elementAt(selectedIndex);
			StringTokenizer st = new StringTokenizer(extractName, "[");
			selectedName = st.nextToken();
			selectedKey = keyVec.elementAt(selectedIndex);
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if (!userVec.isEmpty()) {
			if (e.getClickCount() == 2) {
				new DisplayMain(this);
				this.setVisible(false);
			}
		}
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	@Override
	public void mouseExited(MouseEvent e) {
	}
	@Override
	public void mousePressed(MouseEvent e) {
	}
	@Override
	public void mouseReleased(MouseEvent e) {
	}

}
