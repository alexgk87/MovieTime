import controller.EpisodeController;
import controller.TvSerieController;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import io.javalin.vue.VueComponent;
import repository.TvSerieCSVRepository;
import repository.TvSerieDataRepository;
import repository.TvSerieJSONRepository;
import repository.TvSerieRepository;

import java.io.File;

public class Application {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.staticFiles.enableWebjars();
            config.vue.vueAppName = "app";
        }).start(8100);

        app.before("/", ctx -> ctx.redirect("/tvserie"));

        app.get("/tvserie", new VueComponent("tvserie-overview"));
        app.get("/tvserie/{tvserie-id}/sesong/{sesong-nr}", new VueComponent("tvserie-detail"));
        app.get("/tvserie/{tvserie-id}/sesong/{sesong-nr}/episode/{episode-nr}", new VueComponent("episode-detail"));
        app.get("/tvserie/{tvserie-id}/createepisode", new VueComponent("episode-create"));
        app.get("/tvserie/{tvserie-id}/sesong/{sesong-nr}/episode/{episode-nr}/updateepisode", new VueComponent("episode-update"));

        //TvSerieRepository tvSerieRepository = new TvSerieJSONRepository("E:\\Skole\\Høgskolen i Østfold\\ITF10619-1 Programmering 2\\Oblig 5\\MovieTime\\src\\main\\java\\tvshows_10.json");
        TvSerieRepository tvSerieRepository = new TvSerieCSVRepository("E:\\Skole\\Høgskolen i Østfold\\ITF10619-1 Programmering 2\\Oblig 5\\MovieTime\\src\\main\\java\\tvshows_10.csv");
        TvSerieController tvSerieController = new TvSerieController(tvSerieRepository);
        EpisodeController episodeController = new EpisodeController(tvSerieRepository);

        app.get("api/tvserie", new Handler() {
            @Override
            public void handle(Context ctx) throws Exception {
                tvSerieController.getAlleTvSerier(ctx);
            }
        });

        app.get("api/tvserie/{tvserie-id}", tvSerieController::getTVSerie);
        app.get("api/tvserie/{tvserie-id}/sesong/{sesong-nr}", episodeController::getEpisoderISesong);
        app.get("api/tvserie/{tvserie-id}/sesong/{sesong-nr}/episode/{episode-nr}", episodeController::getEpisode);
        app.get("api/tvserie/{tvserie-id}/sesong/{sesong-nr}/episode/{episode-nr}/deleteepisode", episodeController::slettEpisode);
        app.post("api/tvserie/{tvserie-id}/createepisode", episodeController::opprettEpisode);
        app.post("api/tvserie/{tvserie-id}/sesong/{sesong-nr}/episode/{episode-nr}/updateepisode", episodeController::oppdaterEpisode);
    }
}
