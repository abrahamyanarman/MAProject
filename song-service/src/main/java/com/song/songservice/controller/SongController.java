package com.song.songservice.controller;

import com.song.songservice.dto.SongDTO;
import com.song.songservice.entity.Song;
import com.song.songservice.exception.SongMissingValidationException;
import com.song.songservice.service.SongService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/songs")
public class SongController {

    private final SongService songService;

    public SongController(SongService songService) {
        this.songService = songService;
    }

    @PostMapping
    public ResponseEntity<Map<String, Long>> createSong(@RequestBody SongDTO song) {
        try {
            Song createdSong = songService.createSong(song);
            Map<String, Long> response = Collections.singletonMap("id", createdSong.getId());
            return ResponseEntity.ok(response);
        } catch (SongMissingValidationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> getSong(@PathVariable Long id) {
        Optional<Song> songOptional = songService.getSongById(id);
        return songOptional.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<Long[]> deleteSongs(@RequestParam("id") Long[] ids) {
        songService.deleteSongs(ids);
        return ResponseEntity.ok(ids);
    }
}
