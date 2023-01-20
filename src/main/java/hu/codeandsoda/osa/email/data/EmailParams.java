package hu.codeandsoda.osa.email.data;


public class EmailParams {

    private String to;

    private String subject;

    private String htmlMessage;

    private String textMessage;

    private Long userId;

    public EmailParams() {
    }

    public EmailParams(EmailParamsBuilder emailParamsBuilder) {
        to = emailParamsBuilder.to;
        subject = emailParamsBuilder.subject;
        htmlMessage = emailParamsBuilder.htmlMessage;
        textMessage = emailParamsBuilder.textMessage;
        userId = emailParamsBuilder.userId;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getHtmlMessage() {
        return htmlMessage;
    }

    public void setHtmlMessage(String htmlMessage) {
        this.htmlMessage = htmlMessage;
    }

    public String getTextMessage() {
        return textMessage;
    }

    public void setTextMessage(String textMessage) {
        this.textMessage = textMessage;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public static class EmailParamsBuilder {

        private String to;

        private String subject;

        private String htmlMessage;

        private String textMessage;

        private Long userId;

        public EmailParamsBuilder setTo(String to) {
            this.to = to;
            return this;
        }

        public EmailParamsBuilder setSubject(String subject) {
            this.subject = subject;
            return this;
        }

        public EmailParamsBuilder setHtmlMessage(String htmlMessage) {
            this.htmlMessage = htmlMessage;
            return this;
        }

        public EmailParamsBuilder setTextMessage(String textMessage) {
            this.textMessage = textMessage;
            return this;
        }

        public EmailParamsBuilder setUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public EmailParams build() {
            return new EmailParams(this);
        }
    }

}
