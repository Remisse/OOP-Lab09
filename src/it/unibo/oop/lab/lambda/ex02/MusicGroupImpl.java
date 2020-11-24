package it.unibo.oop.lab.lambda.ex02;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Stream;
import static java.util.stream.Collectors.*;

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

    private Stream<Song> getSongsInAlbum(final String albumName) {
        return this.songs.stream()
                         .filter(s -> s.getAlbumName().filter(o -> o.equals(albumName)).isPresent());
    }

    public int countSongs(final String albumName) {
        return (int) this.getSongsInAlbum(albumName)
                         .count();
    }

    public int countSongsInNoAlbum() {
        return (int) this.songs.stream()
                               .filter(s -> s.getAlbumName().isEmpty())
                               .count();
    }

    public OptionalDouble averageDurationOfSongs(final String albumName) {
        return this.getSongsInAlbum(albumName)
                   .mapToDouble(Song::getDuration)
                   .average();
    }

    public Optional<String> longestSong() {
        return Optional.of(this.songs.stream()
                                     .collect(maxBy((s1, s2) -> Double.compare(s1.getDuration(),
                                                                               s2.getDuration())))
                                     .get()
                                     .getSongName());

    }

    public Optional<String> longestAlbum() {
        //For each album, create a stream containing all the songs in that album
        //Do a reduce to each of these streams
        //Return the max
        //Alternatively, collect every song in a map and group them by album?
        return Optional.empty();
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
