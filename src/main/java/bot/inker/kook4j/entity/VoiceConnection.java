package bot.inker.kook4j.entity;

public final class VoiceConnection {

    private String ip;
    private String port;
    private int rtcpPort;
    private boolean rtcpMux;
    private int bitrate;
    private String audioSsrc;
    private String audioPt;

    VoiceConnection() {
    }

    private VoiceConnection(Builder builder) {
        this.ip = builder.ip;
        this.port = builder.port;
        this.rtcpPort = builder.rtcpPort;
        this.rtcpMux = builder.rtcpMux;
        this.bitrate = builder.bitrate;
        this.audioSsrc = builder.audioSsrc;
        this.audioPt = builder.audioPt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String ip() {
        return ip;
    }

    public String port() {
        return port;
    }

    public int rtcpPort() {
        return rtcpPort;
    }

    public boolean rtcpMux() {
        return rtcpMux;
    }

    public int bitrate() {
        return bitrate;
    }

    public String audioSsrc() {
        return audioSsrc;
    }

    public String audioPt() {
        return audioPt;
    }

    public String rtpUrl() {
        if (rtcpMux) {
            return "rtp://" + ip + ":" + port;
        } else {
            return "rtp://" + ip + ":" + port + "?rtcpport=" + rtcpPort;
        }
    }

    public String ffmpegOutput() {
        return "[select=a:f=rtp:ssrc=" + audioSsrc + ":payload_type=" + audioPt + "]" + rtpUrl();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return "VoiceConnection{ip='" + ip + "', port='" + port +
                "', bitrate=" + bitrate + ", rtcpMux=" + rtcpMux + "}";
    }

    public static final class Builder {
        private String ip;
        private String port;
        private int rtcpPort;
        private boolean rtcpMux;
        private int bitrate;
        private String audioSsrc;
        private String audioPt;

        Builder() {
        }

        Builder(VoiceConnection src) {
            this.ip = src.ip;
            this.port = src.port;
            this.rtcpPort = src.rtcpPort;
            this.rtcpMux = src.rtcpMux;
            this.bitrate = src.bitrate;
            this.audioSsrc = src.audioSsrc;
            this.audioPt = src.audioPt;
        }

        public Builder ip(String ip) {
            this.ip = ip;
            return this;
        }

        public Builder port(String port) {
            this.port = port;
            return this;
        }

        public Builder rtcpPort(int rtcpPort) {
            this.rtcpPort = rtcpPort;
            return this;
        }

        public Builder rtcpMux(boolean rtcpMux) {
            this.rtcpMux = rtcpMux;
            return this;
        }

        public Builder bitrate(int bitrate) {
            this.bitrate = bitrate;
            return this;
        }

        public Builder audioSsrc(String audioSsrc) {
            this.audioSsrc = audioSsrc;
            return this;
        }

        public Builder audioPt(String audioPt) {
            this.audioPt = audioPt;
            return this;
        }

        public VoiceConnection build() {
            return new VoiceConnection(this);
        }
    }
}
