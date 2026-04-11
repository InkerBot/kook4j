package bot.inker.kook4j.request;

public final class VoiceJoinRequest {

    private final String audioSsrc;
    private final String audioPt;
    private final Boolean rtcpMux;
    private final String password;

    private VoiceJoinRequest(Builder builder) {
        this.audioSsrc = builder.audioSsrc;
        this.audioPt = builder.audioPt;
        this.rtcpMux = builder.rtcpMux;
        this.password = builder.password;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String audioSsrc() {
        return audioSsrc;
    }

    public String audioPt() {
        return audioPt;
    }

    public Boolean rtcpMux() {
        return rtcpMux;
    }

    public String password() {
        return password;
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private String audioSsrc;
        private String audioPt;
        private Boolean rtcpMux;
        private String password;

        Builder() {
        }

        Builder(VoiceJoinRequest src) {
            this.audioSsrc = src.audioSsrc;
            this.audioPt = src.audioPt;
            this.rtcpMux = src.rtcpMux;
            this.password = src.password;
        }

        public Builder audioSsrc(String audioSsrc) {
            this.audioSsrc = audioSsrc;
            return this;
        }

        public Builder audioPt(String audioPt) {
            this.audioPt = audioPt;
            return this;
        }

        public Builder rtcpMux(boolean rtcpMux) {
            this.rtcpMux = rtcpMux;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public VoiceJoinRequest build() {
            return new VoiceJoinRequest(this);
        }
    }
}
