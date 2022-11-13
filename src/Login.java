import java.awt.*;
import javax.swing.*;

public class Login extends JFrame {
    JTextField ID = new JTextField(30);
    JPasswordField Password = new JPasswordField(30);

    public Login() {
       
        setTitle("네이버 메일");
        JPanel title = new JPanel();
        JLabel naver = new JLabel("NAVER");
        
        //네이버
        naver.setForeground(new Color(52, 199, 41));
        naver.setFont(new Font("Dialog", Font.BOLD, 60));
        title.add(naver);
        
        JPanel jp1 = new JPanel();
        jp1.setLayout(new GridLayout(3, 2));

        JPanel idPanel =
                new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel jlb1 = new JLabel("아이디 : ", JLabel.CENTER);
        jlb1.setFont(new Font("Dialog", Font.PLAIN, 15));
        idPanel.add(jlb1);
        
        //아이디 
        JPanel idPanel2 =
                new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField IDField = new JTextField(10);
        IDField.add(ID);
        idPanel2.add(IDField);

        jp1.add(idPanel);
        jp1.add(idPanel2);

        //비밀번호
        JPanel pwdPanel =
                new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel jlb2 = new JLabel("비밀번호 : ", JLabel.CENTER);
        jlb2.setFont(new Font("Dialog", Font.PLAIN, 15));
        pwdPanel.add(jlb2);
        
        JPanel pwdPanel2 =
                new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPasswordField PasswordField = new JPasswordField(10);
        PasswordField.add(Password);
        pwdPanel2.add(PasswordField);

        jp1.add(pwdPanel);
        jp1.add(pwdPanel2);

        //다음
        JPanel loginPanel =
                new JPanel(new BorderLayout());
        JButton jLogin = new JButton("다음");
        loginPanel.add(jLogin);
        
        JPanel jp3 = new JPanel();
        jp3.add(loginPanel);

        JPanel jp2 = new JPanel();
        jp2.setLayout(new FlowLayout());
        jp2.add(jp1);
        
        //레이아웃
        setLayout(new BorderLayout());
        add(title, BorderLayout.NORTH);
        add(jp2, BorderLayout.CENTER);
        add(jp3, BorderLayout.SOUTH);

        setBounds(300, 300, 300, 250);

        Dimension frameSize = getSize();
        Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((windowSize.width - frameSize.width) / 2,
                (windowSize.height - frameSize.height) / 2);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        //다음 버튼 
        jLogin.addActionListener(e -> {
            String id = IDField.getText();
            String password = new String(PasswordField.getPassword());
            String naverMail = "@naver.com";
            String sender = id+naverMail;
            User user = new User(id, password, sender);

            if(id.length()==0)
                JOptionPane.showMessageDialog(null, "ID를 입력하세요", "오류", JOptionPane.ERROR_MESSAGE);
            else if(password.length()==0)
                JOptionPane.showMessageDialog(null, "비밀번호를 입력하세요", "오류", JOptionPane.ERROR_MESSAGE);
            else{
                new MailSend(user);
                dispose();
            }
        });
    }
}