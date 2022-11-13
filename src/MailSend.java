import java.awt.*;
import javax.swing.*;

public class MailSend extends JFrame{
    JTextField sender=new JTextField(20);
    JTextField recipient=new JTextField(20);
    JTextField subject=new JTextField(30);
    JTextArea content=new JTextArea(20,30);
    User user;

    public MailSend(User user) {

        super("네이버 메일");
        this.user = user;
        JPanel inputPanel=new JPanel(new BorderLayout(10,10));
        JPanel labelPanel=new JPanel( new GridLayout(5,1,5,10) );
        JPanel textFieldPanel=new JPanel( new GridLayout(5,1,5,10) );
        inputPanel.add( labelPanel, BorderLayout.WEST);
        inputPanel.add( textFieldPanel, BorderLayout.CENTER);

        //BorderLayout.WEST
        JLabel jl1 = new JLabel("보내는 이 :");
        jl1.setFont(new Font("Dialog", Font.PLAIN, 15));
        jl1.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        JLabel jl2 = new JLabel("받는 이 :");
        jl2.setFont(new Font("Dialog", Font.PLAIN, 15));
        jl2.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        JLabel jl3 = new JLabel("내게 쓰기");
        jl3.setFont(new Font("Dialog", Font.PLAIN, 15));
        jl3.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        JLabel jl4 = new JLabel("제목 :");
        jl4.setFont(new Font("Dialog", Font.PLAIN, 15));
        jl4.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        JLabel jl5 = new JLabel("내용 :");
        jl5.setFont(new Font("Dialog", Font.PLAIN, 15));
        jl5.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        labelPanel.add(jl1);
        labelPanel.add(jl2);
        labelPanel.add(jl3);
        labelPanel.add(jl4);
        labelPanel.add(jl5);
        
        //sender는 앞에서 입력한 id이용하여 메일 주소 고정
        sender.setText(this.user.sender);
        sender.setEditable(false);

        //내게 쓰기 (체크 시 받는 메일 고정)
        JCheckBox sendToMe = new JCheckBox();
        sendToMe.addActionListener(e -> {
            if(sendToMe.isSelected()) {
                recipient.setText(sender.getText());
                recipient.setEditable(false);
            }
            else {
                recipient.setText("");
                recipient.setEditable(true);
            }
        });

        textFieldPanel.add(sender);
        textFieldPanel.add(recipient);
        textFieldPanel.add(sendToMe);
        textFieldPanel.add(subject);
        textFieldPanel.setBorder(BorderFactory.createEmptyBorder(10,0, 0, 10));

        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        //이전, 전송, 닫기 버튼
        JButton back = new JButton("이전");
        JButton send = new JButton("전송");
        JButton exit = new JButton("닫기");

        //이전 버튼
        back.addActionListener(e->{
            new Login();
            dispose();
        });
        
        //전송버튼
        send.addActionListener(e -> {
            if (e.getSource()==send) {
                try {
                    SMTPSender.sendMail(
                            this.user,
                            recipient.getText(),
                            subject.getText(),
                            content.getText()
                    );
                    JOptionPane.showMessageDialog(null, "메일을 전송하였습니다.", "전송 성공", JOptionPane.INFORMATION_MESSAGE);
                } catch(Exception ex) {
                    JOptionPane.showMessageDialog(
                            null, "메일전송이 실패하였습니다.\n "+ex.getMessage(),"전송 실패", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //닫기 버튼
        exit.addActionListener(e -> {
            if (e.getSource()==exit) {
                System.exit(0);
            }
        });

        JPanel buttonPanel=new JPanel();
        buttonPanel.add( back);
        buttonPanel.add( send );
        buttonPanel.add( exit );
        
        //레이아웃
        this.setSize(500, 500);
        this.add( inputPanel, BorderLayout.NORTH );
        this.add( content, BorderLayout.CENTER );
        this.add( buttonPanel, BorderLayout.SOUTH );

        Dimension frameSize = getSize();
        Dimension windowSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((windowSize.width - frameSize.width) / 2,
                (windowSize.height - frameSize.height) / 2);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }
}