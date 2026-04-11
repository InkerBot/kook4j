package bot.inker.kook4j.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VoiceConnectionBuilderTest {

    @Test
    void rtpUrl_formatsCorrectly() {
        var conn = VoiceConnection.builder()
                .ip("192.168.1.1")
                .port("12345")
                .audioSsrc("ssrc-abc")
                .audioPt("111")
                .rtcpPort(12346)
                .bitrate(128000)
                .rtcpMux(false)
                .build();

        assertEquals("192.168.1.1", conn.ip());
        assertEquals("12345", conn.port());
        assertNotNull(conn.rtpUrl());
        assertTrue(conn.rtpUrl().contains("192.168.1.1"));
        assertTrue(conn.rtpUrl().contains("12345"));
    }

    @Test
    void ffmpegOutput_notNull() {
        var conn = VoiceConnection.builder()
                .ip("10.0.0.1").port("9000")
                .audioSsrc("ssrc").audioPt("96")
                .rtcpPort(9001).bitrate(64000)
                .rtcpMux(true)
                .build();
        assertNotNull(conn.ffmpegOutput());
    }
}
