package org.fullstack5.pacmanapi;

import org.fullstack5.pacmanapi.models.response.GameState;
import reactor.core.publisher.Flux;

public interface GameService {
    Flux<GameState> get(String pinCode);
    String register();
}
