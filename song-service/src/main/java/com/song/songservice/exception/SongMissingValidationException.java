package com.song.songservice.exception;

public class SongMissingValidationException extends RuntimeException{
    public SongMissingValidationException() {
        super("Song metadata missing validation error");
    }
}
