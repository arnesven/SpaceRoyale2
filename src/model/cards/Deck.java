package model.cards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck<T extends GameCard> {
    private List<T> cards;

    public Deck() {
        cards = new ArrayList<>();
    }

    public void addCard(T card) {
        cards.add(card);
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public int size() {
        return cards.size();
    }

    public void addCopies(T card, int count) {
        for (int i = 0; i < count; ++i) {
            addCard((T)card.copy());
        }
    }

    public T drawOne() {
        return cards.removeFirst();
    }


    public boolean isEmpty() {
        return size() == 0;
    }

    public void putOnBottom(T card) {
        cards.addLast(card);
    }

    public T peek() {
        return cards.getFirst();
    }
}
