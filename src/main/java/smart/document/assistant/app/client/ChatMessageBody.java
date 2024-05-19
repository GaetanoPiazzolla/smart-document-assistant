package smart.document.assistant.app.client;

public class ChatMessageBody {

        private String message;
        private String chatId;

        public ChatMessageBody() {
        }

        public ChatMessageBody(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getChatId() {
            return chatId;
        }

        public void setChatId(String chatId) {
            this.chatId = chatId;
        }
}
