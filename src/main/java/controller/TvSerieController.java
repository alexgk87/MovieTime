package controller;

import io.javalin.http.Context;
import repository.TvSerieRepository;

public class TvSerieController {
    private TvSerieRepository tvSerieRepository;

    public TvSerieController(TvSerieRepository tvSerieRepository) {
        this.tvSerieRepository = tvSerieRepository;
    }

    public void getAlleTvSerier(Context ctx) {
        ctx.json(tvSerieRepository.getTVSerier());
    }

    public void getTVSerie(Context context) {
        String tvSerieId = context.pathParam("tvserie-id");

        context.json(tvSerieRepository.getTvSerie(tvSerieId));
    }
}
