import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.net.ssl.*;
import java.io.*;

// User credential class
class User {
    String username;    // ID
    String password;    // PASSWORD
    String sender;      // sender

    User(String username, String password, String sender) {
        this.username = username;
        this.password = password;
        this.sender = sender;
    }

    void setUsername(String username) {
        this.username = username;
    }
    void setPassword(String password) {
        this.password = password;
    }
    void setSender(String sender) {
        this.sender = sender;
    }
}

class SocketMaker {
    class Header {
        String From;
        String To;
        String Subject;
    }
    public Header header = new Header();
    public String body;
    public BufferedReader inFromServer;
    public PrintWriter printWriter;
    public SSLSocket socket;
    public String line;
    public SocketMaker(String smtpServer, int smtpServerPort, User user, String recipient, String subject, String content) throws IOException {

        // 1. SMTP 서버에 소켓 연결
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        this.socket = (SSLSocket) sslSocketFactory.createSocket(smtpServer, smtpServerPort);

        // Header 설정
        this.header.From = user.sender;
        this.header.To = recipient;
        this.header.Subject = subject;

        // Body 설정
        this.body = content;

    }
    public void makeReaderAndWriter() throws Exception {
        // 2. Reader와 Writer를 얻는다.(명령을 보내고 응답코드를 받기 위해)
        inFromServer = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        printWriter = new PrintWriter(this.socket.getOutputStream(), true);

        line = inFromServer.readLine();
        System.out.println(line);
        if (!line.startsWith("220"))
            throw new Exception("SMTP서버가 아닙니다");
    }

    // 3. EHLO
    public void Ehlo(String smtpServer) throws Exception {
        System.out.println();
        System.out.println("EHLO "+smtpServer);
        printWriter.println("EHLO "+smtpServer);

        // 6개 응답값
        line = inFromServer.readLine();
        System.out.println(line);
        line = inFromServer.readLine();
        System.out.println(line);
        line = inFromServer.readLine();
        System.out.println(line);
        line = inFromServer.readLine();
        System.out.println(line);
        line = inFromServer.readLine();
        System.out.println(line);
        line = inFromServer.readLine();
        System.out.println(line);
        if (!line.startsWith("250"))
            throw new Exception("서버 연결에 실패했습니다");
    }

    // 4 AUTH LOGIN
    public void authLogin(String username, String password) throws Exception {
        String encryptedUsername = Base64.getEncoder().encodeToString(username.getBytes(StandardCharsets.UTF_8));
        String encryptedPassword = Base64.getEncoder().encodeToString(password.getBytes(StandardCharsets.UTF_8));

        System.out.println();
        System.out.println("AUTH LOGIN");
        printWriter.println("AUTH LOGIN ");
        line = inFromServer.readLine();
        System.out.println(line);

        System.out.println("username");
        printWriter.println(encryptedUsername);
        line = inFromServer.readLine();
        System.out.println(line);

        System.out.println("password");
        printWriter.println(encryptedPassword);
        line = inFromServer.readLine();
        System.out.println(line);

        if (!line.startsWith("235"))
            throw new Exception("로그인에 실패했습니다");
    }

    // 5. MAIL FROM
    public void MailFrom() throws Exception {
        System.out.println();
        System.out.println("MAIL FROM:<"+this.header.From+">");
        printWriter.println("MAIL FROM:<"+this.header.From+">");
        line = inFromServer.readLine();
        System.out.println(line);
        if (!line.startsWith("250"))
            throw new Exception("보내는 이메일 입력값을 확인해주세요");
    }

    // 6. RCTP TO
    public void RctpTo() throws Exception {
        System.out.println();
        System.out.println("RCPT TO:<" + this.header.To + ">");
        printWriter.println("RCPT TO:<" + this.header.To + ">");
        line = inFromServer.readLine();
        System.out.println(line);
        if (!line.startsWith("250"))
            throw new Exception("받는 이메일 입력값을 확인해주세요");
    }

    // 순서 : DATA명령전송 -> 응답코드(354) 코드 확인 
    // 7. DATA
    public void data() throws Exception {
        System.out.println();
        System.out.println("DATA");
        printWriter.println("DATA");

        line = inFromServer.readLine();
        System.out.println(line);
        if (!line.startsWith("354"))
            throw new Exception("DATA 명령이 실패했습니다");
    }

    // 본문전송 -> . 전송 -> 응답코드(250) 확인
    // 8. SEND CONTENT
    public void sendMailContent() throws Exception {
    	System.out.println();
    	System.out.println("From: "+this.header.From);
    	printWriter.println("From: "+this.header.From);

    	System.out.println("To: "+this.header.To);
    	printWriter.println("To: "+this.header.To);
    	
    	System.out.println("Subject: "+this.header.Subject);
    	printWriter.println("Subject: "+this.header.Subject);
    	
    	System.out.println();
        printWriter.println();

        System.out.println(this.body);
        printWriter.println(this.body);
        
        System.out.println(".");
        printWriter.println(".");

        line = inFromServer.readLine();
        System.out.println(line);
        if (!line.startsWith("250"))
            throw new Exception("내용전송이 실패했습니다");
    }

    // 9. QUIT
    public void quit() throws Exception {
    	System.out.println();
        System.out.println("QUIT");
        printWriter.println("QUIT");
        line = inFromServer.readLine();
        System.out.println(line);
        if (!line.startsWith("221"))
            throw new Exception("서버와 연결이 종료되지 않았습니다.");
    }

    // 10. CLOSE
    public void close() throws IOException {
        printWriter.close();
        inFromServer.close();
        socket.close();
    }
}


public class SMTPSender {

    public static void sendMail(User user, String recipient, String subject, String content) throws Exception {
        final String smtpServer = "smtp.naver.com";
        final int smtpServerPort = 465;

        try {

            // 1. 소켓 생성
            SocketMaker socket = new SocketMaker(smtpServer, smtpServerPort, user, recipient, subject, content);

            // 2. BufferReader PrintWriter 생성
            socket.makeReaderAndWriter();

            // 3. EHLO
            socket.Ehlo(smtpServer);

            // 4. AUTH LOGIN
            socket.authLogin(user.username, user.password);

            // 5. MAIL FROM
            socket.MailFrom();

            // 6. RCPT TO
            socket.RctpTo();

            // 7. DATA
            socket.data();

            // 8. content
            socket.sendMailContent();

            // 9. QUIT
            socket.quit();

            // 10. socket close
            socket.close();
        } catch (Exception exception) {
            throw exception;
        }
    }

    public static void main(String[] args) throws Exception {
        try {
            new Login();
        } catch (Exception e) {
            System.out.println("==========================");
            System.out.println("메일이 발송되지 않았습니다.");
            System.out.println(e.toString());
        }
    }
}
