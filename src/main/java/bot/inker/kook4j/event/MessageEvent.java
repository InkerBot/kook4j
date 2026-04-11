package bot.inker.kook4j.event;

import bot.inker.kook4j.MessageType;
import bot.inker.kook4j.entity.Attachment;
import bot.inker.kook4j.entity.Quote;
import bot.inker.kook4j.entity.User;
import bot.inker.kook4j.request.MessageCreateRequest;

import java.util.List;

public final class MessageEvent extends Event {

    private int type;
    private String content;
    private User author;
    private List<String> mention;
    private boolean mentionAll;
    private List<Integer> mentionRoles;
    private boolean mentionHere;
    private Attachment attachments;
    private Quote quote;

    public MessageEvent() {
    }

    public int type() {
        return type;
    }

    public MessageType messageType() {
        return MessageType.of(type);
    }

    public String content() {
        return content;
    }

    public User author() {
        return author;
    }

    public List<String> mention() {
        return mention;
    }

    public boolean mentionAll() {
        return mentionAll;
    }

    public List<Integer> mentionRoles() {
        return mentionRoles;
    }

    public boolean mentionHere() {
        return mentionHere;
    }

    public Attachment attachments() {
        return attachments;
    }

    public Quote quote() {
        return quote;
    }

    public void respond(String content) {
        if (isPerson()) {
            bot().dm(authorId()).send(content);
        } else {
            bot().channel(targetId()).send(content);
        }
    }

    public void respond(MessageCreateRequest request) {
        if (isPerson()) {
            bot().dm(authorId()).send(request);
        } else {
            bot().channel(targetId()).send(request);
        }
    }

    public void reply(String content) {
        MessageCreateRequest request = MessageCreateRequest.builder()
                .content(content)
                .quote(msgId())
                .build();
        if (isPerson()) {
            bot().dm(authorId()).send(request);
        } else {
            bot().channel(targetId()).send(request);
        }
    }

    public void reply(MessageCreateRequest request) {
        MessageCreateRequest withQuote = request.toBuilder()
                .quote(msgId())
                .build();
        if (isPerson()) {
            bot().dm(authorId()).send(withQuote);
        } else {
            bot().channel(targetId()).send(withQuote);
        }
    }

    public void populate(int type, String content, User author, List<String> mention,
                         boolean mentionAll, List<Integer> mentionRoles, boolean mentionHere,
                         Attachment attachments, Quote quote) {
        this.type = type;
        this.content = content;
        this.author = author;
        this.mention = mention;
        this.mentionAll = mentionAll;
        this.mentionRoles = mentionRoles;
        this.mentionHere = mentionHere;
        this.attachments = attachments;
        this.quote = quote;
    }

    @Override
    public String toString() {
        return "MessageEvent{type=" + type + ", author=" + (author != null ? author.username() : authorId()) +
                ", content='" + (content != null && content.length() > 50 ? content.substring(0, 50) + "..." : content) + "'}";
    }
}
