package it.unibo.oop.lab.lambda.ex02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;

/**
 *
 */
public final class MusicGroupImpl implements MusicGroup {

    private final Map<String, Integer> albums = new HashMap<>();
    private final Set<Song> songs = new HashSet<>();

    public void addAlbum(final String albumName, final int year) {
        this.albums.put(albumName, year);
    }

    public void addSong(final String songName, final Optional<String> albumName, final double duration) {
        if (albumName.isPresent() && !this.albums.containsKey(albumName.get())) {
            throw new IllegalArgumentException("invalid album name");
        }
        this.songs.add(new MusicGroupImpl.Song(songName, albumName, duration));
    }

    public Stream<String> orderedSongNames() {
        return this.songs.stream()
                         .map(Song::getSongName)
                         .sorted();
    }

    public Stream<String> albumNames() {
        return this.albums.entrySet().stream()
                                     .map(Entry::getKey);
    }

    public Stream<String> albumInYear(final int year) {
        return this.albums.entrySet().stream()
                                     .filter(e -> e.getValue() == year)
                                     .map(Entry::getKey);
    }

    /*
     * Returns a stream of all songs of an album.
     */
    private Stream<Song> getSongsInAlbum(final String albumName) {
        return this.songs.stream()
                         .filter(s -> s.getAlbumName().filter(o -> o.equals(albumName)).isPresent());
    }

    public int countSongs(final String albumName) {
        return this.getSongsInAlbum(albumName)
                   .mapToInt(s -> 1)
                   .sum();
    }

    public int countSongsInNoAlbum() {
        return this.songs.stream()
                         .filter(s -> s.getAlbumName().isEmpty())
                         .mapToInt(s -> 1)
                         .sum();
    }

    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.getSongsInAlbum(albumName)
                   .mapToDouble(Song::getDuration)
                   .average();
    }

    public Optional<String> longestSong() {
        return this.songs.stream()
                         .max((s1, s2) -> Double.compare(s1.getDuration(), s2.getDuration()))
                         .map(s -> s.getSongName());
    }

    /*
     * Computes the length of an album.
     */
    private double albumLength(final String album) {
        return this.songs.stream()
                         .filter(s -> s.getAlbumName().filter(a -> a.equals(album)).isPresent())
                         .mapToDouble(Song::getDuration)
                         .sum();
    }

    public Optional<String> longestAlbum() {
        return this.albums.entrySet()
                          .stream()
                          .map(e -> {
                              final String name = e.getKey();
                              return Map.entry(name, this.albumLength(name));
                          })
                          .max((e1, e2) -> Double.compare(e1.getValue(), e2.getValue()))
                          .map(e -> e.getKey());
    }

    private static final class Song {

        private final String songName;
        private final Optional<String> albumName;
        private final double duration;
        private int hash;

        Song(final String name, final Optional<String> album, final double len) {
            super();
            this.songName = name;
            this.albumName = album;
            this.duration = len;
        }

        public String getSongName() {
            return songName;
        }

        public Optional<String> getAlbumName() {
            return albumName;
        }

        public double getDuration() {
            return duration;
        }

        public int hashCode() {
            if (hash == 0) {
                hash = songName.hashCode() ^ albumName.hashCode() ^ Double.hashCode(duration);
            }
            return hash;
        }

        public boolean equals(final Object obj) {
            if (obj instanceof Song) {
                final Song other = (Song) obj;
                return albumName.equals(other.albumName) && songName.equals(other.songName)
                        && duration == other.duration;
            }
            return false;
        }

        public String toString() {
            return "Song [songName=" + songName + ", albumName=" + albumName + ", duration=" + duration + "]";
        }

    }

}
