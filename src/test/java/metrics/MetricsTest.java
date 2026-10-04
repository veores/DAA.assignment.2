package metrics;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MetricsTest {

    @Test
    void startsAtZero() {
        Metrics metrics = new Metrics();
        assertEquals(0, metrics.comparisons());
        assertEquals(0, metrics.moves());
        assertEquals(0, metrics.steps());
    }

    @Test
    void countersAccumulateIndependently() {
        Metrics metrics = new Metrics();
        metrics.compare();
        metrics.move();
        metrics.move();
        metrics.step();
        metrics.addComparisons(4);
        metrics.addMoves(5);
        metrics.addSteps(6);
        assertEquals(5, metrics.comparisons());
        assertEquals(7, metrics.moves());
        assertEquals(7, metrics.steps());
    }

    @Test
    void resetClearsEverything() {
        Metrics metrics = new Metrics();
        metrics.addComparisons(3);
        metrics.addMoves(3);
        metrics.addSteps(3);
        metrics.reset();
        assertEquals(0, metrics.comparisons());
        assertEquals(0, metrics.moves());
        assertEquals(0, metrics.steps());
    }
}
