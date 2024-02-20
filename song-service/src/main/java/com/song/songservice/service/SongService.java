package com.song.songservice.service;

import com.song.songservice.dto.SongDTO;
import com.song.songservice.entity.Song;

import java.util.Optional;

public interface SongService {
    Song createSong(SongDTO song);

    Optional<Song> getSongById(Long id);

    void deleteSongs(Long[] ids);
}
