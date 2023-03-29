package repository;

import model.Episode;
import model.Person;
import model.TvSerie;

import java.time.LocalDate;
import java.util.ArrayList;

public interface TvSerieRepository {
    ArrayList<TvSerie> getTVSerier();

    TvSerie getTvSerie(String tvSerieId);

    ArrayList<Episode> getEpisoderISesong(String tvSerieTittel, int sesongNr);

    Episode getEpisode(String tvSerieTittel, int sesongNr, int episodeNr);

    // Oppgave 2.3a
    void opprettEnEpisode(String tvserienavn, String tittel, String beskrivelse, int episodeNummer, int sesongNummer, int spilletid, LocalDate utgivelsesdato, String bildeUrl);
    void oppdaterEnEpisode(String tvserienavn, int urlSesNr, int urlEpNr, String tittel, String beskrivelse, int episodeNummer, int sesongNummer, int spilletid, LocalDate utgivelsesdato, String bildeUrl);
    void slettEnEpisode(String tvserienavn, int episodeNummer, int sesongNummer);
}
