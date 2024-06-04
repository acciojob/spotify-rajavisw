package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class SpotifyRepository {
    public HashMap<Artist, List<Album>> artistAlbumMap;
    public HashMap<Album, List<Song>> albumSongMap;
    public HashMap<Playlist, List<Song>> playlistSongMap;
    public HashMap<Playlist, List<User>> playlistListenerMap;
    public HashMap<User, Playlist> creatorPlaylistMap;
    public HashMap<User, List<Playlist>> userPlaylistMap;
    public HashMap<Song, List<User>> songLikeMap;

    public List<User> users;
    public List<Song> songs;
    public List<Playlist> playlists;
    public List<Album> albums;
    public List<Artist> artists;

    public SpotifyRepository(){
        //To avoid hitting apis multiple times, initialize all the hashmaps here with some dummy data
        artistAlbumMap = new HashMap<>();
        albumSongMap = new HashMap<>();
        playlistSongMap = new HashMap<>();
        playlistListenerMap = new HashMap<>();
        creatorPlaylistMap = new HashMap<>();
        userPlaylistMap = new HashMap<>();
        songLikeMap = new HashMap<>();

        users = new ArrayList<>();
        songs = new ArrayList<>();
        playlists = new ArrayList<>();
        albums = new ArrayList<>();
        artists = new ArrayList<>();
    }

    public User createUser(String name, String mobile) {
        if(name==null || mobile==null) return null;
        User user=new User(name,mobile);
        users.add(user);
        return user;
    }

    public Artist createArtist(String name) {
        if(name==null) return null;
        Artist artist=new Artist(name);
        artists.add(artist);
        return artist;
    }

    public Album createAlbum(String title, String artistName) {
        if(title==null || artistName==null) return null;


        Artist artist1=null;
        for(Artist artist:artists){
            if(artist.getName().equals(artistName)){
                artist1=artist;
                break;
            }
        }
        if(artist1==null){
            artist1=createArtist(artistName);
        }

        Album album=new Album(title);
        albums.add(album);

        List<Album> albumList=artistAlbumMap.get(artist1);
        if(albumList==null){
            albumList=new ArrayList<>();
        }
        albumList.add(album);
        artistAlbumMap.put(artist1,albumList);

        return album;
    }

    public Song createSong(String title, String albumName, int length) throws Exception{
        if(title==null || albumName==null) return null;
        Album album1=null;
        for (Album album:albums){
            if(album.getTitle().equals(albumName)){
                album1=album;
                break;
            }
        }
        if(album1==null) throw new RuntimeException("Album does not exist");

        Song song=new Song(title,length);
        songs.add(song);

        List<Song> songList=albumSongMap.get(album1);
        if(songList==null){
            songList=new ArrayList<>();
        }
        songList.add(song);
        albumSongMap.put(album1,songList);

        return song;
    }

    public Playlist createPlaylistOnLength(String mobile, String title, int length) throws Exception {
        if(mobile==null || title==null) return null;
        Playlist playlist=new Playlist(title);

        List<Song> songList=new ArrayList<>();
        for(Song song:songs){
            if(song.getLength()==length) songList.add(song);
        }
        playlistSongMap.put(playlist,songList);

        User user=null;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)){
                user=user1;
                break;
            }
        }
        if(user==null) throw new RuntimeException("User does not exist");

        List<Playlist> playlists1= userPlaylistMap.get(user);
        if(playlists1==null){
            playlists1=new ArrayList<>();
        }
        playlists1.add(playlist);
        userPlaylistMap.put(user,playlists1);

        creatorPlaylistMap.put(user,playlist);

        List<User> userList=playlistListenerMap.get(playlist);
        if(userList==null){
            userList=new ArrayList<>();
        }
        userList.add(user);
        playlistListenerMap.put(playlist,userList);

        playlists.add(playlist);
        return playlist;
    }

    public Playlist createPlaylistOnName(String mobile, String title, List<String> songTitles) throws Exception {
        if(mobile==null || title==null) return null;
        Playlist playlist=new Playlist(title);

        List<Song> songList=new ArrayList<>();
        for(Song song:songs){
            for(String s:songTitles){
                if(s.equals(song.getTitle())){
                    songList.add(song);
                }
            }
        }
        playlistSongMap.put(playlist,songList);

        User user=null;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)){
                user=user1;
                break;
            }
        }
        if(user==null) throw new RuntimeException("User does not exist");

        List<Playlist> playlists1= userPlaylistMap.get(user);
        if(playlists1==null){
            playlists1=new ArrayList<>();
        }
        playlists1.add(playlist);
        userPlaylistMap.put(user,playlists1);

        creatorPlaylistMap.put(user,playlist);

        List<User> userList=playlistListenerMap.get(playlist);
        if(userList==null){
            userList=new ArrayList<>();
        }
        userList.add(user);
        playlistListenerMap.put(playlist,userList);

        playlists.add(playlist);
        return playlist;
    }

    public Playlist findPlaylist(String mobile, String playlistTitle) throws Exception {
        if(mobile==null || playlistTitle==null) return null;

        User user=null;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)){
                user=user1;
                break;
            }
        }
        if(user==null) throw new RuntimeException("User does not exist");

        for(User u:creatorPlaylistMap.keySet()){
            if(u.getMobile().equals(user.getMobile())) return null;
        }

        Playlist playlist=null;
        for(Playlist p:playlists){
            if(p.getTitle().equals(playlistTitle)){
                playlist=p;
                break;
            }
        }
        if(playlist==null) throw new RuntimeException("Playlist does not exist");

        List<User> userList=playlistListenerMap.get(playlist);
        for(User u:userList){
            if(u.getMobile().equals(user.getMobile())) return null;
        }
        userList.add(user);
        playlistListenerMap.put(playlist,userList);

        return playlist;

    }

    public Song likeSong(String mobile, String songTitle) throws Exception {
        if(mobile==null || songTitle==null) return null;

        User user=null;
        for(User user1:users){
            if(user1.getMobile().equals(mobile)){
                user=user1;
                break;
            }
        }
        if(user==null) throw new RuntimeException("User does not exist");

        Song song=null;
        for(Song s:songs){
            if(s.getTitle().equals(songTitle)){
                song=s;
                break;
            }
        }
        if(song==null) throw new RuntimeException("Song does not exist");

        List<User> userList=songLikeMap.get(song);
        if(userList==null){
            userList=new ArrayList<>();
            userList.add(user);
        }
        else {
            boolean flag=true;
            for(User u:userList){
                if(u.getMobile().equals(user.getMobile())){
                    flag=false;
                    break;
                }
            }
            if(flag){
                userList.add(user);
            }
        }
        song.setLikes(userList.size());

        songLikeMap.put(song,userList);
        return song;
    }

    public String mostPopularArtist() {
        for(Artist artist:artistAlbumMap.keySet()){
            int likes=0;
            for(Album album:artistAlbumMap.get(artist)){
                for(Song song:albumSongMap.get(album)){
                    likes+=song.getLikes();
                }
            }
            artist.setLikes(likes);
        }

        String popularArtist="";
        int maxLikes=Integer.MIN_VALUE;
        for(Artist artist:artists){
            if(artist.getLikes()>maxLikes){
                maxLikes= artist.getLikes();
                popularArtist=artist.getName();
            }
        }
        return popularArtist;

    }

    public String mostPopularSong() {
        String popularSong="";
        int maxLikes=Integer.MIN_VALUE;
        for(Song song:songs){
            if(song.getLikes()>maxLikes){
                maxLikes= song.getLikes();
                popularSong=song.getTitle();
            }
        }
        return popularSong;
    }
}
