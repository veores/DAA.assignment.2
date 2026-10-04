package metrics;

public final class Metrics {

    private long comparisons;
    private long moves;
    private long steps;

    public void compare() {
        comparisons++;
    }

    public void move() {
        moves++;
    }

    public void step() {
        steps++;
    }

    public void addComparisons(long count) {
        comparisons += count;
    }

    public void addMoves(long count) {
        moves += count;
    }

    public void addSteps(long count) {
        steps += count;
    }

    public long comparisons() {
        return comparisons;
    }

    public long moves() {
        return moves;
    }

    public long steps() {
        return steps;
    }

    public void reset() {
        comparisons = 0;
        moves = 0;
        steps = 0;
    }

    @Override
    public String toString() {
        return "Metrics{comparisons=" + comparisons + ", moves=" + moves + ", steps=" + steps + "}";
    }
}
