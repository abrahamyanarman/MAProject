package com.song.songservice.service.impl;

import com.song.songservice.dto.SongDTO;
import com.song.songservice.entity.Song;
import com.song.songservice.exception.SongMissingValidationException;
import com.song.songservice.repository.SongRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SongServiceImplTest {

    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private SongServiceImpl songService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateSong_Success() {
        SongDTO songDTO = new SongDTO("Test Song", "Test Artist", "Test Album", "3:30", "resourceId123", "2022");
        Song savedSong = new Song(1L, "Test Song", "Test Artist", "Test Album", "3:30", "resourceId123", "2022");
        when(songRepository.save(any(Song.class))).thenReturn(savedSong);

        Song createdSong = songService.createSong(songDTO);

        assertNotNull(createdSong);
        assertEquals(savedSong.getId(), createdSong.getId());
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
        Long songId = 1L;
        Song mockSong = new Song(songId, "Test Song", "Test Artist", "Test Album", "3:30", "resourceId123", "2022");
        when(songRepository.findById(songId)).thenReturn(Optional.of(mockSong));

        Optional<Song> result = songService.getSongById(songId);

        assertTrue(result.isPresent());
        assertEquals(songId, result.get().getId());
    }

    @Test
    void testGetSongById_NotFound() {
        Long songId = 1L;
        when(songRepository.findById(songId)).thenReturn(Optional.empty());

        Optional<Song> result = songService.getSongById(songId);

        assertFalse(result.isPresent());
    }

    @Test
    void testDeleteSongs() {
        Long[] ids = {1L, 2L, 3L};

        songService.deleteSongs(ids);

        for (Long id : ids) {
            verify(songRepository, times(1)).deleteById(id);
        }
    }
}
