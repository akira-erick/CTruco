/*
 *  Copyright (C) 2021 Lucas B. R. de Oliveira - IFSP/SCL
 *  Contact: lucas <dot> oliveira <at> ifsp <dot> edu <dot> br
 *
 *  This file is part of CTruco (Truco game for didactic purpose).
 *
 *  CTruco is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  CTruco is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with CTruco.  If not, see <https://www.gnu.org/licenses/>
 */

package com.bueno.domain.entities.truco;

import com.bueno.domain.entities.game.HandScore;
import com.bueno.domain.entities.player.util.Player;

import java.util.Objects;
import java.util.Optional;

public class TrucoResult {
    private final HandScore score;
    private final Player winner;
    private final Player lastRequester;

    public TrucoResult(HandScore score) {
        this(score, null, null);
    }

    public TrucoResult(HandScore score, Player winner) {
        this(score, winner, null);
    }

    public TrucoResult(HandScore score, Player winner, Player lastRequester) {
        this.score = Objects.requireNonNull(score);
        this.winner = winner;
        this.lastRequester = lastRequester;
    }

    public HandScore getScore() {
        return score;
    }

    public boolean hasWinner() {
        return winner != null;
    }

    public Optional<Player> getWinner() {
        return Optional.ofNullable(winner);
    }

    public Optional<Player> getLastRequester() {
        return Optional.ofNullable(lastRequester);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrucoResult that = (TrucoResult) o;
        return Objects.equals(score, that.score) && Objects.equals(winner, that.winner) && Objects.equals(lastRequester, that.lastRequester);
    }

    @Override
    public int hashCode() {
        return Objects.hash(score, winner, lastRequester);
    }

    @Override
    public String toString() {
        return "Points=" + score.get() + ", winner=" + (winner != null? winner : "No winner");
    }
}