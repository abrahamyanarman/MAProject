package com.song.songservice.service.impl;

import com.song.songservice.dto.SongDTO;
import com.song.songservice.entity.Song;
import com.song.songservice.exception.SongMissingValidationException;
import com.song.songservice.repository.SongRepository;
import com.song.songservice.service.SongService;
import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;

    public SongServiceImpl(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public Song createSong(SongDTO s) {
        if (Objects.isNull(s) || StringUtils.isBlank(s.getResourceId())) {
            throw new SongMissingValidationException();
        }
        Song song = new Song();
        song.setAlbum(s.getAlbum());
        song.setArtist(s.getArtist());
        song.setName(s.getName());
        song.setLength(s.getLength());
        song.setYear(s.getYear());
        song.setResourceId(s.getResourceId());
        return songRepository.save(song);
    }

    @Override
    public Optional<Song> getSongById(Long id) {
        return songRepository.findById(id);
    }

    @Override
    public void deleteSongs(Long[] ids) {
        for (Long id : ids) {
            songRepository.deleteById(id);
        }
    }
}
