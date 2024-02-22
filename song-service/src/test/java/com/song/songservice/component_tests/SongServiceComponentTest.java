package com.song.songservice.component_tests;

import com.song.songservice.dto.SongDTO;
import com.song.songservice.entity.Song;
import com.song.songservice.exception.SongMissingValidationException;
import com.song.songservice.repository.SongRepository;
import com.song.songservice.service.SongService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringJUnitConfig
@DataJpaTest
@AutoConfigureMockMvc
public class SongServiceComponentTest {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private SongService songService;

    @Test
    void testCreateSong_Success() {
        SongDTO songDTO = new SongDTO("Test Song", "Test Artist", "Test Album", "3:30", "resourceId123", "2022");

        Song createdSong = songService.createSong(songDTO);

        assertNotNull(createdSong);
        assertNotNull(createdSong.getId());
        assertEquals(songDTO.getName(), createdSong.getName());
        assertEquals(songDTO.getArtist(), createdSong.getArtist());
        assertEquals(songDTO.getAlbum(), createdSong.getAlbum());
        assertEquals(songDTO.getLength(), createdSong.getLength());
        assertEquals(songDTO.getYear(), createdSong.getYear());
        assertEquals(songDTO.getResourceId(), createdSong.getResourceId());
    }

    @Test
    void testCreateSong_NullInput() {
        assertThrows(SongMissingValidationException.class, () -> songService.createSong(null));
    }

    @Test
    void testCreateSong_BlankResourceId() {
        SongDTO songDTO = new SongDTO("Test Song", "Test Artist", "Test Album", "3:30", "", "2022");

        assertThrows(SongMissingValidationException.class, () -> songService.createSong(songDTO));
    }

    @Test
    void testGetSongById() {
        Song savedSong = songRepository.save(new Song(null, "Test Song", "Test Artist", "Test Album", "3:30", "resourceId123", "2022"));

        Optional<Song> result = songService.getSongById(savedSong.getId());

        assertTrue(result.isPresent());
        assertEquals(savedSong.getId(), result.get().getId());
    }

    @Test
    void testGetSongById_NotFound() {
        Optional<Song> result = songService.getSongById(-1L);

        assertFalse(result.isPresent());
    }
}

