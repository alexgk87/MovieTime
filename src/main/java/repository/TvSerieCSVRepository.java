package repository;

import model.Episode;
import model.Person;
import model.TvSerie;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TvSerieCSVRepository implements TvSerieRepository {
    private final HashMap<Integer, TvSerie> tvSerier = new HashMap<>();
    private final ArrayList<TvSerie> arrayTvSerier = new ArrayList<>();

    public TvSerieCSVRepository(String filnavn) {
        lesFraCSVFil("E:\\Skole\\Høgskolen i Østfold\\ITF10619-1 Programmering 2\\Oblig 5\\MovieTime\\src\\main\\java\\tvshows_10.csv");
        skrivTilCSVFil(arrayTvSerier, new File("oppdatertSerieFil.csv"));
    }

    private void lesFraCSVFil(String filnavn) {
        try (BufferedReader br = new BufferedReader(new FileReader(filnavn))) {
            String linje;
            DateTimeFormatter datoFormaterer = DateTimeFormatter.ISO_LOCAL_DATE;
            int counter = 0;

            while ((linje = br.readLine()) != null) {
                String[] midlertidig = linje.split(";");

                TvSerie nyTvSerie = new TvSerie(midlertidig[0], midlertidig[1], LocalDate.from(datoFormaterer.parse(midlertidig[2])), midlertidig[3]);
                Episode enEpisode = new Episode(midlertidig[4], midlertidig[5], Integer.parseInt(midlertidig[6]),
                        Integer.parseInt(midlertidig[7]), Integer.parseInt(midlertidig[8]),
                        LocalDate.from(datoFormaterer.parse(midlertidig[9])), new Person(midlertidig[11], midlertidig[12],
                        LocalDate.from(datoFormaterer.parse(midlertidig[13]))), midlertidig[10]);

                if (tvSerier.size() == 0) {
                    nyTvSerie.leggTilEpisode(enEpisode);
                    tvSerier.put(counter, nyTvSerie);
                }
                else if (tvSerier.get(counter-1).getTittel().equals(nyTvSerie.getTittel())) {
                    tvSerier.get(counter-1).leggTilEpisode(enEpisode);
                    continue;
                }
                else {
                    tvSerier.put(counter, nyTvSerie);
                    tvSerier.get(counter).leggTilEpisode(enEpisode);
                }
                counter++;
            }

            arrayTvSerier.addAll(tvSerier.values());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Oppgave 2.2c & 2.6
    private void skrivTilCSVFil(ArrayList<TvSerie> arrayTvSerier, File filnavn) {
        Thread skriveThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try (BufferedWriter br = new BufferedWriter(new FileWriter(filnavn))) {
                    for (TvSerie serie : arrayTvSerier) {
                        for (Episode episode : serie.getEpisoder()) {
                            br.write(serie.getTittel() + ";" + serie.getBeskrivelse() + ";" + serie.getUtgivelsesdato() + ";" + serie.getBildeUrl() + ";" + episode.getTittel() + ";"
                                    + episode.getBeskrivelse() + ";" + episode.getEpisodeNummer() + ";" + episode.getSesongNummer() + ";" + episode.getSpilletid() + ";"
                                    + episode.getUtgivelsesdato() + ";" + episode.getBildeUrl() + ";" + episode.getRegissor().getFornavn() + ";"
                                    + episode.getRegissor().getEtternavn() + ";" + episode.getRegissor().getFodselsDato());
                            br.newLine();
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        skriveThread.start();
    }

    @Override
    public ArrayList<TvSerie> getTVSerier() {
        return new ArrayList<>(arrayTvSerier);
    }

    @Override
    public TvSerie getTvSerie(String tvSerieTittel) {
        for (TvSerie tvSerie: arrayTvSerier) {
            if (tvSerie.getTittel().equals(tvSerieTittel))
                return tvSerie;
        }

        return null;
    }

    @Override
    public ArrayList<Episode> getEpisoderISesong(String tvSerieTittel, int sesongNr) {
        return getTvSerie(tvSerieTittel).hentEpisoderISesong(sesongNr);
    }

    @Override
    public Episode getEpisode(String tvSerieTittel, int sesongNr, int episodeNr) {
        return getTvSerie(tvSerieTittel).getEpisode(sesongNr, episodeNr);
    }

    // Oppgave 2.4
    @Override
    public void opprettEnEpisode(String tvserienavn, String tittel, String beskrivelse, int episodeNummer, int sesongNummer, int spilletid, LocalDate utgivelsesdato, String bildeUrl) {
        for (TvSerie serie : arrayTvSerier) {
            if (serie.getTittel().equals(tvserienavn))
                serie.leggTilEpisode(new Episode(tittel, beskrivelse, episodeNummer, sesongNummer, spilletid, utgivelsesdato, bildeUrl));
        }
        skrivTilCSVFil(arrayTvSerier, new File("oppdatertSerieFil.csv"));
    }

    // Oppgave 2.5
    @Override
    public void oppdaterEnEpisode(String tvserienavn, int urlSesNr, int urlEpNr, String tittel, String beskrivelse, int episodeNummer, int sesongNummer, int spilletid, LocalDate utgivelsesdato, String bildeUrl) {
        for (TvSerie serie : arrayTvSerier) {
            if (serie.getTittel().equals(tvserienavn))
                for (Episode episode : serie.getEpisoder()) {
                    if (episode.getSesongNummer() == urlSesNr && episode.getEpisodeNummer() == urlEpNr) {
                        episode.setTittel(tittel);
                        episode.setSesongNummer(sesongNummer);
                        episode.setEpisodeNummer(episodeNummer);
                        episode.setBeskrivelse(beskrivelse);
                        episode.setSpilletid(spilletid);
                        episode.setUtgivelsesdato(utgivelsesdato);
                        episode.setBildeUrl(bildeUrl);
                        break;
                    }
                }
        }

        skrivTilCSVFil(arrayTvSerier, new File("oppdatertSerieFil.csv"));
    }

    // Oppgave 2.3
    @Override
    public void slettEnEpisode(String tvserienavn, int episodeNummer, int sesongNummer) {
        for (TvSerie serie : arrayTvSerier) {
            if (serie.getTittel().equals(tvserienavn))
                for (Episode episode : serie.getEpisoder()) {
                    if (episode.getSesongNummer() == sesongNummer && episode.getEpisodeNummer() == episodeNummer) {
                        serie.getEpisoder().remove(episode);
                        break;
                    }
            }
        }
        skrivTilCSVFil(arrayTvSerier, new File("oppdatertSerieFil.csv"));
    }
}
