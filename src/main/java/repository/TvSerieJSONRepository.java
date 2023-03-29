package repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.Episode;
import model.Person;
import model.TvSerie;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class TvSerieJSONRepository implements TvSerieRepository {

    private final ArrayList<TvSerie> tvSerier = new ArrayList<>();

    public TvSerieJSONRepository(String filnavn) {
        getTvSerierFraJSON(filnavn);

        skrivTvSerierTilJSON(tvSerier, new File("seriertilJSON.json"));
    }

    @NotNull
    private ArrayList<TvSerie> getTvSerierFraJSON(String filnavn) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            TvSerie[] tvSerierArray = objectMapper.readValue(new File(filnavn), TvSerie[].class);

            tvSerier.addAll(Arrays.asList(tvSerierArray));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tvSerier;
    }

    private void skrivTvSerierTilJSON(ArrayList<TvSerie> tvSeriertilJSON, File filsti) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(filsti, tvSeriertilJSON);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<TvSerie> getTVSerier() {
        return new ArrayList<>(tvSerier);
    }

    @Override
    public TvSerie getTvSerie(String tvSerieTittel) {
        for (TvSerie tvSerie: tvSerier) {
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

    @Override
    public void opprettEnEpisode(String tvserienavn, String tittel, String beskrivelse, int episodeNummer, int sesongNummer, int spilletid, LocalDate utgivelsesdato, String bildeUrl) {

    }

    @Override
    public void oppdaterEnEpisode(String tvserienavn, int urlSesNr, int urlEpNr, String tittel, String beskrivelse, int episodeNummer, int sesongNummer, int spilletid, LocalDate utgivelsesdato, String bildeUrl) {

    }

    @Override
    public void slettEnEpisode(String tvserienavn, int episodeNummer, int sesongNummer) {

    }

}
