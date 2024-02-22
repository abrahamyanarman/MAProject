package com.song.songservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.song.songservice.dto.SongDTO;
import com.song.songservice.entity.Song;
import com.song.songservice.exception.SongMissingValidationException;
import com.song.songservice.service.SongService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(SongController.class)
@AutoConfigureMockMvc
class SongControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SongService songService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testCreateSong() throws Exception {
        SongDTO songDTO = new SongDTO("Test Song", "Test Artist", "Test Album", "3:30", "resourceId123", "2022");

        when(songService.createSong(any(SongDTO.class))).thenReturn(new Song(1L, songDTO.getName(), songDTO.getArtist(), songDTO.getAlbum(), songDTO.getLength(), songDTO.getResourceId(), songDTO.getYear()));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(songDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        System.out.println(result.getResponse().getContentAsString());
    }

    @Test
    void testGetSongById() throws Exception {
        Long songId = 1L;
        Song song = new Song(songId, "Test Song", "Test Artist", "Test Album", "3:30", "resourceId123", "2022");

        when(songService.getSongById(songId)).thenReturn(Optional.of(song));

        mockMvc.perform(MockMvcRequestBuilders.get("/songs/{id}", songId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(songId));
    }

    @Test
    void testDeleteSongs() throws Exception {
        Long[] songIds = {1L, 2L, 3L};

        mockMvc.perform(MockMvcRequestBuilders.delete("/songs")
                        .param("id", Arrays.stream(songIds).map(String::valueOf).collect(Collectors.joining(","))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testCreateSong_ExceptionHandling() throws Exception {
        SongDTO songDTO = new SongDTO("Test Song", "Test Artist", "Test Album", "3:30", "resourceId123", "2022");

        when(songService.createSong(any(SongDTO.class))).thenThrow(new SongMissingValidationException());

        mockMvc.perform(MockMvcRequestBuilders.post("/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(songDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateSong_InternalServerError() throws Exception {
        SongDTO songDTO = new SongDTO("Test Song", "Test Artist", "Test Album", "3:30", "resourceId123", "2022");

        when(songService.createSong(any(SongDTO.class))).thenThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.post("/songs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(songDTO)))
                .andExpect(status().isInternalServerError());
    }
}
