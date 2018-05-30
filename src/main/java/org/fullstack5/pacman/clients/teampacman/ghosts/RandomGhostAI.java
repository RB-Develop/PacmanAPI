package org.fullstack5.pacman.clients.teampacman.ghosts;

import ch.qos.logback.core.net.server.Client;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.fullstack5.pacman.api.models.Direction;
import org.fullstack5.pacman.api.models.Maze;
import org.fullstack5.pacman.api.models.Position;
import org.fullstack5.pacman.api.models.request.MoveRequest;
import org.fullstack5.pacman.api.models.response.GameState;
import org.fullstack5.pacman.api.models.response.MovingPiece;
import org.fullstack5.pacman.clients.teampacman.AI;
import org.fullstack5.pacman.clients.teampacman.ClientUtils;
import org.fullstack5.pacman.clients.teampacman.ServerComm;

import java.util.List;

@AllArgsConstructor
public final class RandomGhostAI implements AI {

    private static final int GHOST_COUNT = 4;

    private final String gameId;
    private final String authId;
    private final Maze maze;

    @Override
    public final void runAI(final GameState state) {
        Position pacmanPosition = state.getPacman().getCurrentPosition();

        for (int i = 0; i < GHOST_COUNT; i++) {
            final MovingPiece ghost = ClientUtils.getGhost(state, i);
            final List<Direction> directions = ClientUtils.getAvailableDirections(maze, ghost);
            if (directions.size() > 1) {
                directions.remove(ClientUtils.getReverseDirection(ghost.getDirection()));
            }

            Direction direction = determineDirection(directions, ghost.getCurrentPosition(),
                    pacmanPosition, ghost.isVulnerable());

            performMove(i, direction);
        }
    }

    private Direction determineDirection(List<Direction> availableDirections,
                                         Position currentPosition,
                                         Position pacmanPosition,
                                         boolean isVulnerable) {
        boolean considerEast = currentPosition.getX() < pacmanPosition.getX();
        if (considerEast && availableDirections.contains(Direction.EAST)) {
            if (isVulnerable && availableDirections.contains(Direction.WEST)) {
                return Direction.WEST;
            } else if (!isVulnerable) {
                return Direction.EAST;
            }
        } else if (!considerEast && availableDirections.contains(Direction.WEST)) {
            if (isVulnerable && availableDirections.contains(Direction.EAST)) {
                return Direction.EAST;
            } else if (!isVulnerable) {
                return Direction.WEST;
            }
        }

        boolean considerSouth = currentPosition.getY() < pacmanPosition.getY();
        if (considerSouth && availableDirections.contains(Direction.SOUTH)) {
            if (isVulnerable && availableDirections.contains(Direction.NORTH)) {
                return Direction.NORTH;
            } else if (!isVulnerable) {
                return Direction.SOUTH;
            }
        } else if (!considerSouth && availableDirections.contains(Direction.NORTH)) {
            if (isVulnerable && availableDirections.contains(Direction.SOUTH)) {
                return Direction.SOUTH;
            } else if (!isVulnerable) {
                return Direction.NORTH;
            }
        }

        return ClientUtils.randomItem(availableDirections);
    }

    private void performMove(final int ghostNumber, final Direction direction) {
        final MoveRequest request = new MoveRequest(gameId, authId, direction, ClientUtils.getGhostType(ghostNumber));
        ServerComm.performMove(request);
    }
}
