package com.song.resourceprocessor.utils;

import com.song.resourceprocessor.dto.SongDTO;
import org.apache.commons.lang3.StringUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.Property;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@Component
public class ResourceUtils {
    private static final Logger logger = LoggerFactory.getLogger(ResourceUtils.class);
    private final Parser audioParser;

    public ResourceUtils(Parser audioParser) {
        this.audioParser = audioParser;
    }

    public SongDTO parseMetadataAndConvertToSongDTO (byte [] data) {
        Metadata metadata = new Metadata();
        BodyContentHandler bodyContentHandler = new BodyContentHandler();
        ParseContext parseContext = new ParseContext();
        try(InputStream inputStream = new ByteArrayInputStream(data)) {
            audioParser.parse(inputStream, bodyContentHandler, metadata, parseContext);
            SongDTO songDTO = new SongDTO();
            songDTO.setAlbum(StringUtils.defaultString(metadata.get("xmpDM:album")));
            songDTO.setArtist(StringUtils.defaultString(metadata.get("xmpDM:albumArtist")));
            songDTO.setYear(StringUtils.defaultString(metadata.get("xmpDM:releaseDate")));
            songDTO.setName(StringUtils.defaultString(metadata.get("dc:title")));
            songDTO.setLength(StringUtils.defaultString(metadata.get("xmpDM:duration")));
            return songDTO;
        } catch (IOException ioe) {
            logger.error("{} occurred during the reading the file", IOException.class.getSimpleName(), ioe);
            return null;
        } catch (TikaException te) {
            logger.error("{} occurred during the parsing the file", TikaException.class.getSimpleName(), te);
            return null;
        } catch (SAXException se) {
            logger.error("{} occurred during the processing the file", SAXException.class.getSimpleName(), se);
            return null;
        }
    }
}
