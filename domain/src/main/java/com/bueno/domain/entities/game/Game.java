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

package com.bueno.domain.entities.game;

import com.bueno.domain.entities.deck.Card;
import com.bueno.domain.entities.deck.Deck;
import com.bueno.domain.entities.player.util.Player;

import java.util.*;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Game {

    private final UUID uuid;
    private final Player player1;
    private final Player player2;

    private final List<Hand> hands;

    private Player firstToPlay;
    private Player lastToPlay;

    private final static Logger LOGGER = Logger.getLogger(Game.class.getName());

    public Game(Player player1, Player player2) {
        this(player1, player2, UUID.randomUUID());
    }

    public Game(Player player1, Player player2, UUID uuid) {
        this.player1 = Objects.requireNonNull(player1);
        this.player2 = Objects.requireNonNull(player2);
        this.uuid = uuid;
        this.hands = new ArrayList<>();
        prepareNewHand();
    }

    public Hand prepareNewHand(){
        LOGGER.info("Preparing to play new hand...");
        defineHandPlayingOrder();

        final Deck deck = new Deck();
        deck.shuffle();

        final Card vira = deck.takeOne();
        LOGGER.info("Vira: " + vira);
        firstToPlay.setCards(deck.take(3));
        lastToPlay.setCards(deck.take(3));

        Hand hand = new Hand(firstToPlay, lastToPlay, vira);
        hands.add(hand);
        return hand;
    }

    private void defineHandPlayingOrder() {
        firstToPlay = player1.equals(firstToPlay) ? player2 : player1;
        lastToPlay = firstToPlay.equals(player1) ? player2 : player1;
        LOGGER.info("First to play: " + firstToPlay + " | Last to play: " + lastToPlay );
    }

    public void updateScores() {
        final HandResult result = currentHand().getResult().orElseThrow();
        Optional<Player> winner = result.getWinner();

        if (winner.isEmpty()) return;
        if (winner.get().equals(player1)) player1.addScore(result.getScore());
        else player2.addScore(result.getScore());
        LOGGER.info(player1.getUuid() + " score = " + player1.getScore() + " | " + player2.getUuid() + " score : " + player2.getScore() );
    }

    public Optional<Player> getWinner() {
        if (player1.getScore() == Player.MAX_SCORE) return Optional.of(player1);
        if (player2.getScore() == Player.MAX_SCORE) return Optional.of(player2);
        return Optional.empty();
    }

    public Intel getIntel(){
        return isDone() ? Intel.ofConcluded(this) : currentHand().getIntel();
    }

    public List<Intel> getIntelSince(Intel lastIntel){
        final List<Intel> handHistory = currentHand().getHistory();
        final Comparator<Intel> byTimestamp = Comparator.comparing(Intel::timestamp);
        final Predicate<Intel> isAfter = intel -> byTimestamp.compare(intel, lastIntel) > 0;
        final List<Intel> currentHandResult = handHistory.stream().filter(isAfter).collect(Collectors.toList());

        if(currentHandResult.size() < handHistory.size())
            return currentHandResult;

        final Hand previousHand = hands.get(hands.size() - 2);
        final List<Intel> lastHandResult = previousHand.getHistory().stream().filter(isAfter).collect(Collectors.toList());
        final List<Intel> result = new ArrayList<>();
        result.addAll(lastHandResult);
        result.addAll(currentHandResult);
        return result;
    }

    public UUID getUuid() {
        return uuid;
    }

    public Player getFirstToPlay() {
        return firstToPlay;
    }

    public Player getLastToPlay() {
        return lastToPlay;
    }

    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public List<Hand> getHands() {return new ArrayList<>(hands);}

    public int handsPlayed(){
        return hands.size();
    }

    public Hand currentHand(){
        if(hands.isEmpty()) return null;
        int lastHandIndex = hands.size() - 1;
        return hands.get(lastHandIndex);
    }

    public boolean isMaoDeOnze() {
        return player1.getScore() == 11 ^ player2.getScore() == 11;
    }

    public boolean isDone() {
        return getWinner().isPresent();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return uuid.equals(game.uuid) && player1.equals(game.player1) && player2.equals(game.player2);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, player1, player2);
    }
}