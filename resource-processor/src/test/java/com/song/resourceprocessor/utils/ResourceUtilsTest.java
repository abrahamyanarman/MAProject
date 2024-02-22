package com.song.resourceprocessor.utils;

import com.song.resourceprocessor.dto.SongDTO;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.Parser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class ResourceUtilsTest {

    @Mock
    private Parser mockParser;

    private ResourceUtils resourceUtils;

    @BeforeEach
    void setUp() {
        resourceUtils = new ResourceUtils(mockParser);
    }

    @Test
    void testParseMetadataAndConvertToSongDTO_Success() throws Exception {
        doAnswer(invocation -> {
            Metadata metadata = invocation.getArgument(2);
            metadata.set("xmpDM:album", "Test Album");
            metadata.set("xmpDM:albumArtist", "Test Artist");
            metadata.set("xmpDM:releaseDate", "2000");
            metadata.set("dc:title", "Test Title");
            metadata.set("xmpDM:duration", "300");
            return null;
        }).when(mockParser).parse(any(InputStream.class), any(), any(), any());

        byte[] testData = "test data".getBytes();
        SongDTO result = resourceUtils.parseMetadataAndConvertToSongDTO(testData);

        assertNotNull(result);
        assertEquals("Test Album", result.getAlbum());
        assertEquals("Test Artist", result.getArtist());
        assertEquals("2000", result.getYear());
        assertEquals("Test Title", result.getName());
        assertEquals("300", result.getLength());
    }

    @Test
    void testParseMetadataAndConvertToSongDTO_IOException() throws Exception {
        doThrow(IOException.class).when(mockParser).parse(any(InputStream.class), any(), any(), any());
        byte[] testData = "test data".getBytes();

        SongDTO result = resourceUtils.parseMetadataAndConvertToSongDTO(testData);

        assertNull(result);
    }
}
