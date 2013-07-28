package org.hxzon.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message.RecipientType;
import javax.mail.Part;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EMailUtil {
    private static final Logger logger = LoggerFactory.getLogger(EMailUtil.class);
    private static ExecutorService excutor = Executors.newFixedThreadPool(5);

    private static void check(EMail email) {
        if (email == null) {
            throw new RuntimeException("email is null");
        }
        if (email.getHost() == null || email.getHost().isEmpty()) {
            throw new RuntimeException("host is null");
        }
        if (email.getUsername() == null || email.getUsername().isEmpty()) {
            throw new RuntimeException("username is null");
        }
        if (email.getPassword() == null || email.getPassword().isEmpty()) {
            throw new RuntimeException("password is null");
        }
        if (email.getFrom() == null || email.getFrom().isEmpty()) {
            throw new RuntimeException("from is null");
        }
        if (email.getTos().isEmpty()) {
            throw new RuntimeException("to is null");
        }
    }

    public static Future<Boolean> send(final EMail email, final boolean debug) {
        return excutor.submit(new Callable<Boolean>() {
            public Boolean call() {
                return doSend(email, debug);
            }
        });
    }

    public static Future<Boolean> send(EMail email) {
        return send(email, false);
    }

    public static boolean sendAndWait(EMail email) {
        return doSend(email, false);
    }

    public static boolean sendAndWait(EMail email, boolean debug) {
        return doSend(email, debug);
    }

    private static boolean doSend(EMail email, boolean debug) {
        try {
            check(email);
            Properties props = new Properties();
            props.setProperty("mail.smtp.auth", "true");
            props.setProperty("mail.transport.protocol", "smtp");
            props.setProperty("mail.smtp.host", email.getHost());
            Authenticator auth = new MyAuthenticator(email.getUsername(), email.getPassword());
            Session session = Session.getInstance(props, auth);
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email.getFrom()));
            for (String to : email.getTos()) {
                message.addRecipient(RecipientType.TO, new InternetAddress(to));
            }
            message.setSubject(email.getSubject());
            session.setDebug(debug);
            MimeMultipart multiPart = new MimeMultipart("related");
            for (EMailContent content : email.getEMailContent()) {
                if (content instanceof EMailHtmlContent) {
                    BodyPart htmlBodyPart = new MimeBodyPart();
                    htmlBodyPart.setContent(content.getText(), "text/html;charset=utf8");
                    multiPart.addBodyPart(htmlBodyPart);
                } else if (content instanceof EMailImage) {
                    BodyPart inlineBodyPart = new MimeBodyPart();
                    inlineBodyPart.setDisposition(Part.INLINE);
                    inlineBodyPart.setHeader("Content-ID", content.getFileName());
                    inlineBodyPart.setDataHandler(new DataHandler(getDataSource(content.getFilePath())));
                    multiPart.addBodyPart(inlineBodyPart);
                } else if (content instanceof EMailAttachment) {
                    //Base64 encoding for Chinese file name
                    sun.misc.BASE64Encoder enc = new sun.misc.BASE64Encoder();
                    String fileName = "=?GBK?B?" + enc.encode(content.getFileName().getBytes()) + "?=";
                    BodyPart attachmentBodyPart = new MimeBodyPart();
                    attachmentBodyPart.setDisposition(Part.ATTACHMENT);
                    attachmentBodyPart.setDataHandler(new DataHandler(getDataSource(content.getFilePath())));
                    attachmentBodyPart.setFileName(fileName);
                    multiPart.addBodyPart(attachmentBodyPart);
                }
            }
            message.setContent(multiPart);
            Transport.send(message);
        } catch (Exception e) {
            logger.error("send email fail", e);
            return false;
        }

        return true;
    }

    private static class MyAuthenticator extends Authenticator {
        private PasswordAuthentication pa;

        public MyAuthenticator(String username, String password) {
            this.pa = new PasswordAuthentication(username, password);
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return pa;
        }
    }

    public static DataSource getDataSource(String filePath) {
        try {
            URL url = new URL(filePath);
            return new URLDataSource(url);
        } catch (MalformedURLException e) {
            return new FileDataSource(new File(filePath));
        }
    }

    public interface EMailContent {
        public String getText();

        public String getFileName();

        public String getFilePath();
    }

    public static class EMailHtmlContent implements EMailContent {
        private final StringBuffer html;

        public EMailHtmlContent() {
            html = new StringBuffer();
        }

        public EMailHtmlContent(String content) {
            this.html = new StringBuffer(content);
        }

        public String getText() {
            return html.toString();
        }

        public void addText(String content) {
            html.append(content);
        }

        public String getFileName() {
            return null;
        }

        public String getFilePath() {
            return null;
        }
    }

    public static class AbstractEMailAttachment implements EMailContent {
        private String fileName;
        private String filePath;

        public AbstractEMailAttachment() {

        }

        public AbstractEMailAttachment(String fileName, String filePath) {
            this.fileName = fileName;
            this.filePath = filePath;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getText() {
            return null;
        }

    }

    public static class EMailAttachment extends AbstractEMailAttachment {
        public EMailAttachment() {

        }

        public EMailAttachment(String fileName, String filePath) {
            super(fileName, filePath);
        }
    }

    public static class EMailImage extends AbstractEMailAttachment {
        public EMailImage() {

        }

        public EMailImage(String fileName, String filePath) {
            super(fileName, filePath);
        }
    }

    public static class EMail {
        private String host; // smtp服务器
        private String username; // 用户名
        private String password; // 密码

        private String from; // 发件人地址
        private List<String> tos = new ArrayList<String>();// 收件人地址
        private String tmpTos;
        private String subject = ""; // 邮件标题
        private String tmpTextContent;
        private String encoding;

        private EMailHtmlContent htmlContent;
        private List<EMailContent> contents = new ArrayList<EMailContent>();

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        //tos
        public List<String> getTos() {
            return tos;
        }

        public void addTos(Collection<String> tos) {
            for (String to : tos) {
                addTo(to);
            }
        }

        public void addTos(String[] tos) {
            for (String to : tos) {
                addTo(to);
            }
        }

        public String getTmpTos() {
            return tmpTos;
        }

        public void setTmpTos(String tos) {
            this.tmpTos = tos;
            addTosSplit(tos);
        }

        public void addTosSplit(String tos) {
            for (String to : tos.split("[;,]")) {
                addTo(to);
            }
        }

        //FIXME
        public void addTo(String to) {
            if (to != null) {
                to = to.trim();
                if (!to.isEmpty()) {
                    this.tos.add(to);
                }
            }
        }

        //subject
        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        //charset
//		public List<String> getCharsets() {
//			return this.charsets;
//		}
//
//		public void addCharset(String charset) {
//			this.charsets.add(charset);
//		}

        public String getEncoding() {
            return encoding;
        }

        public void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        //text content
        public String getTmpTextContent() {
            return tmpTextContent;
        }

        public void setTmpTextContent(String tmpTextContent) {
            this.tmpTextContent = tmpTextContent;
        }

        public List<EMailContent> getEMailContent() {
            return this.contents;
        }

        public void addText(CharSequence text) {
            if (text != null && text.length() != 0) {
                String html = text.toString().replace("\n", "<br />");
                addHtml(html);
            }
        }

        public void addHtml(CharSequence html) {
            if (html != null && html.length() != 0) {
                if (htmlContent == null) {
                    htmlContent = new EMailHtmlContent("<div>" + html + "</div>");
                    this.contents.add(htmlContent);
                } else {
                    htmlContent.addText("<div>" + html + "</div>");
                }
            }
        }

        //attachment
        public void addAttachment(AbstractEMailAttachment attachment) {
            this.contents.add(attachment);
        }

        public void addAttachment(String fileName, String filePath) {
            addAttachment(new EMailAttachment(fileName, filePath));
        }

    }

    public static void main(String args[]) {
        EMail email = new EMail();
        email.setHost("");
        email.setFrom("");
        email.setUsername("");
        email.setPassword("");
        email.addTo("");
        email.addText("test");
        EMailUtil.sendAndWait(email);
    }

}
