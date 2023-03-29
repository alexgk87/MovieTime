package controller;

import io.javalin.http.Context;
import model.Episode;
import model.Produksjon;
import repository.TvSerieRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EpisodeController {
    private final TvSerieRepository tvSerieRepository;

    public EpisodeController(TvSerieRepository tvSerieRepository) {
        this.tvSerieRepository = tvSerieRepository;
    }

    public void getEpisoderISesong(Context context) {
        String tvSerieTittel = context.pathParam("tvserie-id");
        String sesong = context.pathParam("sesong-nr");
        String sortering = context.queryParam("sortering");

        int sesongNr = sesong.isEmpty()? 1 : Integer.parseInt(sesong);

        ArrayList<Episode> episoder = tvSerieRepository.getEpisoderISesong(tvSerieTittel, sesongNr);

        if (sortering != null) {
            switch (sortering) {
                case "episodenr" -> Collections.sort(episoder);
                case "tittel" -> episoder.sort((e1, e2) -> e1.getTittel().compareTo(e2.getTittel()));
                case "spilletid" -> episoder.sort(Comparator.comparingInt(Produksjon::getSpilletid));
            }
        }

        context.json(episoder);
    }

    public void getEpisode(Context context) {
        String tvSerieTittel = context.pathParam("tvserie-id");
        String sesongNr = context.pathParam("sesong-nr");
        String episodeNr = context.pathParam("episode-nr");

        context.json(tvSerieRepository.getEpisode(tvSerieTittel, Integer.parseInt(sesongNr), Integer.parseInt(episodeNr)));

    }

    public void slettEpisode(Context context) {
        String tvSerieNavn = context.pathParam("tvserie-id");
        String sesongNr = context.pathParam("sesong-nr");
        String episodeNr = context.pathParam("episode-nr");

        // La til en redirect tilbake til sesongoversikten når man bruker api/..../deleteepisode
        context.redirect(String.format("http://localhost:8100/tvserie/%s/sesong/%s", tvSerieNavn, sesongNr));

        tvSerieRepository.slettEnEpisode(tvSerieNavn, Integer.parseInt(episodeNr), Integer.parseInt(sesongNr));
    }

    public void opprettEpisode(Context context) {
        // Fikk ikke helt til koden til 2.5c her (sammenfalle lik kode).
        String tvSerieNavn = context.pathParam("tvserie-id");

        String tittel = context.formParam("tittel");
        String sesongNr = context.formParam("sesongNummer");
        String episodeNr = context.formParam("episodeNummer");
        String beskrivelse = context.formParam("beskrivelse");
        String spilletid = context.formParam("spilletid");
        String utgivelsesdato = context.formParam("utgivelsesdato");
        String bildeUrl = context.formParam("bildeUrl");

        context.redirect(String.format("/tvserie/%s/sesong/%s", tvSerieNavn, sesongNr));

        tvSerieRepository.opprettEnEpisode(
                tvSerieNavn, tittel, beskrivelse,
                Integer.parseInt(episodeNr), Integer.parseInt(sesongNr),
                Integer.parseInt(spilletid), LocalDate.parse(utgivelsesdato, DateTimeFormatter.ISO_LOCAL_DATE), bildeUrl
        );
    }

    public void oppdaterEpisode(Context context) {
        // Her har jeg brukt koden som 2.5c ønsker.
        Object[] urlArray = parseURL(context);

        context.redirect(String.format("/tvserie/%s/sesong/%s/episode/%s", urlArray[0], urlArray[4], urlArray[5]));

        tvSerieRepository.oppdaterEnEpisode(
                (String) urlArray[0], (int) urlArray[1], (int) urlArray[2],
                (String) urlArray[3], (String) urlArray[6], (int) urlArray[5],
                (int) urlArray[4], (int) urlArray[7], (LocalDate) urlArray[8], (String) urlArray[9]
        );
    }

    // Her prøvde jeg meg på en sammeslåing av kode (ref 2.5c). Men fikk det ikke helt til.
    // Tanken var og returnere en Array med objekter, der jeg typecastet det til int / String / LocalDate
    // der jeg hadde behov. Det fungerte fint i oppdaterEpisode, men det skar seg litt med oppretting av episode.
    public Object[] parseURL(Context context) {
        String urlTvSerieNavn = context.pathParam("tvserie-id");
        String urlSesNr = context.pathParam("sesong-nr");
        String urlEpNr = context.pathParam("episode-nr");

        String tittel = context.formParam("tittel");
        String sesongNr = context.formParam("sesongNummer");
        String episodeNr = context.formParam("episodeNummer");
        String beskrivelse = context.formParam("beskrivelse");
        String spilletid = context.formParam("spilletid");
        String utgivelsesdato = context.formParam("utgivelsesdato");
        String bildeUrl = context.formParam("bildeUrl");

        return new Object[]{
                urlTvSerieNavn,
                Integer.parseInt(urlSesNr),
                Integer.parseInt(urlEpNr),
                tittel,
                Integer.parseInt(sesongNr),
                Integer.parseInt(episodeNr),
                beskrivelse,
                Integer.parseInt(spilletid),
                LocalDate.parse(utgivelsesdato, DateTimeFormatter.ISO_LOCAL_DATE),
                bildeUrl
        };
    }
}
