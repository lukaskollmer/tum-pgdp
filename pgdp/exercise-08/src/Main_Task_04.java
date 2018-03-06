/*
* exercise-08/task-04
*
* Videosammlung
*
* */


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main_Task_04 {

    static class Videosammlung {
        final int size;
        private Video[] videos;

        private int verbleibende;

        Videosammlung(int size) {
            this.size = size;
            this.videos = new Video[size];
            this.verbleibende = size;
        }

        Video[] getVideos() {
            return videos;
        }

        int addVideo(Video video) {
            for (int i = 0; i < videos.length; i++) {
                if (videos[i] == null) {
                    videos[i] = video;
                    verbleibende--;
                    return i;
                }
            }

            return -1;
        }

        Video verkaufen(int id) {
            for (int i = 0; i < videos.length; i++) {
                Video video = videos[i];
                if (video == null) continue;

                if (video.id == id) {
                    verbleibende++;
                    videos[i] = null;
                    return video;
                }
            }

            return null;
        }

        Video verkaufen(String titel) {
            for (Video video : videos) {
                if (video == null) continue;
                if (video.titel.equals(titel)) {
                    return verkaufen(video.id);
                }
            }

            return null;
        }


        String[] videosInGenre(String genre) {
            List<String> titles = new ArrayList<>();

            for (Video video : videos) {
                if (video == null) continue;
                if (video.hasGenre(genre) != -1) {
                    titles.add(video.titel);
                }
            }

            // https://stackoverflow.com/a/22731751/2513803
            return titles.toArray(new String[titles.size()]);
        }

        public int getVerbleibende() {
            return verbleibende;
        }
    }





    public static void main(String... args) {


        Video v0 = new Video("GoT");
        v0.addGenre("Fantasy");
        v0.addGenre("drama");

        Video v1 = new Video("HP 1");
        v1.addGenre("drama");

        Video v2 = new Video("Django");
        v2.addGenre("western");

        Video v3 = new Video("lotr");
        v3.addGenre("fantasy");


        for (Video v : Arrays.asList(v0, v1, v2, v3)) {
            System.out.format("%s\n", v);
        }
    }

    static class Counter {
        private static int index = -1;

        static int increment() {
            return ++index;
        }
    }


    static class Video {
        private int id;
        private String titel;
        private String[] genres;


        Video(String titel) {
            this.id = Counter.increment();
            this.titel = titel;

            this.genres = new String[0];
        }


        int addGenre(String genre) {
            int index = hasGenre(genre);
            if (index != -1) {
                return index;
            }

            String[] currentGenres = this.genres;
            this.genres = new String[currentGenres.length + 1];

            for (int i = 0; i < currentGenres.length; i++) {
                genres[i] = currentGenres[i];
            }


            int indexOfNewElement = this.genres.length - 1;
            this.genres[indexOfNewElement] = genre;

            return indexOfNewElement;

        }



        int hasGenre(String genre) {
            for (int i = 0; i < genres.length; i++) {
                if (genres[i].equals(genre)) {
                    return i;
                }
            }

            return -1;
        }





        @Override
        public String toString() {
            return String.format("<Video id=%s titel='%s' genres=%s >", id, titel, Arrays.toString(genres));
        }


        // getters

        public int getId() {
            return id;
        }

        public String getTitel() {
            return titel;
        }

        public String[] getGenres() {
            return genres;
        }
    }
}
